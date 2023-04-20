import java.io.*;
import java.net.Socket;

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

    public static void main(String [] args) {
        ServerSocket serverSocket = new ServerSocket(5555);
        Socket socket = ServerSocket.accept();
        //determine action type (1 - create new account, 2 - log in, 3 - exit)
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Client connected!");
        String input = inputReader.nextLine();
        int accountChoice = castToInt(str);
        if(isInRange(accountChoice, 1, 3)) {
            System.out.println("Chosen: " + accountChoice);
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

}