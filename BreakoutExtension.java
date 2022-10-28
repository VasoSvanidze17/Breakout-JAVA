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
import javafx.util.converter.CharacterStringConverter;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import com.sun.media.jfxmedia.control.VideoDataBuffer;
import com.sun.org.apache.bcel.internal.generic.GOTO;

public class BreakoutExtension extends GraphicsProgram{

/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 404; 
	public static final int APPLICATION_HEIGHT = 670;
	
/** Dimensions of game board (usually the same) */
	private static final int WIDTH = 404;
	private static final int HEIGHT = 600;
	
/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60; 
	private static final int PADDLE_HEIGHT = 10;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 3;

/** Separation between bricks */
	private static final int BRICK_SEP = 4;
	
	private static final int BUTTON_SEP = 60;

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
	
	private static final double BUTTON_X = 150;
	private static final double BUTTON_Y = 40;
	private static final double BUTTON_ARC_SIZE = 30;
	
	private static final int CLICK_PAUSE = 200;
	
	//instance variables
	GOval ball;
	GRect paddle;
	GImage background;
	
	//for button
	GRoundRect startRect;
	GLabel startLabel;
	
	GRoundRect settingRect;
	GLabel settingLabel;
	
	GRoundRect cancelRect;
	GLabel cancelLabel;
	
	GRoundRect restartRect;
	GLabel restartLabel;
	
	GLabel scoreLabelNum;
	
	//GImage brickOfChine = new
	
/* Method: run() */
/** Runs the Break out program. */
	public void run() {
		addMouseListeners(); //For a moving paddle and clicks
		
		loading(); //this method makes program more realistic
		removeAll(); //remove loading screen
		
		addGameBar(); //for number of life and score
		
		menu();
		
		removeUnnecessaryObj(); //when player will cancel in menu and start playing remove some obj
		
		/*this is main method for this game, Where are all the animation of the game. this method has two three parameter. first is velocity of of ball on X axis.
		 * second parameter is also velocity and third is number of life*/
		animation(rGenSpeedX(1.0, 3.0), VY, NTURNS);
	}
	
	private void loading() {
		backgroundLevel1(APPLICATION_WIDTH, APPLICATION_HEIGHT); //background of game
		addGameName(); //draw game name on loading screen
		addLoadingLabel(); //draw label "Loading" on canvas
		addLoadingBar(); //for animation of loading
	}

	//Background Implementation
	private void backgroundLevel1(double x, double y) {
		background = new GImage("starting.jpg");
		background.setSize(x, y);
		add(background);
	}
	
	//addGameName Implementation
	private void addGameName() {
		GLabel gameName = new GLabel("BREAK OUT");
		gameName.setFont("SenSerif-Bold-50");
		gameName.setColor(Color.GREEN);
		add(gameName, getWidth()/2 - gameName.getWidth()/2, getHeight()/2 - getHeight()/11);
	}
	
	//create loading label
	private void addLoadingLabel() {
		GLabel loading = new GLabel("L O A D I N G");
		loading.setFont("SenSerif-Bold-20");
		loading.setColor(Color.WHITE);
		add(loading, getWidth()/2 - loading.getWidth()/2, getHeight()/2 + BUTTON_SEP * 2/3);
	}
	
	//create loading bar and small animation of loading
	private void addLoadingBar() {
		GRoundRect loadingBar = new GRoundRect(getWidth()/2, getHeight()/22);
		loadingBar.setFilled(true);
		loadingBar.setColor(Color.BLACK);
		add(loadingBar, getWidth()/2 - loadingBar.getWidth()/2, getHeight()/2 + BUTTON_SEP);
		loadingAnimation(loadingBar); //loading animation
	}
	
	//implementation of loading animation
	private void loadingAnimation(GRoundRect rect) {
		double x = 1; //size of filler(size of recte)
		AudioClip loading = MediaTools.loadAudioClip("loading.au");
		loading.play(); //sound of loading animation
		//animation
		for(int i = 0; rect.getX() + (i + 1) * x <= rect.getX() + rect.getWidth(); i++) { 
			GRect fillRect = new GRect(x, rect.getHeight() + 2);
			fillRect.setFilled(true);
			fillRect.setColor(Color.RED);
			pause(8);
			add(fillRect, rect.getX() + i * x, rect.getY());
		}
		loading.stop(); //stop play when loading ended. 
	}
	
