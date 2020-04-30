package me.nakukibo.colorbynumber.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.List;

import me.nakukibo.colorbynumber.BaseActivity;
import me.nakukibo.colorbynumber.R;
import me.nakukibo.colorbynumber.bitmap.CustomBitmap;
import me.nakukibo.colorbynumber.utils.GlideApp;

public class ImageListAdapter extends ArrayAdapter<CustomBitmap> {

    private static final String TAG = "ImageListAdapter";

    private BaseActivity context;
    private List<CustomBitmap> customBitmaps;

    public ImageListAdapter(@NonNull BaseActivity context, @NonNull List<CustomBitmap> customBitmaps) {
        super(context, R.layout.color_image, customBitmaps);
        this.context = context;
        this.customBitmaps = customBitmaps;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //TODO: add error and other fallback customBitmaps to GlideApp

        View colorImageView = convertView;

        if(colorImageView == null){
            colorImageView = LayoutInflater.from(context).inflate(R.layout.color_image, parent, false);
        }

        File dir = context.getColoredSubdirectory();
        final CustomBitmap customBitmap = customBitmaps.get(position);
        ImageView imageView = colorImageView.findViewById(R.id.image_display);

        FloatingActionButton btnDel = colorImageView.findViewById(R.id.btn_delete);
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customBitmaps.remove(position);
                context.deleteImage(customBitmap.getFileName());
                notifyDataSetChanged();
            }
        });
        btnDel.setFocusable(false);

        GlideApp
                .with(context)
                .load(new File(dir, customBitmap.getFileName()))
                .centerCrop()
                .into(imageView);

        TextView imgName = colorImageView.findViewById(R.id.image_name);
        imgName.setText(customBitmap.getFileName());

        return colorImageView;
    }
}
