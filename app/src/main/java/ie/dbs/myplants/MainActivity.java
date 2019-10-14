package ie.dbs.myplants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TODO filters and search bar
public class MainActivity extends AppCompatActivity {
    private Button addPlantButton;
    private ArrayList<Object> plants = new ArrayList<>();
    private List<Plant>myPlants=new ArrayList<>();
    FirebaseRecyclerOptions<Plant> options;
    FirebaseRecyclerAdapter<Plant, PlantRecyclerAdapter> adapter;
    RecyclerView recyclerView;


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


        addPlantButton = findViewById(R.id.addPlant);
        addPlantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddPlant.class);
                startActivity(intent);
            }
        });


        String userID = Utils.user.getUid();
        final DatabaseReference plantListRef = Utils.databaseReference.child("users").child(userID).child("plants");
        plantListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                displayPlants();
                for (DataSnapshot plantSnapshot : dataSnapshot.getChildren()) {
                    Plant myPlant = plantSnapshot.getValue(Plant.class);
                    plants.add(myPlant);
                    //myPlants.add(myPlant);
                }

                Log.v("myplantsindb", plants.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.v("Error retrieving plant", "loadPost:onCancelled", databaseError.toException());
            }
        });

        //TODO transfer this into splash together with database reference it should update the plant next watering
        for (int i=0;i<plants.size();i++){
            myPlants.set(i, Utils.autoChangeDatesOnceItIsReached(myPlants.get(i)));
            addPlant(myPlants.get(i));
        }

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

            default:
                break;
        }
        return true;
    }


    private void displayPlants()
    {
        String userID = Utils.user.getUid();
        final DatabaseReference plantListRef = Utils.databaseReference.child("users").child(userID).child("plants");
        options = new FirebaseRecyclerOptions.Builder<Plant>()
                .setQuery(plantListRef, Plant.class).build();
        adapter = new FirebaseRecyclerAdapter<Plant, PlantRecyclerAdapter>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull PlantRecyclerAdapter holder, int position, @NonNull Plant model) {
                        holder.txt_name.setText(model.getName());
                        String myPath = model.getProfilePicPath();
                        Bitmap bitmap=Utils.getImageFromFile(myPath);
                        if(bitmap.getWidth()>bitmap.getHeight())
                            bitmap=Bitmap.createBitmap(bitmap, 0,0,bitmap.getHeight(), bitmap.getHeight());
                        else
                            bitmap=Bitmap.createBitmap(bitmap, 0,0,bitmap.getWidth(), bitmap.getWidth());
                        holder.img_avatar.setImageBitmap(bitmap);
                        holder.plantID=model.getPlantID();
                    }

                    @NonNull
                    @Override
                    public PlantRecyclerAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View itemView = LayoutInflater.from(getBaseContext()).inflate(R.layout.all_plants, parent, false);
                        return new PlantRecyclerAdapter(itemView);
                    }
                };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter!=null)
            adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayPlants();
        if(adapter!=null)
        adapter.startListening();
    }

    private void addPlant(Plant plant) {
        String userID=Utils.user.getUid();
        Utils.databaseReference.child("users").child(userID).child("plants").child(plant.getPlantID()).setValue(plant);
    }
}
