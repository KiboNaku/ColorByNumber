package me.nakukibo.colorbynumber;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;

import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class CustomBitmap {

    private static final String TAG = "CustomBitmap";

    private BitmapPixel[][] pixels;
    private Set<Integer> uniqueColors;

    static CustomBitmap makeBitmap(@NonNull final Bitmap bitmap, final OnSuccessListener onSuccessListener){

        final CustomBitmap customBitmap = new CustomBitmap();

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        if(height < 1 || width < 1){
            throw new InvalidParameterException("The height and width of the bitmap must be at least 1.");
        }

        customBitmap.pixels = new BitmapPixel[height][width];
        customBitmap.uniqueColors = new HashSet<>();

        Thread threadMake = new Thread(new Runnable() {
            @Override
            public void run() {

                convertToCustom(customBitmap, bitmap);
                if(onSuccessListener != null) {
                    Log.d(TAG, "run: successful finish makeBitmap");
                    onSuccessListener.onSuccess();
                }
            }
        });

        threadMake.start();

        return customBitmap;
    }

    public Bitmap getGroupedBitmap(){

        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);

        for(int i=0; i<getHeight(); i++){
            for(int j=0; j<getWidth(); j++){
                bitmap.setPixel(j, i, getPixel(j, i).getColorGroup());
            }
        }

        return bitmap;
    }


    public Bitmap getUnColoredBitmap(){

        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);

        for(int i=0; i<getHeight(); i++){
            for(int j=0; j<getWidth(); j++){
                bitmap.setPixel(j, i, getPixel(j, i).getColorUncolored());
            }
        }

        return bitmap;
    }

    private static void convertToCustom(CustomBitmap customBitmap, Bitmap photo){

        boolean[][] modified = new boolean[customBitmap.getHeight()][customBitmap.getWidth()];

        BitmapPixel nextPixel;
        populatePixels(customBitmap, photo);

        while((nextPixel = getNextToConvert(customBitmap)) != null){
            formulatePixelColors(customBitmap, nextPixel, modified);
        }

    }

    private static void populatePixels(CustomBitmap customBitmap, Bitmap photo){

        for(int i=0; i<customBitmap.getHeight(); i++){
            for(int j=0; j<customBitmap.getWidth(); j++){

                BitmapPixel bitmapPixel = new BitmapPixel(j, i, photo.getPixel(j, i),  null, null);
                customBitmap.pixels[i][j] = bitmapPixel;
            }
        }

        while(true){}
    }

    private static void formulatePixelColors(CustomBitmap customBitmap, BitmapPixel nextPixel, boolean[][] modified) {

        final int MAX_NEIGHBORS = 9;

        LinkedList<BitmapPixel> surColors = new LinkedList<>();

        int color = nextPixel.colorOriginal;
        modified[nextPixel.yPos][nextPixel.xPos] = true;
        surColors.add(nextPixel);

        while(!surColors.isEmpty()){

            BitmapPixel curPixel = surColors.pop();
            curPixel.colorGroup = color;

            int neighbors = pushSimilarNeighbors(customBitmap, curPixel, color, surColors, modified);
            if(neighbors >= 0) {
                curPixel.colorUncolored = neighbors >= MAX_NEIGHBORS? Color.WHITE : Color.BLACK;
            }
        }


    }

    private static int pushSimilarNeighbors(CustomBitmap customBitmap, BitmapPixel curPixel, int color,
                                            LinkedList<BitmapPixel> surColors, boolean[][] modified) {

        // TODO: maybe modify what counts as a neighbor (eg maybe not corner pixels)

        int neighbors = 0;

        int minX = curPixel.xPos > 0 ? curPixel.xPos - 1 : curPixel.xPos;
        int maxX = curPixel.xPos < customBitmap.getWidth()-1 ? curPixel.xPos + 1 : curPixel.xPos;

        int minY = curPixel.yPos > 0 ? curPixel.yPos - 1 : curPixel.yPos;
        int maxY = curPixel.yPos < customBitmap.getHeight()-1 ? curPixel.yPos + 1 : curPixel.yPos;

        for(int i=minY; i<=maxY; i++){
            for(int j=minX; j<=maxX; j++){

                BitmapPixel neighborPixel = customBitmap.getPixel(j, i);
                if(similarColors(color, neighborPixel.colorOriginal)){

                    neighbors ++;

                    if(!modified[neighborPixel.yPos][neighborPixel.xPos]) {

                        modified[neighborPixel.yPos][neighborPixel.xPos] = true;
                        surColors.add(neighborPixel);
                    }
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

        double dL = hsl1[0] - hsl2[0];
        double dA = hsl1[1] - hsl2[1];
        double dB = hsl1[2] - hsl2[2];

        return dL*dL + dA*dA + dB*dB <= MAX_DISTANCE;
    }

    private static BitmapPixel getNextToConvert(CustomBitmap customBitmap){

        for(int i=0; i<customBitmap.getHeight(); i++){
            for(int j=0; j<customBitmap.getWidth(); j++){

                BitmapPixel bitmapPixel = customBitmap.getPixel(j, i);

                if(bitmapPixel.colorGroup == null) {

                    return bitmapPixel;
                }
            }
        }

        return null;
    }

    public int getWidth() {
        return pixels[0].length;
    }

    public int getHeight() {
        return pixels.length;
    }

    public BitmapPixel getPixel(int x, int y) {
        return pixels[y][x];
    }

    public Set<Integer> getUniqueColors() {
        return uniqueColors;
    }

    public interface OnSuccessListener{
        void onSuccess();
    }

    public static class BitmapPixel{

        private int xPos;
        private int yPos;
        private int colorOriginal;
        private Integer colorGroup;
        private Integer colorUncolored;

        public BitmapPixel(int xPos, int yPos, int colorOriginal, Integer colorGroup, Integer colorUncolored) {

            this.xPos = xPos;
            this.yPos = yPos;
            this.colorOriginal = colorOriginal;
            this.colorGroup = colorGroup;
            this.colorUncolored = colorUncolored;
        }

        public Integer getColorOriginal() {
            return colorOriginal;
        }

        public Integer getColorGroup() {
            return colorGroup;
        }

        public Integer getColorUncolored() {
            return colorUncolored;
        }
    }
}
