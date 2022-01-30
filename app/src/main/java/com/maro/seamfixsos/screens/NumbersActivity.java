package com.maro.seamfixsos.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.maro.seamfixsos.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NumbersActivity extends AppCompatActivity {

    ProgressBar progressBar;
    EditText phone1, phone2, phone3;
    Button save;
    Set<String> set = new HashSet<>();
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numbers);

        progressBar = findViewById(R.id.progressBar);
        phone1 = findViewById(R.id.entered_phone_one);
        phone2 = findViewById(R.id.entered_phone_two);
        phone3 = findViewById(R.id.entered_phone_three);
        save = findViewById(R.id.save_numbers);


        progressBar.setVisibility(View.INVISIBLE);
        sharedPreferences = getApplicationContext().getSharedPreferences("Numbers", MODE_PRIVATE);

        save.setOnClickListener(v->{
            if(!phone1.getText().toString().isEmpty() || !phone2.getText().toString().isEmpty() || !phone3.getText().toString().isEmpty()){
                set.clear();
                set.add(phone1.getText().toString());
                set.add(phone2.getText().toString());
                set.add(phone3.getText().toString());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putStringSet("Numbers", set);
                editor.apply();
                Toast.makeText(this, "Numbers updated", Toast.LENGTH_SHORT).show();
            }
        });

    }
}