package dao;

import config.DatabaseConnection;
import models.Lecturer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LecturerDAO {
    private Connection connection;
    
    public LecturerDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    public boolean addLecturer(Lecturer lecturer) throws SQLException {
        String sql = "INSERT INTO lecturers (lecturer_id, first_name, last_name, email, " +
                     "phone, department, qualification) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, lecturer.getLecturerId());
            pstmt.setString(2, lecturer.getFirstName());
            pstmt.setString(3, lecturer.getLastName());
            pstmt.setString(4, lecturer.getEmail());
            pstmt.setString(5, lecturer.getPhone());
            pstmt.setString(6, lecturer.getDepartment());
            pstmt.setString(7, lecturer.getQualification());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public List<Lecturer> getAllLecturers() throws SQLException {
        List<Lecturer> lecturers = new ArrayList<>();
        String sql = "SELECT * FROM lecturers ORDER BY lecturer_id";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Lecturer lecturer = new Lecturer();
                lecturer.setLecturerId(rs.getString("lecturer_id"));
                lecturer.setFirstName(rs.getString("first_name"));
                lecturer.setLastName(rs.getString("last_name"));
                lecturer.setEmail(rs.getString("email"));
                lecturer.setPhone(rs.getString("phone"));
                lecturer.setDepartment(rs.getString("department"));
                lecturer.setQualification(rs.getString("qualification"));
                lecturer.setHireDate(rs.getDate("hire_date"));
                lecturers.add(lecturer);
            }
        }
        return lecturers;
    }
    
    public boolean updateLecturer(Lecturer lecturer) throws SQLException {
        String sql = "UPDATE lecturers SET first_name=?, last_name=?, email=?, " +
                     "phone=?, department=?, qualification=? WHERE lecturer_id=?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, lecturer.getFirstName());
            pstmt.setString(2, lecturer.getLastName());
            pstmt.setString(3, lecturer.getEmail());
            pstmt.setString(4, lecturer.getPhone());
            pstmt.setString(5, lecturer.getDepartment());
            pstmt.setString(6, lecturer.getQualification());
            pstmt.setString(7, lecturer.getLecturerId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteLecturer(String lecturerId) throws SQLException {
        String sql = "DELETE FROM lecturers WHERE lecturer_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, lecturerId);
            return pstmt.executeUpdate() > 0;
        }
    }
}