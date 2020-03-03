package me.nakukibo.colorbynumber;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ImporterActivity extends Activity {
    private static final int REQUEST_CODE_FETCH_PHOTO = 1;
    private static final int REQUEST_CODE_CAMERA = 2;

    private ImageView imageViewRetrievedPhoto;

    private String absoluteImageFilePath = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importer);

        imageViewRetrievedPhoto = findViewById(R.id.image_view_retrieved);

        Button buttonLaunchCamera = findViewById(R.id.button_launch_camera);
        buttonLaunchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasCameraHardware(ImporterActivity.this)){
                    if (ContextCompat.checkSelfPermission(ImporterActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ImporterActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                REQUEST_CODE_CAMERA);
                    } else {
                        launchCamera();
                    }
                } else {
                    Toast.makeText(ImporterActivity.this, "Cannot open camera. No hardware camera detected.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                Toast.makeText(this, "Cannot open camera. Camera permission not given.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void launchCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File tempPhotoFile;

            try{
                tempPhotoFile = createPhotoFile();

            } catch (IOException e){
                e.printStackTrace();
                Toast.makeText(this, "Error: failed to create photo save file. Cannot proceed", Toast.LENGTH_LONG).show();
                return;
            }

            Uri photoUri = FileProvider.getUriForFile(this, "me.nakukibo.colorbynumber.provider", tempPhotoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, REQUEST_CODE_FETCH_PHOTO);
        } else {
            Toast.makeText(this, "Unexpected error: failed to launch camera.", Toast.LENGTH_LONG).show();
        }
    }


    private static final String TAG = "ImporterActivity";
    private File createPhotoFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SS", Locale.US).format(new Date());
        String fileName = "color-by-number_" + timeStamp;

        File photoFile = File.createTempFile(fileName, ".jpg");
        absoluteImageFilePath = photoFile.getAbsolutePath();

        Log.d(TAG, "createPhotoFile: " + absoluteImageFilePath);

        return photoFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        final String ERROR_NULL = "Unexpected error: failed to retrieve image.";

        if (requestCode == REQUEST_CODE_FETCH_PHOTO && resultCode == RESULT_OK) {

            Bitmap imageBitmap = BitmapFactory.decodeFile(absoluteImageFilePath);
//            BitmapConversion.convert(imageBitmap);
            imageViewRetrievedPhoto.setImageBitmap(imageBitmap);
        } else {
            Toast.makeText(this, ERROR_NULL, Toast.LENGTH_LONG).show();
        }
    }

    private boolean hasCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }
}