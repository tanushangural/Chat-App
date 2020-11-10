package com.example.chatapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ChatlistAdapter extends RecyclerView.Adapter<ChatlistAdapter.ChatlistViewHolder> {

    private List<ChatlistModel> chatlistModelList;
    private Context context;

    public ChatlistAdapter(List<ChatlistModel> chatlistModelList, Context context) {
        this.chatlistModelList = chatlistModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chatlist_layout,parent,false);
        return new ChatlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatlistViewHolder holder, int position) {

        final ChatlistModel chatlistModel = chatlistModelList.get(position);
        holder.fullnameCL.setText(chatlistModel.getUsername());

        StorageReference fileref = FirebaseStorage.getInstance().getReference().child(Constants.IMAGES_FOLDER+"/"+chatlistModel.getPhotoname());
        fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(context).load(uri).placeholder(R.drawable.face).error(R.drawable.face).into(holder.ivprofileCL);
            }
        });

        holder.llChatList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ChatActivity.class);
                intent.putExtra("user_key",chatlistModel.getUserId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatlistModelList.size();
    }






    public class ChatlistViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llChatList;
        private TextView fullnameCL, lastmessageCL, unreadCountCL, lastmessageTimeCL;
        private ImageView ivprofileCL;

        public ChatlistViewHolder(@NonNull View itemView) {
            super(itemView);

            llChatList = itemView.findViewById(R.id.chatLIstLayout);
            fullnameCL = itemView.findViewById(R.id.usernameCL);

            ivprofileCL = itemView.findViewById(R.id.ivProfileChatList);
        }
    }
}
