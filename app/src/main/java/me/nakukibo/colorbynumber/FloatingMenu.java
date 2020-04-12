package me.nakukibo.colorbynumber;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FloatingMenu extends Fragment {

    private static final String TAG = "FloatingMenu";
    private FloatingActionButton btnMenu;
    private FloatingActionButton btnCamera;
    private FloatingActionButton btnGallery;
    private boolean menuOn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.floating_menu, container, false);

        Log.d(TAG, "onCreateView: floating menu created.");

        this.menuOn = false;

        btnMenu = view.findViewById(R.id.btn_menu);
        btnCamera = view.findViewById(R.id.btn_camera);
        btnGallery = view.findViewById(R.id.btn_gallery);

//        hideMenu();

//        showMenu();
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: toggling menu");
                if (menuOn) hideMenu();
                else showMenu();
            }
        });

        return view;
    }

    public void hideMenu() {
        menuOn = false;

        btnMenu.show();
        btnMenu.setScaleX(0);
        btnCamera.hide();
        btnGallery.hide();
    }

    public void showMenu() {
        menuOn = true;

        btnMenu.show();
        btnMenu.setScaleX(-1);
        btnCamera.show();
        btnGallery.show();
    }
}
