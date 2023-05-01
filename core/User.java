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
 */
public class User {
    private String password;
    private String username;
    private String role;
    private HashMap<User, ConversationHistory> conversations; // each user can get their conversation history
    public static HashMap<String, User> usersByUsername = new HashMap<>(); // distinguished by their name
    private List<String> blockList;
    private List<String> invisList;
    private List<String> conFilenames;

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

    public void setBlockList(List<String> list) {
        blockList = list;
    }

    public void setInvisList(List<String> list) {
        invisList = list;
    }

    public void setConFilenames(List<String> list) {
        conFilenames = list;
    }

    public HashMap<User, ConversationHistory> getConversations() {
        return conversations;
    }

    public List<String> getBlockList() {
        return blockList;
    }

    public List<String> getInvisList() {
        return invisList;
    }

    public List<String> getConFilenames() {
        return conFilenames;
    }
}
