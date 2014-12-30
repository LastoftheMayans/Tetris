package tetris;

/**
 * AI class. Generates a new move whenever a new tetromino is created/AI button is turned
 * on and can be polled to return each move.
 * 
 * AI works by moving each piece into each of its ~32 positions (4 rotations and ~8 x
 * coordinates). For each piece, it asks the board for the score of the shadow and picks
 * the lowest score (low scores are better).
 * 
 * AI is completely original and has a high score of ~6300 lines.
 * 
 * @author dmayans
 */

import java.util.LinkedList;

public class AI {
	
	private Board _board;
	private LinkedList<Integer> _move;

	public AI(Board board) {
		_board = board;
		_move = new LinkedList<Integer>();
	}
	
	public void newMove(Tetromino t, int type) {
		t.hide(); // Prevent interfererence from the current tetromino
		int score = Integer.MAX_VALUE;
		int l = 0;
		for(int r=0; r<4; r++) { // for each rotation
			for(int x=0; x<10; x++) { // for each x position
				Tetromino ghost = new Tetromino(_board, t.getType());
				l = 0;
				ghost.move(Constants.DOWN); // move down once to prevent the top of the board
											// from blocking rotation
				for(int i=0; i<r; i++) {ghost.move(Constants.NROTATE);}; // NROTATE is 
						// non-recursive rotation, used to block wall kicks. Functionally identical otherwise
				while(ghost.move(Constants.LEFT)) {l++;}; // flush left
				for(int i=0; i<x; i++) {ghost.move(Constants.RIGHT);}
				if(score>_board.getScore(t)) { // if this ghost is a better move, replace the old move
					_move.clear();
					score = _board.getScore(t);
					_move.addLast(2); // drop twice
					for(int i=0; i<r; i++) {
						_move.addLast(4); // rotate r times
					}
					for(int i=0; i<l; i++) { // move left l times
						_move.addLast(0);
					}
					for(int i=0; i<x; i++) { // move right x times
						_move.addLast(1);
					}
				}
				ghost.hide();
			}
		}
		// This is the same operation, but on the stored piece
		for(int r=0; r<4; r++) {
			if(type==-1) {type = 1;}
			for(int x=0; x<10; x++) {
				Tetromino ghost = new Tetromino(_board, type);
				l = 0;
				ghost.move(Constants.DOWN);
				for(int i=0; i<r; i++) {ghost.move(Constants.NROTATE);};
				while(ghost.move(Constants.LEFT)) {l++;};
				for(int i=0; i<x; i++) {ghost.move(Constants.RIGHT);}
				// The next couple lines handle the relative values of the active piece and stored piece
				// (i.e., it's better to have a T in store than an O, even if storing the O would yield a
				// slightly better score
				if(score>_board.getScore(t)+Constants.SCORES[type]-Constants.SCORES[t.getType()]) {
					_move.clear();
					score = _board.getScore(t)+Constants.SCORES[type]-Constants.SCORES[t.getType()];
					_move.addLast(-1); // -1 commands a piece swap
					_move.addLast(2);
					for(int i=0; i<r; i++) {
						_move.addLast(4);
					}
					for(int i=0; i<l; i++) {
						_move.addLast(0);
					}
					for(int i=0; i<x; i++) {
						_move.addLast(1);
					}
				}
				ghost.hide();
			}
		}
		t.show();
	}
	
	public Integer poll() {
		return _move.pollFirst();
	}
	
}
