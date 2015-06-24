package org.opennms.snmpextend.agent.snippets;

import com.google.common.base.Throwables;
import org.opennms.snmpextend.agent.args.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Singleton
public class SnippetManager extends Thread {

    private final static Logger LOG = LoggerFactory.getLogger(SnippetManager.class);

    public final static ScriptEngineManager SCRIPT_ENGINE_MANAGER = new ScriptEngineManager();

    private final Config config;

    private final ConcurrentMap<Path, Snippet> snippets;

    static {
        SCRIPT_ENGINE_MANAGER.getEngineFactories().forEach(factory -> {
            LOG.trace("Available factory: {} ({})", factory.getEngineName(), factory.getExtensions());
        });
    }

    @Inject
    public SnippetManager(final Config config) {
        this.config = config;

        try {
            this.snippets = Files.list(this.config.getSnippetPath())
                                 .collect(Collectors.toConcurrentMap(Function.identity(),
                                                                     p -> new Snippet(config, p)));
        } catch (final IOException e) {
            LOG.error("Failed to open directory: {}", this.config.getSnippetPath());
            throw Throwables.propagate(e);
        }

        // Daemonize and start the watcher
        this.setDaemon(true);
        this.setName("watcher");
        this.start();
    }

    @Override
    public void run() {
        try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
            this.config.getSnippetPath().register(watchService,
                                                  StandardWatchEventKinds.ENTRY_CREATE,
                                                  StandardWatchEventKinds.ENTRY_DELETE,
                                                  StandardWatchEventKinds.ENTRY_MODIFY,
                                                  StandardWatchEventKinds.OVERFLOW);

            LOG.trace("Directory watcher registered");

            while (true) {
                try {
                    final WatchKey watchKey = watchService.take();

                    for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
                        final Path path = this.config.getSnippetPath().resolve((Path) watchEvent.context());

                        LOG.trace("Got directory watcher event: {} > {} ({})", path, watchEvent.kind(), watchEvent.count());

                        if (StandardWatchEventKinds.OVERFLOW == watchEvent.kind()) {
                            LOG.warn("Directory watching events lost...");

                        } else if (StandardWatchEventKinds.ENTRY_CREATE == watchEvent.kind()) {
                            LOG.trace("Snippet added: {}", path);

                            this.snippets.put(path, new Snippet(config, path));

                        } else if (StandardWatchEventKinds.ENTRY_MODIFY == watchEvent.kind()) {
                            LOG.trace("Snippet modified: {}", path);

                            this.snippets.get(path).flush();

                        } else if (StandardWatchEventKinds.ENTRY_DELETE == watchEvent.kind()) {
                            LOG.trace("Snippet deleted: {}", path);

                            this.snippets.remove(path);
                        }
                    }

                    watchKey.reset();

                } catch (final InterruptedException e) {
                    LOG.warn("Failed to wait for file changes", e);
                }
            }


        } catch (final IOException e) {
            LOG.error("Failed to create filesystem watcher");
            throw Throwables.propagate(e);
        }
    }

    public Collection<Snippet> getSnippets() {
        return this.snippets.values();
    }
}
