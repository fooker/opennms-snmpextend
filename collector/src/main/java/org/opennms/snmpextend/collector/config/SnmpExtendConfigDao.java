package org.opennms.snmpextend.collector.config;

import org.opennms.netmgt.rrd.RrdRepository;

/**
 * Dao interface for the SNMP-Extend config.
 */
public interface SnmpExtendConfigDao {
    /**
     * Returns the config.
     *
     * @return the config instance
     */
    SnmpExtendConfig getConfig();

    /**
     * Returns the Rrd repository for a given collection name.
     *
     * @return the repository
     */
    RrdRepository getRrdRepository();

    /**
     * Returns the base Rrd's path.
     *
     * @return the Rrd's path
     */
    String getRrdPath();
}
