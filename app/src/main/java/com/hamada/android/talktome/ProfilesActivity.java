package com.hamada.android.talktome;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

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
    @BindView(R.id.profile_toolbar)
    Toolbar mToolbar;
    private DatabaseReference mReference;
    private DatabaseReference mFriendRequestRef;
    private DatabaseReference mFriendRef;
    private DatabaseReference mNotificationRef;
    private String mCurrentState;
    private FirebaseAuth mAuth;
    private String mSender_Id;
    private String mReciver_Id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //make node for notification
        mNotificationRef=FirebaseDatabase.getInstance().getReference().child("notification");
        mNotificationRef.keepSynced(true);
        //reference for requests
        mFriendRequestRef=FirebaseDatabase.getInstance().getReference().child("friendRequest");
        mReference=FirebaseDatabase.getInstance().getReference().child("users");
        mFriendRef=FirebaseDatabase.getInstance().getReference().child("friends");
        //to show data when offline
        mReference.keepSynced(true);
        //to get current user online
        mAuth=FirebaseAuth.getInstance();
        mSender_Id=mAuth.getCurrentUser().getUid();
        mCurrentState="not_friends";

        Intent intent=getIntent();
        if (intent !=null && intent.hasExtra(UsersActivity.USER_ID)){
           mReciver_Id=getIntent().getExtras()
                   .get(UsersActivity.USER_ID).toString();
            Log.d("TAGID",mReciver_Id);
           getDataById(mReciver_Id);
        }

        //first check the user
        if (!mSender_Id.equals(mReciver_Id)) {
            mReduestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mReduestButton.setEnabled(false);
                    //this  to send request for any user
                    if (mCurrentState.equals("not_friends")) {

                        sendRequestParson();
                    }
                    //this to cancel request when send request
                    if (mCurrentState.equals("request_send")) {

                        cancelFriendRequest();
                    }
                    //this to accept request
                    if (mCurrentState.equals("request_received")) {
                        acceptFriendRequest();
                    }
                    //this to unfriend request
                    if (mCurrentState.equals("friends")) {
                        unFriendParson();

                    }
                }
            });
        }else {
            //make two button un visible
            mReduestButton.setVisibility(View.INVISIBLE);
            mDeclineButton.setVisibility(View.INVISIBLE);
        }

    }



    //this method to make un friend for the parson
    private void unFriendParson() {
        //to remove receiver
        mFriendRef.child(mSender_Id).child(mReciver_Id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            //to remove sender
                            mFriendRef.child(mReciver_Id).child(mSender_Id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                mReduestButton.setEnabled(true);
                                                mCurrentState="not_friends";
                                                mReduestButton.setText("Send Friend Request");

                                            }
                                        }
                                    });
                        }
                    }
                });

    }
//this method to accept friend
    private void acceptFriendRequest() {

        //to get date format
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat format=new SimpleDateFormat("dd-MM-yyyy");
        final String saveTime=format.format(calendar.getTime());
        //to store this time in database
        mFriendRef.child(mSender_Id).child(mReciver_Id).child("data").setValue(saveTime)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        mFriendRef.child(mReciver_Id).child(mSender_Id).child("data").setValue(saveTime)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //this to remove node for request when become friends
                                        mFriendRequestRef.child(mSender_Id).child(mReciver_Id).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            mFriendRequestRef.child(mReciver_Id).child(mSender_Id)
                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()){
                                                                        mReduestButton.setEnabled(true);
                                                                        mCurrentState="friends";
                                                                        mReduestButton.setText("UnFriend");
                                                                        mDeclineButton.setVisibility(View.INVISIBLE);
                                                                        mDeclineButton.setEnabled(false);
                                                                    }
                                                                }
                                                            });
                                                        }

                                                    }
                                                });

                                    }
                                });
                    }
                });
    }

    //this method to cancel friend request
    private void cancelFriendRequest() {
        //access for sender and receiver id
        mFriendRequestRef.child(mSender_Id).child(mReciver_Id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      if (task.isSuccessful()){
                          mFriendRequestRef.child(mReciver_Id).child(mSender_Id)
                                  .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {

                                  if (task.isSuccessful()){
                                      mReduestButton.setEnabled(true);
                                      mCurrentState="not_friends";
                                      mReduestButton.setText("Send Friend Request");
                                  }
                              }
                          });
                      }

                    }
                });
    }

    //method to show details for profile user
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
                //to change the button name when send or receiver
                mFriendRequestRef.child(mSender_Id)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                               if (dataSnapshot.hasChild(mReciver_Id)) {
                                   String req_type = dataSnapshot.child(mReciver_Id)
                                           .child("request_type").getValue().toString();
                                   Log.d("TAG", req_type);
                                   if (req_type.equals("send")) {
                                       mCurrentState = "request_send";
                                       mReduestButton.setText("Cancel Friend Request");
                                   } else if (req_type.equals("receiver")) {
                                       mCurrentState = "request_received";
                                       mReduestButton.setText("Accept Friend Request");

                                       //make decline button visible
                                       mDeclineButton.setVisibility(View.VISIBLE);
                                       mDeclineButton.setEnabled(true);

                                       mDeclineButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               declineFriendRequest();
                                           }
                                       });

                                   }
                               }
                           else
                               {
                                   mFriendRef.child(mSender_Id).
                                           addValueEventListener(new ValueEventListener() {
                                               @Override
                                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                   if (dataSnapshot.hasChild(mReciver_Id)){
                                                       mCurrentState="friends";
                                                       mReduestButton.setText("UnFriend");
                                                   }
                                               }

                                               @Override
                                               public void onCancelled(@NonNull DatabaseError databaseError) {

                                               }
                                           });
                               }
                           }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void declineFriendRequest() {
        //access for sender and receiver id
        mFriendRequestRef.child(mSender_Id).child(mReciver_Id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mFriendRequestRef.child(mReciver_Id).child(mSender_Id)
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        mReduestButton.setEnabled(true);
                                        mCurrentState="not_friends";
                                        mReduestButton.setText("Send Friend Request");
                                        mDeclineButton.setVisibility(View.INVISIBLE);
                                        mDeclineButton.setEnabled(false);
                                    }
                                }
                            });
                        }

                    }
                });
    }

    //method to send request
    private void sendRequestParson(){

        //make request for user
        mFriendRequestRef.child(mSender_Id).child(mReciver_Id)
                .child("request_type").setValue("send")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            //to notify the user when any one send request
                            mFriendRequestRef.child(mReciver_Id).child(mSender_Id).
                                    child("request_type").setValue("receiver")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){

                                                //make notification
                                                HashMap<String,String> notificationData=
                                                        new HashMap<>();
                                                notificationData.put("from",mSender_Id);
                                                notificationData.put("type","request");
                                                mNotificationRef.child(mReciver_Id).setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()){
                                                                    //make a button is true
                                                                    mReduestButton.setEnabled(true);
                                                                    //current state
                                                                    mCurrentState="request_send";
                                                                    //change button name
                                                                    mReduestButton.setText("Cancel Friend Request");
                                                                }
                                                            }
                                                        });

                                            }

                                        }
                                    });
                        }
                    }
                });
    }
}
