package me.nakukibo.colorbynumber;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.nakukibo.colorbynumber.bitmap.CustomBitmap;
import me.nakukibo.colorbynumber.color.ColorSet;
import me.nakukibo.colorbynumber.color.ColorView;


public class ConversionActivity extends BaseActivity {

    //TODO: remove ConversionActivity and move to MainActivity

    public static final String FILE_NAME = "filepath";

    private static final int REQUEST_CODE_FETCH_PHOTO = 1;
    private static final int REQUEST_CODE_READ_GALLERY = 2;

    private static final int LAUNCH_CODE_CAMERA = 1;
    private static final int LAUNCH_CODE_OPEN_GALLERY = 2;

    private static final String TAG = "ConversionActivity";

    private CustomBitmap customBitmap;
    private Bitmap uncolored = null;
    private Integer color = null;
    private ColorSet colors = null;
    private List<Integer> viewList = null;

    private File tempfile = null;

    //TODO: fix the order of requesting permissions
    private int viewIndex = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);

        Button buttonLaunchCamera = findViewById(R.id.button_launch_camera);
        buttonLaunchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (hasCameraHardware(ConversionActivity.this)) {

                    if (ContextCompat.checkSelfPermission(ConversionActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(ConversionActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(ConversionActivity.this,
                                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                LAUNCH_CODE_CAMERA);
                    } else {
                        launchCamera();
                    }
                } else {
                    Toast.makeText(ConversionActivity.this, "Cannot open camera. No hardware camera detected.", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button buttonLaunchGallery = findViewById(R.id.button_launch_gallery);
        buttonLaunchGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(ConversionActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(ConversionActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(ConversionActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_READ_GALLERY);
                } else {
                    launchGallery();
                }
            }
        });
    }

    private void launchGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, LAUNCH_CODE_OPEN_GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == LAUNCH_CODE_CAMERA) {

            if (grantResults.length > 1) {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                } else if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Cannot open camera. Camera permission not given.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Cannot open camera. Writing to memory permission not given.", Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == REQUEST_CODE_READ_GALLERY) {

            if (grantResults.length > 1) {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    launchGallery();
                } else if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Cannot open gallery. Gallery permission not given.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Cannot open gallery. Writing to memory permission not given.", Toast.LENGTH_LONG).show();
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_FETCH_PHOTO) {

                final String IMPORT = "camera";

                View loadingBar = findViewById(R.id.progress_bar_load_image);
                final String ERROR_NULL = "Unexpected error: failed to retrieve image.";

                Bundle extras = data.getExtras();
                if (extras == null) {
                    Toast.makeText(this, ERROR_NULL, Toast.LENGTH_LONG).show();
                    return;
                }

                Bitmap imageBitmap = (Bitmap) extras.get("data");
                if (imageBitmap == null) {
                    Toast.makeText(this, ERROR_NULL, Toast.LENGTH_LONG).show();
                    return;
                }

                loadingBar.setVisibility(View.VISIBLE);
                useBitmap(imageBitmap, IMPORT);
                loadingBar.setVisibility(View.INVISIBLE);

            } else if (requestCode == LAUNCH_CODE_OPEN_GALLERY) {

                final String IMPORT = "gallery";

                Log.d(TAG, "onActivityResult: fetching photo from gallery");

                View loadingBar = findViewById(R.id.progress_bar_load_image);
                loadingBar.setVisibility(View.VISIBLE);

                Bitmap selectedImage = null;

                try {

                    final Uri imageUri = data.getData();

                    if (imageUri != null) {
                        selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (selectedImage == null) {

                    Toast.makeText(this, "Failed to retrieve image.", Toast.LENGTH_LONG).show();
                } else {
                    useBitmap(selectedImage, IMPORT);
                }

                loadingBar.setVisibility(View.INVISIBLE);
            }
        } else {
            Toast.makeText(this, "Failed to retrieve result.", Toast.LENGTH_LONG).show();
        }
    }

    private void useBitmap(Bitmap selectedImage, String importFormat) {

        final int MAX_RES_BIG = 1600;
        final int MAX_RES_SM = 900;

        int width = selectedImage.getWidth();
        int height = selectedImage.getHeight();

        Log.d(TAG, "useBitmap: image sizing (w x h): " + width + " x " + height);

        if(width > height){

            double bigScale = width/(double) MAX_RES_BIG;
            double smScale = height/(double) MAX_RES_SM;

            if(bigScale > smScale){
                width = MAX_RES_BIG;
                height = (int) Math.floor(height/bigScale);
            } else {
                height = MAX_RES_SM;
                width = (int) Math.floor(width/smScale);
            }
        }else {

            double bigScale = height/(double) MAX_RES_BIG;
            double smScale = width/(double) MAX_RES_SM;

            if(bigScale > smScale){
                height = MAX_RES_BIG;
                width = (int) Math.floor(width/bigScale);
            } else {
                width = MAX_RES_SM;
                height = (int) Math.floor(height/smScale);
            }

        }

        Log.d(TAG, "useBitmap: new image sizing (w x h): " + width + " x " + height);

        selectedImage = Bitmap.createScaledBitmap(selectedImage, width, height, true);

        String fileName = getNewBitmapName(importFormat);

        File originalFile = saveToInternalStorage(getOriginalSubdirectory(), fileName, selectedImage);

        ImageView photoImageView = findViewById(R.id.image_view_retrieved);
        photoImageView.setImageBitmap(selectedImage);

        Intent intent = new Intent(this, ImportPopup.class);
        intent.putExtra(FILE_NAME, fileName);
        startActivity(intent);

//                customBitmap = new CustomBitmap(imageBitmap, new CustomBitmap.OnCompleteListener() {
//                    @Override
//                    public void onComplete() {
//                        imageViewRetrievedPhoto.setImageBitmap(customBitmap.getColored());
////                        saveToInternalStorage(customBitmap);
//                        findViewById(R.id.progress_bar_load_image).setVisibility(View.INVISIBLE);
//
//                        findViewById(R.id.layout_colors).setVisibility(View.VISIBLE);
//                        HorizontalScrollView colorsScroll = findViewById(R.id.scroll_colors);
//
//                        colors = customBitmap.getUniqueColors();
//
//                        Log.d(TAG, "onComplete: number of colors = " + colors.size());
//
//                        viewList = new ArrayList<>();
//                        viewList.add(R.id.color);
//                        viewList.add(R.id.color1);
//                        viewList.add(R.id.color2);
//                        viewList.add(R.id.color3);
//                        viewList.add(R.id.color4);
//                        viewList.add(R.id.color5);
//                        viewList.add(R.id.color6);
//                        viewList.add(R.id.color7);
//                        viewList.add(R.id.color8);
//                        viewList.add(R.id.color9);
//
//                        for(int i=0; i<Math.min(colors.size(), 10); i++){
//                            int color = colors.pop().getColor();
//                            Log.d(TAG, "selectColor: color = (" + Color.red(color) + ", " + Color.green(color) + ", " + Color.blue(color) + ")");
//                            ((ColorView) findViewById(viewList.get(i))).setColor(color);
//                        }
//
////                        for(Integer color: customBitmap.getUniqueColors()){
////                            Log.d(TAG, "onComplete: color = (" + Color.red(color) + ", " + Color.green(color) + ", " + Color.blue(color) + ")");
////                            View colorView = new View(ConversionActivity.this);
////                            colorView.setBackgroundColor(color);
////                            colorsScroll.addView(colorView, 200, 200);
////                        }
//                    }
//                });
//                customBitmap.execute();
    }

