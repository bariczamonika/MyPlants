package ie.dbs.myplants;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static FirebaseUser user;
    public static FirebaseAuth mAuth;
    public static int MyVersion;
    public static Context applicationContext;
   // public static File homeDirectory;
  //  public static File plantDirectory;

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

    public static int checkHowManyFilesInDirectory(File myFile)
    {
        if(myFile.exists())
        return myFile.listFiles().length;
        else return 0;

    }

    public static void createDirectoryAndSaveFile(Bitmap imageToSave/*, String fileName*/) {

       // int number=0;
        File homeDirectory=new File(Environment.getDataDirectory() + "/MyPlants");
        if (!homeDirectory.exists()) {
            File plantDirectory = new File("/sdcard/MyPlants/");
            plantDirectory.mkdirs();
        }
        File myFile=new File("/sdcard/Myplants");
        int number=myFile.listFiles().length+1;
        File file = new File("/sdcard/MyPlants/", "picture"+number+".jpeg");
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
}
