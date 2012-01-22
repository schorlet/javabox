package demo.hello.shared.cell;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import demo.hello.shared.SuperEntity;

/**
 * CellEntity
 */
@Entity
public class CellEntity extends SuperEntity {

    @Basic(optional = false)
    @Column(unique = true)
    @NotNull
    String a;

    @NotNull
    Double b;

    @NotNull
    Boolean c;

    @NotNull
    Date d;

    public CellEntity() {}

    public CellEntity(final String a, final Double b, final Boolean c, final Date d) {
        super();
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public String getA() {
        return a;
    }

    public void setA(final String a) {
        this.a = a;
    }

    public Double getB() {
        return b;
    }

    public void setB(final Double b) {
        this.b = b;
    }

    public Boolean getC() {
        return c;
    }

    public void setC(final Boolean c) {
        this.c = c;
    }

    public Date getD() {
        return d;
    }

    public void setD(final Date d) {
        this.d = d;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("CellEntity [a=").append(a).append(", b=").append(b).append(", c=")
            .append(c).append(", d=").append(d).append(", identifier=").append(identifier)
            .append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (a == null ? 0 : a.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final CellEntity other = (CellEntity) obj;
        if (a == null) {
            if (other.a != null) return false;
        } else if (!a.equals(other.a)) return false;
        return true;
    }

}
