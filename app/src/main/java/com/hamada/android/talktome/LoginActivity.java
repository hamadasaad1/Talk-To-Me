package com.hamada.android.talktome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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
    }

    public void Login(View view) {

        String email=mUserName.getText().toString();
        String password=mPassword.getText().toString();
        loginAccount(email,password);

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
                                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
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
