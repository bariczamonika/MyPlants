package ie.dbs.myplants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;


public class RegisterActivity extends Activity {
    private EditText emailAddress;
    private EditText passwordField;
    private EditText confirmPasswordField;
    private static final String TAG="RegisterActivity";
    private ConnectionReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        emailAddress=findViewById(R.id.email);
        passwordField=findViewById(R.id.password);
        confirmPasswordField=findViewById(R.id.confirmPassword);
        Button registerButton = findViewById(R.id.register);
        receiver=new ConnectionReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, filter);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(emailAddress.getText().toString(),passwordField.getText().toString());
            }
        });
    }

    //create account with email and password
    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!Utils.validateRegistrationForm(emailAddress,passwordField, confirmPasswordField)) {
            return;
        }

        Utils.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            Utils.user = Utils.mAuth.getCurrentUser();
                            sendEmailVerification();
                            AlertDialog dialog=new AlertDialog.Builder(RegisterActivity.this).create();
                            dialog.setTitle("Information");
                            dialog.setMessage("A verification email was sent to your email address");
                            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            dialog.show();

                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            AlertDialog alertDialog=new AlertDialog.Builder(RegisterActivity.this).create();
                            alertDialog.setTitle("Error");
                            alertDialog.setMessage("Failure to create user" + task.getException());
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                });
    }


    //send the verification email
    private void sendEmailVerification() {


        Utils.user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this,
                                    "Verification email sent to " + Utils.user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(RegisterActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, filter);
    }
}
