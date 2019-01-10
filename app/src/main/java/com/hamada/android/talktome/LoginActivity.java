package com.hamada.android.talktome;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hamada.android.talktome.Network.CheckNetwork;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.button2)
    Button login;
    @BindView(R.id.et_userName)
    EditText mUserName;
    @BindView(R.id.et_password)
    EditText mPassword;
    @BindView(R.id.tv_new_register)
    TextView mNewRegister;
    private FirebaseAuth mFirebaseAuth;
    private ProgressDialog dialog;
    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    private DatabaseReference mDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
       //calling method splash
        splashScreen();
        mNewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
        mFirebaseAuth=FirebaseAuth.getInstance();
        dialog=new ProgressDialog(this);
        mDatabaseReference=FirebaseDatabase.getInstance().getReference().child("users");
    }

    public void Login(View view) {

        String email=mUserName.getText().toString();
        String password=mPassword.getText().toString();


        if (CheckNetwork.isInternetAvailable(this)) {
            loginAccount(email,password);

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ofline);
            builder.setTitle(R.string.dialog_titile_internet);
            builder.setMessage(R.string.dialog_message_internet);
            builder.setPositiveButton(R.string.dialog_action_internet,
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(intent);

                }
            });
            builder.show();

        }

    }

    //Splash
    private void splashScreen(){

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                login.setVisibility(View.VISIBLE);
                mNewRegister.setVisibility(View.VISIBLE);
                mUserName.setVisibility(View.VISIBLE);
                mPassword.setVisibility(View.VISIBLE);

            }
        }, SPLASH_DISPLAY_LENGTH);
    }

   //login
    private void loginAccount(String email,String password){

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please Enter Your Email",
                    Toast.LENGTH_SHORT).show();
        }if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please Enter Your Password",
                    Toast.LENGTH_SHORT).show();
        }else {
            dialog.setTitle("LoginActivity ");
            dialog.setMessage("Please wait to login");
            dialog.show();
            mFirebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){
                                //get user id
                                String onlin_Id=mFirebaseAuth.getCurrentUser().getUid();
                                //get node
                                String deviceToken=FirebaseInstanceId.getInstance().getToken();
                                //store device token
                                mDatabaseReference.child(onlin_Id).child("device_token").setValue(deviceToken)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {


                                                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });

                            }else {
                                Toast.makeText(LoginActivity.this, "Please check for Email or Password"
                                        , Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    });

        }

    }
}
