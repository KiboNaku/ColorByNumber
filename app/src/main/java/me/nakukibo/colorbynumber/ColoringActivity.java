package me.nakukibo.colorbynumber;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

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

       if(fileName == null) {
           Toast.makeText(this, "Error: Failed to find image.", Toast.LENGTH_LONG).show();
           finish();
       }
        openImage(fileName);
    }

    private void openImage(String fileName){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir(MainActivity.IMAGES_SUBFILE, Context.MODE_PRIVATE);
        File f = new File(directory, fileName);

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            ((ImageView) findViewById(R.id.image_coloring)).setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: Failed to open image.", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
