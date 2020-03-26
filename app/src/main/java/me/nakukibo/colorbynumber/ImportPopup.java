package me.nakukibo.colorbynumber;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImportPopup extends BaseActivity {

    private static final String TAG = "ImportPopup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_popup);

        setWindow();
        loadImage();
    }

    private void loadImage() {

        final String ERROR_MSG = "Failed to fetch image. Try again.";
        String fileName = getIntent().getStringExtra(ConversionActivity.FILE_NAME);

        if (fileName == null) {
            finish();
            return;
        }

        try {

            File directory = getOriginalSubdirectory();
            File f = new File(directory, fileName);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img = findViewById(R.id.image_view_import);
            img.setImageBitmap(b);
        } catch (FileNotFoundException | NullPointerException e) {

            e.printStackTrace();
            Toast.makeText(this, ERROR_MSG, Toast.LENGTH_LONG).show();
        }
    }

    private void setWindow() {

        final double WIDTH_MUL = .8;
        final double HEIGHT_MUL = .8;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int newWidth = (int) (displayMetrics.widthPixels * WIDTH_MUL);
        int newHeight = (int) (displayMetrics.heightPixels * HEIGHT_MUL);

        getWindow().setLayout(newWidth, newHeight);
    }
}
