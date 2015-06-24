package org.opennms.snmpextend.agent.proto;

import com.google.common.base.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.primitives.Ints;

import java.util.*;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A SNMP object ID.
 */
public class ObjectId implements Comparable<ObjectId> {

    /**
     * The root (empty) object ID.
     */
    public static final ObjectId ROOT = new ObjectId();

    /**
     * The parts the object ID is build of.
     */
    private final List<Integer> parts;

    /**
     * Build an object ID from a list of parts.
     *
     * @param parts the parts the object ID is build of
     */
    private ObjectId(final List<Integer> parts) {
        this.parts = parts;
    }

    /**
     * Build an object ID from a array of parts.
     *
     * @param parts the parts the object ID is build of
     */
    private ObjectId(final int... parts) {
        this(Ints.asList(parts));
    }

    /**
     * Create a direct child of the object ID by attaching the passed ID.
     *
     * @param part the ID to attach.
     * @return the new object ID
     */
    public ObjectId at(final int part) {
        return new ObjectId(ImmutableList.<Integer>builder()
                                         .addAll(this.parts)
                                         .add(part)
                                         .build());
    }

    /**
     * Create a direct child of the object ID by attaching the passed IDs.
     *
     * @param parts the IDs to attach.
     * @return the new object ID
     */
    public ObjectId at(final int... parts) {
        return new ObjectId(ImmutableList.<Integer>builder()
                                         .addAll(this.parts)
                                         .addAll(Ints.asList(parts))
                                         .build());
    }

    @Override
    public boolean equals(final Object that) {
        if (that == null) {
            return false;
        }
        if (that == this) {
            return true;
        }
        if (that.getClass() != ObjectId.class) {
            return false;
        }

        return Objects.equals(this.parts,
                              ((ObjectId) that).parts);
    }

    @Override
    public int compareTo(final ObjectId that) {
        final Iterator<Integer> i1 = this.parts.iterator();
        final Iterator<Integer> i2 = that.parts.iterator();

        while (i1.hasNext() || i2.hasNext()) {
            final int p1 = Iterators.getNext(i1, Integer.MIN_VALUE);
            final int p2 = Iterators.getNext(i2, Integer.MIN_VALUE);

            final int c = Integer.compare(p1, p2);
            if (c != 0) {
                return c;
            }
        }

        return 0;
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(this.parts);
    }

    @Override
    public String toString() {
        return '.' + Joiner.on('.').join(this.parts);
    }

    /**
     * Build an object ID from a array of parts.
     *
     * @param parts the parts the object ID is build of
     */
    public static ObjectId get(final int... parts) {
        return new ObjectId(parts);
    }

    /**
     * Parse the given string to an object ID.
     * The passed string must be an ObjectID with an optional leading dot.
     *
     * @param oid the object ID to parse
     * @return the object ID
     */
    public static ObjectId parse(final String oid) {
        return new ObjectId(Splitter.on('.')
                                    .omitEmptyStrings()
                                    .splitToList(oid)
                                    .stream()
                                    .map(Integer::parseInt)
                                    .collect(Collectors.toList()));
    }

    /**
     * Get the parts of the object ID as list.
     *
     * @return the list of parts
     */
    public List<Integer> getParts() {
        return parts;
    }

    /**
     * Checks if this object ID is a child of the passed object ID.
     *
     * @param baseOid the parent object ID to check for
     * @return {@code true} if it's a child, {@code false} otherwise
     */
    public boolean startsWith(final ObjectId baseOid) {
        if (baseOid.parts.size() > this.parts.size()) {
            return false;
        }

        final Iterator<Integer> it = this.parts.iterator();
        for (final int p : baseOid.parts) {
            if (it.next() != p) {
                return false;
            }
        }

        return true;
    }
}
