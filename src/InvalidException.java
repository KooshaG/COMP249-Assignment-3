public class InvalidException extends Exception{

    public InvalidException(){
        super("Error: Input row cannot be parsed due to missing information");
    }
    //we might need to add extra stuff to this class later

}