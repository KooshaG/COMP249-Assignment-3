// ---------------------------------------
// COMP 249
// Assignment 3
// Written By: Adamo Orsini (40174716) and Koosha Gholipour (40176826)
// Due March 31, 2021
// ---------------------------------------
import java.lang.Exception;
/**
 * @author Adamo, Koosha
 * Exception called if csv data is missing
 */
public class CSVDataMissing extends Exception {
    
    public CSVDataMissing(){
        super("Missing Data");
    }
    public CSVDataMissing(String s){
        super(s);
    }
}
