package ie.dbs.myplants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SinglePlant extends AppCompatActivity {
    private Plant myPlant;
    private TextView plant_name;
    private ImageView plant_images;
    private Button add_pic;
    private TextView picture_date;
    private List<String> plantImages=new ArrayList<>();
    private Map<String, String> plantImagesMap;
    int index=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_plant);
        add_pic=findViewById(R.id.single_add_pic);
        plant_images=findViewById(R.id.single_images);
        picture_date=findViewById(R.id.picture_date);
        final String plantID=getIntent().getStringExtra("plantID");
        String userID = Utils.user.getUid();


        final DatabaseReference plantListRef = Utils.databaseReference.child("users").child(userID).child("plants").child(plantID);
        plantListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot plantSnapshot : dataSnapshot.getChildren()) {
                    myPlant=dataSnapshot.getValue(Plant.class);
                    plant_name=findViewById(R.id.single_plant_name);
                    plant_name.setText(myPlant.getName());
                    Log.v("myplantsingleplant", myPlant.getName());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.v("Error retrieving plant", "loadPost:onCancelled", databaseError.toException());
            }
        });


        //TODO something is wrong some pics are duplicates maybe the stringarray the intent sends back?
        final DatabaseReference plantImagesRef = Utils.databaseReference.child("users").child(userID).child("plants").child(plantID).child("images");
        plantImagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                plantImages.clear();
                for (DataSnapshot plantSnapshot : dataSnapshot.getChildren()) {

                    PlantImage plantImage=plantSnapshot.getValue(PlantImage.class);
                    plantImages.add(plantImage.getPicturePath());
                    String picName=Utils.getPictureDateFromPicturePath(plantImage.getPicturePath());
                    picture_date.setText(picName);
                   // Log.v("plantImageID", plantImageID);
                  /*  plantImage=dataSnapshot.getValue(PlantImage.class);
                    Log.v("plantImage", plantImage.getPicturePath());*/

                    Utils.getImageFromFile(plantImages.get(0),plant_images);
                    index=0;
                    Log.v("plantImagePaths", plantImages.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.v("Error retrieving plant", "loadPost:onCancelled", databaseError.toException());
            }
        });

        plant_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (plantImages!=null)
                {
                    int size=plantImages.size();
                    if(size>index+1)
                        index=index+1;
                    else if(size==index+1)
                        index=0;
                    Utils.getImageFromFile(plantImages.get(index), plant_images);
                }
            }
        });

        add_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SinglePlant.this, AddPictureToPlant.class);
                intent.putExtra("IsProfilePicture", false);
                intent.putExtra("plantID", plantID);
                startActivity(intent);
            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
        String plantID=getIntent().getStringExtra("plantID");
        String[] stringArray=getIntent().getStringArrayExtra("image");
        if(stringArray!=null)
        {
            for (int i=0;i<stringArray.length;i++)
            Utils.PushPicToDB(plantID, stringArray[i]);
        }
    }



}
