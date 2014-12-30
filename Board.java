package tetris;

/**
 * Board class handles the game board, accessors and mutators for the data within, a
 * scoring algorithm for the AI, and logic for clearing lines.
 * 
 * @author dmayans
 */

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Board {
	
	private ArrayList<RectangleShape[]> _board;
	
	public Board() {
		_board = new ArrayList<RectangleShape[]>();
		for(int i=0; i<22; i++) {
			_board.add(new RectangleShape[10]);
			for(int j=0; j<10; j++) {
				_board.get(i)[j] = new RectangleShape();
				this.setRectangle(_board.get(i)[j]);
			}
		}
		this.setLocations(0);
	}
	
	// Accessors
	public boolean isOccupied(int x, int y) {
		if(x<0 || x>9 || y<0 || y>21) {return true;}
		return _board.get(y)[x].on();
	}
	
	private boolean isShadow(int x, int y) {
		return _board.get(y)[x].isShadow();
	}
	
	public boolean leftKick(int x, int y, double[] center) {
		return !this.isOccupied((int) (center[0]-center[1]+y), (int) (center[1]+center[0]-x)) &&
				!this.isOccupied((int) (center[0]-center[1]+y+1), (int) (center[1]+center[0]-x));
	}
	
	public boolean rightKick (int x, int y, double[] center) {
		return !this.isOccupied((int) (center[0]-center[1]+y), (int) (center[1]+center[0]-x)) &&
				!this.isOccupied((int) (center[0]-center[1]+y-1), (int) (center[1]+center[0]-x));
	}
	
	// Mutators
	public void paintBlock(int x, int y, Color c) {
		// Paints a single light the given color
		_board.get(y)[x].flickOn(c);
	}
	
	public void darkenBlock(int x, int y) {
		// Paints a single light black
		_board.get(y)[x].flickOff();
	}
	
	// Scoring algorithm- low score is better
	public int getScore(Tetromino t) {
		int score = -9 * this.simulateClear(t.min()); // -9 points for each line cleared
		for(int x=0; x<10; x++) {
			int numBlocks = 0;
			int blockScore = 0;
			for (int y=0; y<20; y++) {
				if(this.isShadow(x, y) && !_board.get(y)[x].simulated()) {numBlocks++;}
				// +1 point for each shadow above the ground
			}
			int y = 0;
			while(numBlocks>0) {
				if(this.isShadow(x, y) && !_board.get(y)[x].simulated()) {
					numBlocks--;
					if(y>15) {blockScore+=15;} // +15 points for height above 15 rows
					score += blockScore; // (i.e., as the stacks get higher, the ai will
					blockScore++; // will quickly favor lower positions
				} else if(this.isOccupied(x, y) && !_board.get(y)[x].simulated()) {
					blockScore++;
				} else if(!_board.get(y)[x].simulated()) {
					score += 17; // 17 points for each gap beneath the shadow
				}
				y++;
			}
			int towers = 0;
			for(int j=0; j<16; j++) {
				if(!_board.get(y)[x].simulated()) {
					if(this.isOccupied(x, j)) {
						towers = 0;
					} else if(this.isOccupied(x-1, j) &&
							this.isOccupied(x+1, j)) {
						towers++;
						if(towers==3) { // 14 points for generating a tower
							score += 14; // (tower = only an I can fit cleanly inside)
						}
					}
				}
			}
		}
		this.unSimulate(t.min());
		return score;
	}
	
	// Method used internally to set initial square conditions
	private void setRectangle(RectangleShape rectangle) {
		rectangle.setSize(30,30);
		rectangle.setBorderColor(Constants.GRAY);
		rectangle.setVisible(false);
	}
	
	// Graphically updates the locations of each of the squares when a row is cleared
	private void setLocations(int min) {
		for(int i=min; i<20; i++) {
			for(int j=0; j<10; j++) {
				_board.get(i)[j].setVisible(true);
				_board.get(i)[j].setLocation(30*j,570-30*i);
			}
		}
	}
	
	// Logic for clearing rows
	public int clearRow(int min) {
		int cleared = 0;
		for(int i=min; i<Math.min(20, min+4); i++) {
			boolean flag = true;
			for(int j=0; j<10; j++) {
				flag = flag && this.isOccupied(j,i);
			}
			if(flag) {
				cleared++;
				_board.remove(i);
				i--;
				_board.add(new RectangleShape[10]);
				for(int j=0; j<10; j++) {
					_board.get(21)[j] = new RectangleShape();
					this.setRectangle(_board.get(21)[j]);
				}
			}
		}
		this.setLocations(min);
		return cleared;
	}
	
	// Simulates line clears (so cleared lines do not interfere with scoring algorithm
	private int simulateClear(int min) {
		int lines = 0;
		for(int i=min; i<Math.min(20, min+4); i++) {
			boolean flag = true;
			for(int j=0; j<10; j++) {
				flag = flag && this.isOccupied(j,i);
			}
			if(flag) {
				lines++;
				for(int j=0; j<10; j++) {
					_board.get(i)[j].simulate(true);
				}
			}
		}
		return lines;
	}
	
	// Removes all simulated line clears
	private void unSimulate(int min) {
		for(int i=min; i<Math.min(20, min+4); i++) {
			for(RectangleShape light : _board.get(i)) {
				light.simulate(false);
			}
		}
	}
	
	// Paint method called by the tetris object
	public void paint(Graphics g) {
		for(int i=0; i<20; i++) {
			for(int j=0; j<10; j++) {
				_board.get(i)[j].paint(g);
			}
		}
	}
	
	// Checks to see if any block is in the lowest invisible row
	public boolean isOver() {
		boolean flag = false;
		for(int i=0; i<10; i++) {
			flag = flag || this.isOccupied(i, 20);
		}
		return flag;
	}
	
	// Clears the board for game over / restart
	public void clear() {
		for(RectangleShape[] row : _board) {
			for(RectangleShape block : row) {
				block.flickOff();
			}
		}
	}
}
