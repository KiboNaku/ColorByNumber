package me.nakukibo.colorbynumber.bitmap;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.io.File;
import java.util.LinkedList;

import me.nakukibo.colorbynumber.BaseActivity;
import me.nakukibo.colorbynumber.color.ColorSet;
import me.nakukibo.colorbynumber.color.MColor;

class BitmapConversion {

    private static final String TAG = "BitmapConversion";

    static ColorSet getUniqueColors(Bitmap bitmap) {
        return new ColorSet(bitmap);
    }

    static void createColorGrouped(CustomBitmap customBitmap, Bitmap bitmap, ColorSet uniqueColors, File dir, String fileName) {

        Log.d(TAG, "createColorGrouped: creating color grouped bitmap");

        bitmap = bitmap.copy(bitmap.getConfig(), true);
        boolean[][] changedColor = new boolean[bitmap.getHeight()][bitmap.getWidth()];
        PixelCoordinates pixelCoordinates;

        while ((pixelCoordinates = getNextFalseInArray(changedColor)) != null) {
            setColorGroupedPixel(bitmap, uniqueColors, pixelCoordinates, changedColor);
            customBitmap.updateProgress(bitmap);
        }

        BaseActivity.saveToInternalStorage(dir, fileName, bitmap);
    }

    static void createColorBlank(CustomBitmap customBitmap, Bitmap bitmap, File dir, String fileName) {

        Log.d(TAG, "createColorBlank: creating blank bitmap");

        final int HEIGHT = bitmap.getHeight();
        final int WIDTH = bitmap.getWidth();
        final int MAX_UPDATES = 100;

        int[] trailingColorsPrev = new int[WIDTH];
        int[] trailingColorsSame = new int[WIDTH];

        int updateCountDown = Math.min(WIDTH * HEIGHT / MAX_UPDATES, HEIGHT);
        int count = updateCountDown;

        bitmap = bitmap.copy(bitmap.getConfig(), true);

        for (int i = 0; i < bitmap.getHeight(); i++) {
            for (int j = 0; j < bitmap.getWidth(); j++) {

                setColorBlankPixel(bitmap, new PixelCoordinates(j, i), trailingColorsPrev, trailingColorsSame);
                count--;
                if (count <= 0) {
                    customBitmap.updateProgress(bitmap);
                    count = updateCountDown;
                }
            }

            int[] temp = trailingColorsPrev;
            trailingColorsPrev = trailingColorsSame;
            trailingColorsSame = temp;
        }

        BaseActivity.saveToInternalStorage(dir, fileName, bitmap);
    }

    private static void setColorGroupedPixel(Bitmap bitmap, ColorSet uniqueColors,
                                             PixelCoordinates coordinate, boolean[][] changedColor) {

        MColor pixel = new MColor(bitmap.getPixel(coordinate.x, coordinate.y));
        pixel = uniqueColors.getClosestColor(pixel);

        changedColor[coordinate.y][coordinate.x] = true;

        LinkedList<PixelCoordinates> surColors = new LinkedList<>();
        LinkedList<PixelCoordinates> changedColors = new LinkedList<>();

        surColors.add(new PixelCoordinates(coordinate.x, coordinate.y));
        changedColors.add(new PixelCoordinates(coordinate.x, coordinate.y));

        while (!surColors.isEmpty()) {
            PixelCoordinates pixelCoordinates = surColors.pop();
            pushNeighbors(bitmap, surColors, changedColors, pixelCoordinates, pixel, uniqueColors, changedColor);
        }

        while (!changedColors.isEmpty()) {
            PixelCoordinates pixelCoordinates = changedColors.pop();
            bitmap.setPixel(pixelCoordinates.x, pixelCoordinates.y, pixel.getColor());
        }
    }

    private static void setColorBlankPixel(Bitmap bitmap, PixelCoordinates coordinates, int[] trailingColorsPrev,
                                           int[] trailingColorsSame) {

        final int MAX_NEIGHBORS = 4;

        int pixel = bitmap.getPixel(coordinates.x, coordinates.y);
        int sameColorNeighbors = getNumSameColorNeighbors(bitmap, pixel, coordinates, trailingColorsPrev, trailingColorsSame);

        trailingColorsSame[coordinates.x] = pixel;
        bitmap.setPixel(coordinates.x, coordinates.y, sameColorNeighbors >= MAX_NEIGHBORS ? Color.WHITE : Color.BLACK);
    }

    private static int getNumSameColorNeighbors(Bitmap bitmap, int color, PixelCoordinates coordinates,
                                                int[] trailingColorsPrev, int[] trailingColorsSame) {

        int neighbors = 0;

        if (coordinates.y > 0) {
            if (trailingColorsPrev[coordinates.x] == color) neighbors++;
        }

        if (coordinates.x > 0) {
            if (trailingColorsSame[coordinates.x - 1] == color) neighbors++;
        }

        if (coordinates.y < bitmap.getHeight() - 1) {
            if (bitmap.getPixel(coordinates.x, coordinates.y + 1) == color) neighbors++;
        }

        if (coordinates.x < bitmap.getWidth() - 1) {
            if (bitmap.getPixel(coordinates.x + 1, coordinates.y) == color) neighbors++;
        }

        return neighbors;
    }

    private static void pushNeighbors(Bitmap photo, LinkedList<PixelCoordinates> surColors, LinkedList<PixelCoordinates> changed,
                                      PixelCoordinates curPixel, MColor color, ColorSet colorSet, boolean[][] changedColor) {

        int minX = curPixel.x > 0 ? curPixel.x - 1 : curPixel.x;
        int maxX = curPixel.x < changedColor[0].length - 1 ? curPixel.x + 1 : curPixel.x;

        int minY = curPixel.y > 0 ? curPixel.y - 1 : curPixel.y;
        int maxY = curPixel.y < changedColor.length - 1 ? curPixel.y + 1 : curPixel.y;

        for (int i = minY; i <= maxY; i++) {
            for (int j = minX; j <= maxX; j++) {

                MColor nColor = new MColor(photo.getPixel(j, i));

                if (nColor.distanceSqrFrom(color) <= colorSet.getMinDistSqr()) {

                    if (!changedColor[i][j]) {
                        changedColor[i][j] = true;
                        surColors.add(new PixelCoordinates(j, i));
                        changed.add(new PixelCoordinates(j, i));
                    }
                }
            }
        }
    }

    private static PixelCoordinates getNextFalseInArray(boolean[][] booleanArray) {

        for (int i = 0; i < booleanArray.length; i++) {
            for (int j = 0; j < booleanArray[0].length; j++) {
                if (!booleanArray[i][j]) return new PixelCoordinates(j, i);
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