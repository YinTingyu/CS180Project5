package core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hold information of seller's store
 *
 * <p>Purdue University -- CS18000 -- Spring 2023 -- project 4
 *
 * @author Tingyu Yin
 * @version April 8, 2023
 */
public class Store {
    private String storeName;
    private List<String> product;
    private List<Double> price;
    private List<Integer> amountAvailable;
    private Seller seller;
    private Map<String, Store> storeMap = new HashMap<>();

    public Store(String storeName, List<String> product, List<Integer> amountAvailable,
                 List<Double> price, Seller seller) {
        this.storeName = storeName;
        this.product = product;
        this.price = price;
        this.amountAvailable = amountAvailable;
        this.seller = seller;
        storeMap.put(storeName, this);
    }

    public String getStoreName() {
        return storeName;
    }

    public List<String> getProduct() {
        return product;
    }

    public List<Double> getPrice() {
        return price;
    }

    public List<Integer> getAmountAvailable() {
        return amountAvailable;
    }

    public Seller getSeller() {
        return seller;
    }


    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setProduct(List<String> product) {
        this.product = product;
    }

    public void setPrice(List<Double> price) {
        this.price = price;
    }

    public void setAmountAvailable(List<Integer> amountAvailable) {
        this.amountAvailable = amountAvailable;
    }


    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public void addProduct(String newProduct, Integer amount, Double price) {
        getProduct().add(newProduct);
        getAmountAvailable().add(amount);
        getPrice().add(price);
    }

    public void deleteProduct(int index) {
        getProduct().remove(index);
        getAmountAvailable().remove(index);
        getPrice().remove(index);
    }
}


