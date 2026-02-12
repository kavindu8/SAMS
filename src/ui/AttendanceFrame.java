package ui;

import config.DatabaseConnection;
import utils.ValidationUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class AttendanceFrame extends JFrame {
    private JComboBox<String> cmbEnrollment, cmbStatus;
    private JTextField txtDate, txtRemarks;
    private JTable tblAttendance;
    private DefaultTableModel tableModel;
    private Connection connection;
    
    public AttendanceFrame() {
        connection = DatabaseConnection.getInstance().getConnection();
        
        setTitle("Attendance Management");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        
        initComponents();
        loadEnrollments();
        loadAttendance();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel formPanel = createFormPanel();
        JPanel tablePanel = createTablePanel();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, tablePanel);
        splitPane.setDividerLocation(200);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Attendance Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
      
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Student Enrollment:"), gbc);
        cmbEnrollment = new JComboBox<>();
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        panel.add(cmbEnrollment, gbc);
        
    
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Date (yyyy-MM-dd):"), gbc);
        txtDate = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        gbc.gridx = 1;
        panel.add(txtDate, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Status:"), gbc);
        cmbStatus = new JComboBox<>(new String[]{"Present", "Absent", "Late", "Excused"});
        gbc.gridx = 3;
        panel.add(cmbStatus, gbc);
        
     
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Remarks:"), gbc);
        txtRemarks = new JTextField();
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        panel.add(txtRemarks, gbc);
        
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 4;
        panel.add(createButtonPanel(), gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton btnMark = new JButton("Mark Attendance");
        btnMark.setBackground(new Color(40, 167, 69));
        btnMark.setForeground(Color.WHITE);
        btnMark.addActionListener(e -> markAttendance());
        
        JButton btnDelete = new JButton("Delete");
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteAttendance());
        
        JButton btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> clearFields());
        
        panel.add(btnMark);
        panel.add(btnDelete);
        panel.add(btnClear);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Attendance Records"));
        
        String[] columns = {"Attendance ID", "Student Name", "Course", "Date", "Status", "Remarks"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblAttendance = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tblAttendance);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadEnrollments() {
        try {
            cmbEnrollment.removeAllItems();
            cmbEnrollment.addItem("-- Select Enrollment --");
            
            String sql = "SELECT e.enrollment_id, CONCAT(s.first_name, ' ', s.last_name) as student_name, " +
                        "c.course_code, c.course_name " +
                        "FROM enrollments e " +
                        "JOIN students s ON e.student_id = s.student_id " +
                        "JOIN courses c ON e.course_id = c.course_id " +
                        "WHERE e.status = 'Enrolled' " +
                        "ORDER BY student_name";
            
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                String item = rs.getInt("enrollment_id") + " - " + 
                             rs.getString("student_name") + " - " + 
                             rs.getString("course_code");
                cmbEnrollment.addItem(item);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading enrollments: " + ex.getMessage());
        }
    }
    
    private void markAttendance() {
        if (!validateInputs()) return;
        
        try {
            String selectedEnrollment = (String) cmbEnrollment.getSelectedItem();
            int enrollmentId = Integer.parseInt(selectedEnrollment.split(" - ")[0]);
            
            String sql = "INSERT INTO attendance (enrollment_id, attendance_date, status, remarks) " +
                        "VALUES (?, ?, ?, ?)";
            
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, enrollmentId);
            pstmt.setDate(2, java.sql.Date.valueOf(txtDate.getText().trim()));
            pstmt.setString(3, (String) cmbStatus.getSelectedItem());
            pstmt.setString(4, txtRemarks.getText().trim());
            
            if (pstmt.executeUpdate() > 0) {
                ValidationUtil.showSuccess("Attendance marked successfully!");
                clearFields();
                loadAttendance();
            }
        } catch (SQLException ex) {
            ValidationUtil.showError("Error: " + ex.getMessage());
        }
    }
    
    private void deleteAttendance() {
        int row = tblAttendance.getSelectedRow();
        if (row < 0) {
            ValidationUtil.showError("Please select an attendance record to delete!");
            return;
        }
        
        if (ValidationUtil.showConfirmation("Are you sure you want to delete this record?")) {
            try {
                int attendanceId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                
                String sql = "DELETE FROM attendance WHERE attendance_id = ?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, attendanceId);
                
                if (pstmt.executeUpdate() > 0) {
                    ValidationUtil.showSuccess("Attendance deleted successfully!");
                    loadAttendance();
                }
            } catch (SQLException ex) {
                ValidationUtil.showError("Error: " + ex.getMessage());
            }
        }
    }
    
    private void loadAttendance() {
        try {
            tableModel.setRowCount(0);
            
            String sql = "SELECT a.attendance_id, CONCAT(s.first_name, ' ', s.last_name) as student_name, " +
                        "c.course_name, a.attendance_date, a.status, a.remarks " +
                        "FROM attendance a " +
                        "JOIN enrollments e ON a.enrollment_id = e.enrollment_id " +
                        "JOIN students s ON e.student_id = s.student_id " +
                        "JOIN courses c ON e.course_id = c.course_id " +
                        "ORDER BY a.attendance_date DESC";
            
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("attendance_id"),
                    rs.getString("student_name"),
                    rs.getString("course_name"),
                    rs.getDate("attendance_date"),
                    rs.getString("status"),
                    rs.getString("remarks")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ValidationUtil.showError("Error loading attendance: " + ex.getMessage());
        }
    }
    
    private boolean validateInputs() {
        if (cmbEnrollment.getSelectedIndex() == 0) {
            ValidationUtil.showError("Please select a student enrollment!");
            return false;
        }
        if (!ValidationUtil.isNotEmpty(txtDate.getText(), "Date")) return false;
        
        return true;
    }
    
    private void clearFields() {
        cmbEnrollment.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
        txtDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        txtRemarks.setText("");
    }
}