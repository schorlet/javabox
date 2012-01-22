package demo.hello.client.cell;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.user.cellview.client.Column;

public abstract class CellColumn<T, C> extends Column<T, C> {

    final String columnName;

    public CellColumn(final String columnName, final Cell<C> cell) {
        super(cell);
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }
}
