package me.nakukibo.colorbynumber;

import android.graphics.Bitmap;

import androidx.core.graphics.ColorUtils;

import java.util.LinkedList;

class BitmapConversion {
    
    static void convert(Bitmap photo){
        boolean[][] changedColor = new boolean[photo.getHeight()][photo.getWidth()];

        while(getNextFalseInArray(changedColor) != null){
            setToAverage(photo, getNextFalseInArray(changedColor), changedColor);
        }

    }

    private static void setToAverage(Bitmap photo, PixelCoordinates nextFalse, boolean[][] changedColor) {
        int pixel = photo.getPixel(nextFalse.x, nextFalse.y);

        changedColor[nextFalse.y][nextFalse.x] = true;

        LinkedList<PixelCoordinates> surColors = new LinkedList<>();
        LinkedList<PixelCoordinates> changedColors = new LinkedList<>();

        surColors.add(new PixelCoordinates(nextFalse.x, nextFalse.y));
        changedColors.add(new PixelCoordinates(nextFalse.x, nextFalse.y));

        while(!surColors.isEmpty()){
            PixelCoordinates pixelCoordinates = surColors.pop();
            pushNeighbors(photo, surColors, changedColors, pixelCoordinates, pixel, changedColor);
        }

        while(!changedColors.isEmpty()){
            PixelCoordinates pixelCoordinates = changedColors.pop();
            photo.setPixel(pixelCoordinates.x, pixelCoordinates.y, pixel);
        }
    }

    private static void pushNeighbors(Bitmap photo, LinkedList<PixelCoordinates> surColors, LinkedList<PixelCoordinates> changed,
                                      PixelCoordinates curPixel,  int color, boolean[][] changedColor) {
        int minX = curPixel.x > 0 ? curPixel.x - 1 : curPixel.x;
        int maxX = curPixel.x < changedColor[0].length-1 ? curPixel.x + 1 : curPixel.x;

        int minY = curPixel.y > 0 ? curPixel.y - 1 : curPixel.y;
        int maxY = curPixel.y < changedColor.length-1 ? curPixel.y + 1 : curPixel.y;

        for(int i=minY; i<=maxY; i++){
            for(int j=minX; j<=maxX; j++){

                if(!changedColor[i][j] && similarColors(color, photo.getPixel(j, i))) {
                    changedColor[i][j] = true;
                    surColors.add(new PixelCoordinates(j, i));
                    changed.add(new PixelCoordinates(j, i));
                }

            }
        }
    }

    // TODO: improve color comparison algorithm
    private static boolean similarColors(int color, int pixel) {
//        final int MAX_DISTANCE = 4000; //for rgb search
        final double MAX_DISTANCE = 150.0; //lab search

        double[] hsl1 = new double[3];
        double[] hsl2 = new double[3];
        ColorUtils.colorToLAB(color, hsl1);
        ColorUtils.colorToLAB(pixel, hsl2);

//        Log.d(TAG, "similarColors: hsl1 = " + hsl1[0] +", "+ hsl1[1] +", "+ hsl1[2]);
//        Log.d(TAG, "similarColors: hsl2 = " + hsl2[0] +", "+ hsl2[1] +", "+ hsl2[2]);

//        int r1 = Color.red(color);
//        int g1 = Color.green(color);
//        int b1 = Color.blue(color);
//
//        int r2 = Color.red(pixel);
//        int g2 = Color.green(pixel);
//        int b2 = Color.blue(pixel);

//        return (r1-r2)*(r1-r2) + (g1-g2)*(g1-g2) + (b1-b2)*(b1-b2) < MAX_DISTANCE;
        return (hsl1[0] - hsl2[0])*(hsl1[0] - hsl2[0]) + (hsl1[1] - hsl2[1])*(hsl1[1] - hsl2[1]) + (hsl1[2] - hsl2[2])*(hsl1[2] - hsl2[2]) <= MAX_DISTANCE;
    }

    private static PixelCoordinates getNextFalseInArray(boolean[][] booleanArray){
        for(int i=0; i<booleanArray.length; i++){
            for(int j=0; j<booleanArray[0].length; j++){
                if(!booleanArray[i][j]) return new PixelCoordinates(j, i);
            }
        }
        return null;
    }

    private static class PixelCoordinates {
        private int x;
        private int y;

        PixelCoordinates(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
