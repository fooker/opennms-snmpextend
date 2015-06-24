package org.opennms.snmpextend.agent.values;

public class IntegerValue extends Value {

    private final int value;

    public IntegerValue(final int value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return Integer.toString(this.value);
    }

    @Override
    public String getType() {
        return "INTEGER";
    }
}
