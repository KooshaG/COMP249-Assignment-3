import java.lang.Exception;
public class CSVFileInvalidException extends Exception{
    
    public CSVFileInvalidException(){
        super("Data field is missing");
    }
    public CSVFileInvalidException(String s) {
        super(s);
    }
}
