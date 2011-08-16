package demo.batch.customer.entity;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ProductEntity.class)
public class ProductEntity_ {
    public static volatile SingularAttribute<ProductEntity, String> identifier;
    public static volatile SingularAttribute<ProductEntity, String> name;

}
