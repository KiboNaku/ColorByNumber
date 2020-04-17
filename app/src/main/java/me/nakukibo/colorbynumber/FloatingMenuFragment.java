package me.nakukibo.colorbynumber;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class FloatingMenuFragment extends Fragment {

    private static final String TAG = "FloatingMenuFragment";

    private static final int REQUEST_CODE_FETCH_PHOTO = 11;
    private static final int REQUEST_CODE_READ_GALLERY = 12;

    private static final int LAUNCH_CODE_CAMERA = 21;
    private static final int LAUNCH_CODE_OPEN_GALLERY = 22;

    public FloatingMenuFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View menuView = inflater.inflate(R.layout.fragment_floating_menu, container, false);

//        btnToggle = view.findViewById(R.id.btn_toggle);
//        btnCamera = view.findViewById(R.id.btn_camera);
//        btnGallery = view.findViewById(R.id.btn_gallery);
//
//        showMenu();
//        btnToggle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (menuOn) hideMenu();
//                else showMenu();
//            }
//        });
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

        final Drawable[] drawables = {
                getResources().getDrawable(android.R.drawable.ic_menu_camera, getActivity().getTheme()),
                getResources().getDrawable(android.R.drawable.ic_menu_gallery, getActivity().getTheme())};
        ListView menuList = menuView.findViewById(R.id.list_menu);
        DrawableListAdapter menuAdapter = new DrawableListAdapter(getContext(), drawables);

//        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getActivity(), drawables[position], Toast.LENGTH_LONG).show();
//            }
//        });

        menuList.setAdapter(menuAdapter);

        return menuView;
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
}
