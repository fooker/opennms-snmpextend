package org.opennms.snmpextend.collector;

import org.opennms.netmgt.collectd.CollectionTimedOut;
import org.opennms.netmgt.collectd.CollectionWarning;
import org.opennms.netmgt.collectd.SnmpCollectionAgent;
import org.opennms.netmgt.collection.api.*;
import org.opennms.netmgt.events.api.EventProxy;
import org.opennms.netmgt.rrd.RrdRepository;
import org.opennms.netmgt.snmp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class SnmpExtendCollector implements ServiceCollector {

    private static final Logger LOG = LoggerFactory.getLogger(SnmpExtendCollector.class);

    public static final SnmpObjId SNMP_EXT_NAME_ID = SnmpObjId.get("1.3.6.1.4.1.5813.1.1");
    public static final SnmpObjId SNMP_EXT_VALUE_ID = SnmpObjId.get("1.3.6.1.4.1.5813.1.2");

    @Override
    public void initialize(final Map<String, String> parameters) throws CollectionInitializationException {

    }

    @Override
    public void release() {

    }

    @Override
    public void initialize(final CollectionAgent agent, final Map<String, Object> parameters) throws
                                                                                              CollectionInitializationException {
        ((SnmpCollectionAgent) agent).validateAgent();
    }

    @Override
    public void release(final CollectionAgent agent) {

    }

    @Override
    public CollectionSet collect(final CollectionAgent agent,
                                 final EventProxy eproxy,
                                 final Map<String, Object> parameters) throws
                                                                       CollectionException {
        final SnmpAgentConfig snmpAgentConfig = ((SnmpCollectionAgent) agent).getAgentConfig();

        final SnmpExtendCollectionSet collectionSet = new SnmpExtendCollectionSet(agent);

        final TableTracker tracker = new TableTracker(collectionSet,
                                                      SNMP_EXT_NAME_ID,
                                                      SNMP_EXT_VALUE_ID);

        final SnmpWalker walker = SnmpUtils.createWalker(snmpAgentConfig,
                                                         "SNMP Extend collector for " + agent.getHostAddress(),
                                                         tracker);
        walker.start();

        try {
            walker.waitFor();

        } catch (final InterruptedException e) {
            throw new CollectionWarning("Collection interrupted", e);
        }

        if (walker.failed()) {
            if (walker.timedOut()) {
                throw new CollectionTimedOut(walker.getErrorMessage());
            }

            throw new CollectionWarning("Collection failed due to: " + walker.getErrorMessage(),
                                        walker.getErrorThrowable());
        }

        return collectionSet;
    }

    @Override
    public RrdRepository getRrdRepository(final String collectionName) {
        return new RrdRepository() {{
            setStep(300);
            setHeartBeat(600);
            setRrdBaseDir(new File("/opt/opennms/share/rrd/snmpext/"));
            setRraList(new ArrayList<String>() {{
                add("RRA:AVERAGE:0.5:1:2016");
                add("RRA:AVERAGE:0.5:12:1488");
                add("RRA:AVERAGE:0.5:288:366");
                add("RRA:MAX:0.5:288:366");
                add("RRA:MIN:0.5:288:366");
            }});
        }};
    }
}
