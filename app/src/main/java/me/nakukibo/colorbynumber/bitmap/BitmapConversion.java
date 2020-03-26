package me.nakukibo.colorbynumber.bitmap;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.io.File;
import java.util.LinkedList;

import me.nakukibo.colorbynumber.BaseActivity;
import me.nakukibo.colorbynumber.color.ColorSet;
import me.nakukibo.colorbynumber.color.MColor;

class BitmapConversion {

    static ColorSet getUniqueColors(Bitmap bitmap) {
        return new ColorSet(bitmap);
    }

    static void createColorGrouped(Bitmap bitmap, ColorSet uniqueColors, File dir, String fileName) {

        bitmap = bitmap.copy(bitmap.getConfig(), true);
        boolean[][] changedColor = new boolean[bitmap.getHeight()][bitmap.getWidth()];
        PixelCoordinates pixelCoordinates;

        while ((pixelCoordinates = getNextFalseInArray(changedColor)) != null) {
            setColorGroupedPixel(bitmap, uniqueColors, pixelCoordinates, changedColor);
        }

        BaseActivity.saveToInternalStorage(dir, fileName, bitmap);
    }

    static void createColorBlank(Bitmap bitmap, ColorSet uniqueColors, File dir, String fileName) {

        bitmap = bitmap.copy(bitmap.getConfig(), true);

        for (int i = 0; i < bitmap.getHeight(); i++) {
            for (int j = 0; j < bitmap.getWidth(); j++) {
                setColorBlankPixel(bitmap, uniqueColors, new PixelCoordinates(j, i));
            }
        }

        BaseActivity.saveToInternalStorage(dir, fileName, bitmap);
    }

    private static void setColorGroupedPixel(Bitmap bitmap, ColorSet uniqueColors,
                                             PixelCoordinates nextFalse, boolean[][] changedColor) {

        MColor pixel = new MColor(bitmap.getPixel(nextFalse.x, nextFalse.y));
        pixel = uniqueColors.getClosestColor(pixel);

        changedColor[nextFalse.y][nextFalse.x] = true;

        LinkedList<PixelCoordinates> surColors = new LinkedList<>();
        LinkedList<PixelCoordinates> changedColors = new LinkedList<>();

        surColors.add(new PixelCoordinates(nextFalse.x, nextFalse.y));
        changedColors.add(new PixelCoordinates(nextFalse.x, nextFalse.y));

        while (!surColors.isEmpty()) {
            PixelCoordinates pixelCoordinates = surColors.pop();
            pushNeighbors(bitmap, surColors, changedColors, pixelCoordinates, pixel, uniqueColors, changedColor);
        }

        while (!changedColors.isEmpty()) {
            PixelCoordinates pixelCoordinates = changedColors.pop();
            bitmap.setPixel(pixelCoordinates.x, pixelCoordinates.y, pixel.getColor());
        }
    }

    private static void setColorBlankPixel(Bitmap bitmap, ColorSet uniqueColors,
                                           PixelCoordinates nextFalse) {

        MColor pixel = new MColor(bitmap.getPixel(nextFalse.x, nextFalse.y));
        int sameColorNeighbors = getNumSameColorNeighbors(bitmap, pixel, uniqueColors, nextFalse);

        if (sameColorNeighbors < 9) {
            bitmap.setPixel(nextFalse.x, nextFalse.y, Color.BLACK);
        }
    }

    private static int getNumSameColorNeighbors(Bitmap bitmap, MColor color, ColorSet colorSet, PixelCoordinates coordinates) {

        int neighbors = 0;

        int minX = coordinates.x > 0 ? coordinates.x - 1 : coordinates.x;
        int maxX = coordinates.x < bitmap.getWidth() - 1 ? coordinates.x + 1 : coordinates.x;

        int minY = coordinates.y > 0 ? coordinates.y - 1 : coordinates.y;
        int maxY = coordinates.y < bitmap.getHeight() - 1 ? coordinates.y + 1 : coordinates.y;

        for (int i = minY; i <= maxY; i++) {
            for (int j = minX; j <= maxX; j++) {

                MColor nColor = new MColor(bitmap.getPixel(j, i));

                if (nColor.distanceSqrFrom(color) <= colorSet.getMinDistSqr()) {
                    neighbors++;
                }
            }
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