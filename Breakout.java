/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram{

/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;
	
/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;
	
/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 30;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

/** Separation between bricks */
	private static final int BRICK_SEP = 4;

/** Width of a brick */
	private static final int BRICK_WIDTH = 
			(WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;
	
	//velocity of ball on Y axis
	private static final double VY = 3.0;
	
	//animation speed
	private static final int GAME_PLAY_SPEED = 9;
	
	//random generator for velocity of ball on X axis
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	GOval ball; 
	GRect paddle;
	
/* Method: run() */
/** Runs the Breakout program. */
	public void run() {
		addMouseListeners(); //For a moving paddle and click to start
		
		setup(); //this method draws object before start game
		
		clickToStart(); //draw relevant object with String and wait for click to start
		
		/*this is main method for this game, Where are all the animation of the game. this method has two three parameter. first is velocity of of ball on X axis.
		 * second parameter is also velocity and third is number of life*/
		animation(rGenSpeedX(), VY, NTURNS); 
	}
	
	private void setup() {
		drawBricks(); //this method draw all bricks
		drawPaddle(); //this method draw paddle that must be moved and it depends on x coordinate of mouse
		drawBall(); //this method draw ball that have velocity towards x-axis, also y-axis
	}
	
	//first for loop draws rows and second for loop draws column
	private void drawBricks() {
		for(int i = 0; i < NBRICK_ROWS; i++) {
			for(int j = 0; j < NBRICKS_PER_ROW; j++) {
				GRect brick = new GRect (BRICK_SEP + j * (BRICK_SEP + BRICK_WIDTH), BRICK_Y_OFFSET + i * (BRICK_HEIGHT + BRICK_SEP), BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setFillColor(fillBrick(i)); //fillBrick method fill brick with relevant colour
				add(brick);
			}
		}
	}
	
	// this method fills brick with relevant colour
	private Color fillBrick(int a) {
		Color color;
		if(a < 2) {//for first and second rows
			color = Color.RED;
		}else if(a < 4) {//for third and fourth rows
			color = Color.ORANGE;
		}else if(a < 6) {//for fifth and sixth rows
			color = Color.YELLOW;
		}else if(a < 8) {//for seventh and eighth rows
 			color = Color.GREEN;
		}else {//for last rows
			color = Color.CYAN;
		}
		return color;
	}
	
	//this metod creates and draws paddle object
	private void drawPaddle() {
		paddle = new GRect (WIDTH/2 - PADDLE_WIDTH/2, HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	
	//this metod creates and draws ball object
	private void drawBall() {
		ball = new GOval (WIDTH/2 - BALL_RADIUS, HEIGHT/2 - BALL_RADIUS, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}
	
	// this metod draw relevant object with String before to start game and wait for click to start and then remove to created object
	private void clickToStart() {
		GLabel start = new GLabel("Click To Start");
		start.setFont("SenSerif-Bold-50");
		start.setColor(Color.GREEN);
		add(start, WIDTH/2 - start.getWidth()/2, HEIGHT/2 - start.getHeight()/2);
		waitForClick();
		remove(start);
	}
	
	/*first parameter is velocity of of ball on X axis. second parameter is also velocity and third is number of life*/
	private void animation(double vx, double vy, int life) {
		int brickNum = NBRICK_ROWS * NBRICKS_PER_ROW; //for counting brick
		//if we want to see animation then this loop must repeat always when we have nonzero life and nonzero brick
		while(life != 0 && brickNum != 0) { 
			ball.setLocation(ball.getX() + vx, ball.getY() + vy); //ball location depends on this method
			add(paddle);

			//check four point around ball and if any of these points are on wall or (right or left bottom side of ball) then ball must change direction on x axis
			vx = checkObjectForSpeedX(vx);
			//check four point around ball and if any of these points are on paddle or top wall or brick then ball must change direction on y axis. 
			vy = checkObjectForSpeedY(vy);
			life = checkLife(life); //this method check number of life 
			
			brickNum = numOfBricks(getCollidingObject(), brickNum); //this metod check number of brick
			pause(GAME_PLAY_SPEED);
		}
		
		gameOver(life); //if life equals zero then gamer is lost :)
		winGame(brickNum); //if life equals zero then gamer is winner :)
	}
	
	//This method randomly takes the velocity with respect to the X-axis
	private double rGenSpeedX() {
		double VX = rgen.nextDouble(1.0, 3.0);
		if(rgen.nextBoolean(0.5)) {
			VX = -VX;
		}
		return VX;
	}
	
	//for calculating number of brick
	private int numOfBricks(GObject colider, int brick) {
		if(colider != null)  {
			if(colider != paddle) {
				brick--;
			}
		}	
		return brick;
	}
	
	//this method checks four point around ball and if any of these points are on wall or (right or left bottom side of paddle) then ball must change direction on x axis
	private double checkObjectForSpeedX(double vx) {
		if(ball.getX() <= 0 || ball.getX() + 2 * BALL_RADIUS >= WIDTH) { //for right and left wall
			vx = -vx;
		}else if(ball.getY() >= HEIGHT) {
			vx = rgen.nextDouble(1.0, 3.0);
		}else if(ball.getY() + 2 * BALL_RADIUS > paddle.getY() + VY && getCollidingObject() == paddle) { //for paddle
				vx = -vx;
		}
		return vx;
	}
	
	//this method checks four point around ball and if any of these points are on paddle or top wall or brick then ball must change direction on y axis. 
	private double checkObjectForSpeedY(double vy) {
		GObject colider = getCollidingObject();
		if(colider != null) {
			if(colider != paddle) { //for brick
				remove(colider);
				vy = -vy;
			}else if(colider == paddle && ball.getY() + 2 * BALL_RADIUS <= paddle.getY() + VY){ //for paddle
				vy = -vy;
			}
		}else if(ball.getY() <= 0) { //for top
			vy = -vy;
		}
		return vy;
	}
	
	//this method return object which may be on any of four points around of ball
	private GObject getCollidingObject() {
		if(getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		}else if(getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != null) {
			return getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
		}else if(getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS) != null) {
			return getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
		}else{
			return getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
		}
	}
	
	private int checkLife(int life) {
		if(ball.getY() >= HEIGHT - 2 * BALL_RADIUS) {
			ball.setLocation(WIDTH/2 - BALL_RADIUS, HEIGHT/2 - BALL_RADIUS);
			life--;
		}
		return life;
	}
	
	private void gameOver(int life) {
		if(life == 0) {
			GLabel over = new GLabel("Game Over");
			over.setFont("SensSerif-bold-40");
			over.setColor(Color.RED);
			add(over, WIDTH/2 - over.getWidth()/2, HEIGHT/2 - over.getHeight()/2);
		}
	}
	
	private void winGame(int brick) {
		if(brick == 0) {
			GLabel win = new GLabel("Win Game");
			win.setFont("SensSerif-bold-40");
			win.setColor(Color.GREEN);
			add(win, WIDTH/2 - win.getWidth()/2, HEIGHT/2 - win.getHeight()/2);
		}
	}
	
	//for moveing paddle
	@Override
	public void mouseMoved(MouseEvent e) {
		if(e.getX() <= APPLICATION_WIDTH - PADDLE_WIDTH) {//borders for paddle
			GPoint paddlePoint = new GPoint(e.getX(), HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
			paddle.setLocation(paddlePoint);
		}
	}
}
