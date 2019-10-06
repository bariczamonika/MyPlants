package ie.dbs.myplants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {
    private Button addPlantButton;
    protected AlertDialog alertDialog;
    private ArrayList<Object> plants = new ArrayList<>();
    FirebaseRecyclerOptions<PlantAndProfilePic> options;
    FirebaseRecyclerAdapter<PlantAndProfilePic, PlantRecyclerAdapter> adapter;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
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
        final DatabaseReference plantListRef = Utils.databaseReference.child("users").child(userID).child("plantsAndPics");
        plantListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                displayPlants();
                for (DataSnapshot plantSnapshot : dataSnapshot.getChildren()) {
                    PlantAndProfilePic myPlant = plantSnapshot.getValue(PlantAndProfilePic.class);
                    plants.add(myPlant);
                }

                Log.v("myplantsindb", plants.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.v("Error retrieving plant", "loadPost:onCancelled", databaseError.toException());
            }
        });

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
        final DatabaseReference plantListRef = Utils.databaseReference.child("users").child(userID).child("plantsAndPics");
        options = new FirebaseRecyclerOptions.Builder<PlantAndProfilePic>()
                .setQuery(plantListRef, PlantAndProfilePic.class).build();
        adapter = new FirebaseRecyclerAdapter<PlantAndProfilePic, PlantRecyclerAdapter>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull PlantRecyclerAdapter holder, int position, @NonNull PlantAndProfilePic model) {
                        holder.txt_name.setText(model.getPlantName());
                        String myPath = model.getPath();
                        Utils.getImageFromFile(myPath, holder.img_avatar);
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
}
