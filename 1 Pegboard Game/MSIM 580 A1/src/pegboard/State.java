package pegboard;

/**
 * The State class represents the current state of a pegboard
 * The pegs are represented by an array of bytes
 * The State class has methods to validate peg movements and to move pegs right, left, up and down
 * Additionally the State class has methods to move pegs diagonally
 * Peg movements should only be called after validation to prevent out of bounds movements
 * 
 * @author Ben Bissantz
 * 
 */
public class State {
	
	/**
	 * Declare variables for the State class
	 * byte is used to conserve memory and improve performance
	 */
	byte height;
	byte width;
	byte pegs;
	byte[] board;
	long nodes;
	State parent;

	/**
	 * Create a child to the current State
	 * This method handles operations common to all peg movements
	 * The child inherits properties from the current state
	 * @return child state
	 */
	public State createChild() {
		State child = new State();
		child.height = height;
		child.width = width;
		child.pegs = pegs;
		child.nodes = nodes;
		child.board = new byte[height * width];
		for (int i = 0; i < height * width; i++) {
			child.board[i] = this.board[i];
		}
		child.parent = this;
		child.pegs--;
		return child;
	}
	
	/**
	 * Determine if it is possible to move a peg at index i to the right
	 * @param i index of peg to move
	 * @return true if move is possible
	 */
	public boolean lookRight(int i) {
		return (i % width <= width - 3 && board[i] == 1 && board[i + 1] == 1 && board[i + 2] == 0) ? true : false;
	}
	
	/**
	 * Move a peg at index i to the right into an empty space
	 * Calls create child to generate a new state and updates positions
	 * @param i index of peg to move
	 * @return new state with updated positions
	 */
	public State moveRight(int i) {
		State child = createChild();
		child.board[i] = 0;
		child.board[i+1] = 0;
		child.board[i+2] = 1;
		return child;
	}

	/**
	 * Determine if it is possible to move a peg at index i to the left
	 * @param i index of peg to move
	 * @return true if move is possible
	 */
	public boolean lookLeft(int i) {
		return (i % width >= 2 && board[i] == 1 && board[i - 1] == 1 && board[i - 2] == 0) ? true : false;
	}
	
	/**
	 * Move a peg at index i to the left into an empty space
	 * Calls create child to generate a new state and updates positions
	 * @param i index of peg to move
	 * @return new state with updated positions
	 */
	public State moveLeft(int i) {
		State child = createChild();
		child.board[i] = 0;
		child.board[i-1] = 0;
		child.board[i-2] = 1;
		return child;
	}

	/**
	 * Determine if it is possible to move a peg at index i up
	 * @param i index of peg to move
	 * @return true if move is possible
	 */
	public boolean lookUp(int i) {
		return (i / width >= 2 && board[i] == 1 && board[i - width] == 1 && board[i - 2 * width] == 0) ? true : false;
	}
	
	/**
	 * Move a peg at index i up into an empty space
	 * Calls create child to generate a new state and updates positions
	 * @param i index of peg to move
	 * @return new state with updated positions
	 */
	public State moveUp(int i) {
		State child = createChild();
		child.board[i] = 0;
		child.board[i-width] = 0;
		child.board[i-2*width] = 1;
		return child;
	}

	/**
	 * Determine if it is possible to move a peg at index i down
	 * @param i index of peg to move
	 * @return true if move is possible
	 */
	public boolean lookDown(int i) {
		return (i / width <= height - 3 && board[i] == 1 && board[i + width] == 1 && board[i + 2 * width] == 0) ? true : false;
	}
	
	/**
	 * Move a peg at index i down into an empty space
	 * Calls create child to generate a new state and updates positions
	 * @param i index of peg to move
	 * @return new state with updated positions
	 */
	public State moveDown(int i) {
		State child = createChild();
		child.board[i] = 0;
		child.board[i+width] = 0;
		child.board[i+2*width] = 1;
		return child;
	}
	
	/**
	 * Determine if it is possible to move a peg at index i diagonally up and to the right
	 * @param i index of peg to move
	 * @return true if move is possible
	 */
	public boolean lookUpRight(int i) {
		return (i / width >= 2 && i % width <= width - 3 && board[i] == 1 && board[i - width + 1] == 1 && board[i - 2*width + 2] == 0) ? true : false;
	}
	
	/**
	 * Move a peg at index i diagonally up and to the right into an empty space
	 * Calls create child to generate a new state and updates positions
	 * @param i index of peg to move
	 * @return new state with updated positions
	 */
	public State moveUpRight(int i) {
		State child = createChild();
		child.board[i] = 0;
		child.board[i-width+1] = 0;
		child.board[i-2*width+2] = 1;
		return child;
	}
	
	/**
	 * Determine if it is possible to move a peg at index i diagonally up and to the left
	 * @param i index of peg to move
	 * @return true if move is possible
	 */
	public boolean lookUpLeft(int i) {
		return (i / width >= 2 && i % i % width >= 2 && board[i] == 1 && board[i - width - 1] == 1 && board[i - 2*width - 2] == 0) ? true : false;
	}
	
	/**
	 * Move a peg at index i diagonally up and to the left into an empty space
	 * Calls create child to generate a new state and updates positions
	 * @param i index of peg to move
	 * @return new state with updated positions
	 */
	public State moveUpLeft(int i) {
		State child = createChild();
		child.board[i] = 0;
		child.board[i-width-1] = 0;
		child.board[i-2*width-2] = 1;
		return child;
	}
	
	/**
	 * Determine if it is possible to move a peg at index i diagonally down and to the right
	 * @param i index of peg to move
	 * @return true if move is possible
	 */
	public boolean lookDownRight(int i) {
		return (i / width <= height - 3 && i % width <= width - 3 && board[i] == 1 && board[i + width + 1] == 1 && board[i + 2*width + 2] == 0) ? true : false;
	}
	
	/**
	 * Move a peg at index i diagonally down and to the right into an empty space
	 * Calls create child to generate a new state and updates positions
	 * @param i index of peg to move
	 * @return new state with updated positions
	 */
	public State moveDownRight(int i) {
		State child = createChild();
		child.board[i] = 0;
		child.board[i+width+1] = 0;
		child.board[i+2*width+2] = 1;
		return child;
	}
	
	/**
	 * Determine if it is possible to move a peg at index i diagonally down and to the left
	 * @param i index of peg to move
	 * @return true if move is possible
	 */
	public boolean lookDownLeft(int i) {
		return (i / width <= height - 3 && i % width >= 2 && board[i] == 1 && board[i + width - 1] == 1 && board[i + 2*width - 2] == 0) ? true : false;
	}
	
	/**
	 * Move a peg at index i diagonally down and to the left into an empty space
	 * Calls create child to generate a new state and updates positions
	 * @param i index of peg to move
	 * @return new state with updated positions
	 */
	public State moveDownLeft(int i) {
		State child = createChild();
		child.board[i] = 0;
		child.board[i+width-1] = 0;
		child.board[i+2*width-2] = 1;
		return child;
	}
}