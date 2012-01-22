package demo.hello.shared;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

/**
 * FilterConstraintProxy
 */
@ProxyFor(value = FilterConstraint.class)
public interface FilterConstraintProxy extends ValueProxy {

    String getColumn();

    void setColumn(String column);

    String getValue();

    void setValue(String value);

    FilterConstraintTypeEnum getType();

    void setType(FilterConstraintTypeEnum type);

    FilterConstraintOpEnum getOp();

    void setOp(FilterConstraintOpEnum op);
}
