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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private Context context;
    private List<RequestModel> requestModelList;

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

        RequestModel  requestModel = requestModelList.get(position);

        holder.tvfullname.setText(requestModel.getUsername());
        StorageReference fileref = FirebaseStorage.getInstance().getReference().child(Constants.IMAGES_FOLDER+"/"+requestModel.getPhotoname());
        fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(context).load(uri).placeholder(R.drawable.face).error(R.drawable.face).into(holder.ivprofile);
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
             pbDecision = itemView.findViewById(R.id.progresssBar);


        }
    }
}
