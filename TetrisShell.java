package tetris;

/**
 * Generates a shell around the tetris game that handles the outer graphical panel and
 * additional functionality: upcoming pieces, pause/ai buttons, restart and quit buttons,
 * and piece storage.
 * 
 * @author dmayans
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

@SuppressWarnings("serial")
public class TetrisShell extends JPanel {
	
	private UpcomingPieces _upcoming;
	private int _lines;
	
	private NextPiece _next;
	private StoredPiece _stored;
	private SubPiece _sub1;
	private SubPiece _sub2;
	private SubPiece _sub3;
	private JLabel _score;
	private JToggleButton _paused;
	
	private Tetris _tetris;
	private LossFrame _loss;
	
	public TetrisShell() {
		super();
		
		// Sets up the proxy for the upcoming pieces
		_upcoming = new UpcomingPieces();
		_lines = 0;
		
		// Almost entirely ui with some listeners here and there
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		Font font = new Font("Comic Sans MS", 0, 12);
		
		c.gridx = 3;
		c.gridy = 0;
		c.gridheight = 12;
		c.gridwidth = 1;
		c.insets = new Insets(5,0,5,5);
		this.add(new Tetris(_upcoming,this),c);
		
		c.gridx = 0;
		c.gridheight = 1;
		c.gridwidth = 3;
		c.insets = new Insets(5,5,5,5);
		JLabel stored = new JLabel("Stored Piece:");
		stored.setFont(font);
		this.add(stored,c);
		
		c.gridy = 1;
		_stored = new StoredPiece();
		this.add(_stored,c);
		
		c.gridy = 2;
		JLabel next = new JLabel("Next Piece:");
		next.setFont(font);
		this.add(next,c);
		
		c.gridy = 3;
		_next = new NextPiece();
		this.add(_next,c);
		
		c.gridy = 4;
		c.gridwidth = 1;
		c.insets = new Insets(0,5,5,5);
		_sub1 = new SubPiece(1);
		this.add(_sub1,c);
		
		c.gridx = 1;
		c.insets = new Insets(0,0,5,5);
		_sub2 = new SubPiece(2);
		this.add(_sub2,c);
		
		c.gridx = 2;
		_sub3 = new SubPiece(3);
		this.add(_sub3,c);
		
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 3;
		c.insets = new Insets(5,5,5,5);
		JLabel cleared = new JLabel("Lines Cleared:");
		cleared.setFont(font);
		this.add(cleared,c);
		
		c.gridy = 6;
		_score = new JLabel("0");
		_score.setFont(font);
		this.add(_score,c);
		
		c.gridy = 7;
		c.weighty = 1;
		this.add(new JPanel(),c);
		
		c.gridy = 8;
		c.weighty = 0;
		_paused = new JToggleButton("Pause");
		_paused.setFont(font);
		_paused.setPreferredSize(new Dimension(120,30));
		_paused.addItemListener(new PauseListener());
		this.add(_paused,c);
		
		c.gridy = 9;
		c.insets = new Insets(0,5,5,5);
		JButton restart = new JButton("Restart");
		restart.setFont(font);
		restart.setPreferredSize(new Dimension(120,30));
		restart.addActionListener(new RestartListener());
		this.add(restart,c);
		
		c.gridy = 10;
		JToggleButton ai = new JToggleButton("AI");
		ai.setFont(font);
		ai.setPreferredSize(new Dimension(120,30));
		ai.addItemListener(new AIListener());
		this.add(ai,c);
		
		c.gridy = 11;
		JButton quit = new JButton("Quit");
		quit.setFont(font);
		quit.setPreferredSize(new Dimension(120,30));
		quit.addActionListener(new QuitListener());
		this.add(quit,c);
	}
	
	// Accessors. Important when the stored piece is empty
	public boolean isEmpty() {
		return _stored.isEmpty();
	}
	
	// Called to peek at the stored piece (AI)
	public int peekSwap() {
		return _stored.peek();
	}
	
	// Mutator. Important for passing pause and stored piece information
	// between tetris and the shell.
	public void setTetris(Tetris t) {
		_tetris = t;
	}
	
	// Called by game to display game over message
	public void end() {
		_loss = new LossFrame();
	}
	
	// Called by the tetris game whenever a piece locks to update the ui
	public void updateScore(int lines) {
		// Update the scoreboard
		_lines += lines;
		_score.setText(Integer.toString(_lines));
		// Update the Rate
		_tetris.dropRate(12000/(lines+15));
		// Update the upcoming pieces
		_next.paintNext(Constants.TETROMINOES[_upcoming.peek(1)]);
		_sub1.paintNext();
		_sub2.paintNext();
		_sub3.paintNext();
	}
	
	public void clearScore() {
		_lines = 0;
		this.updateScore(0);
	}
	
	// Passes information between tetris and its shell
	public int swapPiece(int type) {
		return _stored.swapPiece(type);
	}
	
	// Passes pause information between tetris and its shell. Pause is called
	// by the Tetris and callPause is called by the TetrisShell to prevent recursion
	public void pause(boolean b) {
		_paused.setSelected(b);
	}
	
	public void callPause(boolean b) {
		_tetris.pause(b);
	}
	
	// Several private classes. They each edit information and freely call on the
	// contained tetris object, which is why they are contained here.
	private class NextPiece extends JPanel {
		
		private JPanel[][] _board; // NOT the tetris board
		private int[] _x;
		private int[] _y;
		
		public NextPiece() {
			super();
			this.setPreferredSize(new Dimension(120,120));
			_board = new JPanel[4][4];
			_x = new int[4];
			_y = new int[4];
			// Sets up a 4x4 board of black lights to store the graphical information
			// for the next piece
			this.setLayout(new GridLayout(4,4));
			for(int i=3; i>=0; i--) {
				for(int j=0; j<4; j++) {
					_board[i][j] = new JPanel();
					_board[i][j].setPreferredSize(new Dimension(30,30));
					_board[i][j].setBackground(Color.BLACK);
					this.add(_board[i][j]);
				}
			}
			
			int[][] piece = Constants.TETROMINOES[_upcoming.peek(1)];
			for(int i=0; i<3; i++) {
				_x[i] = piece[0][i];
				_y[i] = piece[1][i];
			}
			_x[3] = 1;
			_y[3] = 0;
			this.paintNext(piece);
		}
		
		// Method called to paint a new tetromino
		public void paintNext(int[][] type) {
			for(int i=0; i<3; i++) {
				_board[_y[i]+1][_x[i]].setBackground(Color.BLACK);
				_x[i] = type[0][i];
				_y[i] = type[1][i];
			}
			Color color = new Color(type[2][0],type[2][1],type[2][2]);
			for(int i=0; i<4; i++) {
				_board[_y[i]+1][_x[i]].setBackground(color);
			}
		}
	}
	
	// Three of these are used. Each is displayed beneath the larger next piece
	// panel. They only display the color of the upcoming pieces, but show farther
	// ahead.
	private class SubPiece extends JPanel {
		private int _i;
		
		public SubPiece(int i) {
			super();
			this.setPreferredSize(new Dimension(36,36));
			_i = i+1;
			this.paintNext();
		}
		
		// Called whenever a tetromino locks to push the paint forward
		public void paintNext() {
			int x[][] = Constants.TETROMINOES[_upcoming.peek(_i)];
			this.setBackground(new Color(x[2][0],x[2][1],x[2][2]));
		}
	}
	
	// A more complicated private class. It stores and displays a piece and can
	// pass it back and forth with the Tetris object
	
	private class StoredPiece extends JPanel {
		
		private JPanel[][] _board;
		private int _tetrominoType;
		private int[] _x;
		private int[] _y;
		
		public StoredPiece() {
			super();
			this.setPreferredSize(new Dimension(120,120));
			_board = new JPanel[4][4];
			_x = new int[4];
			_y = new int[4];
			// Sets up a grid of black lights (similar to the next piece display)
			this.setLayout(new GridLayout(4,4));
			for(int i=3; i>=0; i--) {
				for(int j=0; j<4; j++) {
					_board[i][j] = new JPanel();
					_board[i][j].setPreferredSize(new Dimension(30,30));
					_board[i][j].setBackground(Color.BLACK);
					this.add(_board[i][j]);
				}
			}
			_x[3] = 1;
			_y[3] = 0;
		}
		
		// Important when null
		public boolean isEmpty() {
			return _tetrominoType == -1;
		}
		
		// Used by AI
		public int peek() {
			return _tetrominoType;
		}
		
		// Called by the Tetris object, then passed through the shell
		public int swapPiece(int type) {
			int temp = _tetrominoType;
			_tetrominoType = type;
			this.paintNext(_tetrominoType);
			repaint();
			return temp;
		}
		
		// Called during restart
		public void clearPiece() {
			for(int i=0; i<4; i++) {
				_board[_y[i]+1][_x[i]].setBackground(Color.BLACK);
			}
			_tetrominoType = -1;
		}
		
		// Code used to update graphics when stored piece is swapped
		public void paintNext(int typeInt) {
			int[][] type = Constants.TETROMINOES[typeInt];
			for(int i=0; i<3; i++) {
				_board[_y[i]+1][_x[i]].setBackground(Color.BLACK);
				_x[i] = type[0][i];
				_y[i] = type[1][i];
			}
			Color color = new Color(type[2][0],type[2][1],type[2][2]);
			for(int i=0; i<4; i++) {
				_board[_y[i]+1][_x[i]].setBackground(color);
			}
		}
	}
	
	// Listener for the pause JToggleButton. Has code to pass information
	// back to the tetris object
	private class PauseListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			callPause(e.getStateChange()==ItemEvent.SELECTED);
		}
	}
	
	// Listener for the ai JToggleButton
	private class AIListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			_tetris.ai(e.getStateChange()==ItemEvent.SELECTED);
		}
	}
	
	// Listener for the quit button
	private class QuitListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}
	
	// Listener for the restart button
	private class RestartListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			_tetris.restart();
			_stored.clearPiece();
			if(_loss != null) {
				_loss.setVisible(false);
				_loss.dispose();
			}
		}
	}
	
	// "You Lose" message
	private class LossFrame extends JFrame {
		public LossFrame() {
			super();
			this.setPreferredSize(new Dimension(300,100));
			
			// Layout. Borrows listeners from shell
			this.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			Font font = new Font("Comic Sans MS", 0, 12);
			
			c.weightx = 0;
			c.weighty = 0;
			c.gridx = 3;
			c.gridwidth = 1;
			c.gridy = 1;
			JLabel message = new JLabel("Game Over!");
			message.setFont(font);
			this.add(message, c);
			
			c.gridx = 1;
			c.gridwidth = 2;
			c.gridy = 3;
			JButton restart = new JButton("Restart");
			restart.setFont(font);
			restart.addActionListener(new RestartListener());
			this.add(restart, c);
			
			c.gridx = 4;
			JButton quit = new JButton("Quit");
			quit.setFont(font);
			quit.addActionListener(new QuitListener());
			this.add(quit, c);
			
			c.weightx = 0.5;
			c.weighty = 0.5;
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 7;
			c.gridheight = 1;
			this.add(new JPanel(), c);
			
			c.gridy = 2;
			this.add(new JPanel(), c);
			
			c.gridy = 4;
			this.add(new JPanel(), c);
			
			c.gridwidth = 2;
			c.gridy = 1;
			this.add(new JPanel(), c);
			
			c.gridx = 5;
			this.add(new JPanel(), c);
			
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = 3;
			this.add(new JPanel(), c);
			
			c.gridx = 3;
			c.weightx = 0;
			c.weighty = 0;
			this.add(new JPanel(), c);
			
			c.gridx = 6;
			c.weightx = 0.5;
			c.weighty = 0.5;
			this.add(new JPanel(), c);
			
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.pack();
			this.setResizable(false);
			this.setVisible(true);
		}
	}
}
