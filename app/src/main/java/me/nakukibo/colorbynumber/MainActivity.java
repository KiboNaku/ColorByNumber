package me.nakukibo.colorbynumber;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import me.nakukibo.colorbynumber.adapters.ImageListAdapter;
import me.nakukibo.colorbynumber.bitmap.CustomBitmap;
import me.nakukibo.colorbynumber.menu.FloatingMenuFragment;
import me.nakukibo.colorbynumber.menu.MenuItem;

//TODO: reduce the minimum sdk required

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_PERMISSION_EXT_STORAGE = 11;

    private static final int LAUNCH_CODE_CAMERA = 21;
    private static final int LAUNCH_CODE_GALLERY = 22;

    private static final int REQUEST_CODE_OPEN_POPUP = 41;

    //TODO: add shared preferences to timestamp most recent images
    //TODO: add placeholders and stuff later

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionActivities();
        instantiateFragment();
        printAllImageFiles();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadColoringImages();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_EXT_STORAGE:

                if (grantResults.length > 1 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    doStorageTask();
                } else {
                    Toast.makeText(this, "Cannot create directories. Permission not granted.", Toast.LENGTH_LONG).show();
                }
                break;

            case LAUNCH_CODE_CAMERA:

                if (grantResults.length > 1) {

                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        launchCamera();
                    } else if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Cannot open camera. Camera permission not given.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Cannot open camera. Writing to memory permission not given.", Toast.LENGTH_LONG).show();
                    }
                }
                break;

            case LAUNCH_CODE_GALLERY:

                if (grantResults.length > 1) {

                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        launchGallery();
                    } else if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Cannot open gallery. Gallery permission not given.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Cannot open gallery. Writing to memory permission not given.", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == LAUNCH_CODE_CAMERA) {

                final String IMPORT = "camera";
                final String ERROR_NULL = "Unexpected error: failed to retrieve image.";

                Log.d(TAG, "onActivityResult: fetching photo from camera");

                Bundle extras = data.getExtras();
                if (extras == null) {
                    Toast.makeText(this, ERROR_NULL, Toast.LENGTH_LONG).show();
                    return;
                }

                Bitmap imageBitmap = (Bitmap) extras.get("data");
                if (imageBitmap == null) {
                    Toast.makeText(this, ERROR_NULL, Toast.LENGTH_LONG).show();
                } else {
                    useBitmap(imageBitmap, IMPORT);
                }

            } else if (requestCode == LAUNCH_CODE_GALLERY) {

                final String IMPORT = "gallery";

                Log.d(TAG, "onActivityResult: fetching photo from gallery");
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
            }
        } else {
            Toast.makeText(this, "Failed to retrieve result.", Toast.LENGTH_LONG).show();
        }
    }

    private void permissionActivities() {

        if (!hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) || !hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    REQUEST_CODE_PERMISSION_EXT_STORAGE);
        } else {
            doStorageTask();
        }
    }

    private void instantiateFragment() {

        FloatingMenuFragment floatingMenuFragment = new FloatingMenuFragment();

        // camera menu button
        floatingMenuFragment.addMenuItem(new MenuItem(getDrawable(android.R.drawable.ic_menu_camera)) {
            @Override
            public void onClick(View v) {
                if (hasCameraHardware(MainActivity.this)) {

                    if (!hasPermission(Manifest.permission.CAMERA) ||
                            !hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                LAUNCH_CODE_CAMERA);
                    } else {
                        launchCamera();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Cannot open camera. No hardware camera detected.", Toast.LENGTH_LONG).show();
                }
            }
        });

        // gallery menu button
        floatingMenuFragment.addMenuItem(new MenuItem(getDrawable(android.R.drawable.ic_menu_gallery)) {
            @Override
            public void onClick(View v) {
                if (!hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                        !hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            LAUNCH_CODE_GALLERY);
                } else {
                    launchGallery();
                }
            }
        });

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.floating_menu, floatingMenuFragment)
                .commit();
    }

    private void useBitmap(Bitmap selectedImage, String importFormat) {

        final int MAX_RES_BIG = 1600;
        final int MAX_RES_SM = 900;

        int width = selectedImage.getWidth();
        int height = selectedImage.getHeight();

        Log.d(TAG, "useBitmap: image sizing (w x h): " + width + " x " + height);

        if (width > height) {

            double bigScale = width / (double) MAX_RES_BIG;
            double smScale = height / (double) MAX_RES_SM;

            if (bigScale > smScale) {
                width = MAX_RES_BIG;
                height = (int) Math.floor(height / bigScale);
            } else {
                height = MAX_RES_SM;
                width = (int) Math.floor(width / smScale);
            }
        } else {

            double bigScale = height / (double) MAX_RES_BIG;
            double smScale = width / (double) MAX_RES_SM;

            if (bigScale > smScale) {
                height = MAX_RES_BIG;
                width = (int) Math.floor(width / bigScale);
            } else {
                width = MAX_RES_SM;
                height = (int) Math.floor(height / smScale);
            }

        }

        Log.d(TAG, "useBitmap: new image sizing (w x h): " + width + " x " + height);

        selectedImage = Bitmap.createScaledBitmap(selectedImage, width, height, true);

        String fileName = getNewBitmapName(importFormat);
        saveToInternalStorage(getOriginalSubdirectory(), fileName, selectedImage);

        Intent intent = new Intent(this, ImportPopup.class);
        intent.putExtra(ImportPopup.FILE_NAME, fileName);
        startActivityForResult(intent, REQUEST_CODE_OPEN_POPUP);
    }

    private void doStorageTask() {

        boolean success = createDirectories();

        if (!success) {
            Toast.makeText(this, "Failed to create directories. Cannot continue.", Toast.LENGTH_LONG).show();
        }

        loadColoringImages();
    }

    private boolean createDirectories() {

        boolean success;

        success = createDirectory(getImagesDirectory(), "creating main images directory: ");
        if (!success) return false;

        success = createDirectory(getOriginalSubdirectory(), "creating sub original directory: ");
        if (!success) return false;


        success = createDirectory(getColoredSubdirectory(), "creating sub colored directory: ");
        if (!success) return false;

        success = createDirectory(getBlankSubdirectory(), "creating sub blank directory: ");
        return success;
    }

    private void loadColoringImages() {

        LinkedList<CustomBitmap> customBitmaps = new LinkedList<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String bitmapsStr = sharedPreferences.getString(PREF_CBITMAPS, null);

        try {

            if (bitmapsStr != null) {
                JSONArray bitmapsArray = new JSONArray(bitmapsStr);
                for (int i = 0; i < bitmapsArray.length(); i++) {
                    customBitmaps.add(CustomBitmap.make(new JSONObject((String) bitmapsArray.get(i)),
                            getOriginalSubdirectory(), getColoredSubdirectory(), getBlankSubdirectory()));
                }
            } else {
                Log.d(TAG, "loadColoringImages: bitmapStr is null");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListView imgList = findViewById(R.id.images_list);
        imgList.setAdapter(new ImageListAdapter(this, customBitmaps));

        imgList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                        hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Intent intent = new Intent(MainActivity.this, ColoringActivity.class);
                    intent.putExtra(ColoringActivity.FILE_NAME, ((CustomBitmap) parent.getItemAtPosition(position)).getFileName());
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Requested permission not granted. Cannot continue.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean createDirectory(File dir, String logHead) {

        boolean success = true;

        Log.d(TAG, "createDirectory: creating directory path =  " + dir.getPath());

        if (!dir.exists()) {
            success = dir.mkdir();
            Log.d(TAG, "createDirectory: " + logHead + (success ? "success" : "fail"));
        } else {
            Log.d(TAG, "createDirectory: " + logHead + "already created");
        }

        return success;
    }

    private void launchCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, LAUNCH_CODE_CAMERA);
        } else {
            Toast.makeText(this, "Unexpected error: failed to launch camera.", Toast.LENGTH_LONG).show();
        }
    }

    private void launchGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, LAUNCH_CODE_GALLERY);
    }

    private boolean hasCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }
}