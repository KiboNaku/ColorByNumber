package me.nakukibo.colorbynumber;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.LinkedList;

public class ColorSet extends LinkedList<MColor>{

    private long maxDistSqr = 180;

    public ColorSet(@NonNull Collection<? extends MColor> c) {
        addAll(c);
    }

    @Override
    public void addFirst(MColor mColor) {
        if(canAdd(mColor)) super.addFirst(mColor);
    }

    @Override
    public void addLast(MColor mColor) {
        if(canAdd(mColor)) super.addLast(mColor);
    }

    @Override
    public boolean add(MColor mColor) {
        if(!canAdd(mColor)) return false;
        return super.add(mColor);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends MColor> c) {

        boolean modified = false;

        for(MColor mColor: c){
            if(canAdd(mColor)) {
                add(mColor);
                modified = true;
            }
        }

        return modified;
    }

    @Override
    public void add(int index, MColor element) {
        if(canAdd(element)) super.add(index, element);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends MColor> c) {

        boolean modified = false;

        for(MColor mColor: c){
            if(canAdd(mColor)) {
                add(index, mColor);
                modified = true;
            }
        }

        return modified;
    }

    public boolean canAdd(MColor color){

        for(MColor mColor: this){
            if(color.distanceSqrFrom(mColor) > maxDistSqr) {
                return false;
            }
        }

        return true;
    }
}
