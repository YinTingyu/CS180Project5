package core;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Test {

    public static void main(String [] args)
    {
        try {
            BufferedReader bfr = new BufferedReader(new FileReader("Greene&&Tim.csv"));
            String str = bfr.readLine();
            while(str!=null)
            {
                System.out.println(str);
                str = bfr.readLine();
            }

        } catch (Exception e){

        }
        ArrayList<String> tempList = decodeString("AA01$Bob_SDf$Hello$Customer");
        for(String e: tempList)
        {
            System.out.println(e);
        }

        String tempString = listToString(tempList);
        System.out.println(tempString);
    }
    
    private static ArrayList<String> decodeString(String bigString) {//this method will break down a client message to see what its saying
        String str = bigString;
        ArrayList<String> list = new ArrayList<String>();

        if (str != null && !str.equals("null") && !str.equals("") && !str.equals("{}")) {
            boolean done = false;
            do {
                int indexOf$ = str.indexOf("$");
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


    private static String listToString(ArrayList<String> list)
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
}
