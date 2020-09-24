package com.example.chatapp;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.utilities.Utilities;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.internal.Util;
import com.google.firebase.storage.internal.Util.*;

import java.util.List;
import java.util.zip.Inflater;

public class FindFriendAdapter extends RecyclerView.Adapter<FindFriendAdapter.FindFriendViewHolder> {


    private Context context;
    private List<FindFriendClass> findFriendModelList;

    private DatabaseReference friendRequestDatabase;
    private FirebaseUser currentUser;
    private  String userId;

    public FindFriendAdapter(Context context, List<FindFriendClass> findFriendModelList) {
        this.context = context;
        this.findFriendModelList = findFriendModelList;
    }

    @NonNull
    @Override
    public FindFriendAdapter.FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(context).inflate(R.layout.find_friends_layout, parent,false);
        return new FindFriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FindFriendAdapter.FindFriendViewHolder holder, int position) {
        final FindFriendClass friendModel = findFriendModelList.get(position);

        holder.tvFullName.setText(friendModel.getUsername());
        StorageReference fileRef = FirebaseStorage.getInstance().getReference().child(Constants.IMAGES_FOLDER + "/" + friendModel.getPhotoname());
        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .placeholder(R.drawable.face)
                        .error(R.drawable.face)
                        .into(holder.ivProfile);

            }
        });

        friendRequestDatabase = FirebaseDatabase.getInstance().getReference().child(NodeNames.FRIEND_REQUESTS);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(friendModel.isSentRequest())
        {
            holder.btnSendRequest.setVisibility(View.GONE);
            holder.btnCancelRequest.setVisibility(View.VISIBLE);
        }else
        {
            holder.btnSendRequest.setVisibility(View.VISIBLE);
            holder.btnCancelRequest.setVisibility(View.GONE);
        }

        holder.btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.btnSendRequest.setVisibility(View.GONE);
                holder.pbRequest.setVisibility(View.VISIBLE);

                userId = friendModel.getUserid();

                friendRequestDatabase.child(currentUser.getUid()).child(userId).child(NodeNames.REQUEST_TYPE)
                        .setValue(Constants.REQUEST_STATUS_SENT).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            friendRequestDatabase.child(userId).child(currentUser.getUid()).child(NodeNames.REQUEST_TYPE)
                                    .setValue(Constants.REQUEST_STATUS_RECEIVED).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {

                                        Toast.makeText(context,"request sent successfully", Toast.LENGTH_SHORT).show();

                                        String title = "New Friend Request";
                                        String message= "Friend request from " + currentUser.getDisplayName();

                                        holder.btnSendRequest.setVisibility(View.GONE);
                                        holder.pbRequest.setVisibility(View.GONE);
                                        holder.btnCancelRequest.setVisibility(View.VISIBLE);
                                    }
                                    else
                                    {
                                        Toast.makeText(context, "failed to sent request"
                                                , Toast.LENGTH_SHORT).show();
                                        holder.btnSendRequest.setVisibility(View.VISIBLE);
                                        holder.pbRequest.setVisibility(View.GONE);
                                        holder.btnCancelRequest.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(context,"failed to sent request"
                                    , Toast.LENGTH_SHORT).show();
                            holder.btnSendRequest.setVisibility(View.VISIBLE);
                            holder.pbRequest.setVisibility(View.GONE);
                            holder.btnCancelRequest.setVisibility(View.GONE);
                        }

                    }
                });



            }
        });

        holder.btnCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.btnCancelRequest.setVisibility(View.GONE);
                holder.pbRequest.setVisibility(View.VISIBLE);

                userId = friendModel.getUserid();

                friendRequestDatabase.child(currentUser.getUid()).child(userId).child(NodeNames.REQUEST_TYPE)
                        .setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            friendRequestDatabase.child(userId).child(currentUser.getUid()).child(NodeNames.REQUEST_TYPE)
                                    .setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {

                                        Toast.makeText(context, "request cancel successfully", Toast.LENGTH_SHORT).show();
                                        holder.btnSendRequest.setVisibility(View.VISIBLE);
                                        holder.pbRequest.setVisibility(View.GONE);
                                        holder.btnCancelRequest.setVisibility(View.GONE);
                                    }
                                    else
                                    {
                                        Toast.makeText(context,"failed to cancel request"
                                                , Toast.LENGTH_SHORT).show();
                                        holder.btnSendRequest.setVisibility(View.GONE);
                                        holder.pbRequest.setVisibility(View.GONE);
                                        holder.btnCancelRequest.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(context,"failed to cancel request"
                                    , Toast.LENGTH_SHORT).show();
                            holder.btnSendRequest.setVisibility(View.GONE);
                            holder.pbRequest.setVisibility(View.GONE);
                            holder.btnCancelRequest.setVisibility(View.VISIBLE);
                        }

                    }
                });



            }
        });
    }

    @Override
    public int getItemCount() {
        return findFriendModelList.size();
    }

    public class FindFriendViewHolder  extends  RecyclerView.ViewHolder{
        private ImageView ivProfile;
        private TextView tvFullName;
        private Button btnSendRequest, btnCancelRequest;
        private ProgressBar pbRequest;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvFullName = itemView.findViewById(R.id.tvUserName);
            btnSendRequest = itemView.findViewById(R.id.buttonSent);
            btnCancelRequest = itemView.findViewById(R.id.buttonCancel);
            pbRequest = itemView.findViewById(R.id.pbfindFriendsLayout);

        }
    }


}
