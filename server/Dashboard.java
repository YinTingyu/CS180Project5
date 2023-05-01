package Project5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Scanner;
/**
 * This class will allow a user to view a dashboard based on user type
 * catered to the statistics that each user type will want to see
 *
 * <p>Purdue University -- CS18000 -- Spring 2023 -- Project 4
 *
 * @author Srinath Dantu
 * @version April 9, 2023
 */
public class Dashboard {
    private User user;
    private ArrayList<String> finalResult;

    public Dashboard(User user) throws IOException {
        this.user = user;
        this.finalResult = new ArrayList<>();
        dashSetup();
        DashPrint();
        System.out.print("Would you like to like to sort the dashboard?\n1) Yes\n2) No\n");
        Scanner input = new Scanner(System.in);
        int choice = input.nextInt();
        if (choice == 1){
            DashArrange();
            DashPrint();
        }
    }

    public void dashSetup() throws IOException {
        if (user.getRole().equals("Seller")) {
            // Open the customers.csv file and create a BufferedReader object to read it
            File file = new File("customers.csv");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // Read the first line of the file
            String line = bufferedReader.readLine();

            // Loop through the file, line by line
            while (line != null) {
                String[] arr1 = null;
                try{
                    String[] arr = line.split(","); // Split the line into an array of data points
                    arr1 = arr[2].split(";"); // Split the third data point
                }catch (ArrayIndexOutOfBoundsException ignored){}
                // into an array of conversation filenames

                // Loop through each conversation filename in the array
                if (arr1 != null) {
                    for (String s : arr1) {
                        // If the conversation file contains messages sent by the user being checked,
                        // create a TreeMap to count the occurrence of each word in the conversation
                        if (s.contains(user.getUsername())) {
                            TreeMap<String, Integer> mostCommonWords = new TreeMap<>();

                            // Open the conversation file and create a BufferedReader object to read it
                            File conversationFile = new File(s);
                            FileReader conversationFileReader = new FileReader(conversationFile);
                            BufferedReader conversationBufferedReader = new BufferedReader(conversationFileReader);

                            // Read the first line of the conversation file
                            String conversation = conversationBufferedReader.readLine();
                            int i = 0;
                            String str = "Customer: ";
                            // Loop through the conversation file, line by line
                            while (conversation != null) {
                                String[] conversationLines = conversation.split(",");

                                // If this is the first line of the conversation,
                                // determine which user the customer is talking to
                                if (i == 1) {
                                    if (!conversationLines[0].equals(user.getUsername())) {
                                        str += conversationLines[0] + " || Most Common Words: ";
                                    } else {
                                        str += conversationLines[1] + " || Most Common Words: ";
                                    }
                                } else if (i > 1){
                                    String[] messageContents = conversationLines[3].split(" ");

                                    // Loop through each word in the message and count its occurrence in the conversation
                                    for (int j = 0; j < messageContents.length; j++) {
                                        if (!messageContents[j].equalsIgnoreCase("and") &&
                                                !messageContents[j].equalsIgnoreCase("the") &&
                                                !messageContents[j].equalsIgnoreCase("a") &&
                                                !messageContents[j].equalsIgnoreCase("an") &&
                                                !messageContents[j].equalsIgnoreCase("to") &&
                                                !messageContents[j].equals("")) {
                                            if (!mostCommonWords.containsKey(messageContents[j])) {
                                                mostCommonWords.put(messageContents[j], 1);
                                            } else {
                                                mostCommonWords.replace(messageContents[j],
                                                        mostCommonWords.get(messageContents[j]) + 1);
                                            }
                                        }
                                    }
                                }

                                // Determine which words were the most common in the conversation and add them to the final result string

                                i++;
                                conversation = conversationBufferedReader.readLine();
                            }
                            int size = mostCommonWords.size();
                            int count = 0;
                            if (size >= 5) {
                                for (Map.Entry<String, Integer> entry : mostCommonWords.entrySet()) {
                                    if (count > 4) break;
                                    if (count == 4) {
                                        str += entry.getKey() + " || Total Messages Sent: ";
                                    } else {
                                        str += entry.getKey() + ", ";
                                    }
                                    count++;
                                }
                            } else { //will print out all the most common words if the size of the hash map
                                // is less than 5
                                for (Map.Entry<String, Integer> pair : mostCommonWords.entrySet()) {
                                    if (count == size - 1) {
                                        str += pair.getKey() + " || Total Messages Sent: ";
                                    } else {
                                        str += pair.getKey() + ", ";
                                    }
                                    count++;
                                }
                            }
                            int lines = 0;
                            Scanner scanner = new Scanner(conversationFile);
                            while (scanner.hasNextLine()) { //gets total number of messages in file
                                scanner.nextLine();
                                lines++;
                            }
                            scanner.close();
                            str += Integer.toString((lines - 2));
                            finalResult.add(str);
                        }
                    }
                }
                line = bufferedReader.readLine();
            }
        } else {
            // Read the seller details from the "sellers.csv" file
            File file = new File("sellers.csv");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine(); // Read each line of csv file

            while (line != null) {
                String[] arr1 = null;
                try{
                    String[] arr = line.split(","); // Split the line into separate elements
                    arr1 = arr[2].split(";"); // Split the file names by "$!"
                }catch (ArrayIndexOutOfBoundsException ignored){}

                if (arr1 != null) {
                    for (String s : arr1) {
                        if (s.contains(user.getUsername())) {
                            // Initialize HashMaps to keep track of messages sent and received
                            HashMap<String, Integer> storesMessagesReceived = new HashMap<>();
                            HashMap<String, Integer> userMessagesSent = new HashMap<>();

                            // Read the conversation file for this store
                            File conversationFile = new File(s);
                            FileReader conversationFileReader = new FileReader(conversationFile);
                            BufferedReader conversationBufferedReader = new BufferedReader(conversationFileReader);
                            String conversation = conversationBufferedReader.readLine();
                            String str = "";
                            int i = 0;
                            while (conversation != null) {
                                String[] conversationLines = conversation.split(","); // Split the
                                // elements of the conversation file
                                if (i > 1) {
                                    if (!conversationLines[2].equals(user.getUsername())) {
                                        // Increment the number of messages received by the store
                                        if (!storesMessagesReceived.containsKey(conversationLines[1])) {
                                            storesMessagesReceived.put(conversationLines[1], 1);
                                        } else {
                                            storesMessagesReceived.replace(conversationLines[1],
                                                    storesMessagesReceived.get(conversationLines[1]) + 1);
                                        }
                                    } else {
                                        // Increment the number of messages sent by the user
                                        if (!userMessagesSent.containsKey(conversationLines[1])) {
                                            userMessagesSent.put(conversationLines[1], 1);
                                        } else {
                                            userMessagesSent.replace(conversationLines[1],
                                                    userMessagesSent.get(conversationLines[1]) + 1);
                                        }
                                    }
                                }
                                i++;
                                conversation = conversationBufferedReader.readLine();
                            }
                            // Add the message count for each store to the final result
                            for (Map.Entry<String, Integer> entry : storesMessagesReceived.entrySet()) {
                                str += "Store: " + entry.getKey() + ", Messages Received: " + entry.getValue() + " || ";
                            }
                            // Add the message count for the user to the final result
                            for (Map.Entry<String, Integer> entry : userMessagesSent.entrySet()) {
                                str += "Store: " + entry.getKey() + ", Messages Sent by User: " + entry.getValue();
                            }

                            finalResult.add(str); // Add the message count for this conversation to the final result
                        }
                    }
                }
                    line = bufferedReader.readLine();
            }
        }
    }

