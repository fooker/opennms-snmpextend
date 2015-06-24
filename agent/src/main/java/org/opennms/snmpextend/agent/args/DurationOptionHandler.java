package org.opennms.snmpextend.agent.args;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OneArgumentOptionHandler;
import org.kohsuke.args4j.spi.Setter;

import java.time.Duration;

public class DurationOptionHandler extends OneArgumentOptionHandler<Duration> {
    public DurationOptionHandler(final CmdLineParser parser,
                                 final OptionDef option,
                                 final Setter<? super Duration> setter) {
        super(parser, option, setter);
    }

    @Override
    protected Duration parse(final String s) throws NumberFormatException, CmdLineException {
        return Duration.ofSeconds(Integer.parseInt(s));
    }
}
