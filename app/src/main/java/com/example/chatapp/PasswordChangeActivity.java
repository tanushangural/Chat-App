package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordChangeActivity extends AppCompatActivity {

    private EditText etPassword,etConfirmPassword;
    private Button confirmButton;
    String password,confirmPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        etPassword = findViewById(R.id.passwordEfittext);
        etConfirmPassword = findViewById(R.id.confirmPasswordEdittext);
        confirmButton = findViewById(R.id.confirmPasswordButton);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = etPassword.getText().toString().trim();
                confirmPassword = etConfirmPassword.getText().toString().trim();


                if(password.equals("")){
                    etPassword.setError("Empty");
                }
                else if(confirmPassword.isEmpty()){
                    etConfirmPassword.setError("Empty");
                }
                else if(password.equals(confirmPassword) == false){
                    etConfirmPassword.setError("Not matched");
                }
                else{
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                    if(firebaseUser!=null){
                        firebaseUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(PasswordChangeActivity.this, "Successfully Changed", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                else{
                                    Toast.makeText(PasswordChangeActivity.this, "Failed: "+task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }
            }
        });

    }
}
