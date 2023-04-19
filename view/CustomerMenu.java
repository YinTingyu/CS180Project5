package view;

import core.Customer;
import core.Seller;
import core.Store;
import utils.CSVReader;
import utils.CSVWriter;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CustomerMenu {
    private static final String PROMPT_ACTION = "What do you want to do?";
    private static final String BLOCK_CHOICE = "Block a user";
    private static final String INVISIBLE_CHOICE = "Become invisible to a user";
    private static final String VIEW_ALL_STORES = "View all the stores";
    private static final String SEARCH_USER = "Search a user";
    private static final String WHOM_TO_CONTACT = "Please select a user to contact: ";
    private static final String WHOM_TO_BLOCK = "Please select a user to block: ";
    private static final String WHOM_TO_INVISIBLE = "Whom you want to become invisible to: ";
    private static final String BLOCK_SUCCEED = "Successfully blocked this user!";
    private static final String INVISIBLE_SUCCEED = "Successfully become invisible to this user!";
    private static final String ENTER_MESSAGE = "Please enter your message: ";
    private static final String LOG_OUT = "Thanks for using our message service!";

    public String message;
    public List<String> blockList;
    public List<String> invisList;

    public void showCustomerMenu(Customer customer) throws IOException { // this is used when login and when go back
        run(customer);
    }

    public void openViewStoresWindow(Customer customer) throws IOException { // to open view all the stores GUI
        ViewStoresWindow storesWindow = new ViewStoresWindow(() -> {
            try {
                showCustomerMenu(customer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        storesWindow.run();
    }

    public void run(Customer customer) throws IOException {

        Login login = new Login();

        CSVReader csvReader = new CSVReader();
        CSVWriter CSVWriter = new CSVWriter();
        Map<String, Seller> sellerMap = csvReader.readSellers();
        Map<String, Store> storeMap = csvReader.readStores();


        Object[] menuONeOptions = {BLOCK_CHOICE, INVISIBLE_CHOICE,
                VIEW_ALL_STORES, SEARCH_USER, LOG_OUT};

        String[] sellerList = sellerMap.keySet().toArray(new String[0]);
        blockList = csvReader.getBlockList(customer); // load all the blocked users
        invisList = csvReader.getInvisList(customer); // load all the invisible users

        // friend list are set as all the sellers by default (no block and no invisible)
        List<String> friendList = new ArrayList<>(Arrays.asList(sellerList));

        friendList.removeAll(blockList); // the friend list after remove users in block list
        String[] friendArray = friendList.toArray(new String[0]); // need array in JOptionPane


        int action = JOptionPane.showOptionDialog(null, PROMPT_ACTION, "Menu",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, menuONeOptions, menuONeOptions[0]);

        switch (action) {

            case 0: // block a user ( to make other user be unable to send message to me)

                int friends = JOptionPane.showOptionDialog(null, WHOM_TO_BLOCK, "Block a user",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                        null, friendArray, friendArray[0]);
                if (friends != -1) {

                    String toBlockSellerName = friendArray[friends];
                    blockList.add(toBlockSellerName); // add new user to block list
                    friendList.remove(toBlockSellerName); // remove this user from the friend list
                    String[] newFriendArray = friendList.toArray(new String[0]);

                    // show block succeed message and new friend list
                    JOptionPane.showMessageDialog(null, BLOCK_SUCCEED,
                            "Block a user", JOptionPane.INFORMATION_MESSAGE);
                    String friendsString = String.join("\n", newFriendArray);
                    JOptionPane.showMessageDialog(null, friendsString,
                            "Friend List", JOptionPane.INFORMATION_MESSAGE);

                    // update customers.csv file
                    CSVWriter.writeBlockList("./src/customers.csv");
                }

                // how to make other user unable to send message to me??? TO BE implement

                break;

            case 1: // become invisible to a user(to make other user be unable to search me)

                friends = JOptionPane.showOptionDialog(null, WHOM_TO_INVISIBLE, "Become invisible to",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                        null, friendArray, friendArray[0]);
                if (friends != -1) {

                    String invisibleSellerName = invisList.get(friends);

                    // do we need to remove this user from the friend list ?

                    invisList.add(invisibleSellerName);
                    String[] newFriendArray = friendList.toArray(new String[0]);

                    // show block succeed message and new friend list
                    JOptionPane.showMessageDialog(null, INVISIBLE_SUCCEED,
                            "Become invisible to", JOptionPane.INFORMATION_MESSAGE);

                    // String friendsString = String.join("\n", newFriendArray);
                    // JOptionPane.showMessageDialog(null, friendsString,
                    //"Friend List", JOptionPane.INFORMATION_MESSAGE);

                    // update customers.csv file
                    CSVWriter.writeInvisList("./src/customers.csv");
                }

                break;

            case 2: // view all the stores

                openViewStoresWindow(customer);

                break;

            case 3: // search a user

                break;
            case 4: // log out
        }

    }
}
