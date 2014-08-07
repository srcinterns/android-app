package src.interns.candar;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import android.util.Log;

public class UDPlistener extends Thread{
	private DatagramSocket socket;
	public int current_segment = -1;
	private int index;
	private int[] pointArray = new int[2000];
	private Listener listener;
	private boolean run = true;
	
	@Override
	public void run()
	{
        int segment;
        DatagramPacket packet;
		try {
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
                packet = new DatagramPacket(new byte[6], 6);
				socket.receive(packet);
				Log.d("UDP listener", "Received message");
				ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
				segment = buffer.getInt();
				index = (int) buffer.getShort() & 0x0000FFFF;
                if ((index >= 2000) || (index < 0)) {
                    Log.w("UDP", "Index out of bounds! Received:" + index);
                    continue;
                }
                pointArray[index] = -256;

                if(this.current_segment < segment)
				{
                    this.current_segment = segment;
					UpdateListener(listener);
					pointArray = new int[2000];
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
