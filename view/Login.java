package view;

import core.Customer;
import core.Seller;
import core.User;
import utils.CSVReader;
import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Login {

    private static final String WELCOME = "Welcome to our sending message service!";
    private static final String PROMPT_ACTION = "What do you want to do?";
    private static final String LOGIN_CHOICE = "Login";
    private static final String CREATE_ACCOUNT_CHOICE = "Register";
    private static final String ENTER_USERNAME = "Please enter your username: ";
    private static final String ENTER_PASSWORD = "Please enter your password: ";
    private static final String LOGIN_SUCCEED = "Login successfully";
    private static final String INVALID_NAME_OR_PWD = "Invalid username or password!";
    private static final String NAME_ALREADY_TAKEN = "Username already taken!";
    private static final String REGISTER_SUCCEED = "Create account successfully!";
    private static final String EXIT = "Exit";
    private static final String FAREWELL = "GoodBye~";

    public User user;

    public void runLogin() throws IOException {

        CSVReader csvReader = new CSVReader();
        Map<String, Customer> customerMap = csvReader.readSCustomers();
        Map<String, Seller> sellerMap = csvReader.readSellers();

        Map<String, User> userMap = new HashMap<>(); // put all the customers and sellers into a user map
        userMap.putAll(customerMap);
        userMap.putAll(sellerMap);

        JOptionPane.showMessageDialog(null, WELCOME,
                "Login", JOptionPane.INFORMATION_MESSAGE);

        Object[] loginOptions = {LOGIN_CHOICE, CREATE_ACCOUNT_CHOICE, EXIT};
        String[] roleOptions = new String[] {"Customer", "Seller"};
        int role = 3;

        boolean done = false;
        while (!done) {
            int action = JOptionPane.showOptionDialog(null, PROMPT_ACTION, "Login",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, loginOptions, loginOptions[0]);

            switch (action) {
                case 0: // Login
                    String username = JOptionPane.showInputDialog(null, ENTER_USERNAME,
                            "Login", JOptionPane.INFORMATION_MESSAGE);
                    String password = JOptionPane.showInputDialog(null, ENTER_PASSWORD,
                            "Login", JOptionPane.INFORMATION_MESSAGE);

                    user = userMap.get(username);

                    if (userMap.get(username) != null && user.authenticate(password)) {
                        // Login successfully
                        JOptionPane.showMessageDialog(null, LOGIN_SUCCEED,
                                "Login", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, INVALID_NAME_OR_PWD,
                                "Login failed", JOptionPane.ERROR_MESSAGE);

                    }

                    done = true;

                    break;
                case 1: // Create a new account
                    String newUsername = JOptionPane.showInputDialog(null, ENTER_USERNAME,
                            "Sign Up", JOptionPane.INFORMATION_MESSAGE);
                    String newPassword = JOptionPane.showInputDialog(null, ENTER_PASSWORD,
                            "Sign Up", JOptionPane.INFORMATION_MESSAGE);
                    boolean usernameTaken = userMap.containsKey(newUsername);

                    while (usernameTaken) {
                        JOptionPane.showMessageDialog(null, NAME_ALREADY_TAKEN,
                                "Sign Up", JOptionPane.ERROR_MESSAGE);
                        newUsername = JOptionPane.showInputDialog(null, ENTER_USERNAME,
                                "Sign Up", JOptionPane.INFORMATION_MESSAGE);
                        newPassword = JOptionPane.showInputDialog(null, ENTER_PASSWORD,
                                "Sign Up", JOptionPane.INFORMATION_MESSAGE);
                        usernameTaken = userMap.containsKey(newUsername);

                        if (!usernameTaken) {
                            role = JOptionPane.showOptionDialog(null, PROMPT_ACTION, "Sign up",
                                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                                    null, roleOptions, roleOptions[0]);
                            JOptionPane.showMessageDialog(null, REGISTER_SUCCEED,
                                    "Sign Up", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }

                    switch (role) {
                        case 0: // a customer created
                            Customer newCustomer = new Customer(newUsername, newPassword);
                            customerMap.put(newUsername, newCustomer);
                        case 1: // a seller created
                            Seller newSeller = new Seller(newUsername, newPassword);
                            sellerMap.put(newUsername, newSeller);
                    }

                case 2: // Exit
                    JOptionPane.showMessageDialog(null, FAREWELL, "",
                            JOptionPane.INFORMATION_MESSAGE);
                    done = true;
                    break;
            }

        }

    }


}
