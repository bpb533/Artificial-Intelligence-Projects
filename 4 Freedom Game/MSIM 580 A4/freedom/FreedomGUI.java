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
	private char currentPlayer;
	private char otherPlayer;
	private char playerOne = '\u26AA';
	private char playerTwo = '\u26AB';
	private boolean computerOne = false;
	private boolean computerTwo = true;
	private boolean alphaBetaAlgorithm = true;
	private boolean evalScore = true;
	private boolean evalThree = true;
	private boolean evalAllPoss = true;
	private int difficulty = 0;
	private int boardSize = 10;
	private int boardArea = 100;
	private int emptySpaces;
	private JButton[][] board;
	private boolean[][] boardState = new boolean[2][];
	private boolean gameOver;

	/**
	 * Declaration of menu objects
	 */
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem newHvH;
	private JMenuItem newHvC;
	private JMenuItem newCvC;
	private JMenuItem about;
	private JMenuItem quit;
	private JMenu optionsMenu;
	private JMenuItem minimax;
	private JMenuItem alphaBeta;
	private JMenuItem sizeSix;
	private JMenuItem sizeEight;
	private JMenuItem sizeTen;
	private JMenuItem difficultyEasy;
	private JMenuItem difficultyNormal;
	private JMenuItem difficultyGenius;
	private JMenuItem evaluationScore;
	private JMenuItem evaluationThree;
	private JMenuItem evaluationAllPoss;
	private JMenuItem evaluationAll;
	private JMenuItem evaluationNone;
	
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
		
		// About option
		about = new JMenuItem("About");
		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, 
						"Freedom is a two-player abstract strategy board game invented by Veljko Cirovic and Nebojsa Sankovic in 2010. It is played with black and\n"
						+ "white stones on a square board. The game is related to Go-Moku and Slimetrail. It can be played with a Go set or with pen and paper.\n"
						+ "Rules:\n"
						+ "Board: Freedom is played on a 10×10 square board. Beginners can try the game on a 8×8 board. Other board sizes may be also used.\n"
						+ "Objective: The objective of Freedom is to have more \"live\" stones at the end of the game, than your opponent. A stone is considered\n"
						+ "to be \"live\" if it is a part of some horizontal, vertical or diagonal row of exactly 4 stones of the same color.\n"
						+ "Play: A game begins with an empty board. Each player has an allocated color: White and Black. White plays first, putting one white stone\n"
						+ "anywhere on the board. After this move players take turns placing their stones on empty cells adjacent to the last opponent's stone.\n"
						+ "If all cells adjacent to the last opponent's stone are occupied then the player gets the right (\"freedom\") to place his stone on any\n"
						+ "empty cell of the board. The game ends when the board is filled with stones.\n"
						+ "The last player has the right to pass on his last turn (and leave the last cell empty) if placing his stone reduces his score.\n"
						+ "Rules from https://boardgamegeek.com/boardgame/100480/freedom");
			}
		});
		fileMenu.add(about);
		
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
			}
		});
		optionsMenu.add(minimax);
		// Options for computer algorithm alpha-beta		
		alphaBeta = new JMenuItem("Computer Algorithm Alpha-Beta");
		alphaBeta.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				alphaBetaAlgorithm = true;
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
		
		// Options for easy difficulty
		difficultyEasy = new JMenuItem("Difficulty Easy");
		difficultyEasy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				difficulty = -1;
			}
		});
		optionsMenu.add(difficultyEasy);
		// Options for normal difficulty
		difficultyNormal = new JMenuItem("Difficulty Normal");
		difficultyNormal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				difficulty = 0;
			}
		});
		optionsMenu.add(difficultyNormal);
		// Options for genius difficulty
		difficultyGenius = new JMenuItem("Difficulty Genius");
		difficultyGenius.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				difficulty = 1;
			}
		});
		optionsMenu.add(difficultyGenius);
		
		// Options for score evaluation
		evaluationScore = new JMenuItem("Evaluation Function Toggle Score");
		evaluationScore.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				evalScore = !evalScore;
			}
		});
		optionsMenu.add(evaluationScore);
		// Options for three in a row evaluation
		evaluationThree = new JMenuItem("Evaluation Function Toggle 3 in a Row");
		evaluationThree.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				evalThree = !evalThree;
			}
		});
		optionsMenu.add(evaluationThree);
		// Options for all possible evaluation
		evaluationAllPoss = new JMenuItem("Evaluation Function Toggle Poss 4 in a Row");
		evaluationAllPoss.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				evalAllPoss = !evalAllPoss;
			}
		});
		optionsMenu.add(evaluationAllPoss);
		// Options for all evaluation criteria
		evaluationAll = new JMenuItem("Evaluation Function Use All Criteria");
		evaluationAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				evalScore = true;
				evalThree = true;
				evalAllPoss = true;
			}
		});
		optionsMenu.add(evaluationAll);
		// Options for no evaluation criteria
		evaluationNone = new JMenuItem("Evaluation Function Use No Criteria");
		evaluationNone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				evalScore = false;
				evalThree = false;
				evalAllPoss = false;
			}
		});
		optionsMenu.add(evaluationNone);
		
		
		
		
		
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
		boardArea = boardSize * boardSize;
		emptySpaces = boardArea;
		board = new JButton[boardSize][boardSize];
		boardState[0] = new boolean[boardArea];
		boardState[1] = new boolean[boardArea];
		// Add buttons to the board
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				JButton btn = new JButton();
				btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 40));
				btn.setText(" ");
				board[i][j] = btn;
				btn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (((JButton)e.getSource()).getText().equals(" ") && !gameOver) {
							btn.setText(String.valueOf(currentPlayer));
							updateBoardState();
							togglePlayer();
							gameOver();
							if (computerTwo && !gameOver) {
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
	 * Updates the boolean array representing the current board state which is used by the algorithms to determine moves
	 */
	private void updateBoardState() {
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (board[i][j].getText().charAt(0) != ' ')	boardState[board[i][j].getText().charAt(0) - playerOne][i * boardSize + j] = true;
			}
		}
	}
	
	/**
	 * Switches the active player
	 */
	private void togglePlayer() {
		currentPlayer = currentPlayer == playerOne ? playerTwo : playerOne;
		otherPlayer = otherPlayer == playerOne ? playerTwo : playerOne;
		emptySpaces--;
	}
	
	/**
	 * If there is a computer player calls the selected algorithm
	 */
	private void computerMove() {
		if (currentPlayer == playerOne && computerOne == true || currentPlayer == playerTwo && computerTwo == true) {
			if (!evalScore && !evalThree && !evalAllPoss) randomMove(); else if (alphaBetaAlgorithm) alphaBetaMove(); else minimaxMove();
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
			if (board[i][j].getText().equals(" ")) {
				board[i][j].setText(String.valueOf(currentPlayer));
				boardState[currentPlayer - playerOne][i * boardSize + j] = true;
				played = true;
				System.out.print("Player " + (1 + currentPlayer - playerOne) + " - " + emptySpaces + " Spaces - Random Move: " + i + "," + j + " - Time: 0s\n");
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
		int max = -2000000000;
		long startTime = System.currentTimeMillis();
		// Search depth increases as less spaces are available
		int searchDepth = (emptySpaces < 12) ? emptySpaces - 1 : 7;
		if (emptySpaces > 20) searchDepth = 6;
		if (emptySpaces > 30) searchDepth = 5;
		if (emptySpaces > 50) searchDepth = 4;
		if (emptySpaces > 80) searchDepth = 3;
		searchDepth = searchDepth + difficulty;
		// For each empty space
		System.out.print("Player " + (1 + currentPlayer - playerOne) + " - " + emptySpaces + " Spaces - Calculating AB Move: ");
		for (int i = 0; i < boardArea; i++) {
			if (!boardState[0][i] && !boardState[1][i]) {
				// Create a new board state for each possible move
				boolean[][] possBoard = new boolean[2][];
				possBoard[0] = boardState[0].clone();
				possBoard[1] = boardState[1].clone();
				possBoard[currentPlayer - playerOne][i] = true;
				// Try each empty space
				int min = minValueAB(possBoard, -2000000000, 2000000000, searchDepth);
				if (min > max || (min == max && Math.random() < 0.5)) {
					max = min;
					x = i / boardSize;
					y = i % boardSize;
				}
			}
			if (i % boardSize == 0) System.out.print((100 * (i) / boardArea) + "% ");
		}
		// Update the board and board state
		System.out.print("100% - Move: " + x + "," + y + " - Time: " + (System.currentTimeMillis() - startTime) / 1000 + "s\n");
		board[x][y].setText(String.valueOf(currentPlayer));
		boardState[currentPlayer - playerOne][x * boardSize + y] = true;
	}
	
	/**
	 * Max value function for the alpha-beta pruning algorithm
	 * @param possBoard a possible board state to test
	 * @param alpha value of alpha in the alpha-beta pruning algorithm
	 * @param beta value of beta in the alpha-beta pruning algorithm
	 * @param searchDepth depth to continue searching
	 * @return utility of the move selected by the max player
	 */
	private int maxValueAB(boolean[][] possBoard, int alpha, int beta, int searchDepth) {
		// If search depth has been reached return utility value
		searchDepth--;
		if (searchDepth <= 0) return (getUtility(possBoard, currentPlayer) - getUtility(possBoard, otherPlayer));
		int max = -2000000000;
		// For each empty space
		for (int i = 0; i < boardArea; i++) {
			if (!possBoard[0][i] && !possBoard[1][i]) {
				// Add move to the possible board state and get min value function results then remove the move 
				possBoard[currentPlayer - playerOne][i] = true; 
				int min = minValueAB(possBoard, alpha, beta, searchDepth);
				possBoard[currentPlayer - playerOne][i] = false;
				// Compare results from the min value function
				max = (min > max) ? min : max;
				if (max >= beta) return max;
				alpha = (alpha > max) ? alpha : max;
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
	private int minValueAB(boolean[][] possBoard, int alpha, int beta, int searchDepth) {
		// If search depth has been reached returns utility value
		searchDepth--;
		if (searchDepth <= 0) return (getUtility(possBoard, currentPlayer) - getUtility(possBoard, otherPlayer));
		int min = 2000000000;
		// For each empty space
		for (int i = 0; i < boardArea; i++) {
			if (!possBoard[0][i] && !possBoard[1][i]) {
				// Add move to the possible board state and get max value function results then remove the move
				possBoard[otherPlayer - playerOne][i] = true;
				int max = maxValueAB(possBoard, alpha, beta, searchDepth);
				possBoard[otherPlayer - playerOne][i] = false;
				// Compare results from the max value function
				min = (min > max) ? max : min;
				if (min <= alpha) return min;
				beta = (beta < min) ? beta : min;
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
		int max = -2000000000;
		long startTime = System.currentTimeMillis();
		// Search depth increases as less spaces are available
		int searchDepth = (emptySpaces < 12) ? emptySpaces - 1 : 7;
		if (emptySpaces > 20) searchDepth = 6;
		if (emptySpaces > 30) searchDepth = 5;
		if (emptySpaces > 50) searchDepth = 4;
		if (emptySpaces > 80) searchDepth = 3;
		searchDepth = searchDepth + difficulty - 1;
		// For each empty space
		System.out.print("Player " + (1 + currentPlayer - playerOne) + " - " + emptySpaces + " Spaces - Calculating MM Move: ");
		for (int i = 0; i < boardArea; i++) {
			if (!boardState[0][i] && !boardState[1][i]) {
				// Create a new board state for each possible move
				boolean[][] possBoard = new boolean[2][];
				possBoard[0] = boardState[0].clone();
				possBoard[1] = boardState[1].clone();
				possBoard[currentPlayer - playerOne][i] = true;
				// Try each empty space
				int min = minValue(possBoard, searchDepth);
				if (min > max || (min == max && Math.random() < 0.5)) {
					max = min;
					x = i / boardSize;
					y = i % boardSize;
				}
			}
			if (i % boardSize == 0) System.out.print((100 * (i) / boardArea) + "% ");
		}
		// Update the board and board state
		System.out.print("100% - Move: " + x + "," + y + " - Time: " + (System.currentTimeMillis() - startTime) / 1000 + "s\n");
		board[x][y].setText(String.valueOf(currentPlayer));
		boardState[currentPlayer - playerOne][x * boardSize + y] = true;
	}
	
	/**
	 * Max value function for the minimax algorithm
	 * @param possBoard a possible board state to test
	 * @param searchDepth depth to continue searching
	 * @return utility of the move selected by the max player
	 */
	private int maxValue(boolean[][] possBoard, int searchDepth) {
		// If search depth has been reached return utility value
		searchDepth--;
		if (searchDepth <= 0) return (getUtility(possBoard, currentPlayer) - getUtility(possBoard, otherPlayer));
		int max = -2000000000;
		// For each empty space
		for (int i = 0; i < boardArea; i++) {
			if (!possBoard[0][i] && !possBoard[1][i]) {
				// Add move to the possible board state and get max value function results then remove the move
				possBoard[currentPlayer - playerOne][i] = true;
				int min = minValue(possBoard, searchDepth);
				possBoard[currentPlayer - playerOne][i] = false;
				// Compare results from the min value function
				max = (min > max) ? min : max;
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
	private int minValue(boolean[][] possBoard, int searchDepth) {
		// If search depth has been reached return utility value
		searchDepth--;
		if (searchDepth <= 0) return (getUtility(possBoard, currentPlayer) - getUtility(possBoard, otherPlayer));
		int min = 2000000000;
		// For each empty space
		for (int i = 0; i < boardArea; i++) {
			if (!possBoard[0][i] && !possBoard[1][i]) {
				// Add move to the possible board state and get max value function results then remove the move
				possBoard[otherPlayer - playerOne][i] = true;
				int max = maxValue(possBoard, searchDepth);
				possBoard[otherPlayer - playerOne][i] = false;
				// Compare results from the max value function
				min = (min > max) ? max : min;
			}
		}
		// Return minimum utility value
		return min;
	}
		
	/**
	 * Determines if the game has concluded, the board is either full or has one empty space
	 */
	private void gameOver() {
		// One empty space, determine if player will pass
		if (emptySpaces == 1) {
			int currentScore = getScore(boardState, currentPlayer);
			for (int i = 0; i < boardSize; i++) {
				for (int j = 0; j < boardSize; j++) {
					if (board[i][j].getText().equals(" ")) {
						board[i][j].setText(String.valueOf(currentPlayer));
						updateBoardState();
						if (currentScore > getScore(boardState, currentPlayer)) {
							board[i][j].setText(" ");
							updateBoardState();
						}
						emptySpaces = 0;
						i = boardSize;
						j = boardSize;
					}
				}
			}
		}
		// Update and display final scores
		if (emptySpaces == 0) {
			gameOver =  true;
			JOptionPane.showMessageDialog(null, "Game Over\n" + playerOne + " Player Score: " + getScore(boardState, playerOne) + "\n" + playerTwo + " Player Score: " + getScore(boardState, playerTwo));
		}
	}
	
	/**
	 * Gets the score for a given board and player
	 * Counts the number of times the player has exactly four spaces in a row
	 * @param gameBoard board state to evaluate
	 * @param player Player to get the score for
	 * @return Score of the input player
	 */
	private int getScore(boolean[][] gameBoard, char player) {
		int score = 0;
		int p = player - playerOne;
		// Check horizontal and vertical
		for (int i = 0; i < boardSize - 3; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (gameBoard[p][i * boardSize + j] && gameBoard[p][(i + 1) * boardSize + j] 
						&& gameBoard[p][(i + 2) * boardSize + j] && gameBoard[p][(i + 3) * boardSize + j]) {
					score++;
					if ((i > 0 && gameBoard[p][(i - 1) * boardSize + j]) || (i < boardSize - 4 && gameBoard[p][(i + 4) * boardSize + j])) score--;
				}
			}
		}
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize - 3; j++) {
				if (gameBoard[p][i * boardSize + j] && gameBoard[p][i * boardSize + j + 1] 
						&& gameBoard[p][i * boardSize + j + 2] && gameBoard[p][i * boardSize + j + 3]) {
					score++;
					if ((j > 0 && gameBoard[p][i * boardSize + j - 1]) || (j < boardSize - 4 && gameBoard[p][i * boardSize + j + 4])) score--;
				}
			}
		}
		// Check diagonal
		for (int i = 0; i < boardSize - 3; i++) {
			for (int j = 0; j < boardSize - 3; j++) {
				if (gameBoard[p][i * boardSize + j] && gameBoard[p][(i + 1) * boardSize + j + 1] 
						&& gameBoard[p][(i + 2) * boardSize + j + 2] && gameBoard[p][(i + 3) * boardSize + j + 3]) {
					score++;
					if ((i > 0 && j > 0 && gameBoard[p][(i - 1) * boardSize + j - 1]) || (i < boardSize - 4 && j < boardSize - 4 && gameBoard[p][(i + 4) * boardSize + j + 4])) score--;
				}
			}
		}
		for (int i = 3; i < boardSize; i++) {
			for (int j = 0; j < boardSize - 3; j++) {
				if (gameBoard[p][i * boardSize + j] && gameBoard[p][(i - 1) * boardSize + j + 1] 
						&& gameBoard[p][(i - 2) * boardSize + j + 2] && gameBoard[p][(i - 3) * boardSize + j + 3]) {
					score++;
					if ((i < boardSize - 1 && j > 0 && gameBoard[p][(i + 1) * boardSize + j - 1]) || (i > 3 && j < boardSize - 4 && gameBoard[p][(i - 4) * boardSize + j + 4])) score--;
				}
			}
		}		
		return score;
	}

	/**
	 * Gets the utility for a given possible board state and player
	 * Counts the number of times the player can have exactly four spaces in a row including current blank spaces for a given board state
	 * @param possBoard Possible board state to evaluate
	 * @param player Player to get the possible score for
	 * @return Possible score of the input player
	 */
	private int getUtility(boolean[][] possBoard, char player) {
		int utility = 0;
		if (evalScore) utility = 2 * getScore(possBoard, player);
		int p = player - playerOne;
		int o = playerTwo - player;
		// Check for open-ended instances of three in a row
		if (evalThree) {
			// Check horizontal
			for (int i = 0; i < boardSize - 4; i++) {
				for (int j = 0; j < boardSize; j++) {
					if (!possBoard[p][i * boardSize + j] && !possBoard[o][i * boardSize + j] 
							&& possBoard[p][(i + 1) * boardSize + j] && possBoard[p][(i + 2) * boardSize + j] && possBoard[p][(i + 3) * boardSize + j] 
							&& !possBoard[p][(i + 4) * boardSize + j] && !possBoard[o][(i + 4) * boardSize + j]) utility++;
				}
			}
			for (int i = 0; i < boardSize; i++) {
				for (int j = 0; j < boardSize - 4; j++) {
					if (!possBoard[p][i * boardSize + j] && !possBoard[o][i * boardSize + j] 
							&& possBoard[p][i * boardSize + j + 1] && possBoard[p][i * boardSize + j + 2] && possBoard[p][i * boardSize + j + 3] 
							&& !possBoard[p][i * boardSize + j + 4] && !possBoard[o][i * boardSize + j + 4]) utility++;
				}
			}
			// Check diagonal
			for (int i = 0; i < boardSize - 4; i++) {
				for (int j = 0; j < boardSize - 4; j++) {
					if (!possBoard[p][i * boardSize + j] && !possBoard[o][i * boardSize + j]
							&& possBoard[p][(i + 1) * boardSize + j + 1] && possBoard[p][(i + 2) * boardSize + j + 2] && possBoard[p][(i + 3) * boardSize + j + 3] 
							&& !possBoard[p][(i + 4) * boardSize + j + 4] && !possBoard[o][(i + 4) * boardSize + j + 4]) utility++;
				}
			}
			for (int i = 4; i < boardSize; i++) {
				for (int j = 0; j < boardSize - 4; j++) {
					if (!possBoard[p][i * boardSize + j] && !possBoard[o][i * boardSize + j] 
							&& possBoard[p][(i - 1) * boardSize + j + 1] && possBoard[p][(i - 2) * boardSize + j + 2] && possBoard[p][(i - 3) * boardSize + j + 3]
							&& !possBoard[p][(i - 4) * boardSize + j + 4] && !possBoard[o][(i - 4) * boardSize + j + 4]) utility++;
				}
			}
		}
		utility = 2 * utility;
		// Check for all possible scores
		if (evalAllPoss) {
			// Check horizontal
			for (int i = 0; i < boardSize - 3; i++) {
				for (int j = 0; j < boardSize; j++) {
					if (!possBoard[o][i * boardSize + j] && !possBoard[o][(i + 1) * boardSize + j]
							&& !possBoard[o][(i + 2) * boardSize + j] && !possBoard[o][(i + 3) * boardSize + j]) {
						utility++;
						if ((i > 0 && possBoard[p][(i - 1) * boardSize + j]) || (i < boardSize - 4 && possBoard[p][(i + 4) * boardSize + j])) utility--;
					}
				}
			}
			for (int i = 0; i < boardSize; i++) {
				for (int j = 0; j < boardSize - 3; j++) {
					if (!possBoard[o][i * boardSize + j] && !possBoard[o][i * boardSize + j + 1]
							&& !possBoard[o][i * boardSize + j + 2] && !possBoard[o][i * boardSize + j + 3]) {
						utility++;
						if ((j > 0 && possBoard[p][i * boardSize + j - 1]) || (j < boardSize - 4 && possBoard[p][i * boardSize + j + 4])) utility--;
					}
				}
			}
			// Check diagonal
			for (int i = 0; i < boardSize - 3; i++) {
				for (int j = 0; j < boardSize - 3; j++) {
					if (!possBoard[o][i * boardSize + j] && !possBoard[o][(i + 1) * boardSize + j + 1] 
							&& !possBoard[o][(i + 2) * boardSize + j + 2] && !possBoard[o][(i + 3) * boardSize + j + 3]) {
						utility++;
						if ((i > 0 && j > 0 && possBoard[p][(i - 1) * boardSize + j - 1]) || (i < boardSize - 4 && j < boardSize - 4 && possBoard[p][(i + 4) * boardSize + j + 4])) utility--;
					}
				}
			}
			for (int i = 3; i < boardSize; i++) {
				for (int j = 0; j < boardSize - 3; j++) {
					if (!possBoard[o][i * boardSize + j] && !possBoard[o][(i - 1) * boardSize + j + 1]
							&& !possBoard[o][(i - 2) * boardSize + j + 2] && !possBoard[o][(i - 3) * boardSize + j + 3]) {
						utility++;
						if ((i < boardSize - 1 && j > 0 && possBoard[p][(i + 1) * boardSize + j - 1]) || (i > 3 && j < boardSize - 4 && possBoard[p][(i - 4) * boardSize + j + 4])) utility--;
					}
				}
			}
		}
		return utility;
	}
}