//    public void paintColor(View view) {
//
//        if (customBitmap == null || customBitmap.getBlank() == null || color == null || viewIndex < 0)
//            return;
//
//        Log.d(TAG, "paintColor: color is painting");
//        Log.d(TAG, "paintColor: painting color " + "(" + Color.red(color) + ", " + Color.green(color) + ", " + Color.blue(color) + ")");
//
//
//        Bitmap colored = customBitmap.getColored();
//        Bitmap blank = customBitmap.getBlank();
//
//        for (int i = 0; i < blank.getHeight(); i++) {
//            for (int j = 0; j < blank.getWidth(); j++) {
//                if (colored.getPixel(j, i) == color) {
//                    blank.setPixel(j, i, color);
//                }
//            }
//        }
//
//        ColorView colorView = ((ColorView) findViewById(viewList.get(viewIndex)));
//
//        if (colors.size() > 0) colorView.setColor(colors.pop().getColor());
//        else colorView.setVisibility(View.GONE);
//
//        color = -1;
//        viewIndex = -1;
//
//        ((ImageView) findViewById(R.id.image_view_retrieved)).setImageBitmap(customBitmap.getBlank());
//
//    }

    public void selectColor(View view) {

        int id = view.getId();
        boolean foundView = false;

        Log.d(TAG, "selectColor: colorview size=" + viewList.size());

        for (int i = 0; i < viewList.size(); i++) {
            if (id == viewList.get(i)) {
                viewIndex = i;
                foundView = true;
            }
        }

        if (!foundView) Log.d(TAG, "selectColor: cannot find view");

        color = ((ColorView) view).getColor();
        Log.d(TAG, "selectColor: color = (" + Color.red(color) + ", " + Color.green(color) + ", " + Color.blue(color) + ")");
    }

    private boolean hasCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private String getNewBitmapName(String importFormat) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        return importFormat + "_" + timeStamp + ".jpg";
    }
}