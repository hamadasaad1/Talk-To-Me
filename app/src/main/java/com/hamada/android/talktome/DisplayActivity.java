package com.hamada.android.talktome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hamada.android.talktome.Adapter.AllUsersAdapter;
import com.hamada.android.talktome.Model.Users;

import java.util.ArrayList;
import java.util.List;

public class DisplayActivity extends AppCompatActivity {



    private RecyclerView mRecyclerView;

    private List<Users> list;
    private AllUsersAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        mRecyclerView=findViewById(R.id.my_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        list=new ArrayList<>();

        display();
    }






    private void display(){
        //to fetch all the users of firebase Auth app
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        final DatabaseReference usersdRef = rootRef.child("users");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String name = ds.child("user_name").getValue(String.class);
                    String image=ds.child("user_image").getValue(String.class);
                    Users user=ds.getValue(Users.class);
                   // Log.d("TAG", name);





                    //Users users=new Users(name,image);
                    list.add(user);

                }



                mAdapter=new AllUsersAdapter(list,DisplayActivity.this);
                mRecyclerView.setAdapter(mAdapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        usersdRef.addListenerForSingleValueEvent(eventListener);
    }


}
