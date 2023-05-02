package view;

import core.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

import javax.swing.*;

public class FileExportGUI extends JFrame implements ActionListener {

    private static final String REJECTION_CODE = "NOT OKAY";

    private String name;
    private User user;
    private String exportedFileName;

    private JButton exportButton;
    private JTextField nameField;
    private Socket socket;
    private PrintWriter pw;

    public FileExportGUI(User user, Socket socket) {
        this.user = user;
        this.socket = socket;
        try{
            pw = new PrintWriter(socket.getOutputStream());
        }catch (Exception e){
            e.printStackTrace();
        }

        // create components
        JLabel nameLabel = new JLabel("Enter the name of the user that you would " +
                "like to extract the conversation history of:");
        nameField = new JTextField();
        exportButton = new JButton("Export");
        name = nameField.getName();

        // add action listener to export button
        exportButton.addActionListener(this);

        // create panel for name field and export button
        JPanel inputPanel = new JPanel(new GridLayout(2, 1));
        inputPanel.add(nameField);
        inputPanel.add(exportButton);

        // create main panel and add components
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(nameLabel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        // add main panel to frame
        this.add(mainPanel);

        // set frame properties
        this.pack();
        this.setTitle("Export Conversation History");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == exportButton) {
            name = nameField.getText();
            exportedFileName = "Exported" + user.getUsername() + name + ".csv";
            exportToFile(exportedFileName, name);
        }
    }

    public void exportToFile(String exportedFileName, String name) {
        try {
            // sending to server
            pw.println("BB05$" + user.getUsername() + "$" + name);
            pw.flush();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = bufferedReader.readLine();
            bufferedReader.close();
            if(!line.equals(REJECTION_CODE)) {
                String[] lines = line.split(";;;");

                File file = new File(exportedFileName);
                file.createNewFile();
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                for(String e: lines)
                {
                    bufferedWriter.write(e);
                    bufferedWriter.newLine();
                }
                bufferedWriter.flush();
                bufferedWriter.close();
                JLabel successLabel = new JLabel("Conversation history exported to " + exportedFileName);
                JPanel successPanel = new JPanel(new BorderLayout());
                successPanel.add(successLabel, BorderLayout.CENTER);
                JOptionPane.showMessageDialog(this, successPanel,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JLabel errorLabel = new JLabel("File does not exist");
                JPanel errorPanel = new JPanel(new BorderLayout());
                errorPanel.add(errorLabel, BorderLayout.CENTER);
                JOptionPane.showMessageDialog(this, errorPanel, "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            e.printStackTrace(); // only for testing
        }
    }
}