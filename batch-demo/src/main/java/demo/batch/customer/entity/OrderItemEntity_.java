package demo.batch.customer.entity;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(OrderItemEntity.class)
public class OrderItemEntity_ {
    public static volatile SingularAttribute<OrderItemEntity, Integer> quantity;
    public static volatile SingularAttribute<OrderItemEntity, ProductEntity> product;

}
