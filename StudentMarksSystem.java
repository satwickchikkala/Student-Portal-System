package src;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentMarksSystem extends Frame implements ActionListener {
    // Form components
    TextField nameField, regNoField, sub1Field, sub2Field, sub3Field;
    Button okButton;
    Label resultLabel;
    
    // Database connection details
    final String DB_URL = "jdbc:mysql://localhost:3306/studentdb";
    final String USER = "studentuser";
    final String PASS = "studentpass"; // Change to your MySQL password
    
    public StudentMarksSystem() {
        // Set up the frame
        setTitle("Student Marks Management System");
        setSize(500, 400);
        setLayout(new GridLayout(8, 2));
        
        // Add components
        add(new Label("Name:"));
        nameField = new TextField();
        add(nameField);
        
        add(new Label("Registration Number:"));
        regNoField = new TextField();
        add(regNoField);
        
        add(new Label("Subject 1 Marks (out of 100):"));
        sub1Field = new TextField();
        add(sub1Field);
        
        add(new Label("Subject 2 Marks (out of 100):"));
        sub2Field = new TextField();
        add(sub2Field);
        
        add(new Label("Subject 3 Marks (out of 100):"));
        sub3Field = new TextField();
        add(sub3Field);
        
        // Add empty label for spacing
        add(new Label(""));
        add(new Label(""));
        
        okButton = new Button("OK");
        okButton.addActionListener(this);
        add(okButton);
        
        resultLabel = new Label("");
        add(resultLabel);
        
        // Handle window closing
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == okButton) {
            try {
                // Get input values
                String name = nameField.getText();
                String regNo = regNoField.getText();
                int sub1 = Integer.parseInt(sub1Field.getText());
                int sub2 = Integer.parseInt(sub2Field.getText());
                int sub3 = Integer.parseInt(sub3Field.getText());
                
                // Validate marks (0-100)
                if (sub1 < 0 || sub1 > 100 || sub2 < 0 || sub2 > 100 || sub3 < 0 || sub3 > 100) {
                    resultLabel.setText("Marks should be between 0 and 100");
                    return;
                }
                
                // Calculate percentage and grade
                double percentage = (sub1 + sub2 + sub3) / 3.0;
                String grade = calculateGrade(percentage);
                
                // Store in database
                saveToDatabase(name, regNo, sub1, sub2, sub3, percentage, grade);
                
                // Show result
                resultLabel.setText(String.format("Percentage: %.2f%%, Grade: %s", percentage, grade));
                
            } catch (NumberFormatException e) {
                resultLabel.setText("Please enter valid numbers for marks");
            } catch (SQLException e) {
                resultLabel.setText("Database error: " + e.getMessage());
            }
        }
    }
    
    private String calculateGrade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B+";
        if (percentage >= 60) return "B";
        if (percentage >= 50) return "C";
        if (percentage >= 40) return "D";
        return "F";
    }
    
    private void saveToDatabase(String name, String regNo, int sub1, int sub2, int sub3, 
                          double percentage, String grade) throws SQLException {
    // Explicitly load driver
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        System.out.println("MySQL JDBC Driver Registered!");
    } catch (ClassNotFoundException e) {
        throw new SQLException("MySQL JDBC Driver not found", e);
    }
    
    // Update connection URL with timezone
    String DB_URL = "jdbc:mysql://localhost:3306/studentdb?useSSL=false&serverTimezone=UTC";
    
    try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
        // Rest of your database code...
    }
}
        
        // Connect to database
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Create table if not exists
            String createTableSQL = "CREATE TABLE IF NOT EXISTS student_marks (" +
                                   "id INT AUTO_INCREMENT PRIMARY KEY, " +
                                   "name VARCHAR(100), " +
                                   "reg_no VARCHAR(20), " +
                                   "subject1 INT, " +
                                   "subject2 INT, " +
                                   "subject3 INT, " +
                                   "percentage DOUBLE, " +
                                   "grade VARCHAR(5), " +
                                   "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
            }
            
            // Insert data
            String insertSQL = "INSERT INTO student_marks (name, reg_no, subject1, subject2, subject3, percentage, grade) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                pstmt.setString(1, getName());
                pstmt.setString(2, getregNo);
                pstmt.setInt(3, getsub1);
                pstmt.setInt(4, sub2);
                pstmt.setInt(5, sub3);
                pstmt.setDouble(6, percentage);
                pstmt.setString(7, grade);
                
                pstmt.executeUpdate();
            }
        }
    }
    
    public static void main(String[] args) {
        new StudentMarksSystem();
    }
}