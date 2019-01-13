package com.hamada.android.talktome;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.hamada.android.talktome.Model.Users;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {
    @BindView(R.id.recycleer_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private DatabaseReference mReference;
    public static String USER_ID="user_id";
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseRecyclerAdapter<Users,AllUserViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
       // mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mReference=FirebaseDatabase.getInstance().getReference().child("users");

         mReference.keepSynced(true);



        setupRecyclerView();

    }
    @Override
    protected void onStart() {
        super.onStart();

        adapter.startListening();


    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void setupRecyclerView(){

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .limitToLast(50);


        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        };
        query.addChildEventListener(childEventListener);




        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(query, Users.class)
                        .build();



        adapter=new FirebaseRecyclerAdapter<Users, AllUserViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final AllUserViewHolder holder,
                            final int position, @NonNull final Users model) {

                        holder.mTVNameAlluser.setText(model.getUser_name());
                        holder.mStateTV.setText(model.getUser_state());
                        Picasso.get().load(model.getUser_thumb_image())
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.profile)
                                .into(holder.mcircleImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(model.getUser_thumb_image())
                                                .placeholder(R.drawable.profile)
                                                .into(holder.mcircleImageView);

                                    }
                                });
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String Position_id=getRef(position).getKey();
                                Intent intent=new Intent
                                        (UsersActivity.this,ProfilesActivity.class);
                                intent.putExtra(USER_ID,Position_id);
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AllUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view=LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.cardview_alluser,parent,false);
                        return new AllUserViewHolder(view);
                    }
                };
        mRecyclerView.setAdapter(adapter);

    }

    public static class AllUserViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_all_user)
        TextView mTVNameAlluser;
        @BindView(R.id.tv_all_state)
        TextView mStateTV;
        CircleImageView mcircleImageView;
        public AllUserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            mcircleImageView=itemView.findViewById(R.id.profile_all_user);

        }

    }
}
