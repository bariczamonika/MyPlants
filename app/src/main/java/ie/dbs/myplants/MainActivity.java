package ie.dbs.myplants;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private ArrayList<Plant> myPlants;
    private DatabaseReference databaseReference;
    private SearchView searchView;
    private AlertDialog alertDialog1;
    private ArrayList<Plant> filteredPlants;
    private ConnectionReceiver receiver;

    private RecyclerView recyclerView;
    final private CharSequence[] values = {"Name", "Date Added", "Outdoor Plants", "Indoor Plants"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        recyclerView = findViewById(R.id.my_recycler_view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.hasFixedSize();
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);

        receiver=new ConnectionReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, filter);
        searchView=findViewById(R.id.searchBar);
        Button addPlantButton = findViewById(R.id.addPlant);
        addPlantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddPlant.class);
                intent.putExtra("plantID", "");
                startActivity(intent);
            }
        });

        FloatingActionButton floatingActionButton=findViewById(R.id.fab_sort_by);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialogWithRadioButtons();
            }
        });
        String userID = Utils.user.getUid();
        databaseReference = Utils.databaseReference.child("users").child(userID).child("plants");
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayPlants();
    }

    //display all plants
    private void displayPlants(){
        if(databaseReference!=null){
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        myPlants=new ArrayList<>();
                        List<Integer> plantIDs=new ArrayList<>();
                        for(DataSnapshot plantSnapshot:dataSnapshot.getChildren()){
                            Plant myPlant=plantSnapshot.getValue(Plant.class);
                            myPlants.add(myPlant);
                            plantIDs.add(Integer.parseInt(myPlant.getPlantID()));
                        }
                        int currentID=0;
                        for(int i=0;i<plantIDs.size();i++)
                        {
                                if (!plantIDs.contains(i)) {
                                    currentID = i;
                                    break;
                                }
                                else
                                    currentID=i+1;
                        }
                        Utils.plantIterator=currentID;
                        PlantRecyclerAdapter plantRecyclerAdapter=new PlantRecyclerAdapter(myPlants);
                        recyclerView.setAdapter(plantRecyclerAdapter);
                        filteredPlants=myPlants;
                    }
                    if(searchView!=null){
                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                filteredPlants=search(newText);
                                return true;
                            }
                        });
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    //search among the plants and returned the results
    private ArrayList<Plant> search(String str){
        ArrayList<Plant> list=new ArrayList<>();
        for(Plant plant:myPlants){
            if(plant.getName().toLowerCase().trim().contains(str.toLowerCase())){
                list.add(plant);
            }
        }
        PlantRecyclerAdapter plantRecyclerAdapter=new PlantRecyclerAdapter(list);
        recyclerView.setAdapter(plantRecyclerAdapter);
        return list;
    }

    //create sort by alertdialog
    private void CreateAlertDialogWithRadioButtons() {
        if(searchView.getQuery()==null)
            filteredPlants=myPlants;
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Sort By");

        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch(item)
                {
                    case 0:
                        ArrayList<Plant>sortedList=Utils.sortStringBubble(filteredPlants, true);
                        PlantRecyclerAdapter plantRecyclerAdapter=new PlantRecyclerAdapter(sortedList);
                        recyclerView.setAdapter(plantRecyclerAdapter);
                        Toast.makeText(MainActivity.this, "Name selected", Toast.LENGTH_LONG).show();
                        break;
                    case 1:

                        sortedList=Utils.sortStringBubble(filteredPlants, false);
                        plantRecyclerAdapter=new PlantRecyclerAdapter(sortedList);
                        recyclerView.setAdapter(plantRecyclerAdapter);
                        Toast.makeText(MainActivity.this, "Date selected", Toast.LENGTH_LONG).show();
                        break;
                     case 2:
                        sortedList=Utils.sortByOutDoorPlant(filteredPlants,true);
                        plantRecyclerAdapter=new PlantRecyclerAdapter(sortedList);
                        recyclerView.setAdapter(plantRecyclerAdapter);
                        Toast.makeText(MainActivity.this, "Outdoor plants selected", Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        sortedList=Utils.sortByOutDoorPlant(filteredPlants,false);
                        plantRecyclerAdapter=new PlantRecyclerAdapter(sortedList);
                        recyclerView.setAdapter(plantRecyclerAdapter);
                        Toast.makeText(MainActivity.this, "Indoor plants selected", Toast.LENGTH_LONG).show();
                        break;
                        default:
                            break;
                }
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(MainActivity.this, DashBoard.class);
        startActivity(intent);
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
