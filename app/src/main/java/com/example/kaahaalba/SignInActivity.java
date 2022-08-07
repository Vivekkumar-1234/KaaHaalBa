package com.example.kaahaalba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.kaahaalba.Models.Users;
import com.example.kaahaalba.databinding.ActivitySignInBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

    ActivitySignInBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Remove Toolbar :-
        getSupportActionBar().hide();

        // Ab hum FirebaseAuth ka instance le lege:-
        auth = FirebaseAuth.getInstance();

        // Ab hum FirebaseDatabase ka instance le lege:-
        database = FirebaseDatabase.getInstance();

        // Ab hum ak Loading lgayege ki jaise hi user SignIn par click kare to ak Loading chale uske liy :-
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Login to your Account");

        // uske bad ye SignActivity.java m add onCreate method m krege :-
            // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // for the requestIdToken, this is in the values.xml file that
                // is generated from your google-services.json
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        // Ab SignIn wale Button par onClickListener lga dege :-
        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show Error Message jab ap Email Id , Password nhi likh rhe or Sign In kar rhe ho to :-
                if (binding.etEmail.getText().toString().isEmpty()){
                    binding.etEmail.setError("Enter your Email");
                    return;
                }

                if (binding.etPassword.getText().toString().isEmpty()){
                    binding.etPassword.setError("Enter your Password");
                    return;
                }

                // Ab hum ProgressDailog ko show karvayege :-
                progressDialog.show();

                // Iske ander Authentication wala kam karna h . Ki user n jab click kar diya h to auth ko lena h or SignUp kar dena uske liy :-
                auth.signInWithEmailAndPassword(binding.etEmail.getText().toString() , binding.etPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Ab hum ProgressDailog ko end karvayege :-
                                progressDialog.dismiss();
                                if(task.isSuccessful())
                                {
                                    // Jab user SignIn par click karega to hum Intent chala dege :-
                                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                               else
                                {
                                    // During SignIn agar koi bhi Problem/Exception ata h to vo hum Toast m show karva dege :-
                                    Toast.makeText(SignInActivity.this, task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // Ab hum Click for SignUp wale Button par kam krege :-
        binding.tvClickSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(SignInActivity.this , SignUpActivity.class);
                startActivity(intent);
            }
        });

        // btn_google par onClickListener lga dege :-
        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        /* Ak bar User ne SignIn kar lya lekin Jab hum App
         ko dubara open karte h to vo dubara SignIn karne ko khta h Usko Problem ko hatane k liy :- */
        if(auth.getCurrentUser()!=null)
        {
            Intent intent =new Intent(SignInActivity.this , MainActivity.class);
            startActivity(intent);
        }
    }

    // Ye hum onCreate k bhar paste karege :-
    int RC_SIGN_IN = 65;
    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        /* Phle hum Intent se First Activity se Second Activity m jate the Lekin Ab is
         code se hum Second Activity se data lekar First Activity m aa rhe h :- */
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG" , "firebaseAuthWithGoogle:" + account.getId());
                // Data ko lekar ane wala kam firebaseAuthWithGoogle s karege :- uska hum method bnayege.
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
                // ...
            }
        }
    }

    // Data ko lekar ane wala kam firebaseAuthWithGoogle s karege :- uska hum method bnayege.
    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();

                            // Ab hum Google Se profile pic , username , Id lege :-
                            Users users = new Users();
                            users.setUserId(user.getUid());
                            users.setUserName(user.getDisplayName());
                            users.setProfilepic(user.getPhotoUrl().toString());

                            // Ab hum Database par kam krege :-
                            database.getReference().child("Users").child(user.getUid()).setValue(users);


                            // Jab hum Google se Sign in krege to Main Activity m chale jaege :-
                            Intent intent = new Intent(SignInActivity.this , MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(SignInActivity.this, "Sign in with Google", Toast.LENGTH_SHORT).show();

                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            //Snackbar.make(mBinding.mainLayout , "Authentication Failed. " , Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

}