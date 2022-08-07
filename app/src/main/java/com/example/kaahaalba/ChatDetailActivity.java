package com.example.kaahaalba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.kaahaalba.Adapters.ChatAdapter;
import com.example.kaahaalba.Models.MessageModel;
import com.example.kaahaalba.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailActivity extends AppCompatActivity {

    ActivityChatDetailBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Remove Toolbar :-
        getSupportActionBar().hide();

        // Ab hum FirebaseDatabase ka instance le lege:-
        database = FirebaseDatabase.getInstance();

        // Ab hum FirebaseAuth ka instance le lege:-
        auth = FirebaseAuth.getInstance();

        // Ab hum userId , profilepic , userName ko chatDetailActivity m receive krege :-
        // Isko Final kar dege to ye Global ho jayaga :-
        final String senderId = auth.getUid();

        // Ab hum receiver ki Id ko lege :-
        String recieveId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");

        // Ab hum set karva dege:-
        binding.userName.setText(userName);

        // Use Picasso to Load image:-
        Picasso.get().load(profilePic).placeholder(R.drawable.avatar).into(binding.profileImage);

        // Ab hum BackArrow par kam krege ChatDeatailActivity m :-
        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatDetailActivity.this , MainActivity.class);
                startActivity(intent);
            }
        });

        // Ab hame y batana h n ki hamara Data kha se aa rha or kha se nhi uske liy :-
        final ArrayList<MessageModel> messageModels = new ArrayList<>();

        // Ab hum chatAdapter ko set karva dege :-
        final ChatAdapter chatAdapter = new ChatAdapter(messageModels , this , recieveId);
        binding.chatRecyclerView.setAdapter(chatAdapter);

        // Ab LinearLayoutManager lgayege :-
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        final String senderRoom = senderId + recieveId;
        final String receiverRoom = recieveId + senderId;

        // Ab hum message ko RecyclerView m udate karva dege :-
        // Ab hum Firebase se Data ko get krege =>
        database.getReference().child("chats")
                .child(senderRoom)
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
                            model.setMessageId(snapshot1.getKey());

                            messageModels.add(model);
                        }

                        /* Jab Sender msg send kar rha h to receiver k pass to msg send ho jata Lekin
                         Sender ki screen par msg jab update/show hota h jab hum Back pressed karte
                         h usko rokne k liy :- */
                        chatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // Ab hum Message send karne ka Backend krege chatDetailActivity m :-
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Empty message chala jata h jab hum send par click karte h to usko rokne k liy :-
                if (binding.etMessage.getText().toString().isEmpty()){
                    binding.etMessage.setError("Write the message");
                    return;
                }

                String message = binding.etMessage.getText().toString();

                // senderId , message ko database m save karvayege.
                final MessageModel model = new MessageModel(senderId , message);

                // Ab hum Timestamp ko set krege :-
                model.setTimestamp(new Date().getTime());

                // Ab hum etMessage ko empty kar dege message send karne k bat :-
                binding.etMessage.setText("");

                // Ab hum child node bnayege Database m :-
                database.getReference().child("chats")
                        .child(senderRoom)

                        /* Ab hum push() method ko use krege :- uske through jo Runtime par time hota h mtlb 2 bj k 4 min
                         itne sec par msg ho gya uska Timestamp ban kar String
                         m convert ho k ak node ban jata h . Ye wala
                         kam  .push() method k through ho jata h. */
                        .push()

                        // Ab hum value ko set krege and addOnSuccessListener lgayege :- isme receiverRoom ka kam krege.
                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        database.getReference().child("chats")
                                .child(receiverRoom)

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
        });

    }
}