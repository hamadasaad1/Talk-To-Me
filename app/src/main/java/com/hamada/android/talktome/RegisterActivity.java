package com.hamada.android.talktome;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hamada.android.talktome.Model.Users;
import com.hamada.android.talktome.Network.CheckNetwork;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RegisterActivity extends AppCompatActivity {

   private FirebaseAuth mFirebaseAuth;
   private Toolbar mToolbar;
   @BindView(R.id.nameRegister)
    EditText mNameRegister;
   @BindView(R.id.emailRegister)
   EditText mEmailRegister;
   @BindView(R.id.passwordRegister)
   EditText mPasswordRegister;
   @BindView(R.id.register)
    Button mRegister;
    private ProgressDialog dialog;
    private DatabaseReference mDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        ButterKnife.bind(this);
        mToolbar=findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mFirebaseAuth=FirebaseAuth.getInstance();
        dialog=new ProgressDialog(this);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               final String name=mNameRegister.getText().toString();
                String email=mEmailRegister.getText().toString();
                String password=mPasswordRegister.getText().toString();
                 //check internet connected or not
                if (CheckNetwork.isInternetAvailable(RegisterActivity.this)) {
                    registerAccount(name,email,password);

                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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
        });
    }


    private void registerAccount(final String name, String email, String password){

        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please Enter Your Name!",
                    Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please Enter Your Email!",
                    Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please Enter Your Password!",
                    Toast.LENGTH_SHORT).show();
        }else {
            dialog.setTitle("Create New Account");
            dialog.setMessage("Please wait....");
            dialog.show();
            mFirebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){
                                String user_Id=mFirebaseAuth.getCurrentUser().getUid();

                                String deviceToken=FirebaseInstanceId.getInstance().getToken();
                              mDatabaseReference=FirebaseDatabase.getInstance()
                                      .getReference().child("users").child(user_Id);
                              mDatabaseReference.child("user_name").setValue(name);
                              mDatabaseReference.child("user_state").setValue("Hello Every one");
                              mDatabaseReference.child("user_image").setValue("profile");
                              mDatabaseReference.child("device_token").setValue(deviceToken);
                              mDatabaseReference.child("user_thumb_image").setValue("image")
                                      .addOnCompleteListener(new OnCompleteListener<Void>() {
                                          @Override
                                          public void onComplete(@NonNull Task<Void> task) {
                                              if (task.isSuccessful()){
                                                  Intent intent=new Intent(RegisterActivity.this,
                                                          MainActivity.class);
                                                  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|
                                                          Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                  startActivity(intent);
                                                  finish();
                                              }
                                          }
                                      });


                            }else {
                                Toast.makeText(RegisterActivity.this,
                                        "Error Try again...."
                                        , Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    });
        }
    }
}
