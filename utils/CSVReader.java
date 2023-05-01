package utils;

import core.*;

import java.io.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

public class CSVReader {

    public static Map<String, Customer> customerMap = new HashMap<>();
    public static Map<String, Store> storeMap = new HashMap<>();
    public static Map<String, Seller> sellerMap = new HashMap<>();
    public static Map<User, ConversationHistory> historyMap = new HashMap<>();

    public String getFilenames(String user, String other) {
        String csvFilename = "./src/" + user + "&&" + other + ".csv"; // add "./src/" just for running it, this is my path in Intellij
        return csvFilename;                                         //  you can change the path
    }

    public Map<String, Customer> readCustomers() throws IOException {
        String customerCSV = "./src/customers.csv";
        BufferedReader bfr = new BufferedReader(new FileReader(customerCSV));
        String line;
        bfr.readLine(); // escape the header
        while ((line = bfr.readLine()) != null) {
            String[] attributes = line.split(",");
            String username = attributes[0];
            String password = attributes[1];
            Customer customer = new Customer(username, password);
            customerMap.put(username, customer);
        }
        bfr.close();

        return customerMap;
    }

    public List<String> readAllLines(String filename) throws IOException {

        BufferedReader bfr = new BufferedReader(new FileReader(filename));
        List<String> allLines = new ArrayList<>();
        String line;

        bfr.readLine(); // escape the header !
        while ((line = bfr.readLine()) != null) {
            allLines.add(line);
        }
        return allLines;
    }


    public Map<String, Store> readStores() throws IOException {

        String storeFilename = "./src/stores.csv";
        BufferedReader bfr = new BufferedReader(new FileReader(storeFilename));
        String line;

        bfr.readLine(); // escape the header
        while ((line = bfr.readLine()) != null) {
            String[] attributes = line.split(",");
            String storeName = attributes[0];
            String seller = attributes[2];

            List<String> productList = new ArrayList<>();
            List<Double> priceList = new ArrayList<>();
            List<Integer> amountList = new ArrayList<>();

            String[] allProducts = attributes[1].split(";");

            for (int i = 0; i < allProducts.length; i++) {
                String[] productsInfo = allProducts[i].split("-");

                String product = productsInfo[0];
                int amount = Integer.parseInt(productsInfo[1]);
                double price = Double.parseDouble(productsInfo[2]);

                productList.add(product);
                amountList.add(amount);
                priceList.add(price);
            }

            readSellers();
            Store store = new Store(storeName, productList, amountList,
                    priceList, sellerMap.get(seller));
            storeMap.put(storeName, store);

        }
        bfr.close();

        return storeMap;
    }

    public Map<String, Seller> readSellers() throws IOException {

        String sellerFilename = "./src/sellers.csv"; // this is my path, you can change
        BufferedReader bfr = new BufferedReader(new FileReader(sellerFilename));
        String line;

        bfr.readLine(); // escape the header
        while ((line = bfr.readLine()) != null) {
            String[] attributes = line.split(",");
            String username = attributes[0];
            String password = attributes[1];

            Seller seller = new Seller(username, password);
            sellerMap.put(username, seller);

        }
        bfr.close();

        return sellerMap;
    }

    public Map<String, User> getUserMap() throws IOException {
        customerMap = readCustomers();
        sellerMap = readSellers();
        Map<String, User> userMap = new HashMap<>();
        userMap.putAll(customerMap);
        userMap.putAll(sellerMap);

        return userMap;
    }


    // all the user and store should be load into application after the three methods above


    // this method is to find a specific user's block list
    public List<String> getBlockList(User user) throws IOException {

        List<String> blockedList = new ArrayList<>();
        String line;

        if (user.getRole().equals("Customer")) {
            String customerCSV = "./src/customers.csv"; // this my path in Intellij, you can change it
            BufferedReader bfr = new BufferedReader(new FileReader(customerCSV));

            bfr.readLine(); // escape the header
            while ((line = bfr.readLine()) != null) {
                String[] attr = line.split(",");
                String username = attr[0];

                if (user.getUsername().equals(username)) { // find a specific user's block list

                    if (!attr[4].equals("...")) {
                        String[] block = attr[3].split(";");
                        for (int i = 0; i < block.length; i++) {
                            blockedList.add(block[i]);
                        }
                    } else {
                        blockedList.clear();
                    }

                }
            }

        } else if (user.getRole().equals("Seller")) {
            String sellerCSV = "./src/sellers.csv";
            BufferedReader bfr = new BufferedReader(new FileReader(sellerCSV));

            bfr.readLine(); // escape the header
            while ((line = bfr.readLine()) != null) {
                String[] attr = line.split(",");
                String username = attr[0];

                if (user.getUsername().equals(username)) { // find a specific user's block list

                    if (!attr[4].equals("...")) {
                        String[] block = attr[3].split(";");
                        for (int i = 0; i < block.length; i++) {
                            blockedList.add(block[i]);
                        }
                    } else {
                        blockedList.clear();
                    }
                }
            }
            bfr.close();
        }
        return blockedList;
    }

