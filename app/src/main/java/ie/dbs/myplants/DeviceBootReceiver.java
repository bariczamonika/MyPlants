package ie.dbs.myplants;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class DeviceBootReceiver extends BroadcastReceiver {
    final private List<Plant> plants=new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction()!=null) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                String userID = Utils.user.getUid();
                final DatabaseReference plantListRef = Utils.databaseReference.child("users").child(userID).child("plants");
                plantListRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot plantSnapshot : dataSnapshot.getChildren()) {
                            Plant myPlant = plantSnapshot.getValue(Plant.class);
                            plants.add(myPlant);
                        }

                        Log.v("myplantsindb", plants.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.v("Error retrieving plant", "loadPost:onCancelled", databaseError.toException());
                    }
                });
                if (plants.size() != 0) {
                    for (Plant plant : plants) {
                        if (plant.isNotificationFertilizing())
                            Utils.setFertilizingNotification(plant);
                        if (plant.isNotificationWatering())
                            Utils.setWateringNotification(plant);
                    }
                }
            }
        }
    }
}


