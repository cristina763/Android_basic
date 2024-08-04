package com.example.a0801_android_basic;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import org.json.JSONObject;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ActivityCreate extends AppCompatActivity {
    ListView listView;
    Button button;
    EditText editTextName, editTextAge, editTextHeight, editTextWeight;
    private Spinner spinnerGender;
    ArrayList<String> dataList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // 找到視圖的元件並連接
        button = findViewById(R.id.buttonSubmit);
        listView = findViewById(R.id.listView);
        editTextName = findViewById(R.id.editTextName);
        spinnerGender = findViewById(R.id.spinnerGender);
        editTextAge = findViewById(R.id.editTextAge);
        editTextHeight = findViewById(R.id.editTextHeight);
        editTextWeight = findViewById(R.id.editTextWeight);

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        // 初始化適配器
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        // 宣告按鈕的監聽器監聽按鈕是否被按下
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 按下之後會執行的程式碼
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        submitData();
                    }
                }).start();
            }
        });
    }

    // 提交資料到伺服器
    private void submitData() {
        try {
            String name = editTextName.getText().toString();
            String gender = spinnerGender.getSelectedItem().toString();
            String age = editTextAge.getText().toString();
            String height = editTextHeight.getText().toString();
            String weight = editTextWeight.getText().toString();

            URL url = new URL("http://192.168.0.10/android_create.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);

            String postData = "name=" + name + "&gender=" + gender + "&age=" + age + "&height=" + height + "&weight=" + weight;

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
                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.has("error")) {
                    final String errorMessage = jsonObject.getString("error");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ActivityCreate.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    final String nameResult = jsonObject.getString("name");
                    final String bmiResult = jsonObject.getString("bmi");
                    final String bmrResult = jsonObject.getString("bmr");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 啟動新的 Activity 並傳遞數據
                            Intent intent = new Intent(ActivityCreate.this, ActivityResult.class);
                            intent.putExtra("name", nameResult);
                            intent.putExtra("bmi", bmiResult);
                            intent.putExtra("bmr", bmrResult);
                            startActivity(intent);
                        }
                    });
                }
            } else {
                Log.e("HTTP Error", "Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Submit Data Error", e.toString());
        }
    }
}