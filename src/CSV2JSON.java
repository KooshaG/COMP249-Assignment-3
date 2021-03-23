import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class CSV2JSON {

    
    public static void main(String[] args) {
        
    }
    
    static String regex=",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    
    /*
    okay, so regex is really hard to read so this is the best i can explain what is going on. The regex in its full statement is
    [,(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)] the next part is to split up each item.

    , look for commas,
    (?= Search ahead but dont include the search in the regex
    (?: match all parts inside this
    [^\"] * look for the parts of the string not within quotations before there is a quotation mark
    \" [^\"] * \") * look for the part of string with quotations between it (This part is where it makes sure it doesnt split inside a quote) and repeat as many times as it needs to
    [^\"] * $) look at everything but quotes until the end of the string

    I took this regex from here: https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes
    */
    public static String[] convert(String str){
        return str.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    }
}

