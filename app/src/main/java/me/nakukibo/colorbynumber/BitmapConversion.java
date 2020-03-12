package me.nakukibo.colorbynumber;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.LinkedList;

class BitmapConversion {


    private static final String TAG = "BitmapConversion";

    static void makeCustomBitmap(Bitmap original, CustomBitmap customBitmap){

        ColorSet uniqueColors = new ColorSet();
        addAllColors(uniqueColors, original);

        Log.d(TAG, "makeCustomBitmap: number of colors before = " + uniqueColors.size());
        uniqueColors.cleanse();

        Log.d(TAG, "makeCustomBitmap: number of colors after = " + uniqueColors.size());

        Bitmap colorGrouped = original.copy(original.getConfig(), true);
        Bitmap colorBlank = original.copy(original.getConfig(), true);

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

    private static void addAllColors(ColorSet uniqueColors, Bitmap original) {

        for(int i=0; i<original.getHeight(); i++){
            for(int j=0; j<original.getWidth(); j++){

                uniqueColors.add(new MColor(original.getPixel(j, i)));
            }
        }
    }

    private static void setBitmaps(Bitmap original, Bitmap colorGrouped, Bitmap colorBlank, ColorSet uniqueColors,
                                   PixelCoordinates nextFalse, boolean[][] changedColor) {

        MColor pixel = new MColor(original.getPixel(nextFalse.x, nextFalse.y));
        pixel = uniqueColors.getClosestColor(pixel);

        changedColor[nextFalse.y][nextFalse.x] = true;

        LinkedList<PixelCoordinates> surColors = new LinkedList<>();
        LinkedList<PixelCoordinates> changedColors = new LinkedList<>();

        surColors.add(new PixelCoordinates(nextFalse.x, nextFalse.y));
        changedColors.add(new PixelCoordinates(nextFalse.x, nextFalse.y));

        while(!surColors.isEmpty()){
            PixelCoordinates pixelCoordinates = surColors.pop();
            int neighbors = pushNeighbors(original, surColors, changedColors, pixelCoordinates, pixel, uniqueColors, changedColor);
            colorBlank.setPixel(pixelCoordinates.x, pixelCoordinates.y, neighbors >= 9 ? Color.WHITE : Color.BLACK);
        }

        while(!changedColors.isEmpty()){
            PixelCoordinates pixelCoordinates = changedColors.pop();
            colorGrouped.setPixel(pixelCoordinates.x, pixelCoordinates.y, pixel.getColor());
        }
    }

    private static int pushNeighbors(Bitmap photo, LinkedList<PixelCoordinates> surColors, LinkedList<PixelCoordinates> changed,
                                      PixelCoordinates curPixel,  MColor color, ColorSet colorSet, boolean[][] changedColor) {

        int neighbors = 0;

        int minX = curPixel.x > 0 ? curPixel.x - 1 : curPixel.x;
        int maxX = curPixel.x < changedColor[0].length-1 ? curPixel.x + 1 : curPixel.x;

        int minY = curPixel.y > 0 ? curPixel.y - 1 : curPixel.y;
        int maxY = curPixel.y < changedColor.length-1 ? curPixel.y + 1 : curPixel.y;

        for(int i=minY; i<=maxY; i++){
            for(int j=minX; j<=maxX; j++){

                MColor nColor = new MColor(photo.getPixel(j, i));

                if(nColor.distanceSqrFrom(color) <= colorSet.getMinDistSqr()){

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