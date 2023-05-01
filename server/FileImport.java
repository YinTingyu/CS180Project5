package Project5;

import java.io.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Scanner;
import java.text.SimpleDateFormat;
/**
 * This class will allow a user to import a text file into an ongoing conversation
 *
 * <p>Purdue University -- CS18000 -- Spring 2023 -- Project 4
 *
 * @author Srinath Dantu
 * @version April 9, 2023
 */
public class FileImport {
    private String fileNameRead;
    private String store;
    private User user;
    private String desiredUser;
    private String fileWrite;

    //will take username of user, file to read from, file to write to and writes to it
    public FileImport(String fileNameRead, String store, User user){
        this.fileNameRead = fileNameRead;
        this.store = store;
        this.user = user;
        Scanner input = new Scanner(System.in);
        System.out.println("Which user would you like to send the file to?");
        this.desiredUser = input.nextLine();
        this.fileWrite = findCSVFile(this.user);
        writeToFile(this.fileWrite);
    }

    public String findCSVFile(User user){
        String fileWrite = "";
        boolean userRole = false;
        if (user.getRole().equals("Seller")){
            userRole = true;
        }
        String file = userRole ? "sellers.csv" : "customers.csv";
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line = bufferedReader.readLine();
            while(line != null){
                String[] parts = line.split(",");
                if (parts[0].equals(user.getUsername())){
                    String[] files = parts[2].split(";");
                    for (String s : files) {
                        if (s.contains(desiredUser)) {
                            fileWrite = s;
                            System.out.println(fileWrite);
                        }
                    }
                }
                line = bufferedReader.readLine();
            }
        }
        catch (Exception e){
            e.printStackTrace(); //only for testing
            return null;
        }
        return fileWrite;
    }

    public void writeToFile(String fileWrite){
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileNameRead));
            FileWriter fileWriter = new FileWriter(fileWrite, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            String line = bufferedReader.readLine();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

            // get the current date and time
            Date currentDate = new Date();

            // format the date and time using the SimpleDateFormat object
            String formattedDate = dateFormat.format(currentDate);
            //Timestamp timestamp = new Timestamp(new Date().getTime());
            String userName = user.getUsername();
            while (line != null){
                bufferedWriter.write(formattedDate + "," + store + "," + userName + "," + line);
                bufferedWriter.newLine();
                line = bufferedReader.readLine();
            }
            fileWriter.flush();
            bufferedWriter.flush();
            System.out.println("Printed to file successfully!");
        }catch (Exception e){
            e.printStackTrace(); //only for testing
            return;
        }
    }
}