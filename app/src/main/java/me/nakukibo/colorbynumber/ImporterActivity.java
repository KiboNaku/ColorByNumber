package me.nakukibo.colorbynumber;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Locale;

public class ImporterActivity extends Activity {
    private static final long ERROR_HARDWARE = 0;
    private static final long ERROR_PERMISSIONS = 1;
    private static final long ERROR_FAIL_TO_RESOLVE_ACTIVITY = 2;
    private static final long ERROR_RETRIEVES_NULL = 3;
    private static final long ERROR_RETRIEVE_FAIL = 4;

    private static final int REQUEST_CODE_FETCH_PHOTO = 1;
    private static final int REQUEST_CODE_CAMERA = 2;

    private ImageView imageViewRetrievedPhoto;

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
                    showToastError("Cannot open camera. No hardware camera detected.", ERROR_HARDWARE);
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
                showToastError("Cannot open camera. Camera permission not given.", ERROR_PERMISSIONS);
            }
        }
    }

    private void launchCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CODE_FETCH_PHOTO);
        } else {
            showToastError("Unexpected error: failed to launch camera.", ERROR_FAIL_TO_RESOLVE_ACTIVITY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final String ERROR_NULL = "Unexpected error: failed to retrieve image.";

        if (requestCode == REQUEST_CODE_FETCH_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if(extras == null){
                showToastError(ERROR_NULL, ERROR_RETRIEVES_NULL);
                return;
            }

            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if(imageBitmap == null){
                showToastError(ERROR_NULL, ERROR_RETRIEVES_NULL);
                return;
            }

            BitmapConversion.convert(imageBitmap);
            imageViewRetrievedPhoto.setImageBitmap(imageBitmap);
        } else {
            showToastError(ERROR_NULL, ERROR_RETRIEVE_FAIL);
        }
    }

    private boolean hasCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    void showToastError(String errorMessage, final long ERROR_CODE){
        Toast.makeText(
                this,
                String.format(Locale.US, "%s Error code: %d", errorMessage, ERROR_CODE),
                Toast.LENGTH_LONG)
                .show();
    }
}