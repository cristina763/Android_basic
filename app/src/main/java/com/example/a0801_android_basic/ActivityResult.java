package com.example.a0801_android_basic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityResult extends AppCompatActivity {

    TextView textViewName, textViewBmi, textViewBmr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        textViewName = findViewById(R.id.textViewName);
        textViewBmi = findViewById(R.id.textViewBmi);
        textViewBmr = findViewById(R.id.textViewBmr);

        // 獲取 Intent 並讀取數據
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String bmi = intent.getStringExtra("bmi");
        String bmr = intent.getStringExtra("bmr");

        // 顯示數據
        textViewName.setText("Name: " + name);
        textViewBmi.setText("BMI: " + bmi);
        textViewBmr.setText("BMR: " + bmr);
    }
}
