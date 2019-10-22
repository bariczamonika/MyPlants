package ie.dbs.myplants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText emailAddress;
    private EditText passwordField;
    private Button submitButton;
    private TextView register;
    private TextView forgotPassword;
    private static final String TAG="LoginActivity";
    private ConnectionReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailAddress=findViewById(R.id.email);
        passwordField=findViewById(R.id.password);
        submitButton=findViewById(R.id.submit);
        register=findViewById(R.id.register);
        forgotPassword=findViewById(R.id.forgotPassword);
        receiver=new ConnectionReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, filter);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(emailAddress.getText().toString(), passwordField.getText().toString());
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailAddress.getText().toString();
                if(email.isEmpty())
                {
                    final AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("Error");
                    builder.setMessage("Please fill in email address");
                    builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent=new Intent(LoginActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    builder.show();
                }
                else {
                    Utils.mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
                                builder.setTitle("Password reset email sent");
                                builder.setMessage("Please check your inbox");
                                builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                });
                            } else {
                                AlertDialog dialog=new AlertDialog.Builder(LoginActivity.this).create();
                                dialog.setTitle("Error");
                                dialog.setMessage("Failure sending password reset email" + task.getException());
                                dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                            }
                        }
                    });

                    Intent intent=new Intent(LoginActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    //signIn with email and password
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!Utils.validateForm(emailAddress,passwordField)) {
            return;
        }

        Utils.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            Utils.user = Utils.mAuth.getCurrentUser();
                            if (Utils.user.isEmailVerified())
                            {
                                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(LoginActivity.this, "User signed in successfully",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else
                            {
                                AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
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

                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }
}
