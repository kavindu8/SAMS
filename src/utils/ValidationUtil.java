package utils;

import javax.swing.JOptionPane;
import java.util.regex.Pattern;

public class ValidationUtil {
    
   
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.matches(emailRegex, email);
    }
    

    public static boolean isValidPhone(String phone) {
        String phoneRegex = "^[0-9]{10}$";
        return Pattern.matches(phoneRegex, phone);
    }
    

    public static boolean isNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            showError(fieldName + " cannot be empty!");
            return false;
        }
        return true;
    }
    
 
    public static boolean isValidStudentId(String studentId) {
        String idRegex = "^S[0-9]{4,}$";
        return Pattern.matches(idRegex, studentId);
    }
    
    
    public static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Validation Error", 
                                      JOptionPane.ERROR_MESSAGE);
    }
    
    // Show success message
    public static void showSuccess(String message) {
        JOptionPane.showMessageDialog(null, message, "Success", 
                                      JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Show confirmation dialog
    public static boolean showConfirmation(String message) {
        int result = JOptionPane.showConfirmDialog(null, message, "Confirm", 
                                                   JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }
}