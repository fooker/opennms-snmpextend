package org.opennms.snmpextend.agent.values;

public class StringValue extends Value {

    private final String value;

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
