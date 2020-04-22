package me.nakukibo.colorbynumber;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.LinkedList;

import me.nakukibo.colorbynumber.bitmap.CustomBitmap;
import me.nakukibo.colorbynumber.utils.OnCompleteListener;
import me.nakukibo.colorbynumber.utils.OnUpdateListener;

public class ImportPopup extends BaseActivity {

    public static final String FILE_NAME = "filename";

    private static final String TAG = "ImportPopup";
    private String fileName = null;
    private boolean finishedColor = false;
    private CustomBitmap coloredCustomBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_popup);
        getSupportActionBar().hide();

        setWindow();
        startConversion();
    }

    public void acceptImage(View view){

        if(finishedColor){

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String bitmapsStr = sharedPreferences.getString(PREF_CBITMAPS, null);
            try {
                LinkedList<String> bitmapsList = new LinkedList<>();

                JSONArray bitmapsArray = new JSONArray(bitmapsStr);
                for (int i = 0; i < bitmapsArray.length(); i++) {
                    bitmapsList.add((String) bitmapsArray.get(i));
                }

                bitmapsList.push(coloredCustomBitmap.toString());

                sharedPreferences.edit().putString(PREF_CBITMAPS, (new JSONArray(bitmapsList)).toString()).apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
        final View progressConvert = findViewById(R.id.progress_convert);
        progressConvert.setVisibility(View.VISIBLE);

        fileName = getIntent().getStringExtra(FILE_NAME);

        coloredCustomBitmap = new CustomBitmap(fileName, getOriginalSubdirectory(), getColoredSubdirectory(), getBlankSubdirectory());

        coloredCustomBitmap.setOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete() {
                imageView.setImageBitmap(getBitmap(getColoredSubdirectory(), fileName));
                finishedColor = true;
                progressConvert.setVisibility(View.INVISIBLE);
                coloredCustomBitmap.execute(CustomBitmap.CONVERT_BLANK);
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
