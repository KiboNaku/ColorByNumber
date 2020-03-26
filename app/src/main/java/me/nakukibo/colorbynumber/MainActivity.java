package me.nakukibo.colorbynumber;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

//TODO: reduce the minimum sdk required

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    public static final String FILE_NAME = "filename";

    private static final int REQUEST_CODE_WRITE_EXT = 0;
    private static final int REQUEST_CODE_READ_EXT = 1;

    private String fileName = null;
    private boolean permissionsGranted = true;

    //TODO: add shared preferences to timestamp most recent images

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionActivities();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadImageFromStorage();
    }

    private void permissionActivities(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_WRITE_EXT);
        } else {

            boolean success = createDirectories();
            if(!success){

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

    public boolean createDirectories(){

        boolean success;

        success = createDirectory(getImagesDirectory(), "creating main images directory: ");
        if(!success) return false;

        success = createDirectory(getOriginalSubdirectory(), "creating sub original directory: ");
        if(!success) return false;


        success = createDirectory(getColoredSubdirectory(), "creating sub colored directory: ");
        if(!success) return false;

        success = createDirectory(getBlankSubdirectory(), "creating sub blank directory: ");
        return success;
    }

    private boolean createDirectory(File dir, String logHead){

        boolean success = true;

        Log.d(TAG, "createDirectory: creating directory path =  " + dir.getPath());

        if(!dir.exists()) {
            success = dir.mkdir();
            Log.d(TAG, "createDirectory: " + logHead + (success ? "success": "fail"));
        } else {
            Log.d(TAG, "createDirectory: " + logHead + "already created");
        }

        return success;
    }

    public void launchImporter(View view){

        if(permissionsGranted) {
            Intent intent = new Intent(this, ConversionActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Requested permission not granted. Cannot continue.", Toast.LENGTH_LONG).show();
        }
    }

    public void launchColoring(View view){

        if(permissionsGranted) {
            Intent intent = new Intent(this, ColoringActivity.class);
            intent.putExtra(FILE_NAME, fileName);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Requested permission not granted. Cannot continue.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        allowContinuation();

        if(requestCode == REQUEST_CODE_WRITE_EXT) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                boolean success = createDirectories();
                if(!success){

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

    private void allowContinuation(){permissionsGranted = true;}

    private void lockContinuation(){
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