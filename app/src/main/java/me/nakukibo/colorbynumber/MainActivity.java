package me.nakukibo.colorbynumber;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

//TODO: reduce the minimum sdk required

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void launchImporter(View view){
        Intent intent = new Intent(this, ImporterActivity.class);
        startActivity(intent);
    }

}