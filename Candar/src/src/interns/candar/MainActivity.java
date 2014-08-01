package src.interns.candar;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Gravity;
import android.view.TextureView;
import android.widget.FrameLayout;

<<<<<<< HEAD
import java.util.Arrays;

=======
>>>>>>> e992ae6bb6dd87ba7c62ffd0ad0cae8b294e9335

@SuppressWarnings({"UnusedDeclaration"})
public class MainActivity extends Activity implements TextureView.SurfaceTextureListener {
    private TextureView mTextureView;
    private MainActivity.RenderingThread mThread;
    private UDPlistener mUdp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout content = new FrameLayout(this);

        mTextureView = new TextureView(this);
        mTextureView.setSurfaceTextureListener(this);
        mTextureView.setOpaque(false);

        content.addView(mTextureView, new FrameLayout.LayoutParams(500, 800, Gravity.CENTER));
        setContentView(content);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mThread = new RenderingThread(mTextureView);
        mUdp = new UDPlistener(mThread);
        mThread.start();
        mUdp.start();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Ignored
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mThread != null) mThread.stopRendering();
        mUdp.end();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Ignored
    }

    private static class RenderingThread extends Thread implements Listener {
        private final TextureView mSurface;
        private volatile boolean mRunning = true;
<<<<<<< HEAD
        private RotatingArray data = new RotatingArray(512, 800, 10); // TODO: Replace these with width/height constants
=======
        private int[] colors;
        private List<Integer> colorArray = new ArrayList<Integer>();
>>>>>>> e992ae6bb6dd87ba7c62ffd0ad0cae8b294e9335

        public RenderingThread(TextureView surface) {
            mSurface = surface;
        }

        @Override
<<<<<<< HEAD
        public void update(int[] colorArray) {
            data.append(colorArray);
=======
        public void update(int[] array) {
        	for(int i = 0; i < 512; i++)
    		{
    			colorArray.add(array[i]);
    		}
    		colors = convertIntegers(colorArray);
    		Log.d("Color", "" + colors[0]);
>>>>>>> e992ae6bb6dd87ba7c62ffd0ad0cae8b294e9335
        }
        
        public static int[] convertIntegers(List<Integer> integers)
    	{
    	    int[] ret = new int[integers.size()*300];
    	    Iterator<Integer> iterator = integers.iterator();
    	    for (int i = 0; i < ret.length; i++)
    	    {
    	    	if(iterator.hasNext())
    	    	{
    	    		ret[i] = iterator.next().intValue();
    	    	}
    	    }
    	    return ret;
    	}
        
        
        
        
        @Override
        public void run() {

            while (mRunning && !Thread.interrupted()) {
                final Canvas canvas = mSurface.lockCanvas(null);
                try {
<<<<<<< HEAD
                    int[] moo = data.getArray();
                    canvas.drawBitmap(moo, data.getOffset(), 512, 0, 0, 512, 800, false, null);
=======
                    if (null == colors) continue;
                    canvas.drawBitmap(colors, 0, 512, 0, 0, 512, 300, false, null);
        
>>>>>>> e992ae6bb6dd87ba7c62ffd0ad0cae8b294e9335
                } catch (Exception e) {
                    Log.e("RenderThread", "Exception: " + e);
                } finally {
                    mSurface.unlockCanvasAndPost(canvas);
                }

                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    // Interrupted
                }
            }
        }

        void stopRendering() {
            interrupt();
            mRunning = false;
        }

    }
}

