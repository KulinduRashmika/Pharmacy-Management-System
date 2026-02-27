package com.pss.view;

import javax.swing.*;
import java.awt.*;

public class CashierDashboard extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;

    public CashierDashboard(String username) {

        setTitle("Pharmacy Management System - Cashier");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ================= SIDEBAR =================
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(18, 28, 55));
        sidebar.setPreferredSize(new Dimension(230, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JLabel logo = new JLabel("? Cashier");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(20,10,30,10));

        JButton btnBilling = createSidebarButton("Billing");
        JButton btnLogout = createSidebarButton("Logout");

        sidebar.add(logo);
        sidebar.add(btnBilling);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);

        add(sidebar, BorderLayout.WEST);

        // ================= CONTENT =================
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(240,242,245));

        contentPanel.add(createHomePanel(username), "Home");
        contentPanel.add(new BillingForm(), "Billing");

        add(contentPanel, BorderLayout.CENTER);

        // ================= ACTIONS =================
        btnBilling.addActionListener(e -> cardLayout.show(contentPanel, "Billing"));

        btnLogout.addActionListener(e -> {
            new LoginForm().setVisible(true);
            dispose();
        });

        cardLayout.show(contentPanel, "Home");
    }

    // Sidebar Button Style
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

    // Home Panel
    private JPanel createHomePanel(String username) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240,242,245));

        JLabel welcome = new JLabel("Welcome Cashier " + username, JLabel.CENTER);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcome.setBorder(BorderFactory.createEmptyBorder(50,0,0,0));

        panel.add(welcome, BorderLayout.NORTH);

        return panel;
    }
}