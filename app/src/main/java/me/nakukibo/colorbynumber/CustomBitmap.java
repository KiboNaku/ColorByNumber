package me.nakukibo.colorbynumber;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

public class CustomBitmap extends AsyncTask <Void, Void, Void> {

    private Bitmap original;
    private Bitmap colored;
    private Bitmap blank;
    private OnCompleteListener onCompleteListener;

    public CustomBitmap(Bitmap bitmap, OnCompleteListener onCompleteListener){
        this.original = bitmap;
        this.colored = null;
        this.blank = null;
        this.onCompleteListener = onCompleteListener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Bitmap[] bitmaps = BitmapConversion.makeBitmaps(original);

        this.colored = bitmaps[1];
        this.blank = bitmaps[2];

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

    public Bitmap getColored() {
        return colored;
    }


    public Bitmap getBlank() {
        return blank;
    }


    interface OnCompleteListener {
        void onComplete();
    }
}
