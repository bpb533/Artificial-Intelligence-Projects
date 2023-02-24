package crosswordDesign;

import java.io.File;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Scanner;
import java.lang.Math;

/**
 * This program solves a crossword puzzle using words provided in an input word list
 * The user is prompted to select a puzzle file and a list of words
 * @author Ben Bissantz
 */
public class Crossword {
	
	/**
	 * Variables used by multiple methods
	 */
	static String puzzleFileName = null;
	static int width = 0;
	static int height = 0;
	static int shortestWord = 0;
	static int longestWord = 0;
	static boolean sortedDictionary = false;
	static int heuristicChoice = 3;
	static String puzzle = "";
	static boolean[] lockedAcross;
	static boolean[] lockedDown;
	static boolean[] lockedInput;
	static LinkedList<Word> words;	
	static LinkedList<String> dictionary;
	static boolean[][][][] possWordArray;
	static int[] dictionaryIndex;
	static int[] letterScore;
	static long startTime;
	static long searchStartTime;
	static long comparisons = 0;

	/**
	 * Main method, calls other methods to run the program
	 * @param args standard arguments
	 */
	public static void main(String[] args) {
		readInputFiles();
		if (!sortedDictionary) sortDictionary();
		searchStartTime = System.currentTimeMillis();
		switch (heuristicChoice) {
		case 1:  if (backtrackingAlgorithm(0)) {
				System.out.println("Solution Found ... Printing Output File.");
			} else {
				System.out.println("No Solution Found ... Printing Output File.");
			}
			break;
		case 2:  if (backtrackingAlgorithmFC(0)) {
				System.out.println("Solution Found ... Printing Output File.");
			} else {
				System.out.println("No Solution Found ... Printing Output File.");
			}
			break;
		case 3:  if (backtrackingAlgorithmMRV(getMRV())) {
				System.out.println("Solution Found ... Printing Output File.");
			} else {
				System.out.println("No Solution Found ... Printing Output File.");
			}
        	break;
		case 4:  if (backtrackingAlgorithmDH(getDH())) {
				System.out.println("Solution Found ... Printing Output File.");
			} else {
				System.out.println("No Solution Found ... Printing Output File.");
			}
    		break;
		}
		printOutputFile();
	}
	
	/**
	 * Creates a four dimensional boolean array of possible words from the dictionary given a letter assigned to an index in the word
	 * 
	 * * Not implemented at this time *
	 */
	static void createPossWordArray() {
		// Initial status update
		System.out.print("Creating Array of Possible Words ... ");
		// For each word length
		possWordArray = new boolean[longestWord - shortestWord][][][];
		for (int i = 0; i < longestWord - shortestWord; i++) {
			// For each letter of each word length
			possWordArray[i] = new boolean[shortestWord + i][][];
			for (int j = 0; j < shortestWord + i; j++) {
				// For each letter of the alphabet
				possWordArray[i][j] = new boolean[26][];
				for (int k = 0; k < 26; k++) {
					// For each possible word of a given length
					possWordArray[i][j][k] = new boolean[dictionaryIndex[shortestWord + i + 1] - dictionaryIndex[shortestWord + i]];
					for (int l = 0; l < dictionaryIndex[shortestWord + i + 1] - dictionaryIndex[shortestWord + i]; l++) {
						possWordArray[i][j][k][l] = ((int)dictionary.get(dictionaryIndex[shortestWord + i] + l).charAt(j) == (int)('a' + k));
					}
				}
			}
			// Status update for each word length
			System.out.print((shortestWord + i) + " Letter Word Array Created ... ");
		}
		// Status update once array is created
		System.out.print("Letter Word Array Complete.\n");
	}
	
