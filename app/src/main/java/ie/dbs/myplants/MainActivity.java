package ie.dbs.myplants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    private Button addPlantButton;
    private ArrayList<Plant> myPlants;
    private DatabaseReference databaseReference;
    private SearchView searchView;
    private AlertDialog alertDialog1;
    private ArrayList<Plant> filteredPlants;

    RecyclerView recyclerView;
    CharSequence[] values = {"Name", "Date Added", "Outdoor Plants", "Indoor Plants"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.hasFixedSize();
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);

        searchView=findViewById(R.id.searchBar);
        addPlantButton = findViewById(R.id.addPlant);
        addPlantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddPlant.class);
                startActivity(intent);
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

    public void displayPlants(){
        if(databaseReference!=null){
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        myPlants=new ArrayList<>();
                        for(DataSnapshot plantSnapshot:dataSnapshot.getChildren()){
                            Plant myPlant=plantSnapshot.getValue(Plant.class);
                            myPlants.add(myPlant);

                        }
                        PlantRecyclerAdapter plantRecyclerAdapter=new PlantRecyclerAdapter(myPlants);
                        recyclerView.setAdapter(plantRecyclerAdapter);
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
                    else filteredPlants=myPlants;

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout: {
                final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
                dialog.setTitle("Logout");
                dialog.setMessage("Are you sure?");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.mAuth.signOut();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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
                break;
            }
            case R.id.sortBy:{
                CreateAlertDialogWithRadioButtons();
            }

            default:
                break;
        }
        return true;
    }


    protected void CreateAlertDialogWithRadioButtons() {
        if(searchView==null)
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


    private void addPlant(Plant plant) {
        String userID=Utils.user.getUid();
        Utils.databaseReference.child("users").child(userID).child("plants").child(plant.getPlantID()).setValue(plant);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(MainActivity.this, DashBoard.class);
        startActivity(intent);
    }
}
