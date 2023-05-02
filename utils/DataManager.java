package utils;

import core.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {

    private Map<String, User> userMap;
    private Map<String, Store> storeMap;
    private CSVReader csvReader = new CSVReader();
    private CSVWriter csvWriter = new CSVWriter();
    private Map<User, ConversationHistory> conversationHistoryMap;

    public DataManager() {
        userMap = new HashMap<>();
        try {
            loadDataFromCSV();
            refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getFileContents(String filename) {
        try {
            return csvReader.readAllLines(filename);
        } catch (Exception e) {
            return null;
        }
    }

    public void loadDataFromCSV() throws IOException {

        Map<String, Customer> customerMap = csvReader.readCustomers();
        Map<String, Seller> sellerMap = csvReader.readSellers();
        conversationHistoryMap = csvReader.returnHistoryMap();
        storeMap = csvReader.readStores();

        userMap.putAll(customerMap);
        userMap.putAll(sellerMap);
    }

    public void refresh() throws IOException {
        for (User user : userMap.values()) {
            List<String> blockList = csvReader.getBlockList(user);
            List<String> invisList = csvReader.getInvisList(user);
            List<String> conFilenames = csvReader.readConFilenames(user);

            if (user.getRole().equals("Customer")) {
                Customer customer = (Customer) user;
                customer.setBlockList(blockList);
                customer.setInvisList(invisList);
                customer.setConFilenames(conFilenames);

            } else if (user.getRole().equals("Seller")) {
                Seller seller = (Seller) user;
                List<Store> storeList = csvReader.getSellerStores(seller);
                seller.setBlockList(blockList);
                seller.setInvisList(invisList);
                seller.setConFilenames(conFilenames);
                seller.setStores(storeList);
            }
        }
    }

    public User getUser(String username) {
        return userMap.get(username);
    }

    public void addNewAccount(User newUser) throws IOException {
        userMap.put(newUser.getUsername(), newUser);
        csvWriter = new CSVWriter(newUser);
        csvWriter.writeNewAccount(newUser);
    }

    public void writeConversation(String filename, List<String> newMessages) {

        String[] participants = filename.split("&&");
        String sender = participants[0];
        int n = participants[1].length();
        String recipient = participants[1].substring(0, n - 4);
        User user = userMap.get(sender); // find the sender

        if (user instanceof Customer) {
            // the recipient get from the filename should be a store name
            Store store = storeMap.get(recipient);
            Seller seller = store.getSeller();
            ConversationHistory sellerConHis = conversationHistoryMap.get(seller);

            // filename as key
            Map<String, List<Message>> conMap = sellerConHis.getConversationMap();
            List<Message> messages = conMap.get(filename);

            for (String message : newMessages) {
                String[] attrs = message.split("& . _ . &");
                String time = attrs[0];
                String content = attrs[1];
                Message newMessage = new Message(sender, content, time);
                messages.add(newMessage);
            }

        } else if (user instanceof Seller) {
            // the recipient get from the filename should be a customer
            Customer customer = (Customer) userMap.get(recipient);
            ConversationHistory customerConHis = conversationHistoryMap.get(customer);

            Map<String, List<Message>> conMap = customerConHis.getConversationMap();
            List<Message> messages = conMap.get(filename);

            for (String message : newMessages) {
                String[] attrs = message.split("& . _ . &");
                String time = attrs[0];
                String content = attrs[1];
                Message newMessage = new Message(sender, content, time);
                messages.add(newMessage);
            }
        }

        try {
            csvWriter.updateConversationFile(filename, newMessages);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeMessage(String filename, String message) {

        try {
            csvWriter.writeMessage(filename, message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeMessage(String filename, String message, String senderName) {
        try {
            csvWriter.writeMessage(filename, message, senderName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeStores(List<String> storesList) {
        try {
            csvWriter.writeStores(storesList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeProductName(Store store, int index, String newName) {
        try {
            csvWriter.writeProductName(store, index, newName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeProductAmount(Store store, int index, Integer newAmount) {
        try {
            csvWriter.writeProductAmount(store, index, newAmount);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeProductPrice(Store store, int index, Double newPrice) {
        try {
            csvWriter.writeProductPrice(store, index, newPrice);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeNewProduct(Store store, String name, String amount, String price) {

        try {
            csvWriter.writeNewProduct(store, name, amount, price);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeBlockList(User user, List<String> blockList) {
        try {
            csvWriter.writeBlockList(user, blockList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addStore(Store store) {
        storeMap.put(store.getStoreName(), store);

    }

    public void addBlock(User user, String blocked) {
        // add blocked user to block list and write list into csv file
        try {
            List<String> blockList = csvReader.getBlockList(user);
            blockList.add(blocked);
            user.setBlockList(blockList);
            writeBlockList(user, blockList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeBlock(User user, String unblocked) {
        // remove blocked user from block list and write list into csv file
        try {
            List<String> blockList = csvReader.getBlockList(user);
            blockList.remove(unblocked);
            user.setBlockList(blockList);
            writeBlockList(user, blockList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}