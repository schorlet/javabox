package demo.hello.shared.plumbing;

import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Locator;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

import demo.hello.shared.SuperEntity;

/**
 * ProjectDomainLocator.
 * 
 * A Locator allows entity types that do not conform to the RequestFactory
 * entity protocol to be used. Instead of attempting to use a {@code findFoo()},
 * {@code getId()}, and {@code getVersion()} declared in the domain entity type,
 * an instance of a Locator will be created to provide implementations of these
 * methods.
 * <p>
 * Locator subtypes must be default instantiable (i.e. public static types with
 * a no-arg constructor). Instances of Locators may be retained and reused by
 * the RequestFactory service layer.
 * 
 * @param <T> the type of domain object the Locator will operate on
 * @param <I> the type of object the Locator expects to use as an id for the
 *          domain object
 * @see ProxyFor#locator()
 */
public class ProjectDomainLocator extends Locator<SuperEntity, Integer> {

    ProjectEntityFinder entityFinder;

    @Inject
    ProjectDomainLocator(final ProjectEntityFinder entityFinder) {
        this.entityFinder = entityFinder;
    }

    @Override
    public SuperEntity create(final Class<? extends SuperEntity> domainClass) {
        return entityFinder.create(domainClass);
    }

    @Override
    public SuperEntity find(final Class<? extends SuperEntity> domainClass, final Integer id) {
        return entityFinder.find(domainClass, id);
    }

    @Override
    public Class<SuperEntity> getDomainType() {
        return SuperEntity.class;
    }

    @Override
    public Integer getId(final SuperEntity domainObject) {
        return domainObject.getId();
    }

    @Override
    public Class<Integer> getIdType() {
        return Integer.class;
    }

    @Override
    public Object getVersion(final SuperEntity domainObject) {
        return domainObject.getVersion();
    }

}
