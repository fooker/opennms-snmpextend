package org.opennms.snmpextend.agent.values;

/**
 * Base for all value types.
 */
public abstract class Value {

    /**
     * Returns the value as a {@link String}.
     *
     * @return the serialized value
     */
    public abstract String getValue();

    /**
     * Returns the type name.
     * The type name must be a type specific string identiying the type.
     *
     * @return the type name
     */
    public abstract String getType();

    @Override
    public final String toString() {
        return this.getType() + "(" + this.getValue() + ")";
    }
}
