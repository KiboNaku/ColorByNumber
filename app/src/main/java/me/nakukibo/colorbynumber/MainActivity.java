package me.nakukibo.colorbynumber;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.util.LinkedList;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_FETCH_PHOTO = 1;
    private static final int REQUEST_CODE_CAMERA = 2;

    private ImageView imageViewRetrievedPhoto;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewRetrievedPhoto = findViewById(R.id.image_view_retrieved);

        Button buttonLaunchCamera = findViewById(R.id.button_launch_camera);
        buttonLaunchCamera.setOnClickListener(v -> {

            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
            } else {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CODE_FETCH_PHOTO);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CODE_FETCH_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_FETCH_PHOTO && resultCode == Activity.RESULT_OK) {
            Bundle dataExtras = data.getExtras();
            if(dataExtras != null) {

                Bitmap photo = (Bitmap) dataExtras.get("data");

                if(photo == null) {
                    Log.i(TAG, "onActivityResult: failed to parse photo");
                    return;
                }

                setPhoto(photo);

            } else {
                Log.i(TAG, "onActivityResult: failed to fetch photo - dataExtras null");
            }
        }
    }

    private void setPhoto(Bitmap photo){

        boolean[][] changedColor = new boolean[photo.getHeight()][photo.getWidth()];

        while(getNextFalseInArray(changedColor) != null){
            setToAverage(photo, getNextFalseInArray(changedColor), changedColor);
        }

        imageViewRetrievedPhoto.setImageBitmap(photo);
    }

    private void setToAverage(Bitmap photo, PixelCoordinates nextFalse, boolean[][] changedColor) {
        int pixel = photo.getPixel(nextFalse.x, nextFalse.y);

        changedColor[nextFalse.y][nextFalse.x] = true;

        LinkedList<PixelCoordinates> surColors = new LinkedList<>();
        LinkedList<PixelCoordinates> changedColors = new LinkedList<>();

        surColors.add(new PixelCoordinates(nextFalse.x, nextFalse.y));
        changedColors.add(new PixelCoordinates(nextFalse.x, nextFalse.y));

        while(!surColors.isEmpty()){
//            for(int i=0; i<changedColor.length; i++){
//                StringBuilder stringBuilder = new StringBuilder();
//
//                for(int j=0; j<changedColor[0].length; j++){
//                    stringBuilder.append(changedColor[i][j] ? "1":"0");
//                }
//
//                Log.d(TAG, "setToAverage: " + stringBuilder.toString());
//            }

            PixelCoordinates pixelCoordinates = surColors.pop();
            pushNeighbors(photo, surColors, changedColors, pixelCoordinates, pixel, changedColor);
        }

        while(!changedColors.isEmpty()){
            PixelCoordinates pixelCoordinates = changedColors.pop();
            photo.setPixel(pixelCoordinates.x, pixelCoordinates.y, pixel);
        }
    }

    private void pushNeighbors(Bitmap photo, LinkedList<PixelCoordinates> surColors, LinkedList<PixelCoordinates> changed,
                               PixelCoordinates curPixel,  int color, boolean[][] changedColor) {
        int minX = curPixel.x > 0 ? curPixel.x - 1 : curPixel.x;
        int maxX = curPixel.x < changedColor[0].length-1 ? curPixel.x + 1 : curPixel.x;

        int minY = curPixel.y > 0 ? curPixel.y - 1 : curPixel.y;
        int maxY = curPixel.y < changedColor.length-1 ? curPixel.y + 1 : curPixel.y;
        Log.d(TAG, "pushNeighbors: " + String.format("minX: %d, maxX: %d, minY: %d, maxY: %d", minX, maxX, minY, maxY));

        for(int i=minY; i<=maxY; i++){
            for(int j=minX; j<=maxX; j++){

                Log.d(TAG, "pushNeighbors: similar colors? " + (!changedColor[i][j]));
                if(!changedColor[i][j] && similarColors(color, photo.getPixel(j, i))) {
                    Log.d(TAG, "pushNeighbors: x=" + j + "y=" + i);
                    changedColor[i][j] = true;
                    surColors.add(new PixelCoordinates(j, i));
                    changed.add(new PixelCoordinates(j, i));
                }

            }
        }
    }

    private boolean similarColors(int color, int pixel) {
        int r1 = Color.red(color);
        int g1 = Color.green(color);
        int b1 = Color.blue(color);

        int r2 = Color.red(pixel);
        int g2 = Color.green(pixel);
        int b2 = Color.blue(pixel);

        return (r1-r2)*(r1-r2) + (g1-g2)*(g1-g2) + (b1-b2)*(b1-b2) < 8000;
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