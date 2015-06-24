package org.opennms.snmpextend.agent.values;

import java.time.Duration;

/**
 * A duration value.
 */
public class TimeticksValue extends Value {

    /**
     * The value.
     */
    private final Duration value;

    /**
     * Create a new duration value.
     *
     * @param value the value
     */
    public TimeticksValue(final Duration value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return Long.toString(this.value.toMillis() / 10);
    }

    @Override
    public String getType() {
        return "TIMETICKS";
    }
}
