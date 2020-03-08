package me.nakukibo.colorbynumber;

import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.core.graphics.ColorUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

class BitmapConversion {

    
    static void makeCustomBitmap(Bitmap original, CustomBitmap customBitmap){

        Bitmap colorGrouped = original.copy(original.getConfig(), true);
        Bitmap colorBlank = original.copy(original.getConfig(), true);
        Set<Integer> uniqueColors = new HashSet<>();

        boolean[][] changedColor = new boolean[original.getHeight()][original.getWidth()];
        PixelCoordinates pixelCoordinates;

        while((pixelCoordinates = getNextFalseInArray(changedColor)) != null){
            setBitmaps(original, colorGrouped, colorBlank, uniqueColors, pixelCoordinates, changedColor);
        }

        customBitmap.setOriginal(original);
        customBitmap.setColored(colorGrouped);
        customBitmap.setBlank(colorBlank);
        customBitmap.setUniqueColors(uniqueColors);
    }

    private static void setBitmaps(Bitmap original, Bitmap colorGrouped, Bitmap colorBlank, Set<Integer> uniqueColors,
                                   PixelCoordinates nextFalse, boolean[][] changedColor) {

        int pixel = original.getPixel(nextFalse.x, nextFalse.y);

        uniqueColors.add(pixel);
        changedColor[nextFalse.y][nextFalse.x] = true;

        LinkedList<PixelCoordinates> surColors = new LinkedList<>();
        LinkedList<PixelCoordinates> changedColors = new LinkedList<>();

        surColors.add(new PixelCoordinates(nextFalse.x, nextFalse.y));
        changedColors.add(new PixelCoordinates(nextFalse.x, nextFalse.y));

        while(!surColors.isEmpty()){
            PixelCoordinates pixelCoordinates = surColors.pop();
            int neighbors = pushNeighbors(original, surColors, changedColors, pixelCoordinates, pixel, changedColor);
            colorBlank.setPixel(pixelCoordinates.x, pixelCoordinates.y, neighbors >= 9 ? Color.WHITE : Color.BLACK);
        }

        while(!changedColors.isEmpty()){
            PixelCoordinates pixelCoordinates = changedColors.pop();
            colorGrouped.setPixel(pixelCoordinates.x, pixelCoordinates.y, pixel);
        }
    }

    private static int pushNeighbors(Bitmap photo, LinkedList<PixelCoordinates> surColors, LinkedList<PixelCoordinates> changed,
                                      PixelCoordinates curPixel,  int color, boolean[][] changedColor) {

        int neighbors = 0;

        int minX = curPixel.x > 0 ? curPixel.x - 1 : curPixel.x;
        int maxX = curPixel.x < changedColor[0].length-1 ? curPixel.x + 1 : curPixel.x;

        int minY = curPixel.y > 0 ? curPixel.y - 1 : curPixel.y;
        int maxY = curPixel.y < changedColor.length-1 ? curPixel.y + 1 : curPixel.y;

        for(int i=minY; i<=maxY; i++){
            for(int j=minX; j<=maxX; j++){

                if(similarColors(color, photo.getPixel(j, i))){

                    if(!changedColor[i][j]) {
                        changedColor[i][j] = true;
                        surColors.add(new PixelCoordinates(j, i));
                        changed.add(new PixelCoordinates(j, i));
                    }

                    neighbors ++;
                }
            }
        }

        return neighbors;
    }

    // TODO: improve color comparison algorithm
    private static boolean similarColors(int color, int pixel) {

        final double MAX_DISTANCE = 180.0; //lab search

        double[] hsl1 = new double[3];
        double[] hsl2 = new double[3];

        ColorUtils.colorToLAB(color, hsl1);
        ColorUtils.colorToLAB(pixel, hsl2);

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