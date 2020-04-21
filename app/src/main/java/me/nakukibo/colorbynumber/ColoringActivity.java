package me.nakukibo.colorbynumber;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ColoringActivity extends BaseActivity {

    public static final String FILE_NAME = "filename";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coloring);

        String fileName = getIntent().getStringExtra(FILE_NAME);
        File directory = getBlankSubdirectory();

        File f = new File(directory, fileName);

        try {
            ((ImageView) findViewById(R.id.image_coloring)).setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(f)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

//    public void selectColor(View view) {
//
//        int id = view.getId();
//        boolean foundView = false;
//
//        Log.d(TAG, "selectColor: colorview size=" + viewList.size());
//
//        for (int i = 0; i < viewList.size(); i++) {
//            if (id == viewList.get(i)) {
//                viewIndex = i;
//                foundView = true;
//            }
//        }
//
//        if (!foundView) Log.d(TAG, "selectColor: cannot find view");
//
//        color = ((ColorView) view).getColor();
//        Log.d(TAG, "selectColor: color = (" + Color.red(color) + ", " + Color.green(color) + ", " + Color.blue(color) + ")");
//    }

    //    public void paintColor(View view) {
//
//        if (customBitmap == null || customBitmap.getBlank() == null || color == null || viewIndex < 0)
//            return;
//
//        Log.d(TAG, "paintColor: color is painting");
//        Log.d(TAG, "paintColor: painting color " + "(" + Color.red(color) + ", " + Color.green(color) + ", " + Color.blue(color) + ")");
//
//
//        Bitmap colored = customBitmap.getColored();
//        Bitmap blank = customBitmap.getBlank();
//
//        for (int i = 0; i < blank.getHeight(); i++) {
//            for (int j = 0; j < blank.getWidth(); j++) {
//                if (colored.getPixel(j, i) == color) {
//                    blank.setPixel(j, i, color);
//                }
//            }
//        }
//
//        ColorView colorView = ((ColorView) findViewById(viewList.get(viewIndex)));
//
//        if (colors.size() > 0) colorView.setColor(colors.pop().getColor());
//        else colorView.setVisibility(View.GONE);
//
//        color = -1;
//        viewIndex = -1;
//
//        ((ImageView) findViewById(R.id.image_view_retrieved)).setImageBitmap(customBitmap.getBlank());
//
//    }


}
