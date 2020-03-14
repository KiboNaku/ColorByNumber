package me.nakukibo.colorbynumber;

import android.content.Context;
import android.content.ContextWrapper;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public abstract class ColoringAppCompatActivity extends AppCompatActivity {

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
}
