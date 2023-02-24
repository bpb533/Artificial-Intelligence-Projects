package network;

import java.io.File;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * This program compresses data for a neural network to identify handwritten digits
 * This program uses data from the UCI Machine Learning Database
 * https://archive.ics.uci.edu/ml/machine-learning-databases/optdigits/
 * @author Ben Bissantz
 */
public class Network {
	
	/**
	 * Declare global variables
	 */
	static String trainingFileName;
	static int trainingSize = 0;
	static int trainingWidth = 0;
	static int trainingHeight = 0;
	static int[] trainingSummary;
	static int[] trainingDigits;
	static boolean[][] trainingMaps;
	static String testingFileName;
	static int testingSize = 0;
	static int testingWidth = 0;
	static int testingHeight = 0;
	static int[] testingSummary;
	static int[] testingDigits;
	static boolean[][] testingMaps;
	static int columnRemoval = 0;
	static int rowRemoval = 0;
	static boolean compress = false;
	
	/**
	 * Main method, calls other methods to run the program
	 * @param args standard arguments
	 */
	public static void main(String[] args) {
		prepareFiles();
		printSummary();
		for (int i = 0; i < columnRemoval; i++) removeColumn();
		for (int i = 0; i < rowRemoval; i++) removeRow();
		if (compress) compressMaps();
		printSummary();
		printOutputFiles();
		printTargetFiles();
	}
	
