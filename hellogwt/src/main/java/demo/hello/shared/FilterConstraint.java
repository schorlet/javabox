package demo.hello.shared;

import java.util.Date;

/**
 * FilterConstraint
 */
public class FilterConstraint {

    String column;
    String value;
    FilterConstraintTypeEnum type;
    FilterConstraintOpEnum op;

    public FilterConstraint() {}

    public FilterConstraint(final String column, final String value,
        final FilterConstraintTypeEnum type) {
        this.column = column;
        this.value = value;
        this.type = type;
        this.op = FilterConstraintOpEnum.EQ;
    }

    public FilterConstraint(final String column, final String value,
        final FilterConstraintTypeEnum type, final FilterConstraintOpEnum op) {
        this.column = column;
        this.value = value;
        this.type = type;
        this.op = op;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(final String column) {
        this.column = column;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public FilterConstraintTypeEnum getType() {
        return type;
    }

    public void setType(final FilterConstraintTypeEnum type) {
        this.type = type;
    }

    @SuppressWarnings("rawtypes")
    public Comparable getTypedValue() {
        Comparable o = value;

        if (FilterConstraintTypeEnum.FLOAT == type) {
            o = Float.valueOf(value);

        } else if (FilterConstraintTypeEnum.BOOLEAN == type) {
            o = Boolean.valueOf(value);

        } else if (FilterConstraintTypeEnum.DATE == type) {
            o = new Date(Long.valueOf(value));
        }

        return o;
    }

    @SuppressWarnings("rawtypes")
    public Class<? extends Comparable> getJavaType() {
        Class<? extends Comparable> javaClass = String.class;

        if (FilterConstraintTypeEnum.FLOAT == type) {
            javaClass = Float.class;

        } else if (FilterConstraintTypeEnum.BOOLEAN == type) {
            javaClass = Boolean.class;

        } else if (FilterConstraintTypeEnum.DATE == type) {
            javaClass = Date.class;
        }

        return javaClass;
    }

    public FilterConstraintOpEnum getOp() {
        return op;
    }

    public void setOp(final FilterConstraintOpEnum op) {
        this.op = op;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("FilterConstraint [column=").append(getColumn()).append(", op=")
            .append(getOp()).append(", value=").append(getValue()).append(", type=")
            .append(getType()).append("], ");
        return builder.toString();
    }
}
