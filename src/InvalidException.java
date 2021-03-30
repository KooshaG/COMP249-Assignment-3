// ---------------------------------------
// COMP 249
// Assignment 3
// Written By: Adamo Orsini (40174716) and Koosha Gholipour (40176826)
// Due March 31, 2021
// ---------------------------------------
import java.lang.Exception;
public class InvalidException extends Exception{

    public InvalidException(){
        super("Error: Input row cannot be parsed due to missing information");
    }
    public InvalidException(String s){
        super(s);
    }

}