package org.opennms.snmpextend.agent.snippets;

import java.nio.file.Path;

/**
 * Exception for everything snippet related.
 */
public class SnippetException extends Exception {
    private final Path path;

    public SnippetException(final Path path,
                            final String message) {
        super(message);

        this.path = path;
    }

    public SnippetException(final Path path,
                            final String message,
                            final Throwable cause) {
        super(message,
              cause);

        this.path = path;
    }
}
