package org.opennms.snmpextend.proto;

import org.opennms.snmpextend.values.Value;

import java.util.NavigableMap;

public interface DataProvider {

    NavigableMap<ObjectId, Value> fetch();
}
