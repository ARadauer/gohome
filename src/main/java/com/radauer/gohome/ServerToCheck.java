package com.radauer.gohome;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.Date;

import static com.radauer.gohome.GoHome.downIcon;
import static com.radauer.gohome.GoHome.upIcon;

/**
 * Created by Andreas on 12.12.2017.
 */
public class ServerToCheck extends AbstractTableModel {

    private String url;
    private String name;
    private JMenuItem menuItem;

    private LimitedSizeQueue<MeasurePoint> measurePoints = new LimitedSizeQueue(100);


    public ServerToCheck(String url, String name, JMenuItem menuItem) {
        this.url = url;
        this.name = name;
        this.menuItem = menuItem;
    }

    public void addMessurePoint(boolean up, int durration) {
        measurePoints.add(new MeasurePoint(new Date(), up, durration));
        menuItem.setIcon(up ? upIcon : downIcon);
        fireTableDataChanged();
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public LimitedSizeQueue<MeasurePoint> getMeasurePoints() {
        return measurePoints;
    }


    String[] columns = {"time", "status", "duration"};

    @Override
    public int getRowCount() {
        return measurePoints.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columns[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        MeasurePoint point = measurePoints.get(measurePoints.size() - rowIndex - 1);
        switch (columnIndex) {
            case 0:
                return point.getTime();
            case 1:
                return point.isUp();
            case 2:
                return point.getDuration();
        }
        return null;
    }
}

