package me.replydev.qubo.gui;

import javax.swing.table.DefaultTableModel;

class MyTableModel extends DefaultTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public MyTableModel() {
        addColumn("N");
        addColumn("Ip");
        addColumn("Port");
        addColumn("Players");
        addColumn("Version");
        addColumn("MOTD");

    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
