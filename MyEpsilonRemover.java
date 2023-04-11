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

    private static class NFA {
        int states;
        int alphabetSize;
        ArrayList<Integer> acceptingStates;
        HashMap<Integer, HashMap<Character, ArrayList<Integer>>> transitions;
        HashMap<Integer, HashMap<Character, ArrayList<Integer>>> reverse;

        NFA(int states, int alphabetSize, ArrayList<Integer> acceptingStates, HashMap<Integer, HashMap<Character, ArrayList<Integer>>> transitions) {
            this.states = states;
            this.alphabetSize = alphabetSize;
            this.acceptingStates = acceptingStates;
            this.transitions = transitions;
            reverse = this.reverseTransitions(transitions);
        }

        public void removeEMoves() {
            step1();
        }

        public void step1() {
            for(int i=0; i < acceptingStates.size(); i++) {
                HashMap<Character,ArrayList<Integer>> backMoves = new HashMap<>();
                if(reverse.containsKey(acceptingStates.get(i))) {
                    backMoves = reverse.get(acceptingStates.get(i));
                }
                if(backMoves.containsKey('`')) {
                    ArrayList<Integer> fromStates = backMoves.get('`');
                    for(Integer j : fromStates) {
                        if(!acceptingStates.contains(j)) {
                            acceptingStates.add(j);
                        }
                        
                    }
                }
                
            }
        }

        private HashMap<Integer, HashMap<Character, ArrayList<Integer>>> reverseTransitions(HashMap<Integer, HashMap<Character, ArrayList<Integer>>> transitions) {
            HashMap<Integer, HashMap<Character, ArrayList<Integer>>> reverse = new HashMap<>();

            for(Integer i : transitions.keySet()) {
                // ArrayList<Integer> newEndStates = new ArrayList<>();
                HashMap<Character, ArrayList<Integer>> transition = transitions.get(i);
                for(Character c : transition.keySet()) {
                    ArrayList<Integer> endStates = transition.get(c);
                    for(Integer j : endStates) {
                        if(reverse.get(j) == null) {
                            HashMap<Character, ArrayList<Integer>> newTransition = new HashMap<>();
                            ArrayList<Integer> newEndStates = new ArrayList<>();
                            newEndStates.add(i);
                            newTransition.put(c, newEndStates);
                            reverse.put(j, newTransition);
                        } else {
                            reverse.get(j).get(c).add(i);
                        }
                        
                    }
                }
            }
            return reverse;
        }

        public String toString() {
            String ret = "";
            ret += "Number of states: " + states + "\n";
            ret += "Alphabet size: " + alphabetSize + "\n";
            Collections.sort(acceptingStates);
            ret += "Accepting states: " + acceptingStates.stream().map(Object::toString).collect(Collectors.joining(" ")) + "\n";
            for(Integer i : transitions.keySet()) {
                for(Character c : transitions.get(i).keySet()) {
                    ArrayList<Integer> states = transitions.get(i).get(c);
                    ret += "{";
                    String statesString = states.toString();
                    statesString = statesString.replace("[","");
                    statesString = statesString.replace("]","");
                    statesString = statesString.replace(" ","");
                    ret += statesString;
                    ret += "}";
                }
                ret += "\n";
            }
            return ret;
        }


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
                    } else if(lineSplit[j].matches("^([1-9]|[1-5][0-9]|6[0-4])$")) { // Regex to match any state number 0-64
                        transitionStates.add(Integer.parseInt(lineSplit[j]));
                    }
                    char c = (char)(j+96);
                    transition.put(c,transitionStates);
                }
                transitions.put(i, transition);
            }

            nfa = new NFA(states, alphabetSize, acceptingStates, transitions);
            
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }

        return nfa;
    }
    
}

