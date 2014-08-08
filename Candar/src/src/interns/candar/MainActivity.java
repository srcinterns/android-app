package src.interns.candar;


import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.*;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends Activity implements TextureView.SurfaceTextureListener {
    private TextureView mTextureView;
    private MainActivity.RenderingThread mThread;
    private static TextView text;
    private UDPlistener mUdp;
    private static int height;
    private static int width;
    private static int segNum = 0;
    private final static Runnable mUpdateUITimerTask = new Runnable() {
        public void run() {
            text.setText("Segment Number: " + segNum);
        }
    };
    private final static Handler mHandler = new Handler();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout content = new LinearLayout(this);
        HorizontalScrollView hview = new HorizontalScrollView(this);
        LinearLayout textContainer = new LinearLayout(this);
        text = new TextView(this);
        LinearLayout container = new LinearLayout(this);
        
        textContainer.setOrientation(LinearLayout.VERTICAL);
        

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
        hview.addView(textContainer, new FrameLayout.LayoutParams(width, height, Gravity.CENTER));
        textContainer.addView(text, new FrameLayout.LayoutParams(width, 40, Gravity.CENTER));
        textContainer.addView(container, new FrameLayout.LayoutParams(width, height-40, Gravity.CENTER));
        container.addView(mTextureView, new FrameLayout.LayoutParams(width, height, Gravity.CENTER));
        
        setContentView(content);
        
        text.setText("Segment Number: ");
        
        
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
        segNum = 0;
        mThread.clear();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, final int height) {
        mThread = new RenderingThread(mTextureView);
        mUdp = new UDPlistener(mThread);
        mThread.start();
        mUdp.start();
        mThread.clear();
        
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
        private RotatingArray data = new RotatingArray(2000, height, 5); // TODO: Replace these with width/height constants


        public RenderingThread(TextureView surface) {
            mSurface = surface;
        }

        @Override
        public void update(int[] colorArray, int seg) {
            data.append(colorArray);
            segNum = seg;
            mHandler.post(mUpdateUITimerTask);
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
                    data = null;
                    segNum = 0;
                    System.gc();
                    data = new RotatingArray(2000, height, 5);
                    System.gc();
                    continue;
                }

                final Canvas canvas = mSurface.lockCanvas(null);
                try {
                    canvas.drawBitmap(data.getArray(), data.getOffset(), 2000, 0, 0, 2000, height, false, null);
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

