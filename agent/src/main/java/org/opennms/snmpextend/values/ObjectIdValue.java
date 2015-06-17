package org.opennms.snmpextend.values;

import org.opennms.snmpextend.proto.ObjectId;

public class ObjectIdValue extends Value {

    private final ObjectId value;

    public ObjectIdValue(final ObjectId value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value.toString();
    }

    @Override
    public String getType() {
        return "OBJECTID";
    }
}
