package ie.dbs.myplants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


//TODO timeline view (gallery)
//TODO onDragListener


public class SinglePlant extends AppCompatActivity{
    private Plant myPlant;
    private TextView plant_name;
    private ImageView plant_images;
    private ImageView add_pic;
    private TextView picture_date;
    private ImageView scroll_left;
    private ImageView scroll_right;
    private TextView single_information;
    private TextView single_action;
    private TextView single_task;
    private TextView single_task_plus_minus;
    private TextView single_action_plus_minus;
    private TextView single_information_plus_minus;
    private TextView single_information_plant_name;
    private TextView single_information_outdoor_plant;
    private TextView single_information_light_condition;
    private TextView single_information_watering_needs;
    private TextView single_information_fertilizing_needs;
    private TextView single_information_date_added;
    private TextView single_information_description;
    private TextView single_information_notes;
    private TextView single_action_last_watered;
    private TextView single_action_last_fertilized;
    private TextView single_action_last_replanted;
    private TextView single_task_watered;
    private TextView single_task_fertilized;
    private TextView single_task_replanted;
    private TextView single_pic_count;
    private Button modify_information;
    private Button gallery;
    private List<String> plantImages=new ArrayList<>();
    private HashMap<String, String> plantImagesMap=new HashMap<>();
    private ExpandableRelativeLayout expandableRelativeLayout;
    private ExpandableRelativeLayout expandableRelativeLayout2;
    private ExpandableRelativeLayout expandableRelativeLayout3;

