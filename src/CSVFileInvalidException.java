// ---------------------------------------
// COMP 249
// Assignment 3
// Written By: Adamo Orsini (40174716) and Koosha Gholipour (40176826)
// Due March 31, 2021
// ---------------------------------------
import java.lang.Exception;
/**
 * @author Adamo, Koosha
 * Exception called if csv file is invalid
 */
public class CSVFileInvalidException extends InvalidException{
    
    public CSVFileInvalidException(){
        super("Data field is missing");
    }
    public CSVFileInvalidException(String s) {
        super(s);
    }
}
