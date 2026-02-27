package com.pss.view;

import com.pss.util.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UserForm extends JPanel {

    private JTextField txtId, txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JTable table;
    private DefaultTableModel model;

    public UserForm() {

        setLayout(new BorderLayout(15,15));
        setBackground(new Color(240,242,245));
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        // ===== LEFT FORM PANEL =====
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(350, 500));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel title = new JLabel("User Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(0,0,20,0));

        txtId = new JTextField();
        txtId.setVisible(false);

        txtUsername = createStyledField();
        txtPassword = new JPasswordField();
        styleField(txtPassword);

        cmbRole = new JComboBox<>(new String[]{"Admin", "Cashier"});
        cmbRole.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        leftPanel.add(title);
        leftPanel.add(createField("Username", txtUsername));
        leftPanel.add(createField("Password", txtPassword));
        leftPanel.add(createField("Role", cmbRole));

        leftPanel.add(Box.createVerticalStrut(20));

        JButton btnSave = createButton("Add", new Color(34,167,120));
        JButton btnUpdate = createButton("Update", new Color(243,156,18));
        JButton btnDelete = createButton("Delete", new Color(220,53,69));
        JButton btnClear = createButton("Clear", new Color(108,117,125));

        leftPanel.add(btnSave);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(btnUpdate);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(btnDelete);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(btnClear);

        add(leftPanel, BorderLayout.WEST);

        // ===== RIGHT TABLE PANEL =====
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JLabel tableTitle = new JLabel("Users List");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "Username", "Role"});

        table = new JTable(model);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);

        rightPanel.add(tableTitle, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        add(rightPanel, BorderLayout.CENTER);

        loadUsers();

        // ===== BUTTON ACTIONS =====
        btnSave.addActionListener(e -> saveUser());
        btnUpdate.addActionListener(e -> updateUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnClear.addActionListener(e -> clearFields());

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                txtId.setText(model.getValueAt(row, 0).toString());
                txtUsername.setText(model.getValueAt(row, 1).toString());
                cmbRole.setSelectedItem(model.getValueAt(row, 2).toString());
            }
        });
    }

    // ===== METHODS =====

    private JTextField createStyledField() {
        JTextField field = new JTextField();
        styleField(field);
        return field;
    }

    private void styleField(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private JPanel createField(String labelText, JComponent field) {

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(5,5));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        panel.setBorder(BorderFactory.createEmptyBorder(5,0,10,0));

        return panel;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return button;
    }

    // ===== DATABASE METHODS =====

    private void loadUsers() {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement("SELECT * FROM users");
            ResultSet rs = pst.executeQuery();

            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveUser() {

        if (txtUsername.getText().isEmpty() ||
                String.valueOf(txtPassword.getPassword()).isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Username and Password cannot be empty!");
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(
                    "INSERT INTO users (username,password,role) VALUES (?,?,?)"
            );

            pst.setString(1, txtUsername.getText());
            pst.setString(2, new String(txtPassword.getPassword()));
            pst.setString(3, cmbRole.getSelectedItem().toString());

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "User Added Successfully!");
            loadUsers();
            clearFields();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUser() {

        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Select a user to update!");
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(
                    "UPDATE users SET username=?, password=?, role=? WHERE user_id=?"
            );

            pst.setString(1, txtUsername.getText());
            pst.setString(2, new String(txtPassword.getPassword()));
            pst.setString(3, cmbRole.getSelectedItem().toString());
            pst.setInt(4, Integer.parseInt(txtId.getText()));

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "User Updated Successfully!");
            loadUsers();
            clearFields();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteUser() {

        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Select a user first!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this user?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(
                        "DELETE FROM users WHERE user_id=?"
                );

                pst.setInt(1, Integer.parseInt(txtId.getText()));
                pst.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "User Deleted Successfully!");
                loadUsers();
                clearFields();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void clearFields() {
        txtId.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
    }
}