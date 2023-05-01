import java.io.*;
import java.util.Scanner;
/**
 * This class will allow a user to export their conversation data to another CSV file
 *
 * <p>Purdue University -- CS18000 -- Spring 2023 -- Project 4
 *
 * @author Srinath Dantu
 * @version April 9, 2023
 */

public class FileExport {
    private String name;
    private User user;
    private String exportedFileName;
    private String fileToBeExported;

    public FileExport(User user){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the name of the user that you would like to extract the conversation history of: ");
        this.name = scanner.nextLine();
        this.user = user;
        this.exportedFileName = "Exported" + user.getUsername() + this.name + ".csv";
        this.fileToBeExported = findCSVFile(this.name);
        exportToFile(this.exportedFileName, this.fileToBeExported);
    }

    public String findCSVFile(String name){
        boolean userRole = false;
        if (user.getRole().equals("Seller")){
            userRole = true;
        }
        String file = userRole ? "sellers.csv" : "customers.csv";
        String result = null;
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line = bufferedReader.readLine();
            while (line != null){
                String[] parts = line.split(",");
                if (parts[0].equals(user.getUsername())){
                    String[] fileNames = parts[2].split(";");
                    for (String fileName : fileNames) {
                        if (fileName.contains(name)) {
                            result = fileName;
                            System.out.println(result);
                        }
                    }
                }
                line = bufferedReader.readLine();
            }
        }catch (Exception e){
            e.printStackTrace(); //only for testing
        }
        return result;
    }

    public void exportToFile(String exportedFileName, String fileToBeExported){
        try{
            BufferedReader fileReader = new BufferedReader(new FileReader(fileToBeExported));
            File file = new File(exportedFileName);
            file.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            String fileContents = fileReader.readLine();
            while (fileContents != null){
                bufferedWriter.write(fileContents);
                bufferedWriter.newLine();
                fileContents = fileReader.readLine();
            }
            bufferedWriter.flush();
        }catch (Exception e){
            e.printStackTrace(); //only for testing
            return;
        }
    }
}
