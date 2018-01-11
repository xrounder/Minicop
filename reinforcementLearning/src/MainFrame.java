import java.awt.Color;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.util.Random;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	//<editor-fold desc="Not important stuff">
	public static final int	imageWidth		= 360;
	public static final int	imageHeight		= 360;
	public InputOutput		inputOutput		= new InputOutput(this);
	public boolean			stop			= false;
	ImagePanel				canvas			= new ImagePanel();
	ImageObserver			imo				= null;
	Image					renderTarget	= null;
	public int mousex,mousey,mousek;
	public int key;
	//</editor-fold>

	/**
	 * Here the frame is built and configured.
	 *
	 * @param args
	 */
	public MainFrame(String[] args) {
		super("PingPong");

		getContentPane().setSize(imageWidth, imageHeight);
		setSize(imageWidth + 50, imageHeight + 50);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

		canvas.img = createImage(imageWidth, imageHeight);

		add(canvas);

		run();
	}

	// For speed adjustments
	int miliseconds = 0;

	// Frame will display when true
	public static boolean display = false;

	//The max. positions of the ball, paddle and velocity
	int maxXBall = 10;
	int maxYBall = 10;
	int maxXSchlaeger = 10;
	int maxXVel = 2;
	int maxYVel = 2;

	//Matrix with Q-Values for each state and action
	public double[][] Q = new double[maxXBall*maxYBall*maxXSchlaeger*maxXVel*maxYVel][2];

	int counter = 0;
	int max_Counter = 100000;

	int lastreward = 0;
	int lastState = 0;
	int lastAction = 0;

	/**
	 * Here we start the program
	 */
	public void run() {
		//Statring points of the ball, the paddle and the ball's velocity
		int xBall=5, yBall=6, xSchlaeger=5, xV=1, yV=1;

		while (!stop) {
			if(display) {
				inputOutput.fillRect(0,0,imageWidth, imageHeight, Color.black);
				inputOutput.fillRect(xBall*30, yBall*30, 30, 30, Color.green);
				inputOutput.fillRect(xSchlaeger*30, 11*30+20, 90, 10, Color.orange);
			}

			//Now we get the state the agent is in within the environment
			int s = getState(xBall, yBall, xSchlaeger, xV, yV);

			//Determine the action to take
			int action = qLearning(s, lastreward);

			//Move the paddle according to action
			if (action==0){
				xSchlaeger--;
			}
			if (action==1){
				xSchlaeger++;
			}

			//We must not let the paddle go out of bounds
			if (xSchlaeger<0){
				xSchlaeger=0;
			}
			if (xSchlaeger>10){
				xSchlaeger=10;
			}

			//Here we move the ball according to the set velocity one unit on the X- and Y-Axis
			xBall+=xV;
			yBall+=yV;

			//If the ball hits a wall it must bounce into the opposite direction regarding the axis
			if (xBall>9 || xBall<1){
				xV=-xV;
			}
			if (yBall>10 || yBall<1){
				yV=-yV;
			}

			// Reward time:
			// +1 for a hit
			// -1 for a miss
			// 0 for nothing
			if (yBall==11){
				if (xSchlaeger==xBall || xSchlaeger==xBall-1 || xSchlaeger==xBall-2){ // Paddle is three units long
					lastreward = 1;

					if(counter >= max_Counter){
						System.out.println("Positive reward.");
					}
				}
				else{
					if(counter >= max_Counter){
						System.out.println("Negative reward.");
					}

					lastreward = -1;
				}
			} else {
				lastreward = 0;
			}


			//This is just to adjust the speed
			try {
				Thread.sleep(miliseconds);                 //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}

			//Wait until counter is reached bevore drawing anything
			if(counter >= max_Counter){
				miliseconds = 100;
				display = true;
				counter = 0;
			}else{
				counter++;
			}

			if(display) {
				//System.out.println(this.Q[s][action]);
				repaint();
				validate();
			}
		}
	}

	/**
	 * Returns a calculated state. A state consists of the position of the ball(x and y), the paddle (only x) and the velocity of the ball.
	 *
	 * @param xBall Position of the ball on the X-Axis
	 * @param yBall Position of the ball on the Y-Axis
	 * @param xSchlaeger Position of the paddle on the X-Axis
	 * @param xV Velocity along the X-Axis
	 * @param yV Velocity along the Y-Axis
	 * @return The calculated state
	 */
	public int getState(int xBall, int yBall, int xSchlaeger, int xV, int yV){
		xV += 1;
		yV += 1;

		return (xBall + yBall * maxYBall + xSchlaeger * (maxYBall * maxXSchlaeger)
				+ xV * (maxYBall *maxXSchlaeger * maxXVel) + yV *(maxYBall *maxXSchlaeger * maxXVel*maxYVel) );
	}

	/**
	 * Selects an action based on learned Q-Values
	 * @param Q
	 * @return The action
	 */
	public int selectAction(double[] Q) {
		int action = 0;
		Double value = null;

		// Search the highest computed Q value in order to determine action
		for(int i = 0; i < Q.length; i++) {
			if(value == null) {
				value = Q[i];
				action = i;
			} else {
				if(Q[i] > value) {
					value = Q[i];
					action = i;
				}
			}
		}
		return action;
	}

	/**
	 * The Q-Learning aspect of the algorithm.
	 *
	 * @param state The new state
	 * @param reward The new reward
	 * @return The next action to take
	 */
	public int qLearning(int state, int reward) {
		int nextAction = selectAction(Q[state]);
		if(nextAction > 1) {
			nextAction = 0;
		}

		//Q-Learning update formula
		Q[lastState][lastAction] += 0.5 * (reward + 0.5 * this.Q[state][nextAction] - this.Q[lastState][lastAction]); //Slide 64

		//Save the state and action for next iteration
		lastState = state;
		lastAction = nextAction;

		return nextAction;
	}

	//<editor-fold desc="Not important stuff">
	/**
	 * Not important!
	 * @param e
	 */
	public void mouseReleased(MouseEvent e) {
		mousex = e.getX();
		mousey = e.getY();
		mousek = e.getButton();
	}

	/**
	 * Not important!
	 * @param e
	 */
	public void mousePressed(MouseEvent e) {
		mousex = e.getX();
		mousey = e.getY();
		mousek = e.getButton();
	}

	/**
	 * Not important!
	 * @param e
	 */
	public void mouseExited(MouseEvent e) {
		mousex = e.getX();
		mousey = e.getY();
		mousek = e.getButton();
	}

	/**
	 * Not important!
	 * @param e
	 */
	public void mouseEntered(MouseEvent e) {
		mousex = e.getX();
		mousey = e.getY();
		mousek = e.getButton();
	}

	/**
	 * Not important!
	 * @param e
	 */
	public void mouseClicked(MouseEvent e) {
		mousex = e.getX();
		mousey = e.getY();
		mousek = e.getButton();
	}

	/**
	 * Not important!
	 * @param e
	 */
	public void mouseMoved(MouseEvent e) {
		// System.out.println(e.toString());
		mousex = e.getX();
		mousey = e.getY();
		mousek = e.getButton();
	}

	/**
	 * Not important!
	 * @param e
	 */
	public void mouseDragged(MouseEvent e) {
		mousex = e.getX();
		mousey = e.getY();
		mousek = e.getButton();
	}

	/**
	 * Not important!
	 * @param e
	 */
	public void keyTyped(KeyEvent e) {
		key = e.getKeyCode();
	}

	/**
	 * Not important!
	 * @param e
	 */
	public void keyReleased(KeyEvent e) {
		key = e.getKeyCode();
	}

	/**
	 * Not important!
	 * @param e
	 */
	public void keyPressed(KeyEvent e) {
		System.out.println(e.toString());
	}
	//</editor-fold>

	/**
	 * Construct main frame
	 *
	 * @param args
	 *            passed to MainFrame
	 */
	public static void main(String[] args) {
		new MainFrame(args);
	}
}
