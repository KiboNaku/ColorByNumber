package me.nakukibo.colorbynumber;

import android.os.Bundle;
import android.util.DisplayMetrics;

import me.nakukibo.colorbynumber.bitmap.CustomBitmap;

public class ImportPopup extends BaseActivity {

    private static final String TAG = "ImportPopup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_popup);

        setWindow();
        startConversion();
    }

    private void startConversion() {

        String fileName = getIntent().getStringExtra(ConversionActivity.FILE_NAME);
        CustomBitmap customBitmap = new CustomBitmap(fileName, getOriginalSubdirectory(), getColoredSubdirectory(), getBlankSubdirectory());
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
