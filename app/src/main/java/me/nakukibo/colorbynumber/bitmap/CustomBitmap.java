package me.nakukibo.colorbynumber.bitmap;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.File;

import me.nakukibo.colorbynumber.ConversionActivity;
import me.nakukibo.colorbynumber.color.ColorSet;
import me.nakukibo.colorbynumber.utils.OnCompleteListener;

public class CustomBitmap extends AsyncTask<Integer, Void, Void> {

    private String fileName;
    private File originalDir;
    private File coloredDir;
    private File blankDir;
    private ColorSet uniqueColors;
    private OnCompleteListener onCompleteListener;

    private static final String TAG = "CustomBitmap";

    public static final int CONVERT_COLORED = 1;
    public static final int CONVERT_BLANK = 2;

    public CustomBitmap(String fileName, File originalDir, File coloredDir, File blankDir) {
        this.fileName = fileName;
        this.originalDir = originalDir;
        this.coloredDir = coloredDir;
        this.blankDir = blankDir;
        this.uniqueColors = null;
        this.onCompleteListener = null;
    }

    @Override
    protected Void doInBackground(Integer... integers) {

        int code = integers[0];
        Bitmap bitmap = ConversionActivity.getBitmap(originalDir, fileName);

        if (uniqueColors == null) {
            uniqueColors = BitmapConversion.getUniqueColors(bitmap);
        }

        switch (code) {
            case CONVERT_COLORED:

                BitmapConversion.createColorGrouped(bitmap, uniqueColors, coloredDir, fileName);
                break;
            case CONVERT_BLANK:

                Bitmap colored = ConversionActivity.getBitmap(coloredDir, fileName);
                if (colored == null) {
                    BitmapConversion.createColorGrouped(bitmap, uniqueColors, coloredDir, fileName);
                    colored = ConversionActivity.getBitmap(coloredDir, fileName);
                }
                BitmapConversion.createColorBlank(colored, uniqueColors, blankDir, fileName);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (onCompleteListener != null) {
            onCompleteListener.onComplete();
            onCompleteListener = null;
        }
    }

    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }
}


