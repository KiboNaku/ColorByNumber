package me.nakukibo.colorbynumber;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ColoringActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coloring);

        String fileName = getIntent().getStringExtra(MainActivity.FILE_NAME);
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("images", Context.MODE_PRIVATE);

        File f = new File(directory, fileName);

        try {
            ((ZoomableImage) findViewById(R.id.image_coloring)).setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(f)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
