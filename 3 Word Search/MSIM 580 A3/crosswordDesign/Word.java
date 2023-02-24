package crosswordDesign;

import java.util.LinkedList;

/**
 * The word class represents a word in a crossword puzzle
 * @author Ben Bissantz
 */
public class Word {
	
	/**
	 * Variables associated with each word
	 */
	int index;
	int label;
	int length;
	boolean across;
	int[] crossWords;
	LinkedList<String> possWords;
	int dictionaryIndex = -1;
	String str = "";
	
	/**
	 * Constructor class for the word
	 * @param puzzleIndex starting index of the word in the puzzle string
	 * @param puzzleLabel label of the word when the puzzle is printed
	 * @param wordLength length of the word
	 * @param acrossDirection true if across, false if down
	 */
	public Word (int puzzleIndex, int puzzleLabel, int wordLength, boolean acrossDirection) {
		index = puzzleIndex;
		label = puzzleLabel;
		length = wordLength;
		across = acrossDirection;
		crossWords = new int[length];
	}
	
	/**
	 * Creates a string using the word parameters for program output
	 */
	public String toString() {
		return (across) ? String.format("%1$2s Across - %2$2s Letters - %3$s", label, length, str)  : String.format("%1$2s Down   - %2$2s Letters - %3$s", label, length, str);
	}
}