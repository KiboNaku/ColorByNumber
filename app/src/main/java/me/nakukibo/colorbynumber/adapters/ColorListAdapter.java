package me.nakukibo.colorbynumber.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.nakukibo.colorbynumber.BaseActivity;

public class ColorListAdapter extends ArrayAdapter<Integer> {

    private static final String TAG = "ColorListAdapter";

    private BaseActivity context;
    private Integer[] colors;

    public ColorListAdapter(@NonNull BaseActivity context, @NonNull Integer[] colors) {

        super(context, android.R.layout.simple_list_item_1, colors);
        this.context = context;
        this.colors = colors;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View colorImageView = convertView;

//        if(colorImageView == null){
//            colorImageView = LayoutInflater.from(context).inflate(R.layout.color_image, parent, false);
//        }
//
//        File dir = context.getColoredSubdirectory();
////        ColorImage colorImage = images[position];
//        ImageView imageView = colorImageView.findViewById(R.id.image_display);
//
//        GlideApp
//                .with(context)
//                .load(new File(dir, colorImage.getFileName()))
//                .centerCrop()
//                .into(imageView);
//
//        TextView imgName = colorImageView.findViewById(R.id.image_name);
//        imgName.setText(colorImage.getImageName());

        return colorImageView;
    }
}
