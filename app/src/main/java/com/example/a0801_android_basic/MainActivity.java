package com.example.a0801_android_basic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import org.json.JSONObject;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    Button button;
    String result; // 儲存資料用的字串
    ArrayList<String> dataList = new ArrayList<>(); // 儲存資料的列表
    ArrayAdapter<String> adapter; // 列表適配器
    ArrayList<JSONObject> dataObjects = new ArrayList<>(); // 儲存完整數據的列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.buttonSubmit);

        // 找到視圖的元件並連接
        //button = findViewById(R.id.buttonSubmit);
        listView = findViewById(R.id.listView);

        // 初始化適配器
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        Thread thread = new Thread(mutiThread);
        thread.start(); // 開始執行

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //writeDataToExcel();
                Intent intent = new Intent(MainActivity.this, ActivityCreate.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            JSONObject selectedItem = dataObjects.get(position);
            Intent intent = new Intent(MainActivity.this, ActivityUpdate.class);
            intent.putExtra("data", selectedItem.toString());
            startActivity(intent);
        });

    }

    /* ======================================== */

    // 建立一個執行緒執行的事件取得網路資料
    private Runnable mutiThread = new Runnable(){
        public void run()
        {
            try {
                URL url = new URL("http://192.168.0.10/android_basic.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK){
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                    StringBuilder box = new StringBuilder();
                    String line;
                    while((line = bufReader.readLine()) != null) {
                        box.append(line).append("\n");
                    }
                    inputStream.close();
                    result = box.toString();
                }
            } catch(Exception e) {
                result = e.toString();
            }

            runOnUiThread(new Runnable() {
                public void run() {
                    updateListView(result); // 更新 ListView
                }
            });
        }
    };

    // 更新 ListView 的方法
    private void updateListView(String result) {
        dataList.clear(); // 清空現有資料
        dataObjects.clear(); // 清空完整數據列表

        try {
            // 假設伺服器返回的是 JSON 數組
            JSONArray jsonArray = new JSONArray(result);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                dataObjects.add(jsonObject); // 保存完整的 JSON 數據

                String name = jsonObject.getString("name");
                String bmr = jsonObject.getString("bmr");

                dataList.add("Name: " + name + ", BMR: " + bmr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged(); // 通知適配器資料已變更
    }
}