	/**
	 * Gets user input for the puzzle and dictionary choices
	 * Reads crossword puzzle and creates a linked list of words
	 * Reads word list and creates a dictionary
	 */
	static void readInputFiles() {
		
		/**
		 * Declare objects needed for file input
		 */
		Scanner userInput = new Scanner(System.in);
		Scanner puzzleInput = null;
		Scanner dictionaryInput = null;
		int puzzleChoice = 1;
		int dictionaryChoice = 1;
		File puzzleInputFile;
		File dictionaryInputFile;
		String dictionaryFileName = null;
		boolean hasCustomWords = true;
		int customWordChoice = 2;
		LinkedList<String> customWords = new LinkedList<String>();
		
		/**
		 * Prompt user for a puzzle file
		 */
		while (puzzleFileName == null) {
			System.out.println("Please select a file containing a puzzle. Enter digits as follows for the corresponding file.");
			System.out.println("1 - Heart Puzzle");
			System.out.println("2 - Tree Puzzle");
			System.out.println("3 - Simple Puzzle");
			System.out.println("4 - Cat Puzzle");
			System.out.println("5 - Select a Custom Puzzle");
			puzzleChoice = userInput.nextInt();
			if (puzzleChoice < 1 || puzzleChoice > 5) {
				System.out.println("Invalid selection, Tree Puzzle chosen by default.");
				puzzleChoice = 2;
			}
			switch (puzzleChoice) {
				case 1:  puzzleFileName = "Puzzle Heart.txt";
					break;
				case 2:  puzzleFileName = "Puzzle Tree.txt";
		        	break;
				case 3:  puzzleFileName = "Puzzle Simple.txt";
	        		break;
				case 4:  puzzleFileName = "Puzzle Cat.txt";
        			break;
				case 5:
					System.out.println("Please enter the custom file name.");
					puzzleFileName = userInput.next();
					break;
			}
			try {
				puzzleInputFile = new File(puzzleFileName);
				puzzleInput = new Scanner(puzzleInputFile);
			} catch (Exception e) {
				System.out.println("Invalid file name.");
				puzzleFileName = null;
			}
		}
		
		/**
		 * Prompt user for a dictionary file
		 */
		while (dictionaryFileName == null) {
			System.out.println("Please select a dictionary file containing a list of words. Enter digits as follows for the corresponding file.");
			System.out.println("1 - Standard Dictionary Word List");
			System.out.println("2 - Pre-Sorted Standard Dictionary Word List");
			System.out.println("3 - Simple Dictionary Word List Containing 15 Words");
			System.out.println("4 - Pre-Sorted Simple Dictionary Word List Containing 15 Words");
			System.out.println("5 - Select a Custom Dictionary Word List");
			dictionaryChoice = userInput.nextInt();
			if (dictionaryChoice < 1 || dictionaryChoice > 5) {
				System.out.println("Invalid selection, Pre-Sorted Standard Dictionary Word List chosen by default.");
				dictionaryChoice = 2;
			}
			switch (dictionaryChoice) {
				case 1:  dictionaryFileName = "Words.txt";
					break;
				case 2:  dictionaryFileName = "Words Sorted.txt";
					if (puzzleChoice == 1) dictionaryFileName = "Words Sorted Heart.txt";
					if (puzzleChoice == 2) dictionaryFileName = "Words Sorted Tree.txt";
					if (puzzleChoice == 3) dictionaryFileName = "Words Sorted Simple.txt";
					sortedDictionary = true;
					break;
				case 3:  dictionaryFileName = "Words Simple.txt";
					if (puzzleChoice != 3) {
						System.out.println("Invalid selection, Standard Dictionary Word List chosen by default.");
						dictionaryFileName = "Words.txt";
					}
	        		break;
				case 4:  dictionaryFileName = "Words Simple Sorted.txt";
					if (puzzleChoice != 3) {
						System.out.println("Invalid selection, Pre-Sorted Standard Dictionary Word List chosen by default.");
						dictionaryFileName = "Words Sorted.txt";
						if (puzzleChoice == 1) dictionaryFileName = "Words Sorted Heart.txt";
						if (puzzleChoice == 2) dictionaryFileName = "Words Sorted Tree.txt";
					}
					sortedDictionary = true;
					break;
				case 5:
					System.out.println("Please enter the custom file name.");
					dictionaryFileName = userInput.next();
					break;
			}
			try {
				dictionaryInputFile = new File(dictionaryFileName);
				dictionaryInput = new Scanner(dictionaryInputFile);
			} catch (Exception e) {
				System.out.println("Invalid file name.");
				dictionaryFileName = null;
			}
		}
		
		/**
		 * Prompt user for a custom word
		 */
		System.out.println("Adding a custom word may be required if the puzzle file contains words that are not in the dictionary word list.");
		System.out.println("For example if initials are included in the Heart Puzzle the initials will need to be added here if they are not also a word.");
		while (hasCustomWords) {
			System.out.println("Would you like to add a custom word?");
			System.out.println("1 - Yes");
			System.out.println("2 - No");
			customWordChoice = userInput.nextInt();
			if (customWordChoice < 1 || customWordChoice > 2) {
				System.out.println("Invalid Selection.");
			} else {
				hasCustomWords = customWordChoice == 1;
				if(hasCustomWords) {
					System.out.println("Please enter the word. Format supports lower case letters a-z only.");
					System.out.println("Do not include special characters, upper case letters, or numbers.");
					customWords.addLast(userInput.next());
					System.out.println(customWords.getLast() + " added.");
				}
			}
		}
		
		/**
		 * Prompt user for search heuristics
		 */
		System.out.println("Please select the search heuristics to use.");
		System.out.println("1 - Simple Backtracking Algorithm with No Heuristics");
		System.out.println("2 - Forward Checking Only");
		System.out.println("3 - Forward Checking with Minimum Remaining Values");
		System.out.println("4 - Forward Checking with Degree Heuristics");
		heuristicChoice = userInput.nextInt();
		if (heuristicChoice < 1 || heuristicChoice > 4) {
			System.out.println("Invalid selection, Forward Checking with Minimum Remaining Values chosen by default.");
			heuristicChoice = 3;
		}
				
		/**
		 * Read the puzzle file and creates a string representing the puzzle
		 */
		startTime = System.currentTimeMillis();
		// Status update when creation of the puzzle begins
		System.out.print("Reading Inputs ... Creating Puzzle ... ");
		while (puzzleInput.hasNextLine()) {
			height++;
			puzzle = puzzle + puzzleInput.nextLine();
		}
		width = puzzle.length() / height;
		shortestWord = Math.min(width, height);
		// Any letters that are not 0 in the input file are locked
		lockedAcross = new boolean[puzzle.length()];
		lockedDown = new boolean[puzzle.length()];
		lockedInput = new boolean[puzzle.length()];
		for (int i = 0; i < puzzle.length(); i++) {
			lockedAcross[i] = puzzle.charAt(i) != '0';
			lockedDown[i] = puzzle.charAt(i) != '0';
			lockedInput[i] = puzzle.charAt(i) != '0';
		}
		// Status update when puzzle creation is complete
		System.out.print(width + " by " + height + " Puzzle Created ... Finding Words ... ");
		
		/**
		 * Find the locations of words inside the puzzle and create a linked list of words
		 */
		words = new LinkedList<Word>();
		int label = 1;
		for (int i = 0; i < puzzle.length(); i++) {
			boolean increment = false;
			// Look down
			if (i < width * (height - 1) && (i < width || puzzle.charAt(i - width)  == ' ') && puzzle.charAt(i) != ' ' && puzzle.charAt(i + width) != ' ') {
				increment = true;
				// Find word length
				int length = 1;
				while (i + length * width < height * width && puzzle.charAt(i + length * width) != ' ') length++;
				// Add word
				Word nextWord = new Word(i, label, length, false);
				words.addLast(nextWord);
				// Update longest and shortest words
				if (length > longestWord) longestWord = length;
				if (length < shortestWord) shortestWord = length;
			}
			// Look across
			if (i % width != width-1 && (i % width == 0 || puzzle.charAt(i - 1)  == ' ') && puzzle.charAt(i) != ' ' && puzzle.charAt(i + 1) != ' ') {
				increment = true;
				// Find word length
				int length = 1;
				while ((i + length) % width != 0 && puzzle.charAt(i + length) != ' ') length++;
				// Add word
				Word nextWord = new Word(i, label, length, true);
				words.addLast(nextWord);
				// Update longest and shortest words
				if (length > longestWord) longestWord = length;
				if (length < shortestWord) shortestWord = length;
			}
			if (increment) label++;
		}
		// Status update when all words have been found
		System.out.print(words.size() + " Words Found ... Finding Cross Points ... ");
		
		/**
		 * Find points where words cross
		 */
		// For each word
		for (int i = 0; i < words.size(); i++) {
			int crossWordIndex = 0;
			// Check against each word
			for (int j = 0; j < words.size(); j++) {
				// If words are in different directions
				if (words.get(i).across != words.get(j).across) {
					// For each letter of both words
					for (int k = 0; k < words.get(i).length; k++) {
						for (int l = 0; l < words.get(j).length; l++) {
							if (words.get(i).across) {
								// Look across
								if (words.get(i).index + k == words.get(j).index + l * width) {
									// Add word to list of crossing words
									words.get(i).crossWords[crossWordIndex] = j;
									crossWordIndex++;
									// Exit for loops
									k = words.get(i).length;
									l = words.get(j).length;
								}
							} else {
								// Look down
								if (words.get(i).index + k * width == words.get(j).index + l) {
									// Add word to list of crossing words
									words.get(i).crossWords[crossWordIndex] = j;
									crossWordIndex++;
									// Exit for loops
									k = words.get(i).length;
									l = words.get(j).length;
								}
							}
						}
					}
				}
			}
			// Fill in remaining values with -1
			for (int j = crossWordIndex; j < words.get(i).length; j++) words.get(i).crossWords[j] = -1;
		}
		// Status update when all points where words cross have been found
		System.out.print("Cross Points Found ... Creating Dictionary ... ");
		
		/**
		 * Read words from word list and custom words into dictionary
		 */
		dictionary = new LinkedList<String>();
		dictionaryIndex = new int[longestWord+1];
		for (int i = 0; i <= longestWord; i++) dictionaryIndex[i] = 0;
		// Get each word from the input file
		while (dictionaryInput.hasNextLine()) {
			String nextWord = dictionaryInput.nextLine();
			// If the word is too long or too short it is ignored
			if (nextWord.length() >= shortestWord && nextWord.length() <= longestWord) {
				// Add words to dictionary
				if (nextWord.length() < longestWord) {
					dictionary.add(dictionaryIndex[nextWord.length()], nextWord);
				} else dictionary.addLast(nextWord);
				// Update the index for each word length
				for (int i = nextWord.length(); i <= longestWord; i++) dictionaryIndex[i]++;
			}
		}
		// Get each custom word
		while (!customWords.isEmpty()) {
			String nextWord = customWords.removeFirst();
			// If the word is too long or too short it is ignored
			if (nextWord.length() >= shortestWord && nextWord.length() <= longestWord) {
				// Add words to dictionary
				if (nextWord.length() < longestWord) {
					dictionary.add(dictionaryIndex[nextWord.length()], nextWord);
				} else dictionary.addLast(nextWord);
				// Update the index for each word length
				for (int i = nextWord.length(); i <= longestWord; i++) dictionaryIndex[i]++;
			}
		}
		// Status update when words have been added to the dictionary
		System.out.print(dictionary.size() + " Words Added to Dictionary ... Inputs Complete.\n");
		
		/**
		 * Save list of possible words to each word
		 */
		for (int i = 0; i < words.size(); i++) {
			words.get(i).possWords  = new LinkedList<String>();
			for (int j = dictionaryIndex[words.get(i).length - 1]; j < dictionaryIndex[words.get(i).length]; j++)
				words.get(i).possWords.addLast(dictionary.get(j));
		}
		
		/**
		 * Close inputs
		 */
		userInput.close();
		dictionaryInput.close();
		puzzleInput.close();
	}
	
