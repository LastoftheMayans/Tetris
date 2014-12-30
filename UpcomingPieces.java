package tetris;

/**
 * Proxy that holds and 'randomizes' the upcoming pieces. Tetris random upcoming pieces
 * are a random ordering of all seven pieces, not necessarily a random piece each time.
 * This guarantees a level of consistency and a maximum of 12 tetrominoes in between
 * I blocks.
 * 
 * @author dmayans
 */

public class UpcomingPieces {
	
	private int[] _pieces; // Current set of seven pieces
	private int[] _next; // Next set of seven pieces.
	private int _counter;
	
	public UpcomingPieces() {
		_pieces = new int[Constants.TETROMINOES.length];
		_next = new int[Constants.TETROMINOES.length];
		// Sets up initial tetrominoes
		for(int i=0; i<Constants.TETROMINOES.length; i++) {
			_pieces[i] = i;
			_next[i] = i;
		}
		this.shuffle(_pieces);
		this.shuffle(_next);
		this.push();
	}
	
	// Method called to push the list forward and generate a new tetromino at the end
	public int push() {
		if(_counter == Constants.TETROMINOES.length-1) {
			for(int i=0; i<Constants.TETROMINOES.length; i++) {
				_pieces[i] = _next[i];
			}
			_counter = 0;
			this.shuffle(_next);
		} else {
			_counter++;
		}
		return _pieces[_counter];
	}
	
	private void shuffle(int[] x) {
		// Simple Fisher-Yates shuffle
		for(int i=0; i<x.length; i++) {
			int rand = (int) (Math.random()*(x.length-i)) + i;
			int temp = x[i];
			x[i] = x[rand];
			x[rand] = temp;
		}
	}
	
	public void shuffle() {
		this.shuffle(_next);
		this.shuffle(_pieces);
		_counter = 0;
	}

	// Used by the shell to peek and display upcoming pieces
	// (this is why an array is being used instead of a queue)
	public int peek(int index) {
		if(index+_counter<Constants.TETROMINOES.length) {
			return _pieces[index+_counter];
		}
		return _next[index+_counter-Constants.TETROMINOES.length];
	}
}
