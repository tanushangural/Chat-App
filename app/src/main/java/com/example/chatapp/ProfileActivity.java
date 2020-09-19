package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

   private EditText signupName, signupEmail;
   private Button signupButton,logoutButton,passwordchangeButton;
    private String stringSignupName, stringSignupEmail;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private Uri localPathUri, serverPathUri;
   private ImageView profileImage;
   private View progressBar;


    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        signupName = findViewById(R.id.profileName);
        signupEmail = findViewById(R.id.profileEmailId);
        signupButton = findViewById(R.id.updateButton);
        profileImage = findViewById(R.id.profileUpdateImage);
        logoutButton = findViewById(R.id.logoutButton);
        passwordchangeButton = findViewById(R.id.passwordchangeButton);
        progressBar = findViewById(R.id.pbProfile);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser!=null){
            signupName.setText(firebaseUser.getDisplayName());
            signupEmail.setText(firebaseUser.getEmail());
            serverPathUri = firebaseUser.getPhotoUrl();

            if(serverPathUri !=null){
                //for showing images from server, when need to use some library like glide library
                Glide.with(this).load(serverPathUri).placeholder(R.drawable.face).error(R.drawable.face).into(profileImage);
            }
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                startActivity(new Intent(ProfileActivity.this,LoginActivity.class));
                finish();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signupName.getText().toString().isEmpty()){
                    signupName.setError("Something wrong");
                }
                else if(localPathUri == null){
                    progressBar.setVisibility(View.VISIBLE);
                    updateNameOnly();
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    updatePhotoAndName();
                }
            }
        });


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(v);
            }
        });


        passwordchangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,PasswordChangeActivity.class));
            }
        });

    }



    private void changeImage(View view){
        if(serverPathUri == null){
            pickImage();
        }
        else{
            final PopupMenu popupMenu = new PopupMenu(this,view);
            popupMenu.getMenuInflater().inflate(R.menu.menu_picture,popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    if(id == R.id.changePicture){
                        pickImage();
                    }
                    else if(id == R.id.removePhoto){

                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(signupName.getText().toString().trim()).
                                setPhotoUri(null).build();
                        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    String userID = firebaseUser.getUid();
                                    databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);

                                    HashMap<String,String> hashMap = new HashMap<>();
                                    hashMap.put(NodeNames.NAME,signupName.getText().toString().trim());

                                    hashMap.put(NodeNames.PHOTO,"");

                                    databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(ProfileActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                                else{
                                    Toast.makeText(ProfileActivity.this, "Failed to Update", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }

                    return false;
                }
            });
            popupMenu.show();
        }
    }

    private void pickImage(){
        if(ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,101);
        }
        else {
            ActivityCompat.requestPermissions(ProfileActivity.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 102);
        }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 102){
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,101);
        }
        else{
            Toast.makeText(ProfileActivity.this, "Access is Required", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101) {
            localPathUri = data.getData();
            profileImage.setImageURI(localPathUri);
        }
    }


    public void updatePhotoAndName(){
        String strFilename = firebaseUser.getUid()+".jpg";
        final StorageReference storageRef = storageReference.child("images/"+strFilename);
        storageRef.putFile(localPathUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            serverPathUri = uri;
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(signupName.getText().toString().trim()).
                                    setPhotoUri(uri).build();

                            firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if(task.isSuccessful()){
                                        String userID = firebaseUser.getUid();
                                        databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);

                                        HashMap<String,String> hashMap = new HashMap<>();
                                        hashMap.put(NodeNames.NAME,signupName.getText().toString().trim());

                                        hashMap.put(NodeNames.PHOTO,serverPathUri.getPath());

                                        databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(ProfileActivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            }
                                        });

                                    }
                                    else{
                                        Toast.makeText(ProfileActivity.this, "Failed to Update", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });




                        }
                    });
                }
            }
        });
    }

    public void updateNameOnly(){
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(signupName.getText().toString().trim()).build();
        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    String userID = firebaseUser.getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);

                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put(NodeNames.NAME,signupName.getText().toString().trim());


                    databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ProfileActivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });

                }
                else{
                    Toast.makeText(ProfileActivity.this, "Failed to Update", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



}
