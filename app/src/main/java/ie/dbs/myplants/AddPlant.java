package ie.dbs.myplants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddPlant extends AppCompatActivity {

    private EditText edit_text_plant_name;
    private EditText edit_text_plant_description;
    private EditText edit_text_plant_notes;
    private Button btn_add_pic_to_plant;
    private Button btn_plant_submit;
    private Spinner spinner_watering_needs;
    private Spinner spinner_fertilizing_needs;
    private Spinner spinner_light_conditions;
    private RadioGroup radioGroup_outdoor_plant;
    private RadioButton radioButton_outdoor_plant_yes;
    private RadioButton radioButton_outdoor_plant_no;
    private String plant_name;
    private String plant_description;
    private String plant_notes;
    private Watering_Needs plant_watering_needs;
    private Fertilizing_Needs plant_fertilizing_needs;
    private Light_Condition plant_light_conditions;
    private boolean plant_outdoor_plant;
    private ImageView img_view_plant_profile_preview;
    private String[] imgProfilePath;
    private TextView plant_title;
    private boolean modify=false;
    private TextView change_date_added;
    private Date dateAdded;
    private SimpleDateFormat simpleDateFormat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant2);
        edit_text_plant_name=(EditText)findViewById(R.id.edit_text_plant_name);
        edit_text_plant_description=(EditText)findViewById(R.id.edit_text_plant_description);
        edit_text_plant_notes=(EditText)findViewById(R.id.edit_text_plant_notes);
        btn_add_pic_to_plant=(Button)findViewById(R.id.btn_add_profile_pic);
        btn_plant_submit=(Button)findViewById(R.id.btn_plant_submit);
        spinner_watering_needs=(Spinner)findViewById(R.id.spinner_watering_needs);
        spinner_fertilizing_needs=(Spinner)findViewById(R.id.spinner_fertilizing_needs);
        spinner_light_conditions=(Spinner)findViewById(R.id.spinner_light_conditions);
        radioGroup_outdoor_plant=(RadioGroup)findViewById(R.id.radio_group_outdoor_plant);
        radioButton_outdoor_plant_yes=(RadioButton)findViewById(R.id.radio_button_yes);
        radioButton_outdoor_plant_no=(RadioButton)findViewById(R.id.radio_button_no);
        img_view_plant_profile_preview=(ImageView)findViewById(R.id.img_view_plant_profile_preview);
        plant_title=findViewById(R.id.plant_title);
        change_date_added=findViewById(R.id.change_date_added);
        simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
        change_date_added.setText(String.valueOf(simpleDateFormat.format(new Date())));
        setSpinners();

        change_date_added.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDate();
            }
        });

        btn_add_pic_to_plant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.temporary_plant=savePlantDetails();
                Intent intent=new Intent(AddPlant.this, AddPictureToPlant.class);
                intent.putExtra("IsProfilePicture",true);
                intent.putExtra("plantID", Utils.temporary_plant.getPlantID());
                if(modify)
                intent.putExtra("modify", true);
                else
                    intent.putExtra("modify", false);
                startActivity(intent);
            }
        });

        btn_plant_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(!modify) {
                    if(Utils.plantIterator==null)
                        Utils.plantIterator=0;
                    Plant myPlant=savePlantDetails();
                    addPlant(myPlant);

                    String[] stringArray=getIntent().getStringArrayExtra("image");
                    if(getIntent().getStringArrayExtra("image")!=null)
                    {
                        Utils.PushPicToDB(Utils.plantIterator.toString(), stringArray[0]);
                    }
                    Utils.plantIterator++;
                    Toast.makeText(AddPlant.this, "Plant saved successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddPlant.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    Utils.temporary_plant = null;

                }
                else
                {
                    String[] stringArray=getIntent().getStringArrayExtra("image");
                    if(getIntent().getStringArrayExtra("image")!=null)
                    {
                        Utils.PushPicToDB(Utils.temporary_plant.getPlantID(), stringArray[0]);
                    }
                    Toast.makeText(AddPlant.this, "Plant modified successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddPlant.this, SinglePlant.class);
                    Plant myPlant=savePlantDetails();
                    addPlant(myPlant);
                    intent.putExtra("plantID", Utils.temporary_plant.getPlantID());
                    startActivity(intent);
                    finish();
                    Utils.temporary_plant = null;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        modify=getIntent().getBooleanExtra("modify", false);
        if(!modify) {
            try {
                imgProfilePath = getIntent().getStringArrayExtra("image");
                img_view_plant_profile_preview.setImageBitmap(Utils.getImageFromFile(imgProfilePath[0]));
                setSpinners();

                if (Utils.temporary_plant != null) {
                    edit_text_plant_name.setText(Utils.temporary_plant.getName());
                    edit_text_plant_notes.setText(Utils.temporary_plant.getNotes());
                    edit_text_plant_description.setText(Utils.temporary_plant.getDescription());
                    spinner_fertilizing_needs.setSelection(Utils.temporary_plant.getFertilizingNeeds().value);
                    spinner_watering_needs.setSelection(Utils.temporary_plant.getWateringNeeds().value);
                    spinner_light_conditions.setSelection(Utils.temporary_plant.getLightCondition().value);
                    change_date_added.setText(simpleDateFormat.format(Utils.temporary_plant.getDateAdded()));
                    if (Utils.temporary_plant.isOutdoorPlant())
                        radioButton_outdoor_plant_yes.setChecked(true);
                    else if (!Utils.temporary_plant.isOutdoorPlant())
                        radioButton_outdoor_plant_no.setChecked(true);
                    else
                        radioGroup_outdoor_plant.clearCheck();
                }
            } catch (Exception ex) {
                Log.v("Error adding plant", ex.getMessage());
            }
        }
        else
        {
            try {
                        if (Utils.temporary_plant != null) {
                        edit_text_plant_name.setText(Utils.temporary_plant.getName());
                        edit_text_plant_notes.setText(Utils.temporary_plant.getNotes());
                        edit_text_plant_description.setText(Utils.temporary_plant.getDescription());
                        spinner_fertilizing_needs.setSelection(Utils.temporary_plant.getFertilizingNeeds().value);
                        spinner_watering_needs.setSelection(Utils.temporary_plant.getWateringNeeds().value);
                        spinner_light_conditions.setSelection(Utils.temporary_plant.getLightCondition().value);
                            change_date_added.setText(simpleDateFormat.format(Utils.temporary_plant.getDateAdded()));
                        if (Utils.temporary_plant.isOutdoorPlant())
                            radioButton_outdoor_plant_yes.setChecked(true);
                        else if (!Utils.temporary_plant.isOutdoorPlant())
                            radioButton_outdoor_plant_no.setChecked(true);
                        else
                            radioGroup_outdoor_plant.clearCheck();
                            imgProfilePath = getIntent().getStringArrayExtra("image");
                            if(imgProfilePath!=null)
                            img_view_plant_profile_preview.setImageBitmap(Utils.getImageFromFile(imgProfilePath[0]));
                            else if(Utils.temporary_plant.getProfilePicPath()!=null)
                                img_view_plant_profile_preview.setImageBitmap(Utils.getImageFromFile(Utils.temporary_plant.getProfilePicPath()));
                            plant_title.setText(R.string.plant_modify_plant);
                            btn_add_pic_to_plant.setText(R.string.plant_modify_profile_pic);

                }
            }catch (Exception ex) {
                Log.v("Error adding plant", ex.getMessage());}
        }
    }
    private void setSpinners()
    {
        ArrayAdapter<CharSequence> adapter=new ArrayAdapter<CharSequence>(this,
                R.layout.support_simple_spinner_dropdown_item, Utils.getWateringNeedsNames());
        spinner_watering_needs.setAdapter(adapter);
        ArrayAdapter<CharSequence> adapter2=new ArrayAdapter<CharSequence>(this,
                R.layout.support_simple_spinner_dropdown_item, Utils.getFertilizingNeedsNames());
        spinner_fertilizing_needs.setAdapter(adapter2);
        ArrayAdapter<CharSequence> adapter1=new ArrayAdapter<CharSequence>(this,
                R.layout.support_simple_spinner_dropdown_item, Utils.getLightConditionNames());
        spinner_light_conditions.setAdapter(adapter1);
    }

    private Plant savePlantDetails()
    {
        if(Utils.plantIterator==null)
            Utils.plantIterator=0;
        plant_name=edit_text_plant_name.getText().toString();
        plant_description=edit_text_plant_description.getText().toString();
        plant_notes=edit_text_plant_notes.getText().toString();
        try {
            dateAdded = simpleDateFormat.parse(change_date_added.getText().toString());
        }catch (ParseException ex)
        {ex.printStackTrace();}
        plant_fertilizing_needs= Fertilizing_Needs.valueOf(spinner_fertilizing_needs.getSelectedItem().toString().replace(" ","_"));
        plant_watering_needs=Watering_Needs.valueOf(spinner_watering_needs.getSelectedItem().toString().replace(" ","_"));
        plant_light_conditions=Light_Condition.valueOf(spinner_light_conditions.getSelectedItem().toString().replace(" ","_"));
        if(radioButton_outdoor_plant_yes.isChecked())
            plant_outdoor_plant=true;
        else if(radioButton_outdoor_plant_no.isChecked())
            plant_outdoor_plant=false;
        Plant myPlant;
        if(!modify)
        {
        if (imgProfilePath==null)
        myPlant=new Plant(Utils.plantIterator.toString(),plant_name,plant_description,dateAdded,plant_notes,plant_watering_needs,
                plant_fertilizing_needs,plant_outdoor_plant,plant_light_conditions, "");
        else
            myPlant=new Plant(Utils.plantIterator.toString(),plant_name,plant_description,dateAdded,plant_notes,plant_watering_needs,
                    plant_fertilizing_needs,plant_outdoor_plant,plant_light_conditions, imgProfilePath[0]);}
        else
        {
            String plantID=getIntent().getStringExtra("plantID");
            Log.v("plantID", plantID);
            String userID=Utils.user.getUid();
            DatabaseReference databaseReference=Utils.databaseReference.child("users").child(userID).child("plants").child(plantID);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot plantSnapshot : dataSnapshot.getChildren()) {
                       Utils.temporary_plant=dataSnapshot.getValue(Plant.class);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            myPlant=Utils.temporary_plant;
            myPlant.setDescription(plant_description);
            myPlant.setFertilizingNeeds(plant_fertilizing_needs);
            myPlant.setLightCondition(plant_light_conditions);
            myPlant.setName(plant_name);
            myPlant.setNotes(plant_notes);
            myPlant.setOutdoorPlant(plant_outdoor_plant);
            myPlant.setWateringNeeds(plant_watering_needs);
            if(imgProfilePath!=null)
                myPlant.setProfilePicPath(imgProfilePath[0]);
        }
        Log.v("saved temporary plant", myPlant.getFertilizingNeeds().toString());
        return myPlant;
    }


    private void addPlant(Plant myPlant) {
        if(imgProfilePath==null)
            imgProfilePath=new String[]{""};
        String userID=Utils.user.getUid();
        Utils.databaseReference.child("users").child(userID).child("plants").child(myPlant.getPlantID()).setValue(myPlant);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(imgProfilePath!=null)
        Utils.deletePic(imgProfilePath[0]);
        Intent intent=new Intent(AddPlant.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void chooseDate() {
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("dd/MM/yy");
        final DatePickerDialog datePicker =
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(final DatePicker view, final int year, final int month,
                                          final int dayOfMonth) {


                        calendar.set(year, month, dayOfMonth);
                        Date myDate = calendar.getTime();
                        String dateString = simpleDateFormat1.format(myDate);
                        change_date_added.setText(dateString);
                    }
                }, year, month, day); // set date picker to current date
        datePicker.getDatePicker().setMaxDate(new Date().getTime());
        datePicker.show();

        datePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
                Date now=new Date();
                change_date_added.setText(simpleDateFormat1.format(now));
                dialog.dismiss();
            }
        });
    }
}
