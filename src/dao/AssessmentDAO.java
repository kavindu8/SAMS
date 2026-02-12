package dao;

import config.DatabaseConnection;
import models.Assessment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssessmentDAO {
    private Connection connection;
    
    public AssessmentDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    public boolean addAssessment(Assessment assessment) throws SQLException {
        String sql = "INSERT INTO assessments (enrollment_id, assessment_type, marks_obtained, " +
                     "max_marks, assessment_date, remarks) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, assessment.getEnrollmentId());
            pstmt.setString(2, assessment.getAssessmentType());
            pstmt.setDouble(3, assessment.getMarksObtained());
            pstmt.setDouble(4, assessment.getMaxMarks());
            pstmt.setDate(5, new java.sql.Date(assessment.getAssessmentDate().getTime()));
            pstmt.setString(6, assessment.getRemarks());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public List<Assessment> getAllAssessments() throws SQLException {
        List<Assessment> assessments = new ArrayList<>();
        String sql = "SELECT a.*, CONCAT(s.first_name, ' ', s.last_name) as student_name, " +
                     "c.course_name " +
                     "FROM assessments a " +
                     "JOIN enrollments e ON a.enrollment_id = e.enrollment_id " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "ORDER BY a.assessment_date DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Assessment assessment = new Assessment();
                assessment.setAssessmentId(rs.getInt("assessment_id"));
                assessment.setEnrollmentId(rs.getInt("enrollment_id"));
                assessment.setAssessmentType(rs.getString("assessment_type"));
                assessment.setMarksObtained(rs.getDouble("marks_obtained"));
                assessment.setMaxMarks(rs.getDouble("max_marks"));
                assessment.setAssessmentDate(rs.getDate("assessment_date"));
                assessment.setRemarks(rs.getString("remarks"));
                assessment.setStudentName(rs.getString("student_name"));
                assessment.setCourseName(rs.getString("course_name"));
                assessments.add(assessment);
            }
        }
        return assessments;
    }
    
    public boolean updateAssessment(Assessment assessment) throws SQLException {
        String sql = "UPDATE assessments SET assessment_type=?, marks_obtained=?, " +
                     "max_marks=?, assessment_date=?, remarks=? WHERE assessment_id=?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, assessment.getAssessmentType());
            pstmt.setDouble(2, assessment.getMarksObtained());
            pstmt.setDouble(3, assessment.getMaxMarks());
            pstmt.setDate(4, new java.sql.Date(assessment.getAssessmentDate().getTime()));
            pstmt.setString(5, assessment.getRemarks());
            pstmt.setInt(6, assessment.getAssessmentId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteAssessment(int assessmentId) throws SQLException {
        String sql = "DELETE FROM assessments WHERE assessment_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, assessmentId);
            return pstmt.executeUpdate() > 0;
        }
    }
}