package ie.dbs.myplants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Gallery extends AppCompatActivity {
private String plantID;
private TextView gallery_plant_name;
private RecyclerView gallery_recycler_view;
private ArrayList<PlantImage> plantImages=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        plantID=getIntent().getStringExtra("plantID");
        gallery_plant_name=findViewById(R.id.gallery_plant_name);

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
                if(plantImages!=null) {
                    GalleryAdapter galleryAdapter = new GalleryAdapter(plantImages);
                    gallery_recycler_view.setAdapter(galleryAdapter);
                }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }
}
