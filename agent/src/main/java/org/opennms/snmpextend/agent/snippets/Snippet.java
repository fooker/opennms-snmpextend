package org.opennms.snmpextend.agent.snippets;

import com.google.common.base.Throwables;
import org.opennms.snmpextend.agent.args.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

public class Snippet {

    private final static Logger LOG = LoggerFactory.getLogger(Snippet.class);

    private final Config config;

    private final Path path;

    private final String prefix;

    private final ScriptEngine scriptEngine;

    private Instant cachedTime;
    private Result cachedData;

    /**
     * The cache for the script compiled for faster execution (can be {@code null} if the engine does not support
     * compiling).
     */
    private CompiledScript script;

    public Snippet(final Config config, final Path path) {
        this.config = config;
        this.path = path;

        // TODO: This is very fragile: fails on unknown extension, missing extension and other strange names

        final String filename = path.getFileName().toString();

        // Get the filename (without extension) as prefix
        this.prefix = filename.substring(0, filename.lastIndexOf('.'));

        // Get the file extension
        final String suffix = filename.substring(filename.lastIndexOf('.') + 1);

        // Create the script engine used to execute the snippet
        this.scriptEngine = SnippetManager.SCRIPT_ENGINE_MANAGER.getEngineByExtension(suffix);

        LOG.trace("Use script engine for {} : {} > {}", filename, suffix, this.scriptEngine);

        // Flush to start with an empty but well defined cache
        this.flush();
    }

    public Path getPath() {
        return this.path;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public Result load() {
        final Instant now = Instant.now();

        if (now.isAfter(cachedTime.plus(this.config.getCacheDuration()))) {
            LOG.trace("Cache timed out - executing script...");

            final Result.Builder result = Result.builder(this.prefix);

            // TODO: Use a context to redirect I/O
            final Bindings bindings = this.scriptEngine.createBindings();
            bindings.put("prefix", this.prefix);
            bindings.put("results", result);

            if (this.script != null) {
                // Execute the compiled script
                try {
                    this.script.eval(bindings);
                } catch (final ScriptException e) {
                    throw Throwables.propagate(e);
                }

            } else {
                // Execute the script directly from source
                try (final BufferedReader r = Files.newBufferedReader(this.path)) {
                    this.scriptEngine.eval(r, bindings);

                } catch (final IOException | ScriptException e) {
                    throw Throwables.propagate(e);
                }
            }

            this.cachedTime = now;
            this.cachedData = result.build();
        }

        return this.cachedData;
    }

    public void flush() {
        // Reset the cache data and time
        this.cachedTime = Instant.MIN;
        this.cachedData = null;

        // Try to compile the script if the engine supports it
        if (this.scriptEngine instanceof Compilable) {
            final Compilable compilable = (Compilable) this.scriptEngine;

            try (final BufferedReader r = Files.newBufferedReader(this.path)) {
                // Compile the script and cache it
                this.script = compilable.compile(r);

                LOG.trace("Compiled script successfully: {}", this.path);

            } catch (final IOException | ScriptException e) {
                LOG.error("Failed to compile the script: {}", this.path, e);

                // Don't propagate the exception to act like the engine does not support compiling
                this.script = null;
            }
        }
    }
}
