package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * A super class of seller and customer
 *
 * <p>Purdue University -- CS18000 -- Spring 2023 -- project 4
 *
 * @author Tingyu Yin
 * @version April 8, 2023
 * 
 * modified by O. Wang to add a setConversations method
 */
public class User {
    private String password;
    private String username;
    private String role;
    private HashMap<User, ConversationHistory> conversations; // each user can get their conversation history
    public static HashMap<String, User> usersByUsername = new HashMap<>(); // distinguished by their name
    private List<User> blockedUsers;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        conversations = new HashMap<>();
        this.role = role;
        usersByUsername.put(this.username, this);
    }

    // this method is used for helping check the password matching the user's email when login
    public boolean authenticate(String pwdToCheck) {
        return this.password.equals(pwdToCheck);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public HashMap<String, User> getUsersByUsername() {
        return usersByUsername;
    }

    // I think only the password can be edited once the account created
    public void editAccount(String username, String password) {
        User userToEdit = usersByUsername.get(username);
        if (userToEdit != null) {
            userToEdit.setPassword(password);
        }
    }

    public void deleteAccount(String email) {
        usersByUsername.remove(email);
    }

    public void editMessage(User recipient, Message message, String newContent) {
        message.setContent(newContent);
        message.setRecipient(recipient);
    }

    public void createMessage(User recipient, String content) {
        Message message = new Message(this, recipient, content);
        ConversationHistory history = conversations.get(recipient);
        if (history == null) {
            history = new ConversationHistory(new ArrayList<>(), new ArrayList<>());
            conversations.put(recipient, history);
        }
        history.getMessagesHis().add(message);
    }

    public void deleteMessage(Message message) {
        ConversationHistory history = conversations.get(message.getRecipient());
        if (history != null) {
            history.getMessagesHis().remove(message);
        }
    }

    public HashMap<User, ConversationHistory> getConversations() {
        return conversations;
    }

    public ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        for (User user : usersByUsername.values()) {
            users.add(user);
        }
        return users;
    }

    public void blockUser(User user) {
        blockedUsers.add(user);
    }

    public boolean isUserBlocked(User user) {
        return blockedUsers.contains(user);
    }

    public void addConversation(User u, ConversationHistory c) {
        conversations.put(u, c);
    }

    public void setConversations(HashMap<User, ConversationHistory> conversations) {
        this.conversations = conversations;
    }
}
