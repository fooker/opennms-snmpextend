package org.opennms.snmpextend.agent.proto;

import com.google.common.base.Throwables;
import org.opennms.snmpextend.agent.values.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.util.Map;

/**
 * The protocol implementation and handler used for communication with the NetSNMP agent.
 */
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

    /**
     * The data provider used to publish data.
     */
    private final DataProvider provider;

    /**
     * The communication channel to use (receive).
     */
    private final Reader rx;

    /**
     * The communication channel to use (send).
     */
    private final Writer tx;

    @Inject
    public Communicator(final DataProvider provider,
                        final Reader rx,
                        final Writer tx) {
        this.provider = provider;

        this.rx = rx;
        this.tx = tx;
    }

    /**
     * Handle incoming requests until the the communication channels are closed by the agent.
     */
    @Override
    public void run() {
        LOG.trace("Communicator running...");

        try (final BufferedReader rx = new BufferedReader(this.rx);
             final BufferedWriter tx = new BufferedWriter(this.tx)) {

            // Read lines from the agent
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
                        // Read and parse the requested object ID
                        final ObjectId oid = ObjectId.parse(rx.readLine());

                        LOG.trace("Received get {}", oid);

                        // Get the value for the requested object ID
                        final Value value = this.provider.fetch().get(oid);
                        if (value != null) {
                            response = createResponse(oid,
                                                      value);

                        } else {
                            response = RESPONSE_NONE;
                        }

                        break;
                    }

                    case REQUEST_GET_NEXT: {
                        // Read and parse the requested object ID
                        final ObjectId oid = ObjectId.parse(rx.readLine());

                        LOG.trace("Received get next {}", oid);

                        // Find the next (strict higher) object ID and value
                        final Map.Entry<ObjectId, Value> entry = this.provider.fetch().higherEntry(oid);
                        if (entry != null) {
                            response = createResponse(entry.getKey(),
                                                      entry.getValue());

                        } else {
                            response = RESPONSE_NONE;
                        }

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

                // Send the response to the agent line by line
                for (final String r : response) {
                    LOG.trace("Respond line: '{}'", r);

                    tx.write(r);
                    tx.write('\n');
                }

                // Ensure the response is delivered
                tx.flush();
            }

        } catch (final IOException e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * Creates a response for the given object ID and value.
     * <p>
     * The type of the response is interfered from the passed value.
     *
     * @param oid   the object ID of the response to build
     * @param value the value of the response to build
     * @return the response lines
     */
    private static String[] createResponse(final ObjectId oid,
                                           final Value value) {
        return new String[]{
                oid.toString(),
                value.getType(),
                value.getValue()
        };
    }
}
