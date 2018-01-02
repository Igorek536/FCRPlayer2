package org.freecore.fcrplayer2.components;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Simple table model for JTable by Igorek536
 * @author Igorek536
 * @version 1.0
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class SimpleTableModel extends AbstractTableModel {

    private ArrayList<String> cols = new ArrayList<>();
    private ArrayList<String[]> rows = new ArrayList<>();

    private void update() {
        fireTableDataChanged();
    }

    public void addRow(String... row) {
        if (row.length == getColumnCount()) {
            Collections.addAll(rows, row);
            update();
        }
    }

    public int searchRowId(String col) {
        int result = -1;
        for (String[] s : rows) {
            for (String s1 : s) {
                if (col.equals(s1)) {
                    result = rows.indexOf(s);
                }
            }
        }
        return result;
    }

    public boolean isInData(String col) {
        return -1 != searchRowId(col);
    }

    public void changeRow(int id, String... newRow) {
        if (newRow.length == getColumnCount()) {
            rows.set(id, newRow);
            update();
        }
    }

    public void changeCell(int rowId, int colIndex, String cell) {
        if (rowId <= rows.size() && colIndex <= cols.size()) {
            String[] strArr = rows.get(rowId);
            strArr[colIndex] = cell;
            rows.set(rowId, strArr);
        }
    }

    public void removeRow(int id) {
        rows.remove(id);
        update();
    }

    public void setCols(String... colNames) {
        cols.addAll(Arrays.asList(colNames));
        update();
    }

    // Implementation

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return cols.size();
    }

    @Override
    public String getColumnName(int colIndex) {
        return cols.get(colIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int colIndex) {
        String[] result = rows.get(rowIndex);
        return result[colIndex];
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        fireTableCellUpdated(row, col);
    }
}
