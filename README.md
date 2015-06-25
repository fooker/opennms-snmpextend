# opennms-snmpextend
Simple SNMP extension for OpenNMS

## Usage
Build using maven
```
mvn clean package
```
and add the following line to `snmpd.conf`
```
pass_persist 1.3.6.1.4.1.5813.2 /usr/bin/java -jar /path/to/your/source/opennms-snmpextend/agent/target/org.opennms.snmpextend-1.0-SNAPSHOT-jar-with-dependencies.jar
```
After restarting the SNMP daemon, the data can be queried.

The following command can be used to test:
```
snmpwalk ... 127.0.0.1 .1.3.6.1.4.1.5813.2
```

To collect the data, the collector from `/path/to/your/source/opennms-snmpextend/collector/target/org.opennms.snmpextend.collector-1.0-SNAPSHOT.jar` must be dropped in to the OpenNMS `lib` folder.
This can be done by linking or copying.

To configure the data collection copy `/path/to/your/source/opennms-snmpextend/resources/etc/snmp-extend-config.xml` to OpenNMS `etc` directory.

In addition, the following definitions must be added to the OpenNMS `etc/collectd-configuration.xml` file:
```
<collectd-configuration ...>
  <package name="example1">
    ...

    <service name="SNMPext" interval="5000" user-defined="false" status="on">
      <parameter key="collection" value="default"/>
      <parameter key="thresholding-enabled" value="true"/>
    </service>
  </package>

  ...

  <collector service="SNMPext" class-name="org.opennms.snmpextend.collector.SnmpExtendCollector"/>

  ...
</collectd-configuration>
```

And restart OpenNMS.

## Snippets
All snippets must be placed in `/etc/smnp/opennms` and have a extension matching the scripting language used by the script.

Changes to snippets are cached up immediately by the agent.
Beside that, the results of a script are cached to avoid multiple executions.

Each script can add its results to the `resources` object injected in the execution context.
See the documentation of the of the `org.opennms.snmpextend.agent.snippets.Result.Builder` class for available methods.

Example `test.groovy` script:
```
results.addInteger("foo", 23);
results.addInteger("bar", 42);
```

Will lead to the following output:
```
SNMPv2-SMI::enterprises.5813.2.1.23430 = STRING: "testBar"
SNMPv2-SMI::enterprises.5813.2.1.93397 = STRING: "testFoo"
SNMPv2-SMI::enterprises.5813.2.2.23430 = INTEGER: 42
SNMPv2-SMI::enterprises.5813.2.2.93397 = INTEGER: 23
```

Example snippets can be found in `/path/to/your/source/opennms-snmpextend/resources/snippets`.
