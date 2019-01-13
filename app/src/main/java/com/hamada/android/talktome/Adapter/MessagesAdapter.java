package com.hamada.android.talktome.Adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.hamada.android.talktome.Model.Messages;
import com.hamada.android.talktome.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<Messages> messagesList;
    private FirebaseAuth mAuth;

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
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        Messages messages=messagesList.get(position);
        String sender_id=mAuth.getCurrentUser().getUid();

        String from_user =messages.getFrom();
        if (from_user.equals(sender_id)){
            holder.textViewMessage.setBackgroundResource(R.drawable.background_receiver_message);
            holder.textViewMessage.setTextColor(Color.WHITE);
            holder.textViewMessage.setGravity(Gravity.LEFT);
        }
        else {
            holder.textViewMessage.setBackgroundResource(R.drawable.background_text_message);
            holder.textViewMessage.setTextColor(Color.BLACK);
            holder.textViewMessage.setGravity(Gravity.RIGHT);
        }
        holder.textViewMessage.setText(messages.getMessage());
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.textView_message_user)
        TextView textViewMessage;
        public MessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
