package utils;

import core.Customer;
import core.Seller;
import core.Store;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVReaderTest {
    public static Map<String, Customer> customerMap = new HashMap<>();
    public static Map<String, Store> storeMap = new HashMap<>();
    public static Map<String, Seller> sellerMap = new HashMap<>();
    public Map<String, List<String>> blockMap = new HashMap<>();

    public static void main(String[] args) throws IOException {

        // test if the customers have been correctly loaded from the csv file
        CSVReader customerReader = new CSVReader();
        customerMap = customerReader.readSCustomers();
        for (Map.Entry<String, Customer> entry : customerMap.entrySet()) {
            String username = entry.getKey();
            Customer customer = entry.getValue();
            System.out.println(username + ": " + customer.getPassword() + ", ");
        }

        // test if the stores have been correctly loaded from the csv file
        CSVReader storeReader = new CSVReader();
        storeMap = storeReader.readStores();
        for (Map.Entry<String, Store> entry : storeMap.entrySet()) {
            String storeName = entry.getKey();
            Store store = entry.getValue();
            System.out.println("storeName: " + storeName + " product: " + store.getProduct() +
                    " amount: " + store.getAmountAvailable() + " price: " + store.getPrice());
        }

        // test if the sellers have been correctly loaded from the csv file
        CSVReader sellerReader = new CSVReader();
        sellerMap = sellerReader.readSellers();
        for (Map.Entry<String, Seller> entry : sellerMap.entrySet()) {
            String username = entry.getKey();
            Seller seller = entry.getValue();
            System.out.println(username + ": " + seller.getPassword() + ", ");
        }

    }
}
