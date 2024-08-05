package com.example.a0801_android_basic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
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
        listView = findViewById(R.id.listView);

        // 初始化適配器
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        Thread thread = new Thread(mutiThread);
        thread.start(); // 開始執行

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivityCreate.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            showOptionsDialog(position);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 當 MainActivity 恢復時自動刷新資料
        Thread thread = new Thread(mutiThread);
        thread.start();
    }

    private Runnable mutiThread = new Runnable(){
        public void run() {
            try {
                URL url = new URL("http://192.168.43.183/android_basic.php");
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

    private void updateListView(String result) {
        dataList.clear(); // 清空現有資料
        dataObjects.clear(); // 清空完整數據列表

        try {
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
    private void showOptionsDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Update or Delete")
                .setItems(new String[]{"Update", "Delete"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            // 更新
                            JSONObject selectedItem = dataObjects.get(position);
                            Intent intent = new Intent(MainActivity.this, ActivityUpdate.class);
                            intent.putExtra("data", selectedItem.toString());
                            startActivity(intent);
                            break;
                        case 1:
                            // 刪除
                            deleteItem(position);
                            break;
                    }
                });
        builder.create().show();
    }

    private void deleteItem(int position) {
        JSONObject selectedItem = dataObjects.get(position);
        String id;
        try {
            id = selectedItem.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
            return; // 如果获取 id 失败，直接返回
        }

        // 發送刪除請求
        Thread thread = new Thread(() -> {
            try {
                URL url = new URL("http://192.168.43.183/android_delete.php?id=" + id);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> {
                        dataList.remove(position);
                        dataObjects.remove(position);
                        adapter.notifyDataSetChanged();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}

