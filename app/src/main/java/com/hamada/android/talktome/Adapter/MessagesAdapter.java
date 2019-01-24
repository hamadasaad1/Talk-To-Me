package com.hamada.android.talktome.Adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hamada.android.talktome.Model.Messages;
import com.hamada.android.talktome.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<Messages> messagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference mReference;

    public MessagesAdapter(List<Messages> messagesList) {
        this.messagesList = messagesList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(parent.getContext()).inflate
                (R.layout.message_layout_user,parent,false);
        mAuth=FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {

        Messages messages=messagesList.get(position);
        String sender_id=mAuth.getCurrentUser().getUid();

        String type=messages.getType();

        String from_user =messages.getFrom();
        mReference=FirebaseDatabase.getInstance().getReference().child("users").child(from_user);
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                 String image = dataSnapshot.child("user_thumb_image").getValue().toString();



                    Picasso.get().load(image).placeholder(R.drawable.profile)
                            .into(holder.mImageViewUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (type.equals("text")){


            holder.mNameUser.setVisibility(View.GONE);
            holder.mImageViewSend.setVisibility(View.GONE);

            if (from_user.equals(sender_id)){
                holder.textViewMessage.setBackgroundResource(R.drawable.background_text_message);
                holder.textViewMessage.setTextColor(Color.WHITE);
                holder.textViewMessage.setGravity(Gravity.LEFT);
            }
            else {
                holder.textViewMessage.setBackgroundResource(R.drawable.background_receiver_message);
                holder.textViewMessage.setTextColor(Color.BLACK);
                holder.textViewMessage.setGravity(Gravity.RIGHT);
            }
            holder.textViewMessage.setText(messages.getMessage());



        }else {
            holder.textViewMessage.setVisibility(View.INVISIBLE);
            holder.textViewMessage.setPadding(0,0,0,0);

            holder.mImageViewSend.setVisibility(View.VISIBLE);
            Picasso.get().load(messages.getMessage()).placeholder(R.drawable.profile)
                    .into(holder.mImageViewSend);

        }


    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public  static class MessageViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.textView_message_user)
        TextView textViewMessage;
        @BindView(R.id.image_messages)
        ImageView mImageViewSend;
        CircleImageView mImageViewUser;
        @BindView(R.id.name_text_layout)
        TextView mNameUser;

        public MessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            mImageViewUser=itemView.findViewById(R.id.image_message_user);
        }
    }
}
