package server;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import core.ConversationHistory;
import core.Customer;
import core.Seller;
import core.Store;
import core.User;

import utils.DataManager;

/**
 * This class is the server thread that will kick off each time a client is connected
 *
 * <p>Purdue University -- CS18000 -- Spring 2023 -- Project 5
 *
 * @author Oliver Wang
 * @version April 29, 2023
 */

public class ServerThread extends Thread {

    //Codes
    private static final String CREATE_NEW_ACCOUNT_OPTION_CODE = "AA01";
    private static final String LOG_IN_OPTION_CODE = "AA02";
    private static final String EXIT_FIRST_MENU_OPTION_CODE = "AA03";

    private static final String CONTACT_USER_CODE = "BB01";
    private static final String BLOCK_USER_CODE = "BB02";
    private static final String SET_INVISIBLE_CODE = "BB03";
    private static final String VIEW_DASHBOARD_CODE = "BB04";
    private static final String EXPORT_FILE_CODE = "BB05";
    private static final String IMPORT_FILE_CODE = "BB06";
    private static final String CREATE_STORE_CODE = "BB067";

    private static final String QUERY_USER_BLOCKED = "CC01";
    private static final String QUERY_USER_INVISIBLE = "CC02";
    private static final String QUERY_CONVERSATION_STRING = "CC03";

    private static final String CONFIRMATION_CODE = "OKAY";
    private static final String REJECTION_CODE = "NOT OKAY";
    private static final String FULL_EXIT_CODE = "FULL EXIT";
    private static final String REFRESH_CODE = "REFRESH";
    private static final String CUSTOMER_TYPE = "Customer";
    private static final String SELLER_TYPE = "Seller";

    private DataManager dataManager;

    private static ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    public ServerThread(ServerSocket serverSocket, Socket clientSocket, DataManager dataManager, BufferedReader in, PrintWriter out) {
        this.serverSocket = serverSocket;
        this.clientSocket = clientSocket;
        this.dataManager = dataManager;
        this.in = in;
        this.out = out;
    }

