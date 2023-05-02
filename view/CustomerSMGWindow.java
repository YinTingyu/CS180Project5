package view;

import core.Customer;
import core.Store;
import utils.CSVReader;
import utils.CSVWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
/**
 * Customer's send message interface
 *
 * <p>Purdue University -- CS18000 -- Spring 2023 -- project 5
 *
 * @author Tingyu Yin
 * @version April 15, 2023
 */
public class CustomerSMGWindow {
    private CustomerMenu customerMenu;
    private Store store;
    private Customer customer;
    private Socket socket;
    static JTextField inputMessage;

    List<String> messages = new ArrayList<>();

    private JPanel conversationPanel = new JPanel();
    public CustomerSMGWindow(CustomerMenu customerMenu, Store store, Customer customer, Socket socket) {
        this.customerMenu = customerMenu;
        this.store = store;
        this.customer = customer;
        this.socket = socket;
    }

    private void updateConversation(List<String> messages) throws IOException {

        //// Clear the existing conversation panel
        conversationPanel.removeAll();
        CSVReader reader = new CSVReader();
        CSVWriter writer = new CSVWriter(customer);
        String filename = reader.getFilenames(customer.getUsername(), store.getStoreName());
        String otherFilename = reader.getFilenames(store.getSeller().getUsername(), customer.getUsername());


        for (String message : messages) {
            System.out.println(message);
            String[] msgInfo = message.split("& . _ . &");

            // use labels to hold sender's name and timestamp
            JLabel senderLabel = new JLabel(msgInfo[1]);
            JLabel timestampLabel = new JLabel(msgInfo[0]);

            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            headerPanel.add(timestampLabel);
            headerPanel.add(senderLabel);

            JPanel messagePanel = new JPanel(new BorderLayout());
            JTextArea messageArea = new JTextArea(msgInfo[2]);
            messageArea.setEditable(false); // can not edit message when show conversations
            messageArea.setLineWrap(true);
            messageArea.setWrapStyleWord(true);
            messageArea.setPreferredSize(new Dimension(400,
                    messageArea.getPreferredSize().height));

            messagePanel.add(messageArea, BorderLayout.CENTER);
            messagePanel.add(headerPanel, BorderLayout.NORTH);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton editButton = new JButton("Edit");
            JButton deleteButton = new JButton("Delete");
            JButton saveButton = new JButton("Save");
            saveButton.setVisible(false);

            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(saveButton);
            messagePanel.add(buttonPanel, BorderLayout.EAST);
            conversationPanel.add(messagePanel);

            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    messageArea.setEditable(true); // message can be edited now
                    editButton.setVisible(false);
                    saveButton.setVisible(true); // show save button

                }
            });

            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    String newMSG = messageArea.getText();
                    int msgIndex = messages.indexOf(message);
                    String tempMSG = messages.get(msgIndex);
                    String[] attr = tempMSG.split("& . _ . &");
                    attr[2] = newMSG;
                    newMSG = attr[0] + "& . _ . &" + attr[1] + "& . _ . &" + attr[2];
                    messages.set(msgIndex, newMSG);

                    messageArea.setEditable(false);
                    editButton.setVisible(true);
                    saveButton.setVisible(false);

                    try {
                        updateConversation(messages);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    // (write) update csv file
                    try {
                        writer.updateConversationFile(filename, messages);
                        writer.updateConversationFile(otherFilename, messages);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    int msgIndex = messages.indexOf(message);
                    messages.remove(msgIndex);

                    try {
                        updateConversation(messages);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    // (write) update csv file
                    try {
                        writer.updateConversationFile(filename, messages); // only modify who initiate
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        // Refresh the conversation panel
        conversationPanel.revalidate();
        conversationPanel.repaint();

    }

    public void run() throws IOException {
        CSVReader reader = new CSVReader();
        CSVWriter writer = new CSVWriter(customer);

        String filename = reader.getFilenames(customer.getUsername(), store.getStoreName());
        //String otherFilename = reader.getFilenames(store.getSeller().getUsername(), customer.getUsername());
        File file = new File(filename);
        //File otherFile = new File(otherFilename);

//        if (!file.exists() && otherFile.exists()) { // update file from otherFile
//            file.createNewFile();
//            BufferedWriter bwr = new BufferedWriter(new FileWriter(file));
//            String formatHeader = String.format("%s,%s,%s\n", "timestamp",
//                    "username", "message");
//            List<String> messages = reader.readMessages(otherFilename);
//            bwr.write(formatHeader);
//            writer.updateConversationFile(filename, messages);
//
//
//        } else if (!otherFile.exists() && file.exists()) { // update otherFile from file
//            otherFile.createNewFile();
//            BufferedWriter bwr = new BufferedWriter(new FileWriter(otherFile));
//            String formatHeader = String.format("%s,%s,%s\n", "timestamp",
//                    "username", "message");
//            List<String> messages = reader.readMessages(filename);
//            bwr.write(formatHeader);
//            writer.updateConversationFile(otherFilename, messages);
//
//
//        } else { // both of two file do not exist
//            file.createNewFile();
//            otherFile.createNewFile();
//            BufferedWriter bwr = new BufferedWriter(new FileWriter(file));
//            BufferedWriter bwf = new BufferedWriter(new FileWriter(otherFile));
//            String formatHeader = String.format("%s,%s,%s\n", "timestamp",
//                    "username", "message");
//            bwr.write(formatHeader);
//            bwf.write(formatHeader);
//
//        }

        if (!file.exists()) {
            file.createNewFile();
            BufferedWriter bwr = new BufferedWriter(new FileWriter(file));
            String formatHeader = String.format("%s,%s,%s\n", "timestamp",
                    "username", "message");
            bwr.write(formatHeader);
            bwr.close();
        }

        messages = reader.readMessages(filename);


        JFrame frame = new JFrame("Send Message");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(new Dimension(600, 400));
        frame.setLayout(new BorderLayout());

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        labelPanel.add(new JLabel("Store Name: " + store.getStoreName()));
        List<String> other = reader.getInvisList(store.getSeller());
        if (!other.contains(customer.getUsername())) {
            labelPanel.add(new JLabel(" | "));
            labelPanel.add(new JLabel("Seller: " + store.getSeller().getUsername()));
        }
        
        frame.add(labelPanel, BorderLayout.NORTH);

        conversationPanel.setLayout(new BoxLayout(conversationPanel, BoxLayout.Y_AXIS));
        updateConversation(messages);
        JScrollPane scrollPane = new JScrollPane(conversationPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        JLabel inputLabel = new JLabel("Enter: ");
        // a msg&label panel to contain enter label and text field
        JPanel msgAndLabelPanel = new JPanel(new FlowLayout());
        msgAndLabelPanel.add(inputLabel);

        inputMessage = new JTextField(30);
        inputMessage.setPreferredSize(new Dimension(30, 25));
        msgAndLabelPanel.add(inputMessage);

        // a panel to contain msg&label panel and send button
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(msgAndLabelPanel, BorderLayout.WEST);

        JPanel sendButtonPanel = new JPanel(new FlowLayout());
        JButton sendButton = new JButton("Send");
        JButton importButton = new JButton("Import txt"); //file import stuff
        sendButtonPanel.add(importButton);
        sendButtonPanel.add(sendButton);
        inputPanel.add(sendButtonPanel, BorderLayout.EAST);
        importButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                        FileImportGUI fio = new FileImportGUI(customer, socket);
                    }
        });
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String msg = inputMessage.getText();

                // get the current timestamp
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String timestampStr = dateFormat.format(timestamp);

                // add new message to messages list
                String format = "%s& . _ . &%s& . _ . &%s";
                String newMSGStr = String.format(format, timestampStr, customer.getUsername(), msg);
                messages.add(newMSGStr);

                // write csv
                try {
                    inputMessage.setText(""); // once send message, empty send message text field
                    updateConversation(messages);

                    String filename = reader.getFilenames(customer.getUsername(), store.getStoreName());
                    String otherFilename = reader.getFilenames(store.getSeller().getUsername(), customer.getUsername());
                    File other = new File(otherFilename);
                    if (!other.exists()) {
                        other.createNewFile();
                        BufferedWriter bwr = new BufferedWriter(new FileWriter(file));
                        String formatHeader = String.format("%s,%s,%s\n", "timestamp",
                                "username", "message");
                        bwr.write(formatHeader);
                        bwr.close();
                    }
                    writer.writeMessage(filename, newMSGStr);
                    writer.updateConversationFile(otherFilename, messages);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

    }
}
