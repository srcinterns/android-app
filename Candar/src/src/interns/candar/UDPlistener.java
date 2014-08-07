package src.interns.candar;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import android.util.Log;

public class UDPlistener extends Thread{
	private DatagramSocket socket;
	private DatagramPacket packet;
	private byte[] packetArray = new byte[6];
	private int segment;
	private int index;
	private int[] pointArray = new int[1000];
	private Listener listener;
	private boolean run = true;
	
	@Override
	public void run()
	{
		try {
			packet = new DatagramPacket(packetArray, packetArray.length);
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
				packet.setLength(packetArray.length);
				socket.receive(packet);
				Log.d("UDP listener", "Received message");
				ByteBuffer buffer = ByteBuffer.wrap(packetArray);
				segment = buffer.getInt();
				index = (int)buffer.getShort();
				pointArray[index] = -256;
				if(segment == (segment + 1))
				{
					UpdateListener(listener);
					int[] newArray = new int[1000];
					pointArray = newArray;
				}
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
			}
		}
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
		listener.update(pointArray);
	}
	
}
