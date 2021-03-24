public class CSVDataMissing extends Exception {
    
    public CSVDataMissing(){
        super("Missing Data");
    }
    public CSVDataMissing(String s){
        super(s);
    }
}
