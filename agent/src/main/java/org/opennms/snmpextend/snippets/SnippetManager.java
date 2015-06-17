package org.opennms.snmpextend.snippets;

import org.opennms.snmpextend.args.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class SnippetManager {

    private final static Logger LOG = LoggerFactory.getLogger(SnippetManager.class);

    public final static ScriptEngineManager SCRIPT_ENGINE_MANAGER = new ScriptEngineManager();

    private final Config config;

    @Inject
    public SnippetManager(final Config config) {
        this.config = config;

        SCRIPT_ENGINE_MANAGER.getEngineFactories().forEach(factory -> {
            LOG.trace("Available factory: {} ({})", factory.getEngineName(), factory.getExtensions());
        });
    }

    public Set<Snippet> findSnippets() {
        try {
            return Files.list(this.config.getSnippetPath())
                        .map(Snippet::new)
                        .collect(Collectors.toSet());

        } catch (final Exception e) {
            LOG.error("Failed to find snippets: {}", this.config.getSnippetPath(), e);

            return Collections.emptySet();
        }
    }
}
