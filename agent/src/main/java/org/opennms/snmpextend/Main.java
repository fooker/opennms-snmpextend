package org.opennms.snmpextend;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import org.opennms.snmpextend.proto.Communicator;
import org.opennms.snmpextend.proto.DataProvider;
import org.opennms.snmpextend.snippets.CachingDataProvider;
import org.opennms.snmpextend.snippets.SnippetManager;
import org.opennms.snmpextend.snippets.SnippetsDataProvider;

import javax.inject.Inject;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Main extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Provides
    @Inject
    protected DataProvider dataProvider(final SnippetManager snippetManager) {
        DataProvider provider;
        provider = new SnippetsDataProvider(snippetManager);
        provider = new CachingDataProvider(provider);

        return provider;
    }

    @Provides
    @Inject
    protected Communicator communicator(final DataProvider dataProvider) {
        return new Communicator(dataProvider,
                                new InputStreamReader(System.in),
                                new OutputStreamWriter(System.out));
    }

    public static void main(final String... args) {
        final Injector injector = Guice.createInjector(new Main());

        final Communicator communicator = injector.getInstance(Communicator.class);
        communicator.run();
    }

}
