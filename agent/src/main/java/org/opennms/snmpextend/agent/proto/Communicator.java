package org.opennms.snmpextend.agent.proto;

import com.google.common.base.Throwables;
import org.opennms.snmpextend.agent.values.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.util.Map;

@Singleton
public class Communicator implements Runnable {

    private final static Logger LOG = LoggerFactory.getLogger(Communicator.class);

    public static final String REQUEST_PING = "PING";
    public static final String REQUEST_GET = "GET";
    public static final String REQUEST_GET_NEXT = "GETNEXT";
    public static final String REQUEST_SET = "SET";

    public static final String[] RESPONSE_EMPTY = {};
    public static final String[] RESPONSE_PONG = {"PONG"};
    public static final String[] RESPONSE_NONE = {"NONE"};
    public static final String[] RESPONSE_NOT_WRITABLE = {"NOT-WRITABLE"};

    private final DataProvider provider;

    private final Reader rx;
    private final Writer tx;

    @Inject
    public Communicator(final DataProvider provider,
                        final Reader rx,
                        final Writer tx) {
        this.provider = provider;

        this.rx = rx;
        this.tx = tx;
    }

    @Override
    public void run() {
        LOG.trace("Communicator running...");

        try (final BufferedReader rx = new BufferedReader(this.rx);
             final BufferedWriter tx = new BufferedWriter(this.tx)) {
            for (String request = rx.readLine(); request != null && !request.isEmpty(); request = rx.readLine()) {
                LOG.trace("Received line: '{}'", request);

                // The doc is not very clear about casing - assuming everything is uppercase
                request = request.toUpperCase();

                final String[] response;
                switch (request) {
                    case REQUEST_PING: {
                        LOG.trace("Received ping");

                        response = RESPONSE_PONG;
                        break;
                    }

                    case REQUEST_GET: {
                        final ObjectId oid = ObjectId.parse(rx.readLine());

                        LOG.trace("Received get {}", oid);

                        response = handleGet(oid);
                        break;
                    }

                    case REQUEST_GET_NEXT: {
                        final ObjectId oid = ObjectId.parse(rx.readLine());

                        LOG.trace("Received get next {}", oid);

                        response = handleGetNext(oid);
                        break;
                    }

                    case REQUEST_SET: {
                        LOG.trace("Received set  - ignored");

                        rx.readLine(); // Read and ignore OID ...
                        rx.readLine(); // ... and value

                        response = RESPONSE_NOT_WRITABLE;
                        break;
                    }

                    default: {
                        LOG.error("Unknown command: '{}'", request);

                        response = RESPONSE_EMPTY;
                        break;
                    }
                }

                for (final String r : response) {
                    LOG.trace("Respond line: '{}'", r);

                    tx.write(r);
                    tx.write('\n');
                }

                tx.flush();
            }

        } catch (final IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private String[] handleGet(final ObjectId oid) {
        // Get the value
        final Value value = this.provider.fetch().get(oid);
        if (value == null) {
            return RESPONSE_NONE;
        }

        return createResponse(oid,
                              value);
    }

    private String[] handleGetNext(final ObjectId oid) {
        // Find the next (strict higher) OID
        final Map.Entry<ObjectId, Value> entry = this.provider.fetch().higherEntry(oid);
        if (entry == null) {
            return RESPONSE_NONE;
        }

        return createResponse(entry.getKey(),
                              entry.getValue());
    }

    private static String[] createResponse(final ObjectId oid,
                                           final Value value) {
        return new String[]{
                oid.toString(),
                value.getType(),
                value.getValue()
        };
    }
}