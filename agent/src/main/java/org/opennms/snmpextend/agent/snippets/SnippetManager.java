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
import java.util.stream.Stream;

/**
 * Manages the snippets.
 * The manager runs in background and synchronizes the files in the snippet base folder with snippet instances using a
 * filesystem watcher. Every time a file is added to the snippet foler, a {@link Snippet} instance is created for that
 * file, when the file is modified, the cache is flushed and after a file is deleted, the instance is removed.
 */
@Singleton
public class SnippetManager extends Thread {

    private final static Logger LOG = LoggerFactory.getLogger(SnippetManager.class);

    /**
     * The manager for all scripting engines.
     */
    public final static ScriptEngineManager SCRIPT_ENGINE_MANAGER = new ScriptEngineManager();

    /**
     * The global {@link Config}.
     */
    private final Config config;

    /**
     * The mapping between paths and {@link Snippet} instances for all known paths.
     */
    private final ConcurrentMap<Path, Snippet> snippets;

    static {
        // Log all known scripting engines for debugging
        SCRIPT_ENGINE_MANAGER.getEngineFactories().forEach(factory -> {
            LOG.trace("Available factory: {} ({})", factory.getEngineName(), factory.getExtensions());
        });
    }

    @Inject
    public SnippetManager(final Config config) {
        this.config = config;

        // Initial populate the list of known snippets
        try {
            this.snippets = Files.list(this.config.getSnippetPath())
                                 .filter(SnippetManager::filterSnippets)
                                 .flatMap(path -> {
                                     try {
                                         return Stream.of(new Snippet(this.config, path));

                                     } catch (SnippetException e) {
                                         LOG.error("Failed to load script: {}", path, e);
                                         return Stream.empty();
                                     }
                                 })
                                 .collect(Collectors.toConcurrentMap(Snippet::getPath,
                                                                     Function.identity()));

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
        // Watch for filesystem changes
        try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
            this.config.getSnippetPath().register(watchService,
                                                  StandardWatchEventKinds.ENTRY_CREATE,
                                                  StandardWatchEventKinds.ENTRY_DELETE,
                                                  StandardWatchEventKinds.ENTRY_MODIFY,
                                                  StandardWatchEventKinds.OVERFLOW);

            LOG.trace("Directory watcher registered");

            // Loop forever (there is no exit condition as this is a daemon thread)
            while (true) {
                try {
                    final WatchKey watchKey = watchService.take();

                    for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
                        final Path path = this.config.getSnippetPath().resolve((Path) watchEvent.context());

                        LOG.trace("Got directory watcher event: {} > {} ({})",
                                  path,
                                  watchEvent.kind(),
                                  watchEvent.count());

                        // Don't care about ignored paths
                        if (!filterSnippets(path)) {
                            continue;
                        }

                        if (StandardWatchEventKinds.OVERFLOW == watchEvent.kind()) {
                            LOG.warn("Directory watching events lost...");

                            // TODO: Reload the while snippet list (do it atomic)

                        } else if (StandardWatchEventKinds.ENTRY_CREATE == watchEvent.kind()) {
                            LOG.trace("Snippet added: {}", path);

                            // Got a new file - create a new snippet
                            try {
                                this.snippets.put(path, new Snippet(config, path));

                            } catch (final SnippetException e) {
                                LOG.error("Failed to load script: {}", path, e);
                            }

                        } else if (StandardWatchEventKinds.ENTRY_MODIFY == watchEvent.kind()) {
                            LOG.trace("Snippet modified: {}", path);

                            // File was modified - flush the cache
                            this.snippets.get(path).flush();

                        } else if (StandardWatchEventKinds.ENTRY_DELETE == watchEvent.kind()) {
                            LOG.trace("Snippet deleted: {}", path);

                            // File was removed - delete the snippet
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

    /**
     * Returns all known snippets.
     *
     * @return all known snippets
     */
    public Collection<Snippet> getSnippets() {
        return this.snippets.values();
    }

    /**
     * Predicate filtering out non-script paths.
     *
     * @param path the path to test
     * @return {@code true} iff the path is a snippet candidate
     */
    private static boolean filterSnippets(final Path path) {
        try {
            // Skip directories
            if (Files.isDirectory(path)) {
                return false;
            }

            // Skip hidden files
            if (Files.isHidden(path)) {
                return false;
            }

            // Skip files without extension
            if (!path.getFileSystem().getPathMatcher("glob:*.*").matches(path.getFileName())) {
                return false;
            }

        } catch (final IOException e) {
            LOG.error("Failed to open file: {}", path, e);

            // Failed to read the file somehow - so don't try any further
            return false;
        }

        // Give it a try
        return true;
    }
}
