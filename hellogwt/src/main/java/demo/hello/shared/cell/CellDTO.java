package demo.hello.shared.cell;

import java.util.Date;

/**
 * CellDTO
 */
public class CellDTO {
    String a;
    Double b;
    Boolean c;
    Date d;

    public CellDTO(final String a, final Double b, final Boolean c, final Date d) {
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

    public String getSummary() {
        return getA() + "  " + getB() + "  " + getC() + "  " + getD();
    }

}
