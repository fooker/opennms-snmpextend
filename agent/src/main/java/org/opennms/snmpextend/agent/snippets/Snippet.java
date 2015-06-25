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

/**
 * A snippet.
 * A snippet is a script filling a result container ({@link Result}). To reduce load, a call to {@link #exec()} may
 * return cached values.
 * <p>
 * The script file name will be used to derive a prefix used for all result records returned by the script and the
 * extension will be used to determine the script engine to use.
 * <p>
 * If the scripting engine used to handle the snippets script supports compiling, the script will be loaded only once
 * and will be compiled for faster execution.
 */
public class Snippet {

    private final static Logger LOG = LoggerFactory.getLogger(Snippet.class);

    /**
     * The global {@link Config} instance.
     */
    private final Config config;

    /**
     * The path of the script.
     */
    private final Path path;

    /**
     * The prefix extracted from the script file name.
     */
    private final String prefix;

    /**
     * The script engine used to execute the script.
     */
    private final ScriptEngine scriptEngine;

    /**
     * The point in time the cache expires.
     */
    private Instant cacheExpires;

    /**
     * The cached result returned by the last script run.
     */
    private Result cacheData;

    /**
     * The cache for the script compiled for faster execution (can be {@code null} if the engine does not support
     * compiling).
     */
    private CompiledScript script;

    /**
     * Create a new snippet.
     *
     * @param config the global config instance
     * @param path   the path to the script of the snippet
     */
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

    /**
     * Returns the path to the script of the snippet.
     *
     * @return the path to the script of the snippet
     */
    public Path getPath() {
        return this.path;
    }

    /**
     * Returns prefix extracted from the script file name.
     *
     * @return prefix extracted from the script file name
     */
    public String getPrefix() {
        return this.prefix;
    }

    /**
     * Execute the script.
     * <p>
     * The script will only be executed if there is no cached result available or the cache is outdated. If the used
     * script engine does not support compiling, the script is read from disc and executed. If it does support
     * compiling, the compiled script is executed.
     *
     * @return the result returned by the script run
     */
    public Result exec() {
        final Instant now = Instant.now();

        if (now.isAfter(this.cacheExpires)) {
            LOG.trace("Cache timed out - executing script...");

            final Result.Builder resultBuilder = Result.builder(this.prefix);

            // TODO: Use a context to redirect I/O
            final Bindings bindings = this.scriptEngine.createBindings();
            bindings.put("prefix", this.prefix);
            bindings.put("results", resultBuilder);

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

            // Build the result from script
            final Result result = resultBuilder.build();

            // Update the cache and let it expire after the TTL specified by the script using the global configuration
            // as fallback
            this.cacheExpires = now.plus(result.getCacheDuration().orElse(this.config.getCacheDuration()));
            this.cacheData = result;
        }

        // Return the (maybe updated) cached data
        return this.cacheData;
    }

    /**
     * Flush the caches and recompile the script if possible.
     */
    public void flush() {
        // Reset the cache data and time
        this.cacheExpires = Instant.MIN;
        this.cacheData = null;

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
