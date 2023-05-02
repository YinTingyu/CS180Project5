package view;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.SwingUtilities;

/**
 * This class is the server that will interact with the server
 * This class will display the GUI and menus and send requests to the server
 *
 * <p>Purdue University -- CS18000 -- Spring 2023 -- Project 5
 *
 * @author Oliver Wang
 * @version April 18, 2023
 */

public class Client {

    //Codes
    private static final String CREATE_NEW_ACCOUNT_OPTION_CODE = "AA01";
    private static final String LOG_IN_OPTION_CODE = "AA02";
    private static final String EXIT_FIRST_MENU_OPTION_CODE = "AA03";

    private static final String CONFIRMATION_CODE = "OKAY";
    private static final String REJECTION_CODE = "NOT OKAY";
    private static final String FULL_EXIT_CODE = "FULL EXIT";
    private static final String CUSTOMER_TYPE = "Customer";
    private static final String SELLER_TYPE = "Seller";

    private static int portnumber = 5555;
    private static String hostname = "localhost";
    public static void main(String [] args) {

        Scanner in = new Scanner(System.in);

        try {
            Socket socket = new Socket(hostname, portnumber);

            BufferedReader bfr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            
            LoginGUI gui = new LoginGUI(socket);
            gui.run();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
