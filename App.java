package tetris;

import javax.swing.JFrame;

/**
 * @author dmayans
 * 
 * @TODO
 * 1) Fix wall kicks, currently do not function
 * 2) Currently AI button does not properly disable player controls
 * 
 */

public class App {

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.add(new TetrisShell());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
	}

}
