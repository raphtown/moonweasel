package futurehand;
import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.atcs.moonweasel.util.Quaternion;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

/**
 * Interface with FutureHand to provide current roll,pitch,yaw to other programs
 * @author 
 *
 */
public class FutureHand	 extends Thread {
	private final BufferedReader in;
	private float[] rpy = {0f,0f,0f};
	private float recoverytime = 2;
	private float[] compweight = {0.98f,0.98f,0.98f};
	private float[] compweightvert = {1f,0.98f,1f};
	private float[] gy_offset = {0f,0f,0f};
	float last;
	private FileWriter datalog;

	final static int ROLL = 0;
	final static int PITCH = 1;
	final static int YAW = 2;

	final static int X = 0;
	final static int Y = 1;
	final static int Z = 2;

	/**
	 * @param filename to read from
	 */
	public static void main(String[] args) {
		boolean serialPort = false;
		if (args.length!=2) {
			System.err.println("Usage: FutureHand [-f infile | -p serialport]");
			System.exit(-1);
		}
		if (args[0].equals("-f"))
			serialPort=false;
		else if (args[0].equals("-p"))
			serialPort=true;
		else {
			System.err.println("Usage: FutureHand [-f infile | -p serialport]");
			System.exit(-1);
		}
		try {
			FutureHand fh = new FutureHand(serialPort,args[1]);
			@SuppressWarnings("unused")
			Displayer d = new Displayer(fh);
		} catch (Exception e) {
			System.err.println("Unable to open "+(serialPort?"serial port":"file")+": "+args[1]+" for reading.");
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public FutureHand(boolean useSerialPort, String filename) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException
	{
		if (useSerialPort) 
		{
			CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(filename);

			SerialPort port = null;
			int count = -1;
			while(port == null && count++ < 3)
			{
				try
				{
					port = (SerialPort)portId.open("serial",4000);
				}
				catch(PortInUseException e)
				{
					System.err.println("Port in use, retrying...");
					try
					{
						Thread.sleep(1000);
					} 
					catch (InterruptedException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

			port.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			in = new BufferedReader(new InputStreamReader(port.getInputStream()));
			System.out.println("Input port is ready: "+port.toString());
			// Set the port to 115,200 baud, 8 bits, 1 stop bit, no parity
		} 
		else 
		{
			File f = new File(filename);
			System.out.println("Reading from "+f.getAbsolutePath());
			in=new BufferedReader(new FileReader(f));
		}
		datalog = new FileWriter("futurehand.txt");
		start();
	}

	public void run() {
		int v[]=new int[10];

		int waserror = 0;
		// for (int loopcnt=0;loopcnt<10;loopcnt++)
		while (true) 
		{
			String line = null;
			try {
				line=in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
//				System.exit(-1);
			}
			if (line==null)
				break;
			if(waserror == 1)
			{
				waserror = 0;
				System.err.println("Next line: " + line);
			}
			String[] elements=line.split("\t");
			if (elements.length != 10) {
				System.err.println("Expected 10 elements, got "+elements.length);
				waserror = 1;
				continue;
			}
			for (int i=0;i<Math.min(elements.length,v.length);i++) {
				try {
					v[i]=Integer.parseInt(elements[i]);
				} catch (Exception e) {
					System.err.println("Error parsing "+elements[i]);
				}
				//				System.out.print(String.valueOf(v[i])+" ");
			}
			// Rescale integer values to true values
			float t=v[0] / 1000.0f;
			float ac_xyz[]={v[1] / 256.0f,v[2] / 256.0f,v[3] / 256.0f};
			float mg_xyz[]={-v[4] / 1300.0f,-v[5] / 1300.0f,v[6] / 1300.0f};
			float gy_xyz[]={((v[7] / 1023f * 5.0f) - 1.23f) * 300f * (float)Math.PI/180f,((v[8] / 1023f * 5.0f) - 1.23f) * 300f* (float)Math.PI/180f,((v[9] / 1023f * 5.0f) - 1.23f) * 300f* (float)Math.PI/180f};
			// Send the new values to the updater
			updateRPY(t,ac_xyz,mg_xyz,gy_xyz);

			try
			{
				datalog.write(t + " " 
						+ ac_xyz[0] + " " + ac_xyz[1] + " " + ac_xyz[2] + " "
						+ mg_xyz[0] + " " + mg_xyz[1] + " " + mg_xyz[2] + " "
						+ gy_xyz[0] + " " + gy_xyz[1] + " " + gy_xyz[2] + " " 
						+ rpy[0] + " " + rpy[1] + " " + rpy[2] + "\n");
				datalog.flush();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/*
	 * Update the internal estimate of roll,pitch,yaw based on the new inputs 
	 */
	private void updateRPY(float t, float ac_xyz[], float mg_xyz[], float gy_xyz[]) {
		if(last == 0)
			last = t;
		else
		{
			float dt = t - last;

			if(dt < 0 || dt > 1)
			{
				System.err.println("DT less than 0, skipping update");
				System.err.println("t: " + t + ", last: " + last);
				last = t;
				return;
			}

			float ac_xyz_total = 0;

			for(float f: ac_xyz)
			{
				ac_xyz_total = ac_xyz_total + f * f;
			}
			ac_xyz_total = (float) Math.sqrt(ac_xyz_total);

			float ac_xyz_sc[] = new float[3];
			for(int i = 0; i < ac_xyz_sc.length; i++)
			{
				ac_xyz_sc[i] = ac_xyz[i]/ac_xyz_total;
			}

			float acm_rpy[] = new float[3];

			acm_rpy[PITCH] = (float) -Math.asin(ac_xyz_sc[X]);
			acm_rpy[ROLL] = (float) -Math.atan2(-ac_xyz_sc[Y], ac_xyz_sc[Z]);


			float mg_xyz_total = 0;

			for(float f: mg_xyz)
			{
				mg_xyz_total = mg_xyz_total + f * f;
			}
			mg_xyz_total = (float) Math.sqrt(mg_xyz_total);

			float mg_xyz_sc[] = new float[3];
			for(int i = 0; i < mg_xyz_sc.length; i++)
			{
				mg_xyz_sc[i] = mg_xyz[i]/mg_xyz_total;
			}

			float s1 = (float) Math.sin(rpy[ROLL]);
			float c1 = (float) Math.cos(rpy[ROLL]);
			float s2 = (float) Math.sin(rpy[PITCH]);
			float c2 = (float) Math.cos(rpy[PITCH]);

			float[][] l2g_rp = {
					{c2,s2*s1,s2*c1},
					{0,c1,-s1},
					{-s2,c2*s1,c2*c1}};

			float mg_xyz_tc[] = new float[3];
			mg_xyz_tc = multiply3x3by3x1(l2g_rp, mg_xyz_sc);
			acm_rpy[YAW] = (float) -Math.atan2(mg_xyz_tc[Y], mg_xyz_tc[X]);

			// Correct rpy so it has no rollovers wrt acm_rpy
			// Handle rollovers when pitch exceeds +/- 90
			float[] diffrpy = subtractVectors(rpy, acm_rpy);
			float pitchCorrection = (float) (Math.round(diffrpy[ROLL]/(Math.PI))*Math.PI);  //Roll or pitch?  180 or 90?
			rpy[ROLL] = rpy[ROLL] - pitchCorrection;
			rpy[YAW] = rpy[YAW] - pitchCorrection;

			diffrpy = subtractVectors(rpy, acm_rpy);
			for(int i = 0; i < diffrpy.length; i++)
			{
				float rolloverCorrection = (float) (Math.round(diffrpy[i]/(2*Math.PI))*2*Math.PI);
				rpy[i] = rpy[i] - rolloverCorrection;
			}

			// Trig precalcs (redo in case there were rollovers)
			s1 = (float) Math.sin(rpy[ROLL]);
			c1 = (float) Math.cos(rpy[ROLL]);
			s2 = (float) Math.sin(rpy[PITCH]);
			c2 = (float) Math.cos(rpy[PITCH]);

			float[][] kin_matrix = {{1,s1*s2/c2,c1*s2/c2},{0,c1,-s1},{0,s1/c2,c1/c2}};
			float[][] kinv_matrix = {{1,0,-s2},{0,c1,s1*c2},{0,-s1,c1*c2}};

			float[] gy_delta_rpy = multiplyMatrixByScalar(multiply3x3by3x1(kin_matrix, subtractVectors(gy_xyz,gy_offset)), dt);

			if (Math.abs(acm_rpy[PITCH]) > 80*Math.PI/180)
			{
				// Almost vertical, don't use acm roll or yaw since they are meaningless
				rpy= addVectors(multiplyElements(compweightvert, addVectors(rpy, gy_delta_rpy)),
						multiplyElements(addScalarToVector(1, multiplyMatrixByScalar(compweightvert, -1)), acm_rpy));
			}
			else
			{
				rpy= addVectors(multiplyElements(compweight, addVectors(rpy, gy_delta_rpy)),
						multiplyElements(addScalarToVector(1, multiplyMatrixByScalar(compweight, -1)), acm_rpy));
			}

			float[] errg = multiply3x3by3x1(kinv_matrix, subtractVectors(rpy, acm_rpy));

			gy_offset = addVectors(gy_offset, multiplyMatrixByScalar(errg, dt/recoverytime));

			pitchCorrection = (float) (Math.round(rpy[PITCH]/Math.PI) * Math.PI);
			rpy = addScalarToVector(-pitchCorrection, rpy);

			for(int i = 0; i < rpy.length; i++)
			{
				if (rpy[i]> Math.PI)
					rpy[i]=(float)(rpy[i]-2*Math.PI);
				else if (rpy[i]<-Math.PI)
					rpy[i]=(float)(rpy[i]+2*Math.PI);
			}

			last = t;
			//			printVector("acm_rpy",acm_rpy);
			//			printVector("gy_delta_rpy",gy_delta_rpy);
			//			printVector("rpy",rpy);
			//			printVector("gy_offset",gy_offset);
		}
	}

	public static void printVector(String nm, float[] a)
	{
		System.out.println(nm+": ["+a[0]+", "+a[1]+", "+a[2]+"]");	
	}

	public static float[] multiplyMatrixByScalar(float[] a, float b)
	{
		float[] result = new float[3];
		for(int row = 0; row < a.length; row++)
		{
			result[row] = a[row] * b;
		}
		return result;
	}

	public static float[] addScalarToVector(float b, float[] a)
	{
		float[] result = new float[3];

		for(int row = 0; row < a.length; row++)
		{
			result[row] = a[row] + b;
		}
		return result;
	}

	public static float[] subtractVectors(float[] a, float[] b)
	{
		float[] result = new float[3];
		for(int row = 0; row < a.length; row++)
		{
			result[row] = a[row] - b[row];
		}
		return result;
	}

	public static float[] addVectors(float[] a, float[] b)
	{
		float[] result = new float[3];
		for(int row = 0; row < a.length; row++)
		{
			result[row] = a[row] + b[row];
		}
		return result;
	}

	public static float[] multiplyElements(float[] a, float[] b)
	{
		float[] result = new float[3];
		for(int row = 0; row < a.length; row++)
		{
			result[row] = a[row] * b[row];
		}
		return result;
	}


	public static float[] multiply3x3by3x1(float[][] a, float[] b)
	{
		float[] result = new float[3];
		for(int row = 0; row < a.length; row++)
		{
			result[row] = 0;
			for(int col = 0; col < a[0].length; col++)
			{
				result[row] = result[row] + b[col] * a[row][col];
			}
		}
		return result;
	}


	public static float[][] multiply3x3by3x3(float[][] a, float[][] b)
	{
		float[][] result = new float[3][3];
		for(int row = 0; row < a.length; row++)
		{
			for(int col = 0; col < a[0].length; col++)
			{
				result[row][col] = 0;
				for(int col2 = 0; col2 < a[0].length; col2++)
				{
					result[row][col] = result[row][col] + b[col2][col] * a[row][col2];
				}
			}
		}
		return result;
	}



	public float[][] rx(float theta)
	{
		float[][] result = {{1,0,0},
				{0,(float)(Math.cos(theta)),(float)(Math.sin(theta))},
				{0,(float)(-Math.sin(theta)),(float)(Math.cos(theta))}};
		return result;
	}

	public float[][] ry(float theta)
	{
		float[][] result = {{(float)(Math.cos(theta)),0,(float)(-Math.sin(theta))},
				{0,1,0},
				{(float)(Math.sin(theta)),0,(float)(Math.cos(theta))}};
		return result;
	}

	public float[][] rz(float theta)
	{
		float[][] result = {{(float)(Math.cos(theta)),(float)(Math.sin(theta)),0},
				{(float)(-Math.sin(theta)),(float)(Math.cos(theta)),0},
				{0,0,1}};
		return result;
	}

	/*
	 * Retrieve the current estimate of roll, pitch, yaw
	 */
	public float[] getRPY() {
		return rpy.clone();
	}

	public Quaternion getQuaternion()
	{
		float[] c=new float[3]; 
		float[] s=new float[3];
		int[] remap={1,2,0};
		for (int i=0;i<3;i++) {
			c[i]=(float)Math.cos(rpy[remap[i]]/2);
			s[i]=(float)Math.sin(rpy[remap[i]]/2);
		}

		s[0]=-s[0];  // Flip pitch direction
		Quaternion q=new Quaternion(
				c[0]*c[1]*c[2]+s[0]*s[1]*s[2],
				s[0]*c[1]*c[2]-c[0]*s[1]*s[2],
				c[0]*s[1]*c[2]+s[0]*c[1]*s[2],
				c[0]*c[1]*s[2]-s[0]*s[1]*c[2]);
		return q;
	}

}
