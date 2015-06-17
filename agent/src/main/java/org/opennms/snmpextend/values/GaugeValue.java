package org.opennms.snmpextend.values;

public class GaugeValue extends Value {

    private final int value;

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
