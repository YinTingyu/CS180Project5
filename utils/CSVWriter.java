package utils;

import core.User;
import view.CustomerMenu;
import view.Login;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CSVWriter {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("h:mm:ss a");
    public Login login = new Login();
    public CustomerMenu customerMenu = new CustomerMenu();
    public CSVReader csvReader = new CSVReader();

    public void writeMessage(String conversationFile) throws IOException {

        User user = login.user;
        if (user.getRole().equals("Customer")) {
            String messageToWrite = customerMenu.message.replaceAll(",", "_");
            LocalDateTime now = LocalDateTime.now();
            String timestamp = now.format(TIMESTAMP_FORMATTER);
            String username = user.getUsername();
            String formattedMessage = String.format("%s,%s,%s,N\n", timestamp, username, messageToWrite);

            File f = new File(conversationFile);
            if (!f.exists()) {
                f.createNewFile();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(f, true));
            writer.write(formattedMessage);
            writer.close();
        } else if (user.getRole().equals("Seller")) {
            // use inputs from sellerMenu to implement
        }


    }

    public void writeBlockList(String filename) throws IOException {

        User user = login.user;
        List<String> allLines = new ArrayList<>();
        if (user.getRole().equals("Customer")) {
            List<String> blockList = customerMenu.blockList;
            String blockListStr = String.join(";", blockList);

            BufferedWriter bfw = new BufferedWriter(new FileWriter(filename, true));
            allLines = csvReader.readAllLines(filename);

            for (int i = 0; i < allLines.size(); i++) {
                String[] parts = allLines.get(i).split(",");
                if (parts[0].equals(user.getUsername())) { // find the correct user
                    // only update this line
                    parts[3] = blockListStr;
                    String newline = String.join(",", parts);
                    allLines.set(i, newline);
                    break;
                }

                for (String line : allLines) {
                    bfw.write(line);
                    bfw.newLine();
                }
                bfw.close();
            }
        }

    }
}
