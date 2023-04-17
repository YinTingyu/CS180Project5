package utils;

import core.*;
import view.Login;

import java.io.*;
import java.util.*;

public class CSVReader {

    public static Map<String, Customer> customerMap = new HashMap<>();
    public static Map<String, Store> storeMap = new HashMap<>();
    public static Map<String, Seller> sellerMap = new HashMap<>();

    public String getFilenames(String user, String other) {
        String csvFilename = "./src/" + user + "&&" + other + ".csv"; // add "./src/" just for running it, this is my path in Intellij
        return csvFilename;                                         //  you can change the path
    }

    public Map<String, Customer> readSCustomers() throws IOException {
        String customerCSV = "./src/customers.csv";
        BufferedReader bfr = new BufferedReader(new FileReader(customerCSV));
        String line;
        bfr.readLine(); // escape the header
        while ((line = bfr.readLine()) != null) {
            String[] attributes = line.split(",");
            String username = attributes[0];
            String password = attributes[1];

            // need another method to get customer's conversation filenames
            String[] conversationFilenames = attributes[2].split(";"); // e.g [Tim&&target.csv]

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
        bfr.readLine(); // escape the header
        while ((line = bfr.readLine()) != null) {
            allLines.add(line);
        }
        return allLines;
    }


    public Map<String, Store> readStores() throws IOException {

        String storeFilename = "./src/stores.csv";
        BufferedReader bfr = new BufferedReader(new FileReader(storeFilename));
        String line;

        // escape the header
        while ((line = bfr.readLine()) != null) {
            String[] attributes = line.split(",");
            String storeName = attributes[0];
            String seller = attributes[2];
            readSellers();

            List<String> productList = new ArrayList<>();
            List<Double> priceList = new ArrayList<>();
            List<Integer> amountList = new ArrayList<>();

            if (attributes[1].length() == 1) { // if only have one product

                String[] productInfo = attributes[1].split("-"); // if this store only have one product
                String product = productInfo[0];
                Double price = Double.parseDouble(productInfo[1]);
                Integer amount = Integer.parseInt(productInfo[2]);

                productList.add(product);
                priceList.add(price);
                amountList.add(amount);

            } else { // if it has multiple products

                String[] allProducts = attributes[1].split(";");

                for (int i = 0; i < allProducts.length; i++) {
                    String[] productsInfo = allProducts[i].split("-");
                    for (int j = 0; j < productsInfo.length; j++) {
                        productList.add(productsInfo[0]);
                        priceList.add(Double.parseDouble(productsInfo[1]));
                        amountList.add(Integer.parseInt(productsInfo[2]));
                    }
                }
            }

            readSellers(); // I am not sure after I call this method inside this class
                          // whether all the seller will be load in map
            Store store = new Store(storeName, productList, priceList,
                    amountList, sellerMap.get(seller));
            storeMap.put(storeName, store);

        }
        bfr.close();

        return storeMap;
    }

    public Map<String, Seller> readSellers() throws IOException {

        String sellerFilename = "./src/sellers.csv"; // this is my path, you can change
        BufferedReader bfr = new BufferedReader(new FileReader(sellerFilename));
        String line;
        String[] participants;

        bfr.readLine(); // escape the header
        while ((line = bfr.readLine()) != null) {
            String[] attributes = line.split(",");
            String username = attributes[0];
            String password = attributes[1];
            String[] conversationFilenames = attributes[2].split(";"); //e.g Jimmy&&Tim.csv

            String[] store = attributes[3].split(";"); // need another method to get stores of the seller

            for (String filenames : conversationFilenames) {
                participants = filenames.split("&&");
                String recipient = participants[1];
            }

            Seller seller = new Seller(username, password);
            sellerMap.put(username, seller);

        }
        bfr.close();

        return sellerMap;
    }


    // all the user and store should be load into application after the three methods above

    public List<String> getBlockList() throws IOException { // this method is to find a specific user's block list
        Login login = new Login();  // I am trying to get the user who are using the
        User user = login.user;    // application from login interface, not sure about this
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
                    String[] block = attr[3].split(";");
                    for (int i = 0; i < block.length; i++) {
                        blockedList.add(block[i]);
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
                    String[] block = attr[3].split(";");
                    for (int i = 0; i < block.length; i++) {
                        blockedList.add(block[i]);
                    }
                }
            }
            bfr.close();
        }
        return blockedList;
    }

    public List<String> getInvisList() throws IOException { // to find a specific user's invisible list
        Login login = new Login();
        User user = login.user;
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
                    String[] invisible = attr[4].split(";");
                    for (int i = 0; i < invisible.length; i++) {
                        invisList.add(invisible[i]);
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
                    String[] invisible = attr[3].split(";");
                    for (int i = 0; i < invisible.length; i++) {
                        invisList.add(invisible[i]);
                    }
                }
            }
            bfr.close();
        }
        return invisList;
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
            String isDeleted = attr[3];

            String newLine = timestampStr + username + message + isDeleted;
            messageList.add(newLine);
        }
        bfr.close();

        return messageList;
    }

}
