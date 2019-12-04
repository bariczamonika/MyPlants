package ie.dbs.myplants;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.icu.util.LocaleData;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DashBoard extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private RecyclerView task_recycler_view;
    private ArrayList<Plant> task_plants;
    private DatabaseReference databaseReference;
    private RecyclerView task_tomorrow_recycler_view;
    private int longitude;
    private int latitude;
    private SearchView searchView;
    private ArrayList<Plant> filteredPlants;
    private ArrayList<Plant> filteredTomorrowPlants;
    private ArrayList<Plant> my_task_plants;
    private ArrayList<Plant> tomorrow_task_plants;
    private ConnectionReceiver receiver;
    private SharedPreferences sharedPreferences;
    private boolean isTodayExpanded=false;
    RecyclerView recyclerView;

    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
        String lang=sharedPreferences.getString("language", "en");
        Toast.makeText(this, lang, Toast.LENGTH_LONG).show();
        Resources res = getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(lang);
        res.updateConfiguration(conf, dm);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        setContentView(R.layout.activity_dash_board);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        receiver = new ConnectionReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, filter);
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET
        };

        if(!Utils.hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        Button all_plants_button = findViewById(R.id.all_plants_button);
        task_recycler_view = findViewById(R.id.task_recycler_view);
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        searchView=findViewById(R.id.dashboardSearchBar);

        recyclerView=findViewById(R.id.today_weather_recycler_view);

        //settings part
        FloatingActionButton floatingActionButton=findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DashBoard.this, SettingsActivity.class);
                startActivity(intent);

            }
        });





        FloatingActionButton floatingActionButton1=findViewById(R.id.fab_logout);
        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialog = new AlertDialog.Builder(DashBoard.this).create();
                dialog.setTitle("Logout");
                dialog.setMessage("Are you sure?");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.mAuth.signOut();
                        Intent intent = new Intent(DashBoard.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });






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
                        url = getResources().getString(R.string.api_url) + "lat="+latitude+"&lon="
                                +longitude+"&APPID=bf9fa411797e941a3536db50a36c86d5&units=metric";
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
        task_recycler_view.smoothScrollToPosition(0);
        task_recycler_view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);

        task_tomorrow_recycler_view=findViewById(R.id.task_tomorrow_recycler_view);
        RecyclerView.LayoutManager layoutManager1=new LinearLayoutManager(getApplicationContext());
        task_tomorrow_recycler_view.setLayoutManager(layoutManager1);
        task_tomorrow_recycler_view.hasFixedSize();
        task_tomorrow_recycler_view.setItemViewCacheSize(20);
        task_tomorrow_recycler_view.smoothScrollToPosition(0);
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

    //display today's and tomorrow's tasks
    private void displayTasks() {
        if(databaseReference!=null){
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                       Utils.today_plants_task_string.clear();
                       Utils.myPlants.clear();
                        task_plants = new ArrayList<>();
                        for (DataSnapshot plantSnapshot : dataSnapshot.getChildren()) {
                            Plant myPlant=plantSnapshot.getValue(Plant.class);
                            if (myPlant!=null){
                                Utils.myPlants.add(myPlant);
                            Plant plant=Utils.autoChangeDatesOnceItIsReached(myPlant);
                            task_plants.add(plant);
                            Utils.addPlant(plant);}
                        }
                        my_task_plants=todaysPlants(task_plants);
                        Utils.today_plants_task_string.addAll(my_task_plants);
                        tomorrow_task_plants=tomorrowsPlants(task_plants);
                        //notifies the widget
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(Utils.applicationContext);
                        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(Utils.applicationContext, NewAppWidget.class));
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);

                        TaskRecyclerAdapter taskRecyclerAdapter = new TaskRecyclerAdapter(my_task_plants);
                        task_recycler_view.setAdapter(taskRecyclerAdapter);
                        taskRecyclerAdapter.notifyDataSetChanged();
                        task_recycler_view.smoothScrollToPosition(0);
                        TomorrowTaskRecyclerAdapter tomorrowTaskRecyclerAdapter=new TomorrowTaskRecyclerAdapter(tomorrow_task_plants);
                        task_tomorrow_recycler_view.setAdapter(tomorrowTaskRecyclerAdapter);
                        tomorrowTaskRecyclerAdapter.notifyDataSetChanged();
                        task_tomorrow_recycler_view.smoothScrollToPosition(0);


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

    //search among today's tasks
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

    //search among tomorrow's tasks
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



    //return the plants that are due for some tasks today
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

    //return the plants that are due for some tasks tomorrow
    private ArrayList<Plant> tomorrowsPlants(ArrayList<Plant> plants)
    {
        ArrayList<Plant>plantList=new ArrayList<>();
        Date day_after_tomorrow=Utils.addOneSecondToDate(Utils.addDaysToDate(Utils.getLastMinuteOfDay(new Date()),1));
        Date today=Utils.addOneSecondToDate(Utils.getLastMinuteOfDay(new Date()));
        Log.v("day after tomorrow", String.valueOf(day_after_tomorrow));
        Log.v("today", String.valueOf(today));
        for(int i=0;i<plants.size();i++){
            Plant myPlant=plants.get(i);
            if(myPlant.getLastWatered()!=null || myPlant.getLastFertilized()!=null) {
                Date fertilizing_date = myPlant.getNextFertilizing();
                Date watering_date = myPlant.getNextWatering();
                boolean isFertilizingDateTomorrow = fertilizing_date != null && fertilizing_date.before(day_after_tomorrow) && fertilizing_date.after(today);
                boolean isWateringDateTomorrow = watering_date != null && watering_date.before(day_after_tomorrow) && (watering_date.after(today));
                if (isFertilizingDateTomorrow && isWateringDateTomorrow) {

                    Plant newPlant = new Plant(myPlant);
                    newPlant.setNextFertilizing(null);
                    plantList.add(newPlant);
                    Plant newNewPlant = new Plant(myPlant);
                    newNewPlant.setNextWatering(null);
                    plantList.add(newNewPlant);
                } else if (isFertilizingDateTomorrow ^ isWateringDateTomorrow)
                    plantList.add(myPlant);
                if (myPlant.getWateringNeeds().value == 1 && watering_date.before(today)) {
                    plantList.add(myPlant);
                }
            }
        }
        return plantList;
    }


    //call the weather API
    private void callAPI(final String url){
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    final JSONObject jsonObject=new JSONObject(response);
                    final JSONArray listArray=jsonObject.getJSONArray("list");



                  new AsyncTask<Void, Void, Void>() {

                      final ArrayList<WeatherInfo> todayWeatherList=new ArrayList<>();
                      final ArrayList<WeatherInfo> tomorrowWeatherList=new ArrayList<>();
                      final WeatherInfo todaysWeather=new WeatherInfo();
                      final WeatherInfo tomorrowsWeather=new WeatherInfo();
                      int notificationIterator=0;
                        @Override
                        protected Void doInBackground(Void... voids) {

                            try {
                                for(int i=0;i<listArray.length();i++) {
                                    JSONObject jsonTempObject=listArray.getJSONObject(i);
                                    JSONObject tempObject=jsonTempObject.getJSONObject("main");
                                    JSONObject windObject=jsonTempObject.getJSONObject("wind");
                                    String timeObject=jsonTempObject.getString("dt_txt");
                                    JSONArray weatherObject=jsonTempObject.getJSONArray("weather");
                                    if(Utils.isDateToday(Utils.createDateFromString(timeObject)))
                                    {
                                        WeatherInfo weather=new WeatherInfo();
                                        weather.setCurrentTemp(Double.parseDouble(tempObject.getString("temp")));
                                        weather.setCurrentMinTemp(Double.parseDouble(tempObject.getString("temp_min")));
                                        weather.setCurrentMaxTemp(Double.parseDouble(tempObject.getString("temp_max")));
                                        weather.setCurrentWindSpeed(Double.parseDouble(windObject.getString("speed"))*3.6);
                                        weather.setDateTime(timeObject);
                                        JSONObject weatherObj=weatherObject.getJSONObject(0);
                                        weather.setBriefDescription(weatherObj.getString("main"));
                                        weather.setDetailedDescription(weatherObj.getString("description"));
                                        weather.setWeatherIcon(weatherObj.getString("icon"));

                                        todayWeatherList.add(weather);
                                        Log.v("todaystemp",String.valueOf(weather.getCurrentTemp()));
                                        Log.v("todaystempmin",String.valueOf(weather.getCurrentMinTemp()));
                                        Log.v("todaystempmax",String.valueOf(weather.getCurrentMaxTemp()));

                                        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(Utils.applicationContext);
                                        Log.v("sharedprefs",sharedPreferences.getAll().toString());
                                        Log.v("sharedpreftemp", sharedPreferences.getString("temperature","0"));
                                        int windSpeed=50;
                                        int temp=0;
                                        if(sharedPreferences.getString("wind", "50")!=null)
                                            windSpeed=Integer.parseInt(sharedPreferences.getString("wind", "50"));
                                        Log.v("windspeedpref", String.valueOf(windSpeed));

                                        if(sharedPreferences.getString("temperature", "0")!=null)
                                            temp=Integer.parseInt(sharedPreferences.getString("temperature", "0"));
                                        Log.v("temperaturepref", String.valueOf(temp));
                                        if(weather.getCurrentMinTemp()<temp)
                                        {
                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(Utils.applicationContext, Utils.CHANNEL_ID)
                                                    .setSmallIcon(R.drawable.my_plant_icon)
                                                    .setContentTitle(getResources().getString(R.string.weather_alert))
                                                    .setContentText(getResources().getString(R.string.frost_expected) + timeObject)
                                                    .setStyle(new NotificationCompat.BigTextStyle()
                                                            .bigText(getResources().getString(R.string.frost_expected) + timeObject))
                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                            NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(Utils.applicationContext);
                                            notificationManagerCompat.notify(notificationIterator,builder.build());
                                            notificationIterator++;
                                        }

                                        if(weather.getCurrentWindSpeed()>windSpeed)
                                        {
                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(Utils.applicationContext, Utils.CHANNEL_ID)
                                                    .setSmallIcon(R.drawable.my_plant_icon)
                                                    .setContentTitle(getResources().getString(R.string.weather_alert))
                                                    .setContentText(getResources().getString(R.string.wind_expected) + timeObject)
                                                    .setStyle(new NotificationCompat.BigTextStyle()
                                                            .bigText(getResources().getString(R.string.wind_expected) + timeObject))
                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                            NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(Utils.applicationContext);
                                            notificationManagerCompat.notify(notificationIterator,builder.build());
                                            notificationIterator++;
                                        }

                                        Log.v("weatherTimeToday", timeObject);
                                    }
                                    else if (Utils.isDateTomorrow(Utils.createDateFromString(timeObject)))
                                    {
                                        WeatherInfo weather=new WeatherInfo();
                                        weather.setCurrentTemp(Double.parseDouble(tempObject.getString("temp")));
                                        weather.setCurrentMinTemp(Double.parseDouble(tempObject.getString("temp_min")));
                                        weather.setCurrentMaxTemp(Double.parseDouble(tempObject.getString("temp_max")));
                                        weather.setCurrentWindSpeed(Double.parseDouble(windObject.getString("speed"))*3.6);
                                        weather.setDateTime(timeObject);
                                        JSONObject weatherObj=weatherObject.getJSONObject(0);
                                        weather.setBriefDescription(weatherObj.getString("main"));
                                        weather.setDetailedDescription(weatherObj.getString("description"));
                                        weather.setWeatherIcon(weatherObj.getString("icon"));
                                        tomorrowWeatherList.add(weather);
                                        if(weather.getCurrentMinTemp()<0)
                                        {
                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(Utils.applicationContext, Utils.CHANNEL_ID)
                                                    .setSmallIcon(R.drawable.my_plant_icon)
                                                    .setContentTitle(getResources().getString(R.string.weather_alert))
                                                    .setContentText(getResources().getString(R.string.frost_expected) + timeObject)
                                                    .setStyle(new NotificationCompat.BigTextStyle()
                                                            .bigText(getResources().getString(R.string.frost_expected) + timeObject))
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
                                                    .setContentText(getResources().getString(R.string.wind_expected) + timeObject)
                                                    .setStyle(new NotificationCompat.BigTextStyle()
                                                            .bigText(getResources().getString(R.string.wind_expected) + timeObject))
                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                            NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(Utils.applicationContext);
                                            notificationManagerCompat.notify(notificationIterator,builder.build());
                                            notificationIterator++;
                                        }
                                        Log.v("weatherTimeTomorrow", timeObject);
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

                          RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext(),
                                  LinearLayoutManager.HORIZONTAL,false);
                          recyclerView.setLayoutManager(layoutManager);
                          recyclerView.hasFixedSize();
                          recyclerView.setItemViewCacheSize(20);
                          recyclerView.setDrawingCacheEnabled(true);
                          recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                          RecyclerView.Adapter mAdapter=new WeatherForecastRecyclerAdapter(todayWeatherList);
                          recyclerView.setAdapter(mAdapter);
                          RecyclerView recyclerView1=findViewById(R.id.tomorrow_weather_recycler_view);
                          RecyclerView.LayoutManager layoutManager1=new LinearLayoutManager(getApplicationContext(),
                                  LinearLayoutManager.HORIZONTAL,false);
                          recyclerView1.setLayoutManager(layoutManager1);
                          recyclerView1.hasFixedSize();
                          recyclerView1.setItemViewCacheSize(20);
                          recyclerView1.setDrawingCacheEnabled(true);
                          recyclerView1.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                          RecyclerView.Adapter mAdapter1=new WeatherForecastRecyclerAdapter(tomorrowWeatherList);
                          recyclerView1.setAdapter(mAdapter1);
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
                return new HashMap<>();
            }
        };
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //if(stringRequest!=null)
                Utils.queue.add(stringRequest);
            }
        }, 200);
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

    //if the user changes the settings
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(Utils.applicationContext);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        if(key.equals("notification_time_minute")||key.equals("notification_time_hour")){
            String userID = Utils.user.getUid();
            databaseReference = Utils.databaseReference.child("users").child(userID).child("plants");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot plantSnapshot : dataSnapshot.getChildren()) {
                            Plant myPlant=plantSnapshot.getValue(Plant.class);
                            if(myPlant.isNotificationWatering())
                            {
                                Utils.setWateringNotification(myPlant);
                            }
                            if(myPlant.isNotificationFertilizing())
                            {
                                Utils.setFertilizingNotification(myPlant);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        }

        if(key.equals("language")){
            String lang=sharedPreferences.getString("language", "en");
            setLocale(lang);

        }
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    //change the language
    public void setLocale(String lang) {

        Locale myLocale = new Locale(lang);
        Locale.setDefault(Locale.ENGLISH);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(Utils.applicationContext);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(Utils.applicationContext, NewAppWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        RemoteViews views=new RemoteViews(getPackageName(), R.layout.new_app_widget);
        appWidgetManager.updateAppWidget(appWidgetIds,views);
        Intent refresh = new Intent(this, DashBoard.class);
        finish();
        startActivity(refresh);
    }


}
