package org.opennms.snmpextend.collector.config;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

@XmlRootElement(name = "snmp-extend-config")
@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings("all")
public class SnmpExtendConfig implements Serializable {

    @XmlAttribute(name = "rrdRepository")
    private String m_rrdRepository;

    @XmlElement(name = "rrd")
    private Rrd m_rrd;

    public SnmpExtendConfig() {
        super();
    }

    public String getRrdRepository() {
        return m_rrdRepository;
    }

    public void setRrdRepository(String m_rrdRepository) {
        this.m_rrdRepository = m_rrdRepository;
    }

    public Rrd getRrd() {
        return m_rrd;
    }

    public void setRrd(Rrd m_rrd) {
        this.m_rrd = m_rrd;
    }
}
