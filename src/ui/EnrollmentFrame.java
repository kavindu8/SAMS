package ui;

import config.DatabaseConnection;
import utils.ValidationUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class EnrollmentFrame extends JFrame {
    private JComboBox<String> cmbStudent, cmbCourse, cmbSemester, cmbStatus;
    private JTextField txtAcademicYear;
    private JTable tblEnrollment;
    private DefaultTableModel tableModel;
    private Connection connection;
    
    public EnrollmentFrame() {
        connection = DatabaseConnection.getInstance().getConnection();
        
        setTitle("Course Registration / Enrollment");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        
        initComponents();
        loadStudents();
        loadCourses();
        loadEnrollments();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel formPanel = createFormPanel();
        JPanel tablePanel = createTablePanel();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, tablePanel);
        splitPane.setDividerLocation(180);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Enrollment Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 0 - Student & Course
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Student:"), gbc);
        cmbStudent = new JComboBox<>();
        cmbStudent.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        panel.add(cmbStudent, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Course:"), gbc);
        cmbCourse = new JComboBox<>();
        cmbCourse.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 3;
        panel.add(cmbCourse, gbc);
        
     
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Semester:"), gbc);
        cmbSemester = new JComboBox<>(new String[]{"1st sem 1", "1st sem 2","2nd sem 1","2nd sem 2"});
        cmbSemester.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        panel.add(cmbSemester, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Academic Year:"), gbc);
        txtAcademicYear = new JTextField("2026");
        txtAcademicYear.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 3;
        panel.add(txtAcademicYear, gbc);
        
       
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Status:"), gbc);
        cmbStatus = new JComboBox<>(new String[]{"Enrolled", "Dropped", "Completed"});
        cmbStatus.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        panel.add(cmbStatus, gbc);
        
      
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 4;
        panel.add(createButtonPanel(), gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton btnEnroll = new JButton("Enroll Student");
        btnEnroll.setBackground(new Color(40, 167, 69));
        btnEnroll.setForeground(Color.WHITE);
        btnEnroll.addActionListener(e -> enrollStudent());
        
        JButton btnUpdate = new JButton("Update Status");
        btnUpdate.setBackground(new Color(0, 123, 255));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.addActionListener(e -> updateStatus());
        
        JButton btnDelete = new JButton("Delete");
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteEnrollment());
        
        JButton btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> clearFields());
        
        panel.add(btnEnroll);
        panel.add(btnUpdate);
        panel.add(btnDelete);
        panel.add(btnClear);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Enrollment List"));
        
        String[] columns = {"Enrollment ID", "Student ID", "Student Name", "Course Code", "Course Name", "Semester", "Year", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblEnrollment = new JTable(tableModel);
        tblEnrollment.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fillFormFromTable();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tblEnrollment);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadStudents() {
        try {
            cmbStudent.removeAllItems();
            cmbStudent.addItem("-- Select Student --");
            
            String sql = "SELECT student_id, CONCAT(first_name, ' ', last_name) as name " +
                        "FROM students WHERE status='Active' ORDER BY first_name";
            
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                String studentId = rs.getString("student_id");  
                String name = rs.getString("name");
                cmbStudent.addItem(studentId + " - " + name);
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException ex) {
            ValidationUtil.showError("Error loading students: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void loadCourses() {
        try {
            cmbCourse.removeAllItems();
            cmbCourse.addItem("-- Select Course --");
            
            String sql = "SELECT course_id, course_code, course_name FROM courses ORDER BY course_code";
            
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                int courseId = rs.getInt("course_id");
                String courseCode = rs.getString("course_code");
                String courseName = rs.getString("course_name");
                cmbCourse.addItem(courseId + " - " + courseCode + " - " + courseName);
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException ex) {
            ValidationUtil.showError("Error loading courses: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void enrollStudent() {
        if (cmbStudent.getSelectedIndex() == 0) {
            ValidationUtil.showError("Please select a student!");
            return;
        }
        if (cmbCourse.getSelectedIndex() == 0) {
            ValidationUtil.showError("Please select a course!");
            return;
        }
        
        try {
            String selectedStudent = (String) cmbStudent.getSelectedItem();
            String studentId = selectedStudent.split(" - ")[0];  
            
            String selectedCourse = (String) cmbCourse.getSelectedItem();
            int courseId = Integer.parseInt(selectedCourse.split(" - ")[0]);
            
            String semester = (String) cmbSemester.getSelectedItem();
            String year = txtAcademicYear.getText().trim();
            String status = (String) cmbStatus.getSelectedItem();
            
            String sql = "INSERT INTO enrollments (student_id, course_id, enrollment_date, semester, academic_year, status) " +
                        "VALUES (?, ?, CURDATE(), ?, ?, ?)";
            
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, studentId);  
            pstmt.setInt(2, courseId);
            pstmt.setString(3, semester);
            pstmt.setString(4, year);
            pstmt.setString(5, status);
            
            if (pstmt.executeUpdate() > 0) {
                ValidationUtil.showSuccess("Student enrolled successfully!");
                clearFields();
                loadEnrollments();
            }
            
            pstmt.close();
            
        } catch (SQLException ex) {
            ValidationUtil.showError("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void updateStatus() {
        int row = tblEnrollment.getSelectedRow();
        if (row < 0) {
            ValidationUtil.showError("Please select an enrollment to update!");
            return;
        }
        
        try {
            int enrollmentId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            String newStatus = (String) cmbStatus.getSelectedItem();
            
            String sql = "UPDATE enrollments SET status = ? WHERE enrollment_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, enrollmentId);
            
            if (pstmt.executeUpdate() > 0) {
                ValidationUtil.showSuccess("Enrollment status updated!");
                loadEnrollments();
            }
            
            pstmt.close();
            
        } catch (SQLException ex) {
            ValidationUtil.showError("Error: " + ex.getMessage());
        }
    }
    
    private void deleteEnrollment() {
        int row = tblEnrollment.getSelectedRow();
        if (row < 0) {
            ValidationUtil.showError("Please select an enrollment to delete!");
            return;
        }
        
        if (ValidationUtil.showConfirmation("Are you sure you want to delete this enrollment?")) {
            try {
                int enrollmentId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                
                String sql = "DELETE FROM enrollments WHERE enrollment_id = ?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, enrollmentId);
                
                if (pstmt.executeUpdate() > 0) {
                    ValidationUtil.showSuccess("Enrollment deleted successfully!");
                    loadEnrollments();
                }
                
                pstmt.close();
                
            } catch (SQLException ex) {
                ValidationUtil.showError("Error: " + ex.getMessage());
            }
        }
    }
    
    private void loadEnrollments() {
        try {
            tableModel.setRowCount(0);
            
            String sql = "SELECT e.enrollment_id, e.student_id, CONCAT(s.first_name, ' ', s.last_name) as student_name, " +
                        "c.course_code, c.course_name, e.semester, e.academic_year, e.status " +
                        "FROM enrollments e " +
                        "JOIN students s ON e.student_id = s.student_id " +
                        "JOIN courses c ON e.course_id = c.course_id " +
                        "ORDER BY e.enrollment_date DESC";
            
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("enrollment_id"),
                    rs.getString("student_id"),  
                    rs.getString("student_name"),
                    rs.getString("course_code"),
                    rs.getString("course_name"),
                    rs.getString("semester"),
                    rs.getString("academic_year"),
                    rs.getString("status")
                };
                tableModel.addRow(row);
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException ex) {
            ValidationUtil.showError("Error loading enrollments: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void fillFormFromTable() {
        int row = tblEnrollment.getSelectedRow();
        if (row >= 0) {
            String status = tableModel.getValueAt(row, 7).toString();
            cmbStatus.setSelectedItem(status);
        }
    }
    
    private void clearFields() {
        cmbStudent.setSelectedIndex(0);
        cmbCourse.setSelectedIndex(0);
        cmbSemester.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
        txtAcademicYear.setText("2024");
    }
}