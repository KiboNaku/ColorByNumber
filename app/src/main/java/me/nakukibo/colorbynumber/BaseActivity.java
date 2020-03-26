package me.nakukibo.colorbynumber;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class BaseActivity extends Activity {

    public ContextWrapper getAppCW(){
        return new ContextWrapper(getApplicationContext());
    }

    public File getImagesDirectory(){
        return getAppCW().getDir("images", Context.MODE_PRIVATE);
    }

    public File getOriginalSubdirectory(){
        return new File(getImagesDirectory().toString() + File.separator + "original");
    }

    public File getColoredSubdirectory(){
        return new File(getImagesDirectory().toString() + File.separator + "colored");
    }

    public File getBlankSubdirectory(){
        return new File(getImagesDirectory().toString() + File.separator + "blank");
    }

    public static File saveToInternalStorage(File directory, String fileName, Bitmap bitmap) {

        File bitmapFile = new File(directory, fileName);
        try {

            FileOutputStream fos = new FileOutputStream(bitmapFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmapFile;
    }

    public static Bitmap getBitmap(File dir, String fileName) {

        if (fileName == null) {
            return null;
        }

        try {
            File f = new File(dir, fileName);
            return BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException | NullPointerException e) {
            e.printStackTrace();
        }

        return null;
    }
}
