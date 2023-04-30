package core;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import javax.lang.model.util.ElementScanner14;

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

    private static final String CONFIRMATION_CODE = "OKAY";
    private static final String CUSTOMER_TYPE = "Customer";
    private static final String SELLER_TYPE = "Seller";


    private static ArrayList<Customer> customers; //official fat list of customer accounts
    private static ArrayList<Seller> sellers; //official fat list of seller accounts
    private static ArrayList<Store> stores; //list of store accounts that will be built once the program starts
    private static String customersFileName = "customers.csv";
    private static String sellersFileName = "sellers.csv";
    private static HashMap<User, String> tempBlocked;
    private static HashMap<User, String> tempInvis;
    private static User currentAccount;
    private static Store currentStore;
    private static ArrayList<ServerThread> serverThreads;

    private ServerSocket serverSocket;
    private Socket clientSocket;

    public ServerThread(ServerSocket serverSocket, Socket clientSocket) {
        this.serverSocket = serverSocket;
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            
            //determine action type (1 - create new account, 2 - log in, 3 - exit)
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
            
            System.out.println("Serverthread initiated");

            //****************
            while(true)
            {
                //code to construct all data objects
                tempBlocked = new HashMap<User, String>();
                tempInvis = new HashMap<User, String>();
        
                customers = createCustomers("customers.csv");
                sellers = createSellers("sellers.csv");
                stores = createStores(sellers);
        
                refreshUsers(tempBlocked, tempInvis);

                String firstAction = in.readLine(); //determineAccountActionType(in);

                if(firstAction.equals(CREATE_NEW_ACCOUNT_OPTION_CODE))//user is creating a new account
                {
                    createNewAccount(in, out);
                } else if(firstAction.equals(LOG_IN_OPTION_CODE))//user is logging in
                {
                    if(logIn(in))//log in success
                    {
                        while(true)
                        {
                            int actionType = 1; //determineActionType(in);

                            if(actionType == 1)//user is opening message history
                            {
                                int option;
                                User other = selectUser(in);
                                if(currentAccount.getRole().equals("Seller"))
                                {
                                    //currentStore = selectStore(in, (Seller) currentAccount);
                                }
                                if(other == null) //if user has been blocked, exit
                                {
                                    break;
                                }
                                do
                                {
                                    //ConversationHistory currentConversation = openConversation(in, other);
                                    //option = determineConversationAction(in);
                                    switch(option)
                                    {
                                        case 1: //user is sending a message
                                            //sendMessage(in, currentConversation, currentStore.getStoreName());
                                            break;
                                        case 2: //user is editing a message
                                            //editMessage(in, currentConversation);
                                            break;
                                        case 3: //user is deleting a message
                                            //deleteMessage(in, currentConversation);
                                        case 4: //upload txt
                                            System.out.println("Enter name of text file that you would like " +
                                                    "to upload(must only contain message contents): ");
                                            //in.nextLine();
                                            //String fileName = in.nextLine();
                                            System.out.println("Enter the store that you would " +
                                                    "like to send the message to/from: ");
                                            //String store = in.nextLine();
                                            //FileImport fileImport = new FileImport(fileName, store, currentAccount);
                                        case 5: //exit
                                            break;
                                    }
                                } while(option != 5);

                            } else if(actionType == 2) {//user is blocking
                                //blockUser(in);
                            } else if(actionType == 3) {//user is becoming invisible to someone
                                //invisibleUser(in);
                            }
                            else if(actionType == 4)//user is opening up dashboard
                            {
                                try
                                {
                                    //Dashboard db = new Dashboard(currentAccount);
                                } catch(IOException e)
                                {
                                    e.printStackTrace();
                                }

                            }
                            else if (actionType == 5)
                            {
                                //FileExport fileExport = new FileExport(currentAccount);
                            }
                            else if(actionType == 6)//user is exiting
                            {
                                //System.out.println(MESSAGE_LOGGED_OUT);
                                break;
                            }
                        }
                    }
                    else {} //do nothing if log in fails
                } else if(firstAction.equals(EXIT_FIRST_MENU_OPTION_CODE))//user is ending the program
                {
                    //System.out.println(MESSAGE_GOODBYE);
                    break;
                }
            }

            //****************** 

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void createNewAccount(BufferedReader in, PrintWriter out) {
        try {
            while(true) {
                boolean done = false;
                String name;
                do {
                    //receive user's account name
                    name = in.readLine();
                    if(name.contains(","))
                    {
                        //implement later
                        //System.out.println(MESSAGE_INVALID_ACCOUNT_NAME);
                    } else if(name.contains(" "))
                    {
                        //System.out.println(MESSAGE_INVALID_ACCOUNT_NAME);
                    }else if(checkForDuplicates(name))
                    {
                        //System.out.println(MESSAGE_DUPLICATE_ACCOUNT_NAME);
                    } else
                    {
                        sendConfirmation(out);
                        done = true;
                    }
                } while(!done);

                done = false;
                String password = "";
                do {
                    //client should now prompt for a password
                    //client should ask for password twice to verify the password is valid
                    String password1 = in.readLine();
                    password = password1;
                } while(!done);

                done = false;

                //client should now ask for the account type
                String accountType = in.readLine();
                if(accountType.equals(CUSTOMER_TYPE)) {
                    Customer newCustomer = new Customer(name, password);
                    customers.add(newCustomer);
                    writeAccountToFile(newCustomer, customersFileName);
                    return;
                } else {
                    Seller newSeller = new Seller(name, password);
                    sellers.add(newSeller);
                    writeAccountToFile(newSeller, sellersFileName);
                    return;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static int determineAccountActionType(BufferedReader in)
    {
        while(true)
        {
            System.out.println(PROMPT_ACCOUNT_ACTION);
            String input = in.nextLine();
            int type;
            try
            {
                type = Integer.parseInt(input);
                switch(type)
                {
                    case 1: case 2: case 3:
                    return type;
                    default:
                        System.out.println(MESSAGE_ERROR_ACCOUNT_ACTION_TYPE);
                }
            } catch(NumberFormatException e)
            {
                System.out.println(MESSAGE_ERROR_ACCOUNT_ACTION_TYPE);
            }
        }
    }

    //this is a utility method that will attempt to cast a string to an integer. If unsuccessful, it will return a -1
    private static int castToInt(String str) {
        int result = -1;
        try{
            Integer.parseInt(str);
        } catch(NumberFormatException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    //this is a utility method and will check if the int is within the correct range
    private static boolean isInRange(int value, int lower, int upper) {
        if(value >= lower && value <= upper)
        {
            return true;
        } else
        {
            return false;
        }
    }

    //utility method to write all items held in the data structures to CSV files
    private static void writeAllToCSV()
    {
        writeEntireCustomerList(customersFileName, customers);
    }

    private static void writeEntireCustomerList(String filename, ArrayList<Customer> customers)
    {
        try
        {
            File f = new File(filename);
            PrintWriter pw = new PrintWriter(f);
            pw.write("username,password,conversations,blocklist,invisList\n");
            for(Customer customer: customers)
            {
                String str = customer.getUsername() + ",";
                str += customer.getPassword() + ",";
                //str += getConversationsString(customer.getConversations()) + ",";
                //str += getBlockedUsersString(customer.getBlockedList()) + ",";
                //str += getInvisibleListString(customer.getInvisibleToList());
                pw.write(str+"\n");
                pw.flush();
            }
        } catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void writeAccountToFile(User account, String filename)
    {
        try
        {
            File outputFile = new File(filename);
            BufferedWriter bfw = new BufferedWriter(new FileWriter(outputFile, true));
            String str = account.getUsername() + ",";
            str += account.getPassword() + ",";
            str += getConversationsString(account.getConversations()) + ",";
            str += getBlockedUsersString(account.getBlockedList()) + ",";
            str += getInvisibleListString(account.getInvisibleToList());

            if(account.getRole().equals("Seller"))
            {
                Seller temp = (Seller) account;
                str += "," + getStoreListString(temp.getStores());
            }
            ;
            bfw.write(str+"\n");
            bfw.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    private static ArrayList<Customer> createCustomers(String filename) //create arraylist of customers, ignores blocked and invisLists
    {
        try
        {
            File customersFile = new File(filename);
            BufferedReader bfr = new BufferedReader(new FileReader(customersFile));
            ArrayList<Customer> customers = new ArrayList<Customer>();
            //throw away first line
            bfr.readLine();
            while(true)
            {
                String str = bfr.readLine();
                if(str != null && !str.equals(""))
                {
                    String name = str.substring(0, str.indexOf(","));
                    str = str.substring(str.indexOf(",") + 1);
                    String password = str.substring(0, str.indexOf(","));
                    str = str.substring(str.indexOf(",") + 1);
                    String conversationsString = str.substring(0, str.indexOf(","));
                    str = str.substring(str.indexOf(",") + 1);

                    ArrayList<String> conversationCSVs = parseStrings(conversationsString);
                    HashMap<String, ConversationHistory> userConversations = new HashMap<String, ConversationHistory>();
                    if(conversationCSVs != null && conversationCSVs.size() >= 1)
                    {
                        userConversations = constructConversations(conversationCSVs, "Customer");
                    } else
                    {
                        userConversations = null;
                    }


                    Customer newCustomer = new Customer(name, password);
                    customers.add(newCustomer);
                    User.usersByUsername.put(newCustomer.getUsername(), newCustomer);

                } else
                {
                    return customers;
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private static ArrayList<Seller> createSellers(String filename)
    {
        try
        {
            File sellersFile = new File(filename);
            BufferedReader bfr = new BufferedReader(new FileReader(sellersFile));
            ArrayList<Seller> sellers = new ArrayList<Seller>();
            //throw away first line
            bfr.readLine();
            while(true)
            {
                String str = bfr.readLine();
                if(str != null && !str.equals(""))
                {
                    String name = str.substring(0, str.indexOf(","));
                    str = str.substring(str.indexOf(",") + 1);
                    String password = str.substring(0, str.indexOf(","));
                    str = str.substring(str.indexOf(",") + 1);
                    
                    Seller newSeller = new Seller(name, password);
                    sellers.add(newSeller);
                    User.usersByUsername.put(newSeller.getUsername(), newSeller);

                } else
                {
                    return sellers;
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private static ArrayList<Store> createStores(ArrayList<Seller> sellers)
    {
        ArrayList<Store> stores = new ArrayList<Store>();
        for(Seller seller: sellers)
        {
            System.out.println(seller.getStores());
            if(seller.getStores() != null)
            {
                for(Store store: seller.getStores())
                {
                    stores.add(store);
                }
            } else
            {
                return stores;
            }
        }
        return stores;
    }


    private static void refreshUsers(HashMap<User, String> tempBlocked, HashMap<User, String> tempInvis)
    {
        for(Customer c: customers)
        {
            String blockedUsersString = tempBlocked.get(c);
            if(blockedUsersString != null && !blockedUsersString.equals(""))
            {
                ArrayList<String> separatedBlockedUsers = parseStrings(blockedUsersString);
                ArrayList<User> userBlockedList = new ArrayList<User>();
                if(separatedBlockedUsers != null && separatedBlockedUsers.size() >= 1)
                {
                    userBlockedList = constructBlockedList(separatedBlockedUsers);
                } else
                {
                    userBlockedList = null;
                }
                c.setBlockedList(userBlockedList);
            }

            String invisibleToUsersString = tempInvis.get(c);
            if(invisibleToUsersString != null && !invisibleToUsersString.equals(""))
            {
                ArrayList<String> separatedInvisibleToUsers = parseStrings(invisibleToUsersString);
                ArrayList<User> userInvisibleList = new ArrayList<User>();
                if(separatedInvisibleToUsers != null && separatedInvisibleToUsers.size() >= 1)
                {
                    userInvisibleList = constructInvisibleToList(separatedInvisibleToUsers);
                } else
                {
                    userInvisibleList = null;
                }
                c.setInvisibleToList(userInvisibleList);
            }
        }

        for(Seller s: sellers)
        {
            String blockedUsersString = tempBlocked.get(s);
            if(blockedUsersString != null && !blockedUsersString.equals(""))
            {
                ArrayList<String> separatedBlockedUsers = parseStrings(blockedUsersString);
                ArrayList<User> userBlockedList = new ArrayList<User>();
                if(separatedBlockedUsers != null && separatedBlockedUsers.size() >= 1)
                {
                    userBlockedList = constructBlockedList(separatedBlockedUsers);
                } else
                {
                    userBlockedList = null;
                }
                s.setBlockedList(userBlockedList);
            }

            String invisibleToUsersString = tempInvis.get(s);
            if(invisibleToUsersString != null && !invisibleToUsersString.equals(""))
            {
                ArrayList<String> separatedInvisibleToUsers = parseStrings(invisibleToUsersString);
                ArrayList<User> userInvisibleList = new ArrayList<User>();
                if(separatedInvisibleToUsers != null && separatedInvisibleToUsers.size() >= 1)
                {
                    userInvisibleList = constructInvisibleToList(separatedInvisibleToUsers);
                } else
                {
                    userInvisibleList = null;
                }
                s.setInvisibleToList(userInvisibleList);
            }
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

    private static String listToString(ArrayList<String> list) //this method does the reverse of parseStrings
    {
        String result = "";
        if(list == null || list.size() == 0)
        {
            return result;
        } else
        {
            for(int i = 0; i < list.size(); i++)
            {
                result += list.get(i);
                if(i != list.size() - 1)
                {
                    result += ";";
                }
            }
            return result;
        }
    }

    //utility method to send the confirmation
    private static void sendConfirmation(PrintWriter out)
    {
        out.println(CONFIRMATION_CODE);
    }

    private static boolean checkForDuplicates(String name) //return true if an account with that name exists already
    {
        for(User e: customers)
        {
            if(e.getUsername().equals(name))
                return true;
        }
        for(User e: sellers)
        {
            if(e.getUsername().equals(name))
                return true;
        }
        return false;
    }

}