	/**
	 * Prompts user for input files, navigates the files and reads data from the input files
	 */
	static void prepareFiles() {
		Scanner userInput = new Scanner(System.in);
		Scanner trainingInput = null;
		Scanner testingInput = null;
		File trainingFile;
		File testingFile;
		
		/**
		 * Prompt user for a training file
		 */
		while (trainingFileName == null) {
			System.out.println("Please select a training file. Enter digits as follows for the corresponding file.");
			System.out.println("These files have been downloaded from https://archive.ics.uci.edu/ml/machine-learning-databases/optdigits/");
			System.out.println("1 - optdigits-orig.tra.txt    - Training           - 1934 Digits");
			System.out.println("2 - optdigits-orig.cv.txt     - Validation         - 946 Digits");
			System.out.println("3 - optdigits-orig.wdep.txt   - Writer-dependent   - 943 Digits");
			System.out.println("4 - optdigits-orig.windep.txt - Writer-independent - 1797 Digits");
			int fileChoice = userInput.nextInt();
			if (fileChoice < 1 || fileChoice > 4) {
				System.out.println("Invalid selection, optdigits-orig.tra.txt chosen by default.");
				fileChoice = 1;
			}
			switch (fileChoice) {
				case 1:  trainingFileName = "optdigits-orig.tra.txt";
					break;
				case 2:  trainingFileName = "optdigits-orig.cv.txt";
		        	break;
				case 3:  trainingFileName = "optdigits-orig.wdep.txt";
	        		break;
				case 4:  trainingFileName = "optdigits-orig.windep.txt";
        			break;
			}
			try {
				trainingFile = new File(trainingFileName);
				trainingInput = new Scanner(trainingFile);
			} catch (Exception e) {
				System.out.println("Invalid file name.");
				trainingFileName = null;
			}
		}
		
		/**
		 * Navigate to the first digit 
		 */
		String nextInput = null;
		// Get width
		while (trainingWidth == 0) {
			nextInput = trainingInput.next();
			if (nextInput.equals("entwidth")) {
				nextInput = trainingInput.next();
				trainingWidth = trainingInput.nextInt();
			}
		}
		// Get height
		while (trainingHeight == 0) {
			nextInput = trainingInput.next();
			if (nextInput.equals("entheight")) {
				nextInput = trainingInput.next();
				trainingHeight = trainingInput.nextInt();
			}
		}
		// Get number of digits
		while (trainingSize == 0) {
			nextInput = trainingInput.next();
			if (nextInput.equals("ntot")) {
				nextInput = trainingInput.next();
				trainingSize = trainingInput.nextInt();
			}
		}
		// Get first digit
		nextInput = null;
		while (nextInput == null) {
			nextInput = trainingInput.nextLine();
			if (nextInput.length() < trainingWidth) nextInput = null;
		}
		
		/**
		 * Read inputs
		 */
		trainingDigits = new int[trainingSize];
		trainingMaps = new boolean[trainingSize][];
		for (int i = 0; i < trainingSize; i++) {
			trainingMaps[i] = new boolean[trainingWidth * trainingHeight];
			for (int j = 0; j < trainingHeight; j++) {
				for (int k = 0; k < trainingWidth; k++) {
					trainingMaps[i][j * trainingWidth + k] = nextInput.charAt(k) == '1';
				}
				nextInput = trainingInput.nextLine();
			}
			// Save digit number
			trainingDigits[i] = nextInput.charAt(1) - '0';
			if (i < trainingSize - 1) nextInput = trainingInput.nextLine();
		}

		/**
		 * Prompt user for a testing file
		 */
		while (testingFileName == null) {
			System.out.println("Please select a testing file. Enter digits as follows for the corresponding file.");
			System.out.println("These files have been downloaded from https://archive.ics.uci.edu/ml/machine-learning-databases/optdigits/");
			System.out.println("1 - optdigits-orig.tra.txt    - Training           - 1934 Digits");
			System.out.println("2 - optdigits-orig.cv.txt     - Validation         - 946 Digits");
			System.out.println("3 - optdigits-orig.wdep.txt   - Writer-dependent   - 943 Digits");
			System.out.println("4 - optdigits-orig.windep.txt - Writer-independent - 1797 Digits");
			int fileChoice = userInput.nextInt();
			if (fileChoice < 1 || fileChoice > 4) {
				System.out.println("Invalid selection, optdigits-orig.windep.txt chosen by default.");
				fileChoice = 4;
			}
			switch (fileChoice) {
				case 1:  testingFileName = "optdigits-orig.tra.txt";
					break;
				case 2:  testingFileName = "optdigits-orig.cv.txt";
		        	break;
				case 3:  testingFileName = "optdigits-orig.wdep.txt";
	        		break;
				case 4:  testingFileName = "optdigits-orig.windep.txt";
        			break;
			}
			try {
				testingFile = new File(testingFileName);
				testingInput = new Scanner(testingFile);
			} catch (Exception e) {
				System.out.println("Invalid file name.");
				testingFileName = null;
			}
		}
		
		/**
		 * Navigate to the first digit 
		 */
		nextInput = null;
		// Get width
		while (testingWidth == 0) {
			nextInput = testingInput.next();
			if (nextInput.equals("entwidth")) {
				nextInput = testingInput.next();
				testingWidth = testingInput.nextInt();
			}
		}
		// Get height
		while (testingHeight == 0) {
			nextInput = testingInput.next();
			if (nextInput.equals("entheight")) {
				nextInput = testingInput.next();
				testingHeight = testingInput.nextInt();
			}
		}
		// Get number of digits
		while (testingSize == 0) {
			nextInput = testingInput.next();
			if (nextInput.equals("ntot")) {
				nextInput = testingInput.next();
				testingSize = testingInput.nextInt();
			}
		}
		// Get first digit
		nextInput = null;
		while (nextInput == null) {
			nextInput = testingInput.nextLine();
			if (nextInput.length() < testingWidth) nextInput = null;
		}
		
		/**
		 * Read inputs
		 */
		testingDigits = new int[testingSize];
		testingMaps = new boolean[testingSize][];
		for (int i = 0; i < testingSize; i++) {
			testingMaps[i] = new boolean[testingWidth * testingHeight];
			for (int j = 0; j < testingHeight; j++) {
				for (int k = 0; k < testingWidth; k++) {
					testingMaps[i][j * testingWidth + k] = nextInput.charAt(k) == '1';
				}
				nextInput = testingInput.nextLine();
			}
			// Save digit number
			testingDigits[i] = nextInput.charAt(1) - '0';
			if (i < testingSize - 1) nextInput = testingInput.nextLine();
		}
		
		/**
		 * Set compression options
		 */
		// Column removal
		System.out.println("Please enter a number of columns to remove.");
		System.out.println("This will remove columns from the left and right of each digit containing the smallest portions of the digit.");
		System.out.println("8 to 12 is the recommended range for the number of columns to remove.");
		columnRemoval = userInput.nextInt();
		if (columnRemoval < 0 || columnRemoval > trainingWidth) {
			System.out.println("Invalid selection, 8 columns chosen by default.");
			columnRemoval = 8;
		}
		// Row removal
		System.out.println("Please enter a number of rows to remove.");
		System.out.println("This will remove rows from the top and bottom of each digit containing the smallest portions of the digit.");
		System.out.println("2 to 6 is the recommended range for the number of columns to remove.");
		rowRemoval = userInput.nextInt();
		if (rowRemoval < 0 || rowRemoval > trainingHeight) {
			System.out.println("Invalid selection, 2 rows chosen by default.");
			rowRemoval = 2;
		}
		// Compression
		System.out.println("Please select an option for compression.");
		System.out.println("This will reduce the size of the digits to 1/4 of their original size.");
		System.out.println("Compression is applied after removing rows and columns.");
		System.out.println("0 - Do Not Enable Compression");
		System.out.println("1 - Enable Compression");
		compress = userInput.nextInt() == 1;
		
		trainingInput.close();
		testingInput.close();
		userInput.close();
	}
	
