package ui;

import config.DatabaseConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnReset;
    private JLabel lblStatus;
    
    public LoginFrame() {
        setTitle("SAMS - Login");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(false);
        
      
        
        initComponents();
    }
    
    private void initComponents() {
    
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
           
                GradientPaint gp = new GradientPaint(0, 0, new Color(30, 60, 114), 
                                                     getWidth(), getHeight(), new Color(42, 82, 152));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
      
        JPanel leftPanel = createLeftPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(leftPanel, gbc);
        
        
        JPanel rightPanel = createLoginPanel();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(rightPanel, gbc);
        
        add(mainPanel);
    }
    
 
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
               
                GradientPaint gp = new GradientPaint(0, 0, new Color(25, 50, 100), 
                                                     getWidth(), getHeight(), new Color(35, 70, 130));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(50, 40, 50, 40));
        
      
        JLabel lblLogo = new JLabel();
        lblLogo.setFont(new Font("Arial", Font.BOLD, 80));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogo.setForeground(new Color(255, 200, 0));
        
     
        JLabel lblTitle = new JLabel("SAMS");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 48));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
   
        JLabel lblSubtitle = new JLabel("Smart Academic Management System");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(200, 220, 255));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
      
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(100, 150, 200));
        separator.setMaximumSize(new Dimension(300, 2));
        
        
  
        
        panel.add(Box.createVerticalGlue());
        panel.add(lblLogo);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblSubtitle);
        panel.add(Box.createVerticalStrut(30));
        panel.add(separator);
        panel.add(Box.createVerticalStrut(20));
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
   
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
       
        JLabel lblWelcome = new JLabel("Welcome Back");
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 32));
        lblWelcome.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 40, 10, 40);
        panel.add(lblWelcome, gbc);
        
        JLabel lblSubtext = new JLabel("Sign in to your account");
        lblSubtext.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtext.setForeground(new Color(200, 220, 255));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 40, 40, 40);
        panel.add(lblSubtext, gbc);
        
      
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 40, 5, 40);
        
        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Arial", Font.BOLD, 12));
        lblUsername.setForeground(Color.WHITE);
        panel.add(lblUsername, gbc);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 40, 15, 40);
        txtUsername = createModernTextField();
        txtUsername.setPreferredSize(new Dimension(250, 45));
        panel.add(txtUsername, gbc);
        
       
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 40, 5, 40);
        
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 12));
        lblPassword.setForeground(Color.WHITE);
        panel.add(lblPassword, gbc);
        
        gbc.gridy = 5;
        gbc.insets = new Insets(5, 40, 15, 40);
        txtPassword = createModernPasswordField();
        txtPassword.setPreferredSize(new Dimension(250, 45));
        panel.add(txtPassword, gbc);
        
     
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 40, 15, 40);
        lblStatus = new JLabel("");
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(255, 100, 100));
        panel.add(lblStatus, gbc);
        
       
        JPanel buttonPanel = createButtonPanel();
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 40, 20, 40);
        panel.add(buttonPanel, gbc);
        
       
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 40, 20, 40);
        
       
        
       
        txtPassword.addActionListener(e -> performLogin());
        
        return panel;
    }
    

    private JTextField createModernTextField() {
        JTextField textField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }
        };
        
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setForeground(new Color(50, 50, 50));
        textField.setCaretColor(new Color(30, 60, 114));
        textField.setOpaque(true);
        textField.setBackground(new Color(240, 245, 250));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 220), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
    
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(100, 150, 220), 2),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!textField.hasFocus()) {
                    textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(180, 200, 220), 2),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                    ));
                }
            }
        });
        
      
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(30, 120, 220), 3),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 200, 220), 2),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
        });
        
        return textField;
    }
    
  
    private JPasswordField createModernPasswordField() {
        JPasswordField passwordField = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }
        };
        
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setForeground(new Color(50, 50, 50));
        passwordField.setCaretColor(new Color(30, 60, 114));
        passwordField.setOpaque(true);
        passwordField.setBackground(new Color(240, 245, 250));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 220), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
    
        passwordField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(100, 150, 220), 2),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!passwordField.hasFocus()) {
                    passwordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(180, 200, 220), 2),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                    ));
                }
            }
        });
        
 
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(30, 120, 220), 3),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 200, 220), 2),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
        });
        
        return passwordField;
    }
    
  
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setOpaque(false);
        
       
        btnLogin = new JButton("Login") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(20, 60, 150));
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(50, 120, 220));
                } else {
                    g2d.setColor(new Color(30, 100, 200));
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                super.paintComponent(g);
            }
        };
        
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setPreferredSize(new Dimension(120, 45));
        btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> performLogin());
        
       
        btnReset = new JButton("Reset") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(150, 150, 150));
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(180, 180, 180));
                } else {
                    g2d.setColor(new Color(160, 170, 180));
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                super.paintComponent(g);
            }
        };
        
        btnReset.setFont(new Font("Arial", Font.BOLD, 14));
        btnReset.setForeground(Color.WHITE);
        btnReset.setPreferredSize(new Dimension(120, 45));
        btnReset.setContentAreaFilled(false);
        btnReset.setBorderPainted(false);
        btnReset.setFocusPainted(false);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> resetFields());
        
        panel.add(btnLogin);
        panel.add(btnReset);
        
        return panel;
    }
    
    
    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText(" Please enter username and password!");
            lblStatus.setForeground(new Color(255, 100, 100));
            return;
        }
        
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String role = rs.getString("role");
                
              
                if (!role.equals("Lecturer") && !role.equals("Staff")) {
                    lblStatus.setText(" Access Denied! Only Lecturer and Staff accounts allowed.");
                    lblStatus.setForeground(new Color(255, 100, 100));
                    return;
                }
                
                lblStatus.setText(" Login Successful! Welcome " + username);
                lblStatus.setForeground(new Color(100, 200, 100));
                
             
                Timer timer = new Timer(800, e -> {
                    new DashboardFrame(username, role).setVisible(true);
                    this.dispose();
                });
                timer.setRepeats(false);
                timer.start();
                
            } else {
                lblStatus.setText(" Invalid username or password!");
                lblStatus.setForeground(new Color(255, 100, 100));
                txtPassword.setText("");
            }
            
        } catch (SQLException ex) {
            lblStatus.setText("Database Error: " + ex.getMessage());
            lblStatus.setForeground(new Color(255, 100, 100));
        }
    }
    
    private void resetFields() {
        txtUsername.setText("");
        txtPassword.setText("");
        lblStatus.setText("");
        txtUsername.requestFocus();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}