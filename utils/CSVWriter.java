package utils;

import core.Customer;
import core.Seller;
import core.Store;
import core.User;

import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVWriter {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public User user;
    public CSVReader csvReader = new CSVReader();

    public CSVWriter(User user) {
        this.user = user;
    }

    public CSVWriter() {

    }
    private String customerFile = "./src/customers.csv";
    private String sellerFile = "./src/sellers.csv";
    private String storeFile = "./src/" + "stores" + ".csv";


    // used when user send a message
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


    // used when user edit conversations
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

    public void writeBlockList(User user, List<String> blockList) throws IOException {

        List<String> allLines = new ArrayList<>();
        // join block list to a string separate by ";"
        String blockListStr = String.join(";", blockList);
        BufferedWriter bfw;

        // rewrite everything include header
        if (user instanceof Customer) {
            bfw = new BufferedWriter(new FileWriter(customerFile));

            allLines = csvReader.readAllLines(customerFile);

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

            String header = String.format("%s,%s,%s,%s,%s",
                    "username", "password", "conversation", "blocklist", "invislist");
            bfw.write(header);
            for (String line : allLines) {
                bfw.write(line);
                bfw.newLine();
            }
            bfw.close();

        } else if (user instanceof Seller) {

            bfw = new BufferedWriter(new FileWriter(sellerFile));

            allLines = csvReader.readAllLines(sellerFile);

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

            String header = String.format("%s,%s,%s,%s,%s,%s",
                    "username", "password", "conversation", "blocklist", "invislist", "stores");
            bfw.write(header);
            for (String line : allLines) {
                bfw.write(line);
                bfw.newLine();
            }
            bfw.close();
        }


    }

    public void writeInvisList(User user, List<String> invisList) throws IOException {

        List<String> allLines = new ArrayList<>();
        String invisListStr = String.join(";", invisList);
        BufferedWriter bfw;

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
            bfw = new BufferedWriter(new FileWriter(customerFile));

            allLines = csvReader.readAllLines(customerFile);

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

            String header = String.format("%s,%s,%s,%s,%s",
                    "username", "password", "conversation", "blocklist", "invislist");
            bfw.write(header);
            for (String line : allLines) {
                bfw.write(line);
                bfw.newLine();
            }
            bfw.close();

        } else if (user.getRole().equals("Seller")) {
            bfw = new BufferedWriter(new FileWriter(sellerFile));

            allLines = csvReader.readAllLines(sellerFile);

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

            String header = String.format("%s,%s,%s,%s,%s,%s",
                    "username", "password", "conversation", "blocklist", "invislist", "stores");
            bfw.write(header);
            for (String line : allLines) {
                bfw.write(line);
                bfw.newLine();
            }
            bfw.close();
        }

    }

    public void writeStores(List<String> storesList) throws IOException {

        List<String> allLines = csvReader.readAllLines(sellerFile);
        BufferedWriter bfw = new BufferedWriter(new FileWriter(sellerFile));
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
        String header = String.format("%s,%s,%s,%s,%s,%s",
                "username", "password", "conversation", "blocklist", "invislist", "stores");
        bfw.write(header);
        for (String line : allLines) {
            bfw.write(line);
            bfw.newLine();
        }
        bfw.close();
    }

    public void writeProductName(int index, String newName) throws IOException {

        List<String> allLines = csvReader.readAllLines(storeFile);
        BufferedWriter bfw = new BufferedWriter(new FileWriter(storeFile));

        for (String line : allLines) {
            String[] attr = line.split(",");
            String[] productsInfo = attr[1].split(";");
            String[] product = productsInfo[index].split("-");
            product[0] = newName;
        }

        // rewrite everything
        String header = String.format("%s,%s-%s-%s,%s",
                "storeName", "product-amount-price", "sellerName");
        bfw.write(header);
        for (String line : allLines) {
            bfw.write(line);
            bfw.newLine();
        }
        bfw.close();
    }

    public void writeProductAmount(int index, Integer newAmount) throws IOException {

        List<String> allLines = csvReader.readAllLines(storeFile);
        BufferedWriter bfw = new BufferedWriter(new FileWriter(storeFile));
        String amountStr = String.valueOf(newAmount);
        for (String line : allLines) {
            String[] attr = line.split(",");
            String[] productsInfo = attr[1].split(";");
            String[] product = productsInfo[index].split("-");
            product[1] = amountStr;
        }

        // rewrite everything
        String header = String.format("%s,%s-%s-%s,%s",
                "storeName", "product-amount-price", "sellerName");
        bfw.write(header);
        for (String line : allLines) {
            bfw.write(line);
            bfw.newLine();
        }
        bfw.close();
    }

    public void writeProductPrice(int index, Double newPrice) throws IOException {

        List<String> allLines = csvReader.readAllLines(storeFile);
        BufferedWriter bfw = new BufferedWriter(new FileWriter(storeFile));
        String priceStr = String.valueOf(newPrice);
        for (String line : allLines) {
            String[] attr = line.split(",");
            String[] productsInfo = attr[1].split(";");
            String[] product = productsInfo[index].split("-"); // index
            product[2] = priceStr;
        }

        // rewrite everything
        String header = String.format("%s,%s-%s-%s,%s",
                "storeName", "product-amount-price", "sellerName");
        bfw.write(header);
        for (String line : allLines) {
            bfw.write(line);
            bfw.newLine();
        }
        bfw.close();
    }

    public void writeNewProduct(Store store, String name, String amount, String price) throws IOException {

        List<String> allLines = csvReader.readAllLines(storeFile);
        BufferedWriter bfw = new BufferedWriter(new FileWriter(storeFile));

        for (String line : allLines) {
            String[] attr = line.split(","); // each store's info

            if (attr[0].equals(store.getStoreName())) { // find which store added new product
                String[] products = attr[1].split(";");
                List<String> productList = Arrays.asList(products);
                String newProductInfo = String.format("%s-%s-%s", name, amount, price);
                productList.add(newProductInfo);
                String allProducts = String.join(";", productList);
                attr[1] = allProducts;
            }
        }
        // rewrite everything
        String header = String.format("%s,%s-%s-%s,%s",
                "storeName", "product-amount-price", "sellerName");
        bfw.write(header);
        for (String line : allLines) {
            bfw.write(line);
            bfw.newLine();
        }
        bfw.close();
    }

    public void updateProduct(Store store, int index) throws IOException {

        List<String> allLines = csvReader.readAllLines(storeFile);
        BufferedWriter bfw = new BufferedWriter(new FileWriter(storeFile));

        for (String line : allLines) {
            String[] attr = line.split(",");
            if (attr[0].equals(store.getStoreName())) {
                String[] products = attr[1].split(";");
                List<String> productList = Arrays.asList(products);
                productList.remove(index);
                String allProducts = String.join(";", productList);
                attr[1] = allProducts;
            }
        }

        // rewrite everything
        String header = String.format("%s,%s-%s-%s,%s",
                "storeName", "product-amount-price", "sellerName");
        bfw.write(header);
        for (String line : allLines) {
            bfw.write(line);
            bfw.newLine();
        }
        bfw.close();
    }


    public void writeLatestLogOutTime(Timestamp timestamp, User user) throws IOException {
        String tsp = timestamp.toString();
        if (user instanceof Customer) {
            String filename = "./src/" + "customers" + ".csv";
            BufferedWriter bfw = new BufferedWriter(new FileWriter(filename));
            List<String> allLines = csvReader.readAllLines(filename);

            for (int i = 0; i < allLines.size(); i++) {
                String[] attr = allLines.get(i).split(",");
                if (attr[0].equals(user.getUsername())) { // find the line of user who log out
                    attr[5] = tsp;
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

        } else if (user instanceof Seller) {
            String filename = "./src/" + "sellers" + ".csv";
            BufferedWriter bfw = new BufferedWriter(new FileWriter(filename));
            List<String> allLines = csvReader.readAllLines(filename);

            for (int i = 0; i < allLines.size(); i++) {
                String[] attr = allLines.get(i).split(",");
                if (attr[0].equals(user.getUsername())) { // find the line of user who log out
                    attr[5] = tsp;
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
        }
    }

    public void writeNewAccount(User newUser) throws IOException {
        BufferedWriter bfw;

        if (newUser instanceof Customer) {
            bfw = new BufferedWriter(new FileWriter(customerFile, true));
            String newCustomer = newUser.getUsername() + ","
                    + newUser.getPassword() + ",...,...,...";
            bfw.write(newCustomer);

        } else if (newUser instanceof Seller) {
            bfw = new BufferedWriter(new FileWriter(sellerFile, true));
            String newSeller = newUser.getUsername() + ","
                    + newUser.getPassword() + ",...,...,...";
            bfw.write(newSeller);
        }
    }
}