	/**
	 * Prints an array displaying the total number of digits that use a specific portion of the input area
	 * Also prints two sample digits from the dataset
	 */
	static void printSummary() {
		trainingSummary = new int[trainingWidth * trainingHeight];
		for (int i = 0; i < trainingWidth * trainingHeight; i++) trainingSummary[i] = 0;
		for (int i = 0; i < trainingSize; i++) {
			for (int j = 0; j < trainingHeight; j++) {
				for (int k = 0; k < trainingWidth; k++) {
					if (trainingMaps[i][j * trainingWidth + k]) trainingSummary[j * trainingWidth + k]++;
				}
			}
		}
		
		System.out.print("\nTraining Summary\n");
		System.out.print("Index");
		for (int i = 0; i < trainingWidth; i++) System.out.print(String.format("%1$5s",(i + 1)));
		for (int i = 0; i < trainingHeight; i++) {
			System.out.print(String.format("\n%1$5s",(i + 1)));
			for (int j = 0; j < trainingWidth; j++) {
				System.out.print(String.format("%1$5s", trainingSummary[i * trainingWidth + j]));
			}
		}
		printTrainingDigit(0);
		printTrainingDigit(10);
		
		testingSummary = new int[testingWidth * testingHeight];
		for (int i = 0; i < testingWidth * testingHeight; i++) testingSummary[i] = 0;
		for (int i = 0; i < testingSize; i++) {
			for (int j = 0; j < testingHeight; j++) {
				for (int k = 0; k < testingWidth; k++) {
					if (testingMaps[i][j * testingWidth + k]) testingSummary[j * testingWidth + k]++;
				}
			}
		}
		
		System.out.print("\nTesting Summary\n");
		System.out.print("Index");
		for (int i = 0; i < testingWidth; i++) System.out.print(String.format("%1$5s",(i + 1)));
		for (int i = 0; i < testingHeight; i++) {
			System.out.print(String.format("\n%1$5s",(i + 1)));
			for (int j = 0; j < testingWidth; j++) {
				System.out.print(String.format("%1$5s", testingSummary[i * testingWidth + j]));
			}
		}
		printTestingDigit(0);
		printTestingDigit(10);
	}
	
	/**
	 * Print a digit from the training dataset
	 * @param digit index of the digit to print
	 */
	static void printTrainingDigit(int digit) {
		digit = (digit > trainingSize || digit < 0) ? 0 : digit;
		System.out.print("\n\nTraining Digit " + digit + ": " + trainingDigits[digit] + " " + trainingWidth + " x " + trainingHeight + "\n");
		for (int i = 0; i < trainingHeight; i++) {
			for (int j = 0; j < trainingWidth; j++) {
				System.out.print((trainingMaps[digit][i * trainingWidth + j]) ? 1:0);
			}
			System.out.print("\n");
		}
	}
	
	/**
	 * Print a digit from the testing dataset
	 * @param digit index of the digit to print
	 */
	static void printTestingDigit(int digit) {
		digit = (digit > testingSize || digit < 0) ? 0 : digit;
		System.out.print("\n\nTesting Digit " + digit + ": " + testingDigits[digit] + " " + testingWidth + " x " + testingHeight + "\n");
		for (int i = 0; i < testingHeight; i++) {
			for (int j = 0; j < testingWidth; j++) {
				System.out.print((testingMaps[digit][i * testingWidth + j]) ? 1:0);
			}
			System.out.print("\n");
		}
	}
	
