package com.example.vinit.farmzone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import android.widget.RelativeLayout.LayoutParams;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String name,who,uid;
    private TextView message;
    private ImageView send_message;
    private RecyclerView user_chat;

    private FirebaseAuth mAuth;
    private DatabaseReference mref;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter<Messege,messageviewholder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        who = getIntent().getStringExtra("who");
        name = getIntent().getStringExtra("name");
        uid = getIntent().getStringExtra("uid");

        linearLayoutManager = new LinearLayoutManager(this);
        user_chat = (RecyclerView)findViewById(R.id.chat_user);
        user_chat.setHasFixedSize(true);
        user_chat.setLayoutManager(linearLayoutManager);
        user_chat.setNestedScrollingEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        mref = FirebaseDatabase.getInstance().getReference("messages");
        //linearLayoutManager.setReverseLayout(true);

        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View custom_action_bar = layoutInflater.inflate(R.layout.chat_custom_bar,null);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle(null);
        ActionBar actionBar = getSupportActionBar();
        TextView user_name = (TextView)custom_action_bar.findViewById(R.id.name);
        user_name.setText(name);
        actionBar.setCustomView(custom_action_bar);

        message = (TextView)findViewById(R.id.message);
        send_message = (ImageView)findViewById(R.id.send_msg);

        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(message.getText().toString()))
                {
                    final String pushkey = mref.push().getKey();
                    final String mess = message.getText().toString();
                    mref.child(mAuth.getCurrentUser().getUid()).child(uid).child(pushkey).child("to").setValue(uid);
                    mref.child(mAuth.getCurrentUser().getUid()).child(uid).child(pushkey).child("from").setValue(mAuth.getCurrentUser().getUid());
                    mref.child(mAuth.getCurrentUser().getUid()).child(uid).child(pushkey).child("time").setValue(ServerValue.TIMESTAMP);
                    mref.child(mAuth.getCurrentUser().getUid()).child(uid).child(pushkey).child("message").setValue(mess).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            mref.child(uid).child(mAuth.getCurrentUser().getUid()).child(pushkey).child("to").setValue(uid);
                            mref.child(uid).child(mAuth.getCurrentUser().getUid()).child(pushkey).child("from").setValue(mAuth.getCurrentUser().getUid());
                            mref.child(uid).child(mAuth.getCurrentUser().getUid()).child(pushkey).child("time").setValue(ServerValue.TIMESTAMP);
                            mref.child(uid).child(mAuth.getCurrentUser().getUid()).child(pushkey).child("message").setValue(mess);

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chat");
                            ref.child(mAuth.getCurrentUser().getUid()).child(uid).child("conversation").setValue("yes");
                            ref.child(uid).child(mAuth.getCurrentUser().getUid()).child("conversation").setValue("yes");

                            message.setText(null);
                            firebaseRecyclerAdapter.notifyDataSetChanged();
                        }
                    });

                }
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
        Query query = FirebaseDatabase.getInstance().getReference("messages").child(mAuth.getCurrentUser().getUid()).child(uid);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Messege, messageviewholder>(
                Messege.class,
                R.layout.message_row,
                messageviewholder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(messageviewholder viewHolder, Messege model, int position)
            {
                viewHolder.display_message(model,mAuth.getCurrentUser().getUid());
            }
        };
        user_chat.setAdapter(firebaseRecyclerAdapter);
        user_chat.scrollToPosition(20);


    }

    private static class messageviewholder extends RecyclerView.ViewHolder {

        View mview;

        public messageviewholder(@NonNull View itemView) {
            super(itemView);
            mview = itemView;
        }

        public void display_message(Messege msg,String current_user)
        {
            TextView text = (TextView)mview.findViewById(R.id.msg);
            text.setText(msg.getMessage());
            LayoutParams params = (RelativeLayout.LayoutParams)text.getLayoutParams();
            if (msg.getFrom().equals(current_user)) {
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                text.setLayoutParams(params);
                //text.setBackgroundResource(R.drawable.sender_addchat);
            } else {
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                text.setLayoutParams(params);
                //text.setBackgroundResource(R.drawable.addchat);
            }


            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            text.setLayoutParams(params);
        }
    }


        @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i;
        if(who.equals("Renter"))
            i = new Intent(ChatActivity.this,RenterHomeActivity.class);
        else
            i = new Intent(ChatActivity.this,ProviderHomeActivity.class);
        i.putExtra("fragment","chatFragment");
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
}
