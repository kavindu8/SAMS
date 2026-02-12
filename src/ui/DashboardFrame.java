package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import config.DatabaseConnection;
import java.sql.*;

public class DashboardFrame extends JFrame {
    private String username;
    private String userRole;
    private JLabel lblTotalStudents, lblTotalCourses, lblActiveEnrollments;
    private JPanel contentPanel;
    
    public DashboardFrame(String username, String userRole) {
        this.username = username;
        this.userRole = userRole;
        
        setTitle("SAMS - Dashboard");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        
        initComponents();
        loadStatistics();
    }
    
    private void initComponents() {

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gp = new GradientPaint(0, 0, new Color(240, 245, 250),
                                                     getWidth(), getHeight(), new Color(220, 235, 250));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
       
        JPanel navbar = createNavbar();
        mainPanel.add(navbar, BorderLayout.NORTH);
        
   
        JPanel sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);
        
  
        contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
 
    private JPanel createNavbar() {
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
                
               
                g2d.setColor(new Color(15, 40, 90));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        
        panel.setLayout(new BorderLayout(20, 0));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        panel.setPreferredSize(new Dimension(0, 70));
        
     
        JLabel lblTitle = new JLabel(" SAMS Dashboard");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        panel.add(lblTitle, BorderLayout.WEST);
        
    
        panel.add(Box.createHorizontalBox(), BorderLayout.CENTER);
        
       
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        
     
        JLabel lblUserInfo = new JLabel(  username + " (" + userRole + ")");
        lblUserInfo.setFont(new Font("Arial", Font.PLAIN, 13));
        lblUserInfo.setForeground(new Color(200, 220, 255));
        rightPanel.add(lblUserInfo);
        
     
        JButton btnLogout = createModernButton("Logout", new Color(220, 53, 69), new Color(255, 80, 100));
        btnLogout.addActionListener(e -> logout());
        rightPanel.add(btnLogout);
        
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    
    private JPanel createSidebar() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(245, 248, 252));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
             
