package ie.dbs.myplants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ImageDialog extends Activity {
    private ImageView mDialog;
    private TextView date_added;
    private Button delete_pic;
    private ArrayList<String> plantImages=new ArrayList<>();
    private String plantID;
    private int position;
    private Plant myPlant;
    private List<String> keys=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layout);
        date_added=findViewById(R.id.image_date_added);
        delete_pic=findViewById(R.id.delete_pic);
        mDialog = (ImageView)findViewById(R.id.your_image);
        plantID=getIntent().getStringExtra("plantID");
        position=getIntent().getIntExtra("position",0);


        final String userID = Utils.user.getUid();
        final DatabaseReference plantListRef = Utils.databaseReference.child("users").child(userID).child("plants").child(plantID);
        plantListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myPlant=dataSnapshot.getValue(Plant.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference databaseReference=Utils.databaseReference.child("users").child(userID).child("plantsPics").child(plantID).child("images");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                for (final DataSnapshot plantSnapshot : dataSnapshot.getChildren()) {
                    final PlantImage plantImage = plantSnapshot.getValue(PlantImage.class);
                    if (plantImage != null) {
                        plantImages.add(plantImage.getPicturePath());
                        keys.add(plantSnapshot.getKey());
                    }
                }
                plantImages=Utils.sortStringBubbleArrayStringArray(plantImages);
                mDialog.setImageBitmap(Utils.getFullImageFromFile(plantImages.get(position)));
                date_added.setText(Utils.getPictureDateFromPicturePath(plantImages.get(position)));
                delete_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String key = keys.get(position);
                        if (myPlant.getProfilePicPath().equals(plantImages.get(position))) {
                            final AlertDialog alertDialog1 = new AlertDialog.Builder(ImageDialog.this).create();
                            alertDialog1.setTitle("Alert");
                            alertDialog1.setMessage("This is the profile picture. Are you sure you would like to delete it?");
                            alertDialog1.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Utils.databaseReference.child("users").child(userID).child("plantsPics").child(plantID).child("images").child(key).removeValue();
                                    myPlant.setProfilePicPath("");
                                    Utils.addPlant(myPlant);
                                    Utils.deletePic(plantImages.get(position));
                                    Intent intent = new Intent(ImageDialog.this, Gallery.class);
                                    intent.putExtra("plantID", plantID);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alertDialog1.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog1.dismiss();
                                }
                            });
                            alertDialog1.show();
                        } else {
                            final AlertDialog alertDialog = new AlertDialog.Builder(ImageDialog.this).create();
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("Are you sure you would like to delete this picture?");
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Utils.databaseReference.child("users").child(userID).child("plantsPics").child(plantID).child("images").child(key).removeValue();
                                            Utils.deletePic(plantImages.get(position));
                                            Intent intent = new Intent(ImageDialog.this, Gallery.class);
                                            intent.putExtra("plantID", plantID);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();


                        }
                    }
                });
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        mDialog.setClickable(true);
        mDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
