package com.example.a0801_android_basic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ActivityUpdate extends AppCompatActivity {
    EditText editTextName, editTextGender, editTextAge, editTextHeight, editTextWeight;
    Button buttonUpdate;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        editTextName = findViewById(R.id.editTextName);
        editTextGender = findViewById(R.id.editTextGender);
        editTextAge = findViewById(R.id.editTextAge);
        editTextHeight = findViewById(R.id.editTextHeight);
        editTextWeight = findViewById(R.id.editTextWeight);
        buttonUpdate = findViewById(R.id.buttonSave);

        Intent intent = getIntent();
        String data = intent.getStringExtra("data");

        try {
            JSONObject jsonObject = new JSONObject(data);
            id = jsonObject.getString("id");
            editTextName.setText(jsonObject.getString("name"));
            editTextGender.setText(jsonObject.getString("gender"));
            editTextAge.setText(jsonObject.getString("age"));
            editTextHeight.setText(jsonObject.getString("height"));
            editTextWeight.setText(jsonObject.getString("weight"));
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
        try {
            String name = editTextName.getText().toString();
            String gender = editTextGender.getText().toString();
            String age = editTextAge.getText().toString();
            String height = editTextHeight.getText().toString();
            String weight = editTextWeight.getText().toString();

            URL url = new URL("http://192.168.0.10/android_update.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);

            String postData = "id=" + id + "&name=" + name + "&gender=" + gender + "&age=" + age + "&height=" + height + "&weight=" + weight;

            OutputStream os = connection.getOutputStream();
            os.write(postData.getBytes());
            os.flush();
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
                inputStream.close();

                String result = response.toString();
                if (result.equals("success")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(ActivityUpdate.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
