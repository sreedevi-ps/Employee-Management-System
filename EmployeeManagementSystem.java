import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EmployeeManagementSystem extends JFrame {
    private JTextField txtId, txtName, txtAge;
    private JButton btnAdd, btnUpdate, btnDelete, btnView;
    private JTable table;
    private DefaultTableModel tableModel;

    private Connection connection;

    public EmployeeManagementSystem() {
        setTitle("Employee Management System");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        
        txtId = new JTextField(10);
        txtName = new JTextField(10);
        txtAge = new JTextField(10);

        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnView = new JButton("View");

        table = new JTable();
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Age"}, 0);
        table.setModel(tableModel);

        
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("ID:"));
        inputPanel.add(txtId);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(txtName);
        inputPanel.add(new JLabel("Age:"));
        inputPanel.add(txtAge);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnView);

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addEmployee();
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateEmployee();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteEmployee();
            }
        });

        btnView.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewEmployees();
            }
        });
    }

    public void connect() {
        try {
            String url = "jdbc:mysql://localhost:3306/sreedevi";
            String username = "root";
            String password = "12345";
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addEmployee() {
        int id = Integer.parseInt(txtId.getText());
        String name = txtName.getText();
        int age = Integer.parseInt(txtAge.getText());

        try {
            String sql = "INSERT INTO employees (id, name, age) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.setString(2, name);
            statement.setInt(3, age);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new employee was added successfully.");
                clearFields();
                viewEmployees();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEmployee() {
        String input = txtId.getText(); 
        
        if (!input.isEmpty()) {
            try {
                int employeeId = Integer.parseInt(input);
                String name = txtName.getText();
                int age = Integer.parseInt(txtAge.getText());
    
                String sql = "UPDATE employees SET name = ?, age = ? WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, name);
                statement.setInt(2, age);
                statement.setInt(3, employeeId);
    
                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Employee with ID " + employeeId + " was updated successfully.");
                    clearFields();
                    viewEmployees();
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid employee ID. Please enter a valid integer.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Employee ID cannot be empty.");
        }
    }
    
    public void deleteEmployee() {
        String input = txtId.getText(); 
        
        if (!input.isEmpty()) {
            try {
                int employeeId = Integer.parseInt(input);
    
                String sql = "DELETE FROM employees WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, employeeId);
    
                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("Employee with ID " + employeeId + " was deleted successfully.");
                    clearFields();
                    viewEmployees();
                } else {
                    System.out.println("Employee with ID " + employeeId + " does not exist.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid employee ID. Please enter a valid integer.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Employee ID cannot be empty.");
        }
    }

   void viewEmployees() {
        try {
            String sql = "SELECT * FROM employees";
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);

           
            tableModel.setRowCount(0);

           
            while (result.next()) {
                int id = result.getInt("id");
                String name = result.getString("name");
                int age = result.getInt("age");

                Object[] rowData = {id, name, age};
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtAge.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                EmployeeManagementSystem ems = new EmployeeManagementSystem();
                ems.connect();
                ems.setVisible(true);
            }
        });
    }
}
