package org.opennms.snmpextend.agent.snippets;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;
import org.opennms.snmpextend.agent.proto.ObjectId;
import org.opennms.snmpextend.agent.values.*;

import java.net.InetAddress;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;

public class Result implements Iterable<Result.Record> {

    public static class Record {
        private final String name;
        private final Value value;

        private Record(final String name, final Value value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return this.name;
        }

        public Value getValue() {
            return this.value;
        }
    }

    private final List<Record> entries;

    private Result(final List<Record> entries) {
        this.entries = entries;
    }

    @Override
    public Iterator<Record> iterator() {
        return this.entries.iterator();
    }

    public static class Builder {

        private final ImmutableList.Builder<Record> entries = ImmutableList.builder();
        private final ImmutableSortedMap.Builder<String, Value> values = ImmutableSortedMap.naturalOrder();

        private final String prefix;

        private Builder(final String prefix) {
            this.prefix = prefix;
        }

        public Builder add(final String name,
                           final Value value) {
            // Extend the name with the prefix if not already included
            final String extendedName;
            if (name.startsWith(this.prefix)) {
                extendedName = name;
            } else {
                extendedName = this.prefix + Character.toUpperCase(name.charAt(0)) + name.substring(1);
            }

            this.entries.add(new Record(extendedName, value));

            return this;
        }

        public Builder addInteger(final String name, final int value) {
            return this.add(name, new IntegerValue(value));
        }

        public Builder addGauge(final String name, final int value) {
            return this.add(name, new GaugeValue(value));
        }

        public Builder addCounter(final String name, final int value) {
            return this.add(name, new CounterValue(value));
        }

        public Builder addTimeticks(final String name, final Duration value) {
            return this.add(name, new TimeticksValue(value));
        }

        public Builder addIpAddress(final String name, final InetAddress value) {
            return this.add(name, new IpAddressValue(value));
        }

        public Builder addObjectId(final String name, final ObjectId value) {
            return this.add(name, new ObjectIdValue(value));
        }

        public Builder addString(final String name, final String value) {
            return this.add(name, new StringValue(value));
        }

        public Result build() {
            return new Result(this.entries.build());
        }
    }

    public static Builder builder(final String prefix) {
        return new Builder(prefix);
    }
}
