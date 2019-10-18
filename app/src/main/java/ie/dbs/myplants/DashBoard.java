package ie.dbs.myplants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
import java.util.ArrayList;
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
    private List<Object> data;
    private JSONArray jsonArray;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        all_plants_button = findViewById(R.id.all_plants_button);
        task_recycler_view = findViewById(R.id.task_recycler_view);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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
                            task_plants.add(plantSnapshot.getValue(Plant.class));
                        }
                        ArrayList<Plant>my_task_plants=todaysPlants(task_plants);
                        TaskRecyclerAdapter taskRecyclerAdapter = new TaskRecyclerAdapter(my_task_plants);
                        task_recycler_view.setAdapter(taskRecyclerAdapter);
                        ArrayList<Plant>tomorrow_task_plants=tomorrowsPlants(task_plants);
                        TaskRecyclerAdapter taskRecyclerAdapter1=new TaskRecyclerAdapter(tomorrow_task_plants);
                        task_tomorrow_recycler_view.setAdapter(taskRecyclerAdapter1);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(DashBoard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
    private ArrayList<Plant> todaysPlants(ArrayList<Plant> plants)
    {
        ArrayList<Plant>plantList=new ArrayList<>();
        Date yesterday=Utils.addDaysToDate(Utils.getLastMinuteOfDay(new Date()),-1);
        Date tomorrow=Utils.getLastMinuteOfDay(new Date());
        Log.v("yesterday", String.valueOf(yesterday));
        Log.v("tomorrow", String.valueOf(tomorrow));
        for(int i=0;i<plants.size();i++){
            Date fertilizing_date=plants.get(i).getNextFertilizing();
            Date watering_date=plants.get(i).getNextWatering();
            if(fertilizing_date!=null&&fertilizing_date.before(tomorrow)&&fertilizing_date.after(yesterday)){
                plantList.add(plants.get(i));
            }
            else if(watering_date!=null&&watering_date.before(tomorrow) && watering_date.after(yesterday)){
                plantList.add(plants.get(i));
            }
        }
        return plantList;
    }

    private ArrayList<Plant> tomorrowsPlants(ArrayList<Plant> plants)
    {
        ArrayList<Plant>plantList=new ArrayList<>();
        Date day_after_tomorrow=Utils.addDaysToDate(Utils.getLastMinuteOfDay(new Date()),2);
        Date today=Utils.getLastMinuteOfDay(new Date());
        Log.v("day after tomorrow", String.valueOf(day_after_tomorrow));
        Log.v("today", String.valueOf(today));
        for(int i=0;i<plants.size();i++){
            Date fertilizing_date=plants.get(i).getNextFertilizing();
            Date watering_date=plants.get(i).getNextWatering();
            if(fertilizing_date!=null&&fertilizing_date.before(day_after_tomorrow)&&fertilizing_date.after(today)){
                plantList.add(plants.get(i));
            }
            else if(watering_date!=null&&watering_date.before(day_after_tomorrow) && watering_date.after(today)){
                plantList.add(plants.get(i));
            }
        }
        return plantList;
    }


    protected void callAPI(final String url){
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    //jsonArray=new JSONArray(response);
                    final JSONObject jsonObject=new JSONObject(response);



                    //TODO display data
                  /*  RecyclerView recyclerView=(RecyclerView) findViewById(R.id.my_recycler_view);
                    RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.hasFixedSize();
                    recyclerView.setItemViewCacheSize(20);
                    recyclerView.setDrawingCacheEnabled(true);
                    recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    RecyclerView.Adapter mAdapter=new ModuleAdapter(adverts );
                    recyclerView.setAdapter(mAdapter);*/



                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... voids) {
                            WeatherInfo weatherInfo;
                           List<WeatherInfo> wetList=new ArrayList<>();
                            try {
                                for (int i=0;i<jsonObject.length();i++) {
                                    double currentTemp=Double.parseDouble(jsonObject.getString("list.main.temp"));
                                    double currentMinTemp=Double.parseDouble(jsonObject.getString("list.main.temp_min"));
                                    double currentMaxTemp=Double.parseDouble(jsonObject.getString("list.main.temp_max"));
                                    double currentWindSpeed=Double.parseDouble(jsonObject.getString("list.wind.speed"));
                                    weatherInfo = new WeatherInfo(currentTemp,currentMinTemp,currentMaxTemp,currentWindSpeed);
                                    wetList.add(weatherInfo);
                                    Log.v("weatherinfo", weatherInfo.toString());
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.v("thisisanerror", e.getMessage());
                            }
                            return null;
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

}
