package src.interns.candar;

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

import java.util.Arrays;


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
        private RotatingArray data = new RotatingArray(512, 800, 10); // TODO: Replace these with width/height constants

        public RenderingThread(TextureView surface) {
            mSurface = surface;
        }

        @Override
        public void update(int[] colorArray) {
            data.append(colorArray);
        }

        @Override
        public void run() {

            while (mRunning && !Thread.interrupted()) {
                final Canvas canvas = mSurface.lockCanvas(null);
                try {
                    int[] moo = data.getArray();
                    canvas.drawBitmap(moo, data.getOffset(), 512, 0, 0, 512, 800, false, null);
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
