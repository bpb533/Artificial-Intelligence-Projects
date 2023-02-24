package freedom;

import javax.swing.SwingUtilities;

public class FreedomGame {
	
	public static void main (String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new FreedomGUI();
			}
		});
	}
}
