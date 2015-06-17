package org.opennms.snmpextend.values;

public abstract class Value {

    public abstract String getValue();
    public abstract String getType();

    @Override
    public final String toString() {
        return this.getType() + "(" + this.getValue() + ")";
    }
}
