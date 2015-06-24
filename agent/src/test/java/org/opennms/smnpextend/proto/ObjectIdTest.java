package org.opennms.smnpextend.proto;

import org.hamcrest.comparator.ComparatorMatcherBuilder;
import org.junit.Test;
import org.opennms.snmpextend.agent.proto.ObjectId;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class ObjectIdTest {

    @Test
    public void testCreation() {
        final ObjectId oid = ObjectId.get(1, 2, 3, 4);

        assertThat(oid.getParts(), contains(1, 2, 3, 4));
    }

    @Test
    public void testParsing() {
        final ObjectId oid = ObjectId.parse(".1.2.3.4");

        assertThat(oid.getParts(), contains(1, 2, 3, 4));
    }

    @Test
    public void testEquals() {
        final ObjectId oid1 = ObjectId.get(1, 2, 3, 4);
        final ObjectId oid2 = ObjectId.get(1, 2, 3, 4);
        final ObjectId oid3 = ObjectId.get(1, 2, 3, 4, 5);
        final ObjectId oid4 = ObjectId.get(1, 2, 3, 5);

        assertThat(oid1, is(equalTo(oid1)));
        assertThat(oid1, is(equalTo(oid2)));

        assertThat(oid2, is(not(equalTo(oid3))));
        assertThat(oid2, is(not(equalTo(oid4))));
        assertThat(oid3, is(not(equalTo(oid4))));
    }

    @Test
    public void testHash() {
        final ObjectId oid1 = ObjectId.get(1, 2, 3, 4);
        final ObjectId oid2 = ObjectId.get(1, 2, 3, 4);
        final ObjectId oid3 = ObjectId.get(1, 2, 3, 4, 5);
        final ObjectId oid4 = ObjectId.get(1, 2, 3, 5);

        assertThat(oid1.hashCode(), is(oid1.hashCode()));
        assertThat(oid1.hashCode(), is(oid2.hashCode()));

        assertThat(oid2.hashCode(), is(not(oid3.hashCode())));
        assertThat(oid2.hashCode(), is(not(oid4.hashCode())));
        assertThat(oid3.hashCode(), is(not(oid4.hashCode())));
    }

    @Test
    public void testCompare() {
        final ComparatorMatcherBuilder<ObjectId> oid = ComparatorMatcherBuilder.usingNaturalOrdering();

        final ObjectId oid1 = ObjectId.get(1, 2, 3, 4);
        final ObjectId oid2 = ObjectId.get(1, 2, 3, 4);
        final ObjectId oid3 = ObjectId.get(1, 2, 3, 4, 5);
        final ObjectId oid4 = ObjectId.get(1, 2, 3, 5);

        assertThat(oid1, oid.comparesEqualTo(oid2));
        assertThat(oid2, oid.comparesEqualTo(oid1));

        assertThat(oid3, oid.greaterThan(oid2));
        assertThat(oid2, oid.lessThan(oid3));

        assertThat(oid4, oid.greaterThan(oid3));
        assertThat(oid3, oid.lessThan(oid4));

        assertThat(oid4, oid.greaterThan(oid2));
        assertThat(oid2, oid.lessThan(oid4));
    }

    @Test
    public void testStartsWith() {

        final ObjectId oid1 = ObjectId.get(1, 2, 3);
        final ObjectId oid2 = ObjectId.get(1, 2, 3, 4);
        final ObjectId oid3 = ObjectId.get(1, 2, 3, 4, 5);
        final ObjectId oid4 = ObjectId.get(1, 2, 3, 5);

        assertThat(oid2.startsWith(oid1), is(true));
        assertThat(oid3.startsWith(oid1), is(true));
        assertThat(oid4.startsWith(oid1), is(true));

        assertThat(oid1.startsWith(oid2), is(false));
        assertThat(oid1.startsWith(oid3), is(false));
        assertThat(oid1.startsWith(oid4), is(false));

        assertThat(oid3.startsWith(oid2), is(true));

        assertThat(oid3.startsWith(oid4), is(false));

        assertThat(oid2.startsWith(oid4), is(false));
        assertThat(oid4.startsWith(oid2), is(false));
    }
}
