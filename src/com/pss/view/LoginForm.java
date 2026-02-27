package com.pss.view;

import com.pss.util.DBConnection;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblMessage;

    public LoginForm() {

        setTitle("Pharmacy Sales System - Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 2));

        // ===== LEFT PANEL (Branding) =====
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(20, 30, 60));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(new EmptyBorder(150, 80, 150, 50));

        JLabel lblTitle = new JLabel("Pharmacy");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 40));

        JLabel lblSubTitle = new JLabel("Sales Management");
        lblSubTitle.setForeground(Color.WHITE);
        lblSubTitle.setFont(new Font("Segoe UI", Font.BOLD, 40));

        JLabel lblDesc = new JLabel("<html>Inventory • Billing • Reports<br>Dashboard & Analytics</html>");
        lblDesc.setForeground(Color.LIGHT_GRAY);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        JLabel lblFooter = new JLabel("Pharmacy Sales System");
        lblFooter.setForeground(Color.GRAY);
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        leftPanel.add(lblTitle);
        leftPanel.add(lblSubTitle);
        leftPanel.add(Box.createVerticalStrut(30));
        leftPanel.add(lblDesc);
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(lblFooter);

        // ===== RIGHT PANEL (Login Card) =====
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(new Color(240, 240, 240));

        JPanel loginCard = new JPanel();
        loginCard.setPreferredSize(new Dimension(400, 400));
        loginCard.setBackground(Color.WHITE);
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel lblSignIn = new JLabel("Sign in");
        lblSignIn.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel lblInfo = new JLabel("Use your staff account to continue.");
        lblInfo.setForeground(Color.GRAY);

        JLabel lblUser = new JLabel("Username");
        txtUsername = new JTextField();
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JLabel lblPass = new JLabel("Password");
        txtPassword = new JPasswordField();
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(50, 100, 220));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton btnExit = new JButton("Exit");
        btnExit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        lblMessage = new JLabel("");
        lblMessage.setForeground(Color.RED);

        loginCard.add(lblSignIn);
        loginCard.add(Box.createVerticalStrut(10));
        loginCard.add(lblInfo);
        loginCard.add(Box.createVerticalStrut(30));
        loginCard.add(lblUser);
        loginCard.add(txtUsername);
        loginCard.add(Box.createVerticalStrut(15));
        loginCard.add(lblPass);
        loginCard.add(txtPassword);
        loginCard.add(Box.createVerticalStrut(25));
        loginCard.add(btnLogin);
        loginCard.add(Box.createVerticalStrut(10));
        loginCard.add(btnExit);
        loginCard.add(Box.createVerticalStrut(10));
        loginCard.add(lblMessage);

        rightPanel.add(loginCard);

        add(leftPanel);
        add(rightPanel);
        
        getRootPane().setDefaultButton(btnLogin);
        txtUsername.addActionListener(e -> login());
        txtPassword.addActionListener(e -> login());
        
        // ===== ACTIONS =====
        btnLogin.addActionListener(e -> login());
        btnExit.addActionListener(e -> System.exit(0));
        
        
    }

    private void login() {

    String username = txtUsername.getText();
    String password = String.valueOf(txtPassword.getPassword());

    try {
        Connection con = DBConnection.getConnection();
        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, username);
        pst.setString(2, password);

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {

            String role = rs.getString("role");

            if (role.equalsIgnoreCase("Admin")) {

                AdminDashboard dashboard = new AdminDashboard(username);
                dashboard.setVisible(true);

                // Show low stock AFTER dashboard loads
                SwingUtilities.invokeLater(() -> {
                    dashboard.showLowStockAfterLogin();
                });

            } else if (role.equalsIgnoreCase("Cashier")) {

                CashierDashboard dashboard = new CashierDashboard(username);
                dashboard.setVisible(true);
            }

            this.dispose();

        } else {
            lblMessage.setText("Invalid username or password!");
        }

    } catch (Exception ex) {
        ex.printStackTrace();
    }
}

    public static void main(String[] args) {
        new LoginForm().setVisible(true);
    }
}