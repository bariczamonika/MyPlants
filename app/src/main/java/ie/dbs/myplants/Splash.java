package ie.dbs.myplants;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

public class Splash extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Utils.mAuth = FirebaseAuth.getInstance();
        Utils.user=Utils.mAuth.getCurrentUser();
        Utils.MyVersion= Build.VERSION.SDK_INT;
        Utils.applicationContext=getApplicationContext();
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
}
