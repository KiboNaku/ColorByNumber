package me.nakukibo.colorbynumber;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

import static me.nakukibo.colorbynumber.BaseActivity.getBitmap;

public class ImageListAdapter extends ArrayAdapter<ColorImage> {

    private BaseActivity context;
    private ColorImage[] images;

    public ImageListAdapter(@NonNull BaseActivity context, @NonNull ColorImage[] objects) {
        super(context, R.layout.color_image, objects);
        this.context = context;
        this.images = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View colorImageView = convertView;

        if(colorImageView == null){
            colorImageView = LayoutInflater.from(context).inflate(R.layout.color_image, parent, false);
        }

        File dir = context.getColoredSubdirectory();
        ColorImage colorImage = images[position];
        ImageView imageView = colorImageView.findViewById(R.id.image_display);

//        GlideApp
//                .with(context)
//                .load(new File(dir, colorImage.getFileName()))
//                .error(R.id.button_cancel)
//                .centerCrop()
//                .into(imageView);

        imageView.setImageBitmap(getBitmap(context.getColoredSubdirectory(), colorImage.getFileName().substring(0, colorImage.getFileName().length()-4)));

        TextView imgName = colorImageView.findViewById(R.id.image_name);
        imgName.setText(colorImage.getImageName());

        return colorImageView;
    }
}
