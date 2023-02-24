package freedom;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * This Class implements a graphical user interface to play the game Freedom
 * 
 * This Class contains code that has been modified from Pedro's Tic Tac Toe Tutorial
 * Tic Tac Toe GUI Project Parts 1-5 (Java Swing) available on YouTube here
 * https://www.youtube.com/watch?v=YMeVSoNumAg
 * 
 * @author Ben Bissantz
 */
public class FreedomGUI extends JFrame {
	
	/**
	 * Declaration of variables
	 */
	private static final long serialVersionUID = 1L;
	private Container pane;
	private String currentPlayer;
	private String otherPlayer;
	private String playerOne = "\u26AB";
	private String playerTwo = "\u26AA";
	private boolean computerOne = false;
	private boolean computerTwo = false;
	private boolean alphaBetaAlgorithm = false;
	private int boardSize = 10;
	private int emptySpaces;
	private JButton[][] board;
	private String[][] boardState;
	private boolean gameOver;

	/**
	 * Declaration of menu objects
	 */
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem newHvH;
	private JMenuItem newHvC;
	private JMenuItem newCvC;
	private JMenuItem quit;
	private JMenu optionsMenu;
	private JMenuItem minimax;
	private JMenuItem alphaBeta;
	private JMenuItem sizeSix;
	private JMenuItem sizeEight;
	private JMenuItem sizeTen;
	
