package dao;

import config.DatabaseConnection;
import models.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    private Connection connection;
    
    public CourseDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    public boolean addCourse(Course course) throws SQLException {
        String sql = "INSERT INTO courses (course_id, course_name, course_code, credits, " +
                     "department, description, lecturer_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, course.getCourseId());
            pstmt.setString(2, course.getCourseName());
            pstmt.setString(3, course.getCourseCode());
            pstmt.setInt(4, course.getCredits());
            pstmt.setString(5, course.getDepartment());
            pstmt.setString(6, course.getDescription());
            pstmt.setString(7, course.getLecturerId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public List<Course> getAllCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.*, CONCAT(l.first_name, ' ', l.last_name) as lecturer_name " +
                     "FROM courses c LEFT JOIN lecturers l ON c.lecturer_id = l.lecturer_id " +
                     "ORDER BY c.course_code";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Course course = new Course();
                course.setCourseId(rs.getString("course_id"));
                course.setCourseName(rs.getString("course_name"));
                course.setCourseCode(rs.getString("course_code"));
                course.setCredits(rs.getInt("credits"));
                course.setDepartment(rs.getString("department"));
                course.setDescription(rs.getString("description"));
                course.setLecturerId(rs.getString("lecturer_id"));
                course.setLecturerName(rs.getString("lecturer_name"));
                courses.add(course);
            }
        }
        return courses;
    }
    
    public boolean updateCourse(Course course) throws SQLException {
        String sql = "UPDATE courses SET course_name=?, course_code=?, credits=?, " +
                     "department=?, description=?, lecturer_id=? WHERE course_id=?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, course.getCourseName());
            pstmt.setString(2, course.getCourseCode());
            pstmt.setInt(3, course.getCredits());
            pstmt.setString(4, course.getDepartment());
            pstmt.setString(5, course.getDescription());
            pstmt.setString(6, course.getLecturerId());
            pstmt.setString(7, course.getCourseId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteCourse(String courseId) throws SQLException {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, courseId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean assignLecturer(String courseId, String lecturerId) throws SQLException {
        String sql = "UPDATE courses SET lecturer_id = ? WHERE course_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, lecturerId);
            pstmt.setString(2, courseId);
            return pstmt.executeUpdate() > 0;
        }
    }
}