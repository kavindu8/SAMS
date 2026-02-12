package dao;

import config.DatabaseConnection;
import models.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    private Connection connection;
    
    public StudentDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
 
    public boolean addStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (student_id, first_name, last_name, email, " +
                     "phone, date_of_birth, address, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, student.getStudentId());
            pstmt.setString(2, student.getFirstName());
            pstmt.setString(3, student.getLastName());
            pstmt.setString(4, student.getEmail());
            pstmt.setString(5, student.getPhone());
            pstmt.setDate(6, new java.sql.Date(student.getDateOfBirth().getTime()));
            pstmt.setString(7, student.getAddress());
            pstmt.setString(8, student.getStatus());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SQLException("Error adding student: " + e.getMessage());
        }
    }
    

    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY enrollment_date DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getString("student_id"));
                student.setFirstName(rs.getString("first_name"));
                student.setLastName(rs.getString("last_name"));
                student.setEmail(rs.getString("email"));
                student.setPhone(rs.getString("phone"));
                student.setDateOfBirth(rs.getDate("date_of_birth"));
                student.setStatus(rs.getString("status"));
                student.setAddress(rs.getString("address"));
                students.add(student);
            }
        }
        return students;
    }
    

    public boolean updateStudent(Student student) throws SQLException {
        String sql = "UPDATE students SET first_name=?, last_name=?, email=?, " +
                     "phone=?, date_of_birth=?, address=?, status=? WHERE student_id=?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getLastName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getPhone());
            pstmt.setDate(5, new java.sql.Date(student.getDateOfBirth().getTime()));
            pstmt.setString(6, student.getAddress());
            pstmt.setString(7, student.getStatus());
            pstmt.setString(8, student.getStudentId());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteStudent(String studentId) throws SQLException {
        String sql = "DELETE FROM students WHERE student_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
   
    public List<Student> searchStudents(String keyword) throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE student_id LIKE ? OR first_name LIKE ? " +
                     "OR last_name LIKE ? OR email LIKE ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getString("student_id"));
                student.setFirstName(rs.getString("first_name"));
                student.setLastName(rs.getString("last_name"));
                student.setEmail(rs.getString("email"));
                student.setPhone(rs.getString("phone"));
                student.setDateOfBirth(rs.getDate("date_of_birth"));
                student.setStatus(rs.getString("status"));
                students.add(student);
            }
        }
        return students;
    }
}