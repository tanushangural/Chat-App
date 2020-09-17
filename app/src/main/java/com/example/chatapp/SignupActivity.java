package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {


    EditText signupName, signupEmail,signupPassword,signupConfirmPassword;
    Button signupButton;
    String stringSignupName, stringSignupEmail,stringSignupPassword,stringSignupConfirmPassword;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupName = findViewById(R.id.signupName);
        signupEmail = findViewById(R.id.signupEmailId);
        signupPassword = findViewById(R.id.signupEditPassword);
        signupConfirmPassword = findViewById(R.id.signupEditConfirmPassword);
        signupButton = findViewById(R.id.signupButton);





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
                else{
                    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.createUserWithEmailAndPassword(stringSignupEmail,stringSignupPassword).addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        firebaseUser = firebaseAuth.getCurrentUser();
                                        updateNameOnly();
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
    }

    public void updateNameOnly(){
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(signupName.getText().toString().trim()).build();
        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    String userID = firebaseUser.getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);

                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put(NodeNames.NAME,signupName.getText().toString().trim());
                    hashMap.put(NodeNames.EMAIL,signupEmail.getText().toString().trim());
                    hashMap.put(NodeNames.ONLINE,"true");
                    hashMap.put(NodeNames.NAME,"");

                    databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
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
}