	//this method create small board bottom side for number of life and score
	private void addGameBar() {
		GRect lifeBar = new GRect(0, HEIGHT, WIDTH, getHeight() - HEIGHT);
		lifeBar.setFilled(true);
		lifeBar.setFillColor(Color.CYAN);
		add(lifeBar);
	}
	
	
	//this method makes program more realistic
	private void menu() {
		startButton(); //this button is created before compiler enter while, because if player will click firstly on start button, compiler will jump while loop 
		boolean onlyOne = true; //with help of this method paddle and ball will be created once
		
		//this while loop is for setting option 
		while(startRect.getColor() != Color.RED) {
			backgroundLevel1(WIDTH, HEIGHT); //draw background again because when player will click setting and then will click cancel then backround wolud be draw again
			startButton(); //create start button, because when player will click setting and then will click cancel then start and setting button would create again
			settingButton(); //create setting button
			
			//at this time compiler is in blank while loop until player will click button
			while(startRect.getColor() != Color.RED && settingRect.getColor() != Color.RED) { //for delay
				
			}
			
			buttonClickSound(); //play sound of button click
			
			//create ball and paddle by once
			if(onlyOne == true) {
				creatBall();
				creatPaddle();
				onlyOne = false;
			}
			
			//if player will click setting button then rect of button will be filled with red colour. 
			if(settingRect.getColor() == Color.RED) {
				settingRect.setColor(Color.BLUE); //setting button become blue again
				settingOption(); //setting option for colour of ball and paddle
			}
		}
		
		startRect.setColor(Color.BLUE); //start button must become blue again
	}
	
	//start button is created with help of drawButtonRect and drawButtonLabel  
	private void startButton() {
		startRect = drawButtonRect(startRect, getWidth()/2 - BUTTON_X/2, getHeight()/2 - getHeight()/8);
		startLabel = drawButtonText(startLabel, getWidth()/2 - BUTTON_X/2, getHeight()/2 - getHeight()/8, "S T A R T");
	}
	
	//with help of this method i can creat button rect, this method have three parameter. first is created object for butoon rect, second an third parameter is for 
	private GRoundRect drawButtonRect(GRoundRect buttonRect, double rectX, double rectY) {
		buttonRect = new GRoundRect(rectX, rectY, BUTTON_X, BUTTON_Y, BUTTON_ARC_SIZE);
		buttonRect.setFilled(true);
		buttonRect.setColor(Color.BLUE);
		add(buttonRect);
		
		return buttonRect;
	}
	
	//with help of this method i creat button Label. first parameter is instance variable object for button label second and third is for size and last is for string
	//this and drawButtonRect creat together full button
	private GLabel drawButtonText(GLabel buttonText, double rectX, double rectY, String textOFButton) {
		buttonText = new GLabel(textOFButton);
		buttonText.setColor(Color.GREEN);
		buttonText.setFont("SenSerif-Bold-20");
		add(buttonText, rectX + (BUTTON_X - buttonText.getWidth())/2, rectY + BUTTON_Y/2 + buttonText.getAscent()/2);
		return buttonText;
	}
	
	//setting button is created whith help of drawButtonRect and drawButtonLabel  
	private void settingButton() {
		settingRect = drawButtonRect(settingRect, getWidth()/2 - BUTTON_X/2, getHeight()/2 - getHeight()/8 + BUTTON_SEP);
		settingLabel = drawButtonText(settingLabel, getWidth()/2 - BUTTON_X/2, getHeight()/2 - getHeight()/8 + BUTTON_SEP, "S E T T I N G");
	}
	
	//implementation of button click sound
	private void buttonClickSound() {
		AudioClip buttonClick = MediaTools.loadAudioClip("buttonClick.au");
		buttonClick.play();
		pause(CLICK_PAUSE);
	}
	
	//implementation of setting option
	private void settingOption() {
		backgroundLevel1(WIDTH, HEIGHT); //add background again
		
		//add relevant string
		GLabel choose = new GLabel("choose ball and paddle");
		choose.setColor(Color.RED);
		choose.setFont("SenSerif-Bold-30");
		add(choose, getWidth()/2 - choose.getWidth()/2, getHeight()/2 - getHeight()/9);
		
		
		drawChooseColorObject(); //add objects which will be chosen by player 
		cancelButton(); //cancel button to return menu
		
		//while player is in setting option compiler is in blank while loop an if player clicked relevant button compiler will call mouseClicked method 
		while(cancelRect.getColor() != Color.RED) {
			
		}
		
		buttonClickSound(); //if player will click cancel button play relevant sound
		cancelRect.setColor(Color.BLUE); //when player return menu from setting option, cancel button become blue again
	}
	
