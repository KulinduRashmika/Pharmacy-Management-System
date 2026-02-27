package com.pss.view;

import com.pss.util.DBConnection;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MedicineForm extends JPanel {

    private JTextField txtId, txtName, txtCategory, txtPrice, txtQuantity, txtExpiry;
    private JTable table;
    private DefaultTableModel model;

    public MedicineForm() {

        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        // ================= LEFT PANEL (FORM)
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(320, 600));
        leftPanel.setLayout(null);
        leftPanel.setBorder(new TitledBorder("Product Details"));

        JLabel lblName = new JLabel("Product Name");
        lblName.setBounds(30, 40, 250, 25);
        leftPanel.add(lblName);

        txtName = new JTextField();
        txtName.setBounds(30, 65, 250, 30);
        leftPanel.add(txtName);

        JLabel lblCategory = new JLabel("Category");
        lblCategory.setBounds(30, 105, 250, 25);
        leftPanel.add(lblCategory);

        txtCategory = new JTextField();
        txtCategory.setBounds(30, 130, 250, 30);
        leftPanel.add(txtCategory);

        JLabel lblPrice = new JLabel("Price");
        lblPrice.setBounds(30, 170, 250, 25);
        leftPanel.add(lblPrice);

        txtPrice = new JTextField();
        txtPrice.setBounds(30, 195, 250, 30);
        leftPanel.add(txtPrice);

        JLabel lblQty = new JLabel("Stock Quantity");
        lblQty.setBounds(30, 235, 250, 25);
        leftPanel.add(lblQty);

        txtQuantity = new JTextField();
        txtQuantity.setBounds(30, 260, 250, 30);
        leftPanel.add(txtQuantity);

        JLabel lblExpiry = new JLabel("Expiry Date (YYYY-MM-DD)");
        lblExpiry.setBounds(30, 300, 250, 25);
        leftPanel.add(lblExpiry);

        txtExpiry = new JTextField();
        txtExpiry.setBounds(30, 325, 250, 30);
        leftPanel.add(txtExpiry);

        // Hidden ID
        txtId = new JTextField();
        txtId.setVisible(false);

        // ===== Buttons =====
        JButton btnAdd = new JButton("Add");
        btnAdd.setBounds(30, 380, 250, 35);
        btnAdd.setBackground(new Color(34, 167, 240));
        btnAdd.setForeground(Color.WHITE);
        leftPanel.add(btnAdd);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(30, 425, 250, 35);
        btnUpdate.setBackground(new Color(243, 156, 18));
        btnUpdate.setForeground(Color.WHITE);
        leftPanel.add(btnUpdate);

        JButton btnDelete = new JButton("Delete");
        btnDelete.setBounds(30, 470, 250, 35);
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        leftPanel.add(btnDelete);

        JButton btnClear = new JButton("Clear");
        btnClear.setBounds(30, 515, 250, 35);
        btnClear.setBackground(new Color(149, 165, 166));
        btnClear.setForeground(Color.WHITE);
        leftPanel.add(btnClear);

        // ================= RIGHT PANEL (TABLE)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(new TitledBorder("Products List"));

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
                "ID", "Name", "Category", "Price", "Stock", "Expiry"
        });

        table = new JTable(model);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // ================= ADD PANELS
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // ================= LOAD DATA
        loadProducts();

        // ================= BUTTON ACTIONS
        btnAdd.addActionListener(e -> saveProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnClear.addActionListener(e -> clearFields());

        // ================= TABLE CLICK EVENT
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                txtId.setText(model.getValueAt(row, 0).toString());
                txtName.setText(model.getValueAt(row, 1).toString());
                txtCategory.setText(model.getValueAt(row, 2).toString());
                txtPrice.setText(model.getValueAt(row, 3).toString());
                txtQuantity.setText(model.getValueAt(row, 4).toString());
                txtExpiry.setText(model.getValueAt(row, 5).toString());
            }
        });
    }

    // ================= LOAD PRODUCTS
    private void loadProducts() {
        try (Connection con = DBConnection.getConnection()) {

            String sql = "SELECT * FROM products";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getDate("expiry_date")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= SAVE
    private void saveProduct() {
        try (Connection con = DBConnection.getConnection()) {

            String sql = "INSERT INTO products (name, category, price, quantity, expiry_date) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, txtName.getText());
            pst.setString(2, txtCategory.getText());
            pst.setDouble(3, Double.parseDouble(txtPrice.getText()));
            pst.setInt(4, Integer.parseInt(txtQuantity.getText()));
            pst.setDate(5, Date.valueOf(txtExpiry.getText()));

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Product Added Successfully!");
            loadProducts();
            clearFields();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving product!");
        }
    }

    // ================= UPDATE
    private void updateProduct() {
        try (Connection con = DBConnection.getConnection()) {

            String sql = "UPDATE products SET name=?, category=?, price=?, quantity=?, expiry_date=? WHERE id=?";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, txtName.getText());
            pst.setString(2, txtCategory.getText());
            pst.setDouble(3, Double.parseDouble(txtPrice.getText()));
            pst.setInt(4, Integer.parseInt(txtQuantity.getText()));
            pst.setDate(5, Date.valueOf(txtExpiry.getText()));
            pst.setInt(6, Integer.parseInt(txtId.getText()));

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Product Updated Successfully!");
            loadProducts();
            clearFields();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating product!");
        }
    }

    // ================= DELETE
    private void deleteProduct() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a product first!");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            String sql = "DELETE FROM products WHERE id=?";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, Integer.parseInt(txtId.getText()));
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Product Deleted Successfully!");
            loadProducts();
            clearFields();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting product!");
        }
    }

    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtCategory.setText("");
        txtPrice.setText("");
        txtQuantity.setText("");
        txtExpiry.setText("");
    }
}