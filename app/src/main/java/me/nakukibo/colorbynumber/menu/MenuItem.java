package me.nakukibo.colorbynumber.menu;

import android.graphics.drawable.Drawable;
import android.view.View;

public abstract class MenuItem implements View.OnClickListener {

    private Drawable drawable;

    public MenuItem(Drawable drawable) {
        this.drawable = drawable;
    }

    public Drawable getDrawable() {
        return drawable;
    }
}
