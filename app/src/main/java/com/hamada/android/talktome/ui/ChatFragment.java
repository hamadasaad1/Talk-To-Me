package com.hamada.android.talktome.ui;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hamada.android.talktome.MessagesActivity;
import com.hamada.android.talktome.Model.Chat;
import com.hamada.android.talktome.ProfilesActivity;
import com.hamada.android.talktome.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.hamada.android.talktome.UsersActivity.USER_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private View mView;
    private RecyclerView mRecyclerView;

    private DatabaseReference mMessageDatabase;
    private DatabaseReference mConversDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView=inflater.inflate(R.layout.fragment_chat, container, false);

        mRecyclerView=mView.findViewById(R.id.recycler_chat);


        mAuth=FirebaseAuth.getInstance();
        mCurrent_user_id=mAuth.getCurrentUser().getUid();

        mConversDatabase=FirebaseDatabase.getInstance()
                .getReference().child("Chat").child(mCurrent_user_id);
        mConversDatabase.keepSynced(true);
        mUsersDatabase=FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabase.keepSynced(true);

        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("Messages").child(mCurrent_user_id);
        mUsersDatabase.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query conversationQuery = mConversDatabase.orderByChild("timestamp");

        FirebaseRecyclerOptions<Chat> options=new FirebaseRecyclerOptions.Builder<Chat>()
                .setQuery(conversationQuery,Chat.class)
                .build();

        FirebaseRecyclerAdapter<Chat,ChatViewHolder> adapter=
                new FirebaseRecyclerAdapter<Chat, ChatViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int position, @NonNull final Chat model) {

                final String list_user_id = getRef(position).getKey();

                Query lastMessageQuery =
                        mMessageDatabase.child(list_user_id).limitToLast(1);
                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        String data = dataSnapshot.child("message").getValue().toString();
                        holder.setMessage(data,model.isSeen());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("user_name").getValue().toString();
                        String userThumb = dataSnapshot.child("user_thumb_image").getValue().toString();

                        if (dataSnapshot.hasChild("online")){
                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            holder.setImageViewState(userOnline);
                        }
                        holder.userName.setText(userName);

                        if (userThumb.equals("image")){
                            Picasso.get().load(userThumb).placeholder(R.drawable.profile)
                                    .into(holder.imageViewUser);
                        }else {
                            Picasso.get().load(userThumb).placeholder(R.drawable.profile)
                                    .into(holder.imageViewUser);
                        }

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intentChat=new Intent
                                        (getContext(),MessagesActivity.class);
                                intentChat.putExtra(USER_ID,list_user_id);
                                intentChat.putExtra("nameuser",userName);
                                startActivity(intentChat);
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
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.cutom_chat_fragment
                        ,parent,false);


                return new ChatViewHolder(view);
            }
        };

        adapter.startListening();
        mRecyclerView.setAdapter(adapter);
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.user_single_name)
        TextView userName;
        @BindView(R.id.user_single_status)
        TextView state;
        @BindView(R.id.user_single_online_icon)
        ImageView imageViewState;
        CircleImageView imageViewUser;

        public ChatViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            imageViewUser=itemView.findViewById(R.id.user_single_image);
        }


        public void setMessage(String message, boolean isSeen){
            state.setText(message);
            if (!isSeen){
                state.setTypeface(state.getTypeface(), Typeface.BOLD);
            }else {
                state.setTypeface(state.getTypeface(), Typeface.NORMAL);
            }
        }

        public void setImageViewState(String online_status){

            if (online_status.equals("true")){
                imageViewState.setVisibility(View.VISIBLE);

            }else {
                imageViewState.setVisibility(View.INVISIBLE);
            }
        }

    }




}
