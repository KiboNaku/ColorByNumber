package me.nakukibo.colorbynumber;

import android.graphics.Bitmap;

public class CustomBitmap {

    private Bitmap original;
    private Bitmap colored;
    private Bitmap unfilled;

    public CustomBitmap(Bitmap bitmap){
        this.original = bitmap;
        this.colored = null;
        this.unfilled = null;
    }

    public void convert(final OnCompleteListener onCompleteListener){

        Thread conversion = new Thread(new Runnable() {
            public void run() {
                colored = original.copy(Bitmap.Config.ARGB_8888,true);
                BitmapConversion.convertToColorGroupings(colored);
                onCompleteListener.onComplete();
            }
        });

        conversion.start();

    }

    public Bitmap getOriginal() {
        return original;
    }

    public Bitmap getColored() {
        return colored;
    }


    public Bitmap getUnfilled() {
        return unfilled;
    }

    interface OnCompleteListener {
        void onComplete();
    }
}
