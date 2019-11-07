package ie.dbs.myplants;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
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


class Utils {
    public static FirebaseUser user;
    static FirebaseAuth mAuth;
    static int MyVersion;
    static Context applicationContext;
    static FirebaseDatabase firebaseDatabase;
    static DatabaseReference databaseReference;
    static Plant temporary_plant;
    static Integer plantIterator;
    final static String CHANNEL_ID = "my_channel_01";
    static int isCalledFromAlertDialog = 0;
    static RequestQueue queue;
    final static ArrayList<Plant> today_plants_task_string=new ArrayList<>();

    //asks for permission on SDK>16
    static void AskForPermission(String myPermission, Activity whichActivity) {

        if (Utils.MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (ContextCompat.checkSelfPermission(applicationContext, myPermission)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(whichActivity, new String[]{myPermission}, 1);
            }
        }
    }

    //create custom directory and save file
    static String createDirectoryAndSaveFile(Bitmap imageToSave, Context context, String timestamp) {

        File homeDirectory = new File(applicationContext.getExternalFilesDir(null) + "/MyPlants");
        if (!homeDirectory.exists()) {
            File plantDirectory = new File(applicationContext.getExternalFilesDir(null) + "/MyPlants/");
            plantDirectory.mkdirs();
        }
        File myFile = new File(applicationContext.getExternalFilesDir(null) + "/MyPlants/");

        if(timestamp.equals(" "))
        timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(myFile, "picture" +
                timestamp + ".jpeg");
        String imagePath = file.getAbsolutePath();
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
    static boolean validateForm(@NotNull EditText emailAddress, EditText password) {
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
    static boolean validateRegistrationForm(@NotNull EditText emailAddress, EditText password, EditText
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
    static String[] getLightConditionNames() {
        String myArray = Arrays.toString(Light_Condition.values());
        myArray = myArray.substring(1, myArray.length() - 1);
        return myArray.replace('_', ' ').split(", ");
    }

    @NotNull
    static String[] getWateringNeedsNames() {
        String myArray = Arrays.toString(Watering_Needs.values());
        myArray = myArray.substring(1, myArray.length() - 1);
        return myArray.replace('_', ' ').split(", ");
    }

    @NotNull
    static String[] getFertilizingNeedsNames() {
        String myArray = Arrays.toString(Fertilizing_Needs.values());
        myArray = myArray.substring(1, myArray.length() - 1);
        return myArray.replace('_', ' ').split(", ");
    }


    //push pictures to database
    static void PushPicToDB(String plantID, String imagePath) {
        String userID = Utils.user.getUid();
        PlantImage plantImage = new PlantImage(imagePath);
        Utils.databaseReference.child("users").child(userID).child("plantsPics").child(plantID).child("images").push().setValue(plantImage);
    }

    //retrieve image from saved position
    static Bitmap getThumbNailFromFile(String path) {
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

    static Bitmap getFullImageFromFile(String path)
    {
        Drawable drawable = applicationContext.getResources().getDrawable(R.drawable.replacement_pic);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        if (path != null) {
            if (!path.equals("")) {
                final File imgFile = new File(path);
                if (imgFile.exists()) {
                    bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                }
            }
        }
        return bitmap;
    }

    static String getPictureDateFromPicturePath(String picturePath) {
        int length = picturePath.length();
        int index = picturePath.indexOf("20");
        String trimmedPicturePath = picturePath.substring(index, length - 11);
        String year = trimmedPicturePath.substring(0, 4);
        String month = trimmedPicturePath.substring(4, 6);
        String day = trimmedPicturePath.substring(6, 8);
        return day + "/" + month + "/" + year;
    }

    static void deletePic(String picturePath) {
        File file = new File(picturePath);
        if (file.exists()) {
            if (file.delete())
                Log.v("File deleted", picturePath);
            else
                Log.v("File not deleted", picturePath);
        }
    }


    static int convertFertilizingNeedsToInteger(int value) {
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


    static Date addDaysToDate(Date myDate, int days) {
        Date newDate;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getLastMinuteOfDay(myDate));
        calendar.add(Calendar.DAY_OF_MONTH, days);
        newDate = calendar.getTime();
        return newDate;
    }

    static Plant autoChangeDatesOnceItIsReached(Plant myPlant) {

        Date now = new Date();
        now = addDaysToDate(getLastMinuteOfDay(now), -1);
        if (myPlant != null) {
            if (myPlant.getNextWatering() != null) {
                while (myPlant.getNextWatering().before(now) || myPlant.getNextWatering().equals(now)) {
                    myPlant.setLastWatered(myPlant.getNextWatering());
                    int days = myPlant.getWateringNeeds().value;
                    myPlant.setNextWatering(Utils.addDaysToDate(myPlant.getLastWatered(), days));
                    myPlant.setTaskWateringChecked(false);
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(Utils.applicationContext);
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(Utils.applicationContext, NewAppWidget.class));
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
                }
            }

            if (myPlant.getNextFertilizing() != null) {
                while (myPlant.getNextFertilizing().before(now) || (myPlant.getNextFertilizing() == now)) {

                    myPlant.setLastFertilized(myPlant.getNextFertilizing());
                    myPlant.setTaskFertilizinChecked(false);
                    int days = Utils.convertFertilizingNeedsToInteger(myPlant.getFertilizingNeeds().value);
                    myPlant.setNextFertilizing(Utils.addDaysToDate(myPlant.getLastFertilized(), days));
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(Utils.applicationContext);
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(Utils.applicationContext, NewAppWidget.class));
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);

                }

            }
        }
        return myPlant;
    }

    static Date getLastMinuteOfDay(Date now) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, day, 23, 59, 59);
        now = calendar.getTime();
        return now;
    }

    private static Date getTimeOfDay(Date now) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(Utils.applicationContext);
        String hour=sharedPreferences.getString("notification_time_hour", "11");
        String minute=sharedPreferences.getString("notification_time_minute", "00");
        int hourInt=11;
        int minuteInt=0;
        if(hour!=null)
            hourInt=Integer.parseInt(hour);
        if(minute!=null){
            if(minute.equals("00"))
                minuteInt=0;
            else
                minuteInt=Integer.parseInt(minute);
        }
        calendar.set(year, month, day, hourInt, minuteInt, 0);
        now = calendar.getTime();
        return now;
    }

    static Date addOneSecondToDate(Date now) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.SECOND, 1);
        now = calendar.getTime();
        return now;
    }

    static Date createDateFromString(String string) {
        Date date;
        String[] stringArray = string.split(" ");
        String[] dateArray = stringArray[0].split("-");
        String[] timeArray = stringArray[1].split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]) - 1, Integer.parseInt(dateArray[2]),
                Integer.parseInt(timeArray[0]), Integer.parseInt(timeArray[1]), Integer.parseInt(timeArray[2]));
        date = calendar.getTime();
        return date;
    }

    static boolean isDateToday(Date date) {
        boolean check = false;
        Date now = new Date();
        Date today = addOneSecondToDate(addDaysToDate(getLastMinuteOfDay(now), -1));
        Date tomorrow = getLastMinuteOfDay(now);
        if (date.before(tomorrow) && date.after(today))
            check = true;
        return check;
    }

    static boolean isDateTomorrow(Date date) {
        boolean check = false;
        Date now = new Date();
        Date day_after_tomorrow = addDaysToDate(getLastMinuteOfDay(now), 1);
        Date tomorrow = addOneSecondToDate(getLastMinuteOfDay(now));
        if (date.before(day_after_tomorrow) && date.after(tomorrow))
            check = true;
        return check;
    }


    static void cancelNotification(int notificationID, Activity whichActivity) {
        Intent notificationIntent = new Intent(whichActivity, AlarmReceiver.class);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, notificationID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(whichActivity, notificationID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) whichActivity.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(whichActivity, "Notification " + notificationID + "cancelled", Toast.LENGTH_SHORT).show();
    }

    private static void scheduleNotification(Notification notification, Date whenToStart, int notificationID, long interval) {

        Intent notificationIntent = new Intent(Utils.applicationContext, AlarmReceiver.class);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, notificationID);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(Utils.applicationContext, notificationID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(whenToStart);
        Date date = calendar.getTime();
        Log.v("notification time", String.valueOf(date));

        AlarmManager alarmManager = (AlarmManager) Utils.applicationContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), interval, pendingIntent);
        //alarmManager.setRepeating(AlarmManager.RTC, delay, interval, pendingIntent);
        Toast.makeText(Utils.applicationContext, "Notification set" + notificationID+" "+date, Toast.LENGTH_SHORT).show();
    }


    private static Notification getNotification(String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(Utils.applicationContext, Utils.CHANNEL_ID);
        return builder
                .setContentTitle("Scheduled Notification")
                .setContentText(content)
                .setSmallIcon(R.drawable.my_plant_icon).build();
    }



    static void setFertilizingNotification(Plant myPlant) {

        if (myPlant.getFertilizingNeeds() != Fertilizing_Needs.None) {
            if (myPlant.getNextFertilizing() == null) {
                myPlant.setLastFertilized(new Date());
                myPlant.setNextFertilizing(Utils.addDaysToDate(myPlant.getLastFertilized(), Utils.convertFertilizingNeedsToInteger(myPlant.getFertilizingNeeds().value)));
            }

            long interval = TimeUnit.DAYS.toMillis(Utils.convertFertilizingNeedsToInteger(myPlant.getFertilizingNeeds().value));
            Date now = new Date();
            Date notificationDate = getTimeOfDay(myPlant.getNextFertilizing());
            if (now.getTime() > notificationDate.getTime())
                notificationDate = getTimeOfDay((addDaysToDate(notificationDate, convertFertilizingNeedsToInteger(myPlant.getFertilizingNeeds().value))));
            int notificationID = generateNotificationID(myPlant.getPlantID(), false);
            Utils.scheduleNotification(Utils.getNotification(myPlant.getName() + " needs fertilizing"), notificationDate, notificationID, interval);
        } else {
            myPlant.setNotificationFertilizing(false);
            Toast.makeText(Utils.applicationContext, "Please set up your fertilizing needs first",
                    Toast.LENGTH_LONG).show();

        }
    }

    static void setWateringNotification(Plant myPlant) {

        if (myPlant.getWateringNeeds() != Watering_Needs.None) {
            if (myPlant.getNextWatering() == null) {
                myPlant.setLastWatered(new Date());
                myPlant.setNextWatering(Utils.addDaysToDate(myPlant.getLastWatered(), myPlant.getWateringNeeds().value));
            }

            long interval = TimeUnit.DAYS.toMillis(myPlant.getWateringNeeds().value);
            Date now = new Date();
            Date notificationDate = getTimeOfDay(myPlant.getNextWatering());
            if (now.getTime() > notificationDate.getTime())
                notificationDate = getTimeOfDay((addDaysToDate(notificationDate, myPlant.getWateringNeeds().value)));
            long delay = notificationDate.getTime();
            int notificationID = generateNotificationID(myPlant.getPlantID(), true);
            Log.v("notification date", String.valueOf(notificationDate));
            Log.v("delayAlarm", String.valueOf(delay));
            Log.v("intervalAlarm", String.valueOf(TimeUnit.DAYS.toMillis(myPlant.getWateringNeeds().value)));
            Utils.scheduleNotification(Utils.getNotification(myPlant.getName() + " needs watering" + notificationID), notificationDate, notificationID, interval);
        } else {
            myPlant.setNotificationWatering(false);
            Toast.makeText(Utils.applicationContext, "Please set up your watering needs first",
                    Toast.LENGTH_LONG).show();
        }

    }


    static int generateNotificationID(String plantID, boolean isWatering) {
        int notificationID = 0;
        if (isWatering) {
            if (plantID != null) {
                String notificationIDString;
                if (!plantID.equals("0")) {
                    notificationIDString = plantID + "0";
                    notificationID = Integer.valueOf(notificationIDString);
                } else
                    notificationID = 0;
            }
        } else {
            if (plantID != null) {
                String notificationIDString;
                if (!plantID.equals("0")) {
                    notificationIDString = plantID + "1";
                    notificationID = Integer.valueOf(notificationIDString);
                } else
                    notificationID = 1;
            }
        }
        return notificationID;
    }


    static ArrayList<PlantImage> sortStringBubbleArray(ArrayList<PlantImage> plants)
    {
        int j;
        boolean flag = true;  // will determine when the sort is finished
        PlantImage temp;

        while (flag) {
            flag = false;
            for (j = 0; j < plants.size() - 1; j++) {
                if (getPictureDateFromPicturePath(plants.get(j).getPicturePath()).compareToIgnoreCase(getPictureDateFromPicturePath(plants.get(j + 1).getPicturePath())) > 0) {
                    temp = plants.get(j);
                    plants.set(j, plants.get(j + 1));
                    plants.set(j + 1, temp);
                    flag = true;
                }
            }
        }
            return plants;
    }

    static ArrayList<String> sortStringBubbleArrayStringArray(ArrayList<String> plants)
    {
        int j;
        boolean flag = true;  // will determine when the sort is finished
        String temp;

        while (flag) {
            flag = false;
            for (j = 0; j < plants.size() - 1; j++) {
                if (getPictureDateFromPicturePath(plants.get(j)).compareToIgnoreCase(getPictureDateFromPicturePath(plants.get(j + 1))) > 0) {
                    temp = plants.get(j);
                    plants.set(j, plants.get(j + 1));
                    plants.set(j + 1, temp);
                    flag = true;
                }
            }
        }
        return plants;
    }
    static ArrayList<Plant> sortStringBubble(ArrayList<Plant> plants, boolean name) {
        int j;
        boolean flag = true;  // will determine when the sort is finished
        Plant temp;

        while (flag) {
            flag = false;
            for (j = 0; j < plants.size() - 1; j++) {
                if (name) {
                    if (plants.get(j).getName().compareToIgnoreCase(plants.get(j + 1).getName()) > 0) {
                        temp = plants.get(j);
                        plants.set(j, plants.get(j + 1));
                        plants.set(j + 1, temp);
                        flag = true;
                    }
                } else {
                    if (plants.get(j).getDateAdded().compareTo(plants.get(j + 1).getDateAdded()) > 0) {
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

    static ArrayList<Plant> sortByOutDoorPlant(ArrayList<Plant> plants, boolean outdoor) {
        ArrayList<Plant> sortedPlants = new ArrayList<>();
        for (Plant plant : plants) {
            if (outdoor) {
                if (plant.isOutdoorPlant()) {
                    sortedPlants.add(plant);
                }
            } else {
                if (!plant.isOutdoorPlant()) {
                    sortedPlants.add(plant);
                }
            }
        }
        return sortedPlants;
    }

    private static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<>();
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

    private static List<Object> toList(JSONArray array) {
        List<Object> list = new ArrayList<>();
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

    static void addPlant(Plant plant) {
        String userID = Utils.user.getUid();
        Utils.databaseReference.child("users").child(userID).child("plants").child(plant.getPlantID()).setValue(plant);
    }

    static int scrollRight(int size, int index) {

        if (size > index + 1)
            index = index + 1;
        else if (size == index + 1)
            index = 0;
        return index;
    }

    static int scrollLeft(int size, int index) {

        if (index > 0)
            index = index - 1;
        else if (index == 0)
            index = size - 1;
        return index;
    }
}

