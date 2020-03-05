package me.nakukibo.colorbynumber;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ImporterActivity extends Activity {
    private static final int REQUEST_CODE_FETCH_PHOTO = 1;
    private static final int REQUEST_CODE_CAMERA = 1;

    private static final int REQUEST_CODE_OPEN_GALLERY = 2;
    private static final int REQUEST_CODE_READ_EXT = 2;

    private ImageView imageViewRetrievedPhoto;

    //TODO: fix the order of requesting permissions

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

        Button buttonLaunchGallery = findViewById(R.id.button_launch_gallery);
        buttonLaunchGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(ImporterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ImporterActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_READ_EXT);
                } else {
                    launchGallery();
                }
            }
        });
    }

    private void launchGallery() {
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_CODE_OPEN_GALLERY);
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
        } else if (requestCode == REQUEST_CODE_READ_EXT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchGallery();
            } else {
                Toast.makeText(this, "Cannot open gallery. Camera permission not given.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void launchCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CODE_FETCH_PHOTO);
        } else {
            Toast.makeText(this, "Unexpected error: failed to launch camera.", Toast.LENGTH_LONG).show();
        }
    }

    private static final String TAG = "ImporterActivity";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if (requestCode == REQUEST_CODE_FETCH_PHOTO) {

                final String ERROR_NULL = "Unexpected error: failed to retrieve image.";

                Bundle extras = data.getExtras();
                if(extras == null){
                    Toast.makeText(this, ERROR_NULL, Toast.LENGTH_LONG).show();
                    return;
                }

                Bitmap imageBitmap = (Bitmap) extras.get("data");
                if(imageBitmap == null){
                    Toast.makeText(this, ERROR_NULL, Toast.LENGTH_LONG).show();
                    return;
                }

                BitmapConversion.convert(imageBitmap);
                imageViewRetrievedPhoto.setImageBitmap(imageBitmap);
            } else if(requestCode == REQUEST_CODE_OPEN_GALLERY) {

                Log.d(TAG, "onActivityResult: fetching photo from gallery");
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                Bitmap imageBitmap = BitmapFactory.decodeFile(picturePath).copy(Bitmap.Config.ARGB_8888,true);
                BitmapConversion.convert(imageBitmap);
                imageViewRetrievedPhoto.setImageBitmap(imageBitmap);

                saveToInternalStorage(imageBitmap);
            }
        } else {
            Toast.makeText(this, "Failed to retrieve result.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean hasCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    //temporary save code

    private void saveToInternalStorage(Bitmap bitmapImage){

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("images", Context.MODE_PRIVATE);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.US).format(new Date());
        String imageFileName = "Coloring_" + timeStamp;

        File imagePath = new File(directory, imageFileName + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imagePath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}