package com.example.vinit.farmzone;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ChatFragment extends Fragment {
    public ChatFragment() { }

    private RelativeLayout no_chat,large_add,chat,small_add;
    private String who;
    private RecyclerView user_chat;

    private FirebaseAuth mAuth;
    private DatabaseReference mref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        who = getArguments().getString("who");
        no_chat = (RelativeLayout)view.findViewById(R.id.no_chat);
        chat = (RelativeLayout)view.findViewById(R.id.chat);
        large_add = (RelativeLayout)view.findViewById(R.id.large_addchat);
        small_add = (RelativeLayout)view.findViewById(R.id.small_addchat);

        user_chat = (RecyclerView)view.findViewById(R.id.chat_user);
        user_chat.setHasFixedSize(true);
        user_chat.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAuth = FirebaseAuth.getInstance();
        mref = FirebaseDatabase.getInstance().getReference("messages");


        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.hasChild(mAuth.getCurrentUser().getUid()))
                {
                    chat.setVisibility(View.VISIBLE);
                    no_chat.setVisibility(View.GONE);
                }
                else {
                    chat.setVisibility(View.GONE);
                    no_chat.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        large_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(),SelectUserActivity.class);
                i.putExtra("who",who);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        small_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(),SelectUserActivity.class);
                i.putExtra("who",who);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        fun();
    }

    private void fun()
    {
        Query query = FirebaseDatabase.getInstance().getReference("chat").child(mAuth.getCurrentUser().getUid()).orderByChild("conversation").equalTo("yes");

        FirebaseRecyclerAdapter<Uid,userviewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Uid, userviewholder>(
                Uid.class,
                R.layout.user_row,
                userviewholder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(final userviewholder viewHolder, final Uid model, int position)
            {
                viewHolder.setuser(model.getUid(),who);

                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(getActivity(),ChatActivity.class);
                        i.putExtra("name",viewHolder.user_name);
                        i.putExtra("who",who);
                        i.putExtra("uid",model.getUid());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                });
            }
        };
        user_chat.setAdapter(firebaseRecyclerAdapter);
    }

    private static class userviewholder extends RecyclerView.ViewHolder {

        View mview;
        String user_name;

        public userviewholder(@NonNull View itemView) {
            super(itemView);
            mview = itemView;
        }

        public void setuser(String uid, String who)
        {
            DatabaseReference databaseReference;
            if(who.equals("Renter"))
                databaseReference = FirebaseDatabase.getInstance().getReference("Provider").child(uid);
            else
                databaseReference = FirebaseDatabase.getInstance().getReference("Renter").child(uid);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    TextView name = (TextView)mview.findViewById(R.id.name);
                    ImageView dp = (ImageView)mview.findViewById(R.id.dp);
                    name.setText(dataSnapshot.child("name").getValue().toString());
                    user_name = dataSnapshot.child("name").getValue().toString();
                    if(dataSnapshot.child("profile_url").getValue() != null)
                        Picasso.get().load(dataSnapshot.child("profile_url").getValue().toString()).placeholder(R.drawable.ic_account_circle_black_24dp).transform(new CircleTransform()).centerCrop().resize(75,75).into(dp);
                    else
                        dp.setImageResource(R.drawable.ic_account_circle_black_24dp);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
