package demo.axon.customer.query;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(CustomerEntity.class)
public class CustomerEntity_ {
    public static volatile SingularAttribute<CustomerEntity, String> identifier;
    public static volatile SingularAttribute<CustomerEntity, String> name;
}
