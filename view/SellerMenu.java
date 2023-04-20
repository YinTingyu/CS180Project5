package view;

import core.Seller;
import core.Store;
import utils.CSVReader;
import utils.CSVWriter;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SellerMenu {
    private static final String PROMPT_ACTION = "What do you want to do?";
    private static final String SEND_MESSAGE_CHOICE = "Send a message";
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


    public void run() throws IOException {

        Login login = new Login();

        CSVReader csvReader = new CSVReader();
        CSVWriter CSVWriter = new CSVWriter();
        Map<String, Seller> sellerMap = csvReader.readSellers();
        Map<String, Store> storeMap = csvReader.readStores();


        Object[] menuONeOptions = {SEND_MESSAGE_CHOICE, BLOCK_CHOICE, INVISIBLE_CHOICE,
                VIEW_ALL_STORES, SEARCH_USER, LOG_OUT};

        String[] sellerList = sellerMap.keySet().toArray(new String[0]);
        //blockList = csvReader.getBlockList(); // load all the blocked users
        //invisList = csvReader.getInvisList(); // load all the invisible users

        // friend list are set as all the sellers by default (no block and no invisible)
        List<String> friendList = new ArrayList<>(Arrays.asList(sellerList));

        friendList.removeAll(blockList); // the friend list after remove users in block list
        String[] friendArray = friendList.toArray(new String[0]); // need array in JOptionPane


        boolean done = false;
        while (!done) {
            int action = JOptionPane.showOptionDialog(null, PROMPT_ACTION, "Menu",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, menuONeOptions, menuONeOptions[0]);

            switch (action) {
                case 0: // send a message
                    int friends = JOptionPane.showOptionDialog(null, WHOM_TO_CONTACT, "Send a message",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                            null, friendArray, friendArray[0]);
                    List<String> messagesList = new ArrayList<>();
                    if (friends != -1) { // if customer does not click cancel
                        String selectedSellerName = friendArray[friends];
                        Seller seller = sellerMap.get(selectedSellerName);

                        // enter message
                        message = JOptionPane.showInputDialog(null, ENTER_MESSAGE,
                                "Send a message", JOptionPane.INFORMATION_MESSAGE);

                        // both sender and recipient's conversation file need update
                        String senderFilename = csvReader.getFilenames(login.user.getUsername(), seller.getUsername());
                        CSVWriter.writeMessage(senderFilename); // write sender's conversation file
                        String recipientFilename = csvReader.getFilenames(seller.getUsername(), login.user.getUsername());
                        CSVWriter.writeMessage(recipientFilename); // write recipient's conversation file

                        messagesList = csvReader.readMessages(senderFilename);
                        for (String s : messagesList) {
                            System.out.println(s);
                        }
                    }
            }
        }
    }
}