package me.nakukibo.colorbynumber;

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

    public void cleanse(){

        sort(new MColor.MColorComparator());

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
    }

    public long getMinDistSqr() {
        return minDistSqr;
    }

    public void setMinDistSqr(long minDistSqr) {
        this.minDistSqr = minDistSqr;
    }
}
