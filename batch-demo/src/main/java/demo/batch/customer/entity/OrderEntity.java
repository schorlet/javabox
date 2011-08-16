package demo.batch.customer.entity;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Entity
public class OrderEntity {
    @Id
    protected String identifier;

    @Basic(optional = false)
    protected Date date;

    @ManyToOne(optional = false)
    protected CustomerEntity customer;

    @Embedded
    @ElementCollection(fetch = FetchType.EAGER)
    @OrderBy("quantity asc")
    protected Set<OrderItemEntity> items;

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
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(final Date date) {
        this.date = date;
    }

    /**
     * @return the customer
     */
    public CustomerEntity getCustomer() {
        return customer;
    }

    /**
     * @param customer the customer to set
     */
    public void setCustomer(final CustomerEntity customer) {
        this.customer = customer;
    }

    /**
     * @return the items
     */
    public Set<OrderItemEntity> getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(final Set<OrderItemEntity> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
