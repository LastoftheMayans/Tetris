package tetris;

/**
 * Generates an invisible shell around the board to run the game of tetris. Handles
 * keypresses and appropriate board responses. Generates tetrominos, etc.
 * 
 * @author dmayans
 */

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

@SuppressWarnings("serial")
public class Tetris extends JPanel {
	
	private UpcomingPieces _upcoming;
	private TetrisShell _shell;
	private Board _board;
	private Tetromino _tetromino;
	private AI _AI;
	
	private boolean _paused;
	private boolean _over;
	private boolean _ai;
	
	private Timer _t;
	private Timer _a;
	
	public Tetris(UpcomingPieces upcoming, TetrisShell shell) {
		super();
		this.setPreferredSize(new Dimension(301,601));
		this.setFocusable(true);
		
		_upcoming = upcoming;
		_shell = shell;
		_shell.setTetris(this); // Mutual association necessary for proper pause functionality
		_board = new Board();
		_tetromino = new Tetromino(_board, _upcoming.push());
		_AI = new AI(_board);
		
		_paused = false;
		_over = false;
		_ai = false;
		
		_t = new Timer(800, new TimerListener());
		_t.start();
		_a = new Timer(40, new AIListener());
		
		this.addKeyListener(new KeyboardListener());
	}
	
	// Mutators
	public void swapPiece(int type) {
		// Hides and overwrites old piece
		_tetromino.hide();
		if(_shell.isEmpty()) {
			_shell.swapPiece(_tetromino.getType());
			_tetromino = new Tetromino(_board, _upcoming.push());
			_shell.updateScore(0);
		} else {
			_tetromino = new Tetromino(_board, _shell.swapPiece(type));
		}
	}
	
	public void pause(boolean b) {
		// Called by the shell to pass relevant pause information
		_paused = b && !_over;
		this.grabFocus(); // take focus back from the pause button
	}
	
	public void ai(boolean b) {
		// Called by the shell to pass relevant ai information
		_ai = b;
		if(_ai) {
			_a.start();
			_AI.newMove(_tetromino, _shell.peekSwap());
		} else {
			_a.stop();
		}
		this.grabFocus(); // take focus back from the ai button
	}
	
	// Used to adjust the droprate after lines have been cleared
	public void dropRate(int rate) {
		_t.stop();
		_t = new Timer(rate, new TimerListener());
		_t.start();
		_a.stop();
		_a = new Timer(rate/20, new AIListener());
		if(_ai) {_a.start();}
	}
	
	// Called each time a tetromino locks. Clears rows, checks for game over, etc.
	public void checkEnd(int min) {
		int x = _board.clearRow(min);
		if(_board.isOver()) {
			_paused = true;
			_over = true;
			_shell.end();
		} else {
			// If the game doesn't end, generate a new tetromino
			_tetromino = new Tetromino(_board, _upcoming.push());
		}
		_shell.updateScore(x); // Also updates upcoming pieces
	}
	
	// Restarts the game
	public void restart() {
		this.grabFocus();
		_upcoming.shuffle();
		_board.clear();
		_shell.clearScore();
		_paused = false;
		_over = false;
		_tetromino = new Tetromino(_board, _upcoming.push());
		_AI.newMove(_tetromino, -1);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		_board.paint(g);
	}
	
	// TimerListener used to force the tetromino down
	private class TimerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(!_paused && !_tetromino.move(Constants.DOWN)) {
				checkEnd(_tetromino.min());
			}
			repaint();
		}
	}
	
	// AIListener used to move the tetromino
	private class AIListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(_ai && !_paused) {
				Integer x = _AI.poll();
				if(x==null) {
					while(_tetromino.move(Constants.DOWN));
					checkEnd(_tetromino.min());
					_AI.newMove(_tetromino, _shell.peekSwap());
				} else if(x.equals(-1)) {
					swapPiece(_tetromino.getType());
				} else {
					_tetromino.move(Constants.MOTION[x]);
				}
				repaint();
			}
		}
	}
	
	// KeyboardListener used to respond to arrow keys, etc.
	private class KeyboardListener implements KeyListener {
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode()==KeyEvent.VK_LEFT && !_paused) {
				_tetromino.move(Constants.LEFT);
				repaint();
			} else if(e.getKeyCode()==KeyEvent.VK_RIGHT && !_paused) {
				_tetromino.move(Constants.RIGHT);
				repaint();
			} else if(e.getKeyCode()==KeyEvent.VK_UP && !_paused) {
				_tetromino.move(Constants.ROTATE);
				repaint();
			} else if(e.getKeyCode()==KeyEvent.VK_DOWN && !_paused) {
				if(!_tetromino.move(Constants.DOWN)) {
					checkEnd(_tetromino.min());
				}
				repaint();
			} else if(e.getKeyCode()==KeyEvent.VK_SPACE && !_paused) {
				while(_tetromino.move(Constants.DOWN)) {}
				checkEnd(_tetromino.min());
				repaint();
			} else if(e.getKeyCode()==KeyEvent.VK_C && !_paused) {
				// c used to swap stored piece
				swapPiece(_tetromino.getType());
			} else if(e.getKeyCode()==KeyEvent.VK_P && !_over) {
				_paused = !_paused;
				_shell.pause(_paused);
			}
		}
		
		@Override
		public void keyReleased(KeyEvent e) {}
		
		@Override
		public void keyTyped(KeyEvent e) {}
	}
	
}
