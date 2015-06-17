package org.opennms.snmpextend.snippets;

import org.opennms.snmpextend.proto.DataProvider;
import org.opennms.snmpextend.proto.ObjectId;
import org.opennms.snmpextend.values.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.NavigableMap;

public class CachingDataProvider implements DataProvider {

    private final static Logger LOG = LoggerFactory.getLogger(CachingDataProvider.class);

    private final static TemporalAmount CACHE_TTL = Duration.ofSeconds(5);

    private final DataProvider provider;

    private Instant cachedTime = Instant.MIN;
    private NavigableMap<ObjectId, Value> cachedData = null;

    public CachingDataProvider(final DataProvider provider) {
        this.provider = provider;
    }

    @Override
    public NavigableMap<ObjectId, Value> fetch() {
        final Instant now = Instant.now();

        if (now.isAfter(cachedTime.plus(CACHE_TTL))) {
            LOG.trace("Cache timed out - reloading...");

            this.cachedTime = now;
            this.cachedData = this.provider.fetch();
        }

        return this.cachedData;
    }
}
