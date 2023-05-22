package com.example.weathersuit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    public void Register(View view) {
        EditText editText = findViewById(R.id.editTextTextPersonName);
        SeekBar seekBar = findViewById(R.id.seekBar);
        String name = editText.getText().toString();
        int pow = seekBar.getProgress()/10;
        if(name.length()>0) {
            SharedPreferences sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("USERNAME", name);
            editor.putInt("POW_OF_WARM_CLOTHES", pow);
            editor.commit();
            Registration(name, pow);
            editor.putBoolean("IS_FIRST_START", false);
            editor.commit();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void Registration(String name, int temp_pref){
        OkHttpClient client = new OkHttpClient();
        String url = "http://argirovga.pythonanywhere.com/api/create_user/name="+name+"&temp_pref="+String.valueOf(temp_pref);
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Registration(name, temp_pref);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String myResponse = response.body().string();
                    MainActivity2.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            try {
                                final JSONObject obj = new JSONObject(myResponse);
                                String id = obj.getString("success: uder_id");
                                editor.putString("USERID", id);
                                editor.commit();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }
}