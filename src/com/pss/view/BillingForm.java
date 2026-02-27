package com.pss.view;

import com.pss.util.DBConnection;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class BillingForm extends JPanel {

    private JComboBox<String> cmbProducts;
    private JTextField txtPrice, txtQty, txtTotal;
    private JTable table;
    private DefaultTableModel model;

    private int selectedProductId = 0;

    public BillingForm() {
        setLayout(null);
        setBounds(0, 0, 1000, 650);
        setBackground(new Color(240,240,240));

        initComponents();
        loadProducts();
    }

    private void initComponents() {

        // =========================
        // LEFT PANEL 
        // =========================
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(null);
        leftPanel.setBounds(30, 30, 350, 520);
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(new TitledBorder("Sale Details"));
        add(leftPanel);

        JLabel lblProduct = new JLabel("Product");
        lblProduct.setBounds(30, 50, 100, 25);
        leftPanel.add(lblProduct);

        cmbProducts = new JComboBox<>();
        cmbProducts.setBounds(30, 75, 280, 35);
        leftPanel.add(cmbProducts);

        JLabel lblPrice = new JLabel("Price");
        lblPrice.setBounds(30, 130, 100, 25);
        leftPanel.add(lblPrice);

        txtPrice = new JTextField();
        txtPrice.setBounds(30, 155, 280, 35);
        txtPrice.setEditable(false);
        leftPanel.add(txtPrice);

        JLabel lblQty = new JLabel("Quantity");
        lblQty.setBounds(30, 210, 100, 25);
        leftPanel.add(lblQty);

        txtQty = new JTextField();
        txtQty.setBounds(30, 235, 280, 35);
        leftPanel.add(txtQty);

        JButton btnAdd = new JButton("Add To Cart");
        btnAdd.setBounds(30, 300, 280, 40);
        btnAdd.setBackground(new Color(0,120,215));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        leftPanel.add(btnAdd);

        JLabel lblTotal = new JLabel("Total Amount");
        lblTotal.setBounds(30, 360, 150, 25);
        leftPanel.add(lblTotal);

        txtTotal = new JTextField("0.00");
        txtTotal.setBounds(30, 385, 280, 45);
        txtTotal.setEditable(false);
        txtTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtTotal.setHorizontalAlignment(JTextField.RIGHT);
        leftPanel.add(txtTotal);

        JButton btnComplete = new JButton("Complete Sale");
        btnComplete.setBounds(30, 450, 280, 45);
        btnComplete.setBackground(new Color(0,153,76));
        btnComplete.setForeground(Color.WHITE);
        btnComplete.setFocusPainted(false);
        leftPanel.add(btnComplete);


        // =========================
        // RIGHT PANEL 
        // =========================
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(null);
        rightPanel.setBounds(410, 30, 550, 520);
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new TitledBorder("Cart Items"));
        add(rightPanel);

        model = new DefaultTableModel(
                new String[]{"Product", "Price", "Qty", "Subtotal"}, 0);

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 40, 510, 450);
        rightPanel.add(scroll);


        // =========================
        // EVENTS 
        // =========================
        cmbProducts.addActionListener(e -> loadPrice());
        btnAdd.addActionListener(e -> addToCart());
        btnComplete.addActionListener(e -> completeSale());
    }

    // =========================
    // Load Products 
    // =========================
    private void loadProducts() {
        try (Connection con = DBConnection.getConnection()) {

            String sql = "SELECT id, name FROM products";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                cmbProducts.addItem(rs.getInt("id") + "-" + rs.getString("name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // Load Price 
    // =========================
    private void loadPrice() {

        if (cmbProducts.getSelectedItem() == null) return;

        try (Connection con = DBConnection.getConnection()) {

            String selected = cmbProducts.getSelectedItem().toString();
            selectedProductId = Integer.parseInt(selected.split("-")[0]);

            String sql = "SELECT price FROM products WHERE id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, selectedProductId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                txtPrice.setText(rs.getString("price"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // Add To Cart 
    // =========================
    private void addToCart() {

        try {
            String product = cmbProducts.getSelectedItem().toString();
            double price = Double.parseDouble(txtPrice.getText());
            int qty = Integer.parseInt(txtQty.getText());

            double subtotal = price * qty;

            model.addRow(new Object[]{
                    product.split("-")[1],
                    price,
                    qty,
                    subtotal
            });

            double total = Double.parseDouble(txtTotal.getText());
            txtTotal.setText(String.valueOf(total + subtotal));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid Data");
        }
    }

    // =========================
    // Complete Sale 
    // =========================
    private void completeSale() {

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            String orderSql =
                    "INSERT INTO orders(customer_id, order_date, total_amount) VALUES(?, NOW(), ?)";
            PreparedStatement orderPs =
                    con.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);

            orderPs.setInt(1, 1);
            orderPs.setDouble(2, Double.parseDouble(txtTotal.getText()));
            orderPs.executeUpdate();

            ResultSet rs = orderPs.getGeneratedKeys();
            int orderId = 0;

            if (rs.next()) orderId = rs.getInt(1);

            for (int i = 0; i < model.getRowCount(); i++) {

                String productName = model.getValueAt(i, 0).toString();
                int qtySold = Integer.parseInt(model.getValueAt(i, 2).toString());
                double subtotal = Double.parseDouble(model.getValueAt(i, 3).toString());

                PreparedStatement getProduct =
                        con.prepareStatement("SELECT id, quantity FROM products WHERE name=?");
                getProduct.setString(1, productName);
                ResultSet r2 = getProduct.executeQuery();

                int pid = 0;
                int currentStock = 0;

                if (r2.next()) {
                    pid = r2.getInt("id");
                    currentStock = r2.getInt("quantity");
                }

                if (qtySold > currentStock) {
                    JOptionPane.showMessageDialog(this,
                            "Not enough stock for " + productName);
                    con.rollback();
                    return;
                }

                PreparedStatement detailPs =
                        con.prepareStatement(
                                "INSERT INTO order_details(order_id, product_id, quantity, subtotal) VALUES(?,?,?,?)");

                detailPs.setInt(1, orderId);
                detailPs.setInt(2, pid);
                detailPs.setInt(3, qtySold);
                detailPs.setDouble(4, subtotal);
                detailPs.executeUpdate();

                PreparedStatement updateStock =
                        con.prepareStatement(
                                "UPDATE products SET quantity = quantity - ? WHERE id=?");

                updateStock.setInt(1, qtySold);
                updateStock.setInt(2, pid);
                updateStock.executeUpdate();
            }

            con.commit();

            JOptionPane.showMessageDialog(this, "Sale Completed!");
            generateInvoice(orderId);

            model.setRowCount(0);
            txtTotal.setText("0.00");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // Generate Invoice 
    // =========================
    private void generateInvoice(int orderId) {

        try (Connection con = DBConnection.getConnection()) {

            JasperReport report = JasperCompileManager.compileReport(
                    getClass().getResourceAsStream("/com/pss/reports/invoice.jrxml"));

            Map<String, Object> map = new HashMap<>();
            map.put("ORDER_ID", orderId);

            JasperPrint print =
                    JasperFillManager.fillReport(report, map, con);

            JasperViewer.viewReport(print, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}