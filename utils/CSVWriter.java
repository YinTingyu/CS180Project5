package utils;

import core.User;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CSVWriter {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public User user;
    public CSVReader csvReader = new CSVReader();

    public CSVWriter(User user) {
        this.user = user;
    }

    public void writeMessage(String conversationFile, String message) throws IOException {

        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(TIMESTAMP_FORMATTER);
        String username = user.getUsername();

        File f = new File(conversationFile);
        BufferedWriter writer = new BufferedWriter(new FileWriter(f, true));

        String[] attr = message.split("& . _ . &");
        String contentToWrite = attr[2].replaceAll(",", "_");
        String formattedMessage = String.format("%s,%s,%s\n",
                timestamp, username, contentToWrite);
        writer.write(formattedMessage);

        writer.close();

    }

    public void updateConversationFile(String filename, List<String> newMessages) throws IOException {

        BufferedWriter bfw = new BufferedWriter(new FileWriter(filename));
        String header = String.format("%s,%s,%s", "timestamp", "username", "message");
        bfw.write(header);

        for (String message : newMessages) { // remember deal with comma
            String[] attr = message.split("& . _ . &");
            attr[2] = attr[2].replaceAll(",", "_"); // replace all the "," to "_" in content
            String noCommaMessage = String.join(",", attr);
            bfw.write(noCommaMessage);
        }
        bfw.close();
    }

    public void writeBlockList(String filename, List<String> blockList) throws IOException {

        List<String> allLines = new ArrayList<>();
        // join block list to a string separate by ";"
        String blockListStr = String.join(";", blockList);
        BufferedWriter bfw = new BufferedWriter(new FileWriter(filename));
        allLines = csvReader.readAllLines(filename);

        for (int i = 0; i < allLines.size(); i++) { // find the position of block list
            // separate the element of each line by ","
            String[] parts = allLines.get(i).split(",");

            if (parts[0].equals(user.getUsername())) { // find the correct user
                // only update this line
                parts[3] = blockListStr; // the forth element of this line is block list
                // reset this line in allLine list
                String newline = String.join(",", parts);
                allLines.set(i, newline);
                break;
            }
        }

        // rewrite everything include header
        if (user.getRole().equals("Customer")) {

            String header = String.format("%s,%s,%s,%s,%s",
                    "username", "password", "conversation", "blocklist", "invislist");
            bfw.write(header);
            for (String line : allLines) {
                bfw.write(line);
                bfw.newLine();
            }
        } else if (user.getRole().equals("Seller")) {

            String header = String.format("%s,%s,%s,%s,%s,%s",
                    "username", "password", "conversation", "blocklist", "invislist", "stores");
            bfw.write(header);
            for (String line : allLines) {
                bfw.write(line);
                bfw.newLine();
            }
        }

        bfw.close();
    }

    public void writeInvisList(String filename, List<String> invisList) throws IOException {

        List<String> allLines = new ArrayList<>();
        String invisListStr = String.join(";", invisList);

        BufferedWriter bfw = new BufferedWriter(new FileWriter(filename));
        allLines = csvReader.readAllLines(filename);
        for (int i = 0; i < allLines.size(); i++) {
            String[] parts = allLines.get(i).split(",");

            if (parts[0].equals(user.getUsername())) { // find the correct user
                // only update this line
                parts[4] = invisListStr; // the fifth element of this line is invisible list
                // reset this line in allLine list
                String newline = String.join(",", parts);
                allLines.set(i, newline);
                break;
            }
        }

        // rewrite everything include header
        if (user.getRole().equals("Customer")) {

            String header = String.format("%s,%s,%s,%s,%s",
                    "username", "password", "conversation", "blocklist", "invislist");
            bfw.write(header);
            for (String line : allLines) {
                bfw.write(line);
                bfw.newLine();
            }
        } else if (user.getRole().equals("Seller")) {

            String header = String.format("%s,%s,%s,%s,%s,%s",
                    "username", "password", "conversation", "blocklist", "invislist", "stores");
            bfw.write(header);
            for (String line : allLines) {
                bfw.write(line);
                bfw.newLine();
            }
        }
        bfw.close();
    }

    public void writeStores(List<String> storesList) throws IOException {

        String filename = "./src/" + "sellers" + ".csv";
        List<String> allLines = csvReader.readAllLines(filename);
        BufferedWriter bfw = new BufferedWriter(new FileWriter(filename));
        String storesStr = String.join(";", storesList);

        for (int i = 0; i < allLines.size(); i++) {
            String[] attr = allLines.get(i).split(",");
            if (attr[0].equals(user.getUsername())) {
                attr[5] = storesStr;
                String newline = String.join(",", attr);
                allLines.set(i, newline);
                break;
            }
        }

        // rewrite everything include header
        if (user.getRole().equals("Customer")) {

            String header = String.format("%s,%s,%s,%s,%s",
                    "username", "password", "conversation", "blocklist", "invislist");
            bfw.write(header);
            for (String line : allLines) {
                bfw.write(line);
                bfw.newLine();
            }
        }
        bfw.close();
    }
}
