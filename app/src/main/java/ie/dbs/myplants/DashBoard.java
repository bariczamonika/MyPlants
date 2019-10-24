package ie.dbs.myplants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Notification;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.Permission;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashBoard extends AppCompatActivity {
    private RecyclerView task_recycler_view;
    private Button all_plants_button;
    private ArrayList<Plant> task_plants;
    private DatabaseReference databaseReference;
    private RecyclerView task_tomorrow_recycler_view;
    private FusedLocationProviderClient fusedLocationClient;
    private int longitude;
    private int latitude;
    private SearchView searchView;
    private ArrayList<Plant> filteredPlants;
    private ArrayList<Plant> filteredTomorrowPlants;
    private ArrayList<Plant> my_task_plants;
    private ArrayList<Plant> tomorrow_task_plants;
    private TextView current_temp, current_max_temp, current_min_temp,current_wind_speed,tomorrow_temp,
    tomorrow_min_temp,tomorrow_max_temp,tomorrow_wind_speed;
    private List<Object> data;
    private ExpandableRelativeLayout expandable_today_weather;
    private ExpandableRelativeLayout expandable_tomorrow_weather;
    private TextView tomorrows_forecast, todays_forecast, tomorrows_forecast_plus_minus, todays_forecast_plus_minus;
    private ConnectionReceiver receiver;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        receiver = new ConnectionReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, filter);
        Utils.AskForPermission(Manifest.permission.CAMERA, DashBoard.this);
        Utils.AskForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, DashBoard.this);
        Utils.AskForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, DashBoard.this);
        all_plants_button = findViewById(R.id.all_plants_button);
        task_recycler_view = findViewById(R.id.task_recycler_view);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        current_temp=findViewById(R.id.current_temp);
        current_max_temp=findViewById(R.id.current_max);
        current_min_temp=findViewById(R.id.current_min);
        current_wind_speed=findViewById(R.id.current_wind_speed);
        tomorrow_max_temp=findViewById(R.id.tomorrow_max);
        tomorrow_min_temp=findViewById(R.id.tomorrow_min);
        tomorrow_temp=findViewById(R.id.tomorrow_temperature);
        tomorrow_wind_speed=findViewById(R.id.tomorrow_wind_speed);
        searchView=findViewById(R.id.dashboardSearchBar);
        expandable_today_weather=findViewById(R.id.expandable_layout_today);
        expandable_tomorrow_weather=findViewById(R.id.expandable_layout_tomorrow);
        todays_forecast=findViewById(R.id.todays_forecast);
        tomorrows_forecast=findViewById(R.id.tomorrows_forecast);
        todays_forecast_plus_minus=findViewById(R.id.todays_forecast_plus_minus);
        tomorrows_forecast_plus_minus=findViewById(R.id.tomorrows_forecast_plus_minus);
        expandable_tomorrow_weather.collapse();
        expandable_today_weather.collapse();

        todays_forecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expandable_today_weather.isExpanded()){
                    expandable_today_weather.collapse();
                    todays_forecast_plus_minus.setText("+");
                }
                else{
                    expandable_today_weather.expand();
                    todays_forecast_plus_minus.setText("-");
                }
            }
        });

        tomorrows_forecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expandable_tomorrow_weather.isExpanded()){
                    expandable_tomorrow_weather.collapse();
                    tomorrows_forecast_plus_minus.setText("+");
                }
                else{
                    expandable_tomorrow_weather.expand();
                    tomorrows_forecast_plus_minus.setText("-");
                }
            }
        });

        Utils.AskForPermission(Manifest.permission.ACCESS_COARSE_LOCATION, DashBoard.this);
        Utils.AskForPermission(Manifest.permission.ACCESS_NETWORK_STATE, DashBoard.this);
        Utils.AskForPermission(Manifest.permission.INTERNET, DashBoard.this);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED
        && (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)==PackageManager.PERMISSION_GRANTED)) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        longitude=(int)location.getLongitude();
                        latitude=(int)location.getLatitude();
                        Log.v("longitude", String.valueOf(longitude));
                        Log.v("latitude", String.valueOf(latitude));
                        url = getResources().getString(R.string.api_url) + "lat="+String.valueOf(latitude)+"&lon="
                                +String.valueOf(longitude)+"&APPID=bf9fa411797e941a3536db50a36c86d5&units=metric";
                        Log.v("apiURL", url);
                        callAPI(url);
                    }
                }
            });
        }


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        task_recycler_view.setLayoutManager(layoutManager);
        task_recycler_view.hasFixedSize();
        task_recycler_view.setItemViewCacheSize(20);
        task_recycler_view.setDrawingCacheEnabled(true);
        task_recycler_view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);

        task_tomorrow_recycler_view=findViewById(R.id.task_tomorrow_recycler_view);
        RecyclerView.LayoutManager layoutManager1=new LinearLayoutManager(getApplicationContext());
        task_tomorrow_recycler_view.setLayoutManager(layoutManager1);
        task_tomorrow_recycler_view.hasFixedSize();
        task_tomorrow_recycler_view.setItemViewCacheSize(20);
        task_tomorrow_recycler_view.setDrawingCacheEnabled(true);
        task_tomorrow_recycler_view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);

        all_plants_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DashBoard.this, MainActivity.class);
                startActivity(intent);
            }
        });
        String userID = Utils.user.getUid();
        databaseReference = Utils.databaseReference.child("users").child(userID).child("plants");
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayTasks();
    }

    private void displayTasks() {
        if(databaseReference!=null){
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        task_plants = new ArrayList<>();
                        for (DataSnapshot plantSnapshot : dataSnapshot.getChildren()) {
                            Plant myPlant=plantSnapshot.getValue(Plant.class);
                            myPlant=Utils.autoChangeDatesOnceItIsReached(myPlant);
                            task_plants.add(Utils.autoChangeDatesOnceItIsReached(myPlant));
                            addPlant(myPlant);
                        }
                        my_task_plants=todaysPlants(task_plants);
                        TaskRecyclerAdapter taskRecyclerAdapter = new TaskRecyclerAdapter(my_task_plants);
                        task_recycler_view.setAdapter(taskRecyclerAdapter);
                        tomorrow_task_plants=tomorrowsPlants(task_plants);
                        TomorrowTaskRecyclerAdapter tomorrowTaskRecyclerAdapter=new TomorrowTaskRecyclerAdapter(tomorrow_task_plants);
                        task_tomorrow_recycler_view.setAdapter(tomorrowTaskRecyclerAdapter);
                    }
                    if(searchView!=null){
                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                filteredPlants=searchToday(newText);
                                filteredTomorrowPlants=searchTomorrow(newText);
                                return true;
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(DashBoard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    private ArrayList<Plant> searchToday(String str){
        ArrayList<Plant> list=new ArrayList<>();
        for(Plant plant:my_task_plants){
            if(plant.getName().toLowerCase().trim().contains(str.toLowerCase())){
                list.add(plant);
            }
        }
        TaskRecyclerAdapter taskRecyclerAdapter=new TaskRecyclerAdapter(list);
        task_recycler_view.setAdapter(taskRecyclerAdapter);
        return list;
    }

    private ArrayList<Plant> searchTomorrow(String str){
        ArrayList<Plant> list=new ArrayList<>();
        for(Plant plant:tomorrow_task_plants){
            if(plant.getName().toLowerCase().trim().contains(str.toLowerCase())){
                list.add(plant);
            }
        }
        TomorrowTaskRecyclerAdapter tomorrowTaskRecyclerAdapter=new TomorrowTaskRecyclerAdapter(list);
        task_tomorrow_recycler_view.setAdapter(tomorrowTaskRecyclerAdapter);
        return list;
    }



    private ArrayList<Plant> todaysPlants(ArrayList<Plant> plants)
    {
        ArrayList<Plant>plantList=new ArrayList<>();
        Date tomorrow=Utils.addOneSecondToDate(Utils.getLastMinuteOfDay(new Date()));
        Log.v("tomorrow", String.valueOf(tomorrow));
        for(int i=0;i<plants.size();i++){
            Plant myPlant=plants.get(i);
            Date fertilizing_date=plants.get(i).getNextFertilizing();
            Date watering_date=plants.get(i).getNextWatering();
            boolean isFertilizingDateToday=fertilizing_date!=null&&fertilizing_date.before(tomorrow);
            boolean isWateringDateToday=watering_date!=null&& watering_date.before(tomorrow);

            if(isFertilizingDateToday&&isWateringDateToday)
            {

                Plant newPlant=new Plant(myPlant);
                newPlant.setNextFertilizing(null);
                plantList.add(newPlant);
                Plant newNewPlant=new Plant(myPlant);
                newNewPlant.setNextWatering(null);
                plantList.add(newNewPlant);
            }
            else if(isFertilizingDateToday ^ isWateringDateToday)
                plantList.add(myPlant);
        }
        return plantList;
    }

    private ArrayList<Plant> tomorrowsPlants(ArrayList<Plant> plants)
    {
        ArrayList<Plant>plantList=new ArrayList<>();
        Date day_after_tomorrow=Utils.addOneSecondToDate(Utils.addDaysToDate(Utils.getLastMinuteOfDay(new Date()),1));
        Date today=Utils.addOneSecondToDate(Utils.getLastMinuteOfDay(new Date()));
        Log.v("day after tomorrow", String.valueOf(day_after_tomorrow));
        Log.v("today", String.valueOf(today));
        for(int i=0;i<plants.size();i++){
            Plant myPlant=plants.get(i);
            Date fertilizing_date=myPlant.getNextFertilizing();
            Date watering_date=myPlant.getNextWatering();
            boolean isFertilizingDateTomorrow=fertilizing_date!=null&&fertilizing_date.before(day_after_tomorrow)&&fertilizing_date.after(today);
            boolean isWateringDateTomorrow=watering_date!=null&&watering_date.before(day_after_tomorrow) && (watering_date.after(today));
            if(isFertilizingDateTomorrow&&isWateringDateTomorrow)
            {

                Plant newPlant=new Plant(myPlant);
                newPlant.setNextFertilizing(null);
                plantList.add(newPlant);
                Plant newNewPlant=new Plant(myPlant);
                newNewPlant.setNextWatering(null);
                plantList.add(newNewPlant);
            }
            else if(isFertilizingDateTomorrow ^ isWateringDateTomorrow)
                plantList.add(myPlant);
            if(myPlant.getWateringNeeds().value==1&& watering_date.before(today))
            {
                plantList.add(myPlant);
            }
        }
        return plantList;
    }


    protected void callAPI(final String url){
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    final JSONObject jsonObject=new JSONObject(response);
                    final JSONArray listArray=jsonObject.getJSONArray("list");
                  new AsyncTask<Void, Void, Void>() {

                      List<WeatherInfo> todayWeatherList=new ArrayList<>();
                      List<WeatherInfo> tomorrowWeatherList=new ArrayList<>();
                      WeatherInfo todaysWeather=new WeatherInfo();
                      WeatherInfo tomorrowsWeather=new WeatherInfo();
                      int notificationIterator=0;
                        @Override
                        protected Void doInBackground(Void... voids) {

                            try {
                                for(int i=0;i<listArray.length();i++) {
                                    JSONObject jsonTempObject=listArray.getJSONObject(i);
                                    JSONObject tempObject=jsonTempObject.getJSONObject("main");
                                    JSONObject windObject=jsonTempObject.getJSONObject("wind");
                                    String timeObject=jsonTempObject.getString("dt_txt");
                                    if(Utils.isDateToday(Utils.createDateFromString(timeObject)))
                                    {
                                        WeatherInfo weather=new WeatherInfo();
                                        weather.setCurrentTemp(Double.parseDouble(tempObject.getString("temp")));
                                        weather.setCurrentMinTemp(Double.parseDouble(tempObject.getString("temp_min")));
                                        weather.setCurrentMaxTemp(Double.parseDouble(tempObject.getString("temp_max")));
                                        weather.setCurrentWindSpeed(Double.parseDouble(windObject.getString("speed"))*3.6);
                                        todayWeatherList.add(weather);
                                        if(weather.getCurrentMinTemp()<0)
                                        {
                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(Utils.applicationContext, Utils.CHANNEL_ID)
                                                    .setSmallIcon(R.drawable.my_plant_icon)
                                                    .setContentTitle(getResources().getString(R.string.weather_alert))
                                                    .setContentText(getResources().getString(R.string.frost_expected) + String.valueOf(timeObject))
                                                    .setStyle(new NotificationCompat.BigTextStyle()
                                                            .bigText(getResources().getString(R.string.frost_expected) + String.valueOf(timeObject)))
                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                            NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(Utils.applicationContext);
                                            notificationManagerCompat.notify(notificationIterator,builder.build());
                                            notificationIterator++;
                                        }

                                        if(weather.getCurrentWindSpeed()>50)
                                        {
                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(Utils.applicationContext, Utils.CHANNEL_ID)
                                                    .setSmallIcon(R.drawable.my_plant_icon)
                                                    .setContentTitle(getResources().getString(R.string.weather_alert))
                                                    .setContentText(getResources().getString(R.string.wind_expected) + String.valueOf(timeObject))
                                                    .setStyle(new NotificationCompat.BigTextStyle()
                                                            .bigText(getResources().getString(R.string.wind_expected) + String.valueOf(timeObject)))
                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                            NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(Utils.applicationContext);
                                            notificationManagerCompat.notify(notificationIterator,builder.build());
                                            notificationIterator++;
                                        }

                                        Log.v("weatherTimeToday", String.valueOf(timeObject));
                                    }
                                    else if (Utils.isDateTomorrow(Utils.createDateFromString(timeObject)))
                                    {
                                        WeatherInfo weather=new WeatherInfo();
                                        weather.setCurrentTemp(Double.parseDouble(tempObject.getString("temp")));
                                        weather.setCurrentMinTemp(Double.parseDouble(tempObject.getString("temp_min")));
                                        weather.setCurrentMaxTemp(Double.parseDouble(tempObject.getString("temp_max")));
                                        weather.setCurrentWindSpeed(Double.parseDouble(windObject.getString("speed"))*3.6);
                                        tomorrowWeatherList.add(weather);
                                        if(weather.getCurrentMinTemp()<0)
                                        {
                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(Utils.applicationContext, Utils.CHANNEL_ID)
                                                    .setSmallIcon(R.drawable.my_plant_icon)
                                                    .setContentTitle(getResources().getString(R.string.weather_alert))
                                                    .setContentText(getResources().getString(R.string.frost_expected) + String.valueOf(timeObject))
                                                    .setStyle(new NotificationCompat.BigTextStyle()
                                                            .bigText(getResources().getString(R.string.frost_expected) + String.valueOf(timeObject)))
                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                            NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(Utils.applicationContext);
                                            notificationManagerCompat.notify(notificationIterator,builder.build());
                                            notificationIterator++;
                                        }
                                        if(weather.getCurrentWindSpeed()>50)
                                        {
                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(Utils.applicationContext, Utils.CHANNEL_ID)
                                                    .setSmallIcon(R.drawable.my_plant_icon)
                                                    .setContentTitle(getResources().getString(R.string.weather_alert))
                                                    .setContentText(getResources().getString(R.string.wind_expected) + String.valueOf(timeObject))
                                                    .setStyle(new NotificationCompat.BigTextStyle()
                                                            .bigText(getResources().getString(R.string.wind_expected) + String.valueOf(timeObject)))
                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                            NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(Utils.applicationContext);
                                            notificationManagerCompat.notify(notificationIterator,builder.build());
                                            notificationIterator++;
                                        }
                                        Log.v("weatherTimeTomorrow", String.valueOf(timeObject));
                                    }

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.v("thisisanerror", e.getMessage());
                            }
                            return null;
                        }

                      @Override
                      protected void onPostExecute(Void aVoid) {
                          super.onPostExecute(aVoid);
                          double avgTemp=0;
                          double avgMinTemp=0;
                          double avgMaxTemp=0;
                          double avgWindSpeed=0;
                          DecimalFormat decimalFormat=new DecimalFormat("#.##");
                          if(todayWeatherList!=null) {
                              for (int i = 0; i < todayWeatherList.size(); i++) {
                                  avgTemp = avgTemp + todayWeatherList.get(i).getCurrentTemp();
                                  Log.v("weatherAVGTemp",String.valueOf(avgTemp));
                                  avgMaxTemp = avgMaxTemp + todayWeatherList.get(i).getCurrentMaxTemp();
                                  avgMinTemp = avgMinTemp + todayWeatherList.get(i).getCurrentMinTemp();
                                  avgWindSpeed = avgWindSpeed + todayWeatherList.get(i).getCurrentWindSpeed();
                              }
                              avgTemp = avgTemp / todayWeatherList.size();
                              avgMaxTemp=avgMaxTemp/todayWeatherList.size();
                              avgMinTemp=avgMinTemp/todayWeatherList.size();
                              avgWindSpeed=avgWindSpeed/todayWeatherList.size();
                              Log.v("weatherAVGTemp",String.valueOf(avgTemp));
                              todaysWeather.setCurrentTemp(avgTemp);
                              todaysWeather.setCurrentMaxTemp(avgMaxTemp);
                              todaysWeather.setCurrentMinTemp(avgMinTemp);
                              todaysWeather.setCurrentWindSpeed(avgWindSpeed);
                              current_temp.setText(decimalFormat.format(todaysWeather.getCurrentTemp()) + "°C");
                              current_max_temp.setText(decimalFormat.format(todaysWeather.getCurrentMaxTemp()) + "°C");
                              current_min_temp.setText(decimalFormat.format(todaysWeather.getCurrentMinTemp())+"°C");
                              current_wind_speed.setText(decimalFormat.format(todaysWeather.getCurrentWindSpeed()) + "km/h");
                          }

                          if(tomorrowWeatherList!=null)
                          {
                              for (int i = 0; i < tomorrowWeatherList.size(); i++) {
                                  avgTemp = avgTemp + tomorrowWeatherList.get(i).getCurrentTemp();
                                  avgMaxTemp = avgMaxTemp + tomorrowWeatherList.get(i).getCurrentMaxTemp();
                                  avgMinTemp = avgMinTemp + tomorrowWeatherList.get(i).getCurrentMinTemp();
                                  avgWindSpeed = avgWindSpeed + tomorrowWeatherList.get(i).getCurrentWindSpeed();
                              }
                              avgTemp = avgTemp / tomorrowWeatherList.size();
                              avgMaxTemp=avgMaxTemp/tomorrowWeatherList.size();
                              avgMinTemp=avgMinTemp/tomorrowWeatherList.size();
                              avgWindSpeed=avgWindSpeed/tomorrowWeatherList.size();
                              tomorrowsWeather.setCurrentTemp(avgTemp);
                              tomorrowsWeather.setCurrentMaxTemp(avgMaxTemp);
                              tomorrowsWeather.setCurrentMinTemp(avgMinTemp);
                              tomorrowsWeather.setCurrentWindSpeed(avgWindSpeed);
                              tomorrow_max_temp.setText(decimalFormat.format(tomorrowsWeather.getCurrentMaxTemp()) + "°C");
                              tomorrow_min_temp.setText(decimalFormat.format(tomorrowsWeather.getCurrentMinTemp()) + "°C");
                              tomorrow_temp.setText(decimalFormat.format(tomorrowsWeather.getCurrentTemp())+"°C");
                              tomorrow_wind_speed.setText(decimalFormat.format(tomorrowsWeather.getCurrentWindSpeed()) + "km/h");
                          }

                      }
                  }.execute();




                } catch (Exception e) {
                    Log.v("Error", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("Response is", error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.queue.add(stringRequest);
            }
        }, 200);
    }
    private void addPlant(Plant plant) {
        String userID=Utils.user.getUid();
        Utils.databaseReference.child("users").child(userID).child("plants").child(plant.getPlantID()).setValue(plant);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }
}