	/**
	 * Sorts the dictionary based on the number of times each letter in each word is used in the dictionary
	 */
	static void sortDictionary() {
		// Initial status update
		System.out.print("Sorting Dictionary ... ");
		
		/**
		 * Count the number of times each letter is used in the dictionary to assign a score to each letter
		 */
		letterScore = new int[26];
		for (int i = 0; i < 26; i++) letterScore[i] = 0;
		for (int i = 0; i < dictionary.size(); i++) {
			for (int j = 0; j < dictionary.get(i).length(); j++) {
				letterScore[(int)dictionary.get(i).charAt(j) - (int)'a'] += ((int)dictionary.get(i).charAt(j) >= (int)'a' && (int)dictionary.get(i).charAt(j) <= (int)'z') ? 1 : 0;
			}
		}
		// Status update once counting letters complete
		System.out.print("Letter Scores Assigned ... ");
		
		/**
		 * Sort dictionary based on the amount of times each letter in each word is used
		 */
		// For each word length
		for (int i = shortestWord-1; i < longestWord; i++) {
			// For each word of each length
			for (int j = dictionaryIndex[i]+1; j < dictionaryIndex[i+1]; j++) {
				// Get the word score
				int score = wordScore(dictionary.get(j));
				// If lowest word score the word is already in the last position
				if (!(score <= wordScore(dictionary.get(j-1)))) {
					// If highest word score move to front
					if (score >= wordScore(dictionary.get(dictionaryIndex[i]))) {
						dictionary.add(dictionaryIndex[i], dictionary.remove(j));
					} else {
						// If in between find location to move word
						int right = j-1;
						int left = dictionaryIndex[i];
						while (right - left > 1) {
							int mid = left + ((right - left) / 2);
							if (score > wordScore(dictionary.get(mid))) {
								right = mid;
							} else {
								left = mid;
							}
						}
						dictionary.add(right, dictionary.remove(j));
					}
				}
			}
			// Status update for each word length
			System.out.print((i+1) + " Letter Words Sorted ... ");
		}
		// Final status update
		System.out.print("Sorting Complete.\n");
	}
	
