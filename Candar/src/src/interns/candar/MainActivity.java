package src.interns.candar;


import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends Activity implements TextureView.SurfaceTextureListener {
    private TextureView mTextureView;
    private MainActivity.RenderingThread mThread;
    private UDPlistener mUdp;
    private static int height;
    private static int width;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout content = new LinearLayout(this);
        final HorizontalScrollView hview = new HorizontalScrollView(this);
        LinearLayout linLay = new LinearLayout(this);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        height = size.y - 60;
        Log.d("Height", "" + height);
        width = size.x;
        Log.d("Width", "" + width);

        mTextureView = new TextureView(this);
        mTextureView.setSurfaceTextureListener(this);
        mTextureView.setOpaque(false);
        
        
        content.addView(hview, new FrameLayout.LayoutParams(width, height, Gravity.CENTER));
        hview.addView(linLay, new FrameLayout.LayoutParams(width, height, Gravity.CENTER));
        linLay.addView(mTextureView, new FrameLayout.LayoutParams(1000, height, Gravity.START));
        
        setContentView(content);        
        hview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        hview.setScrollbarFadingEnabled(false);

       
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void handleReset(MenuItem item) {
        mUdp.current_segment = -1;
        mThread.clear();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, final int height) {
        mThread = new RenderingThread(mTextureView);
        mUdp = new UDPlistener(mThread);
        mThread.start();
        mUdp.start();
        mThread.clear();
        /*int[] colorArray = new int[1000];
        for(int i = 0; i < 1000; i++)
		{
			colorArray[i] = -256;
		}
        final int[] colors = colorArray;
        new Thread(new Runnable(){
        	@Override
        	public void run()
        	{
        		for(int i = 0; i < 600; i++)
                {
        			mThread.update(colors);
                }
        	}
        }).start();*/
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
        private volatile boolean mUpdated = false;
        private volatile boolean mClear = false;
        private RotatingArray data = new RotatingArray(1000, height, 5); // TODO: Replace these with width/height constants


        public RenderingThread(TextureView surface) {
            mSurface = surface;
        }

        @Override
        public void update(int[] colorArray) {
            data.append(colorArray);
            mUpdated = true;
        }
                
        
        @Override
        public void run() {

            while (mRunning && !Thread.interrupted()) {
                while (!mUpdated) {
                    Thread.yield();
                }
                mUpdated = false;

                if (mClear) {
                    mClear = false;
                    Canvas canvas = mSurface.lockCanvas(null);
                    canvas.drawColor(0xFF000000);
                    mSurface.unlockCanvasAndPost(canvas);
                    System.gc();
                    data = new RotatingArray(1000, height, 5);
                    System.gc();
                    continue;
                }

                final Canvas canvas = mSurface.lockCanvas(null);
                try {
                    canvas.drawBitmap(data.getArray(), data.getOffset(), 1000, 0, 0, 1000, height, false, null);
                } catch (Exception e) {
                    Log.e("RenderThread", "Exception: " + e);
                } finally {
                    mSurface.unlockCanvasAndPost(canvas);
                }

            }
        }

        void stopRendering() {
            interrupt();
            mRunning = false;
        }

        public void clear() {
            this.mClear = true;
            this.mUpdated = true;
        }

    }
}

