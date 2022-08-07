package com.example.kaahaalba.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kaahaalba.Adapters.UsersAdapter;
import com.example.kaahaalba.Models.Users;
import com.example.kaahaalba.R;
import com.example.kaahaalba.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatsFragment extends Fragment {

    public ChatsFragment() {
        // Required empty public constructor
    }

    FragmentChatsBinding binding;
    ArrayList<Users> list = new ArrayList<>();
    FirebaseDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater, container, false);

        // Ab hum FirebaseDatabase ka instance le lege:-
        database = FirebaseDatabase.getInstance();

        // Ab hum Adapter ko set karvayege onCreateView m :-
        UsersAdapter adapter = new UsersAdapter(list , getContext());
        binding.chatRecyclerView.setAdapter(adapter);

        // Ab hum layout ko set karvayege onCreateView m :-
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        // Ab hum Firebase se Data ko lege or RecyclerView m rakhwayege :-
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSanpshot: snapshot.getChildren()) {
                    Users users = dataSanpshot.getValue(Users.class);
                    users.setUserId(dataSanpshot.getKey());

                    // Login User show n ho uske liy :-
                    if(!users.getUserId().equals(FirebaseAuth.getInstance().getUid())){
                        list.add(users);
                    }

                }
                // Jo bhi Firebase Database changes karoge to ye usko update kar dega :-
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return binding.getRoot();
    }
}