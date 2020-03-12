package me.nakukibo.colorbynumber;

import androidx.core.graphics.ColorUtils;

public class MColor implements Comparable<MColor>{

    private Integer color;

    public MColor(Integer color) {
        this.color = color;
    }

    @Override
    public int compareTo(MColor o) {

        double[] mLab = getLAB();
        double[] compLab = o.getLAB();

        double mSum = mLab[0] + mLab[1] + mLab[2];
        double compSum = compLab[0] + compLab[1] + compLab[2];

        return Double.compare(mSum, compSum);
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

    public void setColor(Integer color) {
        this.color = color;
    }
}
