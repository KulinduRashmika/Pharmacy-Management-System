package com.pss.view;

import com.pss.util.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class LowStockForm extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public LowStockForm() {

        setLayout(new BorderLayout(15,15));
        setBackground(new Color(240,240,240));
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        // ================= HEADER =================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JLabel lblTitle = new JLabel("⚠ Low Stock Products ", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(200, 0, 0));

        headerPanel.add(lblTitle, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // ================= TABLE =================
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Product Name", "Stock Quantity"});

        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(220,220,220));

        // Center stock column
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        // Color low stock rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value,
                                                           boolean isSelected,
                                                           boolean hasFocus,
                                                           int row,
                                                           int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                int qty = Integer.parseInt(table.getValueAt(row, 1).toString());

                if (qty == 0) {
                    c.setBackground(new Color(255, 150, 150)); // Red
                } else if (qty <= 5) {
                    c.setBackground(new Color(255, 200, 120)); // Orange
                } else {
                    c.setBackground(Color.WHITE);
                }

                if (isSelected) {
                    c.setBackground(new Color(180, 200, 240));
                }

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);

        // ================= FOOTER =================
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.setBackground(new Color(0,120,215));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setPreferredSize(new Dimension(120,40));
        btnRefresh.addActionListener(e -> loadLowStock());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(240,240,240));
        bottomPanel.add(btnRefresh);

        add(bottomPanel, BorderLayout.SOUTH);

        loadLowStock();
    }

    // ================= DATABASE LOGIC =================
    private void loadLowStock() {

        model.setRowCount(0);

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement pst = con.prepareStatement(
                    "SELECT name, quantity FROM products WHERE quantity <= 10"
            );

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("name"),
                        rs.getInt("quantity")
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                        "No low stock products found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}