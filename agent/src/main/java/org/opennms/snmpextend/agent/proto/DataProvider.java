package org.opennms.snmpextend.agent.proto;

import org.opennms.snmpextend.agent.values.Value;

import java.util.NavigableMap;

public interface DataProvider {

    NavigableMap<ObjectId, Value> fetch();
}
