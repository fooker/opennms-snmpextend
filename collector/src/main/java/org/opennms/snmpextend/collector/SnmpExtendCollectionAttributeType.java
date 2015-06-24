package org.opennms.snmpextend.collector;

import org.opennms.netmgt.collection.api.AttributeGroupType;
import org.opennms.netmgt.collection.api.CollectionAttribute;
import org.opennms.netmgt.collection.api.Persister;
import org.opennms.netmgt.collection.support.AbstractCollectionAttributeType;

public class SnmpExtendCollectionAttributeType extends AbstractCollectionAttributeType {

    private final String type;
    private final String name;

    public SnmpExtendCollectionAttributeType(final String type,
                                             final String name) {
        super(new AttributeGroupType("snmpext_" + name,
                                     AttributeGroupType.IF_TYPE_IGNORE));

        this.type = type;
        this.name = name;

        this.getGroupType().addAttributeType(this);
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void storeAttribute(final CollectionAttribute attribute, final Persister persister) {
        if ("string".equalsIgnoreCase(this.type)) {
            persister.persistStringAttribute(attribute);
        } else {
            persister.persistNumericAttribute(attribute);
        }
    }
}
