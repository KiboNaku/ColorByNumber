package me.nakukibo.colorbynumber.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import me.nakukibo.colorbynumber.R;

public class FloatingMenu extends LinearLayout {

    private static final String TAG = "FloatingMenu";
    private FloatingActionButton btnMenu;
    private FloatingActionButton btnCamera;
    private FloatingActionButton btnGallery;
    private boolean menuOn;

    public FloatingMenu(Context context) {
        this(context, null);
    }

    public FloatingMenu(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public FloatingMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        inflate(context, R.layout.floating_menu, null);
    }


    //    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.floating_menu, container, false);
//
//        Log.d(TAG, "onCreateView: floating menu created.");
//
////        this.menuOn = false;
////
////        btnMenu = view.findViewById(R.id.btn_menu);
////        btnCamera = view.findViewById(R.id.btn_camera);
////        btnGallery = view.findViewById(R.id.btn_gallery);
////
//////        hideMenu();
////
//////        showMenu();
////        btnMenu.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                Log.d(TAG, "onClick: toggling menu");
////                if (menuOn) hideMenu();
////                else showMenu();
////            }
////        });
//
//        return view;
//    }


}
