import java.io.*;


import java.awt.*;
import javax.swing.*;

public class MNISTReader extends JFrame {

    //just some random values to generate random numbers with custom random generator
	static int m_z=12345,m_w=45678;

	//number of neurons; 28 corresponds the number of pixels of each image, so 784 in total for each layer
    //plus 10 neurons for the labels 0-9
	static final int NEURONS=28*28+10;
	//how many images of the database should we use and load?
	static final int MAX_PATTERNS=1000;

	//the following are not important for OUR code, only for already given methods
	//number of labels
	int numLabels;
	//number of images
	int numImages;
	//number of rows
	int numRows;
	//number of Columns
	int numCols;	

	//list of the labels
	double trainLabel[] = new double[MAX_PATTERNS];
	//list of the images together with their label
	double trainImage[][] = new double[MAX_PATTERNS][28*28];
	//the weigths for the neural network; it's 2D, because every input neuron has a weight for each output neuron -> 794*794
	double weights[][] = new double[NEURONS][NEURONS];
	//the output/hidden layer
	double output[] = new double[NEURONS];
	//the input layer
	double input[] = new double[NEURONS];
	//the reconstructed layer
	double reconstructed_input[] = new double[NEURONS];

    /**
     * custom random numbers generator
     * not necessary to understand
     * @return a random integer
     */
	int randomGen()
	{
	    m_z = Math.abs(36969 * (m_z & 65535) + (m_z >> 16));
	    m_w = Math.abs(18000 * (m_w & 65535) + (m_w >> 16));
	    return Math.abs((m_z << 16) + m_w);
	}

    /**
     * method to show the pixels of the images in the layers in the JFrame window
     * not necessary to understand
     * @param g
     */
	public void paint(Graphics g) {
		int i=0;
		for (int colIdx = 0; colIdx < 28; colIdx++) {
			for (int rowIdx = 0; rowIdx < 28; rowIdx++) {
				int c = (int) (input[i++]);
				if (c > 0.0) {
					g.setColor(Color.blue);
				} else {
					g.setColor(Color.black);
				}

				g.fillRect(10 + rowIdx * 10, 10 + colIdx * 10, 8, 8);
			}
		}
		for (int t=0;t<10;t++){
			int c = (int) (input[i++]);
			if (c > 0.0) {
				g.setColor(Color.blue);
			} else {
				g.setColor(Color.black);
			}
			g.fillRect(10 + t * 10, 10 + 28 * 10, 8, 8);
		}
		i=0;
		for (int colIdx = 0; colIdx < 28; colIdx++) {
			for (int rowIdx = 0; rowIdx < 28; rowIdx++) {
				int c = (int) (output[i++]+0.5);
				if (c > 0.0) {
					g.setColor(Color.blue);
				} else {
					g.setColor(Color.black);
				}

				g.fillRect(300+10 + rowIdx * 10,10 + colIdx * 10, 8, 8);
			}
		}
		for (int t=0;t<10;t++){
			int c = (int) (output[i++]+0.5);
			if (c > 0.0) {
				g.setColor(Color.blue);
			} else {
				g.setColor(Color.black);
			}
			g.fillRect(300+10 + t * 10, 10 + 28 * 10, 8, 8);
		}
		i=0;
		for (int colIdx = 0; colIdx < 28; colIdx++) {
			for (int rowIdx = 0; rowIdx < 28; rowIdx++) {
				int c = (int) (reconstructed_input[i++]+0.5);
				if (c > 0.0) {
					g.setColor(Color.blue);
				} else {
					g.setColor(Color.black);
				}

				g.fillRect(600+10 + rowIdx * 10,10 + colIdx * 10, 8, 8);
			}
		}
		for (int t=0;t<10;t++){
			int c = (int) (reconstructed_input[i++]+0.5);
			if (c > 0.0) {
				g.setColor(Color.blue);
			} else {
				g.setColor(Color.black);
			}
			g.fillRect(600+10 + t * 10, 10 + 28 * 10, 8, 8);
		}

	}

	/**
     * main method to first train the network and then test the network
	 * @param args
	 *            args[0]: label file; args[1]: data file. <--not my comment, looks deprecated
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException,
			InterruptedException {

		MNISTReader frame = new MNISTReader();

		frame.readMnistDatabase();
		frame.setSize(900, 350);
		System.out.println("Learning step:");
		frame.trainOrTestNet(true,10000,frame); //trains the network <maxcount> amount of times/images

		System.out.println("Teststep:");
		frame.trainOrTestNet(false,1000,frame); // tests the network with <maxcount> amount of images
		System.exit(0);
	}

    /**
     * initiates the starting weights for the network
     * not primarily necessary to understand
     * @param weights
     */
	public void init(double weights[][]){
		for (int t=0;t<NEURONS;t++){
			for (int neuron=0; neuron<NEURONS; neuron++){
				weights[neuron][t]=randomGen()%2000/1000.0-1.0; //random double values between 1 and -1
			}
		}
	}

