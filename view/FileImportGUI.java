package Project5;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class FileImportGUI extends JFrame implements ActionListener {
    private JTextField fileNameReadField, storeField, desiredUserField;
    private JLabel fileNameReadLabel, storeLabel, desiredUserLabel;
    private JButton sendButton;
    private User user;
    private Socket socket;

    // constructor
    public FileImportGUI(User user, Socket socket) {
        this.user = user;
        this.socket = socket;

        // set up the JFrame
        setTitle("File Import");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2));

        // create the components
        fileNameReadLabel = new JLabel("File to read from:");
        fileNameReadField = new JTextField();
        storeLabel = new JLabel("Store:");
        storeField = new JTextField();
        desiredUserLabel = new JLabel("Desired user:");
        desiredUserField = new JTextField();
        sendButton = new JButton("Send");

        // add the components to the JFrame
        add(fileNameReadLabel);
        add(fileNameReadField);
        add(storeLabel);
        add(storeField);
        add(desiredUserLabel);
        add(desiredUserField);
        add(sendButton);

        // add action listener to the button
        sendButton.addActionListener(this);

        // display the JFrame
        setVisible(true);
        JOptionPane.showMessageDialog(null,"Imported successfully");
    }

    // action performed method for the send button
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendButton) {
            String fileNameRead = fileNameReadField.getText();
            String store = storeField.getText();
            String desiredUser = desiredUserField.getText();
            FileImport fileImport = new FileImport(fileNameRead, store, user, desiredUser, socket);
            dispose(); // close the JFrame
        }
    }

    public static void main(String[] args) {
        User user = new User("username", "password", "Seller");
        //FileImportGUI fileImportGUI = new FileImportGUI(user);
    }
}