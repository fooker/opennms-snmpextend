package org.opennms.snmpextend.collector;

import org.opennms.netmgt.collection.api.CollectionAgent;
import org.opennms.netmgt.collection.support.AbstractCollectionResource;

public class SnmpExtendCollectionResource extends AbstractCollectionResource {

    protected SnmpExtendCollectionResource(final CollectionAgent agent) {
        super(agent);
    }

    @Override
    public String getResourceTypeName() {
        return RESOURCE_TYPE_NODE;
    }

    @Override
    public String getInstance() {
        return null;
    }

    public void addValue(final String name,
                         final String type,
                         final String value) {
        final SnmpExtendCollectionAttributeType attributeType = new SnmpExtendCollectionAttributeType(type, name);

        final SnmpExtendCollectionAttribute attribute = new SnmpExtendCollectionAttribute(attributeType,
                                                                                          this,
                                                                                          value);

        this.addAttribute(attribute);
    }
}
