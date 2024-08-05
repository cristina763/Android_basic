package com.example.a0801_android_basic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ActivityUpdate extends AppCompatActivity {
    EditText editTextName, editTextAge, editTextHeight, editTextWeight;
    private Spinner spinnerGender;
    Button buttonUpdate;
    String id;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        editTextName = findViewById(R.id.editTextName);
        spinnerGender = findViewById(R.id.spinnerGender);
        editTextAge = findViewById(R.id.editTextAge);
        editTextHeight = findViewById(R.id.editTextHeight);
        editTextWeight = findViewById(R.id.editTextWeight);
        buttonUpdate = findViewById(R.id.buttonSave);

        String[] genders = {"Male", "Female"};

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerGender.setAdapter(adapter);

        Intent intent = getIntent();
        String data = intent.getStringExtra("data");

        try {
            JSONObject jsonObject = new JSONObject(data);
            id = jsonObject.getString("id");
            editTextName.setText(jsonObject.getString("name"));
            editTextAge.setText(jsonObject.getString("age"));
            editTextHeight.setText(jsonObject.getString("height"));
            editTextWeight.setText(jsonObject.getString("weight"));

            String gender = jsonObject.getString("gender");

            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerGender.getAdapter();
            int position = adapter.getPosition(gender);

            if (position >= 0) {spinnerGender.setSelection(position);
            } else {spinnerGender.setSelection(0); }
        } catch (Exception e) {
            e.printStackTrace();
        }

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });
    }

    private void updateData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id); // 确保包含 ID 参数
            jsonObject.put("name", editTextName.getText().toString());
            jsonObject.put("gender", spinnerGender.getSelectedItem().toString());
            jsonObject.put("age", editTextAge.getText().toString());
            jsonObject.put("height", editTextHeight.getText().toString());
            jsonObject.put("weight", editTextWeight.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Thread thread = new Thread(() -> {
            try {
                URL url = new URL("http://192.168.43.183/android_update.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

                // 发送请求
                OutputStream os = connection.getOutputStream();
                String postData = "id=" + id +
                        "&name=" + URLEncoder.encode(editTextName.getText().toString(), "UTF-8") +
                        "&gender=" + URLEncoder.encode(spinnerGender.getSelectedItem().toString(), "UTF-8") +
                        "&age=" + URLEncoder.encode(editTextAge.getText().toString(), "UTF-8") +
                        "&height=" + URLEncoder.encode(editTextHeight.getText().toString(), "UTF-8") +
                        "&weight=" + URLEncoder.encode(editTextWeight.getText().toString(), "UTF-8");
                os.write(postData.getBytes("UTF-8"));
                os.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    runOnUiThread(() -> {
                        //Toast.makeText(ActivityUpdate.this, response.toString(), Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        Intent intent = new Intent(ActivityUpdate.this, MainActivity.class);
        startActivity(intent);
    }
}
