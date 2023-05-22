package com.example.weathersuit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.format.DateUtils;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            if(locationResult == null){
                return;
            }
            GetLocation();
            stopLocationUpdates();
        }
    };
    double latitude = 0;
    double longitude = 0;
    int ImgIds[] = new int[] {R.id.clo1, R.id.clo2, R.id.clo3, R.id.clo4, R.id.clo5, R.id.clo6, R.id.clo7, R.id.clo8, R.id.clo9, R.id.clo10};
    int TxtIds[] = new int[] {R.id.clo_name1, R.id.clo_name2, R.id.clo_name3, R.id.clo_name4, R.id.clo_name5, R.id.clo_name6, R.id.clo_name7, R.id.clo_name8, R.id.clo_name9, R.id.clo_name10};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setSmallestDisplacement(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        startApp();

    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdates(){
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
    public void  stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
    public void startApp(){
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Long mills = System.currentTimeMillis();
        editor.putLong("LAST_OPEN", mills);
        editor.commit();
        ImageView img;
        LinearLayout linearLayout = findViewById(R.id.warning_block);
        linearLayout.setBackground(null);
        ImageView imageView = findViewById(R.id.warning_img);
        imageView.setImageDrawable(null);
        TextView textView = findViewById(R.id.warning);
        textView.setText(null);
        img = findViewById(R.id.sky);
        img.setImageDrawable(null);

        img = findViewById(R.id.pants);
        img.setImageDrawable(null);
        img = findViewById(R.id.jacket);
        img.setImageDrawable(null);
        img = findViewById(R.id.sunglasses);
        img.setImageDrawable(null);
        img = findViewById(R.id.shoes);
        img.setImageDrawable(null);
        img = findViewById(R.id.hat);
        img.setImageDrawable(null);
        img = findViewById(R.id.scarf);
        img.setImageDrawable(null);
        img = findViewById(R.id.gloves);
        img.setImageDrawable(null);
        img = findViewById(R.id.t_shirt);
        img.setImageDrawable(null);
        img = findViewById(R.id.umbrella);
        img.setImageDrawable(null);
        FillGiffs();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }else{
            checkPermissions();
        }
    }
    public void FillGiffs(){
        for(int i: ImgIds){
            ImageView img = findViewById(i);
            Glide.with(this).load(R.drawable.loading123).into(img);
        }
        ImageView img = findViewById(R.id.imageView2);
        Glide.with(this).load(R.drawable.loading123).into(img);
        for(int i: TxtIds){
            TextView txt = findViewById(i);
            txt.setText("Loading...");
        }
        TextView tmp = findViewById(R.id.temp);
        tmp.setText("Loading...");
        TextView hum = findViewById(R.id.humidity);
        hum.setText("Loading...");
        TextView speed = findViewById(R.id.wind_speed);
        speed.setText("Loading...");

    }
    public void EmptyGiffs(){
        for(int i: ImgIds){
            ImageView img = findViewById(i);
            img.setImageDrawable(null);
        }
        for(int i: TxtIds){
            TextView txt = findViewById(i);
            txt.setText(null);
        }
        TextView tmp = findViewById(R.id.temp);
        tmp.setText(null);
        TextView hum = findViewById(R.id.humidity);
        hum.setText(null);
        TextView speed = findViewById(R.id.wind_speed);
        speed.setText(null);
    }
    public void GetLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    SharedPreferences getSharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
                    Double savedLong = (double) getSharedPreferences.getFloat("LONGITUDE", -100);
                    Double savedLat = (double) getSharedPreferences.getFloat("LATITUDE", -100);
                    Location location = task.getResult();
                    if(location != null){
                        latitude = new Double(location.getLatitude());
                        longitude = new Double(location.getLongitude());
                        SharedPreferences sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putFloat("LATITUDE", (float)latitude);
                        editor.putFloat("LONGITUDE", (float)longitude);
                        System.out.println(latitude);
                        System.out.println(longitude);
                        editor.commit();
                        getWeatherData(new Double[]{latitude, longitude});
                    }else if(savedLong != -100 && savedLat != -100){
                        getWeatherData(new Double[]{savedLong, savedLat});
                    }
                    else {
                        System.out.println("Something went wrong during get location");
                        GetLocation();
                    }
                }
            });
        }else{
            checkPermissions();
        }
    }
    public void myDialog(View view){

        final String[] items = new String[]{"I am good!", "It was too cold for me", "It was too warm for me"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("How are you today?").
                setSingleChoiceItems(items, -1,
                        (dialog, item1) -> {
                            SharedPreferences sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("FEED", item1);
                            editor.apply();
                        });
        builder.setPositiveButton("send", (dialog, which) -> {
            SharedPreferences sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
            int it = sharedPreferences.getInt("FEED", -100);
            sendFeed(it);
            dialog.dismiss();
        });
        builder.setNegativeButton("cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void getWeatherData(Double[] coords){
        OkHttpClient client = new OkHttpClient();
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        String user_id = sharedPreferences.getString("USERID", "NO_ID");
        String url = "http://argirovga.pythonanywhere.com/api/all_in_one/lat="+ coords[0].toString()+"&lon="+ coords[1].toString()+"&user_id="+user_id;
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                getWeatherData(coords);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String myResponse = response.body().string();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String Response = myResponse;
                            try {
                                ParseJson(Response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }
    @Override
    protected void onRestart(){
        super.onRestart();
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        Long millsNow = System.currentTimeMillis();
        Long mills = sharedPreferences.getLong("LAST_OPEN", 0);
        if((millsNow-mills)/DateUtils.MINUTE_IN_MILLIS >= 30){
            startApp();
        }
    }
    @SuppressLint("SetTextI18n")
    public void ParseJson(String Response) throws JSONException{
        EmptyGiffs();
        System.out.println(Response);
        final JSONObject obj = new JSONObject(Response);
        final JSONObject obj1 = obj.getJSONObject("specific_all_data_in_city");
        ImageView imageView = findViewById(R.id.imageView2);
        ImageView sky = findViewById(R.id.sky);
        String Condition = obj1.getString("main");
        String warning = obj1.getString("warning");
        setWarning(warning);
        if(Condition.equals("Rain") || Condition.equals("Drizzle")){
            imageView.setImageResource(R.drawable.rain);
            sky.setImageResource(R.drawable.ic_rain_sky);
        }
        if(Condition.equals("Mist") || Condition.equals("Smoke") || Condition.equals("Haze") || Condition.equals("Fog")){
            imageView.setImageResource(R.drawable.fog);
            sky.setImageResource(R.drawable.ic_fog_sky);
        }
        if(Condition.equals("Ash") || Condition.equals("Dust") || Condition.equals("Sand")){
            imageView.setImageResource(R.drawable.dust);
            sky.setImageResource(R.drawable.ic_sand_sky);
        }
        if(Condition.equals("Thunderstorm")){
            imageView.setImageResource(R.drawable.storm);
            sky.setImageResource(R.drawable.ic_rain_sky);
        }
        if(Condition.equals("Snow")){
            imageView.setImageResource(R.drawable.snow);
            sky.setImageResource(R.drawable.ic_snow_sky);
        }
        if(Condition.equals("Tornado")){
            imageView.setImageResource(R.drawable.tornado);
            sky.setImageResource(R.drawable.ic_wind_sky);
        }
        if(Condition.equals("Squalls")){
            imageView.setImageResource(R.drawable.wind);
            sky.setImageResource(R.drawable.ic_wind_sky);
        }
        if(Condition.equals("Clouds")) {
            imageView.setImageResource(R.drawable.clouds);
            sky.setImageResource(R.drawable.blue_sky);
        }
        if(Condition.equals("Clear")){
            imageView.setImageResource(R.drawable.clear);
            sky.setImageResource(R.drawable.ic_blue_sky);
        }
        TextView humidity = findViewById(R.id.humidity);
        humidity.setText("      " +obj1.getString("humidity")+"%");
        TextView speed = findViewById(R.id.wind_speed);
        speed.setText("   " +String.valueOf((int)Math.floor(Float.valueOf(obj1.getString("wind_speed"))))+"m/s");
        TextView temp = findViewById(R.id.temp);
        temp.setText("      " +obj1.getString("temp")+"°");
        LinearLayout layout = findViewById(R.id.list_clothes);
        JSONArray clothes = obj1.getJSONArray("mas_clothes");
        final int n = clothes.length();
        TextView text;
        ImageView img;
        ImageView Cloth;
        for(int i = 0; i < n; i++){
            text = findViewById(TxtIds[i]);
            img = findViewById(ImgIds[i]);
            text.setText(clothes.getString(i));
            switch(clothes.getString(i)){
                case "Sneakers":
                    Cloth = findViewById(R.id.shoes);
                    Cloth.setImageResource(R.drawable.ic_shoes_01);
                    img.setImageResource(R.drawable.trainers);
                    break;
                case "Coat":
                    Cloth = findViewById(R.id.jacket);
                    text.setText("Jacket");
                    Cloth.setImageResource(R.drawable.ic_jacket_for_man_01);
                    img.setImageResource(R.drawable.jacket);
                    break;
                case "Trousers":
                    Cloth = findViewById(R.id.pants);
                    text.setText("Pants");
                    Cloth.setImageResource(R.drawable.ic_pants_for_char_01_01);
                    img.setImageResource(R.drawable.pants);
                    break;
                case "T-shirt":
                    Cloth = findViewById(R.id.t_shirt);
                    Cloth.setImageResource(R.drawable.ic_t_shirt_for_char_01);
                    img.setImageResource(R.drawable.t_shirt);
                    break;
                case "Shorts":
                    Cloth = findViewById(R.id.pants);
                    Cloth.setImageResource(R.drawable.ic_shirts_for_man_01);
                    img.setImageResource(R.drawable.shorts);
                    break;
                case "Panama":
                    Cloth = findViewById(R.id.hat);
                    Cloth.setImageResource(R.drawable.ic_panamka_01);
                    img.setImageResource(R.drawable.panama);
                    break;
                case "Sunglasses":
                    Cloth = findViewById(R.id.sunglasses);
                    Cloth.setImageResource(R.drawable.ic_sunglasses_fro_man_01);
                    img.setImageResource(R.drawable.sun_glasses);
                    break;
                case "Thermal underwear":
                    text.setText("Unnderpants");
                    img.setImageResource(R.drawable.tights);
                    break;
                case "Scarf":
                    Cloth = findViewById(R.id.scarf);
                    Cloth.setImageResource(R.drawable.ic_scarf_for_man_01);
                    img.setImageResource(R.drawable.scarf);
                    break;
                case "Winter boots":
                    Cloth = findViewById(R.id.shoes);
                    Cloth.setImageResource(R.drawable.ic_warm_shoes_01);
                    img.setImageResource(R.drawable.winter_boots);
                    break;
                case "Gloves":
                    Cloth = findViewById(R.id.gloves);
                    Cloth.setImageResource(R.drawable.ic_gloves_for_man_01);
                    text.setText("Mittens");
                    img.setImageResource(R.drawable.mittens);
                    break;
                case "Waterproof coat":
                    Cloth = findViewById(R.id.jacket);
                    text.setText("Rain jacket");
                    Cloth.setImageResource(R.drawable.ic_rain_jacket_for_man_01);
                    img.setImageResource(R.drawable.rain_jacket);
                    break;
                case "Hoodie":
                    Cloth = findViewById(R.id.t_shirt);
                    text.setText("Sweathirt");
                    Cloth.setImageResource(R.drawable.ic_sweatshirt);
                    img.setImageResource(R.drawable.sweatshirt);
                    break;
                case "Demi boots":
                    Cloth = findViewById(R.id.shoes);
                    Cloth.setImageResource(R.drawable.ic_autumn_shoes_01);
                    img.setImageResource(R.drawable.timberland);
                    break;
                case "Warm hat":
                    Cloth = findViewById(R.id.hat);
                    Cloth.setImageResource(R.drawable.ic_beanie_for_man_01);
                    img.setImageResource(R.drawable.beani);
                    break;
                case "Warm trousers":
                    Cloth = findViewById(R.id.pants);
                    text.setText("Warm pants");
                    Cloth.setImageResource(R.drawable.ic_warm_pants_for_man);
                    img.setImageResource(R.drawable.warm_pants);
                    break;
                case "Sandals":
                    Cloth = findViewById(R.id.shoes);
                    Cloth.setImageResource(R.drawable.ic_flip_flops_for_man_01);
                    img.setImageResource(R.drawable.flip_flops);
                    break;
                case "Winter coat":
                    Cloth = findViewById(R.id.jacket);
                    text.setText("Winter jacket");
                    Cloth.setImageResource(R.drawable.ic_winter_jacket_01);
                    img.setImageResource(R.drawable.winter_jacket);
                    break;
                case "Umbrella":
                    Cloth = findViewById(R.id.umbrella);
                    Cloth.setImageResource(R.drawable.ic_umbrella_01);
                    img.setImageResource(R.drawable.umbrella);
                    break;


            }
        }
    }
    public void setWarning(String warning){
        String warningText = "warning";
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("USERNAME", "ABOBA");
        boolean isGoodWarning = false;
        switch(warning){
            case "tornado":
                warningText = username+", be careful! There will be tornado!";
                isGoodWarning = false;
                break;
            case "squalls":
                warningText = username+", be careful! There will be squalls!";
                isGoodWarning = false;
                break;
            case "thunderstorm":
                warningText = username+", be careful! Storm is comming!";
                isGoodWarning = false;
                break;
            case "rain":
                warningText = username+", rain is comming! You need umbrella!";
                isGoodWarning = false;
                break;
            case "dust":
                warningText = username+", be careful! There will be dust!";
                isGoodWarning = false;
                break;
            case "fog":
                warningText = username+", there will be fog!";
                isGoodWarning = true;
                break;
            case "snow":
                warningText = username+", there will be snow!";
                isGoodWarning = true;
                break;
            case "clouds":
                warningText = username+", it is a good cloudy day!";
                isGoodWarning = true;
                break;
            case "clear":
                warningText = username+", it is a good sunny day!";
                isGoodWarning = true;
                break;
        }
        if(isGoodWarning){
            LinearLayout linearLayout = findViewById(R.id.warning_block);
            linearLayout.setBackgroundResource(R.drawable.green_round);
            ImageView imageView1 = findViewById(R.id.warning_img);
            imageView1.setImageResource(R.drawable.warning_green);
            TextView textView = findViewById(R.id.warning);
            textView.setText(warningText);
            textView.setTextColor(getResources().getColor(R.color.BluryWood));

        }else{
            LinearLayout linearLayout = findViewById(R.id.warning_block);
            linearLayout.setBackgroundResource(R.drawable.red_round);
            ImageView imageView1 = findViewById(R.id.warning_img);
            imageView1.setImageResource(R.drawable.warning_red);
            TextView textView = findViewById(R.id.warning);
            textView.setText(warningText);
            textView.setTextColor(getResources().getColor(R.color.red));

        }
    }
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

    protected void checkPermissions() {

        final List<String> missingPermissions = new ArrayList<String>();
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }else{
                        startLocationUpdates();
                    }
                }
                break;
        }
    }

    public void sendFeed(int it){
        OkHttpClient client = new OkHttpClient();
        int item = -100;
        switch (it){
            case 0:
                item = 0;
                break;
            case 1:
                item = -1;
                break;
            case 2:
                item = 1;
                break;
        }
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        String name = sharedPreferences.getString("USERNAME", "ANON_USER");
        String id = sharedPreferences.getString("USERID", "NO_ID");
        float lat = sharedPreferences.getFloat("LATITUDE", 0);
        float lon = sharedPreferences.getFloat("LONGITUDE", 0);
        String url = "http://argirovga.pythonanywhere.com/api/change_user" +
                "/name="+name+"&user_id="+id+"&new_temp_pref="+item+"&lat="+lat+"&lon="+lon;
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                sendFeed(it);

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response){
                if (response.isSuccessful()) {
                    MainActivity.this.runOnUiThread(() -> startApp());
                }
            }
        });

    }

}