	/**
	 * Calculates a score for the input word based on the number of times each letter in the word is used in the dictionary
	 * @param word input word to assign a value
	 * @return value based on the number of times each letter in the word is used in the dictionary
	 */
	static int wordScore(String word) {
		int score = 0;
		for (int i = 0; i < word.length(); i++) {
			score += ((int)word.charAt(i) >= (int)'a' && (int)word.charAt(i) <= (int)'z') ? letterScore[(int)word.charAt(i) - (int)'a'] : 0;
		}
		return score;
	}
	
	/**
	 * Performs a simple backtracking search to find a solution to a puzzle
	 * @param word index of a word to check
	 * @return true if a solution is found
	 */
	static boolean backtrackingAlgorithm(int word) {
		//Test to see if algorithm has completed the puzzle
		boolean test = true;
		for (int i = 0; i < words.size(); i++) {
			if (words.get(i).dictionaryIndex == -1) {
				test = false;
				i = words.size();
			}
		}
		if (test) return test;
		//Try each possible word in the dictionary
		for (int i = dictionaryIndex[words.get(word).length - 1]; i < dictionaryIndex[words.get(word).length]; i++) {
			// If word is valid using forward checking
			if (compareWord(words.get(word), i)) {
				// If adding the word results in another word having no possible words remove the word
				addWord(words.get(word), i);
				// Perform recursive search using the next word
				if (backtrackingAlgorithm(word + 1)) {
					return true;
				} else removeWord(words.get(word));
			}
		}
		// All words have been attempted, no result found
		return false;
	}
	
