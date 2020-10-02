package com.example.chatapp;

import android.content.Context;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Node;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private Context context;
    private List<RequestModel> requestModelList;
    private DatabaseReference databaseReferenceFriendRequest,databaseReferenceChat;
    private FirebaseUser currentUser;

    public RequestAdapter(Context context, List<RequestModel> requestModelList) {
        this.context = context;
        this.requestModelList = requestModelList;
    }

    @NonNull
    @Override
    public RequestAdapter.RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.friend_requests,parent,false);

        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RequestAdapter.RequestViewHolder holder, int position) {

        final RequestModel  requestModel = requestModelList.get(position);

        holder.tvfullname.setText(requestModel.getUsername());
        StorageReference fileref = FirebaseStorage.getInstance().getReference().child(Constants.IMAGES_FOLDER+"/"+requestModel.getPhotoname());
        fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(context).load(uri).placeholder(R.drawable.face).error(R.drawable.face).into(holder.ivprofile);
            }
        });

        databaseReferenceFriendRequest = FirebaseDatabase.getInstance().getReference().child(NodeNames.FRIEND_REQUESTS);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReferenceChat = FirebaseDatabase.getInstance().getReference().child(NodeNames.CHATS);

        holder.buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.pbDecision.setVisibility(View.VISIBLE);
                holder.buttonDeny.setVisibility(View.GONE);
                holder.buttonAccept.setVisibility(View.GONE);

                final String userId = requestModel.getUserId();
                databaseReferenceChat.child(currentUser.getUid()).child(userId).child(NodeNames.TIME_STAMP).setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            databaseReferenceChat.child(userId).child(currentUser.getUid()).child(NodeNames.TIME_STAMP).setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            databaseReferenceFriendRequest.child(currentUser.getUid()).child(userId).child(NodeNames.REQUEST_TYPE).setValue(Constants.REQUEST_STATUS_ACCEPTED).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        databaseReferenceFriendRequest.child(userId).child(currentUser.getUid()).child(NodeNames.REQUEST_TYPE).setValue(Constants.REQUEST_STATUS_ACCEPTED).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    holder.pbDecision.setVisibility(View.GONE);
                                                                    holder.buttonDeny.setVisibility(View.VISIBLE);
                                                                    holder.buttonAccept.setVisibility(View.VISIBLE);
                                                                }
                                                                else{
                                                                    Toast.makeText(context, "Fail to Accept Request", Toast.LENGTH_SHORT).show();
                                                                    holder.pbDecision.setVisibility(View.GONE);
                                                                    holder.buttonDeny.setVisibility(View.VISIBLE);
                                                                    holder.buttonAccept.setVisibility(View.VISIBLE);
                                                                }
                                                            }
                                                        });
                                                    }
                                                    else{
                                                        Toast.makeText(context, "Fail to Accept Request", Toast.LENGTH_SHORT).show();
                                                        holder.pbDecision.setVisibility(View.GONE);
                                                        holder.buttonDeny.setVisibility(View.VISIBLE);
                                                        holder.buttonAccept.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                            });
                                        }
                                        else {
                                            Toast.makeText(context, "Fail to Accept Request", Toast.LENGTH_SHORT).show();
                                            holder.pbDecision.setVisibility(View.GONE);
                                            holder.buttonDeny.setVisibility(View.VISIBLE);
                                            holder.buttonAccept.setVisibility(View.VISIBLE);
                                        }
                                }
                            });
                        }
                        else{
                            Toast.makeText(context, "Fail to Accept Request", Toast.LENGTH_SHORT).show();
                            holder.pbDecision.setVisibility(View.GONE);
                            holder.buttonDeny.setVisibility(View.VISIBLE);
                            holder.buttonAccept.setVisibility(View.VISIBLE);
                        }
                    }
                });








            }
        });

        holder.buttonDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.pbDecision.setVisibility(View.VISIBLE);
                holder.buttonDeny.setVisibility(View.GONE);
                holder.buttonAccept.setVisibility(View.GONE);
                final String userId = requestModel.getUserId();
                databaseReferenceFriendRequest.child(currentUser.getUid()).child(userId).child(NodeNames.REQUEST_TYPE).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            databaseReferenceFriendRequest.child(userId).child(currentUser.getUid()).child(NodeNames.REQUEST_TYPE).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        holder.pbDecision.setVisibility(View.GONE);
                                        holder.buttonDeny.setVisibility(View.VISIBLE);
                                        holder.buttonAccept.setVisibility(View.VISIBLE);
                                    }
                                    else{
                                        Toast.makeText(context, "Fail to deny Request", Toast.LENGTH_SHORT).show();
                                        holder.pbDecision.setVisibility(View.GONE);
                                        holder.buttonDeny.setVisibility(View.VISIBLE);
                                        holder.buttonAccept.setVisibility(View.VISIBLE);
                                        
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(context, "Fail to deny Request", Toast.LENGTH_SHORT).show();
                            holder.pbDecision.setVisibility(View.GONE);
                            holder.buttonDeny.setVisibility(View.VISIBLE);
                            holder.buttonAccept.setVisibility(View.VISIBLE);
                            
                        }
                    }
                });
                
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestModelList.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder {

        private TextView tvfullname ;
        private ImageView ivprofile ;
        private Button buttonAccept, buttonDeny;
        private ProgressBar pbDecision;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

             tvfullname = itemView.findViewById(R.id.tvfriendRequestUserName);
             ivprofile = itemView.findViewById(R.id.ivfriendRequestProfile);
             buttonAccept = itemView.findViewById(R.id.buttonAccept);
             buttonDeny = itemView.findViewById(R.id.buttonDeny);
             pbDecision = itemView.findViewById(R.id.pbfriendRequestLayout);


        }
    }
}