    // to find a specific user's invisible list
    public List<String> getInvisList(User user) throws IOException {

        List<String> invisList = new ArrayList<>();
        String line;

        if (user.getRole().equals("Customer")) { // index of customer's invisible column is 4
            String customerCSV = "./src/customers.csv";
            BufferedReader bfr = new BufferedReader(new FileReader(customerCSV));

            bfr.readLine(); // escape the header
            while ((line = bfr.readLine()) != null) {
                String[] attr = line.split(",");
                String username = attr[0];

                if (user.getUsername().equals(username)) {

                    if (!attr[4].equals("...")) {
                        String[] invisible = attr[4].split(";");
                        for (int i = 0; i < invisible.length; i++) {
                            invisList.add(invisible[i]);
                        }
                    } else {
                        invisList.clear();
                    }
                }
            }

        } else if (user.getRole().equals("Seller")) { // index of seller's invisible column is 4
            String sellerCSV = "./src/sellers.csv";
            BufferedReader bfr = new BufferedReader(new FileReader(sellerCSV));

            bfr.readLine(); // escape the header
            while ((line = bfr.readLine()) != null) {
                String[] attr = line.split(",");
                String username = attr[0];

                if (user.getUsername().equals(username)) { // find a specific user's block list
                    if (!attr[4].equals("...")) {
                        String[] invisible = attr[4].split(";");
                        for (int i = 0; i < invisible.length; i++) {
                            invisList.add(invisible[i]);
                        }
                    } else {
                        invisList.clear();
                    }
                }
            }
            bfr.close();
        }
        return invisList;
    }


    public List<Store> getSellerStores(Seller seller) throws IOException {
        readStores();
        List<Store> sellerStore = new ArrayList<>();
        Store storeToFind;
        String line;
        BufferedReader bfr = new BufferedReader(new FileReader("./src/sellers.csv"));
        bfr.readLine(); // escape the header
        while ((line = bfr.readLine()) != null) {
            String[] attr = line.split(",");
            String sellerName = attr[0];
            if (sellerName.equals(seller.getUsername())) { // find the seller

                if (!attr[5].equals("...")) {
                    String[] storesName = attr[5].split(";"); // get stores of the seller
                    for (String s : storesName) {
                        storeToFind = storeMap.get(s);
                        sellerStore.add(storeToFind);
                    }
                } else {
                    sellerStore.clear();
                }

            }
        }

        bfr.close();

        return sellerStore;
    }


    public List<String> readMessages(String filename) throws IOException {
        List<String> messageList = new ArrayList<>();
        BufferedReader bfr = new BufferedReader(new FileReader(filename));
        String line;
        bfr.readLine(); // escape the header

        while ((line = bfr.readLine()) != null) {
            String[] attr = line.split(",");
            String timestampStr = attr[0];
            String username = attr[1];
            String message = attr[2].replaceAll("_", ",");

            String format = "%s& . _ . &%s& . _ . &%s";
            String newLine = String.format(format, timestampStr,
                    username, message);

            messageList.add(newLine);
        }
        bfr.close();

        return messageList;
    }

    public ConversationHistory readConversationHis(User user, List<String> conFilenames) throws IOException {
        ConversationHistory conversationHis = null;
        conFilenames = readConFilenames(user);
        List<Message> conversationList = new ArrayList<>();
        for (String filename : conFilenames) {
            BufferedReader bfr = new BufferedReader(new FileReader(filename));
            String line;
            bfr.readLine(); // escape the header

            while ((line = bfr.readLine()) != null) {
                String[] attr = line.split(",");
                String time = attr[0];
                String sender = attr[1];
                String content = attr[2].replaceAll("_", ",");
                Message message = new Message(sender, content, time);
                conversationList.add(message);

                // instance ConversationHistory
                conversationHis = new ConversationHistory(conversationList, filename);
            }
        }

        return conversationHis;
    }

    public List<String> readConFilenames(User user) throws IOException {
        String filename;
        BufferedReader bfr;
        String line;
        List<String> allFileList = new ArrayList<>();

        if (user instanceof Customer) {
            filename = "./src/" + "customers" + ".csv";
            bfr = new BufferedReader(new FileReader(filename));
            bfr.readLine(); // escape the header

            while ((line = bfr.readLine()) != null) {
                String[] attr = line.split(",");
                String filenames = attr[2];
                String[] allFiles = filenames.split(";");
                allFileList = Arrays.asList(allFiles);

            }
        } else if (user instanceof Seller) {
            filename = "./src/" + "sellers" + ".csv";
            bfr = new BufferedReader(new FileReader(filename));
            bfr.readLine(); // escape the header

            while ((line = bfr.readLine()) != null) {
                String[] attr = line.split(",");
                String filenames = attr[2];
                String[] allFiles = filenames.split(";");
                allFileList = Arrays.asList(allFiles);

            }
        }

        return allFileList;
    }

    public Map<User, ConversationHistory> returnHistoryMap() throws IOException {

        Map<String, User> userMap = getUserMap();
        Map<User, ConversationHistory> historyMap = new HashMap<>();
        for (User user : userMap.values()) {
            List<String> conFilenames = readConFilenames(user);
            ConversationHistory conversationHis = readConversationHis(user, conFilenames);
            historyMap.put(user, conversationHis);
        }
        return historyMap;
    }



    public Timestamp getTimestamp(String timestampStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp timestamp = null;

        try {
            Date date = dateFormat.parse(timestampStr);
            timestamp = new Timestamp(date.getTime());
        } catch (ParseException e) {
            System.err.println("Invalid parsing date string.");
        }
        return timestamp;

    }
}
