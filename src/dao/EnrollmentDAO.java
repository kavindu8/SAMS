package dao;

import config.DatabaseConnection;
import models.Enrollment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {
    private Connection connection;
    
    public EnrollmentDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    public boolean enrollStudent(Enrollment enrollment) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, course_id, semester, " +
                     "academic_year, status) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, enrollment.getStudentId());
            pstmt.setString(2, enrollment.getCourseId());
            pstmt.setString(3, enrollment.getSemester());
            pstmt.setString(4, enrollment.getAcademicYear());
            pstmt.setString(5, enrollment.getStatus());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public List<Enrollment> getAllEnrollments() throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT e.*, CONCAT(s.first_name, ' ', s.last_name) as student_name, " +
                     "c.course_name, c.course_code " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "ORDER BY e.enrollment_id DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Enrollment enrollment = new Enrollment();
                enrollment.setEnrollmentId(rs.getInt("enrollment_id"));
                enrollment.setStudentId(rs.getString("student_id"));
                enrollment.setCourseId(rs.getString("course_id"));
                enrollment.setEnrollmentDate(rs.getDate("enrollment_date"));
                enrollment.setSemester(rs.getString("semester"));
                enrollment.setAcademicYear(rs.getString("academic_year"));
                enrollment.setStatus(rs.getString("status"));
                enrollment.setStudentName(rs.getString("student_name"));
                enrollment.setCourseName(rs.getString("course_name"));
                enrollment.setCourseCode(rs.getString("course_code"));
                enrollments.add(enrollment);
            }
        }
        return enrollments;
    }
    
    public boolean updateEnrollmentStatus(int enrollmentId, String status) throws SQLException {
        String sql = "UPDATE enrollments SET status = ? WHERE enrollment_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, enrollmentId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteEnrollment(int enrollmentId) throws SQLException {
        String sql = "DELETE FROM enrollments WHERE enrollment_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, enrollmentId);
            return pstmt.executeUpdate() > 0;
        }
    }
}