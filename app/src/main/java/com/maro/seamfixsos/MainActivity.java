package com.maro.seamfixsos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import com.maro.seamfixsos.screens.CameraActivity;
import com.maro.seamfixsos.screens.NumbersActivity;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    Set<String> nSet;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.gotoNumbers);
        Button startCamera = findViewById(R.id.startCamera);
        sharedPreferences = getApplicationContext().getSharedPreferences("Numbers", MODE_PRIVATE);

        nSet = sharedPreferences.getStringSet("Numbers", new HashSet<>());

//        Intent goToNextPage;
//        if(!nSet.isEmpty()){
//            goToNextPage = new Intent(MainActivity.this, CameraActivity.class);
//        } else{
//            goToNextPage = new Intent(MainActivity.this, NumbersActivity.class);
//
//        }
//        startActivity(goToNextPage);



        startCamera.setOnClickListener(v->{
            Intent goToNextPage = new Intent(MainActivity.this, CameraActivity.class);
            startActivity(goToNextPage);
        });

        button.setOnClickListener(v->{
            Intent goToNextPage = new Intent(MainActivity.this, NumbersActivity.class);
            startActivity(goToNextPage);

        });
    }
}