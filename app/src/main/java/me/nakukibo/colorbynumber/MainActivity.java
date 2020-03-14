package me.nakukibo.colorbynumber;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;

//TODO: reduce the minimum sdk required

public class MainActivity extends ColoringAppCompatActivity {

    private String fileName = null;
    private static final String TAG = "MainActivity";

    public static final String FILE_NAME = "filename";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadImageFromStorage();

    }

    public boolean createDirectories(){

        boolean success;

        success = getImagesDirectory().mkdir();
        Log.d(TAG, "createDirectories: creating main images directory: " + (success ? "success": "fail"));
        if(!success) return false;

        success = getOriginalSubdirectory().mkdir();
        Log.d(TAG, "createDirectories: creating sub original directory: " + (success ? "success": "fail"));
        if(!success) return false;

        success = getColoredSubdirectory().mkdir();
        Log.d(TAG, "createDirectories: creating sub colored directory: " + (success ? "success": "fail"));
        if(!success) return false;

        success = getBlankSubdirectory().mkdir();
        Log.d(TAG, "createDirectories: creating sub blank directory: " + (success ? "success": "fail"));

        return success;
    }

    public void launchImporter(View view){
        Intent intent = new Intent(this, ImporterActivity.class);
        startActivity(intent);
    }

    public void launchColoring(View view){
        Intent intent = new Intent(this, ColoringActivity.class);

        intent.putExtra(FILE_NAME, fileName);
        startActivity(intent);
    }

    // temporary load code
    private void loadImageFromStorage() {

//        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("images", Context.MODE_PRIVATE);

            File[] files = directory.listFiles();

            if(files == null) return;

            Log.d("Files", "Size: "+ files.length);
            for (int i = 0; i < files.length; i++)
            {
                Log.d("Files", "FileName:" + files[i].getName());
//                File file = new File(directory, files[i].getName());
//                file.delete();
            }

//            fileName = files[0].getName();

//            File f = new File(directory, fileName);
//            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
//            ImageView img = findViewById(R.id.image_view_loaded);
//            img.setImageBitmap(b);
//        }
//        catch (FileNotFoundException e)
//        {
//            e.printStackTrace();
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadImageFromStorage();
    }
}