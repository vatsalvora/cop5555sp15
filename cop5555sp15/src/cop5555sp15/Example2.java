package cop5555sp15;

/**
 * Created by Vatsal on 4/23/2015.
 */
public class Example2 {
    public static void main(String[] args) throws Exception {
        //Prints the integers from 1 to index
        //Also prints the factorial of all the integers in the range
        String source = "class PSI{\n"
                + "def k: int;\n"
                + "k = 1;\n"
                + "def index: int;\n"
                + "def fact: int;\n"
                + "fact = 1;\n"
                + "while (k<=index) { fact = fact * k; print k; k = k+1;};\n"
                + "}";
        Codelet codelet = CodeletBuilder.newInstance(source);
        codelet.execute();
        int index = CodeletBuilder.getInt(codelet, "index");
        System.out.println("Index: " + index);
        CodeletBuilder.setInt(codelet, "index", 5);
        codelet.execute();
        System.out.println("Factorial: " + CodeletBuilder.getInt(codelet, "fact"));
        //Only works until 12 factorial. After that integer overflow happens. 13! > Integer.MAX_SIZE
        CodeletBuilder.setInt(codelet, "index", 12);
        codelet.execute();
        System.out.println("Factorial: " + CodeletBuilder.getInt(codelet, "fact"));
    }
}