    /**
     * calculates the hidden/output layer
     * this is our code
     * @param in input
     * @param w weights
     * @param out output/hidden
     */
	public void activateForward(double in[], double w[][], double out[]){

		// insert code here
        for (int outputIndex = 0; outputIndex < NEURONS; outputIndex++) {
		    for (int inputIndex = 0; inputIndex < NEURONS; inputIndex++) {
                out[outputIndex] = out[outputIndex] + (in[inputIndex] * w[inputIndex][outputIndex]); //calculate activity of input neurons
            }
            //sigmoid transfer function
            //out[outputIndex] = 1 / (1 + Math.exp(out[outputIndex] + in[0]));
			//out[outputIndex] = Math.round(out[outputIndex]);

		    //threshold transfer function
            double threshold = 0;
            if (out[outputIndex] < threshold){
                out[outputIndex] = 0;
            } else {
                out[outputIndex] = 1;
            }
        }
        output = out;
	}

    /**
     * performs the reconstruction from the output/hidden layer
     * same method as activateForward, but output acts as input
     * this is our code
     * @param rec reconstructed layer
     * @param w weigths
     * @param out output/hidden
     */
	public void activateReconstruction(double rec[], double w[][], double out[]){
		
		// insert code here

        for (int recIndex = 0; recIndex < NEURONS; recIndex++) {
            for (int outputIndex = 0; outputIndex < NEURONS; outputIndex++) {
                rec[recIndex] = rec[recIndex] + (out[outputIndex] * w[recIndex][outputIndex]); //calculate activity of input neurons
            }
            //sigmoid transfer function
            //rec[recIndex] = 1 / (1 + Math.exp(rec[recIndex] + out[0]));
            //rec[recIndex] = Math.round(rec[recIndex]);

		    //threshold transfer function
            double threshold = 0;
            if (rec[recIndex] < threshold){
                rec[recIndex] = 0;
            } else {
                rec[recIndex] = 1;
            }
        }
        reconstructed_input = rec;
		
	}

    /**
     * performs training of the weights to optimize the network
     * this is our code
     * @param in input layer
     * @param out output/hidden layer
     * @param rec reconstructed layer
     * @param w weigths
     */
	public void contrastiveDivergence(double in[], double out[], double rec[], double w[][])
	{
		// insert code here
        double learningRate = 1;
        for (int i = 0; i < NEURONS; i++) {
            for (int j = 0; j < NEURONS; j++) {
                w[i][j] += learningRate * (out[j] * in[i] - out[j] * rec[i]); //Delta Lernregel Folie 89
            }
        }

        weights = w;
	}

