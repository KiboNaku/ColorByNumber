package me.nakukibo.colorbynumber.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import me.nakukibo.colorbynumber.R;
import me.nakukibo.colorbynumber.menu.MenuItem;

public class MenuListAdapter extends ArrayAdapter<MenuItem> {

    public MenuListAdapter(@NonNull Context context, @NonNull List<MenuItem> menuItems) {
        super(context, android.R.layout.simple_list_item_1, menuItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View drawableView = convertView;
        MenuItem menuItem = getItem(position);

        if (drawableView == null) {
            drawableView = LayoutInflater.from(getContext()).inflate(R.layout.drawable_item, parent, false);
        }

        if (menuItem != null) {
            drawableView.findViewById(R.id.view_drawable).setBackground(menuItem.getDrawable());
        }

        return drawableView;
    }
}
