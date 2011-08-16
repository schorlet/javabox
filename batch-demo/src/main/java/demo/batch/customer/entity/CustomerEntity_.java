package demo.batch.customer.entity;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(CustomerEntity.class)
public class CustomerEntity_ {
    public static volatile SingularAttribute<CustomerEntity, String> identifier;
    public static volatile SingularAttribute<CustomerEntity, String> name;
    public static volatile SetAttribute<CustomerEntity, OrderEntity> orders;

}
