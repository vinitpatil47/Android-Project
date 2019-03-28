package com.example.vinit.farmzone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SelectUserActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout no_request_accepted;
    private RecyclerView select_user;
    private String who;
    private TextView text;

    private FirebaseAuth mAuth;
    private DatabaseReference mref;
    private int i=0;

    private LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);

        who = getIntent().getStringExtra("who");

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select User");

        text = (TextView)findViewById(R.id.text);
        no_request_accepted = (LinearLayout)findViewById(R.id.no_request_accepted);
        select_user = (RecyclerView)findViewById(R.id.select_user);
        select_user.setHasFixedSize(true);
        select_user.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        mref = FirebaseDatabase.getInstance().getReference(who).child(mAuth.getCurrentUser().getUid());

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.hasChild("accepted_request"))
                {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chat").child(mAuth.getCurrentUser().getUid());
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            for(DataSnapshot snapshot: dataSnapshot.getChildren())
                            {
                                if(snapshot.child("conversation").getValue().equals("no"))
                                {
                                    select_user.setVisibility(View.VISIBLE);
                                    no_request_accepted.setVisibility(View.GONE);
                                    i=1;
                                    break;
                                }
                            }
                            if(i == 0)
                            {
                                select_user.setVisibility(View.GONE);
                                no_request_accepted.setVisibility(View.VISIBLE);
                                text.setText("No User to Select");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
                else {
                    select_user.setVisibility(View.GONE);
                    no_request_accepted.setVisibility(View.VISIBLE);
                    if(who.equals("Renter"))
                        text.setText("when any Provider accept your request, you can Select that Provider.");
                    else
                        text.setText("when You accept any request from Renter, you can Select that Renter.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        fun();
    }

    private void fun()
    {
        Query query = FirebaseDatabase.getInstance().getReference("chat").child(mAuth.getCurrentUser().getUid()).orderByChild("conversation").equalTo("no");

        final FirebaseRecyclerAdapter<Uid,userviewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Uid, userviewholder>(
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

                        Intent i = new Intent(SelectUserActivity.this,ChatActivity.class);
                        i.putExtra("name",viewHolder.user_name);
                        i.putExtra("who",who);
                        i.putExtra("uid",model.getUid());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                });
            }
        };

        select_user.setAdapter(firebaseRecyclerAdapter);

    }

    private static class userviewholder extends RecyclerView.ViewHolder {

        View mview;
        String user_name;

        public userviewholder(@NonNull View itemView)
        {
            super(itemView);
            mview = itemView;
        }

        public void setuser(String uid,String who)
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
                        Picasso.get().load(dataSnapshot.child("profile_url").getValue().toString()).placeholder(R.drawable.ic_account_circle_black_24dp).transform(new CircleTransform()).centerCrop().resize(80,80).into(dp);
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