    public void run() {
        try {
            System.out.println("Serverthread initiated");
            while(true)
            {
                String firstAction = in.readLine();
                System.out.println(firstAction);

                ArrayList<String> actions = decodeString(firstAction);
                System.out.println(actions);
                if(firstAction == null || firstAction.equals("")) {
                    //do nothing
                } else if(actions.get(0).equals(CREATE_NEW_ACCOUNT_OPTION_CODE)) {//user is creating account
                
                    //if creating account, first string in the list is the code. second is the acc type. third is the name. fourth is pw
                    if(actions.get(1).equals("Customer")) {
                        String name = actions.get(2);
                        String password = actions.get(3);
                        dataManager.addNewAccount(new Customer(name, password));
                    } else {
                        String name = actions.get(2);
                        String password = actions.get(3);
                        dataManager.addNewAccount(new Seller(name, password));
                    }
                    System.out.println("new account made");
                } else if(actions.get(0).equals(LOG_IN_OPTION_CODE)) {//user is logging in
                    //if logging in, first string in the list is the code. second is the name. third is the pw
                    String name = actions.get(2);
                    String password = actions.get(3);
                    User tempUser = dataManager.getUser(name);
                    if(tempUser == null)
                    {
                        if(tempUser.authenticate(password)) {
                            //logged in
                            sendConfirmation(out);
                            currentAccount = tempUser;
                        }
                    } else {
                        sendRejection(out);
                        //do nothing
                    }
                } else if(actions.get(0).equals(EXIT_FIRST_MENU_OPTION_CODE)) { //user is exiting
                    currentAccount = null;
                    currentStore = null;
                } else if(actions.get(0).equals(CONTACT_USER_CODE)) {
                    if(actions.size()<=1) {
                        //print list of users or stores that can be contacted
                    } else {
                        //open up message log between current user and the target user
                        dataManager.getUser(actions.get(1));
                    }
                } else if(actions.get(0).equals(BLOCK_USER_CODE)) {//work on this
                    if(!(actions.size() <= 1))
                    {
                        String userName = actions.get(1);
                        String blockeeName = actions.get(2);
                        dataManager.addBlock(dataManager.getUser(userName),blockeeName);
                    }
                } else if(actions.get(0).equals(SET_INVISIBLE_CODE)) {
                    //print list of users that can be set invisible to
                } else if(actions.get(0).equals(VIEW_DASHBOARD_CODE)) {
                    //print any stats needed for the dashboard
                } else if(actions.get(0).equals(EXPORT_FILE_CODE)) {
                    //things to export the file
                    //second string is the username
                    String name = actions.get(1);
                    //third string is the other user's name
                    String otherName = actions.get(2);
                    File f = new File(name + "&&" + otherName + ".csv");
                    File otherf = new File(otherName + "&&" + name + ".csv");
                    if(f.exists()) {
                        BufferedReader bfr = new BufferedReader(new FileReader(f));
                        String str = bfr.readLine();
                        String result = "";
                        while(str!=null) {
                            result += str + ";;;";
                            str = bfr.readLine();
                        }
                        out.println(result);
                        out.flush();
                        bfr.close();
                    } else if(otherf.exists()) {
                        System.out.println(otherName + "&&" + name + ".csv");
                        BufferedReader bfr = new BufferedReader(new FileReader(otherf));
                        String str = bfr.readLine();
                        String result = "";
                        while(str!=null) {
                            result += str + ";;;";
                            str = bfr.readLine();
                        }
                        out.println(result);
                        out.flush();
                        bfr.close();
                    } else {
                        sendRejection(out);
                    }
                } else if(actions.get(0).equals(IMPORT_FILE_CODE)) {
                    //user is importing a text file to send to someone
                    //second string is senderName
                    String senderName = actions.get(1);
                    User sender = dataManager.getUser(senderName);
                    //third string is recipient
                    String recipientName = actions.get(2);
                    User receiver = dataManager.getUser(recipientName);
                    //fourth string is message
                    String message = actions.get(3);

                    String filename = senderName + "&&" + recipientName + ".csv";
                    String otherFilename = recipientName + "&&" + senderName + ".csv";
                    File other = new File(otherFilename);
                    if(other.exists()) {
                        dataManager.writeMessage(otherFilename, message, senderName);
                    } else {
                        dataManager.writeMessage(filename, message, senderName);
                    }
                    //update on the gui's
                    
                } else if(actions.get(0).equals(CREATE_STORE_CODE)) {
                    //GUI to create a store
                    //second string is storename, third is product, fourth is amount, fifth is price, sixth is seller name
                    String storeName = actions.get(1);
                    List<String> product = parseStrings(actions.get(2));
                    List<String> amountAsString = parseStrings(actions.get(3));
                    List<Integer> amount = new ArrayList<Integer>();
                    for(String str: amountAsString) {
                        amount.add(Integer.parseInt(str));
                    }
                    List<String> priceAsString = parseStrings(actions.get(4));
                    List<Double> price = new ArrayList<Double>();
                    for(String str: priceAsString) {
                        price.add(Double.parseDouble(str));
                    }
                    User seller = dataManager.getUser(actions.get(5));
                    
                    Store newStore = new Store(storeName, product, amount, price, (Seller) seller);
                    dataManager.addStore(newStore);
                } else if(actions.get(0).equals(QUERY_USER_BLOCKED)) {
                    dataManager.refresh();
                    //second string is username
                    String requesterName = actions.get(1);
                    //third string is person to look through blocklist
                    String blockingName = actions.get(2);
                    boolean isNotBlocked = !dataManager.getUser(blockingName).blockList.contains(requesterName);
                    if(isNotBlocked) {
                        sendConfirmation(out);
                    } else {
                        sendRejection(out);
                    }
                } else if(actions.get(0).equals(QUERY_USER_INVISIBLE)) {
                    dataManager.refresh();
                    //second string is username
                    String requesterName = actions.get(1);
                    //third string is person to look through blocklist
                    String invisibleName = actions.get(2);
                    boolean isNotInvis = !dataManager.getUser(invisibleName).invisList.contains(requesterName);
                    if(isNotInvis) {
                        sendConfirmation(out);
                    } else {
                        sendRejection(out);
                    }
                } else if(actions.get(0).equals(QUERY_CONVERSATION_STRING)) {
                    //second string is filename
                    String filename = actions.get(1);
                    List<String> results = dataManager.getFileContents(filename);
                    System.out.print(results);
                    out.println(results);
                    out.flush();
                }

            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static ArrayList<String> decodeString(String bigString) {//this method will break down a client message to see what its saying
        String str = bigString;
        ArrayList<String> list = new ArrayList<String>();

        if (str != null && !str.equals("null") && !str.equals("") && !str.equals("{}")) {
            boolean done = false;
            do {
                int indexOf$ = str.indexOf("$");
                if (indexOf$ != -1) {
                    list.add(str.substring(0, indexOf$));
                    str = str.substring(indexOf$ + 1); //Add one because of additional ;

                } else {
                    list.add(str);
                    done = true;
                }
            } while (!done);

            return list;

        } else {
            return list;
        }
    }

    private static ArrayList<String> parseStrings(String bigString) //this method will return a list of strings given a bigString
    {
        String str = bigString;
        ArrayList<String> list = new ArrayList<String>();

        if (str != null && !str.equals("null") && !str.equals("") && !str.equals("{}")) {
            boolean done = false;
            do {
                int indexOf$ = str.indexOf(";");
                if (indexOf$ != -1) {
                    list.add(str.substring(0, indexOf$));
                    str = str.substring(indexOf$ + 1); //Add one because of additional ;

                } else {
                    list.add(str);
                    done = true;
                }
            } while (!done);

            return list;

        } else {
            return list;
        }

    }

    //utility method to send the confirmation
    private static void sendConfirmation(PrintWriter out) {
        out.println(CONFIRMATION_CODE);
        out.flush();
        System.out.println("sent confirm");
    }

    private static void sendRejection(PrintWriter out) {
        out.println(REJECTION_CODE);
        out.flush();
        System.out.println("sent rejection");
    }

    private static void sendExit(PrintWriter out) {
        out.println(FULL_EXIT_CODE);
        out.flush();
    }

}