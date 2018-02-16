package com.kadek.chtsys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

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
    private Toolbar toolbar;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mDisplayEmail = (TextInputLayout) findViewById(R.id.textinputemail);
        mDisplayName = (TextInputLayout) findViewById(R.id.textinputname);
        mDisplayPass = (TextInputLayout) findViewById(R.id.textinputpass);
        bRegister = (Button) findViewById(R.id.buttreg);
        mAuth = FirebaseAuth.getInstance();
        toolbar = (Toolbar) findViewById(R.id.register_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mDisplayName.getEditText().getText().toString();
                String email = mDisplayEmail.getEditText().getText().toString();
                String pass = mDisplayPass.getEditText().getText().toString();

                if (!TextUtils.isEmpty(name) || (!TextUtils.isEmpty(email)) || (!TextUtils.isEmpty(pass))){
                    progressDialog.setTitle("Registering New User");
                    progressDialog.setMessage("Please Wait");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    register_user(name, email, pass);
                }


            }
        });
    }

    private void register_user(String name, String email, String pass) {
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();

                }else{
                    progressDialog.hide();
                    Toast.makeText(RegisterActivity.this, "Please check the form and try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
