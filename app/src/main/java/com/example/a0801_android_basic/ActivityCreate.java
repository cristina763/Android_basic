package com.example.a0801_android_basic;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ActivityCreate extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;

    private EditText editTextName, editTextHeight, editTextWeight, editTextAge;
    private Spinner spinnerGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        editTextName = findViewById(R.id.editTextName);
        spinnerGender = findViewById(R.id.spinnerGender);
        editTextAge = findViewById(R.id.editTextAge);
        editTextHeight = findViewById(R.id.editTextHeight);
        editTextWeight = findViewById(R.id.editTextWeight);

        Button buttonSubmit = findViewById(R.id.buttonSubmit);

        // 性別
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                String gender = spinnerGender.getSelectedItem().toString();
                String age = editTextAge.getText().toString();
                String height = editTextHeight.getText().toString();
                String weight = editTextWeight.getText().toString();

                JSONObject postData = new JSONObject();
                try {
                    postData.put("name", name);
                    postData.put("gender", gender);
                    postData.put("age", age);
                    postData.put("height", height);
                    postData.put("weight", weight);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                new SendDataToServer().execute(postData.toString());

                Intent intent = new Intent(ActivityCreate.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private class SendDataToServer extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String jsonResponse = "";
            String jsonData = params[0];
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL("http://192.168.1.109/android_basic.php");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                OutputStream os = urlConnection.getOutputStream();
                os.write(jsonData.getBytes("UTF-8"));
                os.close();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    jsonResponse = "Data sent successfully";
                } else {
                    jsonResponse = "Failed to send data";
                }
            } catch (Exception e) {
                e.printStackTrace();
                jsonResponse = "Exception occurred";
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // 您可以在此處處理結果，例如顯示通知或進行其他操作
        }
    }
}
