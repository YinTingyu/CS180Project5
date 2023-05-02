package view;

import core.Customer;
import core.Seller;
import core.User;
import utils.CSVReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * A login GUI
 *
 * <p>Purdue University -- CS18000 -- Spring 2023 -- project 5
 *
 * @author Tingyu Yin
 * @version April 18, 2023
 */
public class LoginGUI implements ActionListener {

    private static final String CREATE_NEW_ACCOUNT_OPTION_CODE = "AA01";
    private static final String LOG_IN_OPTION_CODE = "AA02";
    private static final String EXIT_FIRST_MENU_OPTION_CODE = "AA03";

    private static final String CONTACT_USER_CODE = "BB01";
    private static final String BLOCK_USER_CODE = "BB02";
    private static final String SET_INVISIBLE_CODE = "BB03";
    private static final String VIEW_DASHBOARD_CODE = "BB04";
    private static final String EXPORT_FILE_CODE = "BB05";
    private static final String CREATE_STORE_CODE = "BB06";

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
    static JPasswordField passwordText;
    static JLabel success;

    private Socket socket;
    BufferedReader bfr;
    PrintWriter pw;

    public LoginGUI(Socket socket)
    {
        this.socket = socket;
        try{
            bfr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private final Map<String, User> userMap = new HashMap<>();

    public void openCustomerMenu(Customer customer) {
        CustomerMenu customerMenu = new CustomerMenu(customer, socket);
        try {
            customerMenu.showCustomerMenu(customer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openSellerMenu(Seller seller, Socket socket) {
        System.out.println("open seller");
        SellerMenu sellerMenu = new SellerMenu(seller, socket);
        try {
            sellerMenu.showSellerMenu(seller);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {

        JFrame frame = new JFrame();
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

        passwordText = new JPasswordField();
        passwordText.setBounds(100, 50, 165, 25);
        loginPanel.add(passwordText);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(10, 80, 80, 25);
        loginButton.addActionListener(new LoginGUI(socket));
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
            customerMap = csvReader.readCustomers();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Map<String, Seller> sellerMap;
        try {
            sellerMap = csvReader.readSellers();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        userMap.clear();
        // put all the customers and sellers into a user map
        userMap.putAll(customerMap);
        userMap.putAll(sellerMap);

        String username = userText.getText();
        char[] passwordCharArray = passwordText.getPassword();
        String password = new String(passwordCharArray);
        Arrays.fill(passwordCharArray, '\0'); // '\0' is the null character.

        User user = userMap.get(username);

        if (user != null && user.authenticate(password)) {

            SwingUtilities.getWindowAncestor(userText).dispose();

            if (user instanceof Customer) {
                success.setText(LOGIN_SUCCEED);
                openCustomerMenu((Customer) user);


            } else if (user instanceof Seller) {
                success.setText(LOGIN_SUCCEED);
                openSellerMenu((Seller) user, socket);


            }

        } else if (actionEvent.equals("Register")) {
            createAccountWindow();
        } else {
            success.setText(INVALID_NAME_OR_PWD);
        }

    }

    //done
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

        passwordText = new JPasswordField();
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
                    customerMap = csvReader.readCustomers();
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
                        userMap.put(username, newUser);

                        // write csv file

                        //tell server to write to CSV file
                        pw.println(CREATE_NEW_ACCOUNT_OPTION_CODE + "$Customer$" + username + "$" + password);
                        pw.flush();
                        String file = "customers.csv";
                        /*
                         *                         try {

                            BufferedWriter bfw = new BufferedWriter(new FileWriter(file, true));
                            String newCustomer = username + "," + password + ",...,...,...";
                            // ... to avoid error ArrayOutOfBounds for new user without blocklist/invisible list
                            bfw.write(newCustomer+"\n");
                            bfw.flush();
                            bfw.close();

                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                         */

                        successLabel.setText(REGISTER_SUCCESS_MSG);


                    } else {

                        newUser = new Seller(username, password);
                        sellerMap.put(username, (Seller) newUser);
                        userMap.put(username, newUser);

                        // write csv file
                        pw.println(CREATE_NEW_ACCOUNT_OPTION_CODE + "$Seller$" + username + "$" + password);
                        pw.flush();
                        /*
                         *                         String file = "sellers.csv";
                        try {

                            BufferedWriter bfw = new BufferedWriter(new FileWriter(file, true));
                            String newSeller = username + "," + password + ",...,...,...,...";
                            bfw.write(newSeller);

                            bfw.close();

                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                         */

                        successLabel.setText(REGISTER_SUCCESS_MSG);

                    }

                }
            }
        });
        signUpPanel.add(createButton);

        signUpFrame.setVisible(true);
    }

    public void setSocket(Socket socket)
    {
        try{
            bfr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream());
            System.out.println("Socket has been set");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
