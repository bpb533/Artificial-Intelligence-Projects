package freedom;

import javax.swing.SwingUtilities;

/**
 * This program creates a playable version of the game Freedom
 * Players take turns selecting spaces with the goal of getting four spaces in a row
 * 
 * This program has the option of selecting one or two computer players
 * The computer players implement either the minimax or the alpha-beta pruning algorithm
 * 
 * Additional information on the game Freedom can be found here
 * https://boardgamegeek.com/boardgame/100480/freedom
 * 
 * @author Ben Bissantz
 */
public class FreedomGame {
	
	/**
	 * Main method calles the FreedomGUI class
	 * @param args standard arguments
	 */
	public static void main (String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new FreedomGUI();
			}
		});
	}
}
