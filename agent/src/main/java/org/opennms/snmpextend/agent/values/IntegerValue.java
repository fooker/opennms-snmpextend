package org.opennms.snmpextend.agent.values;

/**
 * A integer value.
 */
public class IntegerValue extends Value {
    /**
     * The value.
     */
    private final int value;

    /**
     * Create a new integer value.
     *
     * @param value the value
     */
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
