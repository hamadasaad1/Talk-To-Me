package com.hamada.android.talktome.ui;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hamada.android.talktome.Model.Requests;
import com.hamada.android.talktome.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private DatabaseReference mRequestReference;
    private FirebaseAuth mAuth;
    private String mOnline_user;
    private DatabaseReference mUserReference;
    private DatabaseReference mFriendReference;
    private DatabaseReference mFriendsREquestReference;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView= inflater.inflate(R.layout.fragment_requests, container, false);

        mRecyclerView=mView.findViewById(R.id.recycler_request);
        mAuth=FirebaseAuth.getInstance();
        mOnline_user=mAuth.getCurrentUser().getUid();
        mRequestReference=FirebaseDatabase.getInstance()
                .getReference().child("friendRequest").child(mOnline_user);

        mUserReference=FirebaseDatabase.getInstance().getReference().child("users");

        mFriendReference=FirebaseDatabase.getInstance().getReference().child("friends");
        mFriendsREquestReference=FirebaseDatabase.getInstance()
                .getReference().child("friendRequest");

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(linearLayoutManager);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Requests> options=new
                FirebaseRecyclerOptions.Builder<Requests>()
        .setQuery(mRequestReference,Requests.class)
        .build();

        FirebaseRecyclerAdapter<Requests,RequestViewHolder> adapter=new
                FirebaseRecyclerAdapter<Requests, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull Requests model) {

                final String list_user_id=getRef(position).getKey();

                DatabaseReference getType_Ref=getRef(position).child("request_type").getRef();

                getType_Ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){
                            String type=dataSnapshot.getValue().toString();

                            if (type.equals("receiver")){

                                mUserReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        String userName = dataSnapshot.child("user_name").getValue().toString();
                                        String userThumb = dataSnapshot.child("user_thumb_image").getValue().toString();

                                        holder.mTvName.setText(userName);

                                        if (userThumb.equals("image")){
                                            Picasso.get().load(userThumb).placeholder(R.drawable.profile)
                                                    .into(holder.imageView);
                                        }else {
                                            Picasso.get().load(userThumb).placeholder(R.drawable.profile)
                                                    .into(holder.imageView);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                //for accept request
                                holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    acceptFriendRequest(mOnline_user,list_user_id);

                                    }
                                });

                                holder.cancleButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        cancelFriendRequest(mOnline_user,list_user_id);

                                    }
                                });

                            }else if (type.equals("send")){

                                holder.cancleButton.setVisibility(View.INVISIBLE);

                                mUserReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        String userName = dataSnapshot.child("user_name").getValue().toString();
                                        String userThumb = dataSnapshot.child("user_thumb_image").getValue().toString();

                                        holder.mTvName.setText(userName);

                                        if (userThumb.equals("image")){
                                            Picasso.get().load(userThumb).placeholder(R.drawable.profile)
                                                    .into(holder.imageView);
                                        }else {
                                            Picasso.get().load(userThumb).placeholder(R.drawable.profile)
                                                    .into(holder.imageView);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                holder.acceptButton.setText(getResources().getString(R.string.bt_cancel));
                                holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        cancelFriendRequest(mOnline_user,list_user_id);
                                    }
                                });

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.custom_request_layout,parent,false);

                return new RequestViewHolder(view);
            }
        };

        adapter.startListening();
        mRecyclerView.setAdapter(adapter);
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_name_request)
        TextView mTvName;
        CircleImageView imageView;

        @BindView(R.id.bt_accept_request)
        Button acceptButton;
        @BindView(R.id.bt_cancel_request)
        Button cancleButton;
        public RequestViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            imageView=itemView.findViewById(R.id.imageview_request);
        }
    }

    private void acceptFriendRequest(final String online_user, final String list_user_id) {

        //to get date format
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat format=new SimpleDateFormat("dd-MM-yyyy");
        final String saveTime=format.format(calendar.getTime());
        //to store this time in database
        mFriendReference.child(online_user).child(list_user_id).child("data").setValue(saveTime)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        mFriendReference.child(list_user_id).child(online_user).child("data").setValue(saveTime)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //this to remove node for request when become friends
                                        mFriendsREquestReference.child(online_user).child(list_user_id).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            mFriendsREquestReference.child(list_user_id).child(online_user)
                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()){

                                                                        Toast.makeText(getContext()
                                                                                , "Friends Done...",
                                                                                Toast.LENGTH_SHORT).show();
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

    private void cancelFriendRequest(final String online_user, final String list_user_id) {
        //access for sender and receiver id
        mFriendsREquestReference.child(online_user).child(list_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mFriendsREquestReference.child(list_user_id).child(online_user)
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        Toast.makeText(getContext(),
                                                "Cancel Done...",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                    }
                });
    }


}
