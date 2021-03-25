import java.io.PrintWriter;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Scanner;

public class CSV2JSON {

    
    public static void main(String[] args) {
        Scanner input=new Scanner(System.in);
        Scanner maintenanceIn=null,rentalIn=null;
        PrintWriter maintOut=null,rentalOut=null;
        String maintFileNameIn, rentFileNameIn, maintFileNameOut, rentFileNameOut;

        System.out.print("Please enter the Maintenance Record with its file extention: ");
        maintFileNameIn=input.nextLine();
        maintFileNameOut=maintFileNameIn.split("\\.")[0]+".json"; //we need the escape slashes because . has a special meaning in regex
        System.out.print("Please enter the Rental Record with its file extention: ");
        rentFileNameIn=input.nextLine();
        rentFileNameOut=rentFileNameIn.split("\\.")[0]+".json";


        try{
            maintenanceIn=new Scanner(new FileInputStream(maintFileNameIn));
        }
        catch(FileNotFoundException e){
            System.out.println("\nCould not open file "+maintFileNameIn+" for reading");
            System.out.println("Please check if file exists! Program will terminate after closing all open files");
            input.close();
            System.exit(0);
        }
        try{
            maintOut=new PrintWriter(new FileOutputStream(maintFileNameOut));
        }
        catch(FileNotFoundException e){
            System.out.println("Could not create file "+maintFileNameOut+" for reading");
            System.out.println("Please check if file exists! Program will terminate after closing all open files");
            input.close();
            maintenanceIn.close();
            System.exit(0);
        }
        try{
            rentalIn=new Scanner(new FileInputStream(rentFileNameIn));
        }
        catch(FileNotFoundException e){
            System.out.println("Could not open file "+rentFileNameIn+" for reading");
            System.out.println("Please check if file exists! Program will terminate after closing all open files");
            input.close();
            maintOut.close();
            maintenanceIn.close();
            System.exit(0);
        }
        try{
            rentalOut=new PrintWriter(new FileOutputStream(rentFileNameOut));
        }
        catch(FileNotFoundException e){
            System.out.println("Could not create file "+rentFileNameOut+" for reading");
            System.out.println("Please check if file exists! Program will terminate after closing all open files");
            input.close();
            maintenanceIn.close();
            rentalIn.close();
            maintOut.close();
            new File(maintFileNameOut).delete();
            System.exit(0);
        }
        System.out.println("Converting CSV into JSON...");
        ProcessFilesForValidation(maintenanceIn, maintFileNameIn, maintOut, maintFileNameOut);
        ProcessFilesForValidation(rentalIn, rentFileNameIn, rentalOut, rentFileNameOut);
    }
    
    /*
    okay, so regex is really hard to read so this is the best i can explain what is going on. The regex in its full statement is:
    [,(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)]

    , look for commas,
    (?= Search ahead but dont include the search in the regex
    (?: match all parts inside this
    [^\"] * look for the parts of the string not within quotations before there is a quotation mark
    \" [^\"] * \") * look for the part of string with quotations between it (This part is where it makes sure it doesnt split inside a quote) and repeat as many times as it needs to
    [^\"] * $) look at everything but quotes until the end of the string

    I took this regex from here: https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes
    */

    public static String[] convert(String csv){
        String regex=",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
        String[] sep=csv.split(regex);
        //sep[0]=sep[0].substring(1);
        return sep;
    }

    public static void ProcessFilesForValidation(Scanner read, String fileIn, PrintWriter write, String fileOut){
        String[] data = convert(read.nextLine());
        int missingData=0;
        try{
            for (String a : data){ //checking to see if all fields are there
                if (a.isBlank()) missingData++;
            }
            if(missingData!=0) throw new CSVFileInvalidException();
        }
        catch (CSVFileInvalidException e){
            try{
                PrintStream errorOut=new PrintStream(new FileOutputStream("error_log.txt"),true); //enable autoflush so we dont have to close it manually
                System.setErr(errorOut);
            }
            catch(FileNotFoundException f){
                System.out.println("Error: Could not make error log, putting everything into console!");
            }
            finally{
                System.out.println("File "+fileIn+"is invalid: field is missing.");
                System.out.println("File is not converted to JSON.");
                System.err.println("File "+fileIn+" is invalid");
                System.err.println("Missing Field: "+(data.length-missingData)+" detected "+missingData+" missing.");
                for (int i=0;i<data.length;i++){
                    System.err.print((data[i].isBlank() ? "***":data[i]) +(i==data.length-1 ? "" : ","));
                }
                File input=new File(fileOut);
                input.delete();
                read.close();
                write.close();
                System.exit(0);
            }
            
        } //now we can finally make our json file
        String[] values=new String[data.length];
        write.println("["); //the json is an array of objects so we have to do this
        int lineNum=0; //to see what line we are on when theres an error with the data
        while (read.hasNext()){
            boolean hasError=true; //this needs to be initiallized as true to go into the loop to check if there is an error
            while(hasError){
                values=convert(read.nextLine());
                lineNum++;
                try{
                    for (String a:values) 
                        if (a.isBlank()) throw new CSVDataMissing();
                    hasError=false;
                }
                catch (CSVDataMissing e){
                    hasError=true;
                    try{
                        PrintStream errorOut=new PrintStream(new FileOutputStream("error_log.txt"),true); //enable autoflush so we dont have to close it manually
                        System.setErr(errorOut);
                    }
                    catch(FileNotFoundException f){
                        System.out.println("Error: Could not make error log, putting everything into console!");
                    }
                    finally{
                        String missingEntries="";
                        System.out.println("In file "+fileIn+" line "+lineNum+" not converted to JSON: missing data");
                        System.err.println("In file "+fileIn+" line "+lineNum);
                        for(int i=0; i<values.length;i++){
                            if (values[i].isBlank()) missingEntries+=data[i]+" ";
                            System.err.print((values[i].isEmpty() ? "***":values[i])+"\t");
                        }
                        System.err.println("\nMissing: "+missingEntries);
                    }
                }
            }
            write.println("\t{"); //we need to keep proper tabs to make the json easier to enterpret
            for(int i=0;i<data.length;i++){
                write.println("\t\t\""+data[i]+"\": "+(isInteger(values[i]) ? values[i] : ("\""+(values[i].charAt(0)=='"' ? values[i].substring(1, values[i].length()-1):values[i])+"\""))+
                (i==data.length-1 ? "":","));
            }                                                           //so whats happening on the line above is that the entry is checked if it is an integer, 
            write.println("\t}"+(read.hasNextLine() ? "," : ""));       //if it is, then it will write the string as is with no formatting, if its a string,
        }                                                               //add quotation marks on it. But, if the string already has quotation marks on it,
        write.println("]");                                             //take the substring of it with no quotes so that there are no double quotations present
        write.close();
        read.close();
    }
    public static boolean isInteger(String s){
        Scanner sc = new Scanner(s);
        if (sc.hasNextInt()){
            sc.close();
            return true;
        } 
        else{
            sc.close();
            return false;
        } 
    }
}

            