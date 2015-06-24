package org.opennms.snmpextend.collector.config;

import org.opennms.core.xml.AbstractJaxbConfigDao;
import org.opennms.netmgt.rrd.RrdRepository;
import org.springframework.core.io.FileSystemResource;

import java.io.File;

/**
 * Dao implementation for Jaxb.
 */
public class SnmpExtendConfigDaoJaxb extends AbstractJaxbConfigDao<SnmpExtendConfig, SnmpExtendConfig> implements SnmpExtendConfigDao {
    /**
     * Config file location
     */
    public static final String CONFIG_FILE_LOCATION = "etc/snmp-extend-config.xml";

    /**
     * Default constructor
     */
    public SnmpExtendConfigDaoJaxb() {
        super(SnmpExtendConfig.class, "SNMP-Extend Data Collection Configuration");
    }

    /**
     * Helper method to instantiate objects of this class.
     *
     * @return a Dao instance
     */
    public static SnmpExtendConfigDao getInstance() {
        final SnmpExtendConfigDaoJaxb snmpExtendConfigDaoJaxb = new SnmpExtendConfigDaoJaxb();
        snmpExtendConfigDaoJaxb.setConfigResource(new FileSystemResource(CONFIG_FILE_LOCATION));
        snmpExtendConfigDaoJaxb.afterPropertiesSet();
        return snmpExtendConfigDaoJaxb;
    }

    /**
     * Returns the configuration instance.
     *
     * @return the configuration object
     */
    @Override
    public SnmpExtendConfig getConfig() {
        return getContainer().getObject();
    }

    @Override
    public SnmpExtendConfig translateConfig(SnmpExtendConfig jaxbConfig) {
        return jaxbConfig;
    }

    /**
     * Returns the RrdRepository instance defined in the config.
     *
     * @return the RrdRepository instance
     */
    @Override
    public RrdRepository getRrdRepository() {
        RrdRepository rrdRepository = new RrdRepository();
        rrdRepository.setRrdBaseDir(new File(getRrdPath()));
        rrdRepository.setRraList(getConfig().getRrd().getRraList());
        rrdRepository.setStep(getConfig().getRrd().getStep());
        rrdRepository.setHeartBeat((2 * getConfig().getRrd().getStep()));
        return rrdRepository;
    }

    /**
     * Returns the rrd path defined in the configuration.
     *
     * @return the path
     */
    @Override
    public String getRrdPath() {
        String rrdPath = getConfig().getRrdRepository();

        if (rrdPath == null) {
            throw new RuntimeException("Configuration error, failed to retrieve path to RRD repository.");
        }

        if (rrdPath.endsWith(File.separator)) {
            rrdPath = rrdPath.substring(0, (rrdPath.length() - File.separator.length()));
        }

        return rrdPath;
    }
}
