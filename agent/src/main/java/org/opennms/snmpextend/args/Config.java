package org.opennms.snmpextend.args;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.PathOptionHandler;
import org.opennms.snmpextend.proto.ObjectId;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

public class Config {

    @Option(name = "--cache",
            metaVar = "TTL",
            handler = DurationOptionHandler.class)
    private Duration cacheDuration = Duration.ofSeconds(30);


    @Option(name = "--snippets",
            metaVar = "PATH",
            handler = PathOptionHandler.class)
    private Path snippetPath = Paths.get("/etc/snmp/opennms");

    @Option(name = "--base-oid",
            metaVar = "OID",
            handler = ObjectIdOptionHandler.class)
    private ObjectId baseObjectId = ObjectId.get(1, 3, 6, 1, 4, 1, 5813, 1);

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

    public Duration getCacheDuration() {
        return this.cacheDuration;
    }

    public Path getSnippetPath() {
        return this.snippetPath;
    }

    public ObjectId getBaseObjectId() {
        return this.baseObjectId;
    }
}
