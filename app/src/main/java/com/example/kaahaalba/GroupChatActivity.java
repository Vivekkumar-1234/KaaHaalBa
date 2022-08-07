package com.example.kaahaalba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.kaahaalba.Adapters.ChatAdapter;
import com.example.kaahaalba.Models.MessageModel;
import com.example.kaahaalba.databinding.ActivityGroupChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {

    ActivityGroupChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Remove Toolbar :-
        getSupportActionBar().hide();

        // Ab hum BackArrow par kam krege GroupChatActivity m :-
        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Ab hum FirebaseDatabase ka instance le lege:-
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        final ArrayList<MessageModel> messageModels = new ArrayList<>();

        // Isme khali hum senderId ko lekar ayege :-
        final String senderId = FirebaseAuth.getInstance().getUid();

        // Ab ham Toolbar/userName/Title ko set krege :-
        binding.userName.setText("Friends Group");

        // Itna karne k bad Adapter set karva dege :-
        final ChatAdapter adapter = new ChatAdapter(messageModels, this);
        binding.chatRecyclerView.setAdapter(adapter);

        // Ab LinearLayoutManager lgayege :-
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        // Ab hum message ko RecyclerView m update karva dege :-
        // Ab hum Firebase se Data ko get krege =>
        database.getReference().child("Group Chat")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Ab hame DataSnapShot multiple chaiye kyuki multiple message honge uske liy hum :-
                        //	loop lgayege jab tak snapshot m children ate ja rhe h.

                        // Ye message bar-bar repeat/update ho rhe h use rokne k liy :-
                        messageModels.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren())
                        {
                            // Is loop k ander hum Data ko show karvayege :-
                            MessageModel model = snapshot1.getValue(MessageModel.class);
                            messageModels.add(model);
                        }

                        /* Jab Sender msg send kar rha h to receiver k pass to msg send ho jata Lekin
                         Sender ki screen par msg jab update/show hota h jab hum Back pressed karte
                         h usko rokne k liy :- */
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // Ab hum Message send karne ka Backend krege GroupChatActivity m :-
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Empty message chala jata h jab hum send par click karte h to usko rokne k liy :-
                if (binding.etMessage.getText().toString().isEmpty()){
                    binding.etMessage.setError("Write the message");
                    return;
                }

                final String message = binding.etMessage.getText().toString();

                // senderId , message ko database m save karvayege.
                final MessageModel model = new MessageModel(senderId, message);

                // Ab hum Timestamp ko set krege :-
                model.setTimestamp(new Date().getTime());

                // Ab hum etMessage ko empty kar dege message send karne k bat :-
                binding.etMessage.setText("");

                // Ab hum child node bnayege Database m :-
                database.getReference().child("Group Chat")
                        /* Ab hum push() method ko use krege :- uske through jo Runtime par time hota h mtlb 2 bj k 4 min
                        itne sec par msg ho gya uska Timestamp ban kar String
                        m convert ho k ak node ban jata h . Ye wala
                        kam  .push() method k through ho jata h. */
                        .push()

                        // Ab hum value ko set krege and addOnSuccessListener lgayege :- isme receiverRoom ka kam krege.
                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });
            }
        });
    }
}