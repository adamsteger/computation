import java.util.*;
import java.util.stream.Collectors;

public class NFA {
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
            step2();
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

        public void step2() {
            boolean changesInPass = true;
            while(changesInPass) {
                changesInPass = false;
                for(Integer i : transitions.keySet()) {
                    if(reverse.containsKey(i) && reverse.get(i).containsKey('`')) {
                        ArrayList<Integer> backStates = reverse.get(i).get('`');
                        HashMap<Character, ArrayList<Integer>> forwardMoves = transitions.get(i);
                        for(Integer j : backStates) {
                            for(Character c : forwardMoves.keySet()) {
                                for(Integer k : forwardMoves.get(c)) {
                                    if(!transitions.get(j).get(c).contains(k)) {
                                        transitions.get(j).get(c).add(k);
                                        changesInPass = true;
                                    }
                                }
                            }
                        }
                    }
                }
                    
                //     } else if(transitions.get(i).containsKey('`')) {
                //         if(reverse.containsKey(i)) {
                //             HashMap<Character,ArrayList<Integer>> backTransitions = reverse.get(i);
                //             ArrayList<Integer> forwardStates = transitions.get(i).get('`');
                //             for(Character c : backTransitions.keySet()) {
                //                 for(Integer j : backTransitions.get(c)) {
                //                     for(Integer k : forwardStates) {
                //                         if(!transitions.get(j).get(c).contains(k)) {
                //                             transitions.get(j).get(c).add(k);
                //                             changesInPass = true;
                //                         }
                //                     }
                //                 }
                //             }

                //         }
                        
                //     }
                // }
            }
            
            for(Integer i : transitions.keySet()) {
                transitions.get(i).get('`').clear();
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
                        } else if(reverse.get(j).containsKey(c)) {
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