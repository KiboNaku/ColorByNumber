package me.nakukibo.colorbynumber;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Set;

public class CustomBitmap extends AsyncTask <Void, Void, Void> {

    private Bitmap original;
    private Bitmap colored;
    private Bitmap blank;
    private Set<Integer> uniqueColors;

    private OnCompleteListener onCompleteListener;

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

    private static final String TAG = "CustomBitmap";

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

    public Set<Integer> getUniqueColors() {
        return uniqueColors;
    }

    public void setUniqueColors(Set<Integer> uniqueColors) {
        this.uniqueColors = uniqueColors;
    }

    interface OnCompleteListener {
        void onComplete();
    }
}


