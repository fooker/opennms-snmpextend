package org.opennms.snmpextend.snippets;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import org.opennms.snmpextend.values.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.NavigableMap;

public class Snippet {

    private final static Logger LOG = LoggerFactory.getLogger(Snippet.class);

    private final Path path;

    private final String prefix;

    private final ScriptEngine scriptEngine;

    public Snippet(final Path path) {
        this.path = path;

        final String filename = path.getFileName().toString();

        // Get the filename (without extension) as prefix
        this.prefix = filename.substring(0, filename.lastIndexOf('.'));

        // Get the file extension
        final String suffix = filename.substring(filename.lastIndexOf('.') + 1);

        // Create the script engine used to execute the snippet
        this.scriptEngine = SnippetManager.SCRIPT_ENGINE_MANAGER.getEngineByExtension(suffix);

        LOG.trace("Use script engine for {} : {} > {}", filename, suffix, this.scriptEngine);
    }

    public Path getPath() {
        return this.path;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public Result load() {
        final Result.Builder result = Result.builder(this.prefix);

        // TODO: Use a context to redirect I/O
        final Bindings bindings = this.scriptEngine.createBindings();
        bindings.put("prefix", this.prefix);
        bindings.put("results", result);

        try (final BufferedReader r = Files.newBufferedReader(this.path)) {
            this.scriptEngine.eval(r, bindings);

        } catch (final IOException | ScriptException e) {
            throw Throwables.propagate(e);
        }

        return result.build();
    }
}
