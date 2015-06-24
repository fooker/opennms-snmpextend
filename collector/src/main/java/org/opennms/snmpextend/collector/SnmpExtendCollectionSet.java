package org.opennms.snmpextend.collector;

import org.opennms.netmgt.collection.api.CollectionAgent;
import org.opennms.netmgt.collection.api.ServiceCollector;
import org.opennms.netmgt.collection.support.SingleResourceCollectionSet;
import org.opennms.netmgt.snmp.RowCallback;
import org.opennms.netmgt.snmp.SnmpRowResult;
import org.opennms.netmgt.snmp.SnmpValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class SnmpExtendCollectionSet extends SingleResourceCollectionSet implements RowCallback {

    private static final Logger LOG = LoggerFactory.getLogger(SnmpExtendCollectionSet.class);

    public SnmpExtendCollectionSet(final CollectionAgent collectionAgent) {
        super(new SnmpExtendCollectionResource(collectionAgent),
              new Date());

        this.setStatus(ServiceCollector.COLLECTION_SUCCEEDED);
    }

    @Override
    public void rowCompleted(final SnmpRowResult result) {
        LOG.trace("Got SNMPEXT result: {}", result);

        final String name = new String(result.getResults().get(0).getValue().getBytes());
        final String type = findType(result.getResults().get(1).getValue().getType());
        final String value = result.getResults().get(1).getValue().toDisplayString();

        LOG.trace("Got SNMPEXT data: {} = {}", name, value);

        ((SnmpExtendCollectionResource) this.getCollectionResource()).addValue(name,
                                                                               type,
                                                                               value);
    }

    private static String findType(final int type) {
        switch (type) {
            case SnmpValue.SNMP_COUNTER32:
            case SnmpValue.SNMP_COUNTER64:
                return "counter";

            case SnmpValue.SNMP_INT32:
            case SnmpValue.SNMP_GAUGE32:
                return "gauge";

            default:
                return "string";
        }
    }
}
