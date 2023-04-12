# Computation: NFA Programs

This repository contains code for a project in CSCE 355:Foundation of Computation.  In this repository, you can find a program to remove epsilon transitions from an NFA, as well as a program to simulate a NFA for a given string input.

# Build Instructions
To run the e-NFA to NFA conversion, first run the following commands in a terminal.
  git clone https://github.com/adamsteger/computation.git
  cd computation
  
Then, compile the code with the following command:
  javac MyEpsilonRemover.java
  
Finally, to run the code, use the following command:
  java MyEpsilonRemover test.txt


Likewise, to run the simulator, compile the code with the following command:
  javac MyNFASimulator.java
  
Then, to run the code, use the following command:
  java MyNFASimulator test.txt

To test the code, enter a string and the program will print out "accept" if the NFA accepts the input and "reject" if the NFA rejects the input. Finally, press Ctrl-C to terminate the program.