	/**
	 * Removes either the top or bottom row from each digit
	 * Removes the row that includes the least amount of the digit
	 */
	static void removeRow() {
		boolean[][] newMaps = new boolean[trainingSize][];
		for (int i = 0; i < trainingSize; i++) {
			newMaps[i] = new boolean[trainingWidth * (trainingHeight - 1)];
			int top = 0;
			int bottom = 0;
			for (int j = 0; j < trainingWidth; j++) {
				if (trainingMaps[i][j]) top++;
				if (trainingMaps[i][(trainingHeight - 1) * trainingWidth + j]) bottom++;
			}
			bottom = (bottom < top) ? 0:1;
			for (int j = 0; j < trainingHeight - 1; j++) {
				for (int k = 0; k < trainingWidth; k++) {
					newMaps[i][j * trainingWidth + k] = trainingMaps[i][(j + bottom) * trainingWidth + k];
				}
			}
		}
		trainingMaps = newMaps;
		trainingHeight--;
		
		newMaps = new boolean[testingSize][];
		for (int i = 0; i < testingSize; i++) {
			newMaps[i] = new boolean[testingWidth * (testingHeight - 1)];
			int top = 0;
			int bottom = 0;
			for (int j = 0; j < testingWidth; j++) {
				if (testingMaps[i][j]) top++;
				if (testingMaps[i][(testingHeight - 1) * testingWidth + j]) bottom++;
			}
			bottom = (bottom < top) ? 0:1;
			for (int j = 0; j < testingHeight - 1; j++) {
				for (int k = 0; k < testingWidth; k++) {
					newMaps[i][j * testingWidth + k] = testingMaps[i][(j + bottom) * testingWidth + k];
				}
			}
		}
		testingMaps = newMaps;
		testingHeight--;
	}
	
	/**
	 * Removes either the left or right column from each digit
	 * Removes the column that includes the least amount of the digit
	 */
	static void removeColumn() {
		boolean[][] newMaps = new boolean[trainingSize][];
		for (int i = 0; i < trainingSize; i++) {
			newMaps[i] = new boolean[(trainingWidth - 1) * trainingHeight];
			int left = 0;
			int right = 0;
			for (int j = 0; j < trainingHeight; j++) {
				if (trainingMaps[i][j * trainingWidth]) left++;
				if (trainingMaps[i][(j + 1) * trainingWidth - 1]) right++;
			}
			left = (right < left) ? 0:1;
			for (int j = 0; j < trainingHeight; j++) {
				for (int k = 0; k < trainingWidth - 1; k++) {
					newMaps[i][j * (trainingWidth - 1) + k] = trainingMaps[i][j * trainingWidth + k + left];
				}
			}
		}
		trainingMaps = newMaps;
		trainingWidth--;
		
		newMaps = new boolean[testingSize][];
		for (int i = 0; i < testingSize; i++) {
			newMaps[i] = new boolean[(testingWidth - 1) * testingHeight];
			int left = 0;
			int right = 0;
			for (int j = 0; j < testingHeight; j++) {
				if (testingMaps[i][j * testingWidth]) left++;
				if (testingMaps[i][(j + 1) * testingWidth - 1]) right++;
			}
			left = (right < left) ? 0:1;
			for (int j = 0; j < testingHeight; j++) {
				for (int k = 0; k < testingWidth - 1; k++) {
					newMaps[i][j * (testingWidth - 1) + k] = testingMaps[i][j * testingWidth + k + left];
				}
			}
		}
		testingMaps = newMaps;
		testingWidth--;
	}
	