	/**
	 * Performs a backtracking search to find a solution to a puzzle using forward checking only
	 * @param word index of a word to check
	 * @return true if a solution is found
	 */
	static boolean backtrackingAlgorithmFC(int word) {
		//Test to see if algorithm has completed the puzzle
		boolean test = true;
		for (int i = 0; i < words.size(); i++) {
			if (words.get(i).dictionaryIndex == -1) {
				test = false;
				i = words.size();
			}
		}
		if (test) return test;
		//Try each possible word in the dictionary
		for (int i = dictionaryIndex[words.get(word).length - 1]; i < dictionaryIndex[words.get(word).length]; i++) {
			// If word is valid using forward checking
			if (compareWord(words.get(word), i)) {
				// If adding the word results in another word having no possible words remove the word
				if (!addWord(words.get(word), i)) {
					removeWord(words.get(word));
				} else {
					// Perform recursive search using the next word
					if (backtrackingAlgorithmFC(word + 1)) {
						return true;
					} else removeWord(words.get(word));
				}
			}
		}
		// All words have been attempted, no result found
		return false;
	}
	
	/**
	 * Performs a backtracking search to find a solution to a puzzle using forward checking and minimum remaining values
	 * @param word index of a word to check
	 * @return true if a solution is found
	 */
	static boolean backtrackingAlgorithmMRV(int word) {
		//Test to see if algorithm has completed the puzzle
		boolean test = true;
		for (int i = 0; i < words.size(); i++) {
			if (words.get(i).dictionaryIndex == -1) {
				test = false;
				i = words.size();
			}
		}
		if (test) return test;
		//Try each possible word in the dictionary
		for (int i = dictionaryIndex[words.get(word).length - 1]; i < dictionaryIndex[words.get(word).length]; i++) {
			// If word is valid using forward checking
			if (compareWord(words.get(word), i)) {
				// If adding the word results in another word having no possible words remove the word
				if (!addWord(words.get(word), i)) {
					removeWord(words.get(word));
				} else {
					// Perform recursive search using the next word
					if (backtrackingAlgorithmMRV(getMRV())) {
						return true;
					} else removeWord(words.get(word));
				}
			}
		}
		// All words have been attempted, no result found
		return false;
	}
	
