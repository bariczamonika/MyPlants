package ie.dbs.myplants;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;


import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.RequestQueue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class Utils extends Activity {
    public static FirebaseUser user;
    public static FirebaseAuth mAuth;
    public static int MyVersion;
    public static Context applicationContext;
    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    public static Plant temporary_plant;
    public static Integer plantIterator;
    public static String CHANNEL_ID = "my_channel_01";
    public static int isCalledFromAlertDialog = 0;
    public static RequestQueue queue;

    //asks for permission on SDK>16
    public static void AskForPermission(String myPermission, Activity whichActivity) {

        if (Utils.MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (ContextCompat.checkSelfPermission(applicationContext, myPermission)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(whichActivity, new String[]{myPermission}, 1);
            }
        }
    }

    //create custom directory and save file
    public static String createDirectoryAndSaveFile(Bitmap imageToSave, Context context) {

        File homeDirectory = new File(applicationContext.getExternalFilesDir(null) + "/MyPlants");
        if (!homeDirectory.exists()) {
            File plantDirectory = new File(applicationContext.getExternalFilesDir(null) + "/MyPlants/");
            plantDirectory.mkdirs();
        }
        File myFile = new File(applicationContext.getExternalFilesDir(null) + "/MyPlants/");

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(myFile, "picture" +
                timestamp + ".jpeg");
        String imagePath = file.getAbsolutePath();
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
                    new String[]{file.toString()}, null,
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

        String myPasswordConfirmation = passwordConfirmation.getText().toString();
        if (TextUtils.isEmpty(myPasswordConfirmation)) {
            passwordConfirmation.setError("Required.");
            valid = false;
        } else if (!myPasswordConfirmation.equals(myPassword)) {
            passwordConfirmation.setError("Passwords don't match");
            valid = false;
        } else {
            passwordConfirmation.setError(null);
        }

        return valid;
    }

    //convert Light condition enum to string array for spinners
    @NotNull
    public static String[] getLightConditionNames() {
        String myArray = Arrays.toString(Light_Condition.values());
        myArray = myArray.substring(1, myArray.length() - 1);
        return myArray.replace('_', ' ').split(", ");
    }

    @NotNull
    public static String[] getWateringNeedsNames() {
        String myArray = Arrays.toString(Watering_Needs.values());
        myArray = myArray.substring(1, myArray.length() - 1);
        return myArray.replace('_', ' ').split(", ");
    }

    @NotNull
    public static String[] getFertilizingNeedsNames() {
        String myArray = Arrays.toString(Fertilizing_Needs.values());
        myArray = myArray.substring(1, myArray.length() - 1);
        return myArray.replace('_', ' ').split(", ");
    }


    //push pictures to database
    public static void PushPicToDB(String plantID, String imagePath) {
        String userID = Utils.user.getUid();
        PlantImage plantImage = new PlantImage(imagePath);
        Utils.databaseReference.child("users").child(userID).child("plantsPics").child(plantID).child("images").push().setValue(plantImage);
    }

    //retrieve image from saved position
    public static Bitmap getImageFromFile(String path) {
        Drawable drawable = applicationContext.getResources().getDrawable(R.drawable.replacement_pic);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        bitmap = Bitmap.createScaledBitmap(bitmap, 400, 280, false);

        if (path != null) {
            if (!path.equals("")) {
                final File imgFile = new File(path);
                if (imgFile.exists()) {
                    bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    if (bitmap.getHeight() > bitmap.getWidth())
                        bitmap = Bitmap.createScaledBitmap(bitmap, 300, 400, false);
                    else
                        bitmap = Bitmap.createScaledBitmap(bitmap, 400, 280, false);
                }
            }
        }
        return bitmap;

    }

    public static String getPictureDateFromPicturePath(String picturePath) {
        int length = picturePath.length();
        int index = picturePath.indexOf("20");
        String trimmedPicturePath = picturePath.substring(index, length - 12);
        String year = trimmedPicturePath.substring(0, 4);
        String month = trimmedPicturePath.substring(4, 6);
        String day = trimmedPicturePath.substring(6, 8);
        String picDate = day + "/" + month + "/" + year;
        return picDate;
    }

    public static void deletePic(String picturePath) {
        File file = new File(picturePath);
        if (file.exists()) {
            if (file.delete())
                Log.v("File deleted", picturePath);
            else
                Log.v("File not deleted", picturePath);
        }
    }


    public static int convertFertilizingNeedsToInteger(int value) {
        int newValue = 0;
        switch (value) {
            case 1:
                newValue = 7;
                break;
            case 2:
                newValue = 14;
                break;
            case 3:
                newValue = 21;
                break;
            case 4:
                newValue = 28;
                break;
            case 5:
                newValue = 42;
                break;
            case 6:
                newValue = 56;
                break;
            default:
                break;
        }
        return newValue;
    }


    public static Date addDaysToDate(Date myDate, int days) {
        Date newDate;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getLastMinuteOfDay(myDate));
        calendar.add(Calendar.DAY_OF_MONTH, days);
        newDate = calendar.getTime();
        return newDate;
    }

    //TODO user can choose the time for notification
    public static Plant autoChangeDatesOnceItIsReached(Plant myPlant) {

        Date now = new Date();
        now = getLastMinuteOfDay(now);
        if (myPlant != null) {
            if (myPlant.getNextWatering() != null) {
                while (myPlant.getNextWatering().before(now)) {
                    if ((myPlant.getNextWatering() == now) || (myPlant.getNextWatering().before(now))) {
                        myPlant.setLastWatered(myPlant.getNextWatering());
                        int days = myPlant.getWateringNeeds().value;
                        myPlant.setNextWatering(Utils.addDaysToDate(myPlant.getLastWatered(), days));
                    }
                }
            }

            if (myPlant.getNextFertilizing() != null) {
                while (myPlant.getNextFertilizing().before(now)) {
                    if ((myPlant.getNextFertilizing() == now) || (myPlant.getNextFertilizing().before(now))) {
                        myPlant.setLastFertilized(myPlant.getNextFertilizing());
                        int days = Utils.convertFertilizingNeedsToInteger(myPlant.getFertilizingNeeds().value);
                        myPlant.setNextFertilizing(Utils.addDaysToDate(myPlant.getLastFertilized(), days));
                    }
                }

            }
        }
        return myPlant;
    }

    public static Date getLastMinuteOfDay(Date now) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, day, 23, 59, 59);
        now = calendar.getTime();
        return now;
    }

    public static Date getTimeOfDay(Date now) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, day, 11, 00, 00);
        now = calendar.getTime();
        return now;
    }


    public static void cancelNotification(int notificationID, Activity whichActivity) {
        Intent notificationIntent = new Intent(whichActivity, AlarmReceiver.class);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, notificationID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(whichActivity, notificationID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) whichActivity.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(whichActivity, "Notification " + notificationID + "cancelled", Toast.LENGTH_SHORT).show();
    }

    public static void scheduleNotification(Notification notification, long delay, int notificationID, long interval) {

        //TODO how to sort out Utils.NotificationIterator Save it in DB? probably the best solution?
        Intent notificationIntent = new Intent(Utils.applicationContext, AlarmReceiver.class);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, notificationID);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(Utils.applicationContext, notificationID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) Utils.applicationContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, futureInMillis, interval, pendingIntent);
        Toast.makeText(Utils.applicationContext, "Notification set" + notificationID, Toast.LENGTH_SHORT).show();
    }


    public static Notification getNotification(String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(Utils.applicationContext, Utils.CHANNEL_ID);
        Notification notification = builder
                .setContentTitle("Scheduled Notification")
                .setContentText(content)
                .setSmallIcon(R.drawable.my_plant_icon).build();
        return notification;
    }

    public static void setFertilizingNotification(Plant myPlant) {

        if (myPlant.getFertilizingNeeds() != Fertilizing_Needs.None) {
            if (myPlant.getNextFertilizing() == null) {
                myPlant.setLastFertilized(new Date());
                myPlant.setNextFertilizing(Utils.addDaysToDate(myPlant.getLastFertilized(), Utils.convertFertilizingNeedsToInteger(myPlant.getFertilizingNeeds().value)));
            }

            long interval = TimeUnit.DAYS.toMillis(Utils.convertFertilizingNeedsToInteger(myPlant.getFertilizingNeeds().value));
            Date now=new Date();
            Date notificationDate=getTimeOfDay(myPlant.getNextFertilizing());
            if(now.getTime()>notificationDate.getTime())
                notificationDate= getTimeOfDay((addDaysToDate(notificationDate,convertFertilizingNeedsToInteger(myPlant.getFertilizingNeeds().value))));
            long delay =notificationDate.getTime()-now.getTime();
            int notificationID=generateNotificationID(myPlant.getPlantID(),false);

            Utils.scheduleNotification(Utils.getNotification(myPlant.getName() + " needs fertilizing"), delay, notificationID,  interval);
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(Utils.applicationContext).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Please set up your plant's fertilizing needs");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

        }
    }

    public static void setWateringNotification(Plant myPlant) {

        if (myPlant.getWateringNeeds() != Watering_Needs.None) {
            if (myPlant.getNextWatering() == null) {
                myPlant.setLastWatered(new Date());
                myPlant.setNextWatering(Utils.addDaysToDate(myPlant.getLastWatered(), myPlant.getWateringNeeds().value));
            }

            //TODO schedule notifications for a certain amount of time? 30 days
            //TODO how to clear notifications
            long interval = TimeUnit.DAYS.toMillis(myPlant.getWateringNeeds().value);
            Date now=new Date();
            Date notificationDate=getTimeOfDay(myPlant.getNextWatering());
            if(now.getTime()>notificationDate.getTime())
               notificationDate= getTimeOfDay((addDaysToDate(notificationDate,myPlant.getWateringNeeds().value)));
            long delay =notificationDate.getTime()-now.getTime();
            int notificationID=generateNotificationID(myPlant.getPlantID(), true);
            Log.v("delayAlarm", String.valueOf(delay));
            Log.v("intervalAlarm", String.valueOf(TimeUnit.DAYS.toMillis(myPlant.getWateringNeeds().value)));
                Utils.scheduleNotification(Utils.getNotification(myPlant.getName() + " needs watering" + notificationID), delay, notificationID,  interval);
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(Utils.applicationContext).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Please set up your plant's watering needs");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }

        }




      public static int generateNotificationID(String plantID, boolean isWatering)
      {
          int notificationID = 0;
          if(isWatering) {
              if (plantID != null) {
                  String notificationIDString;
                  if (!plantID.equals("0")) {
                      notificationIDString = plantID + "0";
                      notificationID = Integer.valueOf(notificationIDString);
                  }
                  else
                      notificationID=0;
              }
          }
          else
          {
              if (plantID != null) {
                  String notificationIDString;
                  if (!plantID.equals("0")) {
                      notificationIDString = plantID + "1";
                      notificationID = Integer.valueOf(notificationIDString);
                  }
                  else
                      notificationID=1;
              }
          }
          return notificationID;
      }


    public static ArrayList<Plant> sortStringBubble( ArrayList<Plant> plants, boolean name )
    {
        int j;
        boolean flag = true;  // will determine when the sort is finished
        Plant temp;

        while ( flag )
        {
            flag = false;
            for ( j = 0;  j < plants.size() - 1;  j++ )
            {
                if(name) {
                    if (plants.get(j).getName().compareToIgnoreCase(plants.get(j + 1).getName()) > 0) {
                        temp = plants.get(j);
                        plants.set(j, plants.get(j + 1));
                        plants.set(j + 1, temp);
                        flag = true;
                    }
                }
                else
                {
                    if(plants.get(j).getDateAdded().compareTo(plants.get(j+1).getDateAdded())>0){
                        temp = plants.get(j);
                        plants.set(j, plants.get(j + 1));
                        plants.set(j + 1, temp);
                        flag = true;
                    }
                }
            }
        }
        return plants;
    }

    public static ArrayList<Plant> sortByOutDoorPlant(ArrayList<Plant>plants, boolean outdoor){
          ArrayList<Plant>sortedPlants=new ArrayList<>();
          for(Plant plant:plants) {
              if (outdoor) {
                  if (plant.isOutdoorPlant()){
                      sortedPlants.add(plant);
                  }
              }
              else
              {
                  if(!plant.isOutdoorPlant()){
                      sortedPlants.add(plant);
                  }
              }
          }
          return sortedPlants;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) {
        List<Object> list = new ArrayList<Object>();
        try {
            for (int i = 0; i < array.length(); i++) {
                Object value = array.get(i);
                if (value instanceof JSONArray) {
                    value = toList((JSONArray) value);
                } else if (value instanceof JSONObject) {
                    value = Utils.toMap((JSONObject) value);
                }
                list.add(value);
            }
        } catch (Exception ex) {
            Log.e("Exception", ex.getMessage());
            Toast.makeText(applicationContext, "That didn't work", Toast.LENGTH_SHORT).show();
        }
        return list;
    }
}

