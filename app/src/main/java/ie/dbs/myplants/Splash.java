package ie.dbs.myplants;

import androidx.annotation.NonNull;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class Splash extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set up Firebase
        Utils.firebaseDatabase = FirebaseDatabase.getInstance();
        Utils.databaseReference = Utils.firebaseDatabase.getReference("EDMT_FIREBASE");
        setContentView(R.layout.activity_splash);
        Utils.mAuth = FirebaseAuth.getInstance();
        Utils.user = Utils.mAuth.getCurrentUser();
        Utils.MyVersion = Build.VERSION.SDK_INT;
        Utils.applicationContext = getApplicationContext();
        createNotificationChannel();
        Utils.temporary_plant = null;
        //Log.v("user", Utils.user.getEmail());
        if(Utils.user!=null) {
            Utils.user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(Splash.this, DashBoard.class);
                        startActivity(intent);
                        finish();
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(Splash.this).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage(task.getException().getMessage());
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "LOGIN AGAIN", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent=new Intent(Splash.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                        alertDialog.show();
                    }
                }
            });
        }
        else{
            Intent intent = new Intent(Splash.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        if (Utils.queue == null) {
            Utils.queue = Volley.newRequestQueue(getApplicationContext());}

    }
    //create the notification channel
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
