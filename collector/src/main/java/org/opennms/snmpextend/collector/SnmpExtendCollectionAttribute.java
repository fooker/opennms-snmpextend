package org.opennms.snmpextend.collector;

import org.opennms.netmgt.collection.api.CollectionAttributeType;
import org.opennms.netmgt.collection.api.CollectionResource;
import org.opennms.netmgt.collection.support.AbstractCollectionAttribute;

public class SnmpExtendCollectionAttribute extends AbstractCollectionAttribute {
    private final String value;

    public SnmpExtendCollectionAttribute(final CollectionAttributeType attribType,
                                         final CollectionResource resource,
                                         final String value) {
        super(attribType, resource);

        this.value = value;
    }

    @Override
    public String getNumericValue() {
        return this.value;
    }

    @Override
    public String getMetricIdentifier() {
        return "SNMPEXT_" + this.getAttributeType().getName();
    }

    @Override
    public String getStringValue() {
        return this.value;
    }
}
