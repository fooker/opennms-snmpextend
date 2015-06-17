package org.opennms.snmpextend.values;

import java.net.InetAddress;

public class IpAddressValue extends Value {

    private final InetAddress value;

    public IpAddressValue(final InetAddress value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value.getHostAddress();
    }

    @Override
    public String getType() {
        return "IPADDRESS";
    }
}
