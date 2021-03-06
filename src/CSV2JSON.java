// ---------------------------------------
// COMP 249
// Assignment 3
// Written By: Adamo Orsini (40174716) and Koosha Gholipour (40176826)
// Due March 31, 2021
// ---------------------------------------
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Scanner;

public class CSV2JSON {
    
    public static void main(String[] args) {
        Scanner input=new Scanner(System.in);
        Scanner[] readers;
        PrintWriter[] writers;
        String[] fileNamesIn;
        String[] fileNamesOut;
        int files;

        System.out.print("How many files are you converting?: ");
        files=Integer.parseInt(input.nextLine()); //we need to do this because using nextInt leaves a \n in the buffer
        if (files<1) {
            System.out.println("Okay, no files will be converted. Thank you!");
            System.exit(0);
        }
        readers=new Scanner[files];
        writers=new PrintWriter[files];
        fileNamesIn=new String[files];
        fileNamesOut=new String[files];
        for(int i=0;i<files;i++){
            System.out.print("Please enter record #"+(i+1)+" with its file extension: ");
            fileNamesIn[i]=input.nextLine().trim();
            fileNamesOut[i]=fileNamesIn[i].split("\\.")[0]+".json"; //we need the escape slashes because . has a special meaning in regex
            System.out.println(fileNamesOut[i]+"\t"+fileNamesOut.length);
            try{
                readers[i]=new Scanner(new FileInputStream(fileNamesIn[i])); //make all the scanners
            }
            catch(FileNotFoundException e){
                System.out.println("\nCould not open file "+fileNamesIn[i]+" for reading");
                System.out.println("Please check if file exists! Program will terminate after closing all open files");
                for(int j=0; j<i;j++){
                    readers[j].close();
                    System.exit(0);
                }
            }
            try{
                writers[i]=new PrintWriter(new FileOutputStream(fileNamesOut[i])); //make all the writers
            }
            catch(FileNotFoundException e){
                System.out.println("\nCould not open file "+fileNamesOut[i]+" for writing");
                System.out.println("Please check if file exists! Program will terminate after closing all open files");
                for(int j=0; j<i;j++){
                    readers[j].close();
                    writers[j].close();
                    File temp=new File(fileNamesOut[j]);
                    temp.delete();
                    System.exit(0);
                }
            }
        }
        System.out.println("Converting CSV into JSON...");
        for(int i=0;i<files;i++){
            ProcessFilesForValidation(readers[i], fileNamesIn[i], writers[i], fileNamesOut[i]);
        }
        System.out.println("Converting complete.");
        
        // User choosing which file to read from
        System.out.print("Please enter the name of a JSON file that you would like displayed: ");
    	String outFile = input.nextLine();

    	BufferedReader breader = null;
    	// First attempt
        try {
        	breader = new BufferedReader(new FileReader(outFile));
        }
        catch(FileNotFoundException fe) {
        	// Second attempt
        	System.out.print("That file could not be found. You have one more chance to enter a valid file to read: ");
        	outFile = input.nextLine();
        	try {
            	breader = new BufferedReader(new FileReader(outFile));
        	}
        	catch(FileNotFoundException fnf) {
        		System.out.print("That file could not be found. Both attempts have been used. Terminating program.");
        		System.exit(0);
        	}
        }
        try {
        	int recordNum = 1;		// Iterating through the records
        	// Read characters until it prints -1, which is EOF
        	int x = breader.read();
        	while (x != -1) {
        		char c = (char)x;
        		
        		if (c == '{')
        			System.out.println("Record #" + recordNum + " contents:");
        		else if (c == '}')
        			System.out.println("\nEnd of record #" + recordNum++ + ".");
        		else if (Character.isLetterOrDigit(c) || c == ' ' || c == '\n' || c == ',' || c == ':' || c == '-' || c == '/' || c == '(' || c == ')')
        			System.out.print(c);
        			
        		x = breader.read();
        	}
        	breader.close();
        }
        catch(IOException io) {
        	io.printStackTrace();
        }
    	
        input.close();
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

    /** Splits csv attribute fields over commas that are not between quotation marks
     * @param csv Line of csv containing csv attribute names
     * @return Array of attribute names
     */
    public static String[] convert(String csv){
        String regex=",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
        String[] sep=csv.split(regex);
        return sep;
    }

    /** Processing input files in csv format and creates output files in json format
     * @param read Scanner reading input from csv files
     * @param fileIn Name of input csv file
     * @param write PrintWriter writing into JSON files
     * @param fileOut Name of output JSON file
     */
    public static void ProcessFilesForValidation(Scanner read, String fileIn, PrintWriter write, String fileOut){
        String[] data = convert(read.nextLine()); //fields
        int missingData=0;
        try{
            for (String a : data){ //checking to see if all fields are there
                if (a.isEmpty()) missingData++;
            }
            if(missingData!=0) throw new CSVFileInvalidException();
        }
        catch (CSVFileInvalidException e){
            try{
                PrintStream errorOut=new PrintStream(new FileOutputStream("error_log.txt",true),true); //enable autoflush so we dont have to close it manually
                System.setErr(errorOut);
            }
            catch(FileNotFoundException f){
                System.out.println("Error: Could not make error log, putting everything into console!");
            }
            finally{
                System.out.println("File "+fileIn+" is invalid: field is missing.");
                System.out.println("File is not converted to JSON.");
                System.err.println("File "+fileIn+" is invalid");
                System.err.println("Missing Field: "+(data.length-missingData)+" detected "+missingData+" missing.");
                for (int i=0;i<data.length;i++){
                    System.err.print((data[i].isEmpty() ? "***":data[i]) +(i==data.length-1 ? "" : ","));
                }
                System.err.println("\n\n*****************************************************************************\n");
                File input=new File(fileOut);
                write.close();
                read.close();
                input.delete();
                System.exit(0);
            }
            return;

        } //now we can finally make our json file
        String[] values=new String[data.length];
        write.println("["); //the json is an array of objects so we have to do this
        int lineNum=0; //to see what line we are on when there's an error with the data
        while (read.hasNextLine()){
            boolean hasError=false;
            while(!hasError){
                values=convert(read.nextLine());
                lineNum++;
                try{
                    for (String a:values) 
                        if (a.isEmpty()) throw new CSVDataMissing();
                    hasError=true;
                }
                catch (CSVDataMissing e){
                    hasError=false; //once the error is caught, we reset hasError
                    try{
                        PrintStream errorOut=new PrintStream(new FileOutputStream("error_log.txt",true),true); //enable autoflush so we dont have to close it manually
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
                            if (values[i].isEmpty()) missingEntries+=data[i]+" ";
                            System.err.print((values[i].isEmpty() ? "***":values[i])+"\t");
                        }
                        System.err.println("\nMissing: "+missingEntries);
                        System.err.println("\n\n*****************************************************************************\n");
                    }
                }
            }
            write.println("\t{"); //we need to keep proper tabs to make the json easier to enterpret
            for(int i=0;i<data.length;i++){
                write.println("\t\t\""+data[i]+"\": "+(isInteger(values[i]) ? values[i] : (values[i].charAt(0)=='"' ? values[i]:"\""+values[i]+"\""))+
                (i==data.length-1 ? "":","));
            }                                                           //so whats happening on the line above is that the entry is checked if it is an integer, 
            write.println("\t}"+(read.hasNextLine() ? "," : ""));       //if it is, then it will write the string as is with no formatting, if its a string,
        }                                                               //add quotation marks on it. But, if the string already has quotation marks on it,
        write.println("]");                                             //don't add any extra quotations so that there are no double quotations present
        write.close();
        read.close();
    }
    /** Checks if String parameter is an integer using Scanner object to avoid other errors
     * @param s String with possible integer
     * @return true if hasNextInt in String s
     */
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

            