package org.opennms.snmpextend.agent.args;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.PathOptionHandler;
import org.opennms.snmpextend.agent.proto.ObjectId;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

/**
 * The general application configuration.
 * <p>
 * The parses command line arguments and stores the parsed values for further use.
 */
public class Config {

    @Option(name = "--cache",
            metaVar = "TTL",
            handler = DurationOptionHandler.class,
            usage = "the duration data from scripts is cached (in seconds)")
    private Duration cacheDuration = Duration.ofSeconds(30);


    @Option(name = "--snippets",
            metaVar = "PATH",
            handler = PathOptionHandler.class,
            usage = "the base directory where the snippes life")
    private Path snippetPath = Paths.get("/etc/snmp/opennms");

    @Option(name = "--base-oid",
            metaVar = "OID",
            handler = ObjectIdOptionHandler.class,
            usage = "the base OID exported")
    private ObjectId baseObjectId = ObjectId.get(1, 3, 6, 1, 4, 1, 5813, 2);

    /**
     * Parse the passed command line arguments and propagate them to the instance
     *
     * @param args the command line arguments
     */
    public Config(final String... args) {
        final CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);

        } catch (final CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println();

            parser.printUsage(System.err);

            System.exit(-1);
        }
    }

    /**
     * Returns the duration the data from scripts is cached.
     *
     * @return the cache duration
     */
    public Duration getCacheDuration() {
        return this.cacheDuration;
    }

    /**
     * Returns the path where the snippes are searched for.
     *
     * @return the snippets base path
     */
    public Path getSnippetPath() {
        return this.snippetPath;
    }

    /**
     * The base object ID for the exported data.
     *
     * @return the path
     */
    public ObjectId getBaseObjectId() {
        return this.baseObjectId;
    }
}
