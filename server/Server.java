package core;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is the server that will interact with the client
 * This class will perform the calculations and provide access to resources that the client requests
 *
 * <p>Purdue University -- CS18000 -- Spring 2023 -- Project 5
 *
 * @author Oliver Wang
 * @version April 18, 2023
 */


public class Server{

    private static final String CUSTOMER_OPTIONS = "1 - Select a store, 2 - "; //need to decide on options for customers
    private static final String SELLER_OPTIONS = "1 - Select a customer, 2 - , ? - Create a new store"; //need to decide on options for sellers

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
    private static ArrayList<Integer> usedPorts;

    public static void main(String [] args) {
        
        /*
        *   Integer newPort = getSmallestAvailablePort();
            ServerSocket serverSocket = new ServerSocket(newPort);
            usedPorts.add(newPort);
        */

        serverThreads = new ArrayList<ServerThread>();
        try {
            ServerSocket serverSocket = new ServerSocket(5555);
            while(true)
            {
                System.out.println("Waiting for client...");
    
                Socket socket = serverSocket.accept();
                System.out.println("Client connected!");

                ServerThread temp = new ServerThread(serverSocket, socket);
                serverThreads.add(temp);
                temp.start();
            }
        } catch(Exception e)
        {
            e.printStackTrace();
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

    private static Integer getSmallestAvailablePort()
    {
        for(Integer i = 1000; i < 9999; i++)
        {
            if(!usedPorts.contains(i))
                return i;
        }

        return null;
    }

}

