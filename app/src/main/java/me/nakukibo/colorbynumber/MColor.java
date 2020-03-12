package me.nakukibo.colorbynumber;

import androidx.core.graphics.ColorUtils;

import java.util.Comparator;

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

    public void setColor(Integer color) {
        this.color = color;
    }

    static class MColorComparator implements Comparator<MColor> {
        @Override
        public int compare(MColor o1, MColor o2) {
            double[] lab1 = o1.getLAB();
            double[] lab2 = o2.getLAB();

            double sum1 = lab1[0] + lab1[1] + lab1[2];
            double sum2 = lab2[0] + lab2[1] + lab2[2];

            return Double.compare(sum1, sum2);
        }
    }
}
