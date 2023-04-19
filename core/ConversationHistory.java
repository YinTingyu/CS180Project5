package core;

import java.util.List;
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
    private List<String> participants;
    private String filename;

    public ConversationHistory(List<Message> messages) {
        this.messages = messages;
        filename = "";
    }

    public List<Message> getMessagesHis() {
        return messages;
    }


    public String getFilename() {
        return filename;
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
}