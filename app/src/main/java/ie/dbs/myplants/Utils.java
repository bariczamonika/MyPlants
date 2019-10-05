package ie.dbs.myplants;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.telephony.emergency.EmergencyNumber;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Utils {
    public static FirebaseUser user;
    public static FirebaseAuth mAuth;
    public static int MyVersion;
    public static Context applicationContext;
    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    public static Plant temporary_plant;
    public static Integer plantIterator;

    //asks for permission on SDK>16
    public static void AskForPermission(String myPermission, Activity whichActivity)
    {

        if(Utils.MyVersion> Build.VERSION_CODES.LOLLIPOP_MR1){
            if(ContextCompat.checkSelfPermission(applicationContext, myPermission)
                    != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(whichActivity, new String []{myPermission},1);
            }
        }
    }

    //create custom directory and save file
    public static String createDirectoryAndSaveFile(Bitmap imageToSave, Context context) {

        File homeDirectory=new File(applicationContext.getExternalFilesDir(null) + "/MyPlants");
        if (!homeDirectory.exists()) {
            File plantDirectory = new File(applicationContext.getExternalFilesDir(null) + "/MyPlants/");
            plantDirectory.mkdirs();
        }
        File myFile=new File(applicationContext.getExternalFilesDir(null) + "/MyPlants/");

        String timestamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(myFile, "picture"+
                timestamp+".jpeg");
        String imagePath=file.getAbsolutePath();
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //TODO check mediascanner it's not showing in gallery
            MediaScannerConnection.scanFile(context,
                    new String[] { file.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.v("ExternalStorage", "Scanned " + path + ":");
                            Log.v("ExternalStorage", "-> uri=" + uri);
                        }
                    });
            Toast.makeText(Utils.applicationContext, "Image saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;
    }

    //validate login form
    public static boolean validateForm(EditText emailAddress, EditText password) {
        boolean valid = true;

        String email = emailAddress.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailAddress.setError("Required.");
            valid = false;
        } else {
            emailAddress.setError(null);
        }

        String myPassword = password.getText().toString();
        if (TextUtils.isEmpty(myPassword)) {
            password.setError("Required.");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }

    //validate registration form
    public static boolean validateRegistrationForm(EditText emailAddress, EditText password, EditText
                                                   passwordConfirmation) {
        boolean valid = true;

        String email = emailAddress.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailAddress.setError("Required.");
            valid = false;
        } else {
            emailAddress.setError(null);
        }

        String myPassword = password.getText().toString();
        if (TextUtils.isEmpty(myPassword)) {
            password.setError("Required.");
            valid = false;
        } else {
            password.setError(null);
        }

        String myPasswordConfirmation=passwordConfirmation.getText().toString();
        if (TextUtils.isEmpty(myPasswordConfirmation)) {
            passwordConfirmation.setError("Required.");
            valid = false;
        }
        else if (!myPasswordConfirmation.equals(myPassword))
        {
            passwordConfirmation.setError("Passwords don't match");
            valid=false;
        }
            else {
            passwordConfirmation.setError(null);
        }

        return valid;
    }

    //convert spinners to integer values
    public static double convertSpinnerValueToInteger(String spinnerValue)
    {
        String[] stringValues= applicationContext.getResources().getStringArray(R.array.plant_watering_needs_array);
        double value=0;

        for (int i=1;i<16;i++) {
            if(stringValues[i].equals(spinnerValue))
            {
                value=i;
                break;
            }
            else
            {
                value=0.5;
            }
        }

        return value;
    }

    //convert Light condition enum to string array for spinners
    public static String[] getLightConditionNames() {
        return Arrays.toString(Light_Condition.values()).
                replace('[',' ').replace(']',' ').split(", ");
    }

    //check how many plants in db for plantID
    public static void setPlantIterator()
    {
        try {
            String userID=user.getUid();
            DatabaseReference plantListRef = databaseReference.child("users").child(userID);

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    plantIterator = (int) dataSnapshot.getChildrenCount();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.v("database error", databaseError.getMessage());
                }
            };
            plantListRef.addListenerForSingleValueEvent(valueEventListener);

        }catch (NullPointerException ex)
        {
            Log.v("Null reference in DB", ex.getMessage());
            plantIterator=0;
        }
    }

public static void PushPicToDB(String plantID, boolean featured, String imagePath)
{
    String userID=Utils.user.getUid();
    PlantImage plantImage=new PlantImage(imagePath,featured);
    Utils.databaseReference.child("users").child(userID).child("plants").child(plantID).child("images").setValue(plantImage);
}


}
