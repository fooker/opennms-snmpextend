package org.opennms.snmpextend.agent.args;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OneArgumentOptionHandler;
import org.kohsuke.args4j.spi.Setter;
import org.opennms.snmpextend.agent.proto.ObjectId;

public class ObjectIdOptionHandler extends OneArgumentOptionHandler<ObjectId> {
    public ObjectIdOptionHandler(final CmdLineParser parser,
                                 final OptionDef option,
                                 final Setter<? super ObjectId> setter) {
        super(parser, option, setter);
    }

    @Override
    protected ObjectId parse(final String s) throws NumberFormatException, CmdLineException {
        return ObjectId.parse(s);
    }
}
