package ie.dbs.myplants;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


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
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 70, out);
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
    public static boolean validateForm(@NotNull EditText emailAddress, EditText password) {
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
    public static boolean validateRegistrationForm(@NotNull EditText emailAddress, EditText password, EditText
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

    //convert Light condition enum to string array for spinners
    @NotNull
    public static String[] getLightConditionNames() {
        String myArray=Arrays.toString(Light_Condition.values());
        myArray=myArray.substring(1, myArray.length()-1);
        return myArray.replace('_', ' ').split(", ");
    }

    @NotNull
    public static String[] getWateringNeedsNames() {
        String myArray=Arrays.toString(Watering_Needs.values());
        myArray=myArray.substring(1, myArray.length()-1);
        return myArray.replace('_', ' ').split(", ");
    }

    @NotNull
    public static String[] getFertilizingNeedsNames() {
        String myArray=Arrays.toString(Fertilizing_Needs.values());
        myArray=myArray.substring(1, myArray.length()-1);
        return myArray.replace('_', ' ').split(", ");
    }


    //push pictures to database
public static void PushPicToDB(String plantID, String imagePath)
{
    String userID=Utils.user.getUid();
    PlantImage plantImage=new PlantImage(imagePath);
    Utils.databaseReference.child("users").child(userID).child("plantsPics").child(plantID).child("images").push().setValue(plantImage);
}

//retrieve image from saved position
public static Bitmap getImageFromFile(String path)
{
    Drawable drawable=applicationContext.getResources().getDrawable(R.drawable.replacement_pic);
    Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
    bitmap=Bitmap.createScaledBitmap(bitmap, 400,280,false);

    if(path!=null) {
        if (!path.equals("")) {
            final File imgFile = new File(path);
            if (imgFile.exists()) {
                         bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        if (bitmap.getHeight() > bitmap.getWidth())
                            bitmap=Bitmap.createScaledBitmap(bitmap, 300,400,false);
                        else
                            bitmap=Bitmap.createScaledBitmap(bitmap, 400,280,false);
            }
        }
    }
    return bitmap;

}

public static String getPictureDateFromPicturePath(String picturePath)
{
        int length=picturePath.length();
        int index=picturePath.indexOf("20");
        String trimmedPicturePath=picturePath.substring(index, length-12);
        String year=trimmedPicturePath.substring(0,4);
        String month=trimmedPicturePath.substring(4,6);
        String day=trimmedPicturePath.substring(6,8);
        String picDate=day+"/"+month+"/"+year;
        return picDate;
}

public static void deletePic(String picturePath)
{
    File file=new File(picturePath);
    if (file.exists())
    {
        if(file.delete())
            Log.v("File deleted", picturePath);
        else
            Log.v("File not deleted", picturePath);
    }
}

public static Date convertStringToDate(String string)
{
    Date myDate=new Date();
    SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yy");
    try
    {
        myDate=sdf.parse(string);
    }
    catch (ParseException ex)
    {Log.v("Date exception", ex.getMessage());}
    return myDate;
}

public static int convertFertilizingNeedsToInteger(int value)
{
    int newValue=0;
    switch (value){
        case 1:
            newValue=7;
            break;
        case 2:
            newValue=14;
            break;
        case 3:
            newValue=21;
            break;
        case 4:
            newValue=28;
            break;
        case 5:
            newValue=42;
            break;
        case 6:
            newValue=56;
            break;
            default:
                break;
    }
return newValue;
}
}
