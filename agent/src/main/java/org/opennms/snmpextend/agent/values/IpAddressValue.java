package org.opennms.snmpextend.agent.values;

import java.net.InetAddress;

/**
 * A IP address value.
 */
public class IpAddressValue extends Value {
    /**
     * The value.
     */
    private final InetAddress value;

    /**
     * Create a new IP address value.
     *
     * @param value the value
     */
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
