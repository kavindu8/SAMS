package ui;

import dao.StudentDAO;
import models.Student;
import utils.ValidationUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StudentManagementFrame extends JFrame {
    private JTextField txtStudentId, txtFirstName, txtLastName, txtEmail, txtPhone, txtSearch;
    private JTextArea txtAddress;
    private JComboBox<String> cmbStatus;
    private JTextField txtDOB;
    private JTable tblStudents;
    private DefaultTableModel tableModel;
    private StudentDAO studentDAO;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch;
    
    public StudentManagementFrame() {
        studentDAO = new StudentDAO();
        
        setTitle("Student Management");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        initComponents();
        loadStudents();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
   
        JPanel formPanel = createFormPanel();
        
     
        JPanel tablePanel = createTablePanel();
        
    
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, tablePanel);
        splitPane.setDividerLocation(280);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Student Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
      
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Student ID:"), gbc);
        txtStudentId = new JTextField(15);
        gbc.gridx = 1;
        panel.add(txtStudentId, gbc);
        
      
        gbc.gridx = 2;
        panel.add(new JLabel("First Name:"), gbc);
        txtFirstName = new JTextField(15);
        gbc.gridx = 3;
        panel.add(txtFirstName, gbc);
        
     
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Last Name:"), gbc);
        txtLastName = new JTextField(15);
        gbc.gridx = 1;
        panel.add(txtLastName, gbc);
        
   
        gbc.gridx = 2;
        panel.add(new JLabel("Email:"), gbc);
        txtEmail = new JTextField(15);
        gbc.gridx = 3;
        panel.add(txtEmail, gbc);
        
    
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Phone:"), gbc);
        txtPhone = new JTextField(15);
        gbc.gridx = 1;
        panel.add(txtPhone, gbc);
        
       
        gbc.gridx = 2;
        panel.add(new JLabel("DOB (yyyy-MM-dd):"), gbc);
        txtDOB = new JTextField(15);
        gbc.gridx = 3;
        panel.add(txtDOB, gbc);
        
  
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Status:"), gbc);
        cmbStatus = new JComboBox<>(new String[]{"Active", "Inactive", "Graduated"});
        gbc.gridx = 1;
        panel.add(cmbStatus, gbc);
        
      
        gbc.gridx = 2;
        panel.add(new JLabel("Address:"), gbc);
        txtAddress = new JTextArea(3, 15);
        JScrollPane scrollPane = new JScrollPane(txtAddress);
        gbc.gridx = 3;
        gbc.gridheight = 2;
        panel.add(scrollPane, gbc);
        
     
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 4;
        gbc.gridheight = 1;
        panel.add(createButtonPanel(), gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        btnAdd = new JButton("Add Student");
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> addStudent());
        
        btnUpdate = new JButton("Update");
        btnUpdate.setBackground(new Color(0, 123, 255));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.addActionListener(e -> updateStudent());
        
        btnDelete = new JButton("Delete");
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteStudent());
        
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
        panel.setBorder(BorderFactory.createTitledBorder("Student List"));
        
       
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        txtSearch = new JTextField(20);
        btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> searchStudents());
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        
       
        String[] columns = {"Student ID", "First Name", "Last Name", "Email", "Phone", "DOB", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblStudents = new JTable(tableModel);
        tblStudents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblStudents.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fillFormFromTable();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tblStudents);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void addStudent() {
        if (!validateInputs()) return;
        
        try {
            Student student = createStudentFromForm();
            
            if (studentDAO.addStudent(student)) {
                ValidationUtil.showSuccess("Student added successfully!");
                clearFields();
                loadStudents();
            }
        } catch (SQLException ex) {
            if (ex.getMessage().contains("Duplicate entry")) {
                ValidationUtil.showError("Student ID already exists!");
            } else {
                ValidationUtil.showError("Error: " + ex.getMessage());
            }
        }
    }
    
    private void updateStudent() {
        if (txtStudentId.getText().isEmpty()) {
            ValidationUtil.showError("Please select a student to update!");
            return;
        }
        
        if (!validateInputs()) return;
        
        try {
            Student student = createStudentFromForm();
            
            if (studentDAO.updateStudent(student)) {
                ValidationUtil.showSuccess("Student updated successfully!");
                clearFields();
                loadStudents();
            }
        } catch (SQLException ex) {
            ValidationUtil.showError("Error: " + ex.getMessage());
        }
    }
    
    private void deleteStudent() {
        if (txtStudentId.getText().isEmpty()) {
            ValidationUtil.showError("Please select a student to delete!");
            return;
        }
        
        if (ValidationUtil.showConfirmation("Are you sure you want to delete this student?")) {
            try {
                if (studentDAO.deleteStudent(txtStudentId.getText())) {
                    ValidationUtil.showSuccess("Student deleted successfully!");
                    clearFields();
                    loadStudents();
                }
            } catch (SQLException ex) {
                ValidationUtil.showError("Error: " + ex.getMessage());
            }
        }
    }
    
    private void searchStudents() {
        String keyword = txtSearch.getText().trim();
        
        if (keyword.isEmpty()) {
            loadStudents();
            return;
        }
        
        try {
            List<Student> students = studentDAO.searchStudents(keyword);
            updateTable(students);
        } catch (SQLException ex) {
            ValidationUtil.showError("Search error: " + ex.getMessage());
        }
    }
    
    private void loadStudents() {
        try {
            List<Student> students = studentDAO.getAllStudents();
            updateTable(students);
        } catch (SQLException ex) {
            ValidationUtil.showError("Error loading students: " + ex.getMessage());
        }
    }
    
    private void updateTable(List<Student> students) {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Student student : students) {
            Object[] row = {
                student.getStudentId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getPhone(),
                student.getDateOfBirth() != null ? sdf.format(student.getDateOfBirth()) : "",
                student.getStatus()
            };
            tableModel.addRow(row);
        }
    }
    
    private void fillFormFromTable() {
        int row = tblStudents.getSelectedRow();
        if (row >= 0) {
            txtStudentId.setText(tableModel.getValueAt(row, 0).toString());
            txtFirstName.setText(tableModel.getValueAt(row, 1).toString());
            txtLastName.setText(tableModel.getValueAt(row, 2).toString());
            txtEmail.setText(tableModel.getValueAt(row, 3).toString());
            txtPhone.setText(tableModel.getValueAt(row, 4).toString());
            txtDOB.setText(tableModel.getValueAt(row, 5).toString());
            cmbStatus.setSelectedItem(tableModel.getValueAt(row, 6).toString());
        }
    }
    
    private Student createStudentFromForm() throws SQLException {
        Student student = new Student();
        student.setStudentId(txtStudentId.getText().trim());
        student.setFirstName(txtFirstName.getText().trim());
        student.setLastName(txtLastName.getText().trim());
        student.setEmail(txtEmail.getText().trim());
        student.setPhone(txtPhone.getText().trim());
        student.setStatus(cmbStatus.getSelectedItem().toString());
        student.setAddress(txtAddress.getText().trim());
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dob = sdf.parse(txtDOB.getText().trim());
            student.setDateOfBirth(dob);
        } catch (ParseException e) {
            throw new SQLException("Invalid date format. Use yyyy-MM-dd");
        }
        
        return student;
    }
    
    private boolean validateInputs() {
        if (!ValidationUtil.isNotEmpty(txtStudentId.getText(), "Student ID")) return false;
        if (!ValidationUtil.isValidStudentId(txtStudentId.getText())) {
            ValidationUtil.showError("Student ID must be in format: STU0001");
            return false;
        }
        
        if (!ValidationUtil.isNotEmpty(txtFirstName.getText(), "First Name")) return false;
        if (!ValidationUtil.isNotEmpty(txtLastName.getText(), "Last Name")) return false;
        
        if (!ValidationUtil.isNotEmpty(txtEmail.getText(), "Email")) return false;
        if (!ValidationUtil.isValidEmail(txtEmail.getText())) {
            ValidationUtil.showError("Invalid email format!");
            return false;
        }
        
        if (!ValidationUtil.isNotEmpty(txtPhone.getText(), "Phone")) return false;
        if (!ValidationUtil.isValidPhone(txtPhone.getText())) {
            ValidationUtil.showError("Phone must be 10-15 digits!");
            return false;
        }
        
        if (!ValidationUtil.isNotEmpty(txtDOB.getText(), "Date of Birth")) return false;
        
        return true;
    }
    
    private void clearFields() {
        txtStudentId.setText("");
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtDOB.setText("");
        txtAddress.setText("");
        cmbStatus.setSelectedIndex(0);
        txtStudentId.requestFocus();
    }
}