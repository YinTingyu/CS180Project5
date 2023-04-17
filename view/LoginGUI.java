package view;

import core.Customer;
import core.Seller;
import core.User;
import utils.CSVReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginGUI implements ActionListener {

    private static final String SIGNUP_TITLE = "Create Account";
    private static final String REGISTER_SUCCESS_MSG = "Create account successfully!";
    private static final String USERNAME_TAKEN_MSG = "Username already taken!";
    private static final String LOGIN_SUCCEED = "Login successfully";
    private static final String INVALID_NAME_OR_PWD = "Invalid username or password!";

    private static final JPanel loginPanel = new JPanel();

    private static JPanel signUpPanel = new JPanel();

    private static JLabel userLabel;
    static JTextField userText;
    private static JLabel passwordLabel;
    static JTextField passwordText;
    static JLabel success;

    private final Map<String, User> userMap = new HashMap<>();

    public void run() {

        JFrame frame = new JFrame();
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        CardLayout cardLayout = new CardLayout();
        Container container = frame.getContentPane();
        container.setLayout(cardLayout);

        container.add(loginPanel, "loginPanel");


        loginPanel.setLayout(null);

        userLabel = new JLabel("Username");
        userLabel.setBounds(10, 20, 80, 25);
        loginPanel.add(userLabel);

        userText = new JTextField(20);
        userText.setBounds(100, 20, 165, 25);
        loginPanel.add(userText);

        passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(10, 50, 80, 25);
        loginPanel.add(passwordLabel);

        passwordText = new JTextField();
        passwordText.setBounds(100, 50, 165, 25);
        loginPanel.add(passwordText);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(10, 80, 80, 25);
        loginButton.addActionListener(new LoginGUI());
        loginPanel.add(loginButton);

        JButton signUpButton = new JButton("Register");
        signUpButton.setBounds(100, 80, 80, 25);
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createAccountWindow();
            }
        });
        loginPanel.add(signUpButton);

        success = new JLabel("");
        success.setBounds(10, 110, 300, 25);
        loginPanel.add(success);

        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        CSVReader csvReader = new CSVReader();
        Map<String, Customer> customerMap;
        try {
            customerMap = csvReader.readSCustomers();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Map<String, Seller> sellerMap;
        try {
            sellerMap = csvReader.readSellers();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // put all the customers and sellers into a user map
        userMap.putAll(customerMap);
        userMap.putAll(sellerMap);

        String username = userText.getText();
        String password = passwordText.getText();

        User user = userMap.get(username);

        if (userMap.get(username) != null && user.authenticate(password)) {
            success.setText(LOGIN_SUCCEED);
        } else if (actionEvent.equals("Register")) {
            createAccountWindow();
        } else {
            success.setText(INVALID_NAME_OR_PWD);
        }

    }

    private void createAccountWindow() {
        JFrame signUpFrame = new JFrame(SIGNUP_TITLE);
        signUpFrame.setSize(500, 500);
        signUpFrame.setLocationRelativeTo(null);

        signUpPanel = new JPanel();
        signUpFrame.add(signUpPanel);
        signUpPanel.setLayout(null);

        userLabel = new JLabel("Username");
        userLabel.setBounds(10, 20, 80, 25);
        signUpPanel.add(userLabel);

        userText = new JTextField(20);
        userText.setBounds(100, 20, 165, 25);
        signUpPanel.add(userText);

        passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(10, 50, 80, 25);
        signUpPanel.add(passwordLabel);

        passwordText = new JTextField();
        passwordText.setBounds(100, 50, 165, 25);
        signUpPanel.add(passwordText);

        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"Customer", "Seller"});
        roleComboBox.setBounds(100, 80, 100, 25);
        signUpPanel.add(roleComboBox);

        JLabel successLabel = new JLabel("");
        successLabel.setBounds(10, 110, 300, 25);
        signUpPanel.add(successLabel);

        JButton createButton = new JButton("Create");
        createButton.setBounds(10, 140, 80, 25);
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CSVReader csvReader = new CSVReader();
                Map<String, Customer> customerMap;
                try {
                    customerMap = csvReader.readSCustomers();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                Map<String, Seller> sellerMap;
                try {
                    sellerMap = csvReader.readSellers();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                userMap.putAll(customerMap);
                userMap.putAll(sellerMap);

                String username = userText.getText();
                String password = passwordText.getText();

                if (userMap.containsKey(username)) {
                    successLabel.setText(USERNAME_TAKEN_MSG);
                } else {
                    String role = (String) roleComboBox.getSelectedItem();
                    User newUser;
                    if (role.equals("Customer")) {
                        newUser = new Customer(username, password);
                        customerMap.put(username, (Customer) newUser);
                    } else {
                        newUser = new Seller(username, password);
                        sellerMap.put(username, (Seller) newUser);
                    }
                    userMap.put(username, newUser);
                    successLabel.setText(REGISTER_SUCCESS_MSG);
                }
            }
        });
        signUpPanel.add(createButton);

        signUpFrame.setVisible(true);
    }
}
