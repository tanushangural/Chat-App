package com.example.chatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private Context context;
    private List<MessageModel> messageModelList;
    private FirebaseAuth firebaseAuth;


    public MessagesAdapter(Context context, List<MessageModel> messageModelList) {
        this.context = context;
        this.messageModelList = messageModelList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.message_layout,parent,false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        MessageModel message = messageModelList.get(position);
        firebaseAuth = FirebaseAuth.getInstance();
        String currentuserId = firebaseAuth.getCurrentUser().getUid();
        String fromUserId = message.getMessageFrom();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy HH:mm");
        String dateTime = simpleDateFormat.format(new Date(message.getMessageTime()));
        String[] strings = dateTime.split(" ");
        String messageTime = strings[1];
        AESAlgo aesAlgo = new AESAlgo();
        String ecryptMessage = message.getMessage();
        String decryptMessage = null;
        try {
            decryptMessage = aesAlgo.decrypt(ecryptMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(currentuserId.equals(fromUserId)){
            holder.llmessageSent.setVisibility(View.VISIBLE);
            holder.llmessageRecieved.setVisibility(View.GONE);


            holder.tvSentMessage.setText(decryptMessage);
            holder.tvSentMessageTime.setText(messageTime);
        }
        else{
            holder.llmessageRecieved.setVisibility(View.VISIBLE);
            holder.llmessageSent.setVisibility(View.GONE);
            holder.tvRecieveMessage.setText(decryptMessage);
            holder.tvRecieveMessageTime.setText(messageTime);
        }

    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }




    public class MessageViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout clMessage;
        LinearLayout llmessageSent,llmessageRecieved;
        TextView tvSentMessage,tvSentMessageTime, tvRecieveMessage,tvRecieveMessageTime;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            clMessage = itemView.findViewById(R.id.clMessage);
            llmessageSent = itemView.findViewById(R.id.llmessageSent);
            llmessageRecieved = itemView.findViewById(R.id.llmessageRecieved);
            tvSentMessage = itemView.findViewById(R.id.tvSentMessage);
            tvSentMessageTime = itemView.findViewById(R.id.tvSentMessageTime);
            tvRecieveMessage = itemView.findViewById(R.id.tvReciveMessage);
            tvRecieveMessageTime = itemView.findViewById(R.id.tvReciveMessageTime);
        }
    }
}
