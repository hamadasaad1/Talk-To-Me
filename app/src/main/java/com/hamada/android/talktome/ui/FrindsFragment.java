package com.hamada.android.talktome.ui;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.hamada.android.talktome.MessagesActivity;
import com.hamada.android.talktome.Model.Friends;
import com.hamada.android.talktome.ProfilesActivity;
import com.hamada.android.talktome.R;
import com.hamada.android.talktome.UsersActivity;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.hamada.android.talktome.UsersActivity.USER_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class FrindsFragment extends Fragment {

    public static final String TAG=FrindsFragment.class.getSimpleName();

    //view
   private View mView ;

   private DatabaseReference mReference;
   private FirebaseAuth mAuth;

   private  RecyclerView mRecyclerView;

   private FirebaseRecyclerAdapter<Friends,FrindsViewHolder> adapter;

   private DatabaseReference mDatabaseReference;
    public FrindsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mView=inflater.inflate(R.layout.fragment_frinds, container, false);


        mRecyclerView=(RecyclerView) mView.findViewById(R.id.friends_recyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);

        mAuth=FirebaseAuth.getInstance();
        String onlin_id=mAuth.getCurrentUser().getUid();
         Log.d(TAG,onlin_id);
        mReference=FirebaseDatabase.getInstance()
                .getReference().child("friends").child(onlin_id);
        mReference.keepSynced(true);
        mDatabaseReference=FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseReference.keepSynced(true);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Friends> options=
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(mReference,Friends.class)
                        .build();

        adapter=new FirebaseRecyclerAdapter<Friends, FrindsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FrindsViewHolder holder,
                                            int position, @NonNull Friends model) {

                final String list_user=getRef(position).getKey();
                Log.d(TAG,list_user);
                mDatabaseReference.child(list_user).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        final String userName=dataSnapshot.child("user_name").getValue().toString();
                        String userState=dataSnapshot.child("user_state").getValue().toString();
                        String userImage=dataSnapshot.child("user_thumb_image").getValue().toString();

                        holder.mNameTV.setText(userName);
                        holder.mStateTV.setText(userState);
                        Picasso.get().load(userImage).placeholder(R.drawable.profile)
                                .into(holder.mcircleImageView);

                        if (dataSnapshot.hasChild("online")){
                            String state_online=  dataSnapshot.child("online").getValue().toString();
                            holder.setUserOnline(state_online);
                        }
                        //set on click for item frends
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //on this create two icon for send message and view profile
                                CharSequence [] charSequences=new
                                        CharSequence[]{"Open Profile","Send Message"};
                                //make alert dialog
                                AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
                                dialog.setTitle("Select Options");
                                dialog.setItems(charSequences, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        //event listener for item
                                        if (which==0){
                                            //this to open profile activity

                                            Intent intent=new Intent
                                                    (getContext(),ProfilesActivity.class);
                                            intent.putExtra(USER_ID,list_user);
                                            startActivity(intent);
                                        }
                                        //this send message
                                        if (which==1){
                                          if(dataSnapshot.child("online").exists()){
                                              Intent intentChat=new Intent
                                                      (getContext(),MessagesActivity.class);
                                              intentChat.putExtra(USER_ID,list_user);
                                              intentChat.putExtra("nameuser",userName);
                                              startActivity(intentChat);
                                          }else
                                          {
                                              mDatabaseReference.child(list_user)
                                                      .setValue(ServerValue.TIMESTAMP)
                                                      .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                          @Override
                                                          public void onSuccess(Void aVoid) {

                                                              Intent intentChat=new Intent
                                                                      (getContext(),MessagesActivity.class);
                                                              intentChat.putExtra(USER_ID,list_user);
                                                              intentChat.putExtra("nameuser",userName);
                                                              startActivity(intentChat);
                                                          }
                                                      });
                                          }
                                        }

                                    }
                                });

                                dialog.show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public FrindsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cardview_alluser,parent,false);
                return new FrindsViewHolder(view);
            }
        };
        adapter.startListening();
        mRecyclerView.setAdapter(adapter);

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public static class FrindsViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_all_user)
        TextView mNameTV;
        @BindView(R.id.tv_all_state)
        TextView mStateTV;
        @BindView(R.id.image_online)
        ImageView mImageViewOnline;
        CircleImageView mcircleImageView;


        public FrindsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            mcircleImageView=itemView.findViewById(R.id.profile_all_user);

        }
        public void setUserOnline(String state_user){

            if (state_user.equals("true")){
                mImageViewOnline.setVisibility(View.VISIBLE);

            }else {
                mImageViewOnline.setVisibility(View.INVISIBLE);
            }
        }
    }
}