	//create objects which will be chosen by player 
	private void drawChooseColorObject() {
		for(int i = 0; i < 4; i++) { //for ball objects
			double radius = getHeight()/15;
			GOval miniBall = new GOval((getWidth() - 4 * radius)/5 + i * (radius + (getWidth() - 4 * radius)/5), getHeight()/2 - radius, radius, radius);
			miniBall.setFilled(true);
			miniBall.setColor(rgen.nextColor());
			add(miniBall);
		}
		
		for(int i = 0; i < 4; i++) { //for paddle objects
			double x = 2 * getHeight()/15;
			double y = 20;
			GRect miniPaddle = new GRect((getWidth() - 4 * x)/5 + i * (x + (getWidth() - 4 * x)/5), getHeight()/2 + 1 * x , x, y);
			miniPaddle.setFilled(true);
			miniPaddle.setColor(rgen.nextColor());
			add(miniPaddle);
		}
	}
	
	//create cancel button
	private void cancelButton() {
		cancelRect = drawButtonRect(cancelRect, getWidth()/2 - BUTTON_X/2, getHeight()/2 + getHeight()/4);
		cancelLabel = drawButtonText(cancelLabel, getWidth()/2 - BUTTON_X/2, getHeight()/2 + getHeight()/4, "C a n c e l");
	}
	
	private void soundOfStarting() {
		AudioClip startSound = MediaTools.loadAudioClip("startSound.au");
		startSound.play();
	}
	
	private void removeUnnecessaryObj() {
		//delete start button
		remove(startRect);
		remove(startLabel);
		//delete setting button
		remove(settingRect);
		remove(settingLabel);
	}
	
	/*first parameter is velocity of of ball on X axis. second parameter is also velocity and third is number of life*/
	private void animation(double vx, double vy, int life) {
		int time = 1;
		restartGameButton();
		
		while(restartRect.getColor() == Color.BLUE) {
			remove(restartRect);//for second play
			remove(restartLabel);//for second play
			if(time > 1) { //for second play
				removeAll(); //remove all and add again necessary object again also some variable must be zero or return value of beginning 
				backgroundLevel1(WIDTH, HEIGHT);
				addGameBar();
				life = NTURNS;
				vx = rGenSpeedX(1.0, 3.0);
			}
			int level = 1; //allBrick is variable which has value of num of bricks of three level
			int scoreOfBricks = 0;
			AudioClip backgroundPlaying1 = MediaTools.loadAudioClip("playing1.au");
			AudioClip backgroundPlaying2 = MediaTools.loadAudioClip("playing2.au");
			
			//first level
			setupFirstLevel(life); //this method draws object before start game
			backgroundPlaying1.loop();
			life = level(vx, vy, scoreOfBricks, NBRICK_ROWS * NBRICKS_PER_ROW * level, life, level); //for first level animation
			backgroundPlaying1.stop();
			level = levelUpAnimation(level, life); //level up
			scoreOfBricks = Integer.parseInt(scoreLabelNum.getLabel());
			
			//second level
			vx = rGenSpeedX(2, 4);
			setupSecondLevel(life);
			if(life != 0) {
				backgroundPlaying2.loop();
			}
			life = level(vx, vy, scoreOfBricks, NBRICK_ROWS * NBRICKS_PER_ROW * level, life, level);
			backgroundPlaying2.stop();
			level = levelUpAnimation(level, life); //level up
			scoreOfBricks = Integer.parseInt(scoreLabelNum.getLabel());
			
			//three level
			vx = rGenSpeedX(3, 5);
			setupThirdLevel(life);
			if(life != 0) {
				backgroundPlaying1.loop();
			}
			life = level(vx, vy, scoreOfBricks, NBRICK_ROWS * NBRICKS_PER_ROW * level, life, level);
			backgroundPlaying1.stop();
			level = levelUpAnimation(level, life); //level up
			scoreOfBricks = Integer.parseInt(scoreLabelNum.getLabel());
			
			
			gameOver(life); //if life equals zero then player is looser :)
			youWon(NBRICK_ROWS * NBRICK_ROWS * (1 + 2 + 3)); //if score of brick is maximum then player is winner :)
			resultScore(scoreOfBricks); 
			restartGameButton();
			while(restartRect.getColor() != Color.RED) {
				
			}
			buttonClickSound();
			time++;
			restartRect.setColor(Color.BLUE);
		}
	}
	
