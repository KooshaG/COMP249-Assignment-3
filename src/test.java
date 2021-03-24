import java.util.Scanner;

public class test {
    
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Do it: ");
        String[] things= convert(in.nextLine());
        String[] newThing=new String[things.length];
        for (int i=0;i<things.length;i++){
            newThing[i]=(things[i].charAt(0)=='"' ? things[i].substring(1, things[i].length()-1):things[i]);
            System.out.println(things[i]+" ---> "+newThing[i]);
        }

    }

    public static String[] convert(String csv){
        String regex=",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
        return csv.split(regex);
    }
}
