package project;

import javax.swing.table.AbstractTableModel;
import java.util.Arrays;

/**
 * Created by pilipenko on 30.10.2017.
 */
public class RowColumnTableModel extends AbstractTableModel {

    private int mRowCount;
    private int mColumnCount;
    private HeaderGenerator mColumnsName;
    private HeaderGenerator mRowsName;

    private double[][] values;

    public RowColumnTableModel(int rowCount, int columnCount, HeaderGenerator columnNames, HeaderGenerator rowNames) {
        this.mRowCount = rowCount;
        this.mColumnCount = columnCount;
        this.mColumnsName = columnNames;
        this.mRowsName = rowNames;
        values = new double[rowCount][columnCount];


        for (double[] row : values)
            Arrays.fill(row, -1.0);
    }

    public int getRowCount() {
        return mRowCount;
    }

    public int getColumnCount() {
        return mColumnCount + 1;
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "";
        }
        return mColumnsName.generate(column - 1, mColumnCount - 1);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return mRowsName.generate(rowIndex, mRowCount);
        }

        double value = values[rowIndex][columnIndex - 1];

        return value == -1 ? "" : format(value);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return false;
        }
        return false;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        values[row][col - 1] = Double.valueOf((String) value);
        fireTableCellUpdated(row, col);
    }

    public void setValues(double[][] values) {
        this.values = values;
        fireTableDataChanged();
    }

    public double[][] getValues() {
        return values;
    }

    private String format(double d) {
        if (d == (long) d) {
            return String.format("%d", (long) d);
        } else {
            String value = String.format("%s", ((int) (d * 1000)) / 1000.0);
            return value;
        }
    }

    public interface HeaderGenerator {
        String generate(int index, int size);
    }
}