	/**
	 * Performs a backtracking search to find a solution to a puzzle using forward checking and degree heuristics
	 * @param word index of a word to check
	 * @return true if a solution is found
	 */
	static boolean backtrackingAlgorithmDH(int word) {
		//Test to see if algorithm has completed the puzzle
		boolean test = true;
		for (int i = 0; i < words.size(); i++) {
			if (words.get(i).dictionaryIndex == -1) {
				test = false;
				i = words.size();
			}
		}
		if (test) return test;
		//Try each possible word in the dictionary
		for (int i = dictionaryIndex[words.get(word).length - 1]; i < dictionaryIndex[words.get(word).length]; i++) {
			// If word is valid using forward checking
			if (compareWord(words.get(word), i)) {
				// If adding the word results in another word having no possible words remove the word
				if (!addWord(words.get(word), i)) {
					removeWord(words.get(word));
				} else {
					// Perform recursive search using the next word
					if (backtrackingAlgorithmDH(getDH())) {
						return true;
					} else removeWord(words.get(word));
				}
			}
		}
		// All words have been attempted, no result found
		return false;
	}
	
	/**
	 * Finds the word with the minimum remaining values for possible words
	 * @return index of the word with the minimum remaining values for possible words
	 */
	static int getMRV() {
		int nextWord = -1;
		int mrv = dictionary.size();
		for (int i = 0; i < words.size(); i++) {
			if (words.get(i).possWords.size() < mrv && words.get(i).dictionaryIndex == -1) {
				nextWord = i;
				mrv = words.get(i).possWords.size();
			}
		}
		return nextWord;
	}
	
	/**
	 * Finds the word with the greatest degree heuristic which puts the most constraints on other possible words
	 * @return index of the word with the greatest degree heuristic
	 */
	static int getDH() {
		int nextWord = -1;
		int dh = -1;
		for (int i = 0; i < words.size(); i++) {
			if (words.get(i).dictionaryIndex == -1) {
				int emptySpaces = 0;
				int crossWords = 0;
				if  (words.get(i).across) {
					for (int j = 0; j < words.get(i).length; j++) emptySpaces += (puzzle.charAt(words.get(i).index + j) == '0') ? 1 : 0;
				} else {
					for (int j = 0; j < words.get(i).length; j++) emptySpaces += (puzzle.charAt(words.get(i).index + j * width) == '0') ? 1 : 0;
				}
				for (int j = 0; j < words.get(i).length; j++) crossWords += (words.get(i).crossWords[j] != -1) ? 1 : 0;
				if (crossWords - words.get(i).length + emptySpaces > dh) {
					dh = crossWords - words.get(i).length + emptySpaces;
					nextWord = i;
				}
			}
		}
		return nextWord;
	}
	
	/**
	 * Updates the list of possible words for a given word
	 * @param puzzleWord word to update list of possible words
	 * @return true if the word has possible words after being updated
	 */
	static boolean updatePossWords(Word puzzleWord) {
		for (int i = 0; i < puzzleWord.length; i++) {
			for (int j = 0; j < puzzleWord.possWords.size(); j++) {
				if(puzzleWord.across) {
					if (puzzle.charAt(puzzleWord.index + i) != '0' && puzzle.charAt(puzzleWord.index + i) != puzzleWord.possWords.get(j).charAt(i)) {
						puzzleWord.possWords.remove(j);
						j--;
					}
				} else {
					if (puzzle.charAt(puzzleWord.index + i * width) != '0' && puzzle.charAt(puzzleWord.index + i * width) != puzzleWord.possWords.get(j).charAt(i)) {
						puzzleWord.possWords.remove(j);
						j--;
					}
				}
			}
		}
		return !puzzleWord.possWords.isEmpty();
	}
	
	/**
	 * Resets the list of possible words for a given word
	 * @param puzzleWord word to reset list of possible words
	 * @return true if the word has possible words after being reset
	 */
	static boolean resetPossWords(Word puzzleWord) {
		puzzleWord.possWords.clear();
		for (int i = dictionaryIndex[puzzleWord.length - 1]; i < dictionaryIndex[puzzleWord.length]; i++)
			puzzleWord.possWords.addLast(dictionary.get(i));
		return updatePossWords(puzzleWord);
	}
	
