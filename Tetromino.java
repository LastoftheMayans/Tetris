package tetris;

/**
 * Models the tetromino that falls down the board. The tetromino exists as a set of
 * coordinates that flick lights on and off on the board.
 * 
 * @author dmayans
 */

import java.awt.Color;

public class Tetromino {
	private Board _board;
	private int _type;
	
	private int[] _currentX;
	private int[] _currentY;
	private int[] _futureX;
	private int[] _futureY;
	
	private Color _color;
	private double[] _center;
	private Shadow _shadow;
	
	public Tetromino(Board board, int typeInt) {
		_board = board;
		_type = typeInt;
		
		// Sets up current position
		int[][] type = Constants.TETROMINOES[typeInt];
		_currentX = new int[4];
		_currentY = new int[4];
		_currentX[3] = 4;
		_currentY[3] = 20;
		for(int i=0; i<3; i++) {
			_currentX[i] = 3+type[0][i];
			_currentY[i] = 20+type[1][i];
		}
		
		_futureX = new int[4];
		_futureY = new int[4];
		
		// Sets up its center, color and current shadow
		_center = new double[2];
		_center[0] = 4 + type[3][0]/2.0;	_center[1] = 20 + type[3][1]/2.0;
		_color = new Color(type[2][0],type[2][1],type[2][2]);
		_shadow = new Shadow(_currentX, _currentY, _board);
	}
	
	// Accessors
	public int getType() {
		return _type;
	}
	
	public int min() {
		// Returns the lowest Y coordinate
		int min = 20;
			for(int i=0; i<4; i++) {
				min = Math.min(min,  _currentY[i]);
			}
		return min;
	}
	
	public boolean move(int[] typeOfMotion) {
		this.hide(); // Hides the tetromino to prevent interference
		if(typeOfMotion==Constants.ROTATE || typeOfMotion==Constants.NROTATE) {
			// Special code for rotation. NROTATE stands for non-recursive rotation, used
			// exclusively during wall-kicks to prevent infinite loops. Functionally
			// idential to ROTATE.
			for(int i=0; i<4; i++) {
				_futureX[i] = (int) (_center[0] - _center[1] + _currentY[i]);
				_futureY[i] = (int) (_center[1] + _center[0] - _currentX[i]);
			}
		} else {
			// Left, right, and down can all be handled by the same code
			for(int i=0; i<4; i++) {
				_futureX[i] = _currentX[i]+typeOfMotion[0];
				_futureY[i] = _currentY[i]+typeOfMotion[1];
			}
		}
		boolean flag = true; // Is this move valid?
		boolean leftkick = true; // Am I allowed to kick right?
		boolean rightkick = true; // Am I allowed to kick left?
		for(int i=0; i<4; i++) {
			flag = flag && !_board.isOccupied(_futureX[i],_futureY[i]);
			// Kick triggers handled by the board
			leftkick = leftkick && _board.leftKick(_futureX[i],_futureY[i],_center);
			rightkick = rightkick && _board.rightKick(_futureX[i],_futureY[i],_center);
		}
		if(flag) {
			// If the move is valid, execute the move and update the shadow
			for(int i=0; i<4; i++) {
				_currentX[i] = _futureX[i];
				_currentY[i] = _futureY[i];
			}
			_center[0] += typeOfMotion[0];
			_center[1] += typeOfMotion[1];
			_shadow = new Shadow(_currentX, _currentY, _board);
		} else if(leftkick && typeOfMotion==Constants.ROTATE) {
			// Handles left wall kicks
			this.move(Constants.RIGHT);
			this.move(Constants.NROTATE);
		} else if(rightkick && typeOfMotion==Constants.ROTATE) {
			// Handles right wall kicks
			this.move(Constants.LEFT);
			this.move(Constants.NROTATE);
		}
		this.show(); // Display the teromino once movement is complete. Board is NOT repainted
		return flag; // in between hide and show, so the piece never graphically disappears from the screen
	}
	
	// Short method to hide the tetromino and its shadow
	public void hide() {
		_shadow.hide();
		for(int i=0; i<4; i++) {
			_board.darkenBlock(_currentX[i], _currentY[i]);
		}
	}
	
	// Short method to show the tetromino and its shadow
	public void show() {
		_shadow.show();
		for(int i=0; i<4; i++) {
			_board.paintBlock(_currentX[i], _currentY[i], _color);
		}
	}
	
}
