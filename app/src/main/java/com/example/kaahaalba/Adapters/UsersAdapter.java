package com.example.kaahaalba.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kaahaalba.ChatDetailActivity;
import com.example.kaahaalba.Models.Users;
import com.example.kaahaalba.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    ArrayList<Users> list;
    Context context;

    // Ab hum Constructor bna dege :-
    public UsersAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    // Ab hum jo bhi Users Sample bnaya use inflate karva dege onCreateViewHolder wale method m :-
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_user , parent , false);
        return new ViewHolder(view);
    }

    // Is Users Sample m value kha se vo wala kam onBindViewHolder m krege :-
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Data Position k hisab se rkhege :-
        Users users = list.get(position);

        // Jo hamari image h vo Firebase se aa rhi h means Online to uske liy use :- picasso
        Picasso.get().load(users.getProfilepic()).placeholder(R.drawable.avatar).into(holder.image);
        holder.userName.setText(users.getUserName());

        // Ab hum Last Message ko show karyaege User’s List m :-
        FirebaseDatabase.getInstance().getReference().child("chats")
                // Ab hum sender and receiver ki Id lege isme :-
                .child(FirebaseAuth.getInstance().getUid() + users.getUserId())

                // Last wala message access karne k liy :-
                .orderByChild("timestamp")

                // Ab limit set krege litne msg chaiye :-
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()){
                            for (DataSnapshot snapshot1 : snapshot.getChildren()){
                                holder.lastMessage.setText(snapshot1.child("message").getValue().toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // Ab hum iska userName , image UsersAdapter m backend krege:-
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context , ChatDetailActivity.class);
                intent.putExtra("userId" , users.getUserId());
                intent.putExtra("profilePic" , users.getProfilepic());
                intent.putExtra("userName" , users.getUserName());
                context.startActivity(intent);
                
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // Yha hum Apne View ko Initialize karva dege :-
        ImageView image;
        TextView userName , lastMessage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.userNamelist);
            lastMessage = itemView.findViewById(R.id.lastMessage);
        }
    }
}
