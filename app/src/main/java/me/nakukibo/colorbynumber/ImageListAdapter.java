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

import me.nakukibo.colorbynumber.utils.GlideApp;

public class ImageListAdapter extends ArrayAdapter<ColorImage> {

    private static final String TAG = "ImageListAdapter";

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

        //TODO: add error and other fallback images to GlideApp

        View colorImageView = convertView;

        if(colorImageView == null){
            colorImageView = LayoutInflater.from(context).inflate(R.layout.color_image, parent, false);
        }

        File dir = context.getColoredSubdirectory();
        ColorImage colorImage = images[position];
        ImageView imageView = colorImageView.findViewById(R.id.image_display);

        GlideApp
                .with(context)
                .load(new File(dir, colorImage.getFileName()))
                .centerCrop()
                .into(imageView);

        TextView imgName = colorImageView.findViewById(R.id.image_name);
        imgName.setText(colorImage.getImageName());

        return colorImageView;
    }
}
