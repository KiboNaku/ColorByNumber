package me.nakukibo.colorbynumber.bitmap;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import me.nakukibo.colorbynumber.BaseActivity;
import me.nakukibo.colorbynumber.color.ColorSet;
import me.nakukibo.colorbynumber.utils.OnCompleteListener;
import me.nakukibo.colorbynumber.utils.OnUpdateListener;

public class CustomBitmap extends AsyncTask<Integer, Bitmap, Void> {

    public static final int CONVERT_COLORED = 1;
    public static final int CONVERT_BLANK = 2;

    private static final String TAG = "CustomBitmap";
    private static final String FILE_NAME = "fileName";
    private static final String UNIQUE_COLORS = "uniqueColors";

    private String fileName;
    private File originalDir;
    private File coloredDir;
    private File blankDir;
    private ColorSet uniqueColors;
    private OnUpdateListener onUpdateListener;
    private OnCompleteListener onCompleteListener;

    private CustomBitmap() {
    }

    public static CustomBitmap make(JSONObject cBitmap, File originalDir, File coloredDir, File blankDir)
            throws JSONException {
        CustomBitmap customBitmap = new CustomBitmap();
        customBitmap.fileName = cBitmap.getString(FILE_NAME);
        customBitmap.originalDir = originalDir;
        customBitmap.coloredDir = coloredDir;
        customBitmap.blankDir = blankDir;

        JSONArray jsonArray = new JSONArray(UNIQUE_COLORS);
        customBitmap.uniqueColors = new ColorSet(jsonArray);
        return customBitmap;
    }

    public CustomBitmap(String fileName, File originalDir, File coloredDir, File blankDir) {
        this.fileName = fileName;
        this.originalDir = originalDir;
        this.coloredDir = coloredDir;
        this.blankDir = blankDir;

        uniqueColors = null;
        onUpdateListener = null;
        onCompleteListener = null;
    }

    @Override
    protected Void doInBackground(Integer... integers) {

        int code = integers[0];

        switch (code) {
            case CONVERT_COLORED:

                Bitmap bitmap = BaseActivity.getBitmap(originalDir, fileName);

                if (uniqueColors == null) {
                    uniqueColors = BitmapConversion.getUniqueColors(bitmap);
                }

                BitmapConversion.createColorGrouped(this, bitmap, uniqueColors, coloredDir, fileName);
                break;
            case CONVERT_BLANK:

                Bitmap colored = BaseActivity.getBitmap(coloredDir, fileName);
                if (colored == null) {

                    Bitmap original = BaseActivity.getBitmap(originalDir, fileName);

                    if (uniqueColors == null) {
                        uniqueColors = BitmapConversion.getUniqueColors(original);
                        Log.d(TAG, "doInBackground: CustomBitmap - number of Colors = " + uniqueColors.size());
                    }

                    BitmapConversion.createColorGrouped(this, original, uniqueColors, coloredDir, fileName);
                    if (original != null) original.recycle();
                    colored = BaseActivity.getBitmap(coloredDir, fileName);
                }
                BitmapConversion.createColorBlank(this, colored, blankDir, fileName);
        }

        return null;
    }

    public void updateProgress(Bitmap... values) {
        publishProgress(values);
    }

    @Override
    protected void onProgressUpdate(Bitmap... values) {
        super.onProgressUpdate(values);

        if (onUpdateListener != null) onUpdateListener.onUpdate(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (onCompleteListener != null) {
            onCompleteListener.onComplete();
            onCompleteListener = null;
        }
    }

    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }

    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    @Override
    @NonNull
    public String toString() {

        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray colorsArray = new JSONArray(uniqueColors);

            jsonObject.put(FILE_NAME, fileName);
            jsonObject.put(UNIQUE_COLORS, colorsArray);

            Log.d(TAG, "toString: " + jsonObject.toString());

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}


