package demo.batch.customer.entity;

import java.util.Date;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(OrderEntity.class)
public class OrderEntity_ {
    public static volatile SingularAttribute<OrderEntity, String> identifier;
    public static volatile SingularAttribute<OrderEntity, Date> date;
    public static volatile SingularAttribute<OrderEntity, CustomerEntity> customer;
    public static volatile SetAttribute<OrderEntity, OrderItemEntity> items;

}
