package me.nakukibo.colorbynumber;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;

//TODO: reduce the minimum sdk required

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_FETCH_PHOTO = 1;
    private static final int REQUEST_CODE_CAMERA = 2;

    private ImageView imageViewRetrievedPhoto;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewRetrievedPhoto = findViewById(R.id.image_view_retrieved);

        Button buttonLaunchCamera = findViewById(R.id.button_launch_camera);
        buttonLaunchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, REQUEST_CODE_FETCH_PHOTO);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CODE_FETCH_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_FETCH_PHOTO && resultCode == Activity.RESULT_OK) {
            Bundle dataExtras = data.getExtras();
            if(dataExtras != null) {

                Bitmap photo = (Bitmap) dataExtras.get("data");

                if(photo == null) {
                    Log.i(TAG, "onActivityResult: failed to parse photo");
                    return;
                }

                setPhoto(photo);

            } else {
                Log.i(TAG, "onActivityResult: failed to fetch photo - dataExtras null");
            }
        }
    }

    private void setPhoto(Bitmap photo){
        BitmapConversion.convert(photo);
        imageViewRetrievedPhoto.setImageBitmap(photo);
    }


}