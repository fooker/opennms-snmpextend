package org.opennms.snmpextend.agent.values;

/**
 * A counter value.
 */
public class CounterValue extends Value {
    /**
     * The value.
     */
    private final int value;

    /**
     * Create a new counter value.
     *
     * @param value the value
     */
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
