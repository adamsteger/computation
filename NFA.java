import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class NFA {
    int states;
    int alphabetSize;
    ArrayList<Integer> acceptingStates;
    HashMap<Integer, HashMap<Character, ArrayList<Integer>>> transitions;
    HashMap<Integer, HashMap<Character, ArrayList<Integer>>> reverse;

    /*
     * Constructor for the NFA
     */
    public NFA(int states, int alphabetSize, ArrayList<Integer> acceptingStates, HashMap<Integer, HashMap<Character, ArrayList<Integer>>> transitions) {
        this.states = states;
        this.alphabetSize = alphabetSize;
        this.acceptingStates = acceptingStates;
        this.transitions = transitions;
        reverse = this.reverseTransitions();
    }

    /*
     * Method to remove the e-moves from the NFA
     */
    public void removeEMoves() {
        step1();
        step2();
    }

    /*
     * This method makes states accepting according to epsilon transitions in the NFA
     * (If there is an epsilon transition from a -> b and b is accepting, make a accepting)
     */
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

    /*
     * This method adds transition bypasses to the NFA according to the epsilon transitions in it
     * (If there is an an epsilon transition from a -> b and a regular transition from b -> c, 
     * make a new transition from a -> c with the given symbol)
     */
    public void step2() {
        // Create a boolean to break the while loop if no changes are made in a given pass
        boolean changesInPass = true;
        while(changesInPass) {
            // Set the boolean to false in case there are no changes in this pass
            changesInPass = false;
            // Iterate through all states of the NFA with a transition into the state.  This represents a pass in this step.
            for(Integer i : reverse.keySet()) {
                // List to track what states have an epsilon transition into the given state
                ArrayList<Integer> backStates = reverse.get(i).get('`');
                // Maps transitions out of the given state
                HashMap<Character, ArrayList<Integer>> forwardMoves = transitions.get(i);
                for(Integer j : backStates) {
                    for(Character c : forwardMoves.keySet()) {
                        for(Integer k : forwardMoves.get(c)) {
                            // Checks to see if each transition already exists, if not it is added
                            if(!transitions.get(j).get(c).contains(k)) {
                                transitions.get(j).get(c).add(k);
                                // Set the boolean to true to represent that there was a change in this pass
                                changesInPass = true;
                            }
                        }
                    }
                }
            }
        }
        
        // Once done, remove all epsilon transitions from the NFA
        for(Integer i : transitions.keySet()) {
            transitions.get(i).get('`').clear();
        }
    }

    /*
     * Method to create the reverse of all transitions in the NFA.
     * @return a hash map that maps the backwards version of all transitions in the NFA 
     */
    private HashMap<Integer, HashMap<Character, ArrayList<Integer>>> reverseTransitions() {
        HashMap<Integer, HashMap<Character, ArrayList<Integer>>> reverse = new HashMap<>();

        // Loop through all states
        for(Integer i : transitions.keySet()) {
            // Get the transitions for the given state
            HashMap<Character, ArrayList<Integer>> transition = transitions.get(i);
            // Loop through each character transition
            for(Character c : transition.keySet()) {
                // Find the states that the transition goes to
                ArrayList<Integer> endStates = transition.get(c);
                for(Integer j : endStates) {
                    if(reverse.get(j) == null) { // Check if that map already exists
                        HashMap<Character, ArrayList<Integer>> newTransition = new HashMap<>();
                        ArrayList<Integer> newEndStates = new ArrayList<>();
                        newEndStates.add(i);
                        newTransition.put(c, newEndStates);
                        reverse.put(j, newTransition);
                    } else if(reverse.get(j).containsKey(c)) { // if it does, add the transition to the map
                        reverse.get(j).get(c).add(i);
                    }
                    
                }
            }
        }
        return reverse;
    }

    /*
     * Method to simulate a string on the NFA
     * @param input represents the string that is being simulated
     * @return returns "accept" if the NFA accepts the string and "reject" if the NFA rejects the string
     */
    public String simulate(String input) {
        String ret = "reject";

        // Make list to keep track of current states
        ArrayList<Integer> currentStates = new ArrayList<>();

        // Add the start state
        currentStates.add(0);

        // Simulate the string
        for(char c : input.toCharArray()) {

            // Make a list to keep track of potential next states on character transition
            ArrayList<Integer> potentialStates = new ArrayList<>();
            for(int j : currentStates) {
                if(transitions.get(j).containsKey(c)) {
                    potentialStates.addAll(transitions.get(j).get(c));
                }
            }
            // If there are no next states, 
            // if(potentialStates.isEmpty()) {
            //     break;
            // }
            currentStates = potentialStates;
        }

        for(int i : currentStates) {
            if(acceptingStates.contains(i)) {
                ret = "accept";
                break;
            }
        }

        return ret;
    }

    
    /*
    * Method to convert the input text file to an NFA
    * @param file: the input ascii file that will be read and converted
    * @return the NFA that will be made from the input file
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
            // Create a map for the transitions to populate
            HashMap<Integer, HashMap<Character, ArrayList<Integer>>> transitions = new HashMap<Integer, HashMap<Character, ArrayList<Integer>>>();

            // Loop for the number of states that was previously read
            for(int i = 0; i < states; i++) {
                // Get the current line and split it to get a string for each set of states
                String line = scanner.nextLine();
                String[] lineSplit = line.split("}");
                HashMap<Character, ArrayList<Integer>> transition = new HashMap<>();
                // Loop through each transition
                for(int j = 0; j < lineSplit.length; j++) {
                    // Reformat to parse the int
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
                    // Convert to character for the transition map
                    char c = (char)(j+96);
                    // Add to current transition
                    transition.put(c,transitionStates);
                }
                // Add transition to map of all transitions
                transitions.put(i, transition);
            }

            // Construct the nfa
            nfa = new NFA(states, alphabetSize, acceptingStates, transitions);
            scanner.close();

            
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        return nfa;
    }

    /*
     * Converts to NFA to a string for output
     * @return a string representation 
     */
    public String toString() {
        String ret = "";
        ret += "Number of states: " + states + "\n";
        ret += "Alphabet size: " + alphabetSize + "\n";
        Collections.sort(acceptingStates);
        ret += "Accepting states: " + acceptingStates.stream().map(Object::toString).collect(Collectors.joining(" ")) + "\n";
        for(Integer i : transitions.keySet()) {
            for(Character c : transitions.get(i).keySet()) {
                ArrayList<Integer> states = transitions.get(i).get(c);
                Collections.sort(states);
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