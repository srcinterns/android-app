package src.interns.candar;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class UDPlistener extends Thread{
	private DatagramSocket socket;
	private byte[] bytes = new byte[518];
	private DatagramPacket packet;
	private long timeStamp;
	private byte[] array = new byte[512];
	private int[] colorArray = new int[512];
	private final int COLOR_MAP = -16711425;
	private final int COLOR_MIN = -65536;
	Listener listener;
	
	@Override
	public void run()
	{
		try {
			packet = new DatagramPacket(bytes, bytes.length);
			socket = new DatagramSocket(12345);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(isAlive())
		{
			try
			{
				packet.setLength(bytes.length);
				socket.receive(packet);
				ByteBuffer buffer = ByteBuffer.wrap(bytes);
				buffer.get();
				int timeInt = buffer.getInt();
				timeStamp = (long)timeInt;
				array = buffer.array();
				byteToInt(array);
				UpdateListener(listener);
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				socket.close();
			}
		}
	}
		
	public void UpdateListener(Listener listener)
	{
		listener.update(colorArray);
	}
	
	public void byteToInt(byte[] array)
	{
		for(int i = 0; i < array.length; i++)
		{
			colorArray[i] = (array[i] * (COLOR_MAP)) + COLOR_MIN;
		}
		
	}
	
	public long getTimeStamp()
	{
		return timeStamp;
	}
	
	public int[] getColorArray()
	{
		return colorArray;
	}

}
