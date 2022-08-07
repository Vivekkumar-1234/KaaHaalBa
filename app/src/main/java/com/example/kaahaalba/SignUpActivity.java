package com.example.kaahaalba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.kaahaalba.Models.Users;
import com.example.kaahaalba.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    private FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Remove Toolbar :-
        getSupportActionBar().hide();

        // Ab hum FirebaseAuth ka instance le lege:-
        auth = FirebaseAuth.getInstance();

        // Ab hum FirebaseDatabase ka instance le lege:-
        database = FirebaseDatabase.getInstance();

        // Ab hum ak Loading lgayege ki jaise hi user SignUp par click kare to ak Loading chale uske liy :-
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We're creating your Account");

        // Ab hum SignUp wale Button par onClickListenr lgayege :-
        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Ab hum ProgressDailog ko show karvayege :-
                progressDialog.show();

                // Iske ander Authentication wala kam karna h . Ki user n jab click kar diya h to auth ko lena h or SignUp kar dena uske liy :-
                auth.createUserWithEmailAndPassword(binding.etEmail.getText().toString(), binding.etPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Ab hum ProgressDailog ko end karvayege :-
                                progressDialog.dismiss();

                                // Jab user SignUp create kar lega to hum Toast dikhayege :-
                                if (task.isSuccessful()) {
                                    // Database ko save karvane k liy hamne ak kam kiya tha Users k ander ab hum usi ko SignUp Activity.java m lege :-
                                    Users user = new Users(binding.etUserName.getText().toString(), binding.etEmail.getText().toString()
                                            , binding.etPassword.getText().toString());

                                    // Ab hum ID get krege :-
                                    String id = task.getResult().getUser().getUid();

                                    // Isi ID k agnist hum sare userka Data differen node pa set karvayege :- child bna dege
                                    database.getReference().child("Users").child(id).setValue(user);

                                    Toast.makeText(SignUpActivity.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    // During SignUp agar koi bhi Problem/Exception ata h to vo hum Toast m show karva dege :-
                                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // Ab hum Already have Account wale Button par kam krege :-
        binding.tvAlreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(SignUpActivity.this , SignInActivity.class);
                startActivity(intent);
            }
        });

    }
}