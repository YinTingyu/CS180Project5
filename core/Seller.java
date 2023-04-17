package core;

import java.util.ArrayList;
import java.util.List;
/**
 * A subclass of user, implement send message, view and search customers,
 * create, edit and delete stores
 *
 * <p>Purdue University -- CS18000 -- Spring 2023 -- project 4
 *
 * @author Tingyu Yin
 * @version April 8, 2023
 */
public class Seller extends User {
    private List<Store> stores;
    private double income;

    public Seller(String email, String password) {
        super(email, password, "Seller");
    }

    public void addStore(String storeName, List<String> products,
                         List<Double> prices, List<Integer> amountsAvailable) {
        Store storeToAdd = new Store(storeName, products, prices, amountsAvailable, this);
        stores.add(storeToAdd);
    }


    public void deleteStore(Store storeToDelete) {
        stores.remove(storeToDelete);
    }

    public void editStore(Store store, String newStoreName, List<String> newProducts,
                          List<Double> newPrices, List<Integer> newAmountsAvailable) {
        store.setStoreName(newStoreName);
        store.setProduct(newProducts);
        store.setPrice(newPrices);
        store.setAmountAvailable(newAmountsAvailable);
    }

    public List<Customer> getAllCustomers() {
        List<Customer> allCustomers = new ArrayList<>();
        for (User user : getAllUsers()) {
            if (user instanceof Customer) {
                allCustomers.add((Customer) user);
            }
        }
        return allCustomers;
    }

    public void sendMessageToCustomer(Customer recipient, String content) {
        createMessage(recipient, content);
    }

    public void deleteMessageWithCustomer(Message message) {
        deleteMessage(message);
    }

    public List<Customer> searchCustomers(String kwd) {
        List<Customer> matchingCustomers = new ArrayList<>();
        for (User user : usersByUsername.values()) {
            if (user instanceof Customer && user.getUsername().contains(kwd)) {
                matchingCustomers.add((Customer) user);
            }
        }
        return matchingCustomers;
    }

    // sellers can get all his stores
    public List<Store> getStores() {
        return stores;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }
}

