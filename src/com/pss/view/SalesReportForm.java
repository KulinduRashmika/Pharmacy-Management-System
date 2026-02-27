package com.pss.view;

import com.pss.util.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.io.File;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

public class SalesReportForm extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTotal;
    private JTextField txtDate, txtMonth;

    private String currentCondition = null; 

    public SalesReportForm() {

        setLayout(new BorderLayout(10,10));
        setBackground(new Color(245, 248, 250));
        setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JLabel title = new JLabel("Sales Report Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setForeground(new Color(33, 63, 102));
        add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(15,15));
        centerPanel.setOpaque(false);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,15,10));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createTitledBorder("Filter Options"));

        topPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        txtDate = new JTextField(10);
        topPanel.add(txtDate);

        JButton btnDaily = createButton("Daily Report");
        topPanel.add(btnDaily);

        topPanel.add(new JLabel("Month (YYYY-MM):"));
        txtMonth = new JTextField(7);
        topPanel.add(txtMonth);

        JButton btnMonthly = createButton("Monthly Report");
        topPanel.add(btnMonthly);

        JButton btnAll = createButton("All Sales");
        topPanel.add(btnAll);

        centerPanel.add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
                "Order ID", "Date", "Customer ID", "Total Amount"
        });

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        lblTotal = new JLabel("Total Sales: Rs. 0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setForeground(new Color(0,128,0));

        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,15,5));
        JButton btnPDF = createButton("Export PDF");
        JButton btnExcel = createButton("Export Excel");
        JButton btnJasper = createButton("Print Jasper Report");

        exportPanel.add(btnPDF);
        exportPanel.add(btnExcel);
        exportPanel.add(btnJasper);

        bottomPanel.add(lblTotal, BorderLayout.WEST);
        bottomPanel.add(exportPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        // ===== ACTIONS =====

        btnAll.addActionListener(e -> {
            currentCondition = null;
            loadSalesData(null);
        });

        btnDaily.addActionListener(e -> {
            if (!txtDate.getText().isEmpty()) {
                currentCondition = "DATE(order_date) = '" + txtDate.getText() + "'";
                loadSalesData(currentCondition);
            }
        });

        btnMonthly.addActionListener(e -> {
            if (!txtMonth.getText().isEmpty()) {
                currentCondition = "DATE_FORMAT(order_date,'%Y-%m') = '" + txtMonth.getText() + "'";
                loadSalesData(currentCondition);
            }
        });

        btnPDF.addActionListener(e -> exportToPDF());
        btnExcel.addActionListener(e -> exportToExcel());
        btnJasper.addActionListener(e -> generateJasperReport());

        loadSalesData(null);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(33, 63, 102));
        button.setForeground(Color.WHITE);
        return button;
    }

    private void loadSalesData(String condition) {

        model.setRowCount(0);
        double grandTotal = 0;

        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT * FROM orders";

            if (condition != null)
                sql += " WHERE " + condition;

            sql += " ORDER BY order_date DESC";

            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                int orderId = rs.getInt("order_id");
                Timestamp date = rs.getTimestamp("order_date");
                int customerId = rs.getInt("customer_id");
                double total = rs.getDouble("total_amount");

                grandTotal += total;

                model.addRow(new Object[]{
                        orderId, date, customerId, total
                });
            }

            lblTotal.setText("Total Sales: Rs. " + String.format("%.2f", grandTotal));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== JASPER REPORT =====
    private void generateJasperReport() {

        try {
            Connection con = DBConnection.getConnection();

            InputStream reportStream =
                    getClass().getResourceAsStream("/com/pss/reports/SalesReport.jrxml");

            JasperReport jr = JasperCompileManager.compileReport(reportStream);

            Map<String, Object> parameters = new HashMap<>();

            if (currentCondition != null) {
    parameters.put("FILTER_CONDITION", " AND " + currentCondition);

    if (currentCondition.contains("DATE(order_date)")) {
        parameters.put("REPORT_TITLE", "DAILY SALES REPORT");
    } else {
        parameters.put("REPORT_TITLE", "MONTHLY SALES REPORT");
    }

} else {
    parameters.put("FILTER_CONDITION", "");
    parameters.put("REPORT_TITLE", "ALL SALES REPORT");
}

            JasperPrint jp = JasperFillManager.fillReport(jr, parameters, con);

            JasperViewer.viewReport(jp, false);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating Jasper Report");
        }
    }

    // ===== PDF EXPORT =====
    private void exportToPDF() {
        try {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {

                File file = chooser.getSelectedFile();

                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                Paragraph title = new Paragraph("ABC Pharmacy - Sales Report");
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);

                PdfPTable pdfTable = new PdfPTable(4);

                pdfTable.addCell("Order ID");
                pdfTable.addCell("Date");
                pdfTable.addCell("Customer ID");
                pdfTable.addCell("Total");

                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < 4; j++) {
                        pdfTable.addCell(model.getValueAt(i, j).toString());
                    }
                }

                document.add(pdfTable);
                document.close();

                JOptionPane.showMessageDialog(this, "PDF Generated Successfully!");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== EXCEL EXPORT =====
    private void exportToExcel() {

        try {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {

                File file = chooser.getSelectedFile();
                FileWriter fw = new FileWriter(file);

                for (int i = 0; i < model.getColumnCount(); i++) {
                    fw.write(model.getColumnName(i) + ",");
                }
                fw.write("\n");

                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        fw.write(model.getValueAt(i, j).toString() + ",");
                    }
                    fw.write("\n");
                }

                fw.close();

                JOptionPane.showMessageDialog(this, "Excel Exported Successfully!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}