	/**
	 * Reduces the size of each digit by one fourth
	 */
	static void compressMaps() {
		boolean[][] newMaps = new boolean[trainingSize][];
		for (int i = 0; i < trainingSize; i++) {
			newMaps[i] = new boolean[trainingWidth / 2 * trainingHeight / 2];
			for (int j = 0; j < trainingHeight / 2; j++) {
				for (int k = 0; k < trainingWidth / 2; k++) {
					newMaps[i][j * (trainingWidth / 2) + k] = 
							trainingMaps[i][j * 2 * trainingWidth + k * 2] && trainingMaps[i][j * 2 * trainingWidth + k * 2 + 1] ||
							trainingMaps[i][j * 2 * trainingWidth + k * 2] && trainingMaps[i][(j * 2 + 1) * trainingWidth + k * 2] ||
							trainingMaps[i][(j * 2 + 1) * trainingWidth + k * 2 + 1] && trainingMaps[i][j * 2 * trainingWidth + k * 2 + 1] ||
							trainingMaps[i][(j * 2 + 1) * trainingWidth + k * 2 + 1] && trainingMaps[i][(j * 2 + 1) * trainingWidth + k * 2];
				}
			}
		}
		trainingMaps = newMaps;
		trainingWidth = trainingWidth/2;
		trainingHeight = trainingHeight/2;
		
		newMaps = new boolean[testingSize][];
		for (int i = 0; i < testingSize; i++) {
			newMaps[i] = new boolean[testingWidth / 2 * testingHeight / 2];
			for (int j = 0; j < testingHeight / 2; j++) {
				for (int k = 0; k < testingWidth / 2; k++) {
					newMaps[i][j * (testingWidth / 2) + k] = 
							testingMaps[i][j * 2 * testingWidth + k * 2] && testingMaps[i][j * 2 * testingWidth + k * 2 + 1] ||
							testingMaps[i][j * 2 * testingWidth + k * 2] && testingMaps[i][(j * 2 + 1) * testingWidth + k * 2] ||
							testingMaps[i][(j * 2 + 1) * testingWidth + k * 2 + 1] && testingMaps[i][j * 2 * testingWidth + k * 2 + 1] ||
							testingMaps[i][(j * 2 + 1) * testingWidth + k * 2 + 1] && testingMaps[i][(j * 2 + 1) * testingWidth + k * 2];
				}
			}
		}
		testingMaps = newMaps;
		testingWidth = testingWidth/2;
		testingHeight = testingHeight/2;
	}
	
	/**
	 * Prints the testing and training data to an new text file.
	 */
	static void printOutputFiles() {
		
		File trainingOutputFile;
		PrintStream trainingOutput = null;
		String trainingOutputName = trainingFileName + "-" + trainingWidth + "x" + trainingHeight + ".txt";
		try {
			trainingOutputFile = new File(trainingOutputName);
			trainingOutput = new PrintStream(trainingOutputFile);
		}
		catch (Exception e) {
			System.out.println("Output file " + trainingOutputName + " not created.");
		}

		for (int i = 0; i < trainingSize; i++) {
			//trainingOutput.print(trainingDigits[i] + "\n");
			for (int j = 0; j < trainingHeight; j++) {
				for (int k = 0; k < trainingWidth; k++) {
					trainingOutput.print((trainingMaps[i][j * trainingWidth + k]) ? "1 ":"0 ");
				}
				//trainingOutput.print("\n");
			}
			trainingOutput.print("\n");
		}
		
		File testingOutputFile;
		PrintStream testingOutput = null;
		String testingOutputName = testingFileName + "-" + testingWidth + "x" + testingHeight + ".txt";
		try {
			testingOutputFile = new File(testingOutputName);
			testingOutput = new PrintStream(testingOutputFile);
		}
		catch (Exception e) {
			System.out.println("Output file " + testingOutputName + " not created.");
		}

		for (int i = 0; i < testingSize; i++) {
			//testingOutput.print(testingDigits[i] + "\n");
			for (int j = 0; j < testingHeight; j++) {
				for (int k = 0; k < testingWidth; k++) {
					testingOutput.print((testingMaps[i][j * testingWidth + k]) ? "1 ":"0 ");
				}
				//testingOutput.print("\n");
			}
			testingOutput.print("\n");
		}
	}
	
	/**
	 * Prints the testing and training targets to an new text file.
	 */
	static void printTargetFiles() {
		
		File trainingOutputFile;
		PrintStream trainingOutput = null;
		String trainingOutputName = trainingFileName + "-targets.txt";
		try {
			trainingOutputFile = new File(trainingOutputName);
			trainingOutput = new PrintStream(trainingOutputFile);
		}
		catch (Exception e) {
			System.out.println("Output file " + trainingOutputName + " not created.");
		}

		for (int i = 0; i < trainingSize; i++) {
			for (int j = 1; j < 11; j++) trainingOutput.print((j == trainingDigits[i] || j == 10 && trainingDigits[i] == 0) ? "1 ":"0 ");
			trainingOutput.print("\n");
		}
		
		File testingOutputFile;
		PrintStream testingOutput = null;
		String testingOutputName = testingFileName + "-targets.txt";
		try {
			testingOutputFile = new File(testingOutputName);
			testingOutput = new PrintStream(testingOutputFile);
		}
		catch (Exception e) {
			System.out.println("Output file " + testingOutputName + " not created.");
		}

		for (int i = 0; i < testingSize; i++) {
			for (int j = 1; j < 11; j++) testingOutput.print((j == testingDigits[i] || j == 10 && testingDigits[i] == 0) ? "1 ":"0 ");
			testingOutput.print("\n");
		}
	}
}