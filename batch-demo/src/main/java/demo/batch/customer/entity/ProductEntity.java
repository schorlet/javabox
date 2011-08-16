package demo.batch.customer.entity;

import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Entity
public class ProductEntity {
    @Id
    protected String identifier;

    @Basic(optional = false)
    @Column(length = 20)
    protected String name;

    /**
     * @return the identifier
     */
    public UUID getIdentifier() {
        return UUID.fromString(identifier);
    }

    /**
     * @param identifier the identifier to set
     */
    public void setIdentifier(final UUID identifier) {
        this.identifier = identifier.toString();
    }

    /**
     * @return the name
     */
    public String getname() {
        return name;
    }

    /**
     * 
     * @param name
     */
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
