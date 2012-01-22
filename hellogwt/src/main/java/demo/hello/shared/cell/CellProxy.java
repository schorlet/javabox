package demo.hello.shared.cell;

import java.util.Date;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

import demo.hello.shared.plumbing.ProjectDomainLocator;

/**
 * CellProxy
 */
@ProxyFor(value = CellEntity.class, locator = ProjectDomainLocator.class)
public interface CellProxy extends EntityProxy {

    Integer getId();

    String getA();

    void setA(final String a);

    Double getB();

    void setB(final Double b);

    Boolean getC();

    void setC(final Boolean c);

    Date getD();

    void setD(final Date d);
}
