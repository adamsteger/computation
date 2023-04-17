import java.io.*;
import java.util.*;

public class MyNFASimulator {
    public static void main(String[] args) {
        File file = new File(args[0]);
        NFA nfa = NFA.readFile(file);
        Scanner scanner = new Scanner(System.in);
        while(true) {
            String input = scanner.nextLine();
            String output = nfa.simulate(input);
            System.out.println(output + "\n");
        }
        
    }
}
