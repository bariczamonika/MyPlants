package ie.dbs.myplants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//TODO filters and search bar
public class MainActivity extends AppCompatActivity{
    private Button addPlantButton;
    private ArrayList<Object> plants = new ArrayList<>();
    private List<Plant>myNewPlants=new ArrayList<>();
    private List<PlantNotifications> allNotifications=new ArrayList<>();
    private final List<Plant> fullPlantList=new ArrayList<>();
    private ArrayList<Plant> myPlants;
    private EditText searchBar;
    private DatabaseReference databaseReference;
    private SearchView searchView;
    RecyclerView recyclerView;
    String search;


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
        if(databaseReference!=null){
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        myPlants=new ArrayList<>();
                        for(DataSnapshot plantSnapshot:dataSnapshot.getChildren()){
                            myPlants.add(plantSnapshot.getValue(Plant.class));
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
                                search(newText);
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

    private void search(String str){
        ArrayList<Plant> list=new ArrayList<>();
        for(Plant plant:myPlants){
            if(plant.getName().toLowerCase().trim().contains(str.toLowerCase())){
                list.add(plant);
            }
        }
        PlantRecyclerAdapter plantRecyclerAdapter=new PlantRecyclerAdapter(list);
        recyclerView.setAdapter(plantRecyclerAdapter);
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


   /* private void displayPlants()
    {

        String userID = Utils.user.getUid();
        final DatabaseReference plantListRef = Utils.databaseReference.child("users").child(userID).child("plants");
        Query query =plantListRef.orderByChild("plants").equalTo(search);
        options = new FirebaseRecyclerOptions.Builder<Plant>()
                .setQuery(query, Plant.class).build();
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
*/
    private void addPlant(Plant plant) {
        String userID=Utils.user.getUid();
        Utils.databaseReference.child("users").child(userID).child("plants").child(plant.getPlantID()).setValue(plant);
    }


}
