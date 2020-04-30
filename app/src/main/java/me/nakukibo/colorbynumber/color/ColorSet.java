package me.nakukibo.colorbynumber.color;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Collection;
import java.util.LinkedList;

public class ColorSet extends LinkedList<MColor> {

    private long minDistSqr = 180;

    public ColorSet(@NonNull Collection<? extends MColor> c) {
        addAll(c);
    }

    @Override
    public void addFirst(MColor mColor) {
        if (canAdd(mColor)) super.addFirst(mColor);
    }

    @Override
    public void addLast(MColor mColor) {
        if (canAdd(mColor)) super.addLast(mColor);
    }

    @Override
    public boolean add(MColor mColor) {
        if (canAdd(mColor)) return super.add(mColor);
        return false;
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends MColor> c) {

        boolean modified = false;

        for (MColor mColor : c) {
            if (canAdd(mColor)) {
                modified = true;
                add(mColor);
            }
        }

        return modified;
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends MColor> c) {

        boolean modified = false;

        for (MColor mColor : c) {
            if (canAdd(mColor)) {
                modified = true;
                add(index, mColor);
            }
        }

        return modified;
    }

    @Override
    public MColor set(int index, MColor element) {
        if (canAdd(element)) return super.set(index, element);
        return null;
    }

    @Override
    public void add(int index, MColor element) {
        if (canAdd(element)) super.add(index, element);
    }

    public boolean canAdd(MColor color) {

        for (MColor mColor : this) {
            if (color.distanceSqrFrom(mColor) < minDistSqr) {
                return false;
            }
        }

        return true;
    }

    public ColorSet(@NonNull Bitmap bitmap) {

        for (int i = 0; i < bitmap.getHeight(); i++) {
            for (int j = 0; j < bitmap.getWidth(); j++) {
                add(new MColor(bitmap.getPixel(j, i)));
            }
        }
    }

    public ColorSet(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            add(new MColor(jsonArray.getInt(i)));
        }
    }

    @NonNull
    public JSONArray toJsonArray() {

        JSONArray colorsArray = new JSONArray();
        for (MColor mColor : this) {
            System.out.print(mColor.getColor() + " ");
            colorsArray.put(mColor.getColor());
        }
        return colorsArray;
    }

    public MColor getClosestColor(MColor cColor) {

        double minDist = Double.MAX_VALUE;
        MColor closest = null;

        for (MColor mColor : this) {

            double cDist = mColor.distanceSqrFrom(cColor);
            if (cDist <= minDist) {
                minDist = cDist;
                closest = mColor;
            }
        }

        return closest;
    }

    public long getMinDistSqr() {
        return minDistSqr;
    }

    public void setMinDistSqr(long minDistSqr) {
        this.minDistSqr = minDistSqr;
    }
}