        public void DashArrange(){
            Scanner input = new Scanner(System.in); // create a Scanner object to read input from user
            int choice = -1;

// check if the user is a seller
            if (user instanceof Seller){
                do {
                    System.out.print("Choose sort: \n1) Alphabetical Sort\n2) Message Number Sort(Least to Most)\n");
                    choice = input.nextInt(); // read user's choice
                    if (choice == 1){
                        // sort the list of results alphabetically by customer name
                        Comparator<String> customerComparator = new Comparator<String>() {
                            @Override
                            public int compare(String customer1, String customer2) {
                                String[] parts1 = customer1.split(" \\|\\| ");
                                String[] parts2 = customer2.split(" \\|\\| ");
                                String name1 = parts1[0].substring(10); // extract the name from the first part of the string
                                String name2 = parts2[0].substring(10);
                                return name1.compareTo(name2);
                            }
                        };
                        finalResult.sort(customerComparator);
                    } else if (choice == 2) {
                        // sort the list of results by message count
                        Comparator<String> messageComparator = new Comparator<String>() {
                            @Override
                            public int compare(String customer1, String customer2) {
                                String[] parts1 = customer1.split(" \\|\\| ");
                                String[] parts2 = customer2.split(" \\|\\| ");
                                int index1 = parts1[2].lastIndexOf(":") + 2; // extract the message count from the third part of the string
                                int index2 = parts2[2].lastIndexOf(":") + 2;
                                int messages1 = Integer.parseInt(parts1[2].substring(index1)); // convert the message count to an int
                                int message2 = Integer.parseInt(parts2[2].substring(index2));
                                return Integer.compare(messages1, message2); // compare the message counts
                            }
                        };
                        finalResult.sort(messageComparator);
                    }else {
                        System.out.println("That is not an option!");
                    }
                } while (choice != 1 && choice != 2); // repeat until the user enters a valid choice
            } else {
                // do-while loop to allow user to choose sorting option until a valid choice is made
                do {
                    // display menu of sorting options
                    System.out.println("Choose Sort: \n1) Messages received from stores(Most to Least)\n" +
                            "2) Messages received from stores(Least to Most)\n" +
                            "3) Messages sent by user to stores(Most to Least)\n" +
                            "4) Messages sent by user to stores(Least to Most)");
                    // read user's choice of sorting option
                    choice = input.nextInt();
                    if (choice == 1){
                        // sort finalResult list based on number of messages received by stores (most to least)
                        Comparator<String> messageComparator = new Comparator<String>() {
                            @Override
                            public int compare(String customer1, String customer2) {
                                String s1 = customer1.split("Messages Received: ")[1].split(" ")[0];
                                String s2 = customer2.split("Messages Received: ")[1].split(" ")[0];
                                int message1 = Integer.parseInt(s1);
                                int message2 = Integer.parseInt(s2);
                                return message2 - message1;
                            }
                        };
                        finalResult.sort(messageComparator);
                    } else if (choice == 2) {
                        // sort finalResult list based on number of messages received by stores (least to most)
                        Collections.sort(finalResult, new Comparator<String>() {
                            @Override
                            public int compare(String customer1, String customer2) {
                                String message1 = customer1.split("Messages Received: ")[1].split(" \\|\\| ")[0];
                                String message2 = customer2.split("Messages Received: ")[1].split(" \\|\\| ")[0];
                                int num1 = Integer.parseInt(message1);
                                int num2 = Integer.parseInt(message2);
                                return num2 - num1;
                            }
                        });
                    } else if (choice == 3) {
                        // sort finalResult list based on number of messages sent by user to stores (most to least)
                        Comparator<String> messageComparator = new Comparator<String>() {
                            @Override
                            public int compare(String customer1, String customer2) {
                                String s1 = customer1.split("Messages Sent by User: ")[1].split(" ")[0];
                                String s2 = customer2.split("Messages Sent by User: ")[1].split(" ")[0];
                                int message1 = Integer.parseInt(s1);
                                int message2 = Integer.parseInt(s2);
                                return message2 - message1;
                            }
                        };
                        finalResult.sort(messageComparator);
                    } else if (choice == 4) {
                        // sort finalResult list based on number of messages sent by user to stores (least to most)
                        Collections.sort(finalResult, new Comparator<String>() {
                            @Override
                            public int compare(String customer1, String customer2) {
                                String message1 = customer1.split("Messages Sent by User: ")[1].split(" \\|\\| ")[0];
                                String message2 = customer2.split("Messages Sent by User: ")[1].split(" \\|\\| ")[0];
                                int num1 = Integer.parseInt(message1);
                                int num2 = Integer.parseInt(message2);
                                return num2 - num1;
                            }
                        });
                    }
                    else {
                        // display error message if user's choice is not valid
                        System.out.println("That is not an option!");
                    }
                } while (choice != 1 && choice != 2 && choice != 3 && choice != 4);
            }
        }

    public void DashPrint(){
        for (String s : finalResult) {
            System.out.println(s);
        }
    }
}