	//when player will click strat button then compiler calls setup method and accordingly to this some objects which is created must be removed
	private void setupFirstLevel(int life) {
		addLifeBar(life);
		scoreText();
		countScore(0);
		add(ball); //add ball
		drawBricks(NBRICK_ROWS, NBRICKS_PER_ROW); //this method draw bricks for first level
		add(paddle); //add paddle
		pause(400); //i use pause for click sound 
		soundOfStarting();
		pause(4000); //i use pause for sound of starting 
	}
	
	//first for loop draws rows and second for loop draws column so it has two parameters, first for row, second for column
	private void drawBricks(int row, int column) {
		for(int i = 0; i < row; i++) {
			for(int j = 0; j < column; j++) {
				GRect brick = new GRect (BRICK_SEP + j * (BRICK_SEP + BRICK_WIDTH), BRICK_Y_OFFSET + i * (BRICK_HEIGHT + BRICK_SEP), BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setFillColor(fillColor(i));
				add(brick);
			}
		}
	}
	
	// this method fills brick with relevant colour
	private Color fillColor(int a) {
		Color color;
		if(a < 2) {//for first and second rows
			color = Color.RED;
			return color;
		}else if(a < 4) {//for third and fourth rows
			color = Color.ORANGE;
			return color;
		}else if(a < 6) {//for fifth and sixth rows
			color = Color.YELLOW;
			return color;
		}else if(a < 8) {//for seventh and eighth rows
			color = Color.GREEN;
			return color;
		}else {//for last rows
			color = Color.CYAN;
			return color;
		}
	}
	
	//this metod creates and draws paddle object
	private void creatPaddle() {
		paddle = new GRect (WIDTH/2 - PADDLE_WIDTH/2, HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.DARK_GRAY);
	}
	
	//this metod creates and draws ball object
	private void creatBall() {
		ball = new GOval (WIDTH/2 - BALL_RADIUS, HEIGHT/2 - BALL_RADIUS, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.PINK);
	}
	
	//This method randomly takes the velocity with respect to the X-axis
	private double rGenSpeedX(double x1, double x2) {
		double VX = rgen.nextDouble(x1, x2);
		if(rgen.nextBoolean(0.5)) {
			VX = -VX;
		}
		return VX;
	}
	
	//this method is for level designe and because of that it has five parameter. some of them will change when player moves next level
	//changing of parameter depends on difficult of levels.
	private int level(double vx, double vy, int score, int bricksInLevel, int life, int level) {
		int decreasingBrick = bricksInLevel;
		//if we want to see animation then this loop must repeat always when we have nonzero life and nonzero brick
		while(levelCondition(life, decreasingBrick)) {
			ball.setLocation(ball.getX() + vx, ball.getY() + vy);  //ball location depends on this method
			decreasingBrick = numOfBricks(decreasingBrick); //this metod check number of brick
			remove(scoreLabelNum); //delete last score and add new score,sometimes lasts score may be new score :) 
			countScore(score + (bricksInLevel - decreasingBrick)); //for counting of score
			
			//check four point around ball and if any of these points are on wall or (right or left bottom side of ball) then ball must change direction on x axis
			vx = checkObjectForSpeedX(vx);
			//check four point around ball and if any of these points are on paddle or top wall or brick then ball must change direction on y axis.
			vy = checkObjectForSpeedY(vy);
			life = checkLife(life); //this method check number of life and delete one life from life bar
			
			pause(GAME_PLAY_SPEED);
		}
		ball.setLocation(getWidth()/2 - BALL_RADIUS/2, getHeight()/2 - BALL_RADIUS);
		return life;
	}
	
	//this method draw level up animation and increase level variable by one
	private int levelUpAnimation(int level, int life) {
		if(life != 0) {
			AudioClip levelUpSound = MediaTools.loadAudioClip("levelUp.au");
			levelUpSound.play();
			
			GLabel levelUp = new GLabel("Level UP");
			levelUp.setFont("SenSerif-Bold-5");
			levelUp.setColor(Color.GREEN);
			add(levelUp, getWidth()/2 - levelUp.getWidth()/2, getHeight()/2 + levelUp.getHeight()/2);
			
			String myFont;
			//with help of this for loop level up label make animation
			for(int i = 25; i < 150; i++) {
				myFont = Integer.toString(i/5);
				levelUp.setFont("SenSerif-Bold-" + myFont);
				levelUp.setLocation(getWidth()/2 - levelUp.getWidth()/2, getHeight()/2 + levelUp.getHeight()/2 + getHeight()/4 - i);
				pause(GAME_PLAY_SPEED);
			}	
			pause(200);
			level++;
			remove(levelUp);
			levelUpSound.stop();
		}
		return level;
	}
	
	//this is created for condition of main animation
	private boolean levelCondition(int life, int numBrick) {
		if(life == 0 || numBrick == 0) {
			return false;
		}else {
			return true;
		}
	}
	
	//for calculating number of brick
	private int numOfBricks(int brick) {
		GObject colider = getCollidingObject(); //create local variable for object which will equals returned object
		if(colider != null)  {//if returned object not equals null
			if(colider != paddle && ball.getY() < HEIGHT) { //if returned object not equals paddle and ball is above game bord so ball colled brick
				brick--;
			}
		}	
		return brick;
	}
	
	//create scroe label
	private void scoreText() {
		GLabel scoreLabel = new GLabel("SCORE: ");
		scoreLabel.setFont("SenSerif-Bold-25");
		scoreLabel.setColor(Color.MAGENTA);
		add(scoreLabel, getWidth()/2 + WIDTH * 1/8, HEIGHT + getHeight()/1.5 - HEIGHT/1.5);
	}
	
	//for counting score
	private void countScore(int number) {
		int scoreNum = number;
		String scoreStr = Integer.toString(scoreNum);
		
		scoreLabelNum = new GLabel(scoreStr);
		scoreLabelNum.setFont("SenSerif-Bold-25");
		scoreLabelNum.setColor(Color.BLACK);
		add(scoreLabelNum, getWidth()/2 + WIDTH * 1/8 + getElementAt(getWidth()/2 + WIDTH * 1/8, HEIGHT + getHeight()/1.5 - HEIGHT/1.5).getWidth(), HEIGHT + getHeight()/1.5 - HEIGHT/1.5);
		
	}
	
	//this method checks four point around ball and if any of these points are on wall or (right or left bottom side of paddle) then ball must change direction on x axis
	private double checkObjectForSpeedX(double vx) {
		if(ball.getX() <= 0 || ball.getX() + 2 * BALL_RADIUS >= WIDTH) { //for right and left wall
			vx = -vx;
		}else if(getCollidingObject() == paddle && ball.getY() + 2 * BALL_RADIUS <= paddle.getY() + VY ) { //for left and right upper side of paddle
			if(ball.getX() + 2 * BALL_RADIUS <= paddle.getX() + PADDLE_WIDTH/2 && vx > 0) { //left side
				vx = -vx;
			}else if(ball.getX() > paddle.getX() + PADDLE_WIDTH/2 && vx < 0){ //right side
				vx = -vx;
			}
		}else if(getCollidingObject() == paddle && ball.getY() + 2 * BALL_RADIUS > paddle.getY() + VY) {//for left and right bottom side of paddle
			vx = -vx;
		}else if(ball.getY() >= HEIGHT) {
			vx = rgen.nextDouble(1.0, 3.0);
		}
		return vx;
	}
	
	//this method checks four point around ball and if any of these points are on paddle or top wall or brick then ball must change direction on y axis.
	private double checkObjectForSpeedY(double vy) {
		GObject colider = getCollidingObject();
		if(colider != null) { //for all except top wall
			if(colider != paddle && colider != background) { //for brick
				soundOfBrick();
				remove(colider);
				vy = -vy;
			}else if(colider != background && ball.getY() + 2 * BALL_RADIUS <= paddle.getY() + VY){ //for paddle
				vy = -vy;
			}
		}else if(ball.getY() <= 0) { //for top
			vy = -vy;
		}
		return vy;
	}
	
	//this method return object which may be on any of four points around of ball
	private GObject getCollidingObject() {
		GObject colider = null; 
		//compiler must enter if when ball is above of game board
		if(ball.getY() < HEIGHT - PADDLE_Y_OFFSET) {
			//this object will be object that will be anu of four point around of ball
			GObject colider1 = getElementAt(ball.getX(), ball.getY());
			GObject colider2 = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
			GObject colider3 = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
			GObject colider4 = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
			
			if(colider1 != null && colider1 != background) {
				colider = colider1;
			}else if(colider2 != null && colider2 != background) {
				colider = colider2;
			}else if(colider3 != null && colider3 != background) {
				colider = colider3;
			}else if(colider4 != null && colider4 != background) {
				colider = colider4;
			}
		}
		return colider;
	}
	
	private void soundOfBrick() {
		AudioClip collisionBrickSound = MediaTools.loadAudioClip("bounceBrick.au");
		collisionBrickSound.play();
	}
	
	//for checkinglife and if life is zero game will end
	private int checkLife(int life) {
		if(ball.getY() >= HEIGHT - 2 * BALL_RADIUS) {
			loseOneLife();
			ball.setLocation(WIDTH/2 - BALL_RADIUS, HEIGHT/2 - BALL_RADIUS);
			life--;
			if(life < 3) {//life < 3 because this function will be always called wenen compiler is in while loop of leveel function 
				remove(getElementAt(life * WIDTH * 1/8, HEIGHT + getHeight()/3 - HEIGHT/3 + 1)); //delete one life from life bar
			}	
		}
		return life;
	}
	
	//create life bar
	private void addLifeBar(int life) {
		GRect lifeBar = new GRect(0, HEIGHT + getHeight()/3 - HEIGHT/3, WIDTH * 3/8, getHeight()/3 - HEIGHT/3);
		lifeBar.setFilled(true);
		lifeBar.setFillColor(Color.BLACK);
		add(lifeBar);
		
		for(int i = 0; i < life; i++) {
			GRoundRect lifeIcon = new GRoundRect(i * WIDTH * 1/8, HEIGHT + getHeight()/3 - HEIGHT/3, WIDTH * 1/8, getHeight()/3 - HEIGHT/3, 10);
			lifeIcon.setFilled(true);
			lifeIcon.setFillColor(Color.RED);
			add(lifeIcon);
		}
	}
	
	private void loseOneLife() {
		AudioClip oopsSound = MediaTools.loadAudioClip("oops.au");
		oopsSound.play();
	}
	
	private void setupSecondLevel(int life) {
		if(life != 0) {
			drawBricks(NBRICK_ROWS * 2, NBRICKS_PER_ROW);
			pause(500);
			soundOfStarting();
			pause(4000); //i use pause for sound of starting
		}
	}
	
	private void setupThirdLevel(int life) {
		if(life != 0) {
			drawBricks(NBRICK_ROWS * 3, NBRICKS_PER_ROW);
			pause(500);
			soundOfStarting();
			pause(4000); //i use pause for sound of starting
		}
	}
	
	private void gameOver(int life) {
		if(life == 0) {
			GLabel over = new GLabel("Game Over");
			over.setFont("SensSerif-bold-40");
			over.setColor(Color.RED);
			add(over, WIDTH/2 - over.getWidth()/2, HEIGHT/2 - over.getHeight()/2);
			soundOfGameOver();
		}
	}
	
	private void soundOfGameOver() {
		AudioClip loseSound = MediaTools.loadAudioClip("loseSound.au");
		loseSound.play();
	}
	
	private void resultScore(int brick) {
		String numScore = Integer.toString(brick);
		GLabel resultScore = new GLabel("Your Score: ");
		resultScore.setFont("SenSerif-Bold-30");
		if(brick == NBRICK_ROWS * NBRICKS_PER_ROW) {
			resultScore.setColor(Color.GREEN);
		}else {
			resultScore.setColor(Color.RED);
		}
		add(resultScore, getWidth()/2 - resultScore.getWidth()/2, getHeight()/2 + getHeight()/5);
		
		GLabel score = new GLabel(numScore);
		score.setFont("SenSerif-Bold-30");
		if(brick == NBRICK_ROWS * NBRICKS_PER_ROW) {
			score.setColor(Color.GREEN);
		}else {
			score.setColor(Color.RED);
		}
		add(score, getWidth()/2 + resultScore.getWidth() - getHeight()/7, getHeight()/2 + getHeight()/5);
	}
	
	//if brick will equal zero player is winner 
	private void youWon(int brick) {
		if(brick == 0) {
			soundOfWinning();
			GLabel win = new GLabel("You Won");
			win.setFont("SensSerif-bold-40");
			win.setColor(Color.GREEN);
			add(win, WIDTH/2 - win.getWidth()/2, HEIGHT/2 - win.getHeight()/2);
		}
	}
	
	private void soundOfWinning() {
		AudioClip winningSound = MediaTools.loadAudioClip("winningSound.au");
		winningSound.play();
	}
	
	private void restartGameButton() {
		restartRect = drawButtonRect(restartRect, getWidth()/2 - BUTTON_X/2, getHeight()/2 - BUTTON_Y/2);
		restartLabel = drawButtonText(restartLabel, getWidth()/2 - BUTTON_X/2, getHeight()/2 - BUTTON_Y/2, "R E S T A R T");
	}
	
	//for moveing paddle and buttons
	@Override 
	public void mouseMoved(MouseEvent e) {	
		if(e.getX() <= APPLICATION_WIDTH - PADDLE_WIDTH) {
			//for paddle
			GPoint paddlePoint = new GPoint(e.getX(), HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
			paddle.setLocation(paddlePoint);
			
			if(getElementAt(e.getX(), e.getY()) == startRect || getElementAt(e.getX(), e.getY()) == startLabel) {//for start button animation
				startRect.setColor(Color.PINK);
			}else if(getElementAt(e.getX(), e.getY()) == settingRect || getElementAt(e.getX(), e.getY()) == settingLabel) {//for setting button animation
				settingRect.setColor(Color.PINK);
			}else if(getElementAt(e.getX(), e.getY()) == cancelRect || getElementAt(e.getX(), e.getY()) == cancelLabel) {//for cancel button animation
				cancelRect.setColor(Color.PINK);
				//next if else is created for becoming button blue again
			}else if(startRect.getColor() == Color.PINK && (getElementAt(e.getX(), e.getY()) != startRect || getElementAt(e.getX(), e.getY()) != startLabel)) {
				startRect.setColor(Color.BLUE);
			}else if(settingRect.getColor() == Color.PINK && (getElementAt(e.getX(), e.getY()) != settingRect || getElementAt(e.getX(), e.getY()) != settingLabel)) {
				settingRect.setColor(Color.BLUE);
			}else if(cancelRect.getColor() == Color.PINK && (getElementAt(e.getX(), e.getY()) != cancelRect || getElementAt(e.getX(), e.getY()) != cancelLabel)) {
				cancelRect.setColor(Color.BLUE);
			}
		}
	} 
	
	//for button clicked, choose colour
	@Override
	public void mouseClicked(MouseEvent e) {
		if(getElementAt(e.getX(), e.getY()) == startRect || getElementAt(e.getX(), e.getY()) == startLabel) { //for start button. become red when click
			startRect.setColor(Color.RED);
		}else if(getElementAt(e.getX(), e.getY()) == settingRect || getElementAt(e.getX(), e.getY()) == settingLabel) {//for setting button. become red when click
			settingRect.setColor(Color.RED);
		}else if(getElementAt(e.getX(), e.getY()) == restartRect || getElementAt(e.getX(), e.getY()) == restartLabel) {
			restartRect.setColor(Color.RED);
		}else if(e.getY() < getHeight()/2 + 2 * getHeight()/15 && e.getY() >= getHeight()/2 - getHeight()/15) { //for choosing colour of ball
			if(getElementAt(e.getX(), e.getY()) != null && getElementAt(e.getX(), e.getY()) != background && getElementAt(e.getX(), e.getY()) != ball && getElementAt(e.getX(), e.getY()) != scoreLabelNum) {
				ball.setColor(getElementAt(e.getX(), e.getY()).getColor());
				remove(getElementAt(e.getX(), e.getY()));
				buttonClickSound();
			}
		}else if(e.getY() >= getHeight()/2 + 2 * getHeight()/15 && e.getY() < getHeight()/2 + getHeight()/4) {//for choosing colour of paddle
			if(getElementAt(e.getX(), e.getY()) != null && getElementAt(e.getX(), e.getY()) != background && getElementAt(e.getX(), e.getY()) != ball && getElementAt(e.getX(), e.getY()) != scoreLabelNum){
				paddle.setColor(getElementAt(e.getX(), e.getY()).getColor());
				remove(getElementAt(e.getX(), e.getY()));
				buttonClickSound();
			}
		}else if(getElementAt(e.getX(), e.getY()) == cancelRect || getElementAt(e.getX(), e.getY()) == cancelLabel) {//for cancel button
			cancelRect.setColor(Color.RED);
		}
	}
}