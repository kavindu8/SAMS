package ui;

import config.DatabaseConnection;
import utils.ValidationUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class LecturerAllocationFrame extends JFrame {
    private JComboBox<String> cmbCourse, cmbLecturer;
    private JTable tblAllocations;
    private DefaultTableModel tableModel;
    private Connection connection;
    
    public LecturerAllocationFrame() {
        connection = DatabaseConnection.getInstance().getConnection();
        
        setTitle("Lecturer Allocation to Courses");
        setSize(900, 600);
        setLocationRelativeTo(null);
        
        initComponents();
        loadCourses();
        loadLecturers();
        loadAllocations();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel formPanel = createFormPanel();
        JPanel tablePanel = createTablePanel();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, tablePanel);
        splitPane.setDividerLocation(150);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Allocate Lecturer to Course"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Course Selection
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Select Course:"), gbc);
        cmbCourse = new JComboBox<>();
        cmbCourse.setPreferredSize(new Dimension(300, 30));
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(cmbCourse, gbc);
        
        // Lecturer Selection
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Select Lecturer:"), gbc);
        cmbLecturer = new JComboBox<>();
        cmbLecturer.setPreferredSize(new Dimension(300, 30));
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(cmbLecturer, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 3;
        panel.add(createButtonPanel(), gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton btnAssign = new JButton("Assign Lecturer");
        btnAssign.setBackground(new Color(40, 167, 69));
        btnAssign.setForeground(Color.WHITE);
        btnAssign.addActionListener(e -> assignLecturer());
        
        JButton btnRemove = new JButton("Remove Allocation");
        btnRemove.setBackground(new Color(220, 53, 69));
        btnRemove.setForeground(Color.WHITE);
        btnRemove.addActionListener(e -> removeAllocation());
        
        JButton btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> clearFields());
        
        panel.add(btnAssign);
        panel.add(btnRemove);
        panel.add(btnClear);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Current Allocations"));
        
        String[] columns = {"Course ID", "Course Name", "Course Code", "Department", "Lecturer Name"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblAllocations = new JTable(tableModel);
        tblAllocations.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fillFormFromTable();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tblAllocations);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadCourses() {
        try {
            cmbCourse.removeAllItems();
            cmbCourse.addItem("-- Select Course --");
            
            String sql = "SELECT course_id, course_name FROM courses ORDER BY course_name";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                int courseId = rs.getInt("course_id");
                String courseName = rs.getString("course_name");
                cmbCourse.addItem(courseId + " - " + courseName);
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading courses: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void loadLecturers() {
        try {
            cmbLecturer.removeAllItems();
            cmbLecturer.addItem("-- Select Lecturer --");
            
            String sql = "SELECT user_id, username FROM users WHERE role = 'Lecturer' ORDER BY username";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                int lecturerId = rs.getInt("user_id");
                String username = rs.getString("username");
                cmbLecturer.addItem(lecturerId + " - " + username);
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading lecturers: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void assignLecturer() {
        if (cmbCourse.getSelectedIndex() == 0) {
            ValidationUtil.showError("Please select a course!");
            return;
        }
        if (cmbLecturer.getSelectedIndex() == 0) {
            ValidationUtil.showError("Please select a lecturer!");
            return;
        }
        
        try {
            String selectedCourse = (String) cmbCourse.getSelectedItem();
            int courseId = Integer.parseInt(selectedCourse.split(" - ")[0]);
            
            String selectedLecturer = (String) cmbLecturer.getSelectedItem();
            int lecturerId = Integer.parseInt(selectedLecturer.split(" - ")[0]);
            
            String sql = "UPDATE courses SET lecturer_id = ? WHERE course_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, lecturerId);
            pstmt.setInt(2, courseId);
            
            if (pstmt.executeUpdate() > 0) {
                ValidationUtil.showSuccess("Lecturer assigned to course successfully!");
                clearFields();
                loadAllocations();
                loadCourses();
            }
            
            pstmt.close();
            
        } catch (SQLException ex) {
            ValidationUtil.showError("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void removeAllocation() {
        int row = tblAllocations.getSelectedRow();
        if (row < 0) {
            ValidationUtil.showError("Please select a course allocation to remove!");
            return;
        }
        
        if (ValidationUtil.showConfirmation("Are you sure you want to remove this allocation?")) {
            try {
                int courseId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                
                String sql = "UPDATE courses SET lecturer_id = NULL WHERE course_id = ?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, courseId);
                
                if (pstmt.executeUpdate() > 0) {
                    ValidationUtil.showSuccess("Allocation removed successfully!");
                    loadAllocations();
                    loadCourses();
                }
                
                pstmt.close();
                
            } catch (SQLException ex) {
                ValidationUtil.showError("Error: " + ex.getMessage());
            }
        }
    }
    
    private void loadAllocations() {
        try {
            tableModel.setRowCount(0);
            
            String sql = "SELECT c.course_id, c.course_name, c.course_code, c.department, " +
                        "COALESCE(u.username, '❌ Not Assigned') as lecturer_name, " +
                        "'' as qualification " +
                        "FROM courses c " +
                        "LEFT JOIN users u ON c.lecturer_id = u.user_id " +
                        "ORDER BY c.course_name";
            
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("course_id"),
                    rs.getString("course_name"),
                    rs.getString("course_code"),
                    rs.getString("department"),
                    rs.getString("lecturer_name"),
                    ""
                };
                tableModel.addRow(row);
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException ex) {
            ValidationUtil.showError("Error loading allocations: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void fillFormFromTable() {
        int row = tblAllocations.getSelectedRow();
        if (row >= 0) {
            String courseId = tableModel.getValueAt(row, 0).toString();
            
            for (int i = 0; i < cmbCourse.getItemCount(); i++) {
                String item = (String) cmbCourse.getItemAt(i);
                if (item.startsWith(courseId + " - ")) {
                    cmbCourse.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
    
    private void clearFields() {
        cmbCourse.setSelectedIndex(0);
        cmbLecturer.setSelectedIndex(0);
    }
}