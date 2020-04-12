package me.nakukibo.colorbynumber;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import me.nakukibo.colorbynumber.bitmap.CustomBitmap;
import me.nakukibo.colorbynumber.utils.OnCompleteListener;
import me.nakukibo.colorbynumber.utils.OnUpdateListener;

public class ImportPopup extends BaseActivity {

    private static final String TAG = "ImportPopup";
    private String fileName = null;
    private boolean finishedColor = false;
    private CustomBitmap coloredCustomBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_popup);

        setWindow();
        startConversion();
    }

    public void acceptImage(View view){

        if(finishedColor){

            CustomBitmap customBitmap = new CustomBitmap(fileName, getOriginalSubdirectory(), getColoredSubdirectory(), getBlankSubdirectory());
            customBitmap.execute(CustomBitmap.CONVERT_BLANK);

            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(this, "Please wait until image is finished.", Toast.LENGTH_LONG).show();
        }
    }

    public void rejectImage(View view){

        coloredCustomBitmap.cancel(true);

        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);

        deleteImage(fileName);

        finish();
    }

    private void startConversion() {

        final ImageView imageView = findViewById(R.id.image_view_import);
        fileName = getIntent().getStringExtra(ConversionActivity.FILE_NAME);

        coloredCustomBitmap = new CustomBitmap(fileName, getOriginalSubdirectory(), getColoredSubdirectory(), getBlankSubdirectory());

        coloredCustomBitmap.setOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete() {
                imageView.setImageBitmap(getBitmap(getColoredSubdirectory(), fileName));
                finishedColor = true;

            }
        });
        coloredCustomBitmap.setOnUpdateListener(new OnUpdateListener() {
            @Override
            public void onUpdate(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        });

        coloredCustomBitmap.execute(CustomBitmap.CONVERT_COLORED);
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
