package com.example.kaahaalba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.kaahaalba.Adapters.FragmentsAdapter;
import com.example.kaahaalba.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Ab hum FirebaseAuth ka instance le lege:-
        auth = FirebaseAuth.getInstance();

        // Ab hum MainActivity.java m viewPager ke Backend par kam krege :-
        binding.viewPager.setAdapter(new FragmentsAdapter(getSupportFragmentManager()));
        binding.tablayout.setupWithViewPager(binding.viewPager);

    }

    // Ab hame is menu ko MainActivity.java m lekar jana h :-
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // onCreateOptionMenu k ander MenuInflator ko use karege :-
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu , menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    // Ab hame menu par coding karni h to :-

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.settings:
                Intent i =new Intent(MainActivity.this , SettingsActivity.class);
                startActivity(i);
                break;
                
            case R.id.logout:
                auth.signOut();

                // Logout hone k bad hame batana h ki hame kha jana h phir :-
                Intent intent = new Intent(MainActivity.this , SignInActivity.class);
                startActivity(intent);
                break;

            case R.id.groupchat:
                Intent intentt = new Intent(MainActivity.this , GroupChatActivity.class);
                startActivity(intentt);
                break;
                
            default:    
        }
        return true;
    }
}