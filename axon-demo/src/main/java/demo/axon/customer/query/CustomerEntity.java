package demo.axon.customer.query;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Entity
public class CustomerEntity {
    @Id
    protected String identifier;

    @Column(nullable = false, unique = true)
    protected String name;

    public UUID getIdentifier() {
        return UUID.fromString(identifier);
    }

    public void setIdentifier(UUID identifier) {
        this.identifier = identifier.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