	/**
	 * Compares a possible word to the puzzle to determine if the word can be added
	 * @param puzzleWord word to compare to the puzzle
	 * @param dictionaryIndex index of word in the dictionary
	 * @return true if the word can be added
	 */
	static boolean compareWord(Word puzzleWord, int dictionaryIndex) {
		// Metric to track number of times this method is called
		comparisons++;
		// Check to see if word is already used
		for (int i = 0; i < words.size(); i++) if (words.get(i).dictionaryIndex == dictionaryIndex) return false; 
		// Check letters that have already been filled in
		if (puzzleWord.across) {
			for (int i = 0; i < puzzleWord.length; i++)
				if (puzzle.charAt(puzzleWord.index + i) != '0' && puzzle.charAt(puzzleWord.index + i) != dictionary.get(dictionaryIndex).charAt(i)) return false;
		} else {
			for (int i = 0; i < puzzleWord.length; i++)
				if (puzzle.charAt(puzzleWord.index + i * width) != '0' && puzzle.charAt(puzzleWord.index + i * width) != dictionary.get(dictionaryIndex).charAt(i)) return false;
		}
		return true;
	}

	/**
	 * Adds a word from the crossword puzzle
	 * Updates possible words for crossing words
	 * @param puzzleWord word to add
	 * @param dictionaryIndex index of word in the dictionary
	 * @return true if adding the word resulted in all of the words crossing the word having possible words
	 */
	static boolean addWord(Word puzzleWord, int dictionaryIndex) {
		lockPuzzle(puzzleWord);
		puzzleWord.dictionaryIndex = dictionaryIndex;
		puzzleWord.str = dictionary.get(dictionaryIndex);
		if (puzzleWord.across) {
			for (int i = 0; i < puzzleWord.length; i++)	{
				if (puzzle.charAt(puzzleWord.index + i) == '0')
					puzzle = puzzle.substring(0, puzzleWord.index + i) + dictionary.get(dictionaryIndex).charAt(i) + puzzle.substring(puzzleWord.index + i + 1);
			}
		} else {
			for (int i = 0; i < puzzleWord.length; i++)	{
				if (puzzle.charAt(puzzleWord.index + i * width) == '0')
					puzzle = puzzle.substring(0, puzzleWord.index + i * width) + dictionary.get(dictionaryIndex).charAt(i) + puzzle.substring(puzzleWord.index + i * width + 1);
			}
		}
		
		boolean success = true;
		for (int i = 0; i < puzzleWord.length; i++) {
			if (puzzleWord.crossWords[i] != -1)
				success = success && updatePossWords(words.get(puzzleWord.crossWords[i]));
		}
		return success;
	}
	
	/**
	 * Removes a word from the crossword puzzle
	 * Sets all letters not used in other words to 0
	 * Updates possible words for crossing words
	 * @param puzzleWord word to remove
	 * @return true if removing the word resulted in all of the words crossing the word having possible words
	 */
	static boolean removeWord(Word puzzleWord) {
		unlockPuzzle(puzzleWord);
		puzzleWord.dictionaryIndex = -1;
		puzzleWord.str = "";
		if (puzzleWord.across) {
			for (int i = 0; i < puzzleWord.length; i++)	{
				if (!lockedAcross[puzzleWord.index + i] && !lockedDown[puzzleWord.index + i] && !lockedInput[puzzleWord.index + i])
					puzzle = puzzle.substring(0, puzzleWord.index + i) + '0' + puzzle.substring(puzzleWord.index + i + 1);
			}
		} else {
			for (int i = 0; i < puzzleWord.length; i++)	{
				if (!lockedAcross[puzzleWord.index + i * width] && !lockedDown[puzzleWord.index + i * width] && !lockedInput[puzzleWord.index + i * width])
					puzzle = puzzle.substring(0, puzzleWord.index + i * width) + '0' + puzzle.substring(puzzleWord.index + i * width + 1);
			}
		}
		
		boolean success = true;
		for (int i = 0; i < puzzleWord.length; i++) {
			if (puzzleWord.crossWords[i] != -1)
				success = success && resetPossWords(words.get(puzzleWord.crossWords[i]));
		}
		return success;
	}
	
	/**
	 * Locks the spaces in the puzzle when a word is added in the direction of the word
	 * @param puzzleWord word being added
	 */
	static void lockPuzzle(Word puzzleWord) {
		if (puzzleWord.across) {
			for (int i = 0; i < puzzleWord.length; i++)	lockedAcross[puzzleWord.index + i] = true;
		} else {
			for (int i = 0; i < puzzleWord.length; i++)	lockedDown[puzzleWord.index + i * width] = true;
		}
	}
	
