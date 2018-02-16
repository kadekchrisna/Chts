package com.kadek.chtsys;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout mDisplayName;
    private TextInputLayout mDisplayEmail;
    private TextInputLayout mDisplayPass;
    private Button bRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mDisplayEmail = (TextInputLayout) findViewById(R.id.textinputemail);
        mDisplayName = (TextInputLayout) findViewById(R.id.textinputname);
        mDisplayPass = (TextInputLayout) findViewById(R.id.textinputpass);
        bRegister = (Button) findViewById(R.id.buttreg);
        mAuth = FirebaseAuth.getInstance();

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mDisplayName.getEditText().getText().toString();
                String email = mDisplayEmail.getEditText().getText().toString();
                String pass = mDisplayPass.getEditText().getText().toString();
                register_user(name, email, pass);
            }
        });
    }

    private void register_user(String name, String email, String pass) {
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                }else{
                    Toast.makeText(RegisterActivity.this, "You got some error!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}