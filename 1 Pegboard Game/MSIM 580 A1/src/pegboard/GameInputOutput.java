package pegboard;

import java.io.File;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Scanner;
import java.lang.Math;

/**
 * This program solves a pegboard game using four different search techniques
 * The user is prompted to select a pegboard, a search method, and a maximum search time
 * The program searches for solutions to the pegboard until either a solution is reached or time expires
 * In the event that a pegboard is unsolvable or time expires, the program generates a solution containing the best results found
 * 
 * @author Ben Bissantz
 *
 */
public class GameInputOutput {

	public static void main(String[] args) {

		/**
		 * Declare objects needed for file input and output
		 */
		Scanner userInput = new Scanner(System.in);
		Scanner input = null;
		int puzzleChoice = 5;
		String inputName = null;
		String outputName = null;
		File inputFile;
		File outputFile;
		PrintStream output = null;
		int maxMinutes = 5;
		int searchMethod = 1;

		/**
		 * Prompt user for a puzzle file
		 */
		while (inputName == null) {
			System.out.println("Please select a puzzle file. Enter digits as follows for the corresponding puzzle.");
			System.out.println("3 - 3x3  4 - 4x4  5 - 5x5  6 - 6x6");
			System.out.println("7 - 7x7  8 - 8x8  9 - 9x9  10 - 10x10");
			System.out.println("11 - Bonus1  12 - Bonus2  13 - Bonus3");
			System.out.println("14 - Bonus4  15 - Bonus5  16 - Bonus6");
			System.out.println("17 - Custom: Maimum Area of 128 Spaces Allowed");
			puzzleChoice = userInput.nextInt();
			if (puzzleChoice < 3 || puzzleChoice > 17) {
				System.out.println("Invalid selection, 5x5 chosen by default.");
				puzzleChoice = 5;
			}
			switch (puzzleChoice) {
				case 3:  inputName = "3x3.txt";
		        	break;
				case 4:  inputName = "4x4.txt";
		        	break;
				case 5:  inputName = "5x5.txt";
		        	break;
				case 6:  inputName = "6x6.txt";
		        	break;
				case 7:  inputName = "7x7.txt";
		        	break;
				case 8:  inputName = "8x8.txt";
		        	break;
				case 9:  inputName = "9x9.txt";
		        	break;
				case 10:  inputName = "10x10.txt";
		        	break;
				case 11:  inputName = "bonus1.txt";
		        	break;
				case 12:  inputName = "bonus2.txt";
		        	break;
				case 13:  inputName = "bonus3.txt";
		        	break;
				case 14:  inputName = "bonus4.txt";
		        	break;
				case 15:  inputName = "bonus5.txt";
		        	break;
				case 16:  inputName = "bonus6.txt";
		        	break;
				case 17:
					System.out.println("Please enter the custom file name.");
					inputName = userInput.next();
					break;
			}
			try {
				inputFile = new File(inputName);
				input = new Scanner(inputFile);
			} catch (Exception e) {
				System.out.println("Invalid file name.");
				inputName = null;
			}
		}

		/**
		 * Prompt user for search method
		 */
		System.out.println("Please enter a number between 1 and 4 to select a serch method.");
		System.out.println("1 - Depth-First Search  2 - Breadth-First Search");
		System.out.println("3 - Greedy-Best First Search  4 - A* Search");
		searchMethod = userInput.nextInt();
		if (searchMethod > 4 || searchMethod < 1) {
			searchMethod = 1;
			System.out.println("Invalid selection, Depth-First Search has been chosen by default");
		}
		
		/**
		 * Prompt user for an output file.
		 */
		while (outputName == null) {
			System.out.println("Please enter the name of the output file.");
			outputName = userInput.next();
			outputFile = new File(outputName);
			try {
				output = new PrintStream(outputFile);
			} catch (Exception e) {
				System.out.println("Invalid file name.");
				outputName = null;
			}
		}
		
		/**
		 * Prompt user for search time
		 */
		System.out.println("Please enter the amount of time in whole minutes you would like to search.");
		maxMinutes = userInput.nextInt();
		if (maxMinutes < 1) {
			maxMinutes = 1;
			System.out.println("Invalid entry, time set to 1 minute.");
		}
		if (maxMinutes > 10080) {
			maxMinutes = 10080;
			System.out.println("Invalid entry, time set to 1 week.");
		}
		userInput.close();
		
		/**
		 * Create initial pegboard state
		 */
		// Variables for initial pegboard
		long startTime = System.currentTimeMillis();
		long maxTime = maxMinutes * 60 * 1000;
		State start = new State();
		byte width = 0;
		byte height = 0;
		String inputPegs = "";
		// Read input from file
		while (input.hasNextLine()) {
			height++;
			inputPegs = inputPegs + input.nextLine();
		}
		// Populate initial pegboard state
		width = (byte) (inputPegs.length() / height);
		start.width = width;
		start.height = height;
		start.nodes = 1;
		start.board = new byte[inputPegs.length()];
		for (byte i = 0; i < inputPegs.length(); i++) {
			byte p = 1;
			if (inputPegs.charAt(i) == '1') {
				p = 1;
				start.pegs++;
			}
			if (inputPegs.charAt(i) == '0') p = 0;
			if (inputPegs.charAt(i) == '8') p = 8;
			start.board[i] = p;
		}
		
		/**
		 *  Create current list of moves and best list of moves
		 */
		LinkedList<State> current = new LinkedList<State>();
		LinkedList<State> best = new LinkedList<State>();
		current.addFirst(start);
		best.addFirst(start);
		
		/**
		 *  Perform search selected during user input
		 */
		switch (searchMethod) {
			case 1:  dfs(current, best, startTime, maxTime);
	        break;
			case 2:  bfs(current, best, startTime, maxTime);
	        break;
			case 3:  gbfs(current, best, startTime, maxTime);
	        break;
			case 4:  astar(current, best, startTime, maxTime);
	        break;
		}
		
		/**
		 * Calculate execution time for output statistics
		 */
		long totalSeconds = (System.currentTimeMillis() - startTime)/1000;
		long hours = totalSeconds / 3600;
		long minutes = totalSeconds / 60 % 60;
		long seconds = totalSeconds % 60;
		long milliseconds = (System.currentTimeMillis() - startTime) % 1000;
		
		/**
		 * Print the best solution to the puzzle found to the output file
		 */
		output.printf("%d Pegs Remain\n", best.getLast().pegs);
		output.printf("Total nodes explored: %d\n", best.getLast().nodes);
		output.printf("Total time: %d:%d:%d:%d\n", hours, minutes, seconds, milliseconds);
		for (int b = 0; b < best.size(); b++) {
			output.printf("Board %d: %d Pegs\n", b, best.get(b).pegs);
			for (byte j = 0; j < height; j++) {
				for (byte i = 0; i < width; i++) {
					output.printf("%d", best.get(b).board[i + j * width]);
				}
				output.printf("\n");
			}
		}
	}

