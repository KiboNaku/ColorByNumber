package me.nakukibo.colorbynumber;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ColoringActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coloring);

        String fileName = getIntent().getStringExtra(MainActivity.FILE_NAME);
        File directory = getBlankSubdirectory();

        File f = new File(directory, fileName);

        try {
            ((ImageView) findViewById(R.id.image_coloring)).setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(f)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
