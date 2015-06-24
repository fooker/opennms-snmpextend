package org.opennms.snmpextend.agent;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.opennms.snmpextend.agent.args.Config;
import org.opennms.snmpextend.agent.proto.Communicator;
import org.opennms.snmpextend.agent.snippets.SnippetsDataProvider;
import org.opennms.snmpextend.agent.proto.DataProvider;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

public class Main {

    public static void main(final String... args) {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Config.class).toInstance(new Config(args));

                bind(Reader.class).toInstance(new InputStreamReader(System.in));
                bind(Writer.class).toInstance(new OutputStreamWriter(System.out));

                bind(DataProvider.class).to(SnippetsDataProvider.class);
            }
        });

        final Communicator communicator = injector.getInstance(Communicator.class);
        communicator.run();
    }

}
