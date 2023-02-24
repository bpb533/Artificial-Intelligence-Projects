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
	private char playerOne = '\u26AB';
	private char playerTwo = '\u26AA';
	private int playerOneScore;
	private int playerTwoScore;
	private boolean computerOne = false;
	private boolean computerTwo = true;
	private boolean alphaBetaAlgorithm = true;
	private int boardSize = 10;
	private int boardArea = 100;
	private int emptySpaces;
	private JButton[][] board;
	private String boardState;
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
		boardArea = boardSize * boardSize;
		emptySpaces = boardArea;
		board = new JButton[boardSize][boardSize];
		boardState = new String();
		// Add buttons to the board
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				JButton btn = new JButton();
				btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 40));
				btn.setText(" ");
				board[i][j] = btn;
				boardState = boardState + " ";
				btn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (((JButton)e.getSource()).getText().equals(" ") && !gameOver) {
							btn.setText(String.valueOf(currentPlayer));
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
		if (currentPlayer == playerOne && computerOne == true || currentPlayer == playerTwo && computerTwo == true) {
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
			if (board[i][j].getText().equals(" ")) {
				board[i][j].setText(String.valueOf(currentPlayer));
				boardState = boardState.substring(0, i * boardSize + j) + currentPlayer + boardState.substring(i * boardSize + j + 1);
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
		int max = 0 - boardArea;
		long startTime = System.currentTimeMillis();
		// Search depth increases as less spaces are available
		int searchDepth = (emptySpaces < 12) ? emptySpaces - 1 : 8;
		if (emptySpaces > 19) searchDepth = 7;
		if (emptySpaces > 31) searchDepth = 6;
		if (emptySpaces > 54) searchDepth = 5;
		// For each empty space
		System.out.print("Calculating Move " + emptySpaces + " Spaces: ");
		for (int i = 0; i < boardArea; i++) {
			if (boardState.charAt(i) == ' ') {
				// Create a new board state for each possible move
				String testBoard = new String();
				testBoard = boardState.substring(0, i) + currentPlayer + boardState.substring(i + 1);
				// Try each empty space
				int min = minValueAB(testBoard, 0 - boardArea, boardArea, searchDepth);
				if (min > max || (min == max && Math.random() < 4 / emptySpaces)) {
					max = min;
					x = i / boardSize;
					y = i % boardSize;
				}
			}
			System.out.print((100 * (i) / boardArea) + "% ");
		}
		// Update the board and board state
		System.out.print("Move Time: " + (System.currentTimeMillis() - startTime) / 1000 + "s\n");
		board[x][y].setText(String.valueOf(currentPlayer));
		boardState = boardState.substring(0, x * boardSize + y) + currentPlayer + boardState.substring(x * boardSize + y + 1);
	}
	
	/**
	 * Max value function for the alpha-beta pruning algorithm
	 * @param possBoard a possible board state to test
	 * @param alpha value of alpha in the alpha-beta pruning algorithm
	 * @param beta value of beta in the alpha-beta pruning algorithm
	 * @param searchDepth depth to continue searching
	 * @return utility of the move selected by the max player
	 */
	private int maxValueAB(String possBoard, int alpha, int beta, int searchDepth) {
		// If search depth has been reached return utility value
		if (searchDepth == 0) return (getUtility(possBoard, currentPlayer) - getUtility(possBoard, otherPlayer));
		int max = 0 - boardArea;
		// For each empty space
		for (int i = 0; i < boardArea; i++) {
			if (possBoard.charAt(i) == ' ') {
				// Create a new board state for each possible move
				String testBoard = new String();
				testBoard = possBoard.substring(0, i) + currentPlayer + possBoard.substring(i + 1);
				// Add move to the new board state and compare results from the min value function 
				int min = minValueAB(testBoard, alpha, beta, searchDepth-1);
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
	private int minValueAB(String possBoard, int alpha, int beta, int searchDepth) {
		// If search depth has been reached returns utility value
		if (searchDepth == 0) return (getUtility(possBoard, currentPlayer) - getUtility(possBoard, otherPlayer));
		int min = boardArea;
		// For each empty space
		for (int i = 0; i < boardArea; i++) {
			if (possBoard.charAt(i) == ' ') {
				// Create a new board state for each possible move
				String testBoard = new String();
				testBoard = possBoard.substring(0, i) + currentPlayer + possBoard.substring(i + 1);
				// Add move to the new board state and compare results from the max value function
				int max = maxValueAB(testBoard, alpha, beta, searchDepth-1);
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
		int max = 0 - boardArea;
		long startTime = System.currentTimeMillis();
		// Search depth increases as less spaces are available
		int searchDepth = (emptySpaces < 12) ? emptySpaces - 1 : 7;
		if (emptySpaces > 20) searchDepth = 6;
		if (emptySpaces > 24) searchDepth = 5;
		if (emptySpaces > 30) searchDepth = 4;
		if (emptySpaces > 40) searchDepth = 3;
		if (emptySpaces > 80) searchDepth = 2;
		// For each empty space
		System.out.print("Calculating Move " + emptySpaces + " Spaces: ");
		for (int i = 0; i < boardArea; i++) {
			if (boardState.charAt(i) == ' ') {
				// Create a new board state for each possible move
				String testBoard = new String();
				testBoard = boardState.substring(0, i) + currentPlayer + boardState.substring(i + 1);
				// Try each empty space
				int min = minValue(testBoard, searchDepth);
				if (min > max || (min == max && Math.random() < 4 / emptySpaces)) {
					max = min;
					x = i / boardSize;
					y = i % boardSize;
				}
			}
			System.out.print((100 * (i) / boardArea) + "% ");
		}
		// Update the board and board state
		System.out.print("Move Time: " + (System.currentTimeMillis() - startTime) / 1000 + "s\n");
		board[x][y].setText(String.valueOf(currentPlayer));
		boardState = boardState.substring(0, x * boardSize + y) + currentPlayer + boardState.substring(x * boardSize + y + 1);
	}
	
	/**
	 * Max value function for the minimax algorithm
	 * @param possBoard a possible board state to test
	 * @param searchDepth depth to continue searching
	 * @return utility of the move selected by the max player
	 */
	private int maxValue(String possBoard, int searchDepth) {
		// If search depth has been reached return utility value
		if (searchDepth == 0) return (getUtility(possBoard, currentPlayer) - getUtility(possBoard, otherPlayer));
		int max = 0 - boardArea;
		// For each empty space
		for (int i = 0; i < boardArea; i++) {
			if (possBoard.charAt(i) == ' ') {
				// Create a new board state for each possible move
				String testBoard = new String();
				testBoard = possBoard.substring(0, i) + currentPlayer + possBoard.substring(i + 1);
				// Add move to the new board state and compare results from the min value function
				int min = minValue(testBoard, searchDepth-1);
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
	private int minValue(String possBoard, int searchDepth) {
		// If search depth has been reached return utility value
		if (searchDepth == 0) return (getUtility(possBoard, currentPlayer) - getUtility(possBoard, otherPlayer));
		int min = boardArea;
		// For each empty space
		for (int i = 0; i < boardArea; i++) {
			if (possBoard.charAt(i) == ' ') {
				// Create a new board state for each possible move
				String testBoard = new String();
				testBoard = possBoard.substring(0, i) + currentPlayer + possBoard.substring(i + 1);
				// Add move to the new board state and compare results from the max value function
				int max = maxValue(testBoard, searchDepth-1);
				min = (min > max) ? max : min;
			}
		}
		// Return minimum utility value
		return min;
	}
	
	/**
	 * Updates the String array representing the current board state which is used by the algorithms to determine moves
	 */
	private void updateBoardState() {
		boardState = "";
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				boardState = boardState + board[i][j].getText().charAt(0);
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
							emptySpaces = 0;
						}
						board[i][j].setText(" ");
						updateBoardState();
						i = boardSize;
						j = boardSize;
					}
				}
			}
		}
		// Update and display final scores
		playerOneScore = getScore(boardState, playerOne);
		playerTwoScore = getScore(boardState, playerTwo);
		if (emptySpaces == 0) {
			gameOver =  true;
			JOptionPane.showMessageDialog(null, "Game Over\n" + playerOne + " Player Score: " + playerOneScore + "\n" + playerTwo + " Player Score: " + playerTwoScore);
		}
	}
	
	/**
	 * Gets the utility for a given possible board state and player
	 * Counts the number of times the player can have exactly four spaces in a row including current blank spaces for a given board state
	 * @param possBoard Possible board state to evaluate
	 * @param player Player to get the possible score for
	 * @return Possible score of the input player
	 */
	private int getUtility(String possBoard, char player) {
		int utility = 2 * getScore(possBoard, player);
		// Check for open-ended instances of three in a row
		// Check horizontal
		for (int i = 0; i < boardSize - 4; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (possBoard.charAt(i * boardSize + j) == ' ' && possBoard.charAt((i + 1) * boardSize + j) == player
						&& possBoard.charAt((i + 2) * boardSize + j) == player && possBoard.charAt((i + 3) * boardSize + j) == player 
						&& possBoard.charAt((i + 4) * boardSize + j) == ' ') utility++;
			}
		}
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize - 4; j++) {
				if (possBoard.charAt(i * boardSize + j) == ' ' && possBoard.charAt(i * boardSize + j + 1) == player 
						&& possBoard.charAt(i * boardSize + j + 2) == player && possBoard.charAt(i * boardSize + j + 3) == player 
						&& possBoard.charAt(i * boardSize + j + 4) == ' ') utility++;
			}
		}
		// Check diagonal
		for (int i = 0; i < boardSize - 4; i++) {
			for (int j = 0; j < boardSize - 4; j++) {
				if (possBoard.charAt(i * boardSize + j) == ' ' && possBoard.charAt((i + 1) * boardSize + j + 1) == player 
						&& possBoard.charAt((i + 2) * boardSize + j + 2) == player && possBoard.charAt((i + 3) * boardSize + j + 3) == player 
						&& possBoard.charAt((i + 3) * boardSize + j + 3) == ' ') utility++;
			}
		}
		for (int i = 4; i < boardSize; i++) {
			for (int j = 0; j < boardSize - 4; j++) {
				if (possBoard.charAt(i * boardSize + j) == ' ' && possBoard.charAt((i - 1) * boardSize + j + 1) == player
						&& possBoard.charAt((i - 2) * boardSize + j + 2) == player && possBoard.charAt((i - 3) * boardSize + j + 3) == player
						&& possBoard.charAt((i - 4) * boardSize + j + 4) == ' ') utility++;
			}
		}
		utility = 2 * utility;
		// Check for all possible scores
		// Check horizontal
		for (int i = 0; i < boardSize - 3; i++) {
			for (int j = 0; j < boardSize; j++) {
				if ((possBoard.charAt(i * boardSize + j) == player || possBoard.charAt(i * boardSize + j) == ' ')
						&& (possBoard.charAt((i + 1) * boardSize + j) == player || possBoard.charAt((i + 1) * boardSize + j) == ' ')
						&& (possBoard.charAt((i + 2) * boardSize + j) == player || possBoard.charAt((i + 2) * boardSize + j) == ' ')
						&& (possBoard.charAt((i + 3) * boardSize + j) == player || possBoard.charAt((i + 3) * boardSize + j) == ' ')) {
					utility++;
					if ((i > 0 && possBoard.charAt((i - 1) * boardSize + j) == player) 
							|| (i < boardSize - 4 && possBoard.charAt((i + 4) * boardSize + j) == player)) utility--;
				}
			}
		}
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize - 3; j++) {
				if ((possBoard.charAt(i * boardSize + j) == player || possBoard.charAt(i * boardSize + j) == ' ')
						&& (possBoard.charAt(i * boardSize + j + 1) == player || possBoard.charAt(i * boardSize + j + 1) == ' ')
						&& (possBoard.charAt(i * boardSize + j + 2) == player || possBoard.charAt(i * boardSize + j + 2) == ' ')
						&& (possBoard.charAt(i * boardSize + j + 3) == player || possBoard.charAt(i * boardSize + j + 3) == ' ')) {
					utility++;
					if ((j > 0 && possBoard.charAt(i * boardSize + j - 1) == player) 
							|| (j < boardSize - 4 && possBoard.charAt(i * boardSize + j + 4) == player)) utility--;
				}
			}
		}
		// Check diagonal
		for (int i = 0; i < boardSize - 3; i++) {
			for (int j = 0; j < boardSize - 3; j++) {
				if ((possBoard.charAt(i * boardSize + j) == player || possBoard.charAt(i * boardSize + j) == ' ')
						&& (possBoard.charAt((i + 1) * boardSize + j + 1) == player || possBoard.charAt((i + 1) * boardSize + j + 1) == ' ')
						&& (possBoard.charAt((i + 2) * boardSize + j + 2) == player || possBoard.charAt((i + 2) * boardSize + j + 2) == ' ')
						&& (possBoard.charAt((i + 3) * boardSize + j + 3) == player || possBoard.charAt((i + 3) * boardSize + j + 3) == ' ')) {
					utility++;
					if ((i > 0 && j > 0 && possBoard.charAt((i - 1) * boardSize + j - 1) == player) 
							|| (i < boardSize - 4 && j < boardSize - 4 && possBoard.charAt((i + 4) * boardSize + j + 4) == player)) utility--;
				}
			}
		}
		for (int i = 3; i < boardSize; i++) {
			for (int j = 0; j < boardSize - 3; j++) {
				if ((possBoard.charAt(i * boardSize + j) == player || possBoard.charAt(i * boardSize + j) == ' ')
						&& (possBoard.charAt((i - 1) * boardSize + j + 1) == player || possBoard.charAt((i - 1) * boardSize + j + 1) == ' ')
						&& (possBoard.charAt((i - 2) * boardSize + j + 2) == player || possBoard.charAt((i - 2) * boardSize + j + 2) == ' ')
						&& (possBoard.charAt((i - 3) * boardSize + j + 3) == player || possBoard.charAt((i - 3) * boardSize + j + 3) == ' ')) {
					utility++;
					if ((i < boardSize - 1 && j > 0 && possBoard.charAt((i + 1) * boardSize + j - 1) == player) 
							|| (i > 3 && j < boardSize - 4 && possBoard.charAt((i - 4) * boardSize + j + 4) == player)) utility--;
				}
			}
		}		
		return utility;
	}
	
	/**
	 * Gets the score for a given board and player
	 * Counts the number of times the player has exactly four spaces in a row
	 * @param gameBoard board state to evaluate
	 * @param player Player to get the score for
	 * @return Score of the input player
	 */
	private int getScore(String gameBoard, char player) {
		int score = 0;
		// Check horizontal and vertical
		for (int i = 0; i < boardSize - 3; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (gameBoard.charAt(i * boardSize + j) == player && gameBoard.charAt((i + 1) * boardSize + j) == player 
						&& gameBoard.charAt((i + 2) * boardSize + j) == player && gameBoard.charAt((i + 3) * boardSize + j) == player) {
					score++;
					if ((i > 0 && gameBoard.charAt((i - 1) * boardSize + j) == player) || (i < boardSize - 4 && gameBoard.charAt((i + 4) * boardSize + j) == player)) score--;
				}
			}
		}
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize - 3; j++) {
				if (gameBoard.charAt(i * boardSize + j) == player && gameBoard.charAt(i * boardSize + j + 1) == player 
						&& gameBoard.charAt(i * boardSize + j + 2) == player && gameBoard.charAt(i * boardSize + j + 3) == player) {
					score++;
					if ((j > 0 && gameBoard.charAt(i * boardSize + j - 1) == player) || (j < boardSize - 4 && gameBoard.charAt(i * boardSize + j + 4) == player)) score--;
				}
			}
		}
		// Check diagonal
		for (int i = 0; i < boardSize - 3; i++) {
			for (int j = 0; j < boardSize - 3; j++) {
				if (gameBoard.charAt(i * boardSize + j) == player && gameBoard.charAt((i + 1) * boardSize + j + 1) == player 
						&& gameBoard.charAt((i + 2) * boardSize + j + 2) == player && gameBoard.charAt((i + 3) * boardSize + j + 3) == player) {
					score++;
					if ((i > 0 && j > 0 && gameBoard.charAt((i - 1) * boardSize + j - 1) == player)	|| (i < boardSize - 4 && j < boardSize - 4 && gameBoard.charAt((i + 4) * boardSize + j + 4) == player)) score--;
				}
			}
		}
		for (int i = 3; i < boardSize; i++) {
			for (int j = 0; j < boardSize - 3; j++) {
				if (gameBoard.charAt(i * boardSize + j) == player && gameBoard.charAt((i - 1) * boardSize + j + 1) == player 
						&& gameBoard.charAt((i - 2) * boardSize + j + 2) == player && gameBoard.charAt((i - 3) * boardSize + j + 3) == player) {
					score++;
					if ((i < boardSize - 1 && j > 0 && gameBoard.charAt((i + 1) * boardSize + j - 1) == player)	|| (i > 3 && j < boardSize - 4 && gameBoard.charAt((i - 4) * boardSize + j + 4) == player)) score--;
				}
			}
		}		
		return score;
	}
}