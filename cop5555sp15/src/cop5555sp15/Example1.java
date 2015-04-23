package cop5555sp15;

import java.util.List;

/**
 * Created by Vatsal on 4/23/2015.
 */
public class Example1 {
    public static void main(String[] args) throws Exception {
        //Largest Sum Contiguous Subarray
        //Prints the maximum contiguous sum of integers in an array
        //Expected Output: 6
        String source = "class LSCS{\n"
                + "def l1: @[int];\n"
                + "def k: int;\n"
                + "k = 0;\n"
                + "def c: int;\n"
                + "c = 0;\n"
                + "def sum: int;\n"
                + "sum = 0;\n"
                + "l1 = @[-2, 1, -3, 4, -1, 2, 1, -5, 4];\n"
                + "def s: int;\n"
                + "s = size(l1);\n"
                + "while (k<s) {c = c + l1[k]; if(c<0) {c = 0;}; if(sum<c){sum =c;};k = k+1;};\n"
                + "}";
        Codelet codelet = CodeletBuilder.newInstance(source);
        codelet.execute();
        int sum = CodeletBuilder.getInt(codelet, "sum");
        System.out.println(sum);
    }
}

