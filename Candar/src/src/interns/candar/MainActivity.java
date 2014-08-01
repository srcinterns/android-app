package src.interns.candar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;

public class MainActivity extends ActionBarActivity implements Listener {
	
	ArrayList<Integer> colorArray = new ArrayList<Integer>();
	UDPlistener listener = new UDPlistener(this);
	int offset = 0;
	TextureView tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		listener.start();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextureView)findViewById(R.id.textureView1);
	}
	
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		listener.end();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void update(int[] array) {
		for(int i = 0; i < 512; i++)
		{
			colorArray.add(array[i]);
		}
		int[] colors = new int[colorArray.size()];
		colors = convertIntegers(colorArray);
		//updateOffset(colors);
		Canvas canvas = new Canvas();
		Paint paint = new Paint();
		canvas.drawBitmap(colors, offset, 512, 0, 0, 512, 1, false, paint);
		tv.draw(canvas);
	}
	
	public static int[] convertIntegers(List<Integer> integers)
	{
	    int[] ret = new int[integers.size()];
	    Iterator<Integer> iterator = integers.iterator();
	    for (int i = 0; i < ret.length; i++)
	    {
	        ret[i] = iterator.next().intValue();
	    }
	    return ret;
	}
	
	public void updateOffset(int[] array)
	{
		if(array.length >= 2048)
		{
			offset = 512; //Not correct
		}
	}
}
