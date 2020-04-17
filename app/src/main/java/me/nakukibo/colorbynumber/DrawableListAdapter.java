package me.nakukibo.colorbynumber;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DrawableListAdapter extends ArrayAdapter<Drawable> {

    private static final String TAG = "ColorListAdapter";

    Drawable[] drawables;

    public DrawableListAdapter(@NonNull Context context, @NonNull Drawable[] drawables) {
        super(context, R.layout.color_image, drawables);
        this.drawables = drawables;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View drawableView = convertView;

        if (drawableView == null) {
            drawableView = LayoutInflater.from(getContext()).inflate(R.layout.drawable_item, parent, false);
        }

        drawableView.findViewById(R.id.view_drawable).setBackground(drawables[position]);

        return drawableView;
    }
}
