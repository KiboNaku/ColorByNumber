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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.nakukibo.colorbynumber.menu.FloatingMenuFragment;
import me.nakukibo.colorbynumber.menu.MenuItem;

//TODO: reduce the minimum sdk required

public class MainActivity extends BaseActivity {

    public static final String FILE_NAME = "filename";

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_WRITE_EXT = 0;
    private static final int REQUEST_CODE_READ_EXT = 1;
    private static final int REQUEST_CODE_FETCH_PHOTO = 11;
    private static final int REQUEST_CODE_READ_GALLERY = 12;
    private static final int REQUEST_CODE_OPEN_POPUP = 13;
    private static final int LAUNCH_CODE_CAMERA = 21;
    private static final int LAUNCH_CODE_OPEN_GALLERY = 22;

    private boolean permissionsGranted = false;
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

        floatingMenuFragment = new FloatingMenuFragment();

        floatingMenuFragment.addMenuItem(new MenuItem(getDrawable(android.R.drawable.ic_menu_camera)) {
            @Override
            public void onClick(View v) {

            }
        });
        floatingMenuFragment.addMenuItem(new MenuItem(getDrawable(android.R.drawable.ic_menu_gallery)) {
            @Override
            public void onClick(View v) {

            }
        });

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.floating_menu, floatingMenuFragment)
                .commit();


//
//        btnCamera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (hasCameraHardware(getContext())) {
//
//                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
//                            != PackageManager.PERMISSION_GRANTED ||
//                            ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                                    != PackageManager.PERMISSION_GRANTED) {
//
//                        ActivityCompat.requestPermissions(getActivity(),
//                                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                                LAUNCH_CODE_CAMERA);
//                    } else {
//                        launchCamera();
//                    }
//                } else {
//                    Toast.makeText(getContext(), "Cannot open camera. No hardware camera detected.", Toast.LENGTH_LONG).show();
//                }
//
//            }
//        });
//
//        btnGallery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED ||
//                        ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                                != PackageManager.PERMISSION_GRANTED) {
//
//                    ActivityCompat.requestPermissions(getActivity(),
//                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                            REQUEST_CODE_READ_GALLERY);
//                } else {
//                    launchGallery();
//                }
//            }
//        });
//        hideMenu();
//        permissionActivities();
//        printAllImageFiles();
    }

    //
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//
//        if (requestCode == LAUNCH_CODE_CAMERA) {
//
//            if (grantResults.length > 1) {
//
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                    launchCamera();
//                } else if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(getContext(), "Cannot open camera. Camera permission not given.", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(getContext(), "Cannot open camera. Writing to memory permission not given.", Toast.LENGTH_LONG).show();
//                }
//            }
//        } else if (requestCode == REQUEST_CODE_READ_GALLERY) {
//
//            if (grantResults.length > 1) {
//
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                    launchGallery();
//                } else if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(getContext(), "Cannot open gallery. Gallery permission not given.", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(getContext(), "Cannot open gallery. Writing to memory permission not given.", Toast.LENGTH_LONG).show();
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == RESULT_OK) {
//            if (requestCode == REQUEST_CODE_FETCH_PHOTO) {
//
//                final String IMPORT = "camera";
//
//                View loadingBar = getActivity().findViewById(R.id.progress_bar_load_image);
//                final String ERROR_NULL = "Unexpected error: failed to retrieve image.";
//
//                Bundle extras = data.getExtras();
//                if (extras == null) {
//                    Toast.makeText(getContext(), ERROR_NULL, Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                Bitmap imageBitmap = (Bitmap) extras.get("data");
//                if (imageBitmap == null) {
//                    Toast.makeText(getContext(), ERROR_NULL, Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                loadingBar.setVisibility(View.VISIBLE);
////                useBitmap(imageBitmap, IMPORT);
//                loadingBar.setVisibility(View.INVISIBLE);
//
//            } else if (requestCode == LAUNCH_CODE_OPEN_GALLERY) {
//
//                final String IMPORT = "gallery";
//
//                Log.d(TAG, "onActivityResult: fetching photo from gallery");
//
//                View loadingBar = getActivity().findViewById(R.id.progress_bar_load_image);
//                loadingBar.setVisibility(View.VISIBLE);
//
//                Bitmap selectedImage = null;
//
//                try {
//
//                    final Uri imageUri = data.getData();
//
//                    if (imageUri != null) {
//                        selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                if (selectedImage == null) {
//
//                    Toast.makeText(getContext(), "Failed to retrieve image.", Toast.LENGTH_LONG).show();
//                } else {
////                    useBitmap(selectedImage, IMPORT);
//                }
//
//                loadingBar.setVisibility(View.INVISIBLE);
//            }
//        } else {
//            Toast.makeText(getContext(), "Failed to retrieve result.", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    private void launchGallery() {
//        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(i, LAUNCH_CODE_OPEN_GALLERY);
//    }
//
//    private void launchCamera() {
//
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_CODE_FETCH_PHOTO);
//        } else {
//            Toast.makeText(getContext(), "Unexpected error: failed to launch camera.", Toast.LENGTH_LONG).show();
//        }
//    }

//    private boolean hasCameraHardware(Context context) {
//        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
//    }

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