	/**
	 * Perform a depth-first search of the list of current states
	 * @param current the linked list of States being currently explored
	 * @param best the linked list of the best path of states found so far
	 * @param startTime system time the search began
	 * @param maxTime system time to end the search based on user input
	 * @return updated linked list of the best path of states found so far
	 */
	public static LinkedList<State> dfs(LinkedList<State> current, LinkedList<State> best, long startTime, long maxTime) {
		// If a state with one peg or the maximum time is reached return the best state found
		if(goal(best.getLast()) || System.currentTimeMillis() > startTime + maxTime) return best;
		// Try each successor state for a given state
		for (int i = 0; i < successor(current.getLast()).size(); i++) {
			current.getLast().nodes ++;
			// Update the current state
			current.add(successor(current.getLast()).get(i));
			// If better than the best state update the best state
			if(current.getLast().pegs < best.getLast().pegs) {
				for (int j = 1; j < best.size(); j++) {
					best.remove(1);
					best.addLast(current.get(j));
				}
				best.add(current.getLast());
			}
			best.getLast().nodes = current.getLast().nodes;
			// Continue searching along current path
			if(!goal(best.getLast())) best = dfs(current, best, startTime, maxTime);
		}
		// When the for loop exits, all successor states have been searched for a given state
		// Remove the last node and continue searching
		if(!goal(current.getLast())) {
			long lastNodes = current.getLast().nodes; 
			current.removeLast();
			if(!current.isEmpty()) current.getLast().nodes = lastNodes;
		}
		// Continuous output to display progress
		if(!current.isEmpty()) System.out.println("Best: " + best.getLast().pegs + " pegs, Current: " + current.getLast().pegs + " pegs, " + best.getLast().nodes + " nodes explored.");
		return best;
	}
	
