package me.nakukibo.colorbynumber;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

//TODO: reduce the minimum sdk required

public class MainActivity extends AppCompatActivity {

    private String fileName = null;
    public static final String FILE_NAME = "filename";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadImageFromStorage();

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
//
//        try {
//            ContextWrapper cw = new ContextWrapper(getApplicationContext());
//            File directory = cw.getDir("images", Context.MODE_PRIVATE);
//
//            File[] files = directory.listFiles();
//            Log.d("Files", "Size: "+ files.length);
//            for (int i = 0; i < files.length; i++)
//            {
//                Log.d("Files", "FileName:" + files[i].getName());
//            }
//
////            fileName = files[0].getName();
//
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