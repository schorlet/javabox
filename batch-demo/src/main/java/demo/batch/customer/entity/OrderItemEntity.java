package demo.batch.customer.entity;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Embeddable
public class OrderItemEntity {
    @Basic(optional = false)
    protected Integer quantity;

    @ManyToOne(optional = false)
    protected ProductEntity product;

    /**
     * @return the quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(final Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the product
     */
    public ProductEntity getProduct() {
        return product;
    }

    /**
     * @param product the product to set
     */
    public void setProduct(final ProductEntity product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
