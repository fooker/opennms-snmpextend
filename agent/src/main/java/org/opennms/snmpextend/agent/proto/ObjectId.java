package org.opennms.snmpextend.agent.proto;

import com.google.common.base.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.primitives.Ints;

import java.util.*;
import java.util.Objects;
import java.util.stream.Collectors;

public class ObjectId implements Comparable<ObjectId> {

    public static final ObjectId ROOT = new ObjectId();

    private final List<Integer> parts;

    private ObjectId(final List<Integer> parts) {
        this.parts = parts;
    }

    private ObjectId(final int... parts) {
        this(Ints.asList(parts));
    }

    public ObjectId at(final int part) {
        return new ObjectId(ImmutableList.<Integer>builder()
                                         .addAll(this.parts)
                                         .add(part)
                                         .build());
    }

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

    public static ObjectId get(final int... parts) {
        return new ObjectId(parts);
    }

    public static ObjectId parse(final String oid) {
        return new ObjectId(Splitter.on('.')
                                    .omitEmptyStrings()
                                    .splitToList(oid)
                                    .stream()
                                    .map(Integer::parseInt)
                                    .collect(Collectors.toList()));
    }

    public List<Integer> getParts() {
        return parts;
    }

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
