package utils;

import core.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversationCSVReader {

    CSVReader reader = new CSVReader();
    public static Map<String, Seller> sellerMap = new HashMap<>();
    List<Message> messageList = new ArrayList<>();
    public ConversationHistory readConversation(String user, String other) throws IOException {
        sellerMap = reader.readSellers();
        String conversationFilename;
        conversationFilename = reader.getFilenames(user, other);
        BufferedReader bfr = new BufferedReader(new FileReader(conversationFilename));
        String line;
        bfr.readLine(); // escape the header

        while ((line = bfr.readLine()) != null) {
            String[] attributes = line.split(",");
            String timestampStr = attributes[0];
            String messageStr = attributes[2];
            Message message = new Message(user, messageStr, timestampStr);
            messageList.add(message);
        }
        ConversationHistory conversationHistory = new ConversationHistory(messageList);

        return conversationHistory;
    }

    public static void main(String[] args) throws IOException {
        // test
        ConversationCSVReader csvReader = new ConversationCSVReader();
        ConversationHistory conversationHistory = csvReader.readConversation("Tim", "Jimmy");
        List<Message> messages = conversationHistory.getMessagesHis();
        for (Message message : messages) {
            System.out.println(message.getContent());
        }

    }
}
