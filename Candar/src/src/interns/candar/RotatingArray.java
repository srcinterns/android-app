package src.interns.candar;

import android.util.Log;

import java.util.Arrays;

/**
 * Created by erichter on 8/1/2014.
 */
public class RotatingArray {

    private int columns = 512;
    private int rows = 300;
    private int segments = 10;

    private int offset = 0;

    private int[] data;

    public RotatingArray(int columns, int rows, int segments) {
        this.columns = columns;
        this.rows = rows;
        this.segments = segments;
        this.data = new int[columns * rows * segments];
    }

    public void append(int[] array) {
        // We should be receiving a row's worth of data, error if not
        if (this.columns != array.length) {
            throw new ArrayStoreException("Array.length != this.columns");
        }

        // Check if we need to rotate back to the beginning of the buffer or not
        if (this.offset >= data.length) {
            hardRotate();
        }

        // Copy the data into the array
        for (int a : array) {
            data[offset++] = a;
        }
    }

    private void hardRotate() {
        Log.i("RotatingArray", "Rotating the array, hold onto your hats!");
        int j = 0;
        for (int i = data.length - (rows*columns); i < data.length; i++,j++) {
            data[j] = data[i];
        }
        offset = j - 1;
    }

    public int[] getArray() {
        return data;
    }

    public int getOffset() {
        int ret = offset - (rows * columns);
        return (ret < 0) ? 0 : ret;
    }

    public void blank() {
        this.offset = 0;
        this.data = new int[columns * rows * segments];
    }
}