	/**
	 * Unlocks the spaces in the puzzle when a word is removed in the direction of the word
	 * @param puzzleWord word being removed
	 */
	static void unlockPuzzle(Word puzzleWord) {
		if (puzzleWord.across) {
			for (int i = 0; i < puzzleWord.length; i++)	lockedAcross[puzzleWord.index + i] = false;
		} else {
			for (int i = 0; i < puzzleWord.length; i++)	lockedDown[puzzleWord.index + i * width] = false;
		}
	}
	
	/**
	 * Creates an output file with the puzzle solution and program performance data
	 */
	static void printOutputFile() {
				
		/**
		 * Create Output File
		 */
		String heuristic = "BT ";
		if (heuristicChoice == 2) heuristic = "FC ";
		if (heuristicChoice == 3) heuristic = "FC-MRV ";
		if (heuristicChoice == 4) heuristic = "FC-DH ";
		File outputFile;
		PrintStream output = null;
		String outputName = "Solution " + heuristic + puzzleFileName;
		try {
			outputFile = new File(outputName);
			output = new PrintStream(outputFile);
		}
		catch (Exception e) {
			System.out.println("Output file " + outputName + " not created.");
		}

		/**
		 * Print the puzzle solution to the output file
		 */
		heuristic = "Backtracking Algorithm\n";
		if (heuristicChoice == 2) heuristic = "Backtracking Algorithm with Forward Checking\n";
		if (heuristicChoice == 3) heuristic = "Backtracking Algorithm with Forward Checking and Minimum Remaining Values\n";
		if (heuristicChoice == 4) heuristic = "Backtracking Algorithm with Forward Checking and Degree Heuristic\n";
		output.printf(puzzleFileName + " Solution " + heuristic);
		for (int i = 0; i < puzzle.length(); i++) {
			if (i % width == 0) output.printf("\n");
			output.print(puzzle.charAt(i));
		}
		
		/**
		 * Calculate execution time for output statistics
		 */
		// Calculate search time
		long searchTime = (System.currentTimeMillis() - searchStartTime)/1000;
		long searchHours = searchTime / 3600;
		long searchMinutes = searchTime / 60 % 60;
		long searchSeconds = searchTime % 60;
		long searchMilliseconds = (System.currentTimeMillis() - searchStartTime) % 1000;
		// Calculate preparation time
		long prepTime = (searchStartTime - startTime)/1000;
		long prepHours = prepTime / 3600;
		long prepMinutes = prepTime / 60 % 60;
		long prepSeconds = prepTime % 60;
		long prepMilliseconds = (searchStartTime - startTime) % 1000;
		// Calculate total time
		long totalTime = (System.currentTimeMillis() - startTime)/1000;
		long totalHours = totalTime / 3600;
		long totalMinutes = totalTime / 60 % 60;
		long totalSeconds = totalTime % 60;
		long totalMilliseconds = (System.currentTimeMillis() - startTime) % 1000;
		
		/**
		 * Print the performance data to the output file
		 */
		output.printf("\n\nProgram Performance Data:\n");
		output.printf("Total comparisons: %d\n", comparisons);
		output.printf("Search time: %d:%d:%d:%d\n", searchHours, searchMinutes, searchSeconds, searchMilliseconds);
		output.printf("Preparation time: %d:%d:%d:%d\n", prepHours, prepMinutes, prepSeconds, prepMilliseconds);
		output.printf("Total time: %d:%d:%d:%d\n", totalHours, totalMinutes, totalSeconds, totalMilliseconds);
		output.printf("Dictionary Summary:\n");
		output.printf("Shortest Word: %d Letters, Longest Word: %d Letters\nDictionary Word Counts: ", shortestWord, longestWord);
		for(int i = 1; i <= longestWord; i++) output.printf((dictionaryIndex[i]-dictionaryIndex[i-1]) + " ");
		output.printf("\nDictionary Sample: ");
		for(int i = shortestWord-1; i < longestWord; i++) {
			output.printf(dictionary.get(dictionaryIndex[i]) + ", " + dictionary.get(dictionaryIndex[i] + 1) + " ... ");
			output.printf(dictionary.get(dictionaryIndex[i+1] - 2) + ", " + dictionary.get(dictionaryIndex[i+1]-1) + "; ");
		}
		
		/**
		 * Print the word list to the output file
		 */
		output.printf("\n\nWord List: Index - Length - Word\n");
		for (int i = 0; i < words.size(); i++) {
			if (words.get(i).across) output.printf(words.get(i).toString() + "\n");
		}
		for (int i = 0; i < words.size(); i++) {
			if (!words.get(i).across) output.printf(words.get(i).toString() + "\n");
		}
	}
}