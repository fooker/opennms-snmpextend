package org.opennms.snmpextend.agent.snippets;

import com.google.common.collect.ImmutableList;
import org.opennms.snmpextend.agent.proto.ObjectId;
import org.opennms.snmpextend.agent.values.*;

import java.net.InetAddress;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * A result container to fill by snippets.
 * The result container maps typed values to names.
 */
public class Result implements Iterable<Result.Record> {

    /**
     * A record containing a single name / value pair.
     */
    public static class Record {
        private final String name;
        private final Value value;

        private Record(final String name, final Value value) {
            this.name = name;
            this.value = value;
        }

        /**
         * Returns the name of the record.
         *
         * @return the name
         */
        public String getName() {
            return this.name;
        }

        /**
         * Returns the value of the record.
         *
         * @return the value
         */
        public Value getValue() {
            return this.value;
        }
    }

    /**
     * The records of the result.
     */
    private final List<Record> records;

    /**
     * The cache TTL for the result.
     */
    private final Optional<Duration> cacheDuration;

    /**
     * Create a result container.
     *
     * @param records       the records in the container
     * @param cacheDuration the cache duration for the records
     */
    private Result(final List<Record> records,
                   final Optional<Duration> cacheDuration) {
        this.records = records;
        this.cacheDuration = cacheDuration;
    }

    @Override
    public Iterator<Record> iterator() {
        return this.records.iterator();
    }

    /**
     * Returns the cache TTL for the result.
     *
     * @return the cache TTL for the result or {@code Optional.empty()} if no cache TTL was specified
     */
    public Optional<Duration> getCacheDuration() {
        return this.cacheDuration;
    }

    /**
     * A Builder for the result container.
     */
    public static class Builder {

        /**
         * The prefix to use for all record names.
         */
        private final String prefix;

        /**
         * The cache TTL for the result.
         *
         * @see Result#cacheDuration
         */
        private Optional<Duration> cacheDuration = Optional.empty();

        /**
         * The list of records.
         *
         * @see Result#records
         * @see org.opennms.snmpextend.agent.snippets.Result.Record
         */
        private final ImmutableList.Builder<Record> records = ImmutableList.builder();

        /**
         * Create a new builder.
         *
         * @param prefix the prefix common to all record names
         */
        private Builder(final String prefix) {
            this.prefix = prefix;
        }

        /**
         * Add a new record.
         *
         * @param name  the name of the record
         * @param value the value of the record
         * @return the builder itself
         */
        public Builder add(final String name,
                           final Value value) {
            // Extend the name with the prefix if not already included
            final String extendedName;
            if (name.startsWith(this.prefix)) {
                extendedName = name;

            } else {
                extendedName = this.prefix + Character.toUpperCase(name.charAt(0)) + name.substring(1);
            }

            // Build and add the record
            this.records.add(new Record(extendedName, value));

            return this;
        }

        /**
         * Add an integer value record.
         *
         * @param name  the name of the record
         * @param value the value of the record
         * @return the builder itself
         */
        public Builder addInteger(final String name, final int value) {
            return this.add(name, new IntegerValue(value));
        }

        /**
         * Add an gauge value record.
         *
         * @param name  the name of the record
         * @param value the value of the record
         * @return the builder itself
         */
        public Builder addGauge(final String name, final int value) {
            return this.add(name, new GaugeValue(value));
        }

        /**
         * Add an counter value record.
         *
         * @param name  the name of the record
         * @param value the value of the record
         * @return the builder itself
         */
        public Builder addCounter(final String name, final int value) {
            return this.add(name, new CounterValue(value));
        }

        /**
         * Add an time tick value record.
         *
         * @param name  the name of the record
         * @param value the value of the record
         * @return the builder itself
         */
        public Builder addTimeticks(final String name, final Duration value) {
            return this.add(name, new TimeticksValue(value));
        }

        /**
         * Add an IP address value record.
         *
         * @param name  the name of the record
         * @param value the value of the record
         * @return the builder itself
         */
        public Builder addIpAddress(final String name, final InetAddress value) {
            return this.add(name, new IpAddressValue(value));
        }

        /**
         * Add an object ID value record.
         *
         * @param name  the name of the record
         * @param value the value of the record
         * @return the builder itself
         */
        public Builder addObjectId(final String name, final ObjectId value) {
            return this.add(name, new ObjectIdValue(value));
        }

        /**
         * Add an string value record.
         *
         * @param name  the name of the record
         * @param value the value of the record
         * @return the builder itself
         */
        public Builder addString(final String name, final String value) {
            return this.add(name, new StringValue(value));
        }

        /**
         * Set the cache to the given value.
         *
         * @param ttl the cache TTL for the result
         * @return the builder itself
         */
        public Builder withCache(final Duration ttl) {
            this.cacheDuration = Optional.ofNullable(ttl);

            return this;
        }

        /**
         * Set the cache to the given value.
         *
         * @param ttl the cache TTL for the result in seconds
         * @return the builder itself
         */
        public Builder withCache(final long ttl) {
            this.cacheDuration = Optional.of(Duration.ofSeconds(ttl));

            return this;
        }

        /**
         * Build the final result container.
         *
         * @return the result container
         */
        public Result build() {
            return new Result(this.records.build(), cacheDuration);
        }
    }

    /**
     * Create a new builder for a result container.
     *
     * @param prefix the prefix common to all record names
     * @return the builder
     */
    public static Builder builder(final String prefix) {
        return new Builder(prefix);
    }
}
