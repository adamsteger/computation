import java.io.*;

public class MyEpsilonRemover {
    public static void main(String[] args) {
        File file = new File(args[0]);
        NFA nfa = NFA.readFile(file);
        nfa.removeEMoves();
        System.out.println(nfa);
    }
}