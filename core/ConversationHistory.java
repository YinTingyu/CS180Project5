package core;

import java.util.*;

/**
 * A class to record all the user's conversation history in this application.
 *
 * <p>Purdue University -- CS18000 -- Spring 2023 -- project 4
 *
 * @author Tingyu Yin
 * @version April 8, 2023
 *
 * April 10, 2023 - edited to include a string for the csv file for each conversation object
 */
public class ConversationHistory {
    private List<Message> messages;

    // use filename to be the key
    private Map<String, List<Message>> conversationMap = new HashMap<>();
    private String filename;

    public ConversationHistory(List<Message> messages, String filename) {
        this.filename = filename;
        this.messages = messages;
        conversationMap.put(filename, messages);
    }

    public List<Message> getMessagesHis() {
        return messages;
    }


    public List<String> getFilenames() {
        Set<String> filenamesSet = conversationMap.keySet();
        List<String> conFilenames = new ArrayList<>();
        conFilenames.addAll(filenamesSet);
        return conFilenames;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void addMessage(Message message)
    {
        messages.add(message);
    }

    public void setMessagesHis(List<Message> messages) {
        this.messages = messages;
    }

    public Map<String, List<Message>> getConversationMap() {
        return conversationMap;
    }
}