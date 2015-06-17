package org.opennms.snmpextend.snippets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Path SNIPPET_PATH;

    static {
        Properties properties = new Properties();

        InputStream inputStream = SnippetManager.class.getClassLoader().getResourceAsStream("snmpextend.properties");
        try {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        SNIPPET_PATH = Paths.get(properties.getProperty("path", "/etc/snmp/opennms"));
    }


    public SnippetManager() {
        System.out.println(SNIPPET_PATH);
        SCRIPT_ENGINE_MANAGER.getEngineFactories().forEach(factory -> {
            LOG.trace("Available factory: {} ({})", factory.getEngineName(), factory.getExtensions());
        });
    }

    public Set<Snippet> findSnippets() {
        try {
            return Files.list(SNIPPET_PATH)
                    .map(Snippet::new)
                    .collect(Collectors.toSet());

        } catch (final Exception e) {
            LOG.error("Failed to find snippets: {}", SNIPPET_PATH, e);

            return Collections.emptySet();
        }
    }
}
