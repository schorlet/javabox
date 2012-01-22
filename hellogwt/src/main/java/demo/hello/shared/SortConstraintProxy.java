package demo.hello.shared;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

/**
 * SortConstraintProxy
 */
@ProxyFor(value = SortConstraint.class)
public interface SortConstraintProxy extends ValueProxy {

    String getColumn();

    void setColumn(final String column);

    boolean isAscending();

    void setAscending(final boolean ascending);
}
