package com.example.kaahaalba;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.kaahaalba.Models.Users;
import com.example.kaahaalba.databinding.ActivitySettingsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Remove Toolbar :-
        getSupportActionBar().hide();

        // create Instance :-
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this , MainActivity.class);
                startActivity(intent);
            }
        });

        // Ab hum Username , About par kam krge jo ki Settings m h :-
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = binding.etStatus.getText().toString();
                String username  = binding.etUserName.getText().toString();

                // Agar hame koi value Firebase m update karni h to uske liy :- Hashmap use karte h.
                HashMap<String , Object> obj = new HashMap<>();
                obj.put("userName" , username);
                obj.put("status" , status);

                database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                        .updateChildren(obj);
                Toast.makeText(SettingsActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
            }
        });

        // Ab hum Firebase se hum Data lekar aayege :-
        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);

                        // Online Image lekar aayege :-
                        Picasso.get()
                                .load(users.getProfilepic())
                                .placeholder(R.drawable.avatar)
                                .into(binding.profileImage);

                        // Update karne k bad phir se jo data disappear ho gya h use dubara se recover krege :-
                        binding.etStatus.setText(users.getStatus());
                        binding.etUserName.setText(users.getUserName());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*"); // For all Action => */*
                startActivityForResult(intent , 33);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getData() !=null){

            // GetData() internal Storage  k image ka Path hota h :-
            Uri sFile = data.getData();

            // Set Image on profileImage :-
            binding.profileImage.setImageURI(sFile);

            // Ab hame Storage k ander Image ko Upload karvana h :-
                // 	Ab hum node create krege =>
            final StorageReference  reference = storage.getReference().child("profile pictures")
                    // Image ka unique name rakhne k liy :-
                    .child(FirebaseAuth.getInstance().getUid());
            // File ko upload kar dege :-
            reference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Toast.makeText(SettingsActivity.this, "Upload", Toast.LENGTH_SHORT).show();

                    // Ab hum picture ko yha set karvayege :-
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                    .child("profilePic").setValue(uri.toString());
                            Toast.makeText(SettingsActivity.this, "Profile Pic Updated", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        }

    }
}