    private Button notificationButton;
    Date myDate=new Date();
    int index=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_plant);
        add_pic=findViewById(R.id.single_add_pic_icon);
        plant_images=findViewById(R.id.single_images);
        picture_date=findViewById(R.id.picture_date);
        scroll_left=findViewById(R.id.single_scroll_left);
        scroll_right=findViewById(R.id.single_scroll_right);
        expandableRelativeLayout=findViewById(R.id.expandableLayout);
        single_information=findViewById(R.id.single_information);
        single_information_plus_minus=findViewById(R.id.single_information_plus_minus);
        single_information_date_added=findViewById(R.id.single_info_value_date_added);
        single_information_fertilizing_needs=findViewById(R.id.single_info_value_fertilizing_needs);
        single_information_light_condition=findViewById(R.id.single_info_value_light_condition);
        single_information_outdoor_plant=findViewById(R.id.single_info_value_outdoor_plant);
        single_information_plant_name=findViewById(R.id.single_info_value_plant_name);
        single_information_watering_needs=findViewById(R.id.single_info_value_watering_needs);
        single_information_description=findViewById(R.id.single_information_value_description);
        single_information_notes=findViewById(R.id.single_information_value_notes);
        single_action_last_watered=findViewById(R.id.single_action_value_watered);
        single_action_last_fertilized=findViewById(R.id.single_action_value_fertilized);
        single_action_last_replanted=findViewById(R.id.single_action_value_replanted);
        single_task_fertilized=findViewById(R.id.single_task_value_fertilized);
        single_task_watered=findViewById(R.id.single_task_value_watered);
        single_pic_count=findViewById(R.id.single_pic_count);
        single_action=findViewById(R.id.single_action);
        single_task=findViewById(R.id.single_tasks);
        single_task_plus_minus=findViewById(R.id.single_tasks_plus_minus_action);
        single_action_plus_minus=findViewById(R.id.single_information_plus_minus_action);
        expandableRelativeLayout2=findViewById(R.id.expandableLayout2);
        expandableRelativeLayout3=findViewById(R.id.expandableLayout3);
        modify_information=findViewById(R.id.modify_information);
        notificationButton=findViewById(R.id.notifications);
        gallery=findViewById(R.id.single_gallery_button);
        expandableRelativeLayout.collapse();
        expandableRelativeLayout2.collapse();
        expandableRelativeLayout3.collapse();



        final String plantID=getIntent().getStringExtra("plantID");
        String userID = Utils.user.getUid();

    /*    DatabaseReference databaseReference=Utils.databaseReference.child("users").child(userID).child("notifications");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot plantSnapshot : dataSnapshot.getChildren()){
                    PlantNotifications PlantNotifications=plantSnapshot.getValue(ie.dbs.myplants.PlantNotifications.class);
                    myNotifications.add(PlantNotifications);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

    gallery.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(SinglePlant.this, Gallery.class);
            intent.putExtra("plantID", plantID);
            startActivity(intent);
        }
    });

        final DatabaseReference plantListRef = Utils.databaseReference.child("users").child(userID).child("plants").child(plantID);
        plantListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot plantSnapshot : dataSnapshot.getChildren()) {
                    myPlant=dataSnapshot.getValue(Plant.class);
                    Log.v("myplantsingleplant", myPlant.getFertilizingNeeds().toString());
                    plant_name=findViewById(R.id.single_plant_name);
                    plant_name.setText(myPlant.getName());
                    single_information_plant_name.setText(myPlant.getName());
                    SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yy");
                    single_information_date_added.setText(sdf.format(myPlant.getDateAdded()));
                    single_information_fertilizing_needs.setText(myPlant.getFertilizingNeeds().name().replace("_"," "));
                    single_information_light_condition.setText(myPlant.getLightCondition().name().replace("_"," "));
                    single_information_watering_needs.setText(myPlant.getWateringNeeds().name().replace("_"," "));
                    if(myPlant.isOutdoorPlant())
                        single_information_outdoor_plant.setText("Yes");
                    else
                        single_information_outdoor_plant.setText("No");
                    single_information_notes.setText(myPlant.getNotes());
                    single_information_description.setText(myPlant.getDescription());
                    if(myPlant.getLastWatered()!=null)
                        single_action_last_watered.setText(sdf.format(myPlant.getLastWatered()));
                    else
                        single_action_last_watered.setText("N/A");
                    if(myPlant.getLastFertilized()!=null)
                        single_action_last_fertilized.setText(sdf.format(myPlant.getLastFertilized()));
                    else
                        single_action_last_fertilized.setText("N/A");
                    if(myPlant.getLastReplanted()!=null)
                        single_action_last_replanted.setText(sdf.format(myPlant.getLastReplanted()));
                    else
                        single_action_last_replanted.setText("N/A");
                    if(myPlant.getNextFertilizing()!=null)
                        single_task_fertilized.setText(sdf.format(myPlant.getNextFertilizing()));
                    else
                        single_task_fertilized.setText("N/A");
                    if(myPlant.getNextWatering()!=null)
                        single_task_watered.setText(sdf.format(myPlant.getNextWatering()));
                    else
                        single_task_watered.setText("N/A");
                    Log.v("myplantsingleplant", myPlant.getName());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.v("Error retrieving plant", "loadPost:onCancelled", databaseError.toException());
            }
        });


        final DatabaseReference plantImagesRef = Utils.databaseReference.child("users").child(userID).child("plantsPics").child(plantID).child("images");
        plantImagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                plantImages.clear();
                for (DataSnapshot plantSnapshot : dataSnapshot.getChildren()) {

                    PlantImage plantImage=plantSnapshot.getValue(PlantImage.class);
                    plantImages.add(plantImage.getPicturePath());
                    String picName=Utils.getPictureDateFromPicturePath(plantImage.getPicturePath());
                    plantImagesMap.put(plantImage.getPicturePath(),picName);
                    picture_date.setText(plantImagesMap.get(plantImages.get(0)));
                    plant_images.setImageBitmap(Utils.getImageFromFile(plantImages.get(0)));
                    index=0;
                    single_pic_count.setText(index+1 + "/" +plantImages.size());
                    Log.v("plantImagePaths", plantImages.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.v("Error retrieving plant", "loadPost:onCancelled", databaseError.toException());
            }
        });

        myPlant=Utils.autoChangeDatesOnceItIsReached(myPlant);


        plant_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (plantImages.size()!=0)
                {
                    int size=plantImages.size();
                    index=scrollRight(size);
                    plant_images.setImageBitmap(Utils.getImageFromFile(plantImages.get(index)));
                    picture_date.setText(plantImagesMap.get(plantImages.get(index)));
                    single_pic_count.setText(index+1 + "/" +size);

                }
            }
        });

        scroll_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (plantImages.size()!=0)
                {
                    int size=plantImages.size();
                    index=scrollLeft(size);
                    plant_images.setImageBitmap(Utils.getImageFromFile(plantImages.get(index)));
                    picture_date.setText(plantImagesMap.get(plantImages.get(index)));
                    single_pic_count.setText(index+1 + "/" +size);
                }
            }
        });

        scroll_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (plantImages.size()!=0)
                {
                    int size=plantImages.size();
                    index=scrollRight(size);
                    plant_images.setImageBitmap(Utils.getImageFromFile(plantImages.get(index)));
                    picture_date.setText(plantImagesMap.get(plantImages.get(index)));
                    single_pic_count.setText(index+1 + "/" +size);
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

        single_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!expandableRelativeLayout.isExpanded()){
                    expandableRelativeLayout.expand();
                    single_information_plus_minus.setText("-");
                }

                else{
                    expandableRelativeLayout.collapse();
                    single_information_plus_minus.setText("+");
                }
            }
        });

        single_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!expandableRelativeLayout2.isExpanded()){
                    expandableRelativeLayout2.expand();
                    single_action_plus_minus.setText("-");
                }
                else{
                    expandableRelativeLayout2.collapse();
                    single_action_plus_minus.setText("+");
                }
            }
        });

        single_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!expandableRelativeLayout3.isExpanded()){
                    expandableRelativeLayout3.expand();
                    single_task_plus_minus.setText("-");
                }
                else{
                    expandableRelativeLayout3.collapse();
                    single_task_plus_minus.setText("+");
                }
            }
        });

        single_action_last_watered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDate(single_action_last_watered,0);


            }
        });

        single_action_last_fertilized.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDate(single_action_last_fertilized,1);
            }
        });

        single_action_last_replanted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDate(single_action_last_replanted,2);
            }
        });

        modify_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SinglePlant.this, AddPlant.class);
                intent.putExtra("plantID", myPlant.getPlantID());
                intent.putExtra("modify", true);
                Utils.temporary_plant=myPlant;
                startActivity(intent);
            }
        });

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createAlertDialogWithRadioButtons();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(SinglePlant.this, MainActivity.class);
        startActivity(intent);
        finish();
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

private int scrollRight(int size)
    {
        if(size>index+1)
            index=index+1;
        else if(size==index+1)
            index=0;
        return index;
    }
     private int scrollLeft(int size)
     {

         if(index>0)
             index=index-1;
         else if(index==0)
             index=size-1;
         return index;
     }

     //TODO set reminders
    private void chooseDate(final TextView textView, final int whatToUseItFor) {
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        if(((whatToUseItFor==0)&&(myPlant.getWateringNeeds()!=Watering_Needs.None)) ||
                ((whatToUseItFor==1)&&(myPlant.getFertilizingNeeds()!=Fertilizing_Needs.None))) {
            final DatePickerDialog datePicker =
                    new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(final DatePicker view, final int year, final int month,
                                              final int dayOfMonth) {

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
                            calendar.set(year, month, dayOfMonth,23,59,59);
                            myDate = calendar.getTime();
                            String dateString = simpleDateFormat.format(myDate);
                            textView.setText(dateString);
                            if (whatToUseItFor == 0) {
                                myPlant.setLastWatered(myDate);
                                Date newDate = new Date();
                                int days = myPlant.getWateringNeeds().value;
                                if ((myPlant.getLastWatered() != null) && (myPlant.getWateringNeeds() != null)) {
                                    newDate = Utils.addDaysToDate(myDate, days);
                                    myPlant.setNextWatering(newDate);
                                }
                                if(myPlant.isNotificationWatering())
                                Utils.setWateringNotification(myPlant);

                            } else if (whatToUseItFor == 1) {
                                myPlant.setLastFertilized(myDate);
                                Date newDate = new Date();
                                int days = Utils.convertFertilizingNeedsToInteger(myPlant.getFertilizingNeeds().value);
                                if ((myPlant.getLastFertilized() != null) && (myPlant.getFertilizingNeeds() != null)) {
                                    newDate = Utils.addDaysToDate(myDate, days);
                                    myPlant.setNextFertilizing(newDate);
                                }
                                if(myPlant.isNotificationFertilizing())
                                Utils.setFertilizingNotification(myPlant);

                            } else {
                                myPlant.setLastReplanted(myDate);
                            }
                            myPlant=Utils.autoChangeDatesOnceItIsReached(myPlant);
                            addPlant(myPlant);


                        }
                    }, year, month, day); // set date picker to current date
            datePicker.getDatePicker().setMaxDate(new Date().getTime());
            datePicker.show();

            datePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(final DialogInterface dialog) {
                    dialog.dismiss();
                }
            });
        }
        else
        {
            if(whatToUseItFor==0)
            {
                AlertDialog alertDialog = new AlertDialog.Builder(SinglePlant.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Please set watering needs first");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            else if(whatToUseItFor==1)
            {
                AlertDialog alertDialog = new AlertDialog.Builder(SinglePlant.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Please set fertilizing needs first");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        }
    }

    private void addPlant(Plant plant) {
        String userID=Utils.user.getUid();
        Utils.databaseReference.child("users").child(userID).child("plants").child(myPlant.getPlantID()).setValue(plant);
    }




    //TODO have to check for if both checked

    protected void createAlertDialogWithRadioButtons() {
        final String[]optionsArray=new String[]{
                "Watering",
                "Fertilizing"
        };
        final boolean[]checkedOptions=new boolean[]{
                myPlant.isNotificationWatering(),
                myPlant.isNotificationFertilizing()
        };

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Notifications");
        builder.setMultiChoiceItems(optionsArray, checkedOptions, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                checkedOptions[which]=isChecked;
            }
        });
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for(int i=0;i<checkedOptions.length;i++){
                    boolean checked=checkedOptions[i];
                    if(checked) {
                        if ((i == 0)&&(!myPlant.isNotificationWatering())) {
                            myPlant.setNotificationWatering(true);
                            Utils.setWateringNotification(myPlant);
                        }
                        else if ((i==1)&&(!myPlant.isNotificationFertilizing())){
                            myPlant.setNotificationFertilizing(true);
                            Utils.setFertilizingNotification(myPlant);
                        }
                    }
                    else
                    {
                        if((i==0)&&(myPlant.isNotificationWatering())) {
                            myPlant.setNotificationWatering(false);
                            Utils.isCalledFromAlertDialog=1;
                            int notificationID=Utils.generateNotificationID(myPlant.getPlantID(), true);
                            Utils.cancelNotification(notificationID,SinglePlant.this);
                            //Utils.cancelNotifications(true, SinglePlant.this, myPlant);
                            //TODO cancel upcoming notifications
                        }
                        else if((i==1)&&(myPlant.isNotificationFertilizing())){
                            myPlant.setNotificationFertilizing(false);
                            Utils.isCalledFromAlertDialog=2;
                            int notificationID=Utils.generateNotificationID(myPlant.getPlantID(), false);
                            Utils.cancelNotification(notificationID, SinglePlant.this);
                            //Utils.cancelNotifications(false, SinglePlant.this, myPlant);

                        }
                    }
                }
                addPlant(myPlant);
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
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
                final AlertDialog dialog = new AlertDialog.Builder(SinglePlant.this).create();
                dialog.setTitle("Logout");
                dialog.setMessage("Are you sure?");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.mAuth.signOut();
                        Intent intent = new Intent(SinglePlant.this, LoginActivity.class);
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








}
