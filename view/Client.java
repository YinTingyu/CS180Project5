package view;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

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
    private static int portnumber = 5555;
    private static String hostname = "localhost";
    public static void main(String [] args) {

        Scanner in = new Scanner(System.in);

        try {
            Socket socket = new Socket(hostname, portnumber);
            BufferedReader bfr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            
            System.out.println("What do you want to send to the server?");
            String response = in.nextLine();
            //display menu here ***********************

            //code here

            //*************************
            

            //**********************create new account

            //code here


            //******************************************


            //****************Log in

            //code here

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

        }
    }

}
