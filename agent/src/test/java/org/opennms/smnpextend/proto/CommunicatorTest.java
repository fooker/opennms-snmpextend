package org.opennms.smnpextend.proto;

import com.google.common.collect.ImmutableSortedMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opennms.snmpextend.agent.proto.Communicator;
import org.opennms.snmpextend.agent.proto.ObjectId;
import org.opennms.snmpextend.agent.values.GaugeValue;
import org.opennms.snmpextend.agent.values.IntegerValue;
import org.opennms.snmpextend.agent.values.StringValue;
import org.opennms.snmpextend.agent.values.Value;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PipedReader;
import java.io.PipedWriter;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class CommunicatorTest {

    private Communicator communicator;
    private Thread runner;

    private BufferedWriter tx;
    private BufferedReader rx;

    @Before
    public void setUp() throws Exception {
        final ObjectId BASE_OID = ObjectId.get(9, 9, 9);

        final PipedWriter tx = new PipedWriter();
        final PipedReader rx = new PipedReader();

        this.communicator = new Communicator(() -> ImmutableSortedMap.<ObjectId, Value>naturalOrder()
                                                                     .put(BASE_OID.at(1, 1), new IntegerValue(23))
                                                                     .put(BASE_OID.at(1, 2, 1), new StringValue("foo"))
                                                                     .put(BASE_OID.at(1, 2, 2), new StringValue("bar"))
                                                                     .put(BASE_OID.at(1, 3), new GaugeValue(42))
                                                                     .build(),
                                             new PipedReader(tx),
                                             new PipedWriter(rx));

        this.runner = new Thread(this.communicator);
        this.runner.start();

        this.tx = new BufferedWriter(tx);
        this.rx = new BufferedReader(rx);
    }

    @After
    public void tearDown() throws Exception {
        this.tx.close();
        this.rx.close();

        this.runner.interrupt();
    }

    @Test
    public void testPing() throws Exception {
        this.tx.write("PING\n");
        this.tx.flush();

        assertThat( this.rx.readLine(), is("PONG"));
    }

    @Test
    public void testPingAfterGarbage() throws Exception {
        this.tx.write("FOOBAR\n");
        this.tx.write("PING\n");
        this.tx.flush();

        assertThat( this.rx.readLine(), is("PONG"));
    }

    @Test
    public void testSet() throws Exception {
        this.tx.write("SET\n");
        this.tx.write(".9.9.9\n");
        this.tx.write("1337\n");
        this.tx.flush();

        assertThat( this.rx.readLine(), is("NOT-WRITABLE"));
    }

    @Test
    public void testGet() throws Exception {
        this.tx.write("GET\n");
        this.tx.write(".9.9.9.1.1\n");
        this.tx.flush();

        assertThat( this.rx.readLine(), is(".9.9.9.1.1"));
        assertThat( this.rx.readLine(), is("INTEGER"));
        assertThat( this.rx.readLine(), is("23"));
    }

    @Test
    public void testGetEmpty() throws Exception {
        this.tx.write("GET\n");
        this.tx.write(".9.9.9.1.2\n");
        this.tx.flush();

        assertThat( this.rx.readLine(), is("NONE"));

        this.tx.write("GET\n");
        this.tx.write(".9.9.9.2\n");
        this.tx.flush();

        assertThat( this.rx.readLine(), is("NONE"));
    }

    @Test
    public void testGetNext() throws Exception {
        this.tx.write("GETNEXT\n");
        this.tx.write(".9.9.9\n");
        this.tx.flush();

        assertThat( this.rx.readLine(), is(".9.9.9.1.1"));
        assertThat( this.rx.readLine(), is("INTEGER"));
        assertThat( this.rx.readLine(), is("23"));

        this.tx.write("GETNEXT\n");
        this.tx.write(".9.9.9.1\n");
        this.tx.flush();

        assertThat( this.rx.readLine(), is(".9.9.9.1.1"));
        assertThat( this.rx.readLine(), is("INTEGER"));
        assertThat( this.rx.readLine(), is("23"));

        this.tx.write("GETNEXT\n");
        this.tx.write(".9.9.9.1.1\n");
        this.tx.flush();

        assertThat( this.rx.readLine(), is(".9.9.9.1.2.1"));
        assertThat( this.rx.readLine(), is("STRING"));
        assertThat( this.rx.readLine(), is("foo"));
    }
}
