package me.nakukibo.colorbynumber.bitmap;

import android.os.AsyncTask;
import android.util.Log;

import me.nakukibo.colorbynumber.color.ColorSet;

public class CustomBitmap extends AsyncTask<Integer, Void, Void> {

    private String fileName;
    private ColorSet uniqueColors;
    private OnCompleteListener onCompleteListener;

    private static final String TAG = "CustomBitmap";

    public static final int CONVERT_COLORED = 1;
    public static final int CONVERT_BLANK = 2;

    public CustomBitmap(String fileName) {
        this.fileName = fileName;
        this.uniqueColors = null;
        this.onCompleteListener = null;
    }

    @Override
    protected Void doInBackground(Integer... integers) {

        int code = integers[0];

        switch (code) {
            case CONVERT_COLORED:
                break;
            case CONVERT_BLANK:
        }

        return null;
    }

    public String getFileName() {
        return fileName;
    }

    public ColorSet getUniqueColors() {
        return uniqueColors;
    }

    public void setUniqueColors(ColorSet uniqueColors) {
        this.uniqueColors = uniqueColors;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d(TAG, "onPostExecute: finished executing");
        if(onCompleteListener != null) onCompleteListener.onComplete();
    }

    interface OnCompleteListener {
        void onComplete();
    }
}


