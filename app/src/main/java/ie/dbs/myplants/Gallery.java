package ie.dbs.myplants;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class Gallery extends Activity {
private String plantID;
private TextView gallery_plant_name;
private RecyclerView gallery_recycler_view;
private ArrayList<PlantImage> plantImages=new ArrayList<>();
private ConnectionReceiver receiver;
private TextView empty_gallery;
private String plant_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        empty_gallery=findViewById(R.id.empty_gallery);
        plantID=getIntent().getStringExtra("plantID");
        gallery_plant_name=findViewById(R.id.gallery_plant_name);
        receiver=new ConnectionReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, filter);
        plant_name=getIntent().getStringExtra("plantName");

        gallery_recycler_view=findViewById(R.id.gallery_recycler_view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        gallery_recycler_view.setLayoutManager(layoutManager);
        gallery_recycler_view.hasFixedSize();
        gallery_recycler_view.setItemViewCacheSize(20);
        gallery_recycler_view.setDrawingCacheEnabled(true);
        gallery_recycler_view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);

        String userID = Utils.user.getUid();
        final DatabaseReference plantImagesRef = Utils.databaseReference.child("users").child(userID).child("plantsPics").child(plantID).child("images");
        plantImagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot plantSnapshot : dataSnapshot.getChildren()) {
                    PlantImage plantImage=plantSnapshot.getValue(PlantImage.class);
                    plantImages.add(plantImage);
                }
                if(plantImages.size()!=0) {
                    gallery_plant_name.setText(plant_name);
                    plantImages=Utils.sortStringBubbleArray(plantImages);
                    GalleryAdapter galleryAdapter = new GalleryAdapter(plantImages, plantID);
                    gallery_recycler_view.setAdapter(galleryAdapter);
                    empty_gallery.setVisibility(View.INVISIBLE);
                }
                else
                {
                    empty_gallery.setVisibility(View.VISIBLE);
                }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(Gallery.this, SinglePlant.class);
        intent.putExtra("plantID", plantID);
        startActivity(intent);
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
