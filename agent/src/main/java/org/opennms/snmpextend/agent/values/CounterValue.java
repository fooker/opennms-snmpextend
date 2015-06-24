package org.opennms.snmpextend.agent.values;

public class CounterValue extends Value {

    private final int value;

    public CounterValue(final int value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return Integer.toString(this.value);
    }

    @Override
    public String getType() {
        return "COUNTER";
    }
}
