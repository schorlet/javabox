package demo.hello.shared;

/**
 * SortConstraint
 */
public class SortConstraint {
    String column;
    boolean ascending;

    public SortConstraint() {}

    public SortConstraint(final String column, final boolean ascending) {
        this.column = column;
        this.ascending = ascending;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(final String column) {
        this.column = column;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(final boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("SortConstraint [column=").append(column).append(", ascending=")
            .append(ascending).append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (ascending ? 1231 : 1237);
        result = prime * result + (column == null ? 0 : column.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final SortConstraint other = (SortConstraint) obj;
        if (ascending != other.ascending) return false;
        if (column == null) {
            if (other.column != null) return false;
        } else if (!column.equals(other.column)) return false;
        return true;
    }

}
