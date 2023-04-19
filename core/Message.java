package core;
import java.sql.Timestamp;
import java.util.Date;
/**
 * A class to record information of single message in this application.
 *
 * <p>Purdue University -- CS18000 -- Spring 2023 -- project 4
 *
 * @author Tingyu Yin
 * @version April 8, 2023
 *
 * modified ver. April 10, 2023
 */
public class Message {
    private User sender;
    private User recipient;
    private String content;
    private Timestamp timestamp;
    private String timestamp_STRING;

    //variables added for compatibility with Test
    private String senderName;

    private String storeName;

    private Store store;


    public Message(User sender, User recipient, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.timestamp = new Timestamp(new Date().getTime());
        this.content = content;
    }

    //new constructor added
    public Message(String sender, String content, String timestamp) {
        this.senderName = sender;
        this.content = content;
        this.timestamp_STRING = timestamp;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    //new getters and setters for senderName string
    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String sender) {
        this.senderName = sender;
    }

    public String getTimestampSTRING()
    {
        return timestamp_STRING;
    }

    public void setTimestampSTRING(String timestamp)
    {
        this.timestamp_STRING = timestamp;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        String tsmpStr;
        tsmpStr = getTimestamp().toString();
        return tsmpStr;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
