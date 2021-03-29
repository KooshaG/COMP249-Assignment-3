import java.lang.Exception;
public class InvalidException extends Exception{

    public InvalidException(){
        super("Error: Input row cannot be parsed due to missing information");
    }
    public InvalidException(String s){
        super(s);
    }
    //we might need to add extra stuff to this class later

}