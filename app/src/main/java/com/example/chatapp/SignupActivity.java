package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {


   private EditText signupName, signupEmail,signupPassword,signupConfirmPassword;
   private Button signupButton;
    private String stringSignupName, stringSignupEmail,stringSignupPassword,stringSignupConfirmPassword;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private Uri localPathUri, serverPathUri;
    private ImageView profileImage;
    private View progressBar;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupName = findViewById(R.id.signupName);
        signupEmail = findViewById(R.id.signupEmailId);
        signupPassword = findViewById(R.id.signupEditPassword);
        signupConfirmPassword = findViewById(R.id.signupEditConfirmPassword);
        signupButton = findViewById(R.id.signupButton);
        progressBar = findViewById(R.id.pbSignUp);
        profileImage = findViewById(R.id.profileImage);


        storageReference = FirebaseStorage.getInstance().getReference();



        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stringSignupName = signupName.getText().toString().trim();
                stringSignupEmail = signupEmail.getText().toString().trim();
                stringSignupPassword = signupPassword.getText().toString().trim();
                stringSignupConfirmPassword = signupConfirmPassword.getText().toString().trim();


                if(stringSignupName.equals("")){
                    signupName.setError("Something Wrong");
                }
                else if(stringSignupEmail.isEmpty()){
                    signupEmail.setError("Something Wrong");
                }
                else if(stringSignupPassword.isEmpty()){
                    signupPassword.setError("Something Wrong");
                }
                else if(stringSignupConfirmPassword.isEmpty()){
                    signupConfirmPassword.setError("Something Wrong");
                }
                else if(stringSignupPassword.equals(stringSignupConfirmPassword) == false){
                    signupConfirmPassword.setError("Not Matched");
                }
                else if(localPathUri == null){
                    Toast.makeText(SignupActivity.this, "Select Profile Photo ", Toast.LENGTH_SHORT).show();
                }
                else{
                    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.createUserWithEmailAndPassword(stringSignupEmail,stringSignupPassword).addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        firebaseUser = firebaseAuth.getCurrentUser();
                                        updatePhotoAndName();

                                    }
                                    else{
                                        Toast.makeText(SignupActivity.this, "Failed: "+task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    );
                }
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(SignupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,101);
                }
                else {
                    ActivityCompat.requestPermissions(SignupActivity.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 102);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 102){
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,101);
        }
        else{
            Toast.makeText(SignupActivity.this, "Access is Required", Toast.LENGTH_SHORT).show();
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
        progressBar.setVisibility(View.VISIBLE);
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
                                        progressBar.setVisibility(View.VISIBLE);
                                        String userID = firebaseUser.getUid();
                                        databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);

                                        HashMap<String,String> hashMap = new HashMap<>();
                                        hashMap.put(NodeNames.NAME,signupName.getText().toString().trim());
                                        hashMap.put(NodeNames.EMAIL,signupEmail.getText().toString().trim());
                                        hashMap.put(NodeNames.ONLINE,"true");
                                        hashMap.put(NodeNames.PHOTO,serverPathUri.getPath());

                                        databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressBar.setVisibility(View.GONE);
                                                if(task.isSuccessful()){
                                                    Toast.makeText(SignupActivity.this, "Successfully Created", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(SignupActivity.this,LoginActivity.class));
                                                }
                                            }
                                        });

                                    }
                                    else{
                                        Toast.makeText(SignupActivity.this, "Failed to Update", Toast.LENGTH_SHORT).show();
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
        progressBar.setVisibility(View.VISIBLE);
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(signupName.getText().toString().trim()).build();
        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.VISIBLE);
                    String userID = firebaseUser.getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);

                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put(NodeNames.NAME,signupName.getText().toString().trim());
                    hashMap.put(NodeNames.EMAIL,signupEmail.getText().toString().trim());
                    hashMap.put(NodeNames.ONLINE,"true");
                    hashMap.put(NodeNames.PHOTO,"");

                    databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.GONE);
                            if(task.isSuccessful()){
                                Toast.makeText(SignupActivity.this, "Successfully Created", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
                            }
                            else{
                                Toast.makeText(SignupActivity.this, "Failed to created", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                else{
                    Toast.makeText(SignupActivity.this, "Failed to Update", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
