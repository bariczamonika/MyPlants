package ie.dbs.myplants;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.UTFDataFormatException;

public class Splash extends Activity {
    private String CHANNEL_ID="my_channel_01";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.firebaseDatabase= FirebaseDatabase.getInstance();
        Utils.databaseReference= Utils.firebaseDatabase.getReference("EDMT_FIREBASE");
        setContentView(R.layout.activity_splash);
        Utils.mAuth = FirebaseAuth.getInstance();
        Utils.user=Utils.mAuth.getCurrentUser();
        Utils.MyVersion= Build.VERSION.SDK_INT;
        Utils.applicationContext=getApplicationContext();
        createNotificationChannel();
        Utils.temporary_plant=null;
        if(Utils.user==null)
        {
            Intent intent=new Intent(Splash.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Intent intent=new Intent(Splash.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
