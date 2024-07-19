import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class RegistrationForm extends JFrame {
    private final JTextField nameField;
    private final JTextField mobileField;
    private final JRadioButton maleButton;
    private final JRadioButton femaleButton;
    private final JComboBox<String> dayComboBox;
    private final JComboBox<String> monthComboBox;
    private final JComboBox<String> yearComboBox;
    private final JTextArea addressArea;
    private final JCheckBox termsCheckBox;
    private final JButton submitButton;
    private final JButton resetButton;
    private final JTextArea displayArea;

    public RegistrationForm() {
        // Create and set up the window
        setTitle("Registration Form");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 2));

        // Create left panel for form inputs
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(10, 2));

        leftPanel.add(new JLabel("Name"));
        nameField = new JTextField();
        leftPanel.add(nameField);

        leftPanel.add(new JLabel("Mobile"));
        mobileField = new JTextField();
        leftPanel.add(mobileField);

        leftPanel.add(new JLabel("Gender"));
        JPanel genderPanel = new JPanel();
        maleButton = new JRadioButton("Male");
        femaleButton = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);
        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);
        leftPanel.add(genderPanel);

        leftPanel.add(new JLabel("DOB"));
        JPanel dobPanel = new JPanel();
        dayComboBox = new JComboBox<>(generateDays());
        monthComboBox = new JComboBox<>(generateMonths());
        yearComboBox = new JComboBox<>(generateYears());
        dobPanel.add(dayComboBox);
        dobPanel.add(monthComboBox);
        dobPanel.add(yearComboBox);
        leftPanel.add(dobPanel);

        leftPanel.add(new JLabel("Address"));
        addressArea = new JTextArea(3, 20);
        leftPanel.add(new JScrollPane(addressArea));

        termsCheckBox = new JCheckBox("Accept Terms And Conditions");
        leftPanel.add(termsCheckBox);

        submitButton = new JButton("Submit");
        resetButton = new JButton("Reset");
        leftPanel.add(submitButton);
        leftPanel.add(resetButton);

        add(leftPanel);

        // Create right panel for data display
        JPanel rightPanel = new JPanel();
        displayArea = new JTextArea(20, 20);
        displayArea.setEditable(false);
        rightPanel.add(new JScrollPane(displayArea));
        add(rightPanel);

        // Add action listeners
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitForm();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetForm();
            }
        });

        setVisible(true);
    }

    private String[] generateYears() {
        String[] years = new String[105]; // Corrected size to 105 to accommodate 1920 to 2024 inclusive
        for (int i = 1920; i <= 2024; i++) {
            years[i - 1920] = String.valueOf(i);
        }
        return years;
    }

    private String[] generateMonths() {
        String[] months = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };
        return months;
    }


    private String[] generateDays() {
        String[] days = new String[31];
        for (int i = 1; i <= 31; i++) {
            days[i - 1] = String.valueOf(i);
        }
        return days;
    }


    private void submitForm() {
        String name = nameField.getText();
        String mobile = mobileField.getText();
        String gender = maleButton.isSelected() ? "Male" : "Female";
        String day = (String) dayComboBox.getSelectedItem();
        String month = (String) monthComboBox.getSelectedItem();
        String year = (String) yearComboBox.getSelectedItem();
        String dob = year + "-" + month + "-" + day;
        String address = addressArea.getText();


        if (!termsCheckBox.isSelected()) {
            JOptionPane.showMessageDialog(this, "Please accept terms and conditions.");
            return;
        }

        try (var conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/registration_db", "root", "")) {
            String sql = "INSERT INTO Users (name, mobile, gender, dob, address) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, mobile);
            stmt.setString(3, gender);
            stmt.setDate(4, Date.valueOf(dob));
            stmt.setString(5, address);
            stmt.executeUpdate();

            displayData();
            JOptionPane.showMessageDialog(this, "Data saved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving data.");
        }
    }

    private void resetForm() {
        nameField.setText("");
        mobileField.setText("");
        genderGroup.clearSelection();
        dayComboBox.setSelectedIndex(0);
        monthComboBox.setSelectedIndex(0);
        yearComboBox.setSelectedIndex(0);
        addressArea.setText("");
        termsCheckBox.setSelected(false);
    }

    private void displayData() {
        displayArea.setText("");
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/registration_db", "root", "")) {
            String sql = "SELECT * FROM Users";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String row = String.format("ID: %d, Name: %s, Mobile: %s, Gender: %s, DOB: %s, Address: %s\n",
                        rs.getInt("id"), rs.getString("name"), rs.getString("mobile"), rs.getString("gender"),
                        rs.getDate("dob"), rs.getString("address"));
                displayArea.append(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving data.");
        }
    }

    public static void main(String[] args) {
        new RegistrationForm();
    }

    private static class genderGroup {

        public genderGroup() {
        }

        public static void clearSelection() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'clearSelection'");
        }
    }
}