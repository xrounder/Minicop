import java.awt.Color;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;

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
	int milliseconds = 0;

	// Frame will display when true
	public static boolean display = false;

	//The max. positions of the ball, paddle and velocity
	int maxXBall = 10;
	int maxYBall = 10;
	int maxXV = 2;
	int maxYV = 2;
	int maxXPaddle = 10;
	int numActions = 3;

	//Matrix with Q-Values for each state and action
	public double[][] Q = new double[maxXBall*maxYBall* maxXPaddle * maxXV * maxYV][numActions];

	int counter = 0;
	int max_Counter = 100000;

	int reward = 0;
	int state = 0;
	int action = 0;

	/**
	 * Here we start the program
	 */
	public void run() {
		//Starting points of the ball, the paddle and the ball's velocity
		int xBall=5;
		int yBall=6;
		int xPaddle=5;
		int xV=1;
		int yV=1;

		while (!stop) {
			if(display) {
				inputOutput.fillRect(0,0,imageWidth, imageHeight, Color.black);
				inputOutput.fillRect(xBall*30, yBall*30, 30, 30, Color.green);
				inputOutput.fillRect(xPaddle*30, 11*30+20, 90, 10, Color.orange);
			}

			//Now we get the state the agent is in within the environment
			int currentState = getState(xBall, yBall, xPaddle, xV, yV);

			//Determine the action to take
			int action = calcQLearning(currentState, reward);

			//Move the paddle according to action
			if (action==1){
				xPaddle--; //left
			}
			if (action==2){
				xPaddle++; //right
			}
			if (action==0){
				//stay and do nothing
			}

			//We must not let the paddle go out of bounds
			if (xPaddle<0){
				xPaddle=0;
			}
			if (xPaddle>10){
				xPaddle=10;
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
				if (xPaddle==xBall || xPaddle==xBall-1 || xPaddle==xBall-2){ // Paddle is three units long
					reward = 1;

					if(counter >= max_Counter){
						System.out.println("Positive reward.");
					}
				}
				else{
					if(counter >= max_Counter){
						System.out.println("Negative reward.");
					}

					reward = -1;
				}
			} else {
				reward = 0;
			}

			//This is just to adjust the speed
			try {
				Thread.sleep(milliseconds);                 //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}

			//Wait until counter is reached before drawing anything
			if(counter >= max_Counter){
				milliseconds = 100;
				display = true;
				counter = 0;
			}else{
				counter++;
			}

			if(display) {
				System.out.println("Q: " + this.Q[currentState][action]);
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
	 * @param xPaddle Position of the paddle on the X-Axis
	 * @param xV Velocity along the X-Axis
	 * @param yV Velocity along the Y-Axis
	 * @return The calculated state
	 */
	public int getState(int xBall, int yBall, int xPaddle, int xV, int yV){
		xV += 1;
		yV += 1;

		return (xBall + yBall * maxYBall + xPaddle * (maxYBall * maxXPaddle)
				+ xV * (maxYBall * maxXPaddle * maxXV) + yV *(maxYBall * maxXPaddle * maxXV * maxYV) );
	}

	/**
	 * Selects an action based on learned Q-Values
	 * @param Q
	 * @return The best possible action
	 */
	public int selectBestAction(double[] Q) {
		int bestAction = 0;
		Double highestValue = null;

		// Search the highest computed Q highestValue in order to determine action
		for(int i = 0; i < Q.length; i++) {
			if(highestValue == null) {
				highestValue = Q[i];
				bestAction = i;
			} else if(Q[i] > highestValue) {
					highestValue = Q[i];
					bestAction = i;
			}
		}
		return bestAction;
	}

	/**
	 * The Q-Learning aspect of the algorithm.
	 *
	 * @param state The new state
	 * @param reward The new reward
	 * @return The next action to take
	 */
	public int calcQLearning(int state, int reward) {
		int nextAction = selectBestAction(Q[state]);

		//Q-Learning update formula
		Q[this.state][action] += 0.5 * (reward + 0.9 * Q[state][nextAction] - Q[this.state][action]); //Slide 64

		//Save the state and action for next iteration
		this.state = state;
		action = nextAction;

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
