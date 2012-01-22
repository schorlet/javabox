package demo.hello.shared.plumbing;

import demo.hello.shared.SuperEntity;

/**
 * ProjectEntityFinder.
 * 
 * Used by ProjectDomainLocator in order to find or create Domain Entity.
 */
public interface ProjectEntityFinder {

    SuperEntity find(Class<? extends SuperEntity> domainClass, Integer id);

    SuperEntity create(Class<? extends SuperEntity> domainClass);

}
