package src.interns.candar;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

import android.graphics.Color;
import android.util.Log;

public class UDPlistener extends Thread{
	private DatagramSocket socket;
	private byte[] bytes = new byte[518];
	private DatagramPacket packet;
	private long timeStamp;
	private byte[] array = new byte[512];
	private int[] colorArray = new int[512];
    private final int COLOR_MIN = 0xFF0000FF;
    private final int COLOR_MAX = 0xFFFF0000;
    private final int COLOR_MAP = COLOR_MAX - COLOR_MIN;
	Listener listener;
	boolean run = true;
	
	@Override
	public void run()
	{
		try {
			packet = new DatagramPacket(bytes, bytes.length);
			socket = new DatagramSocket(18888);
			socket.setBroadcast(true);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(run)
		{
			try
			{
				packet.setLength(bytes.length);
				socket.receive(packet);
				Log.d("UDP listener", "Received message");
				ByteBuffer buffer = ByteBuffer.wrap(bytes);
				buffer.get();
				int timeInt = buffer.getInt();
				timeStamp = (long)(timeInt & 0x00000000FFFFFFFF);
				removeBytesFromStart(buffer, 5);
				array = buffer.array();
				byteToInt(array);				
				UpdateListener(listener);
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	
	public void removeBytesFromStart(ByteBuffer bf, int n) {
		int index = 0;
	    for(int i = n; i < bf.limit(); i++) {
	        bf.put(index++, bf.get(i));
	        bf.put(i, (byte)0);
	    }
	    bf.position(bf.position()-n);
	}
	
	
	public UDPlistener(Listener listener)
	{
		super();
		this.listener = listener;
	}
	
	public void end()
	{
		run = false;
		socket.close();
	}
	
	public void UpdateListener(Listener listener)
	{
		listener.update(colorArray);
	}
	
	public void byteToInt(byte[] array)
	{
		for(int i = 0; i < 512; i++)
		{
			colorArray[i] = (array[i] * (Color.parseColor("blue") - Color.parseColor("black"))) + Color.parseColor("black");
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
