package org.opennms.snmpextend.collector.config;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "rrd")
@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings("all")
public class Rrd implements Serializable {

    @XmlAttribute(name = "step")
    private Integer m_step = null;

    @XmlElement(name = "rra")
    private List<String> m_rraList = new java.util.ArrayList<String>();

    public Rrd() {
        super();
    }

    public Integer getStep() {
        return m_step;
    }

    public void setStep(Integer m_step) {
        this.m_step = m_step;
    }

    public List<String> getRraList() {
        return m_rraList;
    }

    public void setRraList(List<String> m_rraList) {
        this.m_rraList = m_rraList;
    }
}
