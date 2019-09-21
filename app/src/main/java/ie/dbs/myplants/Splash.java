package ie.dbs.myplants;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

public class Splash extends Activity {
private static int SPLASH_TIME_OUT=2500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Utils.mAuth = FirebaseAuth.getInstance();
        if(Utils.user==null)
        {
            Intent intent=new Intent(Splash.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            if (Utils.user.isEmailVerified())
            {
                Intent intent=new Intent(Splash.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                AlertDialog.Builder builder=new AlertDialog.Builder(Splash.this);
                builder.setTitle("Email not verified");
                builder.setMessage("Please verify your email address");
                builder.setPositiveButton("Send email", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.user.sendEmailVerification();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.show();
            }
        }
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(Splash.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_TIME_OUT);*/
    }
}
