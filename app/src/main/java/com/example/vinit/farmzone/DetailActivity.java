package com.example.vinit.farmzone;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String who,key,provider_uid;

    private FirebaseAuth mAuth;
    private DatabaseReference mref;

    private TextView provider_name,provider_mobile,price,equi_name,equi_description;
    private Button send_request;
    private ImageView equi_pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        who = getIntent().getStringExtra("who");
        key = getIntent().getStringExtra("key");

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mref = FirebaseDatabase.getInstance().getReference("Equipment").child(key);

        provider_name = (TextView)findViewById(R.id.provider_name);
        provider_mobile = (TextView)findViewById(R.id.provider_mobile);
        price = (TextView)findViewById(R.id.equi_price);
        equi_name = (TextView)findViewById(R.id.equi_name);
        equi_description = (TextView) findViewById(R.id.equi_desciption);
        send_request = (Button)findViewById(R.id.send_request);
        equi_pic = (ImageView)findViewById(R.id.equi_pic);

        if(who.equals("Provider"))
            send_request.setVisibility(View.GONE);
        else {
            mref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.child("request").hasChild(mAuth.getCurrentUser().getUid()))
                    {
                        send_request.setText("Request_sent");
                        send_request.setBackgroundColor(Color.GREEN);
                    }
                    send_request.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Equipment equipment = dataSnapshot.getValue(Equipment.class);

                equi_name.setText(equipment.getType());
                getSupportActionBar().setTitle(equipment.getType());
                price.setText(equipment.getPrice());
                equi_description.setText(equipment.getDescription());

                if(!equipment.getStatus().equals("available"))
                {
                    send_request.setText("Not Available");
                    send_request.setEnabled(false);
                }
                else {

                    send_request.setText("Send Request");
                    send_request.setEnabled(true);

                }

                if(equipment.getEqui_url() != null)
                    Picasso.get().load(equipment.getEqui_url()).resize(364,230).into(equi_pic);

                provider_uid = equipment.getProvider_uid();
                setuser(provider_uid);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
    }

    private void send()
    {
        send_request.setText("Request Sent");
        send_request.setBackgroundColor(Color.GREEN);
        send_request.setEnabled(false);

        mref.child("requests").child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());

        DatabaseReference Reference = FirebaseDatabase.getInstance().getReference("Renter").child(mAuth.getCurrentUser().getUid()).child("requested_equipment");
        String pushkey = Reference.push().getKey();
        Reference.child(pushkey).child("equi_key").setValue(key);
        Reference.child(pushkey).child("uid").setValue(provider_uid);

        Reference = FirebaseDatabase.getInstance().getReference("Provider").child(provider_uid).child("requested_equipment").child(pushkey);
        Reference.child("equi_key").setValue(key);
        Reference.child("uid").setValue(mAuth.getCurrentUser().getUid());
    }

    private void setuser(String provider_uid)
    {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Provider").child(provider_uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                provider_name.setText(dataSnapshot.child("name").getValue().toString());
                provider_mobile.setText(dataSnapshot.child("mobile_no").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}