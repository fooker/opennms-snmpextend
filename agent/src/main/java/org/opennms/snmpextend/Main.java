package org.opennms.snmpextend;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import org.opennms.snmpextend.args.Config;
import org.opennms.snmpextend.proto.Communicator;
import org.opennms.snmpextend.proto.DataProvider;
import org.opennms.snmpextend.snippets.SnippetManager;
import org.opennms.snmpextend.snippets.SnippetsDataProvider;

import javax.inject.Inject;
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
