package tetris;

/**
 * Handles the logical and graphical condition of each block. Each block can be empty,
 * occupied by a block, occupied by a shadow, and/or simu-cleared (scoring algorithm).
 * 
 * @author dmayans
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class RectangleShape {
	private int _width;
	private int _height;
	private int _x;
	private int _y;
	private Color _color;
	private Color _borderColor;
	private boolean _visible;
	private boolean _on;
	private boolean _simulated;
	
	public RectangleShape() {
		_width = 0;
		_height = 0;
		_x = 0;
		_y = 0;
		_color = Color.BLACK;
		_borderColor = Color.BLACK;
		_visible = false;
		_on = false;
		_simulated = false;
	}
	
	// Accessors
	public boolean on() {
		return _on;
	}
	
	public boolean simulated() {
		return _simulated;
	}
	
	public boolean isShadow() {
		return _color==Color.GRAY;
	}
	
	// Mutators
	public void setLocation(int x, int y) {
		_x = x;
		_y = y;
	}
	
	public void flickOn(Color c) {
		_color = c;
		_on = true;
	}
	
	public void flickOff() {
		_color = Color.BLACK;
		_on = false;
	}
	
	public void simulate(boolean b) {
		_simulated = b;
	}
	
	public void setSize(int width, int height) {
		_width = width;
		_height = height;
	}
	
	public void setBorderColor(Color c) {
		_borderColor = c;
	}
	
	public void setVisible(boolean b) {
		_visible = b;
	}
	
	public void paint(Graphics g) {
		if(_visible) {
			Graphics2D graphics = (Graphics2D) g;
			graphics.setColor(_color);
			graphics.fillRect(_x, _y, _width, _height);
			graphics.setColor(_borderColor);
			graphics.drawRect(_x,_y,_width,_height);
		}
	}
}
