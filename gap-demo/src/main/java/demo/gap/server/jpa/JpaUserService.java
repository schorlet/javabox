package demo.gap.server.jpa;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

import demo.gap.shared.domain.pojo.User;
import demo.gap.shared.domain.service.UserService;

/**
 * JpaUserService
 */
public class JpaUserService implements UserService {
    final Logger logger = LoggerFactory.getLogger(JpaUserService.class);

    final Provider<EntityManager> entityManager;

    @Inject
    JpaUserService(final Provider<EntityManager> provider) {
        this.entityManager = provider;
    }

    @Override
    public boolean isEmpty() {
        logger.trace("isEmpty");

        final Long count = entityManager.get()
            .createQuery("select count(*) from UserEntity", Long.class).getSingleResult();

        return count == 0;
    }

    @Override
    public Set<User> getUsers() {
        logger.trace("getUsers");

        final TypedQuery<UserEntity> createQuery = entityManager.get().createQuery(
            "from UserEntity", UserEntity.class);

        final List<UserEntity> users = createQuery.getResultList();
        return JpaDomainUtil.buildUsers(users);
    }

    @Override
    @Transactional
    public void clear() {
        logger.trace("clear");
        entityManager.get().createQuery("delete from UserEntity").executeUpdate();
        entityManager.get().clear();
    }

    @Override
    @Transactional
    public void addAll(final Set<User> users) {
        logger.trace("addAll {}", users);

        for (final User user : users) {
            final UserEntity entity = JpaDomainUtil.buildUserEntity(user, entityManager.get());
            entityManager.get().persist(entity);
        }
    }
}
