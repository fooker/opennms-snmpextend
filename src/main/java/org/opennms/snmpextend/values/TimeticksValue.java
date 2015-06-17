package org.opennms.snmpextend.values;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public class TimeticksValue extends Value {

    private final Duration value;

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
