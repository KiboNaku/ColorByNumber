package me.nakukibo.colorbynumber;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ImporterActivity extends ColoringAppCompatActivity {

    private static final int REQUEST_CODE_FETCH_PHOTO = 1;
    private static final int REQUEST_CODE_CAMERA = 1;

    private static final int REQUEST_CODE_OPEN_GALLERY = 2;
    private static final int REQUEST_CODE_READ_EXT = 2;

    private CustomBitmap customBitmap;
    private ImageView imageViewRetrievedPhoto;
    private Bitmap uncolored = null;
    private Integer color = null;
    private ColorSet colors = null;
    private List<Integer> viewList = null;
    private int viewIndex = -1;

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

                findViewById(R.id.progress_bar_load_image).setVisibility(View.VISIBLE);

                // TODO: fix camera code

//                final CustomBitmap customBitmap = new CustomBitmap(imageBitmap);
//                customBitmap.convert(new CustomBitmap.OnCompleteListener() {
//                    @Override
//                    public void onComplete() {
//                        imageViewRetrievedPhoto.setImageBitmap(customBitmap.getColored());
//                        findViewById(R.id.progress_bar_load_image).setVisibility(View.INVISIBLE);
//                    }
//                });
            } else if(requestCode == REQUEST_CODE_OPEN_GALLERY) {

                Log.d(TAG, "onActivityResult: fetching photo from gallery");
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                final Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                Bitmap imageBitmap = BitmapFactory.decodeFile(picturePath);

                findViewById(R.id.progress_bar_load_image).setVisibility(View.VISIBLE);

                customBitmap = new CustomBitmap(imageBitmap, new CustomBitmap.OnCompleteListener() {
                    @Override
                    public void onComplete() {
                        imageViewRetrievedPhoto.setImageBitmap(customBitmap.getColored());
//                        saveToInternalStorage(customBitmap);
                        findViewById(R.id.progress_bar_load_image).setVisibility(View.INVISIBLE);

                        findViewById(R.id.layout_colors).setVisibility(View.VISIBLE);
                        HorizontalScrollView colorsScroll = findViewById(R.id.scroll_colors);

                        colors = customBitmap.getUniqueColors();

                        Log.d(TAG, "onComplete: number of colors = " + colors.size());

                        viewList = new ArrayList<>();
                        viewList.add(R.id.color);
                        viewList.add(R.id.color1);
                        viewList.add(R.id.color2);
                        viewList.add(R.id.color3);
                        viewList.add(R.id.color4);
                        viewList.add(R.id.color5);
                        viewList.add(R.id.color6);
                        viewList.add(R.id.color7);
                        viewList.add(R.id.color8);
                        viewList.add(R.id.color9);

                        for(int i=0; i<Math.min(colors.size(), 10); i++){
                            int color = colors.pop().getColor();
                            Log.d(TAG, "selectColor: color = (" + Color.red(color) + ", " + Color.green(color) + ", " + Color.blue(color) + ")");
                            ((ColorView) findViewById(viewList.get(i))).setColor(color);
                        }

//                        for(Integer color: customBitmap.getUniqueColors()){
//                            Log.d(TAG, "onComplete: color = (" + Color.red(color) + ", " + Color.green(color) + ", " + Color.blue(color) + ")");
//                            View colorView = new View(ImporterActivity.this);
//                            colorView.setBackgroundColor(color);
//                            colorsScroll.addView(colorView, 200, 200);
//                        }
                    }
                });
                customBitmap.execute();
            }
        } else {
            Toast.makeText(this, "Failed to retrieve result.", Toast.LENGTH_LONG).show();
        }
    }

    public void paintColor(View view){


        if(customBitmap == null || customBitmap.getBlank() == null || color == null || viewIndex < 0) return;

        Log.d(TAG, "paintColor: color is painting");
        Log.d(TAG, "paintColor: painting color " + "(" + Color.red(color) + ", " + Color.green(color) + ", " + Color.blue(color) + ")");

        
        Bitmap colored = customBitmap.getColored();
        Bitmap blank = customBitmap.getBlank();

        for(int i=0; i<blank.getHeight(); i++){
            for(int j=0; j<blank.getWidth(); j++){
                if(colored.getPixel(j, i) == color){
                    blank.setPixel(j, i, color);
                }
            }
        }

        ColorView colorView =  ((ColorView) findViewById(viewList.get(viewIndex)));

        if(colors.size() > 0) colorView.setColor(colors.pop().getColor());
        else colorView.setVisibility(View.GONE);

        color = -1;
        viewIndex = -1;

        ((ImageView) findViewById(R.id.image_view_retrieved)).setImageBitmap(customBitmap.getBlank());

    }

    public void selectColor(View view){

        int id = view.getId();
        boolean foundView = false;

        Log.d(TAG, "selectColor: colorview size=" + viewList.size());

        for(int i=0; i<viewList.size(); i++){
            if(id == viewList.get(i)) {
                viewIndex = i;
                foundView = true;
            }
        }
        
        if(!foundView) Log.d(TAG, "selectColor: cannot find view");

        color = ((ColorView) view).getColor();
        Log.d(TAG, "selectColor: color = (" + Color.red(color) + ", " + Color.green(color) + ", " + Color.blue(color) + ")");
    }

    private boolean hasCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    //temporary save code

    private void saveToInternalStorage(CustomBitmap customBitmap){


//
//        try {
//            FileOutputStream fOut = openFileOutput("data", Context.);
//            fOut.write(newTask.getTitle().getBytes());
//            fOut.write(newTask.getYear());
//            fOut.write(newTask.getMonth());
//            fOut.write(newTask.getDay());
//            fOut.write(newTask.getHour());
//            fOut.write(newTask.getMinute());
//            fOut.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        public void readData(){
//            FileInputStream fis = null;
//            try {
//                fis = new FileInputStream("data");
//                System.out.println("Total file size to read (in bytes) : "
//                        + fis.available());
//                int content;
//                while ((content = fis.read()) != -1) {
//                    // convert to char and display it
//                    System.out.print((char) content);
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (fis != null)
//                        fis.close();
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }

//        Saving (w/o exception handling code):

        Log.d(TAG, "saveToInternalStorage: attempting to save");

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("images", Context.MODE_PRIVATE);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.US).format(new Date());
        String imageFileName = "Coloring_" + timeStamp;

        File imagePath = new File(directory, imageFileName + ".txt");

        FileOutputStream fos = null;
        ObjectOutputStream os = null;

        try {
            Log.d(TAG, "saveToInternalStorage: fileName = " + imagePath.getAbsolutePath());

            fos = openFileOutput(imageFileName, Context.MODE_PRIVATE);

            os = new ObjectOutputStream(fos);
            os.writeObject(this);

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            try{
                if(os != null) os.close();
                if(fos != null) fos.close();
            } catch (IOException io) {
                io.printStackTrace();
            }
        }

//        Loading (w/o exception handling code):
//
//        FileInputStream fis = context.openFileInput(fileName);
//        ObjectInputStream is = new ObjectInputStream(fis);
//        SimpleClass simpleClass = (SimpleClass) is.readObject();
//        is.close();
//        fis.close();

    }

//    /// <summary>
//    /// Serializes an object.
//    /// </summary>
//    /// <typeparam name="T"></typeparam>
//    /// <param name="serializableObject"></param>
//    /// <param name="fileName"></param>
//    public void SerializeObject<T>(T serializableObject, string fileName)
//    {
//        if (serializableObject == null) { return; }
//
//        try
//        {
//            XmlDocument xmlDocument = new XmlDocument();
//            XmlSerializer serializer = new XmlSerializer(serializableObject.GetType());
//            using (MemoryStream stream = new MemoryStream())
//            {
//                serializer.Serialize(stream, serializableObject);
//                stream.Position = 0;
//                xmlDocument.Load(stream);
//                xmlDocument.Save(fileName);
//            }
//        }
//        catch (Exception ex)
//        {
//            //Log exception here
//        }
//    }
//
//
//    /// <summary>
//    /// Deserializes an xml file into an object list
//    /// </summary>
//    /// <typeparam name="T"></typeparam>
//    /// <param name="fileName"></param>
//    /// <returns></returns>
//    public T DeSerializeObject<T>(string fileName)
//    {
//        if (string.IsNullOrEmpty(fileName)) { return default(T); }
//
//        T objectOut = default(T);
//
//        try
//        {
//            XmlDocument xmlDocument = new XmlDocument();
//            xmlDocument.Load(fileName);
//            string xmlString = xmlDocument.OuterXml;
//
//            using (StringReader read = new StringReader(xmlString))
//            {
//                Type outType = typeof(T);
//
//                XmlSerializer serializer = new XmlSerializer(outType);
//                using (XmlReader reader = new XmlTextReader(read))
//                {
//                    objectOut = (T)serializer.Deserialize(reader);
//                }
//            }
//        }
//        catch (Exception ex)
//        {
//            //Log exception here
//        }
//
//        return objectOut;
//    }
}