package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import utils.DataManager;


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

    private static ArrayList<ServerThread> serverThreads;

    public static void main(String [] args) {

        serverThreads = new ArrayList<ServerThread>();
        try {
            DataManager dataManager = new DataManager();
            ServerSocket serverSocket = new ServerSocket(5555);
            while(true)
            {
                System.out.println("Ready for another client");
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream());

                ServerThread temp = new ServerThread(serverSocket, clientSocket, dataManager, in, out);
                serverThreads.add(temp);
                temp.start();
            }
        } catch(Exception e)
        {
            e.printStackTrace();
        }

    }

}

