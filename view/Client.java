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
            
            LoginGUI gui = new LoginGUI();
            gui.run();

            System.out.println("What do you want to send to the server?");
            String response = in.nextLine();
            //display menu here ***********************



            //*************************
            

            //**********************create new account

            //once the option is selected, immediately prompt for a username
            //wait for server to return with confirmation code
            //if a rejection code is returned, reprompt for a new username
            //if a confirmation code is returned, prompt for a password
            //ask for password twice and check if both passwords match (this is optional)
            //now, ask for the account type (seller or customer)
            //done, return to main menu

            //******************************************


            //****************Log in

            //client should prompt for a username
            //wait for server response. If the response is a rejection, ask for the username again
            //if the response is a confirmation, move on to ask for the password
            //wait for server response. If the response is rejection, ask for password again
            //if the server responds with FULL_EXIT_CODE, exit to start menu (too many password attempts)
            //if the server responds with CONFIRMATION_CODE, listen for the next response
            //the next response is either SELLER_CODE or CUSTOMER_CODE, which will determine what GUI to display

            //******************** */



            //*****************customer menu

            //send message gui
            //code
            //********************

            //block user gui
            //code
            //****************

            //set invisible to user gui
            //code
            //***********

            //view dashboard gui
            //code
            //***********

            //****************** */


            //*****************Seller menu

            //send message gui
            //code
            //********************

            //create store gui
            //code
            //*****************

            //block user gui
            //code
            //****************

            //set invisible to user gui
            //code
            //***********

            //view dashboard gui
            //code
            //***********

            //****************** */

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
