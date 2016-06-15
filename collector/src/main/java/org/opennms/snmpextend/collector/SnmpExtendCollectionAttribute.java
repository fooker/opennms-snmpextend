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
    public Number getNumericValue() {
        try {
            return Double.valueOf(this.value);
        } catch (final NumberFormatException e) {
            return null;
        }
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