    /**
     * either trains the network or tests it
     * this is not our code, but adjusted it a bit
     * @param train true - train the network (or basically the weights) with some images
     *              false - test the network for real without adjusting the weights
     * @param maxCount number of cycles to perform the input-output-reconstruction
     * @param frame the JFrame to show the images
     */
	void trainOrTestNet(boolean train, int maxCount, MNISTReader frame){
		int correct = 0;

		//pick random weights to begin with
		if (train){
			init(weights);
		}
		int pattern=0;
	    for (int count=1; count<maxCount; count++){
			// --- training phase

			for (int t=0;t<NEURONS-10;t++){
				input[t]=trainImage[pattern%(MAX_PATTERNS/10)][t]; // initialize original pattern
			}
			for (int t=NEURONS-10;t<NEURONS;t++){
				input[t]=0; //clearing previous labels
			}
			if (train){
				// --- use the label also as input!
				// the labels are saved in the last ten elements in the input array
                // label 1 = NEURONS-10+1; label 2 = NEURONS-10+2, etc.
				if (trainLabel[pattern%(MAX_PATTERNS/10)]>=0 && trainLabel[pattern%(MAX_PATTERNS/10)]<10){
					input[NEURONS-10+(int)trainLabel[pattern%(MAX_PATTERNS/10)]] = 1.0;
				}
			}

			//drawActivity(0,0,input,red,green,blue); <--was already there, just ignore

			// --- Contrastive divergence
			// Activation
			input[0] = 1;					// bias neuron!
			activateForward(input,weights,output); // positive Phase
			output[0] = 1;					// bias neuron!

			//drawActivity(300,0,output,red,green,blue); <--was already there, just ignore

			activateReconstruction(reconstructed_input,weights,output); // negative phase/ reconstruction

			//drawActivity(600,0,reconstructed_input,red,green,blue); <--was already there, just ignore
			if (train){
				contrastiveDivergence(input,output,reconstructed_input,weights);
			}

			//print sample and recognition rate every 111th time
			if (count%111==0){
				System.out.println("Zahl: "+ (int)trainLabel[pattern%(MAX_PATTERNS/10)]);
                System.out.println("Trainingsmuster: "+count+"                 Erkennungsrate: "+(float)(correct)/(float)(count)*100 + " %");

                //show the sample (input, output, reconstruction) in the frame
				frame.validate();
				frame.setVisible(true);
				frame.repaint();
				try {
				    Thread.sleep(200);                 //20 milliseconds is one second.
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
			}

			//if (!train) {
                int number = 0;
			    //get reconstructed label
                for (int t = NEURONS - 10; t < NEURONS; t++) {
                    if (reconstructed_input[t] > reconstructed_input[NEURONS - 10 + number]) {
                        number = t - (NEURONS - 10);
                    }
                }

                //check is reconstructed label is target label
                if (frame.trainLabel[pattern%(MAX_PATTERNS/10)] == number) {
                    if (!train)
                        System.out.println("Muster: " + frame.trainLabel[pattern % (MAX_PATTERNS/10)] + ", Erkannt: " + number + " KORREKT!!!\n");
                    correct++;
                } else if (!train) {
                    System.out.println("Muster: " + frame.trainLabel[pattern % (MAX_PATTERNS/10)] + ", Erkannt: " + number);
                }
            //}
			pattern++;
		}
	}

    /**
     * read in the images from the mnist database
     * not necessary to understand
     * @throws IOException
     */
	public void readMnistDatabase() throws IOException {
	{
			DataInputStream labels = new DataInputStream(new FileInputStream(
					"train-labels-idx1-ubyte"));
			DataInputStream images = new DataInputStream(new FileInputStream(
					"train-images-idx3-ubyte"));
			int magicNumber = labels.readInt();
			if (magicNumber != 2049) {
				System.err.println("Label file has wrong magic number: "
						+ magicNumber + " (should be 2049)");
				System.exit(0);
			}
			magicNumber = images.readInt();
			if (magicNumber != 2051) {
				System.err.println("Image file has wrong magic number: "
						+ magicNumber + " (should be 2051)");
				System.exit(0);
			}
			numLabels = labels.readInt();
			numImages = images.readInt();
			numRows = images.readInt();
			numCols = images.readInt();

			long start = System.currentTimeMillis();
			int numLabelsRead = 0;
			int numImagesRead = 0;

			while (labels.available() > 0 && numLabelsRead < MAX_PATTERNS) {// numLabels

				byte label = labels.readByte();
				numLabelsRead++;
				trainLabel[numImagesRead]=label;
				double pos = 0, neg = 0;
				int i=0;
				for (int colIdx = 0; colIdx < numCols; colIdx++) {
					for (int rowIdx = 0; rowIdx < numRows; rowIdx++) {
						if (images.readUnsignedByte() > 0) {
							trainImage[numImagesRead][i++] = 1.0;
						} else {
							trainImage[numImagesRead][i++] = 0;
						}

					}
				}

				numImagesRead++;

				// At this point, 'label' and 'image' agree and you can do
				// whatever you like with them.

				if (numLabelsRead % 10 == 0) {
					System.out.print(".");
				}
				if ((numLabelsRead % 800) == 0) {
					System.out.print(" " + numLabelsRead + " / " + numLabels);
					long end = System.currentTimeMillis();
					long elapsed = end - start;
					long minutes = elapsed / (1000 * 60);
					long seconds = (elapsed / 1000) - (minutes * 60);
					System.out
							.println("  " + minutes + " m " + seconds + " s ");

				}

			}

			System.out.println();
			long end = System.currentTimeMillis();
			long elapsed = end - start;
			long minutes = elapsed / (1000 * 60);
			long seconds = (elapsed / 1000) - (minutes * 60);
			System.out.println("Read " + numLabelsRead + " samples in "
					+ minutes + " m " + seconds + " s ");

			labels.close();
			images.close();

		}

	}
	/*
	  public static void writeFile(String fileName, byte[] buf)
	    {
			
			FileOutputStream fos = null;
			
			try
			{
			   fos = new FileOutputStream(fileName);
			   fos.write(buf);
			}
			catch(IOException ex)
			{
			   System.out.println(ex);
			}
			finally
			{
			   if(fos!=null)
			      try
			      {
			         fos.close();
			      }
			      catch(Exception ex)
			      {
			      }
			}
	    }
*/
}
