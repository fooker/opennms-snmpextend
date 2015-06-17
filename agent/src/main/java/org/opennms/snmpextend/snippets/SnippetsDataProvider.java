package org.opennms.snmpextend.snippets;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.hash.Hashing;
import org.opennms.snmpextend.proto.Communicator;
import org.opennms.snmpextend.proto.DataProvider;
import org.opennms.snmpextend.proto.ObjectId;
import org.opennms.snmpextend.values.StringValue;
import org.opennms.snmpextend.values.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NavigableMap;

public class SnippetsDataProvider implements DataProvider {

    private final static Logger LOG = LoggerFactory.getLogger(SnippetsDataProvider.class);

    public static final ObjectId BASE_OID = ObjectId.parse(".1.3.6.1.4.1.5813.1");

    private final SnippetManager snippetManager;

    public SnippetsDataProvider(final SnippetManager snippetManager) {
        this.snippetManager = snippetManager;
    }

    @Override
    public NavigableMap<ObjectId, Value> fetch() {
        final ImmutableSortedMap.Builder<ObjectId, Value> data = ImmutableSortedMap.naturalOrder();

        for (final Snippet snippet : this.snippetManager.findSnippets()) {
            LOG.trace("Muniching snippet: {} - {}", snippet.getPath(), snippet.getPrefix());


            final Result result;

            try {
                result = snippet.load();

            } catch (final Exception e) {
                LOG.error("Failed to execute snippet: {}", snippet.getPath(), e);
                continue;
            }

            result.forEach(record -> {
                final int index = nameToIndex(record.getName());

                LOG.trace("Muniching record: {} @ {} : {}", record.getName(), index, record.getValue());

                data.put(BASE_OID.at(1).at(index), new StringValue(record.getName()));
                data.put(BASE_OID.at(2).at(index), record.getValue());
            });
        }

        return data.build();
    }

    private static int nameToIndex(final String name) {
        return Hashing.consistentHash(Hashing.murmur3_32()
                                             .hashUnencodedChars(name),
                                      2 << 16);
    }
}
