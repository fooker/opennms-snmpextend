package org.opennms.snmpextend.snippets;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.hash.Hashing;
import org.opennms.snmpextend.args.Config;
import org.opennms.snmpextend.proto.DataProvider;
import org.opennms.snmpextend.proto.ObjectId;
import org.opennms.snmpextend.values.StringValue;
import org.opennms.snmpextend.values.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Instant;
import java.util.NavigableMap;

public class SnippetsDataProvider implements DataProvider {

    private final static Logger LOG = LoggerFactory.getLogger(SnippetsDataProvider.class);

    private final Config config;

    private final SnippetManager snippetManager;

    private Instant cachedTime = Instant.MIN;
    private NavigableMap<ObjectId, Value> cachedData = null;

    @Inject
    public SnippetsDataProvider(final Config config,
                                final SnippetManager snippetManager) {
        this.config = config;
        this.snippetManager = snippetManager;
    }

    @Override
    public NavigableMap<ObjectId, Value> fetch() {

        final Instant now = Instant.now();

        if (now.isAfter(cachedTime.plus(this.config.getCacheDuration()))) {
            LOG.trace("Cache timed out - reloading...");

            this.cachedTime = now;
            this.cachedData = this.load();
        }

        return this.cachedData;
    }

    private NavigableMap<ObjectId, Value> load() {
        final ImmutableSortedMap.Builder<ObjectId, Value> data = ImmutableSortedMap.naturalOrder();

        for (final Snippet snippet : this.snippetManager.findSnippets()) {
            LOG.trace("Munching snippet: {} - {}", snippet.getPath(), snippet.getPrefix());


            final Result result;

            try {
                result = snippet.load();

            } catch (final Exception e) {
                LOG.error("Failed to execute snippet: {}", snippet.getPath(), e);
                continue;
            }

            result.forEach(record -> {
                final int index = nameToIndex(record.getName());

                LOG.trace("Munching record: {} @ {} : {}", record.getName(), index, record.getValue());

                data.put(this.config.getBaseObjectId().at(1).at(index), new StringValue(record.getName()));
                data.put(this.config.getBaseObjectId().at(2).at(index), record.getValue());
            });
        }

        return data.build();
    }

    private static int nameToIndex(final String name) {
        return Hashing.consistentHash(Hashing.murmur3_32().hashUnencodedChars(name), 2 << 16);
    }
}
