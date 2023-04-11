import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class MyEpsilonRemover {

    public static void main(String[] args) {
        File file = new File(args[0]);
        NFA nfa = readFile(file);
        System.out.println(nfa);
        nfa.removeEMoves();
        System.out.println(nfa);
    }


    /*
     * Method to convert the input text file to an NFA
     * @param file the input ascii file that will be read and converted
     * @return NFA the NFA that will be made from the input file
     */
    public static NFA readFile(File file) {
        NFA nfa = null;

        try {
            Scanner scanner = new Scanner(file);

            // Parse file to get number of states
            String numOfStates = scanner.nextLine();
            String[] stateSplit = numOfStates.split(": ");
            int states = Integer.parseInt(stateSplit[1]);

            // Parse file to get alphabet size
            String alphabetString = scanner.nextLine();
            String[] alphabetSplit = alphabetString.split(": ");
            int alphabetSize = Integer.parseInt(alphabetSplit[1]);

            // Parse file to get array of accepting states
            String acceptingString = scanner.nextLine();
            String[] acceptingSplit = acceptingString.split(": ");
            String[] arraySplit = acceptingSplit[1].split(" ");
            ArrayList<Integer> acceptingStates = new ArrayList<>();
            for(int i =0; i < arraySplit.length; i++) {
                acceptingStates.add(Integer.parseInt(arraySplit[i]));
            }

            // Parse file to get transitions of NFA
            HashMap<Integer, HashMap<Character, ArrayList<Integer>>> transitions = new HashMap<Integer, HashMap<Character, ArrayList<Integer>>>();

            for(int i = 0; i < states; i++) {
                String line = scanner.nextLine();
                String[] lineSplit = line.split("}");
                HashMap<Character, ArrayList<Integer>> transition = new HashMap<>();
                for(int j = 0; j < lineSplit.length; j++) {
                    lineSplit[j] = lineSplit[j].replace("{","");
                    lineSplit[j] = lineSplit[j].replace(" ","");
                    ArrayList<Integer> transitionStates = new ArrayList<>();
                    if(lineSplit[j].contains(",") && !lineSplit[j].equals("")) { // Checks to see if there is an array of states in the transition
                        String[] transitionArraySplit = lineSplit[j].split(",");
                        for(int k = 0; k < transitionArraySplit.length; k++) {
                            transitionStates.add(Integer.parseInt(transitionArraySplit[k]));
                        }
                    } else if(lineSplit[j].matches("[0-9]|[1-5][0-9]|6[0-3]")) { // Regex to match any state number 0-63 (64 possible states)
                        transitionStates.add(Integer.parseInt(lineSplit[j]));
                    }
                    char c = (char)(j+96);
                    transition.put(c,transitionStates);
                }
                transitions.put(i, transition);
            }

            nfa = new NFA(states, alphabetSize, acceptingStates, transitions);
            scanner.close();

            
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        return nfa;
    }
    
}

