package ui;

import dao.LecturerDAO;
import models.Lecturer;
import utils.ValidationUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class LecturerManagementFrame extends JFrame {
    private JTextField txtLecturerId, txtFirstName, txtLastName, txtEmail, txtPhone, txtDepartment, txtQualification;
    private JTable tblLecturers;
    private DefaultTableModel tableModel;
    private LecturerDAO lecturerDAO;
    
    public LecturerManagementFrame() {
        lecturerDAO = new LecturerDAO();
        
        setTitle("Lecturer Management");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        initComponents();
        loadLecturers();
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
        panel.setBorder(BorderFactory.createTitledBorder("Lecturer Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
      
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Lecturer ID:"), gbc);
        txtLecturerId = new JTextField(15);
        gbc.gridx = 1;
        panel.add(txtLecturerId, gbc);
        
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
        panel.add(new JLabel("Department:"), gbc);
        txtDepartment = new JTextField(15);
        gbc.gridx = 3;
        panel.add(txtDepartment, gbc);
        
     
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Qualification:"), gbc);
        txtQualification = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        panel.add(txtQualification, gbc);
        
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 4;
        panel.add(createButtonPanel(), gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton btnAdd = new JButton("Add Lecturer");
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> addLecturer());
        
        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBackground(new Color(0, 123, 255));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.addActionListener(e -> updateLecturer());
        
        JButton btnDelete = new JButton("Delete");
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteLecturer());
        
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
        panel.setBorder(BorderFactory.createTitledBorder("Lecturer List"));
        
        String[] columns = {"Lecturer ID", "First Name", "Last Name", "Email", "Phone", "Department", "Qualification"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblLecturers = new JTable(tableModel);
        tblLecturers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fillFormFromTable();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tblLecturers);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void addLecturer() {
        if (!validateInputs()) return;
        
        try {
            Lecturer lecturer = createLecturerFromForm();
            
            if (lecturerDAO.addLecturer(lecturer)) {
                ValidationUtil.showSuccess("Lecturer added successfully!");
                clearFields();
                loadLecturers();
            }
        } catch (SQLException ex) {
            ValidationUtil.showError("Error: " + ex.getMessage());
        }
    }
    
    private void updateLecturer() {
        if (txtLecturerId.getText().isEmpty()) {
            ValidationUtil.showError("Please select a lecturer to update!");
            return;
        }
        
        if (!validateInputs()) return;
        
        try {
            Lecturer lecturer = createLecturerFromForm();
            
            if (lecturerDAO.updateLecturer(lecturer)) {
                ValidationUtil.showSuccess("Lecturer updated successfully!");
                clearFields();
                loadLecturers();
            }
        } catch (SQLException ex) {
            ValidationUtil.showError("Error: " + ex.getMessage());
        }
    }
    
    private void deleteLecturer() {
        if (txtLecturerId.getText().isEmpty()) {
            ValidationUtil.showError("Please select a lecturer to delete!");
            return;
        }
        
        if (ValidationUtil.showConfirmation("Are you sure you want to delete this lecturer?")) {
            try {
                if (lecturerDAO.deleteLecturer(txtLecturerId.getText())) {
                    ValidationUtil.showSuccess("Lecturer deleted successfully!");
                    clearFields();
                    loadLecturers();
                }
            } catch (SQLException ex) {
                ValidationUtil.showError("Error: " + ex.getMessage());
            }
        }
    }
    
    private void loadLecturers() {
        try {
            List<Lecturer> lecturers = lecturerDAO.getAllLecturers();
            updateTable(lecturers);
        } catch (SQLException ex) {
            ValidationUtil.showError("Error loading lecturers: " + ex.getMessage());
        }
    }
    
    private void updateTable(List<Lecturer> lecturers) {
        tableModel.setRowCount(0);
        
        for (Lecturer lecturer : lecturers) {
            Object[] row = {
                lecturer.getLecturerId(),
                lecturer.getFirstName(),
                lecturer.getLastName(),
                lecturer.getEmail(),
                lecturer.getPhone(),
                lecturer.getDepartment(),
                lecturer.getQualification()
            };
            tableModel.addRow(row);
        }
    }
    
    private void fillFormFromTable() {
        int row = tblLecturers.getSelectedRow();
        if (row >= 0) {
            txtLecturerId.setText(tableModel.getValueAt(row, 0).toString());
            txtFirstName.setText(tableModel.getValueAt(row, 1).toString());
            txtLastName.setText(tableModel.getValueAt(row, 2).toString());
            txtEmail.setText(tableModel.getValueAt(row, 3).toString());
            txtPhone.setText(tableModel.getValueAt(row, 4).toString());
            txtDepartment.setText(tableModel.getValueAt(row, 5).toString());
            txtQualification.setText(tableModel.getValueAt(row, 6).toString());
        }
    }
    
    private Lecturer createLecturerFromForm() {
        Lecturer lecturer = new Lecturer();
        lecturer.setLecturerId(txtLecturerId.getText().trim());
        lecturer.setFirstName(txtFirstName.getText().trim());
        lecturer.setLastName(txtLastName.getText().trim());
        lecturer.setEmail(txtEmail.getText().trim());
        lecturer.setPhone(txtPhone.getText().trim());
        lecturer.setDepartment(txtDepartment.getText().trim());
        lecturer.setQualification(txtQualification.getText().trim());
        
        return lecturer;
    }
    
    private boolean validateInputs() {
        if (!ValidationUtil.isNotEmpty(txtLecturerId.getText(), "Lecturer ID")) return false;
        if (!ValidationUtil.isNotEmpty(txtFirstName.getText(), "First Name")) return false;
        if (!ValidationUtil.isNotEmpty(txtLastName.getText(), "Last Name")) return false;
        if (!ValidationUtil.isNotEmpty(txtEmail.getText(), "Email")) return false;
        if (!ValidationUtil.isValidEmail(txtEmail.getText())) {
            ValidationUtil.showError("Invalid email format!");
            return false;
        }
        if (!ValidationUtil.isNotEmpty(txtPhone.getText(), "Phone")) return false;
        
        return true;
    }
    
    private void clearFields() {
        txtLecturerId.setText("");
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtDepartment.setText("");
        txtQualification.setText("");
        txtLecturerId.requestFocus();
    }
}