	/**
	 * Constructor method for the Freedom graphical user interface class
	 * Creates the window for the program and initializes the menu bar and board
	 */
	public FreedomGUI() {
		super();
		pane = getContentPane();
		setTitle("Freedom Game");
		setSize(boardSize*72,boardSize*72);
		setResizable(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
		initializeMenuBar();
		resetBoard();
	}
	
	/**
	 * Creates the menu bar and file and options drop down menus
	 */
	private void initializeMenuBar() {
		// Menu bar drop down menus
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		optionsMenu = new JMenu("Options");
		
		// Options for new games Human vs Human
		newHvH = new JMenuItem("New Human vs Human Game");
		newHvH.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				computerOne = false;
				computerTwo = false;
				resetBoard();
			}
		});
		fileMenu.add(newHvH);
		// Options for new games Human vs Computer
		newHvC = new JMenuItem("New Human vs Computer Game");
		newHvC.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				computerOne = false;
				computerTwo = true;
				resetBoard();
			}
		});
		fileMenu.add(newHvC);
		// Options for new games Computer vs Computer		
		newCvC = new JMenuItem("New Computer vs Computer Game");
		newCvC.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				computerOne = true;
				computerTwo = true;
				resetBoard();
				while (!gameOver) {
					computerMove();
					gameOver();
				}
			}
		});
		fileMenu.add(newCvC);
		
		// Quit option
		quit = new JMenuItem("Quit");
		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.add(quit);
		
		// Options for computer algorithm minimax
		minimax = new JMenuItem("Computer Algorithm Minimax");
		minimax.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				alphaBetaAlgorithm = false;
				resetBoard();
			}
		});
		optionsMenu.add(minimax);
		// Options for computer algorithm alpha-beta		
		alphaBeta = new JMenuItem("Computer Algorithm Alpha-Beta");
		alphaBeta.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				alphaBetaAlgorithm = true;
				resetBoard();
			}
		});
		optionsMenu.add(alphaBeta);
		
		// Options for board size 6x6
		sizeSix = new JMenuItem("Board Size 6x6");
		sizeSix.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boardSize = 6;
				resetBoard();
			}
		});
		optionsMenu.add(sizeSix);
		// Options for board size 8x8
		sizeEight = new JMenuItem("Board Size 8x8");
		sizeEight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boardSize = 8;
				resetBoard();
			}
		});
		optionsMenu.add(sizeEight);
		// Options for board size 10x10
		sizeTen = new JMenuItem("Board Size 10x10");
		sizeTen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boardSize = 10;
				resetBoard();
			}
		});
		optionsMenu.add(sizeTen);
		
		// Add items to menu bar
		menuBar.add(fileMenu);
		menuBar.add(optionsMenu);
		setJMenuBar(menuBar);
	}
	
	/**
	 * Resets the board to an empty board based on the selected board size
	 */
	private void resetBoard() {
		// Reset the window
		currentPlayer = playerOne;
		otherPlayer = playerTwo;
		gameOver = false;
		pane.removeAll();
		pane.repaint();
		pane.setLayout(new GridLayout(boardSize,boardSize));
		emptySpaces = boardSize * boardSize;
		board = new JButton[boardSize][boardSize];
		boardState = new String[boardSize][boardSize];
		// Add buttons to the board
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				JButton btn = new JButton();
				btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 40));
				board[i][j] = btn;
				boardState[i][j] = "";
				btn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (((JButton)e.getSource()).getText().equals("") && gameOver == false) {
							btn.setText(currentPlayer);
							updateBoardState();
							togglePlayer();
							gameOver();
							if (computerTwo) {
								computerMove();
								gameOver();
							}
						}
					}
				});
				pane.add(btn);
				validate();
			}
		}
		setSize(boardSize*72,boardSize*72);
	}
	
	/**
	 * If there is a computer player calls the selected algorithm
	 */
	private void computerMove() {
		if (currentPlayer.equals(playerOne) && computerOne == true || currentPlayer.equals(playerTwo) && computerTwo == true) {
			if (alphaBetaAlgorithm) alphaBetaMove(); else minimaxMove();
			togglePlayer();
		}
	}
	
	/**
	 * Performs a random move
	 */
	private void randomMove() {
		boolean played = false;
		while (!played) {
			int i = (int) (Math.random() * boardSize);
			int j = (int) (Math.random() * boardSize);
			if (board[i][j].getText().equals("")) {
				board[i][j].setText(currentPlayer);
				boardState[i][j] = currentPlayer;
				played = true;
			}
		}
	}
	
	/**
	 * Performs a move for the computer player using the alpha-beta pruning algorithm 
	 */
	private void alphaBetaMove() {
		// Variables needed to update the board
		int x = -1;
		int y = -1;
		int max = 0 - (boardSize * boardSize);
		// Search depth increases as less spaces are available
		int searchDepth = (emptySpaces < 14) ? emptySpaces - 1 : 7;
		if (emptySpaces > 16) searchDepth = 6;
		if (emptySpaces > 20) searchDepth = 5;
		if (emptySpaces > 24) searchDepth = 4;
		if (emptySpaces > 40) searchDepth = 3;
		if (emptySpaces > 80) searchDepth = 2;
		// For each empty space
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (boardState[i][j].equals("")) {
					// Create a new board state for each possible move
					String[][] testBoard = new String[boardSize][boardSize];
					for (int k = 0; k < boardSize; k++) {
						for (int l = 0; l < boardSize; l++) {
							testBoard[k][l] = boardState[k][l];
						}
					}
					// Try each empty space
					testBoard[i][j] = currentPlayer;
					int min = minValueAB(testBoard, 0 - (boardSize * boardSize), boardSize * boardSize, searchDepth);
					if (min > max || (min == max && Math.random() < 4 / emptySpaces)) {
						max = min;
						x = i;
						y = j;
					}
				}
			}
		}
		// Update the board and board state
		board[x][y].setText(currentPlayer);
		boardState[x][y] = currentPlayer;
	}
	
	/**
	 * Max value function for the alpha-beta pruning algorithm
	 * @param possBoard a possible board state to test
	 * @param alpha value of alpha in the alpha-beta pruning algorithm
	 * @param beta value of beta in the alpha-beta pruning algorithm
	 * @param searchDepth depth to continue searching
	 * @return utility of the move selected by the max player
	 */
	private int maxValueAB(String[][] possBoard, int alpha, int beta, int searchDepth) {
		// If search depth has been reached return utility value
		if (searchDepth == 0) return (getPossScore(possBoard, currentPlayer) - getPossScore(possBoard, otherPlayer));
		int max = 0 - (boardSize * boardSize);
		// For each empty space
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (possBoard[i][j].equals("")) {
					// Create a new board state for each possible move
					String[][] testBoard = new String[boardSize][boardSize];
					for (int k = 0; k < boardSize; k++) {
						for (int l = 0; l < boardSize; l++) {
							testBoard[k][l] = possBoard[k][l];
						}
					}
					// Add move to the new board state and compare results from the min value function 
					testBoard[i][j] = currentPlayer;
					int min = minValueAB(testBoard, alpha, beta, searchDepth-1);
					max = (min > max) ? min : max;
					if (max >= beta) return max;
					alpha = (alpha > max) ? alpha : max;
				}
			}
		}
		// Return maximum utility value
		return max;
	}
	
	/**
	 * Min value function for the alpha-beta pruning algorithm
	 * @param possBoard a possible board state to test
	 * @param alpha value of alpha in the alpha-beta pruning algorithm
	 * @param beta value of beta in the alpha-beta pruning algorithm
	 * @param searchDepth depth to continue searching
	 * @return utility of the move selected by the min player
	 */
	private int minValueAB(String[][] possBoard, int alpha, int beta, int searchDepth) {
		// If search depth has been reached returns utility value
		if (searchDepth == 0) return (getPossScore(possBoard, currentPlayer) - getPossScore(possBoard, otherPlayer));
		int min = boardSize * boardSize;
		// For each empty space
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (possBoard[i][j].equals("")) {
					// Create a new board state for each possible move
					String[][] testBoard = new String[boardSize][boardSize];
					for (int k = 0; k < boardSize; k++) {
						for (int l = 0; l < boardSize; l++) {
							testBoard[k][l] = possBoard[k][l];
						}
					}
					// Add move to the new board state and compare results from the max value function
					testBoard[i][j] = otherPlayer;
					int max = maxValueAB(testBoard, alpha, beta, searchDepth-1);
					min = (min > max) ? max : min;
					if (min <= alpha) return min;
					beta = (beta < min) ? beta : min;
				}
			}
		}
		// Return minimum utility value
		return min;
	}
	
	/**
	 * Performs a move for the computer player using the minimax algorithm 
	 */
	private void minimaxMove() {
		// Variables needed to update the board
		int x = -1;
		int y = -1;
		int max = 0 - (boardSize * boardSize);
		// Search depth increases as less spaces are available
		int searchDepth = (emptySpaces < 12) ? emptySpaces - 1 : 7;
		if (emptySpaces > 14) searchDepth = 6;
		if (emptySpaces > 18) searchDepth = 5;
		if (emptySpaces > 24) searchDepth = 4;
		if (emptySpaces > 40) searchDepth = 3;
		if (emptySpaces > 80) searchDepth = 2;
		// For each empty space
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (boardState[i][j].equals("")) {
					// Create a new board state for each possible move
					String[][] testBoard = new String[boardSize][boardSize];
					for (int k = 0; k < boardSize; k++) {
						for (int l = 0; l < boardSize; l++) {
							testBoard[k][l] = boardState[k][l];
						}
					}
					// Try each empty space
					testBoard[i][j] = currentPlayer;
					int min = minValue(testBoard, searchDepth);
					if (min > max || (min == max && Math.random() < 4 / emptySpaces)) {
						max = min;
						x = i;
						y = j;
					}
				}
			}
		}
		// Update the board and board state
		board[x][y].setText(currentPlayer);
		boardState[x][y] = currentPlayer;
	}
	
	/**
	 * Max value function for the minimax algorithm
	 * @param possBoard a possible board state to test
	 * @param searchDepth depth to continue searching
	 * @return utility of the move selected by the max player
	 */
	private int maxValue(String[][] possBoard, int searchDepth) {
		// If search depth has been reached return utility value
		if (searchDepth == 0) return (getPossScore(possBoard, currentPlayer) - getPossScore(possBoard, otherPlayer));
		int max = 0 - (boardSize * boardSize);
		// For each empty space
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (possBoard[i][j].equals("")) {
					// Create a new board state for each possible move
					String[][] testBoard = new String[boardSize][boardSize];
					for (int k = 0; k < boardSize; k++) {
						for (int l = 0; l < boardSize; l++) {
							testBoard[k][l] = possBoard[k][l];
						}
					}
					// Add move to the new board state and compare results from the min value function
					testBoard[i][j] = currentPlayer;
					int min = minValue(testBoard, searchDepth-1);
					max = (min > max) ? min : max;
				}
			}
		}
		// Return maximum utility value
		return max;
	}
	
	/**
	 * Min value function for the minimax algorithm
	 * @param possBoard a possible board state to test
	 * @param searchDepth depth to continue searching
	 * @return utility of the move selected by the min player
	 */
	private int minValue(String[][] possBoard, int searchDepth) {
		// If search depth has been reached return utility value
		if (searchDepth == 0) return (getPossScore(possBoard, currentPlayer) - getPossScore(possBoard, otherPlayer));
		int min = boardSize * boardSize;
		// For each empty space
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (possBoard[i][j].equals("")) {
					// Create a new board state for each possible move
					String[][] testBoard = new String[boardSize][boardSize];
					for (int k = 0; k < boardSize; k++) {
						for (int l = 0; l < boardSize; l++) {
							testBoard[k][l] = possBoard[k][l];
						}
					}
					// Add move to the new board state and compare results from the max value function
					testBoard[i][j] = otherPlayer;
					int max = maxValue(testBoard, searchDepth-1);
					min = (min > max) ? max : min;
				}
			}
		}
		// Return minimum utility value
		return min;
	}
	
	/**
	 * Updates the String array representing the current board state which is used by the algorithms to determine moves
	 */
	private void updateBoardState() {
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				boardState[i][j] = board[i][j].getText();
			}
		}
	}
	
	/**
	 * Switches the active player
	 */
	private void togglePlayer() {
		currentPlayer = currentPlayer.equals(playerOne) ? playerTwo : playerOne;
		otherPlayer = otherPlayer.equals(playerOne) ? playerTwo : playerOne;
		emptySpaces--;
	}
	
	/**
	 * Determines if the game has concluded, the board is either full or has one empty space
	 */
	private void gameOver() {
		// One empty space, determine if player will pass
		if (emptySpaces == 1) {
			int currentScore = getScore(currentPlayer);
			for (int i = 0; i < boardSize; i++) {
				for (int j = 0; j < boardSize; j++) {
					if (board[i][j].getText().equals("")) {
						board[i][j].setText(currentPlayer);
						if (currentScore > getScore(currentPlayer)) {
							emptySpaces = 0;
						}
						board[i][j].setText("");
						i = boardSize;
						j = boardSize;
					}
				}
			}
		}
		// Display final scores
		if (emptySpaces == 0) {
			gameOver =  true;
			JOptionPane.showMessageDialog(null, "Game Over\n" + playerOne + " Player Score: " + getScore(playerOne) + "\n" + playerTwo + " Player Score: " + getScore(playerTwo));
		}
	}
	
	/**
	 * Gets the possible score for a given player
	 * Counts the number of times the player can have exactly four spaces in a row including current blank spaces for a given board state
	 * @param possBoard Possible board state to evaluate
	 * @param player Player to get the possible score for
	 * @return Possible score of the input player
	 */
	private int getPossScore(String[][] possBoard, String player) {
		int possScore = 0;
		//Check horizontal and vertical
		for (int i = 0; i < boardSize - 3; i++) {
			for (int j = 0; j < boardSize; j++) {
				if ((possBoard[i][j].equals(player) || possBoard[i][j].equals(""))
						&& (possBoard[i+1][j].equals(player) || possBoard[i+1][j].equals(""))
						&& (possBoard[i+2][j].equals(player) || possBoard[i+2][j].equals(""))
						&& (possBoard[i+3][j].equals(player) || possBoard[i+3][j].equals(""))) {
					possScore++;
					if ((i > 0 && possBoard[i-1][j].equals(player)) 
							|| (i < boardSize - 4 && possBoard[i+4][j].equals(player))) possScore--;  
				}
			}
		}
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize - 3; j++) {
				if ((possBoard[i][j].equals(player) || possBoard[i][j].equals(""))
						&& (possBoard[i][j+1].equals(player) || possBoard[i][j+1].equals(""))
						&& (possBoard[i][j+2].equals(player) || possBoard[i][j+2].equals(""))
						&& (possBoard[i][j+3].equals(player) || possBoard[i][j+3].equals(""))) {
					possScore++;
					if ((j > 0 && possBoard[i][j-1].equals(player)) 
							|| (j < boardSize - 4 && possBoard[i][j+4].equals(player))) possScore--;
				}
			}
		}
		//Check diagonal
		for (int i = 0; i < boardSize - 3; i++) {
			for (int j = 0; j < boardSize - 3; j++) {
				if ((possBoard[i][j].equals(player) || possBoard[i][j].equals(""))
						&& (possBoard[i+1][j+1].equals(player) || possBoard[i+1][j+1].equals(""))
						&& (possBoard[i+2][j+2].equals(player) || possBoard[i+2][j+2].equals(""))
						&& (possBoard[i+3][j+3].equals(player) || possBoard[i+3][j+3].equals(""))) {
					possScore++;
					if ((i > 0 && j > 0 && possBoard[i-1][j-1].equals(player)) 
							|| (i < boardSize - 4 && j < boardSize - 4 && possBoard[i+4][j+4].equals(player))) possScore--;
				}
			}
		}
		for (int i = 3; i < boardSize; i++) {
			for (int j = 0; j < boardSize - 3; j++) {
				if ((possBoard[i][j].equals(player) || possBoard[i][j].equals(""))
						&& (possBoard[i-1][j+1].equals(player) || possBoard[i-1][j+1].equals(""))
						&& (possBoard[i-2][j+2].equals(player) || possBoard[i-2][j+2].equals(""))
						&& (possBoard[i-3][j+3].equals(player) || possBoard[i-3][j+3].equals(""))) {
					possScore++;
					if ((i < boardSize - 4 && j > 0 && possBoard[i+4][j-1].equals(player)) 
							|| (i > 0 && j < boardSize - 4 && possBoard[i-1][j+4].equals(player))) possScore--;
				}
			}
		}		
		return possScore;
	}
	
	/**
	 * Gets the score for a given player
	 * Counts the number of times the player has exactly four spaces in a row
	 * @param player Player to get the score for
	 * @return Score of the input player
	 */
	private int getScore(String player) {
		int score = 0;
		//Check horizontal and vertical
		for (int i = 0; i < boardSize - 3; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (board[i][j].getText().equals(player) && board[i+1][j].getText().equals(player) 
						&& board[i+2][j].getText().equals(player) && board[i+3][j].getText().equals(player)) {
					score++;
					if ((i > 0 && board[i-1][j].getText().equals(player)) 
							|| (i < boardSize - 4 && board[i+4][j].getText().equals(player))) score--;  
				}
			}
		}
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize - 3; j++) {
				if (board[i][j].getText().equals(player) && board[i][j+1].getText().equals(player) 
						&& board[i][j+2].getText().equals(player) && board[i][j+3].getText().equals(player)) {
					score++;
					if ((j > 0 && board[i][j-1].getText().equals(player)) 
							|| (j < boardSize - 4 && board[i][j+4].getText().equals(player))) score--;
				}
			}
		}
		//Check diagonal
		for (int i = 0; i < boardSize - 3; i++) {
			for (int j = 0; j < boardSize - 3; j++) {
				if (board[i][j].getText().equals(player) && board[i+1][j+1].getText().equals(player) 
						&& board[i+2][j+2].getText().equals(player) && board[i+3][j+3].getText().equals(player)) {
					score++;
					if ((i > 0 && j > 0 && board[i-1][j-1].getText().equals(player)) 
							|| (i < boardSize - 4 && j < boardSize - 4 && board[i+4][j+4].getText().equals(player))) score--;
				}
			}
		}
		for (int i = 3; i < boardSize; i++) {
			for (int j = 0; j < boardSize - 3; j++) {
				if (board[i][j].getText().equals(player) && board[i-1][j+1].getText().equals(player) 
						&& board[i-2][j+2].getText().equals(player) && board[i-3][j+3].getText().equals(player)) {
					score++;
					if ((i < boardSize - 4 && j > 0 && board[i+4][j-1].getText().equals(player)) 
							|| (i > 0 && j < boardSize - 4 && board[i-1][j+4].getText().equals(player))) score--;
				}
			}
		}
		return score;
	}
}