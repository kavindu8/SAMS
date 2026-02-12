package ui;

import config.DatabaseConnection;
import utils.ValidationUtil;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.base.*;
import net.sf.jasperreports.engine.type.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import javax.swing.*;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.io.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class ReportsFrame extends JFrame {
    private Connection connection;
    
    public ReportsFrame() {
        connection = DatabaseConnection.getInstance().getConnection();
        
        setTitle("Attendance Report - SAMS");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        mainPanel.setLayout(new GridLayout(3, 1, 20, 20));
        
        JLabel lblTitle = new JLabel("Attendance Report", SwingConstants.CENTER);
        lblTitle.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
        mainPanel.add(lblTitle);
        
        JButton btnView = new JButton("View Report");
        btnView.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 16));
        btnView.setBackground(new Color(0, 123, 255));
        btnView.setForeground(Color.WHITE);
        btnView.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnView.addActionListener(e -> viewReport());
        mainPanel.add(btnView);
        
        JButton btnExportPDF = new JButton("Export as PDF");
        btnExportPDF.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 16));
        btnExportPDF.setBackground(new Color(220, 53, 69));
        btnExportPDF.setForeground(Color.WHITE);
        btnExportPDF.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExportPDF.addActionListener(e -> exportPDF());
        mainPanel.add(btnExportPDF);
        
        add(mainPanel);
    }
    

    private JasperReport createAttendanceReport() throws JRException {
  
        JasperDesign jasperDesign = new JasperDesign();
        jasperDesign.setName("AttendanceReport");
        jasperDesign.setPageWidth(842);
        jasperDesign.setPageHeight(595);
        jasperDesign.setOrientation(OrientationEnum.LANDSCAPE);
        jasperDesign.setColumnWidth(802);
        jasperDesign.setLeftMargin(20);
        jasperDesign.setRightMargin(20);
        jasperDesign.setTopMargin(20);
        jasperDesign.setBottomMargin(20);
        
    
        JRDesignField fieldId = new JRDesignField();
        fieldId.setName("attendanceId");
        fieldId.setValueClass(Integer.class);
        jasperDesign.addField(fieldId);
        
        JRDesignField fieldStudent = new JRDesignField();
        fieldStudent.setName("studentName");
        fieldStudent.setValueClass(String.class);
        jasperDesign.addField(fieldStudent);
        
        JRDesignField fieldCourseCode = new JRDesignField();
        fieldCourseCode.setName("courseCode");
        fieldCourseCode.setValueClass(String.class);
        jasperDesign.addField(fieldCourseCode);
        
        JRDesignField fieldCourseName = new JRDesignField();
        fieldCourseName.setName("courseName");
        fieldCourseName.setValueClass(String.class);
        jasperDesign.addField(fieldCourseName);
        
        JRDesignField fieldDate = new JRDesignField();
        fieldDate.setName("attendanceDate");
        fieldDate.setValueClass(java.sql.Date.class);
        jasperDesign.addField(fieldDate);
        
        JRDesignField fieldStatus = new JRDesignField();
        fieldStatus.setName("status");
        fieldStatus.setValueClass(String.class);
        jasperDesign.addField(fieldStatus);
        
        JRDesignField fieldRemarks = new JRDesignField();
        fieldRemarks.setName("remarks");
        fieldRemarks.setValueClass(String.class);
        jasperDesign.addField(fieldRemarks);
        
      
        JRDesignBand titleBand = new JRDesignBand();
        titleBand.setHeight(60);
        
        JRDesignStaticText titleText = new JRDesignStaticText();
        titleText.setText("Smart Academic Management System - Attendance Report");
        titleText.setX(0);
        titleText.setY(10);
        titleText.setWidth(802);
        titleText.setHeight(30);
        titleText.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        titleText.setFontSize(18f);
        titleText.setBold(true);
        titleText.setForecolor(new Color(0, 51, 102));
        titleBand.addElement(titleText);
        
        JRDesignStaticText dateText = new JRDesignStaticText();
        dateText.setText("Generated: " + new java.util.Date().toString());
        dateText.setX(0);
        dateText.setY(45);
        dateText.setWidth(802);
        dateText.setHeight(15);
        dateText.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        dateText.setFontSize(10f);
        titleBand.addElement(dateText);
        
        jasperDesign.setTitle(titleBand);
        
   
        JRDesignBand columnHeaderBand = new JRDesignBand();
        columnHeaderBand.setHeight(30);
        
  
        JRDesignRectangle headerRect = new JRDesignRectangle();
        headerRect.setX(0);
        headerRect.setY(0);
        headerRect.setWidth(802);
        headerRect.setHeight(30);
        headerRect.setBackcolor(new Color(0, 123, 255));
        headerRect.setMode(ModeEnum.OPAQUE);
        columnHeaderBand.addElement(headerRect);
        

        addColumnHeader(columnHeaderBand, "ID", 0, 40);
        addColumnHeader(columnHeaderBand, "Student Name", 40, 180);
        addColumnHeader(columnHeaderBand, "Course Code", 220, 100);
        addColumnHeader(columnHeaderBand, "Course Name", 320, 180);
        addColumnHeader(columnHeaderBand, "Date", 500, 100);
        addColumnHeader(columnHeaderBand, "Status", 600, 100);
        addColumnHeader(columnHeaderBand, "Remarks", 700, 102);
        
        jasperDesign.setColumnHeader(columnHeaderBand);
        
 
        JRDesignBand detailBand = new JRDesignBand();
        detailBand.setHeight(25);
        
      
        JRDesignRectangle rowRect = new JRDesignRectangle();
        rowRect.setX(0);
        rowRect.setY(0);
        rowRect.setWidth(802);
        rowRect.setHeight(25);
        rowRect.setBackcolor(new Color(245, 245, 245));
        rowRect.setMode(ModeEnum.OPAQUE);
        
        JRDesignExpression bgExpression = new JRDesignExpression();
        bgExpression.setText("new Boolean($V{REPORT_COUNT}.intValue() % 2 == 0)");
        rowRect.setPrintWhenExpression(bgExpression);
        detailBand.addElement(rowRect);
        
       
        addDetailField(detailBand, "$F{attendanceId}", 0, 40, false);
        addDetailField(detailBand, "$F{studentName}", 40, 180, false);
        addDetailField(detailBand, "$F{courseCode}", 220, 100, false);
        addDetailField(detailBand, "$F{courseName}", 320, 180, false);
        addDetailField(detailBand, "$F{attendanceDate}", 500, 100, false);
        
       
        JRDesignTextField statusField = new JRDesignTextField();
        statusField.setX(600);
        statusField.setY(5);
        statusField.setWidth(100);
        statusField.setHeight(20);
        
        JRDesignExpression statusExpression = new JRDesignExpression();
        statusExpression.setText("$F{status}");
        statusField.setExpression(statusExpression);
        
        statusField.setBold(true);
        statusField.setFontSize(10f);
        statusField.setForecolor(new Color(0, 123, 255)); // Blue color
        statusField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        
        detailBand.addElement(statusField);
        
        addDetailField(detailBand, "$F{remarks}", 700, 102, false);
        
        ((JRDesignSection) jasperDesign.getDetailSection()).addBand(detailBand);
        
    
        JRDesignBand summaryBand = new JRDesignBand();
        summaryBand.setHeight(40);
        
        JRDesignStaticText summaryText = new JRDesignStaticText();
        summaryText.setText("Total Records: ");
        summaryText.setX(600);
        summaryText.setY(10);
        summaryText.setWidth(100);
        summaryText.setHeight(20);
        summaryText.setBold(true);
        summaryBand.addElement(summaryText);
        
        JRDesignTextField countField = new JRDesignTextField();
        countField.setX(700);
        countField.setY(10);
        countField.setWidth(102);
        countField.setHeight(20);
        
        JRDesignExpression countExpression = new JRDesignExpression();
        countExpression.setText("$V{REPORT_COUNT}");
        countField.setExpression(countExpression);
        countField.setBold(true);
        summaryBand.addElement(countField);
        
        jasperDesign.setSummary(summaryBand);
        
     
        return JasperCompileManager.compileReport(jasperDesign);
    }
    
 
    private void addColumnHeader(JRDesignBand band, String text, int x, int width) {
        JRDesignStaticText header = new JRDesignStaticText();
        header.setText(text);
        header.setX(x);
        header.setY(5);
        header.setWidth(width);
        header.setHeight(20);
        header.setForecolor(Color.WHITE);
        header.setBold(true);
        header.setFontSize(11f);
        header.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        band.addElement(header);
    }
    
    private void addDetailField(JRDesignBand band, String expression, int x, int width, boolean bold) {
        JRDesignTextField field = new JRDesignTextField();
        field.setX(x);
        field.setY(5);
        field.setWidth(width);
        field.setHeight(20);
        
        JRDesignExpression expr = new JRDesignExpression();
        expr.setText(expression);
        field.setExpression(expr);
        
        field.setFontSize(10f);
        if (bold) field.setBold(true);
        
        band.addElement(field);
    }
    

    private void viewReport() {
        try {
      
            List<AttendanceRecord> attendanceList = getAttendanceData();
            
            if (attendanceList.isEmpty()) {
                ValidationUtil.showError("No attendance records found!");
                return;
            }
            
         
            JasperReport jasperReport = createAttendanceReport();
            
         
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(attendanceList);
            
        
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport, 
                new HashMap<>(), 
                dataSource
            );
            
       
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setVisible(true);
            
            System.out.println("✅ Report viewed successfully!");
            
        } catch (Exception ex) {
            ValidationUtil.showError("❌ Error:\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }
    

    private void exportPDF() {
        try {
          
            List<AttendanceRecord> attendanceList = getAttendanceData();
            
            if (attendanceList.isEmpty()) {
                ValidationUtil.showError("No attendance records found!");
                return;
            }
            
      
            JasperReport jasperReport = createAttendanceReport();
            
        
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(attendanceList);
            
  
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport, 
                new HashMap<>(), 
                dataSource
            );
            
          
            String downloadsPath = System.getProperty("user.home") + File.separator + "Downloads";
            String filename = "AttendanceReport_" + System.currentTimeMillis() + ".pdf";
            String fullPath = downloadsPath + File.separator + filename;
            
            JasperExportManager.exportReportToPdfFile(jasperPrint, fullPath);
            
            ValidationUtil.showSuccess(" PDF exported successfully!\n\n" +
                "Location: Downloads/" + filename);
            
            System.out.println(" PDF saved to: " + fullPath);
            
        } catch (Exception ex) {
            ValidationUtil.showError(" Error:\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
  
    private List<AttendanceRecord> getAttendanceData() throws SQLException {
        List<AttendanceRecord> records = new ArrayList<>();
        
        String query = "SELECT a.attendance_id, " +
                      "CONCAT(s.first_name, ' ', s.last_name) as student_name, " +
                      "c.course_code, c.course_name, a.attendance_date, a.status, a.remarks " +
                      "FROM attendance a " +
                      "INNER JOIN enrollments e ON a.enrollment_id = e.enrollment_id " +
                      "INNER JOIN students s ON e.student_id = s.student_id " +
                      "INNER JOIN courses c ON e.course_id = c.course_id " +
                      "ORDER BY a.attendance_date DESC";
        
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        
        while (rs.next()) {
            AttendanceRecord record = new AttendanceRecord();
            record.setAttendanceId(rs.getInt("attendance_id"));
            record.setStudentName(rs.getString("student_name"));
            record.setCourseCode(rs.getString("course_code"));
            record.setCourseName(rs.getString("course_name"));
            record.setAttendanceDate(rs.getDate("attendance_date"));
            record.setStatus(rs.getString("status"));
            record.setRemarks(rs.getString("remarks"));
            records.add(record);
        }
        
        rs.close();
        stmt.close();
        
        return records;
    }
}

class AttendanceRecord {
    private int attendanceId;
    private String studentName;
    private String courseCode;
    private String courseName;
    private java.sql.Date attendanceDate;
    private String status;
    private String remarks;
    
   
    public int getAttendanceId() { return attendanceId; }
    public String getStudentName() { return studentName; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public java.sql.Date getAttendanceDate() { return attendanceDate; }
    public String getStatus() { return status; }
    public String getRemarks() { return remarks; }
    

    public void setAttendanceId(int attendanceId) { this.attendanceId = attendanceId; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public void setAttendanceDate(java.sql.Date attendanceDate) { this.attendanceDate = attendanceDate; }
    public void setStatus(String status) { this.status = status; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}