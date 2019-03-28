package com.example.vinit.farmzone;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class RenterAcceptedFragment extends Fragment {
    public RenterAcceptedFragment() { }

    private LinearLayout no_request_accepeted;
    private RecyclerView accepted_request;
    private String who;
    private TextView changetext;

    private FirebaseAuth mAuth;
    private DatabaseReference mref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_renter_accepted, container, false);

        who = getArguments().getString("who");
        no_request_accepeted = (LinearLayout)view.findViewById(R.id.no_request_accepted);
        accepted_request = (RecyclerView)view.findViewById(R.id.accepted_request);
        accepted_request.setHasFixedSize(true);
        accepted_request.setLayoutManager(new LinearLayoutManager(getActivity()));
        changetext = (TextView)view.findViewById(R.id.text);

        mAuth = FirebaseAuth.getInstance();
        mref = FirebaseDatabase.getInstance().getReference(who).child(mAuth.getCurrentUser().getUid());

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(!dataSnapshot.hasChild("accepted_request"))
                {
                    no_request_accepeted.setVisibility(View.VISIBLE);
                    if(who.equals("Renter"))
                        changetext.setText("No Request Accepted  Currently");
                    else
                        changetext.setText("You Have not Accepted any Request");
                    accepted_request.setVisibility(View.GONE);
                }
                else {
                    no_request_accepeted.setVisibility(View.GONE);
                    accepted_request.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(who.equals("Renter"))
            renter_fun();
        else
            provider_fun();
    }

    private void provider_fun()
    {
        Query query = FirebaseDatabase.getInstance().getReference("Provider").child(mAuth.getCurrentUser().getUid()).child("accepted_request");

        FirebaseRecyclerAdapter<Uid,equiviewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Uid, equiviewholder>(
                Uid.class,
                R.layout.equipment_row,
                equiviewholder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(equiviewholder viewHolder, Uid model, int position)
            {
                viewHolder.set_provider_equipment(model.getEqui_key(),model.getUid(),model.getStatus());
            }
        };

        accepted_request.setAdapter(firebaseRecyclerAdapter);
    }

    private void renter_fun()
    {
        Query query = FirebaseDatabase.getInstance().getReference("Renter").child(mAuth.getCurrentUser().getUid()).child("accepted_request");

        FirebaseRecyclerAdapter<Uid,equiviewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Uid, equiviewholder>(
                Uid.class,
                R.layout.equipment_row,
                equiviewholder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(final equiviewholder viewHolder, final Uid model, final int position)
            {
                viewHolder.set_renter_equipment(model.getEqui_key(),model.getUid(),model.getStatus());

                viewHolder.cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        reference.child("Renter").child(mAuth.getCurrentUser().getUid()).child("accepted_request").child(getRef(position).getKey()).child("status").setValue("Completed");
                        reference.child("Provider").child(model.getUid()).child("accepted_request").child(getRef(position).getKey()).child("status").setValue("Completed");
                    }
                });
            }
        };

        accepted_request.setAdapter(firebaseRecyclerAdapter);
    }

    private static class equiviewholder extends RecyclerView.ViewHolder {

        View mview;
        Button cancel;
        RelativeLayout approve_request;
        TextView provider_name;

        public equiviewholder(@NonNull View itemView)
        {
            super(itemView);
            mview = itemView;
            cancel = (Button)mview.findViewById(R.id.cancel_request);
            provider_name = (TextView)mview.findViewById(R.id.provider_name);
            approve_request = (RelativeLayout) mview.findViewById(R.id.approve_request);
            approve_request.setVisibility(View.GONE);
            provider_name.setVisibility(View.VISIBLE);
        }

        public void set_provider_equipment(String key, final String uid, final String status)
        {
            cancel.setVisibility(View.GONE);
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Equipment").child(key);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    Equipment equipment = dataSnapshot.getValue(Equipment.class);

                    TextView equi_name = (TextView) mview.findViewById(R.id.equi_name);
                    TextView equi_rent = (TextView) mview.findViewById(R.id.equi_rent);
                    TextView equi_status = (TextView)mview.findViewById(R.id.status);
                    ImageView equi_pic = (ImageView) mview.findViewById(R.id.equi_pic);
                    equi_name.setText(equipment.getType());
                    equi_rent.setText(equipment.getPrice());
                    equi_status.setText(status);
                    if(status.equals("Not Completed"))
                        equi_status.setTextColor(Color.RED);
                    else
                        equi_status.setTextColor(Color.GREEN);

                    DatabaseReference mref = FirebaseDatabase.getInstance().getReference("Renter").child(uid);
                    mref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            provider_name.setText(dataSnapshot.child("name").getValue().toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    if (equipment.getEqui_url() != null)
                        Picasso.get().load(equipment.getEqui_url()).resize(120, 120).centerCrop().into(equi_pic);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public void set_renter_equipment(String key,final String uid,String status)
        {
            cancel.setVisibility(View.VISIBLE);
            if(status.equals("Not Completed"))
            {
                cancel.setText("Tap to Mark as Completed");
                cancel.setBackgroundColor(Color.RED);
            }
            else {
                cancel.setText("Completed");
                cancel.setBackgroundColor(Color.GREEN);
                cancel.setEnabled(false);
            }

            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Equipment").child(key);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    Equipment equipment = dataSnapshot.getValue(Equipment.class);

                    TextView equi_name = (TextView) mview.findViewById(R.id.equi_name);
                    TextView equi_rent = (TextView) mview.findViewById(R.id.equi_rent);
                    ImageView equi_pic = (ImageView) mview.findViewById(R.id.equi_pic);

                    equi_name.setText(equipment.getType());
                    equi_rent.setText(equipment.getPrice());

                    DatabaseReference mref = FirebaseDatabase.getInstance().getReference("Provider").child(uid);
                    mref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            provider_name.setText(dataSnapshot.child("name").getValue().toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    if (equipment.getEqui_url() != null)
                        Picasso.get().load(equipment.getEqui_url()).resize(120, 120).centerCrop().into(equi_pic);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
