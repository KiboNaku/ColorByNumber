package me.nakukibo.colorbynumber;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.nakukibo.colorbynumber.fragments.FloatingMenuFragment;

//TODO: reduce the minimum sdk required

public class MainActivity extends BaseActivity {

    public static final String FILE_NAME = "filename";

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_WRITE_EXT = 0;
    private static final int REQUEST_CODE_READ_EXT = 1;
    private static final int REQUEST_CODE_OPEN_POPUP = 13;

    private boolean permissionsGranted = false;
    private boolean menuOn;
    private FloatingActionButton menuToggle;
    private FloatingMenuFragment floatingMenuFragment;


    //TODO: add shared preferences to timestamp most recent images
    //TODO: add placeholders and stuff later

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
//        // the results will be higher than using the activity context object or the getWindowManager() shortcut
//        wm.getDefaultDisplay().getMetrics(displayMetrics);
//        int screenWidth = displayMetrics.widthPixels;
//        int screenHeight = displayMetrics.heightPixels;
//
//        Log.d(TAG, "onCreate: width=" + screenWidth + ", height=" + screenHeight);
//
//        printAllImageFiles();
//        File[] files = getColoredSubdirectory().listFiles();
//        if(files != null) {
//            List<ColorImage> colorImages = new ArrayList<>();
//
//            for(int i=0; i<files.length; i++){
//                colorImages.add(new ColorImage(files[i].getName()));
//            }
//
//            ListView imgList = findViewById(R.id.images_list);
//            imgList.setAdapter(new ImageListAdapter(this, colorImages));
//
//            imgList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    if (permissionsGranted) {
//                        Intent intent = new Intent(MainActivity.this, ColoringActivity.class);
//                        intent.putExtra(FILE_NAME, ((ColorImage) parent.getItemAtPosition(position)).getFileName());
//                        startActivity(intent);
//                    } else {
//                        Toast.makeText(MainActivity.this, "Requested permission not granted. Cannot continue.", Toast.LENGTH_LONG).show();
//                    }
//                }
//            });
//
//        }
        menuToggle = findViewById(R.id.menu_toggle);


        menuToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuOn) hideMenu();
                else showMenu();
            }
        });


        floatingMenuFragment = new FloatingMenuFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.floating_menu, floatingMenuFragment)
                .commit();

        this.menuOn = false;
        hideMenu();
//        permissionActivities();
//        printAllImageFiles();
    }

    public void hideMenu() {

        Log.d(TAG, "hideMenu: hiding menu");

        menuOn = false;
        menuToggle.setScaleY(1f);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .hide(floatingMenuFragment)
                .commit();
    }

    public void showMenu() {

        Log.d(TAG, "showMenu: showing menu");

        menuOn = true;
        menuToggle.setScaleY(-1f);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .show(floatingMenuFragment)
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

        File originalFile = saveToInternalStorage(getOriginalSubdirectory(), fileName, selectedImage);

        ImageView photoImageView = findViewById(R.id.image_view_retrieved);
        photoImageView.setImageBitmap(selectedImage);

        Intent intent = new Intent(this, ImportPopup.class);
        intent.putExtra(FILE_NAME, fileName);
        startActivityForResult(intent, REQUEST_CODE_OPEN_POPUP);
    }

    private String getNewBitmapName(String importFormat) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        return importFormat + "_" + timeStamp + ".jpg";
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadImageFromStorage();
    }

    private void permissionActivities() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_WRITE_EXT);
        } else {

            boolean success = createDirectories();
            if (!success) {

                Toast.makeText(this, "Failed to create directories. Cannot continue.\n", Toast.LENGTH_LONG).show();
                lockContinuation();
                return;
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_READ_EXT);
        } else {
            loadImageFromStorage();
        }

    }

    public boolean createDirectories() {

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

    public void launchImporter(View view) {

        if (permissionsGranted) {
            Intent intent = new Intent(this, ConversionActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Requested permission not granted. Cannot continue.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        allowContinuation();

        if (requestCode == REQUEST_CODE_WRITE_EXT) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                boolean success = createDirectories();
                if (!success) {

                    Toast.makeText(this, "Failed to create directories. Cannot continue.", Toast.LENGTH_LONG).show();
                    lockContinuation();
                }
            } else {
                Toast.makeText(this, "Cannot create directories. Permission not granted.", Toast.LENGTH_LONG).show();
                lockContinuation();
            }
        } else if (requestCode == REQUEST_CODE_READ_EXT) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImageFromStorage();
            } else {
                Toast.makeText(this, "Unable to read files. Permission not granted.", Toast.LENGTH_LONG).show();
                lockContinuation();
            }
        }
    }

    private void allowContinuation() {
        permissionsGranted = true;
    }

    private void lockContinuation() {
        permissionsGranted = false;
    }

    // temporary load code
    private void loadImageFromStorage() {

//        try {
//            File directory = getOriginalSubdirectory();
//
//            File[] files = directory.listFiles();
//
//            if(files == null) return;
//
//            Log.d("Files", "Size: "+ files.length);
//            for (int i = 0; i < files.length; i++)
//            {
//                Log.d("Files", "FileName:" + files[i].getName());
//                File file = new File(directory, files[i].getName());
//                file.delete();
//            }
//
//            fileName = files[0].getName();
//
//            File f = new File(directory, fileName);
//            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
//            ImageView img = findViewById(R.id.image_view_loaded);
//            img.setImageBitmap(b);
//        }
//        catch (FileNotFoundException | NullPointerException e)
//        {
//            e.printStackTrace();
//        }

    }
}