                g2d.setColor(new Color(200, 210, 225));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
            }
        };
        
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 15, 20, 15));
        panel.setPreferredSize(new Dimension(250, 0));
        
    
        if (userRole.equals("Staff")) {
            panel.add(createMenuSection("STUDENT MANAGEMENT", new MenuItem[]{
                new MenuItem(" Manage Students", () -> new StudentManagementFrame().setVisible(true)),
            }));
            
            panel.add(createMenuSection("ENROLLMENT", new MenuItem[]{
                new MenuItem(" Course Registration", () -> new EnrollmentFrame().setVisible(true)),
            }));
            
            panel.add(createMenuSection("ATTENDANCE", new MenuItem[]{
                new MenuItem(" Mark Attendance", () -> new AttendanceFrame().setVisible(true)),
            }));
            
            panel.add(createMenuSection("ALLOCATION", new MenuItem[]{
                new MenuItem(" Assign Lecturers", () -> new LecturerAllocationFrame().setVisible(true)),
            }));
        } 
        else if (userRole.equals("Lecturer")) {
            panel.add(createMenuSection("ASSESSMENTS", new MenuItem[]{
                new MenuItem(" Manage Results", () -> new AssessmentFrame().setVisible(true)),
            }));
            
            panel.add(createMenuSection("REPORTS", new MenuItem[]{
                new MenuItem("View Reports", () -> new ReportsFrame().setVisible(true)),
            }));
        }
        
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JPanel createMenuSection(String title, MenuItem[] menuItems) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setBorder(new EmptyBorder(0, 0, 20, 0));
        
     
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 11));
        lblTitle.setForeground(new Color(100, 120, 150));
        lblTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        section.add(lblTitle);
        
     
        for (MenuItem item : menuItems) {
            JButton btn = createMenuButton(item.getName(), item.getAction());
            section.add(btn);
            section.add(Box.createVerticalStrut(8));
        }
        
        return section;
    }
    
    private JButton createMenuButton(String text, Runnable action) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (hovered || getModel().isPressed()) {
                    g2d.setColor(new Color(200, 220, 245));
                } else {
                    g2d.setColor(Color.WHITE);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
              
                g2d.setColor(new Color(200, 210, 225));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                
                super.paintComponent(g);
            }
        };
        
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        btn.setForeground(new Color(40, 60, 100));
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btn.repaint();
            }
        });
        
        btn.addActionListener(e -> action.run());
        
        return btn;
    }
    

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
   
        JPanel welcomeSection = createWelcomeSection();
        panel.add(welcomeSection, BorderLayout.NORTH);
        
   
        JPanel statsSection = createStatsSection();
        panel.add(statsSection, BorderLayout.CENTER);
        
   
     
     
        
        return panel;
    }
    
    private JPanel createWelcomeSection() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gp = new GradientPaint(0, 0, new Color(50, 100, 200),
                                                     getWidth(), getHeight(), new Color(80, 130, 230));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        panel.setPreferredSize(new Dimension(0, 120));
        
        JLabel lblWelcome = new JLabel("Welcome, " + username + "!");
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 32));
        lblWelcome.setForeground(Color.WHITE);
        
        JLabel lblRole = new JLabel("Role: " + userRole);
        lblRole.setFont(new Font("Arial", Font.PLAIN, 14));
        lblRole.setForeground(new Color(220, 235, 255));
        
        panel.add(lblWelcome);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblRole);
        
        return panel;
    }
    
    private JPanel createStatsSection() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(30, 0, 30, 0));
        
        lblTotalStudents = createStatCard("Total Students", "0", new Color(52, 152, 219), "👥");
        lblTotalCourses = createStatCard("Total Courses", "0", new Color(46, 204, 113), "📚");
        lblActiveEnrollments = createStatCard("Active Enrollments", "0", new Color(155, 89, 182), "📝");
        
        panel.add(lblTotalStudents);
        panel.add(lblTotalCourses);
        panel.add(lblActiveEnrollments);
        
        return panel;
    }
    
    private JLabel createStatCard(String title, String value, Color color, String icon) {
        JLabel card = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
            
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
         
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, getWidth(), 5, 15, 15);
                
             
                g2d.setColor(new Color(220, 230, 240));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                
              
                super.paintComponent(g);
            }
        };
        
        card.setText("<html><center><font size='6'>" + icon + "</font><br><font size='5' color='#000000'><b>" + value + 
                     "</b></font><br><font size='2' color='#666666'>" + title + "</font></center></html>");
        card.setOpaque(false);
        card.setFont(new Font("Arial", Font.BOLD, 32));
        card.setForeground(color);
        card.setHorizontalAlignment(SwingConstants.CENTER);
        card.setVerticalAlignment(SwingConstants.CENTER);
        card.setBorder(new EmptyBorder(30, 20, 30, 20));
        
        return card;
    }
    
    
    
   
 
    private JButton createModernButton(String text, Color normalColor, Color hoverColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bgColor = getModel().isPressed() ? normalColor.darker() : 
                               (getModel().isRollover() ? hoverColor : normalColor);
                
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                super.paintComponent(g);
            }
        };
        
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(110, 38));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return btn;
    }
    
    private void loadStatistics() {
        new Thread(() -> {
            try {
                Connection conn = DatabaseConnection.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                
              
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM students WHERE status='Active'");
                if (rs.next()) {
                    updateStatCard(lblTotalStudents, "Total Students", rs.getString("count"), "👥");
                }
                
             
                rs = stmt.executeQuery("SELECT COUNT(*) as count FROM courses");
                if (rs.next()) {
                    updateStatCard(lblTotalCourses, "Total Courses", rs.getString("count"), "📚");
                }
                
               
                rs = stmt.executeQuery("SELECT COUNT(*) as count FROM enrollments WHERE status='Enrolled'");
                if (rs.next()) {
                    updateStatCard(lblActiveEnrollments, "Active Enrollments", rs.getString("count"), "📝");
                }
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error loading statistics: " + ex.getMessage());
            }
        }).start();
    }
    
    private void updateStatCard(JLabel card, String title, String value, String icon) {
        SwingUtilities.invokeLater(() -> {
            card.setText("<html><center><font size='6'>" + icon + "</font><br><font size='5' color='#000000'><b>" + 
                        value + "</b></font><br><font size='2' color='#666666'>" + title + "</font></center></html>");
        });
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Confirm Logout", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            this.dispose();
        }
    }
    
   
    private static class MenuItem {
        private String name;
        private Runnable action;
        
        public MenuItem(String name, Runnable action) {
            this.name = name;
            this.action = action;
        }
        
        public String getName() {
            return name;
        }
        
        public Runnable getAction() {
            return action;
        }
    }
}