	/**
	 * Perform a breadth-first search of the list of current states
	 * @param current the linked list of States being currently explored
	 * @param best the linked list of the best path of states found so far
	 * @param startTime system time the search began
	 * @param maxTime system time to end the search based on user input
	 * @return updated linked list of the best path of states found so far
	 */
	public static LinkedList<State> bfs(LinkedList<State> current, LinkedList<State> best, long startTime, long maxTime) {
		// If a state with one peg or the maximum time is reached return the best state found
		if(goal(best.getLast()) || System.currentTimeMillis() > startTime + maxTime) return best;
		// Update the current set of states
		LinkedList<State> newCurrent = new LinkedList<State>();
		for (int x = 0; x < current.size(); x++) {
			for (int y = 0; y < successor(current.get(x)).size(); y++) {
				newCurrent.addFirst(successor(current.get(x)).get(y));
				best.getFirst().nodes ++;
			}
			// Continuous output to display progress
			if(!current.isEmpty()) System.out.println("Best: " + best.getLast().pegs + " pegs, " + best.getFirst().nodes + " nodes explored.");
			if(System.currentTimeMillis() > startTime + maxTime) x = current.size();
		}
		current = newCurrent;
		// Update the best path found
		if(!current.isEmpty()) {
			current.getFirst().nodes = best.getFirst().nodes;
			best.addFirst(current.getFirst());
			for (int j = 1; j < best.size(); j++) {
				best.removeLast();
				best.addFirst(best.getFirst().parent);
			}
			best.getFirst().nodes = best.getLast().nodes;
			return bfs(current, best, startTime, maxTime);
		}
		// If no current nodes return the best path found
		else return best;
	}
	
	/**
	 * Perform a greedy-best first search of the list of current states
	 * @param current the linked list of States being currently explored
	 * @param best the linked list of the best path of states found so far
	 * @param startTime system time the search began
	 * @param maxTime system time to end the search based on user input
	 * @return updated linked list of the best path of states found so far
	 */
	public static LinkedList<State> gbfs(LinkedList<State> current, LinkedList<State> best, long startTime, long maxTime) {
		// If a state with one peg or the maximum time is reached return the best state found
		if(goal(best.getLast()) || System.currentTimeMillis() > startTime + maxTime) return best;
		// Sort successor states based on heuristic value
		LinkedList<State> sortedSuccessors = new LinkedList<State>();
		if (!successor(current.getLast()).isEmpty()) sortedSuccessors.add(successor(current.getLast()).get(0));
		for (int i = 1; i < successor(current.getLast()).size(); i++) {
			if (heuristic(successor(current.getLast()).get(i)) > heuristic(sortedSuccessors.getLast())) {
				sortedSuccessors.addLast(successor(current.getLast()).get(i));
			}
			else for (int j = 0; j < sortedSuccessors.size(); j++) {
				if (heuristic(successor(current.getLast()).get(i)) <= heuristic(sortedSuccessors.get(j))) {
					sortedSuccessors.add(j,successor(current.getLast()).get(i));
					j = successor(current.getLast()).size();
				}
			}	
		}
		// Try each successor state for a given state
		for (int i = 0; i < sortedSuccessors.size(); i++) {
			current.getLast().nodes ++;
			long lastNodes = current.getLast().nodes;
			// Update the current state
			current.add(sortedSuccessors.get(i));
			current.getLast().nodes = lastNodes;
			// If better than the best state update the best state
			if(current.getLast().pegs < best.getLast().pegs) {
				for (int j = 1; j < best.size(); j++) {
					best.remove(1);
					best.addLast(current.get(j));
				}
				best.add(current.getLast());
			}
			best.getLast().nodes = current.getLast().nodes;
			// Continue searching along current path
			if(!goal(best.getLast())) best = gbfs(current, best, startTime, maxTime);
		}
		// When the for loop exits, all successor states have been searched for a given state
		// Remove the last node and continue searching
		if(!goal(current.getLast())) {
			long lastNodes = current.getLast().nodes; 
			current.removeLast();
			if(!current.isEmpty()) current.getLast().nodes = lastNodes;
		}
		// Continuous output to display progress
		if(!current.isEmpty()) System.out.println("Best: " + best.getLast().pegs + " pegs, Current: " + current.getLast().pegs + " pegs, " + best.getLast().nodes + " nodes explored.");
		return best;
	}

