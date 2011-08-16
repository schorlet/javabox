package demo.batch.customer;

import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.types.path.EntityPathBase;

import demo.batch.customer.entity.ProductEntity;
import demo.batch.customer.entity.QProductEntity;

@Repository
public class ProductRepository extends BaseRepository<ProductEntity, String> {

    static final QProductEntity qProductEntity = QProductEntity.productEntity;

    @Override
    protected Class<ProductEntity> getPersistentClass() {
        return ProductEntity.class;
    }

    @Override
    protected EntityPathBase<ProductEntity> getEntityPathBase() {
        return qProductEntity;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void create(final UUID identifier, final String name) {
        final ProductEntity entity = new ProductEntity();
        entity.setIdentifier(identifier);
        entity.setName(name);
        super.persist(entity);
    }

}
