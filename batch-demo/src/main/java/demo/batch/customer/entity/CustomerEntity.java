package demo.batch.customer.entity;

import java.util.Set;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Entity
public class CustomerEntity {
    @Id
    protected String identifier;

    @Basic(optional = false)
    @Column(length = 20)
    protected String name;

    @OneToMany(mappedBy = "customer", cascade = { CascadeType.REMOVE })
    @OrderBy("date asc")
    protected Set<OrderEntity> orders;

    /**
     * @return the identifier
     */
    public UUID getIdentifier() {
        return UUID.fromString(identifier);
    }

    /**
     * @param identifier
     */
    public void setIdentifier(final UUID identifier) {
        this.identifier = identifier.toString();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the orders
     */
    public Set<OrderEntity> getOrders() {
        return orders;
    }

    /**
     * @param orders the orders to set
     */
    public void setOrders(final Set<OrderEntity> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
