package ui;

import dao.AssessmentDAO;
import models.Assessment;
import utils.ValidationUtil;
import config.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class AssessmentFrame extends JFrame {
    private JComboBox<String> cmbEnrollment, cmbAssessmentType;
    private JTextField txtMarksObtained, txtMaxMarks, txtDate, txtRemarks;
    private JTable tblAssessments;
    private DefaultTableModel tableModel;
    private AssessmentDAO assessmentDAO;
    private Connection connection;
    
    public AssessmentFrame() {
        assessmentDAO = new AssessmentDAO();
        connection = DatabaseConnection.getInstance().getConnection();
        
        setTitle("Assessment / Results Management");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        
        initComponents();
        loadEnrollments();
        loadAssessments();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel formPanel = createFormPanel();
        JPanel tablePanel = createTablePanel();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, tablePanel);
        splitPane.setDividerLocation(250);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Assessment Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 0 - Student Enrollment
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Student Enrollment:"), gbc);
        cmbEnrollment = new JComboBox<>();
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        panel.add(cmbEnrollment, gbc);
        
        // Row 1
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Assessment Type:"), gbc);
        cmbAssessmentType = new JComboBox<>(new String[]{"Assignment", "Quiz", "Midterm", "Final"});
        gbc.gridx = 1;
        panel.add(cmbAssessmentType, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Date (yyyy-MM-dd):"), gbc);
        txtDate = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        gbc.gridx = 3;
        panel.add(txtDate, gbc);
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Marks Obtained:"), gbc);
        txtMarksObtained = new JTextField();
        gbc.gridx = 1;
        panel.add(txtMarksObtained, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Max Marks:"), gbc);
        txtMaxMarks = new JTextField();
        gbc.gridx = 3;
        panel.add(txtMaxMarks, gbc);
        
        // Row 3
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Remarks:"), gbc);
        txtRemarks = new JTextField();
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        panel.add(txtRemarks, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 4;
        panel.add(createButtonPanel(), gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton btnAdd = new JButton("Add Assessment");
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> addAssessment());
        
        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBackground(new Color(0, 123, 255));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.addActionListener(e -> updateAssessment());
        
        JButton btnDelete = new JButton("Delete");
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteAssessment());
        
        JButton btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> clearFields());
        
        panel.add(btnAdd);
        panel.add(btnUpdate);
        panel.add(btnDelete);
        panel.add(btnClear);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Assessment Records"));
        
        String[] columns = {"ID", "Student Name", "Course", "Type", "Marks", "Max Marks", "Percentage", "Date", "Remarks"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblAssessments = new JTable(tableModel);
        tblAssessments.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fillFormFromTable();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tblAssessments);
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
    
    private void addAssessment() {
        if (!validateInputs()) return;
        
        try {
            Assessment assessment = createAssessmentFromForm();
            
            if (assessmentDAO.addAssessment(assessment)) {
                ValidationUtil.showSuccess("Assessment added successfully!");
                clearFields();
                loadAssessments();
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error: " + ex.getMessage());
        }
    }
    
    private void updateAssessment() {
        int row = tblAssessments.getSelectedRow();
        if (row < 0) {
            ValidationUtil.showError("Please select an assessment to update!");
            return;
        }
        
        if (!validateInputs()) return;
        
        try {
            Assessment assessment = createAssessmentFromForm();
            assessment.setAssessmentId(Integer.parseInt(tableModel.getValueAt(row, 0).toString()));
            
            if (assessmentDAO.updateAssessment(assessment)) {
                ValidationUtil.showSuccess("Assessment updated successfully!");
                clearFields();
                loadAssessments();
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error: " + ex.getMessage());
        }
    }
    
    private void deleteAssessment() {
        int row = tblAssessments.getSelectedRow();
        if (row < 0) {
            ValidationUtil.showError("Please select an assessment to delete!");
            return;
        }
        
        if (ValidationUtil.showConfirmation("Are you sure you want to delete this assessment?")) {
            try {
                int assessmentId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                
                if (assessmentDAO.deleteAssessment(assessmentId)) {
                    ValidationUtil.showSuccess("Assessment deleted successfully!");
                    loadAssessments();
                }
            } catch (SQLException ex) {
                ValidationUtil.showError("Error: " + ex.getMessage());
            }
        }
    }
    
    private void loadAssessments() {
        try {
            List<Assessment> assessments = assessmentDAO.getAllAssessments();
            updateTable(assessments);
        } catch (SQLException ex) {
            ValidationUtil.showError("Error loading assessments: " + ex.getMessage());
        }
    }
    
    private void updateTable(List<Assessment> assessments) {
        tableModel.setRowCount(0);
        
        for (Assessment assessment : assessments) {
            Object[] row = {
                assessment.getAssessmentId(),
                assessment.getStudentName(),
                assessment.getCourseName(),
                assessment.getAssessmentType(),
                assessment.getMarksObtained(),
                assessment.getMaxMarks(),
                String.format("%.2f%%", assessment.getPercentage()),
                assessment.getAssessmentDate(),
                assessment.getRemarks()
            };
            tableModel.addRow(row);
        }
    }
    
    private void fillFormFromTable() {
        int row = tblAssessments.getSelectedRow();
        if (row >= 0) {
            cmbAssessmentType.setSelectedItem(tableModel.getValueAt(row, 3).toString());
            txtMarksObtained.setText(tableModel.getValueAt(row, 4).toString());
            txtMaxMarks.setText(tableModel.getValueAt(row, 5).toString());
            txtRemarks.setText(tableModel.getValueAt(row, 8).toString());
        }
    }
    
    private Assessment createAssessmentFromForm() throws Exception {
        Assessment assessment = new Assessment();
        
        String selectedEnrollment = (String) cmbEnrollment.getSelectedItem();
        int enrollmentId = Integer.parseInt(selectedEnrollment.split(" - ")[0]);
        assessment.setEnrollmentId(enrollmentId);
        
        assessment.setAssessmentType((String) cmbAssessmentType.getSelectedItem());
        assessment.setMarksObtained(Double.parseDouble(txtMarksObtained.getText().trim()));
        assessment.setMaxMarks(Double.parseDouble(txtMaxMarks.getText().trim()));
        assessment.setAssessmentDate(java.sql.Date.valueOf(txtDate.getText().trim()));
        assessment.setRemarks(txtRemarks.getText().trim());
        
        return assessment;
    }
    
    private boolean validateInputs() {
        if (cmbEnrollment.getSelectedIndex() == 0) {
            ValidationUtil.showError("Please select a student enrollment!");
            return false;
        }
        if (!ValidationUtil.isNotEmpty(txtMarksObtained.getText(), "Marks Obtained")) return false;
        if (!ValidationUtil.isNotEmpty(txtMaxMarks.getText(), "Max Marks")) return false;
        if (!ValidationUtil.isNotEmpty(txtDate.getText(), "Date")) return false;
        
        try {
            double marks = Double.parseDouble(txtMarksObtained.getText().trim());
            double maxMarks = Double.parseDouble(txtMaxMarks.getText().trim());
            
            if (marks > maxMarks) {
                ValidationUtil.showError("Marks obtained cannot be greater than max marks!");
                return false;
            }
            if (marks < 0 || maxMarks < 0) {
                ValidationUtil.showError("Marks cannot be negative!");
                return false;
            }
        } catch (NumberFormatException e) {
            ValidationUtil.showError("Marks must be numeric values!");
            return false;
        }
        
        return true;
    }
    
    private void clearFields() {
        cmbEnrollment.setSelectedIndex(0);
        cmbAssessmentType.setSelectedIndex(0);
        txtMarksObtained.setText("");
        txtMaxMarks.setText("");
        txtDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        txtRemarks.setText("");
    }
}
