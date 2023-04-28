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
        stores = new ArrayList<>();
        // initialize stores instance !!! otherwise you will get NullPointerError
    }



    // sellers can get all his stores
    public List<Store> getStores() {
        return stores;
    }

}

