package me.nakukibo.colorbynumber.menu;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import me.nakukibo.colorbynumber.R;
import me.nakukibo.colorbynumber.adapters.MenuListAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class FloatingMenuFragment extends Fragment {

    private List<MenuItem> menuItems;
    private FloatingActionButton menuToggle;
    private MenuListAdapter menuListAdapter;
    private boolean menuOn;

    public FloatingMenuFragment() {
        menuItems = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View menuView = inflater.inflate(R.layout.fragment_floating_menu, container, false);
        final ListView menuList = menuView.findViewById(R.id.list_menu);

        menuListAdapter = new MenuListAdapter(getContext(), menuItems);

        menuToggle = menuView.findViewById(R.id.menu_toggle);
        menuOn = false;

        menuList.setVisibility(View.GONE);
        menuToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuOn) {
                    menuOn = false;
                    menuToggle.setScaleY(1f);
                    menuList.setVisibility(View.GONE);
                } else {
                    menuOn = true;
                    menuToggle.setScaleY(-1f);
                    menuList.setVisibility(View.VISIBLE);
                }
            }
        });

        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menuListAdapter.getItem(position).onClick(view);
            }
        });

        menuList.setAdapter(menuListAdapter);
        return menuView;
    }

    public void addMenuItem(MenuItem menuItem) {

        if (menuItem != null) {
            menuItems.add(menuItem);
            if (menuListAdapter != null) {
                menuListAdapter.add(menuItem);
                menuListAdapter.notifyDataSetChanged();
            }
        }
    }
}
