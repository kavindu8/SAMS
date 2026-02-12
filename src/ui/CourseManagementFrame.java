package ui;

import dao.CourseDAO;
import dao.LecturerDAO;
import models.Course;
import models.Lecturer;
import utils.ValidationUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CourseManagementFrame extends JFrame {
    private JTextField txtCourseId, txtCourseName, txtCourseCode, txtCredits, txtDepartment, txtSearch;
    private JTextArea txtDescription;
    private JComboBox<String> cmbLecturer;
    private JTable tblCourses;
    private DefaultTableModel tableModel;
    private CourseDAO courseDAO;
    private LecturerDAO lecturerDAO;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    
    public CourseManagementFrame() {
        courseDAO = new CourseDAO();
        lecturerDAO = new LecturerDAO();
        
        setTitle("Course Management");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        initComponents();
        loadCourses();
        loadLecturers();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Form Panel
        JPanel formPanel = createFormPanel();
        
        // Table Panel
        JPanel tablePanel = createTablePanel();
        
        // Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, tablePanel);
        splitPane.setDividerLocation(300);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Course Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 0
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Course ID:"), gbc);
        txtCourseId = new JTextField(15);
        gbc.gridx = 1;
        panel.add(txtCourseId, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Course Code:"), gbc);
        txtCourseCode = new JTextField(15);
        gbc.gridx = 3;
        panel.add(txtCourseCode, gbc);
        
        // Row 1
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Course Name:"), gbc);
        txtCourseName = new JTextField(15);
        gbc.gridx = 1;
        panel.add(txtCourseName, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Credits:"), gbc);
        txtCredits = new JTextField(15);
        gbc.gridx = 3;
        panel.add(txtCredits, gbc);
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Department:"), gbc);
        txtDepartment = new JTextField(15);
        gbc.gridx = 1;
        panel.add(txtDepartment, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Lecturer:"), gbc);
        cmbLecturer = new JComboBox<>();
        gbc.gridx = 3;
        panel.add(cmbLecturer, gbc);
        
        // Row 3
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Description:"), gbc);
        txtDescription = new JTextArea(3, 15);
        JScrollPane scrollPane = new JScrollPane(txtDescription);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        panel.add(scrollPane, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 4;
        panel.add(createButtonPanel(), gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        btnAdd = new JButton("Add Course");
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> addCourse());
        
        btnUpdate = new JButton("Update");
        btnUpdate.setBackground(new Color(0, 123, 255));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.addActionListener(e -> updateCourse());
        
        btnDelete = new JButton("Delete");
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteCourse());
        
        btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> clearFields());
        
        panel.add(btnAdd);
        panel.add(btnUpdate);
        panel.add(btnDelete);
        panel.add(btnClear);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Course List"));
        
        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Search");
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        
        // Table
        String[] columns = {"Course ID", "Course Name", "Code", "Credits", "Department", "Lecturer"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblCourses = new JTable(tableModel);
        tblCourses.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fillFormFromTable();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tblCourses);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadLecturers() {
        try {
            cmbLecturer.removeAllItems();
            cmbLecturer.addItem("-- Select Lecturer --");
            
            List<Lecturer> lecturers = lecturerDAO.getAllLecturers();
            for (Lecturer lecturer : lecturers) {
                cmbLecturer.addItem(lecturer.getLecturerId() + " - " + lecturer.getFullName());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading lecturers: " + ex.getMessage());
        }
    }
    
    private void addCourse() {
        if (!validateInputs()) return;
        
        try {
            Course course = createCourseFromForm();
            
            if (courseDAO.addCourse(course)) {
                ValidationUtil.showSuccess("Course added successfully!");
                clearFields();
                loadCourses();
            }
        } catch (SQLException ex) {
            ValidationUtil.showError("Error: " + ex.getMessage());
        }
    }
    
    private void updateCourse() {
        if (txtCourseId.getText().isEmpty()) {
            ValidationUtil.showError("Please select a course to update!");
            return;
        }
        
        if (!validateInputs()) return;
        
        try {
            Course course = createCourseFromForm();
            
            if (courseDAO.updateCourse(course)) {
                ValidationUtil.showSuccess("Course updated successfully!");
                clearFields();
                loadCourses();
            }
        } catch (SQLException ex) {
            ValidationUtil.showError("Error: " + ex.getMessage());
        }
    }
    
    private void deleteCourse() {
        if (txtCourseId.getText().isEmpty()) {
            ValidationUtil.showError("Please select a course to delete!");
            return;
        }
        
        if (ValidationUtil.showConfirmation("Are you sure you want to delete this course?")) {
            try {
                if (courseDAO.deleteCourse(txtCourseId.getText())) {
                    ValidationUtil.showSuccess("Course deleted successfully!");
                    clearFields();
                    loadCourses();
                }
            } catch (SQLException ex) {
                ValidationUtil.showError("Error: " + ex.getMessage());
            }
        }
    }
    
    private void loadCourses() {
        try {
            List<Course> courses = courseDAO.getAllCourses();
            updateTable(courses);
        } catch (SQLException ex) {
            ValidationUtil.showError("Error loading courses: " + ex.getMessage());
        }
    }
    
    private void updateTable(List<Course> courses) {
        tableModel.setRowCount(0);
        
        for (Course course : courses) {
            Object[] row = {
                course.getCourseId(),
                course.getCourseName(),
                course.getCourseCode(),
                course.getCredits(),
                course.getDepartment(),
                course.getLecturerName() != null ? course.getLecturerName() : "Not Assigned"
            };
            tableModel.addRow(row);
        }
    }
    
    private void fillFormFromTable() {
        int row = tblCourses.getSelectedRow();
        if (row >= 0) {
            txtCourseId.setText(tableModel.getValueAt(row, 0).toString());
            txtCourseName.setText(tableModel.getValueAt(row, 1).toString());
            txtCourseCode.setText(tableModel.getValueAt(row, 2).toString());
            txtCredits.setText(tableModel.getValueAt(row, 3).toString());
            txtDepartment.setText(tableModel.getValueAt(row, 4).toString());
        }
    }
    
    private Course createCourseFromForm() {
        Course course = new Course();
        course.setCourseId(txtCourseId.getText().trim());
        course.setCourseName(txtCourseName.getText().trim());
        course.setCourseCode(txtCourseCode.getText().trim());
        course.setCredits(Integer.parseInt(txtCredits.getText().trim()));
        course.setDepartment(txtDepartment.getText().trim());
        course.setDescription(txtDescription.getText().trim());
        
        // Extract lecturer ID
        String selectedLecturer = (String) cmbLecturer.getSelectedItem();
        if (selectedLecturer != null && !selectedLecturer.startsWith("--")) {
            String lecturerId = selectedLecturer.split(" - ")[0];
            course.setLecturerId(lecturerId);
        }
        
        return course;
    }
    
    private boolean validateInputs() {
        if (!ValidationUtil.isNotEmpty(txtCourseId.getText(), "Course ID")) return false;
        if (!ValidationUtil.isNotEmpty(txtCourseName.getText(), "Course Name")) return false;
        if (!ValidationUtil.isNotEmpty(txtCourseCode.getText(), "Course Code")) return false;
        if (!ValidationUtil.isNotEmpty(txtCredits.getText(), "Credits")) return false;
        if (!ValidationUtil.isNotEmpty(txtDepartment.getText(), "Department")) return false;
        
        try {
            Integer.parseInt(txtCredits.getText().trim());
        } catch (NumberFormatException e) {
            ValidationUtil.showError("Credits must be a number!");
            return false;
        }
        
        return true;
    }
    
    private void clearFields() {
        txtCourseId.setText("");
        txtCourseName.setText("");
        txtCourseCode.setText("");
        txtCredits.setText("");
        txtDepartment.setText("");
        txtDescription.setText("");
        cmbLecturer.setSelectedIndex(0);
        txtCourseId.requestFocus();
    }
}