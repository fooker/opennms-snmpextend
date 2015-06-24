package org.opennms.snmpextend.agent.values;

/**
 * A gauge value.
 */
public class GaugeValue extends Value {
    /**
     * The value.
     */
    private final int value;

    /**
     * Create a new gauge value.
     *
     * @param value the value
     */
    public GaugeValue(final int value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return Integer.toString(this.value);
    }

    @Override
    public String getType() {
        return "GAUGE";
    }
}
