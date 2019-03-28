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

public class RenterRequestFragment extends Fragment {
    public RenterRequestFragment() { }

    private String who;
    private LinearLayout no_request;
    private RecyclerView requested_equi;
    private TextView changetext;

    private FirebaseAuth mAuth;
    private DatabaseReference mref;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_renter_request, container, false);

        who = getArguments().getString("who");
        no_request = (LinearLayout)view.findViewById(R.id.no_request);
        changetext = (TextView)view.findViewById(R.id.text);

        requested_equi = (RecyclerView)view.findViewById(R.id.requested_equi_row);
        requested_equi.setHasFixedSize(true);
        requested_equi.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAuth = FirebaseAuth.getInstance();
        mref = FirebaseDatabase.getInstance().getReference(who).child(mAuth.getCurrentUser().getUid());

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(!dataSnapshot.hasChild("requested_equipment")) {
                    no_request.setVisibility(View.VISIBLE);
                    if(who.equals("Renter"))
                        changetext.setText("You have not requested any Equipment Currently");
                    else
                        changetext.setText("You don't have any Request to confirm");
                    requested_equi.setVisibility(View.GONE);
                }
                else {
                        no_request.setVisibility(View.GONE);
                        requested_equi.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }

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
        Query query = FirebaseDatabase.getInstance().getReference("Provider").child(mAuth.getCurrentUser().getUid()).child("requested_equipment");

        FirebaseRecyclerAdapter<Uid,equiviewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Uid, equiviewholder>(
                Uid.class,
                R.layout.equipment_row,
                equiviewholder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(equiviewholder viewHolder, final Uid model, int position)
            {
                viewHolder.set_provider_request_row(model.getEqui_key(),model.getUid());

                viewHolder.reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        databaseReference = FirebaseDatabase.getInstance().getReference("Renter").child(model.getUid()).child("requested_equipment");

                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                                {
                                    if(snapshot.child("equi_key").getValue()!=null && snapshot.child("equi_key").getValue().equals(model.getEqui_key()))
                                    {
                                        databaseReference.child(snapshot.getKey()).child("equi_key").setValue(null);
                                        databaseReference.child(snapshot.getKey()).child("uid").setValue(null);

                                        databaseReference = FirebaseDatabase.getInstance().getReference("Provider").child(mAuth.getCurrentUser().getUid()).child("requested_equipment").child(snapshot.getKey());
                                        databaseReference.child("equi_key").setValue(null);
                                        databaseReference.child("uid").setValue(null);
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

                viewHolder.accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        databaseReference = FirebaseDatabase.getInstance().getReference("Renter").child(model.getUid()).child("requested_equipment");

                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                String k=null;
                                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                                {
                                    if(snapshot.child("equi_key").getValue()!=null && snapshot.child("equi_key").getValue().equals(model.getEqui_key()))
                                    {
                                        k=snapshot.getKey();

                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Renter").child(model.getUid()).child("accepted_request");
                                        Uid u = snapshot.getValue(Uid.class);
                                        u.setStatus("Not Completed");
                                        ref.child(snapshot.getKey()).setValue(u);

                                        databaseReference.child(snapshot.getKey()).child("equi_key").setValue(null);
                                        databaseReference.child(snapshot.getKey()).child("uid").setValue(null);

                                        ref = FirebaseDatabase.getInstance().getReference("Provider").child(mAuth.getCurrentUser().getUid()).child("accepted_request");
                                        u = snapshot.getValue(Uid.class);
                                        u.setUid(model.getUid());
                                        u.setStatus("Not Completed");
                                        ref.child(snapshot.getKey()).setValue(u);

                                        databaseReference = FirebaseDatabase.getInstance().getReference("Provider").child(mAuth.getCurrentUser().getUid()).child("requested_equipment").child(snapshot.getKey());
                                        databaseReference.child("equi_key").setValue(null);
                                        databaseReference.child("uid").setValue(null);

                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chat");
                                        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                            {
                                                if(!dataSnapshot.child(mAuth.getCurrentUser().getUid()).child(model.getUid()).exists()) {
                                                    databaseReference = FirebaseDatabase.getInstance().getReference("chat");
                                                    databaseReference.child(mAuth.getCurrentUser().getUid()).child(model.getUid()).child("uid").setValue(model.getUid());
                                                    databaseReference.child(mAuth.getCurrentUser().getUid()).child(model.getUid()).child("conversation").setValue("no");
                                                    databaseReference.child(model.getUid()).child(mAuth.getCurrentUser().getUid()).child("uid").setValue(mAuth.getCurrentUser().getUid());
                                                    databaseReference.child(model.getUid()).child(mAuth.getCurrentUser().getUid()).child("conversation").setValue("no");
                                                }
                                                else {

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        };

        requested_equi.setAdapter(firebaseRecyclerAdapter);
    }

    private void renter_fun()
    {
        Query query = FirebaseDatabase.getInstance().getReference("Renter").child(mAuth.getCurrentUser().getUid()).child("requested_equipment");

        FirebaseRecyclerAdapter<Uid,equiviewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Uid, equiviewholder>(
                Uid.class,
                R.layout.equipment_row,
                equiviewholder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(equiviewholder viewHolder, final Uid model, int position)
            {
                viewHolder.set_renter_request_row(model.getEqui_key());

                viewHolder.cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        databaseReference = FirebaseDatabase.getInstance().getReference("Renter").child(mAuth.getCurrentUser().getUid()).child("requested_equipment");
                        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                for(final DataSnapshot snapshot: dataSnapshot.getChildren())
                                {
                                    if(snapshot.child("equi_key").getValue().equals(model.getEqui_key()))
                                    {
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Provider").child(model.getUid()).child("requested_equipment").child(snapshot.getKey());
                                        ref.setValue(null);

                                        ref = FirebaseDatabase.getInstance().getReference("Renter").child(mAuth.getCurrentUser().getUid()).child("requested_equipment").child(snapshot.getKey());
                                        ref.setValue(null);
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        };

        requested_equi.setAdapter(firebaseRecyclerAdapter);
    }

    private static class equiviewholder extends RecyclerView.ViewHolder {

        View mview;
        Button cancel,reject,accept;
        RelativeLayout approve_request;
        TextView provider_name;

        public equiviewholder(@NonNull View itemView) {
            super(itemView);
            mview = itemView;
            cancel = (Button)mview.findViewById(R.id.cancel_request);
            reject = (Button)mview.findViewById(R.id.reject);
            accept = (Button)mview.findViewById(R.id.accept);
            provider_name = (TextView)mview.findViewById(R.id.provider_name);
            approve_request = (RelativeLayout) mview.findViewById(R.id.approve_request);

            provider_name.setVisibility(View.VISIBLE);
        }

        public void set_renter_request_row(String key)
        {
            cancel.setVisibility(View.VISIBLE);
            approve_request.setVisibility(View.GONE);
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Equipment").child(key);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Equipment equipment = dataSnapshot.getValue(Equipment.class);

                    TextView equi_name = (TextView) mview.findViewById(R.id.equi_name);
                    TextView equi_rent = (TextView) mview.findViewById(R.id.equi_rent);
                    ImageView equi_pic = (ImageView) mview.findViewById(R.id.equi_pic);

                    equi_name.setText(equipment.getType());
                    equi_rent.setText(equipment.getPrice());

                    DatabaseReference mref = FirebaseDatabase.getInstance().getReference("Provider").child(equipment.getProvider_uid());
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

        public void set_provider_request_row(String key,final String uid)
        {
            if(key!=null) {
                cancel.setVisibility(View.GONE);
                approve_request.setVisibility(View.VISIBLE);

                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Equipment").child(key);

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Equipment equipment = dataSnapshot.getValue(Equipment.class);

                        TextView equi_name = (TextView) mview.findViewById(R.id.equi_name);
                        TextView equi_rent = (TextView) mview.findViewById(R.id.equi_rent);
                        ImageView equi_pic = (ImageView) mview.findViewById(R.id.equi_pic);

                        equi_name.setText(equipment.getType());
                        equi_rent.setText(equipment.getPrice());

                        DatabaseReference mref = FirebaseDatabase.getInstance().getReference("Renter").child(uid);
                        mref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(valueEventListener!=null)
            databaseReference.removeEventListener(valueEventListener);
    }
}
