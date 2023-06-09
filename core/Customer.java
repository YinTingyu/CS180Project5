package core;
import java.util.ArrayList;
import java.util.List;
/**
 * A subclass of user, implement send message, view and search sellers,
 * can get access to all the stores and purchase products
 *
 * <p>Purdue University -- CS18000 -- Spring 2023 -- project 4
 *
 * @author Tingyu Yin
 * @version April 8, 2023
 */
public class Customer extends User {

    public Customer(String email, String password) {
        super(email, password, "Customer");
    }

    public List<Seller> getAllSellers() {
        List<Seller> allSellers = new ArrayList<>();
        for (User user : getAllUsers()) {
            if (user instanceof Seller) {
                allSellers.add((Seller) user);
            }
        }
        return allSellers;
    }

    public void sendMessageToSeller(Seller recipient, String content) {
        createMessage(recipient, content);
    }

    public void deleteMessageWithSeller(Message message) {
        deleteMessage(message);
    }

    public List<Seller> searchSellers(String kwd) {
        List<Seller> matchingSellers = new ArrayList<>();
        for (User user : usersByUsername.values()) {
            if (user instanceof Seller && user.getUsername().contains(kwd)) {
                matchingSellers.add((Seller) user);
            }
        }
        return matchingSellers;
    }

    public List<Store> accessStores() {
        List<Store> allStores = new ArrayList<>();
        for (Seller seller : getAllSellers()) {
            allStores.addAll(seller.getStores());
        }
        return allStores;
    }

}

