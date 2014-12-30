package tetris;

/**
 * @author dmayans
 */

import java.awt.Color;

public class Constants {
	// Tetrominoes: {{x-coordinates},{y-coordinates},{color},{center of rotation}
	//				{{point1,2,3},{point1,2,3},{rgb},{x,y}}
	// Coordinates relative to centerpiece at (1,0).
	// Center of rotation is in halves relative to centerpiece
	public static final int[][] I = {{0,2,3},{0,0,0},{0,255,255},{1,1}};
	public static final int[][] O = {{2,2,1},{0,1,1},{255,255,0},{1,1}};
	public static final int[][] J = {{2,0,0},{0,0,1},{0,0,255},{0,0}};
	public static final int[][] L = {{0,2,2},{0,0,1},{255,155,0},{0,0}};
	public static final int[][] Z = {{2,1,0},{0,1,1},{255,0,0},{0,0}};
	public static final int[][] S = {{0,1,2},{0,1,1},{0,255,0},{0,0}};
	public static final int[][] T = {{0,1,2},{0,1,0},{175,0,255},{0,0}};
	
	// Turns a random int (index) into a useful object for the tetromino to constrcut
	public static final int[][][] TETROMINOES = {I,O,J,L,Z,S,T};
	// Used by the AI to value tetromino swaps
	public static final int[] SCORES = {6,0,2,2,4,3,8};
	
	
	// Motion arrays. ROTATE and NROTATE must be 0 to prevent the center of rotation from moving
	public static final int[] LEFT = {-1,0};
	public static final int[] RIGHT = {1,0};
	public static final int[] DOWN = {0,-1};
	public static final int[] ROTATE = {0,0};
	public static final int[] NROTATE = {0,0};
	
	// Turns Integers into int[]s for communication between the AI and tetris game.
	public static final int[][] MOTION = {LEFT,RIGHT,DOWN,ROTATE,NROTATE};
	
	// Colors. Gray has been darkened somewhat
	public static final Color GRAY = new Color(50,50,50);
}