	/**
	 * Perform an A* search search of the list of current states
	 * @param current the linked list of States being currently explored
	 * @param best the linked list of the best path of states found so far
	 * @param startTime system time the search began
	 * @param maxTime system time to end the search based on user input
	 * @return updated linked list of the best path of states found so far
	 */
	public static LinkedList<State> astar(LinkedList<State> current, LinkedList<State> best, long startTime, long maxTime) {
		// If a state with one peg or the maximum time is reached return the best state found
		if(goal(best.getLast()) || System.currentTimeMillis() > startTime + maxTime) return best;
		// Remove states with no successor states 
		while (current.getFirst().pegs >= best.getLast().pegs && System.currentTimeMillis() < startTime + maxTime) {
			while (successor(current.getFirst()).isEmpty()) {
				current.removeFirst();
				// No solutions exist, return best solution found
				if(current.isEmpty()) return best;
			}
			// Remove first node with successor states to expand
			State searchState = new State();
			searchState = current.removeFirst();
			// Add successor states in order by heuristic value
			for (int i = 0; i < successor(searchState).size(); i++) {
				if (current.isEmpty()) current.addLast(successor(searchState).get(i));
				else if (heuristic(successor(searchState).get(i)) > heuristic(current.getLast())) {
					current.addLast(successor(searchState).get(i));
				}
				else for (int j = 0; j < current.size(); j++) {
					if (heuristic(successor(searchState).get(i)) <= heuristic(current.get(j))) {
						current.add(j,successor(searchState).get(i));
						j = current.size();
					}
				}
				best.getFirst().nodes ++;
			}
			// Continuous output to display progress
			System.out.println("Best: " + best.getLast().pegs + " pegs, Current: " + current.getFirst().pegs + " pegs, " + best.getFirst().nodes + " nodes explored.");
		}
		// Update the best path found
		if (current.getFirst().pegs < best.getLast().pegs) {
			best.addFirst(current.getFirst());
			for (int i = 1; i < best.size(); i++) {
				best.removeLast();
				best.addFirst(best.getFirst().parent);
			}
		}
		best.getLast().nodes = best.getFirst().nodes;
		best = astar(current, best, startTime, maxTime);
		return best;
	}

	/**
	 * Returns true if state s equals the state with exactly one peg
	 * @param s state to test
	 * @return true if s has exactly one peg
	 */
	public static boolean goal(State s) {
		return s.pegs == 1 ? true : false;
	}
	
	/**
	 * Given a state, returns all possible states from the current state
	 * @param s state to find successor states to
	 * @return linked list of successor states
	 */
	public static LinkedList<State> successor(State s) {
		LinkedList<State> successorStates = new LinkedList<State>(); 
		// For each peg check moves in all directions and adds possible states to a linked list
		for (int i = 0; i < s.width * s.height; i++) {
			if(s.lookRight(i)) successorStates.add(s.moveRight(i));
			if(s.lookDown(i)) successorStates.add(s.moveDown(i));
			if(s.lookLeft(i)) successorStates.add(s.moveLeft(i));
			if(s.lookUp(i)) successorStates.add(s.moveUp(i));
			// Diagonal moves for bonus puzzle 6
			// Uncomment these lines for diagonal moves in bonus puzzle 6
			// Dynamically configuring this functionality has not been added
			// if(s.lookUpLeft(i)) successorStates.add(s.moveUpLeft(i));
			// if(s.lookDownRight(i)) successorStates.add(s.moveDownRight(i));
		}
		return successorStates;
	}
	
	/**
	 * Given a state, returns the heuristic function value
	 * Value is based on Manhattan distance from center with additional penalty for pegs on the edges 
	 * @param s state to determine a heuristic value
	 * @return heuristic value for a state
	 */
	public static int heuristic(State s) {
		int value = 0;
		for (int i = 0; i < s.board.length; i++) {
			if(s.board[i] == 1) {
				// Manhattan distance calculated as the sum of horizontal and vertical distance from the center
				value = value + Math.abs(i / s.height - s.height/2);
				value = value + Math.abs(i % s.width - s.width/2);
				// Additional penalty for pegs on the edges
				value = (i / s.height == 0) ? value + 1 : value;
				value = (i / s.height == s.height - 1) ? value + 1 : value;
				value = (i % s.width == 0) ? value + 1 : value;
				value = (i % s.width == s.width - 1) ? value + 1 : value;
			}
		}
		return value;
	}
}