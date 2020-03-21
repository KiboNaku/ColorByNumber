package me.nakukibo.colorbynumber.color;

import androidx.core.graphics.ColorUtils;

public class MColor {

    private Integer color;

    public MColor(Integer color) {
        this.color = color;
    }

    public double distanceSqrFrom(MColor o) {

        double[] mLab = getLAB();
        double[] cLab = o.getLAB();

        double r0 = mLab[0] - cLab[0];
        double r1 = mLab[1] - cLab[1];
        double r2 = mLab[2] - cLab[2];

        return r0*r0 + r1*r1 + r2*r2;
    }

    public double[] getLAB(){

        double[] lab = new double[3];
        ColorUtils.colorToLAB(color, lab);
        return lab;
    }

    public Integer getColor() {
        return color;
    }
}
