package me.nakukibo.colorbynumber;

import android.util.Log;

import java.util.Iterator;
import java.util.LinkedList;

public class ColorSet extends LinkedList<MColor>{

    private long minDistSqr = 180;

    public boolean canAdd(MColor color, boolean sorted){

        for(MColor mColor: this){
            if(color.distanceSqrFrom(mColor) < minDistSqr) {
                return false;
            }
        }

        return true;
    }


    private static final String TAG = "ColorSet";
    public void cleanse(){

        Log.d(TAG, "cleanse: starting to cleanse");

        // TODO: add running average?
        sort(new MColor.MColorComparator());

        Log.d(TAG, "cleanse: finished sorting");

        Iterator<MColor> i = iterator();
        while(i.hasNext()){

            MColor cColor = i.next();
            while(i.hasNext()){

                MColor nColor = i.next();
                if(cColor.distanceSqrFrom(nColor) < minDistSqr){
                    i.remove();
                } else {
                    break;
                }
            }
        }

        Log.d(TAG, "cleanse: finished cleansing");
    }

    public MColor getClosestColor(MColor cColor){

        double minDist = Double.MAX_VALUE;
        MColor closest = null;

        for(MColor mColor: this){

            double cDist = mColor.distanceSqrFrom(cColor);
            if(cDist <= minDist) {
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
