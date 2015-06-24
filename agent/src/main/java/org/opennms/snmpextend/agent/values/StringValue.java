package org.opennms.snmpextend.agent.values;

/**
 * A string value.
 */
public class StringValue extends Value {

    /**
     * The value.
     */
    private final String value;

    /**
     * Create a new string value.
     *
     * @param value the value
     */
    public StringValue(final String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public String getType() {
        return "STRING";
    }
}
