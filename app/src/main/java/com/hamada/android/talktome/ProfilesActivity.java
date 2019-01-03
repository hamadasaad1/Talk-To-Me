package com.hamada.android.talktome;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfilesActivity extends AppCompatActivity {

    @BindView(R.id.tv_profile_activity_name)
    TextView mName;
    @BindView(R.id.tv_profile_activity_state)
    TextView mState;
    @BindView(R.id.profile_image_activity)
    ImageView imageView;
    @BindView(R.id.bt_request)
    Button mReduestButton;
    @BindView(R.id.bt_decline)
    Button mDeclineButton;
    private DatabaseReference mReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        mReference=FirebaseDatabase.getInstance().getReference().child("users");

        Intent intent=getIntent();
        if (intent !=null && intent.hasExtra(UsersActivity.USER_ID)){
           String user_profile=getIntent().getExtras()
                   .get(UsersActivity.USER_ID).toString();
           getDataById(user_profile);
        }

    }

    private void getDataById(String user_profile){
        mReference.child(user_profile).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("user_name").getValue().toString();
                String state=dataSnapshot.child("user_state").getValue().toString();
                String image_path=dataSnapshot.child("user_thumb_image").getValue().toString();
                String profile_path=dataSnapshot.child("user_image").getValue().toString();
                mName.setText(name);
                mState.setText(state);
                if (image_path.equals("image")){
                    Picasso.get().load(profile_path).placeholder(R.drawable.profile)
                            .into(imageView);
                }else {
                    Picasso.get().load(image_path).placeholder(R.drawable.profile)
                            .into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
