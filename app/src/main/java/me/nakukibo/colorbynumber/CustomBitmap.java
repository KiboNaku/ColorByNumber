package me.nakukibo.colorbynumber;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

public class CustomBitmap extends AsyncTask <Void, Void, Void> {

    private Bitmap original;
    private Bitmap colored;
    private Bitmap blank;
    private ColorSet uniqueColors;

    private OnCompleteListener onCompleteListener;
    private static final String TAG = "CustomBitmap";

    public CustomBitmap(Bitmap bitmap, OnCompleteListener onCompleteListener){
        this.original = bitmap;
        this.colored = null;
        this.blank = null;
        this.uniqueColors = null;
        this.onCompleteListener = onCompleteListener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        BitmapConversion.makeCustomBitmap(original, this);
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d(TAG, "onPostExecute: finished executing");
        if(onCompleteListener != null) onCompleteListener.onComplete();
    }

    public Bitmap getOriginal() {
        return original;
    }

    public void setOriginal(Bitmap original) {
        this.original = original;
    }

    public Bitmap getColored() {
        return colored;
    }

    public void setColored(Bitmap colored) {
        this.colored = colored;
    }

    public Bitmap getBlank() {
        return blank;
    }

    public void setBlank(Bitmap blank) {
        this.blank = blank;
    }

    public ColorSet getUniqueColors() {
        return uniqueColors;
    }

    public void setUniqueColors(ColorSet uniqueColors) {
        this.uniqueColors = uniqueColors;
    }

    interface OnCompleteListener {
        void onComplete();
    }
}


