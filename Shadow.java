package tetris;

/**
 * Models the shadow projected on the board by a tetromino. Also used to calculate the
 * score.
 * 
 * @author dmayans
 */

import java.awt.Color;

public class Shadow {

	private Board _board;
	private int[] _currentX;
	private int[] _currentY;
	
	public Shadow(int[] x, int[] y, Board board) {
		_board = board;
		_currentX = new int[4];
		_currentY = new int[4];
		// Sets up current position
		for(int i=0; i<4; i++) {
			_currentX[i] = x[i];
			_currentY[i] = y[i];
		}
		while(this.moveDown()) {} // Sends the shadow as far down as possible
		for(int i=0; i<4; i++) {
			_currentY[i]++; // The shadow will fall one tile too far since it doesn't
			// use a separate array for future points. This corrects for that.
		}
	}
	
	// Similar, but significantly simplified, version of the Tetromino's move method.
	public boolean moveDown() {
		boolean flag = true;
		for(int i=0; i<4; i++) {
			_currentY[i]--;
			flag = flag && !_board.isOccupied(_currentX[i], _currentY[i]);
		}
		return flag;
	}
	
	// Called by its container to show the shadow
	public void show() {
		for(int i=0; i<4; i++) {
			_board.paintBlock(_currentX[i], _currentY[i], Color.GRAY);
		}
	}
	
	// Called by its container to hide the shadow
	public void hide() {
		for(int i=0; i<4; i++) {
			_board.darkenBlock(_currentX[i], _currentY[i]);
		}
	}

}
