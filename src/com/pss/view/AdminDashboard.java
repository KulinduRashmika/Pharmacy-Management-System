package com.pss.view;

import com.pss.util.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminDashboard extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;

    
    private JPanel dashboardPanel;
    private String currentUsername;

    public AdminDashboard(String username) {

        this.currentUsername = username; 

        setTitle("Pharmacy Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ================= SIDEBAR =================
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(18, 28, 55));
        sidebar.setPreferredSize(new Dimension(230, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JLabel logo = new JLabel("Pharmacy");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(20,10,30,10));

        JButton btnDashboard = createSidebarButton("Dashboard");
        JButton btnUsers = createSidebarButton("Users");
        JButton btnProducts = createSidebarButton("Products");
        JButton btnBilling = createSidebarButton("Billing");
        JButton btnReports = createSidebarButton("Reports");
        JButton btnLowStock = createSidebarButton("Low Stock");
        JButton btnLogout = createSidebarButton("Logout");

        sidebar.add(logo);
        sidebar.add(btnDashboard);
        sidebar.add(btnUsers);
        sidebar.add(btnProducts);
        sidebar.add(btnBilling);
        sidebar.add(btnReports);
        sidebar.add(btnLowStock);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);

        add(sidebar, BorderLayout.WEST);

        // ================= CONTENT =================
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(240,242,245));

        dashboardPanel = createDashboardPanel(username);
        contentPanel.add(dashboardPanel, "Dashboard");

        contentPanel.add(new UserForm(), "Users");
        contentPanel.add(new MedicineForm(), "Products");
        contentPanel.add(new BillingForm(), "Billing");
        contentPanel.add(new SalesReportForm(), "Reports");
        contentPanel.add(new LowStockForm(), "LowStock");

        add(contentPanel, BorderLayout.CENTER);

        // ================= BUTTON ACTIONS =================
        btnDashboard.addActionListener(e -> {
            refreshDashboard(); 
            cardLayout.show(contentPanel, "Dashboard");
            setActive(btnDashboard, btnDashboard, btnUsers, btnProducts, btnBilling, btnReports, btnLowStock);
        });

        btnUsers.addActionListener(e -> {
            cardLayout.show(contentPanel, "Users");
            setActive(btnUsers, btnDashboard, btnUsers, btnProducts, btnBilling, btnReports, btnLowStock);
        });

        btnProducts.addActionListener(e -> {
            cardLayout.show(contentPanel, "Products");
            setActive(btnProducts, btnDashboard, btnUsers, btnProducts, btnBilling, btnReports, btnLowStock);
        });

        btnBilling.addActionListener(e -> {
            cardLayout.show(contentPanel, "Billing");
            setActive(btnBilling, btnDashboard, btnUsers, btnProducts, btnBilling, btnReports, btnLowStock);
        });

        btnReports.addActionListener(e -> {
            cardLayout.show(contentPanel, "Reports");
            setActive(btnReports, btnDashboard, btnUsers, btnProducts, btnBilling, btnReports, btnLowStock);
        });

        btnLowStock.addActionListener(e -> {
            cardLayout.show(contentPanel, "LowStock");
            setActive(btnLowStock, btnDashboard, btnUsers, btnProducts, btnBilling, btnReports, btnLowStock);
        });

        btnLogout.addActionListener(e -> {
            new LoginForm().setVisible(true);
            dispose();
        });

        setActive(btnDashboard, btnDashboard, btnUsers, btnProducts, btnBilling, btnReports, btnLowStock);

        
    }
    public void showLowStockAfterLogin() {
    checkLowStock();
}

    // ===== AUTO REFRESH METHOD =====
    private void refreshDashboard() {

        contentPanel.remove(dashboardPanel);

        dashboardPanel = createDashboardPanel(currentUsername);
        contentPanel.add(dashboardPanel, "Dashboard");

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ================= SIDEBAR BUTTON STYLE =================
    private JButton createSidebarButton(String text) {

        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(200, 45));
        button.setBackground(new Color(25,45,90));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(40,75,150));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(25,45,90));
            }
        });

        return button;
    }

    private void setActive(JButton active, JButton... buttons) {
        for (JButton btn : buttons) {
            btn.setBackground(new Color(25,45,90));
        }
        active.setBackground(new Color(60,120,220));
    }

    // ================= DASHBOARD PANEL =================
    private JPanel createDashboardPanel(String username) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240,242,245));

        JLabel welcome = new JLabel("Welcome " + username, JLabel.CENTER);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcome.setBorder(BorderFactory.createEmptyBorder(25,0,25,0));

        panel.add(welcome, BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel();
        centerWrapper.setBackground(new Color(240,242,245));
        centerWrapper.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 40));

        centerWrapper.add(createStatCard("Total Products", getCount("products")));
        centerWrapper.add(createStatCard("Total Users", getCount("users")));
        centerWrapper.add(createStatCard("Total Orders", getCount("orders")));
        centerWrapper.add(createStatCard("Low Stock Items", getLowStockCount()));

        panel.add(centerWrapper, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String title, int value) {

        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(280,160));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220,220,220),1,true));

        JLabel lblTitle = new JLabel(title, JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JLabel lblValue = new JLabel(String.valueOf(value), JLabel.CENTER);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblValue.setForeground(new Color(60,120,220));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        return card;
    }

    private int getCount(String table) {
        try {
            Connection con = DBConnection.getConnection();
            ResultSet rs = con.createStatement()
                    .executeQuery("SELECT COUNT(*) FROM " + table);
            if(rs.next()) return rs.getInt(1);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getLowStockCount() {
        try {
            Connection con = DBConnection.getConnection();
            ResultSet rs = con.createStatement()
                    .executeQuery("SELECT COUNT(*) FROM products WHERE quantity <= 10");
            if(rs.next()) return rs.getInt(1);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void checkLowStock() {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(
                    "SELECT name, quantity FROM products WHERE quantity <= 10"
            );
            ResultSet rs = pst.executeQuery();

            StringBuilder lowStockItems = new StringBuilder();

            while (rs.next()) {
                lowStockItems.append(rs.getString("name"))
                        .append(" (Stock: ")
                        .append(rs.getInt("quantity"))
                        .append(")\n");
            }

            if (lowStockItems.length() > 0) {
                JOptionPane.showMessageDialog(this,
                        "⚠ Low Stock Alert!\n\n" + lowStockItems.toString(),
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}