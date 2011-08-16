package demo.batch.customer;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.path.EntityPathBase;

import demo.batch.customer.entity.OrderEntity;
import demo.batch.customer.entity.QOrderEntity;

@Repository
public class OrderRepository extends BaseRepository<OrderEntity, String> {

    static final QOrderEntity qOrderEntity = QOrderEntity.orderEntity;

    @Override
    protected Class<OrderEntity> getPersistentClass() {
        return OrderEntity.class;
    }

    @Override
    protected EntityPathBase<OrderEntity> getEntityPathBase() {
        return qOrderEntity;
    }

    public List<OrderEntity> findByCustomer(final String cutomerId) {
        return new JPAQuery(entityManager).from(qOrderEntity)
            .where(qOrderEntity.customer.identifier.eq(cutomerId)).list(qOrderEntity);
    }

}
