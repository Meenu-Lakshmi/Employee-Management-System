import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginScreen extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginScreen() {
        // Set up the JFrame
        setTitle("Login");
        setSize(300, 200); // Increase the size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Use BorderLayout for the main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        // Panel for input fields and button
        JPanel inputPanel = new JPanel(new GridLayout(3, 1, 5, 5)); // Add gaps between components
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        // Create components
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");

        // Set button background color
        loginButton.setBackground(Color.GREEN);

        // Set button font color
        loginButton.setForeground(Color.WHITE);

        // Add action listener to the login button
        loginButton.addActionListener(this);

        // Add components to the input panel
        inputPanel.add(new JLabel("Username:"));
        inputPanel.add(usernameField);
        inputPanel.add(new JLabel("Password:"));
        inputPanel.add(passwordField);

        // Create panel for login button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center the button
        buttonPanel.add(loginButton);

        // Add login button panel to the bottom of main panel
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Make the JFrame visible
        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginScreen();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String username = usernameField.getText();
            String password = String.valueOf(passwordField.getPassword());

            // JDBC URL, username, and password
            String jdbcUrl = "jdbc:mysql://localhost:3306/employee_db";
            String dbUsername = "root";
            String dbPassword = "root";

            // Initialize connection object
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;

            try {
                // Load MySQL JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Establish connection to the database
                connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

                // Prepare SQL statement to query admin table
                String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, username);
                statement.setString(2, password);

                // Execute the query
                resultSet = statement.executeQuery();

                // Check if any rows are returned (login successful)
                if (resultSet.next()) {
                    // Close login screen
                    dispose();

                    // Open new menu
                    new MenuScreen();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ClassNotFoundException | SQLException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    // Close the resources
                    if (resultSet != null) resultSet.close();
                    if (statement != null) statement.close();
                    if (connection != null) connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}

class MenuScreen extends JFrame implements ActionListener {
    private JButton addEmployeeButton;
    private JButton deleteEmployeeButton;
    private JButton editEmployeeButton;
    private JButton viewDetailsButton;
    private JButton viewAllDataButton;

    public MenuScreen() {
        setTitle("Menu");
        setSize(300, 400); // Increase the size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding around each button

        // Create buttons
        addEmployeeButton = createStyledButton("Add Employee");
        deleteEmployeeButton = createStyledButton("Delete Employee");
        editEmployeeButton = createStyledButton("Edit Employee Details");
        viewDetailsButton = createStyledButton("View Details");
        viewAllDataButton = createStyledButton("View All Data");

        // Add action listeners
        addEmployeeButton.addActionListener(this);
        deleteEmployeeButton.addActionListener(this);
        editEmployeeButton.addActionListener(this);
        viewDetailsButton.addActionListener(this);
        viewAllDataButton.addActionListener(this);

        // Add buttons to JFrame with constraints
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(addEmployeeButton, gbc);

        gbc.gridy = 1;
        add(deleteEmployeeButton, gbc);

        gbc.gridy = 2;
        add(editEmployeeButton, gbc);

        gbc.gridy = 3;
        add(viewDetailsButton, gbc);

        gbc.gridy = 4;
        add(viewAllDataButton, gbc);

        // Make the JFrame visible
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(Color.GRAY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle button clicks
        if (e.getSource() == addEmployeeButton) {
            // Prompt user to enter employee details including department
            JTextField nameField = new JTextField();
            JTextField ageField = new JTextField();
            JTextField contactField = new JTextField();
            JTextField emailField = new JTextField();
            JTextField designationField = new JTextField();
            JTextField departmentField = new JTextField();

            Object[] message = {
                    "Name:", nameField,
                    "Age:", ageField,
                    "Contact:", contactField,
                    "Email:", emailField,
                    "Designation:", designationField,
                    "Department (IT, HR, Sales, Finance):", departmentField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Add Employee", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                // Get values entered by the user
                String name = nameField.getText();
                int age = Integer.parseInt(ageField.getText());
                int contact = Integer.parseInt(contactField.getText());
                String email = emailField.getText();
                String designation = designationField.getText();
                String department = departmentField.getText();

                // Add employee to respective table based on department
                switch (department.toLowerCase()) {
                    case "it":
                        addEmployeeToTable(name, age, contact, email, designation, "it_emp");
                        break;
                    case "hr":
                        addEmployeeToTable(name, age, contact, email, designation, "hr_emp");
                        break;
                    case "sales":
                        addEmployeeToTable(name, age, contact, email, designation, "sales_emp");
                        break;
                    case "finance":
                        addEmployeeToTable(name, age, contact, email, designation, "finance_emp");
                        break;
                    default:
                        JOptionPane.showMessageDialog(this, "Invalid department entered.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (e.getSource() == deleteEmployeeButton) {
            // Prompt user for employee ID to delete
            String input = JOptionPane.showInputDialog(this, "Enter Employee ID to delete:");
            if (input != null && !input.isEmpty()) {
                int employeeId = Integer.parseInt(input);
                deleteEmployee(employeeId);
            }
        } else if (e.getSource() == editEmployeeButton) {
            // Prompt user for employee ID to edit details
            String input = JOptionPane.showInputDialog(this, "Enter Employee ID to edit details:");
            if (input != null && !input.isEmpty()) {
                int employeeId = Integer.parseInt(input);
                editEmployeeDetails(employeeId);
            }
        } else if (e.getSource() == viewDetailsButton) {
            // Prompt user for employee ID to view details
            String input = JOptionPane.showInputDialog(this, "Enter Employee ID to view details:");
            if (input != null && !input.isEmpty()) {
                int employeeId = Integer.parseInt(input);
                viewEmployeeDetails(employeeId);
            }
        } else if (e.getSource() == viewAllDataButton) {
            // Open new menu to view all data
            new ViewAllDataMenu();
        }
    }

    // Method to add employee to respective table
    private void addEmployeeToTable(String name, int age, int contact, String email, String designation, String tableName) {
        // JDBC URL, username, and password
        String jdbcUrl = "jdbc:mysql://localhost:3306/employee_db";
        String dbUsername = "root";
        String dbPassword = "root";

        // Initialize connection object
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection to the database
            connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

            // Prepare SQL statement to insert employee into respective table
            String sql = "INSERT INTO " + tableName + " (name, age, contact, email, designation) VALUES (?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, name);
            statement.setInt(2, age);
            statement.setInt(3, contact);
            statement.setString(4, email);
            statement.setString(5, designation);

            // Execute the insert query
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int employeeId = generatedKeys.getInt(1);
                    JOptionPane.showMessageDialog(this, "Employee added successfully " + "\n" + " table. Employee ID: " + employeeId);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add employee to " + tableName + " table.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to add employee.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                // Close the resources
                if (generatedKeys != null) generatedKeys.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    private void deleteEmployee(int employeeId) {
        // JDBC URL, username, and password
        String jdbcUrl = "jdbc:mysql://localhost:3306/employee_db";
        String dbUsername = "root";
        String dbPassword = "root";

        // Initialize connection object
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection to the database
            connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

            // Prepare SQL statement to delete employee from respective table
            String tableName = determineTableName(employeeId);
            if (tableName == null) {
                JOptionPane.showMessageDialog(this, "Invalid Employee ID", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String sql = "DELETE FROM " + tableName + " WHERE employee_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, employeeId);

            // Execute the query
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Employee deleted successfully");
            } else {
                JOptionPane.showMessageDialog(this, "Employee with ID " + employeeId + " not found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to delete employee.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                // Close the resources
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private String determineTableName(int employeeId) {
        if (employeeId >= 1000 && employeeId <= 9999) {
            return "hr_emp";
        } else if (employeeId >= 10000 && employeeId <= 99999) {
            return "it_emp";
        } else if (employeeId >= 1 && employeeId <= 999) {
            return "finance_emp";
        } else if (employeeId >= 100000 && employeeId <= 999999) {
            return "sales_emp";
        }
        return null;
    }

    private void editEmployeeDetails(int employeeId) {
        // Determine the table based on employee ID range
        String tableName = determineTableName(employeeId);

        // JDBC URL, username, and password
        String jdbcUrl = "jdbc:mysql://localhost:3306/employee_db";
        String dbUsername = "root";
        String dbPassword = "root";

        // Initialize connection object
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection to the database
            connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

            // Prepare SQL statement to query employee details from respective table
            String sql = "SELECT * FROM " + tableName + " WHERE employee_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, employeeId);

            // Execute the query
            resultSet = statement.executeQuery();

            // Check if any rows are returned (employee found)
            if (resultSet.next()) {
                // Get employee details
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String contact = resultSet.getString("contact");
                String email = resultSet.getString("email");
                String designation = resultSet.getString("designation");

                // Display editable fields
                JTextField nameField = new JTextField(name);
                JTextField ageField = new JTextField(String.valueOf(age));
                JTextField contactField = new JTextField(contact);
                JTextField emailField = new JTextField(email);
                JTextField designationField = new JTextField(designation);

                Object[] message = {
                        "Name:", nameField,
                        "Age:", ageField,
                        "Contact:", contactField,
                        "Email:", emailField,
                        "Designation:", designationField
                };

                int option = JOptionPane.showConfirmDialog(this, message, "Edit Employee Details", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    // Update employee details
                    String updatedName = nameField.getText();
                    int updatedAge = Integer.parseInt(ageField.getText());
                    String updatedContact = contactField.getText();
                    String updatedEmail = emailField.getText();
                    String updatedDesignation = designationField.getText();

                    // Prepare SQL statement to update employee details
                    sql = "UPDATE " + tableName + " SET name = ?, age = ?, contact = ?, email = ?, designation = ? WHERE employee_id = ?";
                    statement = connection.prepareStatement(sql);
                    statement.setString(1, updatedName);
                    statement.setInt(2, updatedAge);
                    statement.setString(3, updatedContact);
                    statement.setString(4, updatedEmail);
                    statement.setString(5, updatedDesignation);
                    statement.setInt(6, employeeId);

                    // Execute the update query
                    int rowsAffected = statement.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Employee details updated successfully");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update employee details", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Employee with ID " + employeeId + " not found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve employee details.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                // Close the resources
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void viewEmployeeDetails(int employeeId) {
        // Determine the table based on employee ID range
        String tableName = determineTableName(employeeId);

        // JDBC URL, username, and password
        String jdbcUrl = "jdbc:mysql://localhost:3306/employee_db";
        String dbUsername = "root";
        String dbPassword = "root";

        // Initialize connection object
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection to the database
            connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

            // Prepare SQL statement to query employee details from respective table
            String sql = "SELECT * FROM " + tableName + " WHERE employee_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, employeeId);

            // Execute the query
            resultSet = statement.executeQuery();

            // Check if any rows are returned (employee found)
            if (resultSet.next()) {
                // Get employee details
                int empId = resultSet.getInt("employee_id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String contact = resultSet.getString("contact");
                String email = resultSet.getString("email");
                String designation = resultSet.getString("designation");

                // Show details in a dialog
                String message = "Employee ID: " + empId + "\n" +
                        "Name: " + name + "\n" +
                        "Age: " + age + "\n" +
                        "Contact: " + contact + "\n" +
                        "Email: " + email + "\n" +
                        "Designation: " + designation;
                JOptionPane.showMessageDialog(this, message, "Employee Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Employee with ID " + employeeId + " not found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve employee details.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                // Close the resources
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void viewAllData() {
        // Open new menu to view all data
        new ViewAllDataMenu();
    }
}

class AddEmployeeFrame extends JFrame implements ActionListener {
    private JTextField nameField;
    private JTextField ageField;
    private JTextField contactField;
    private JTextField emailField;
    private JTextField designationField;
    private JButton addButton;

    private MenuScreen menuScreen;

    public AddEmployeeFrame(MenuScreen menuScreen) {
        setTitle("Add Employee");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        this.menuScreen = menuScreen;

        // Use GridLayout for the main panel
        JPanel mainPanel = new JPanel(new GridLayout(6, 2, 5, 5)); // Add gaps between components
        setContentPane(mainPanel);

        // Create components
        nameField = new JTextField();
        ageField = new JTextField();
        contactField = new JTextField();
        emailField = new JTextField();
        designationField = new JTextField();
        addButton = new JButton("Add");

        // Add action listener to the add button
        addButton.addActionListener(this);

        // Add components to the main panel
        mainPanel.add(new JLabel("Name:"));
        mainPanel.add(nameField);
        mainPanel.add(new JLabel("Age:"));
        mainPanel.add(ageField);
        mainPanel.add(new JLabel("Contact:"));
        mainPanel.add(contactField);
        mainPanel.add(new JLabel("Email:"));
        mainPanel.add(emailField);
        mainPanel.add(new JLabel("Designation:"));
        mainPanel.add(designationField);
        mainPanel.add(new JLabel()); // Empty label for spacing
        mainPanel.add(addButton);

        // Make the JFrame visible
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            // Get employee details from input fields
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());
            String contact = contactField.getText();
            String email = emailField.getText();
            String designation = designationField.getText();

            // Determine the table based on employee age
            String tableName;
            if (age >= 18 && age <= 30) {
                tableName = "hr_emp";
            } else if (age >= 31 && age <= 40) {
                tableName = "it_emp";
            } else if (age >= 41 && age <= 50) {
                tableName = "finance_emp";
            } else if (age >= 51 && age <= 60) {
                tableName = "sales_emp";
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Age", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // JDBC URL, username, and password
            String jdbcUrl = "jdbc:mysql://localhost:3306/employee_db";
            String dbUsername = "root";
            String dbPassword = "root";

            // Initialize connection object
            Connection connection = null;
            PreparedStatement statement = null;

            try {
                // Load MySQL JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Establish connection to the database
                connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

                // Prepare SQL statement to insert employee details into respective table
                String sql = "INSERT INTO " + tableName + " (name, age, contact, email, designation) VALUES (?, ?, ?, ?, ?)";
                statement = connection.prepareStatement(sql);
                statement.setString(1, name);
                statement.setInt(2, age);
                statement.setString(3, contact);
                statement.setString(4, email);
                statement.setString(5, designation);

                // Execute the query
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Employee added successfully");
                    // Refresh the menu to reflect changes
                    menuScreen.dispose();
                    new MenuScreen();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add employee", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ClassNotFoundException | SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to add employee.", "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    // Close the resources
                    if (statement != null) statement.close();
                    if (connection != null) connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}

class ViewAllDataMenu extends JFrame {

    public ViewAllDataMenu() {
        setTitle("View All Data");
        setSize(600, 400); // Adjust the size as needed
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Use GridBagLayout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Create buttons to view data from each table
        JButton hrButton = createStyledButton("View HR Employees");
        JButton itButton = createStyledButton("View IT Employees");
        JButton financeButton = createStyledButton("View Finance Employees");
        JButton salesButton = createStyledButton("View Sales Employees");

        // Add action listeners to the buttons
        hrButton.addActionListener(e -> viewEmployeeData("hr_emp"));
        itButton.addActionListener(e -> viewEmployeeData("it_emp"));
        financeButton.addActionListener(e -> viewEmployeeData("finance_emp"));
        salesButton.addActionListener(e -> viewEmployeeData("sales_emp"));

        // Add buttons to the JFrame
        add(hrButton, gbc);
        add(itButton, gbc);
        add(financeButton, gbc);
        add(salesButton, gbc);

        // Make the JFrame visible
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setBackground(Color.LIGHT_GRAY);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private void viewEmployeeData(String tableName) {
        // JDBC URL, username, and password
        String jdbcUrl = "jdbc:mysql://localhost:3306/employee_db";
        String dbUsername = "root";
        String dbPassword = "root";

        // Initialize connection object
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection to the database
            connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

            // Create a statement
            statement = connection.createStatement();

            // Query data from the specified table
            resultSet = statement.executeQuery("SELECT * FROM " + tableName);

            // Create a JTable to display the data
            JTable table = new JTable(buildTableModel(resultSet));

            // Add the table to a JScrollPane
            JScrollPane scrollPane = new JScrollPane(table);

            // Create a JFrame to display the table
            JFrame frame = new JFrame("View Data: " + tableName);
            frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
            frame.setSize(800, 400); // Adjust the size as needed
            frame.setLocationRelativeTo(null); // Center the frame
            frame.setVisible(true);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve employee data.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                // Close the resources
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        // Get column names
        int columnCount = metaData.getColumnCount();
        String[] columnNames = new String[columnCount];
        for (int column = 1; column <= columnCount; column++) {
            columnNames[column - 1] = metaData.getColumnName(column);
        }

        // Get data
        Object[][] data = new Object[100][columnCount]; // Limiting to 100 rows for demonstration, adjust as needed
        int rowCount = 0;
        while (rs.next() && rowCount < 100) {
            Object[] row = new Object[columnCount];
            for (int column = 1; column <= columnCount; column++) {
                row[column - 1] = rs.getObject(column);
            }
            data[rowCount] = row;
            rowCount++;
        }

        return new DefaultTableModel(data, columnNames);
    }
}
