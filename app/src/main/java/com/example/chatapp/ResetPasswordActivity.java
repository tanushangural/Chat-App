package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    LinearLayout linearLayout1;
    private TextView textView;

    private Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        etEmail = findViewById(R.id.etEmail);
        linearLayout1 = findViewById(R.id.linearLayout1);

        resetButton = findViewById(R.id.buttonReset);


        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final String email = etEmail.getText().toString().trim();
                if(email.isEmpty()){
                    etEmail.setError("Empty");
                }
                else{
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(ResetPasswordActivity.this, "Instruction to sent to Email", Toast.LENGTH_SHORT).show();
                                finish();


                            }
                            else{
                                textView.setText("Sent message failed");

                            }
                        }
                    });

                }
            }
        });
    }
}
