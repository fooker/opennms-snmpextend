package org.opennms.snmpextend.agent.values;

import org.opennms.snmpextend.agent.proto.ObjectId;

/**
 * A object ID value.
 */
public class ObjectIdValue extends Value {

    /**
     * The value.
     */
    private final ObjectId value;

    /**
     * Create a new object ID value.
     *
     * @param value the value
     */
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
