package com.example.vinit.farmzone;


import android.content.Intent;
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

public class RenterHomeFragment extends Fragment {
    public RenterHomeFragment() { }

    private FirebaseAuth mAuth;
    private DatabaseReference mref;
    private String who;
    private LinearLayout no_equipment,no_uploaded_equi;
    private RecyclerView equipment_row;
    private ImageView add;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_renter_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        who = getArguments().getString("who");

        no_equipment = (LinearLayout)view.findViewById(R.id.no_equipment_row);
        no_uploaded_equi = (LinearLayout)view.findViewById(R.id.no_uploaded_equi);

        equipment_row = (RecyclerView)view.findViewById(R.id.equipment_row);
        equipment_row.setHasFixedSize(true);
        equipment_row.setLayoutManager(new LinearLayoutManager(getActivity()));
        add = (ImageView)view.findViewById(R.id.add);

        if(who.equals("Provider"))
        {
            mref = FirebaseDatabase.getInstance().getReference("Provider").child(mAuth.getCurrentUser().getUid());
            mref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.hasChild("uploaded_equipment"))
                    {
                        equipment_row.setVisibility(View.VISIBLE);
                        no_equipment.setVisibility(View.GONE);
                        no_uploaded_equi.setVisibility(View.GONE);
                    }
                    else {
                        equipment_row.setVisibility(View.GONE);
                        no_equipment.setVisibility(View.VISIBLE);
                        no_uploaded_equi.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
        {
            mref = FirebaseDatabase.getInstance().getReference("Equipment");
            mref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists())
                    {
                        equipment_row.setVisibility(View.VISIBLE);
                        no_equipment.setVisibility(View.GONE);
                        no_uploaded_equi.setVisibility(View.GONE);
                    }
                    else {
                        equipment_row.setVisibility(View.GONE);
                        no_equipment.setVisibility(View.GONE);
                        no_uploaded_equi.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),AddEquipmentActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(who.equals("Provider"))
            provider_fun();
        else
            renter_fun();
    }

    private void renter_fun()
    {
        Query query = FirebaseDatabase.getInstance().getReference("Equipment");

        FirebaseRecyclerAdapter<Equipment,equiviewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Equipment, equiviewholder>(
                Equipment.class,
                R.layout.equipment_row,
                equiviewholder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(equiviewholder viewHolder, Equipment model, final int position) {
                viewHolder.set_renter_row(model);

                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent i = new Intent(getActivity(),DetailActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra("who","Renter");
                        i.putExtra("key",getRef(position).getKey());
                        startActivity(i);
                    }
                });
            }
        };

        equipment_row.setAdapter(firebaseRecyclerAdapter);
    }

    private void provider_fun()
    {
        Query query = FirebaseDatabase.getInstance().getReference("Provider").child(mAuth.getCurrentUser().getUid()).child("uploaded_equipment");

        FirebaseRecyclerAdapter<Uid,equiviewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Uid, equiviewholder>(
                Uid.class,
                R.layout.equipment_row,
                equiviewholder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(equiviewholder viewHolder, final Uid model, int position) {

                viewHolder.set_provider_row(model.getEqui_key());

                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(),DetailActivity.class);
                        i.putExtra("who","Provider");
                        i.putExtra("key",model.getEqui_key());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                });

            }
        };

        equipment_row.setAdapter(firebaseRecyclerAdapter);

    }

    private static class equiviewholder extends RecyclerView.ViewHolder{

        View mview;
        Button cancel;
        RelativeLayout approve_request;
        TextView provider_name;

        public equiviewholder(@NonNull View itemView) {
            super(itemView);
            mview = itemView;
            cancel = (Button)mview.findViewById(R.id.cancel_request);
            approve_request = (RelativeLayout) mview.findViewById(R.id.approve_request);
            provider_name = (TextView)mview.findViewById(R.id.provider_name);
            cancel.setVisibility(View.GONE);
            approve_request.setVisibility(View.GONE);
        }

        public void set_provider_row(String key)
        {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Equipment").child(key);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Equipment equipment = dataSnapshot.getValue(Equipment.class);

                    TextView equi_name = (TextView)mview.findViewById(R.id.equi_name);
                    TextView equi_rent = (TextView)mview.findViewById(R.id.equi_rent);
                    TextView equi_status = (TextView)mview.findViewById(R.id.status);
                    provider_name.setVisibility(View.GONE);
                    ImageView equi_pic = (ImageView)mview.findViewById(R.id.equi_pic);


                    equi_name.setText(equipment.getType());
                    equi_rent.setText(equipment.getPrice());

                    if(equipment.getStatus().equals("available")) {
                        equi_status.setTextColor(Color.GREEN);
                        equi_status.setText(equipment.getStatus());
                    }
                    else {
                        equi_status.setTextColor(Color.RED);
                        equi_status.setText(equipment.getStatus());
                    }

                    if(equipment.getEqui_url() != null)
                        Picasso.get().load(equipment.getEqui_url()).resize(120,120).centerCrop().into(equi_pic);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public void set_renter_row(Equipment model)
        {
            TextView equi_name = (TextView)mview.findViewById(R.id.equi_name);
            TextView equi_rent = (TextView)mview.findViewById(R.id.equi_rent);
            final TextView equi_status = (TextView)mview.findViewById(R.id.status);
            provider_name.setVisibility(View.VISIBLE);
            ImageView equi_pic = (ImageView)mview.findViewById(R.id.equi_pic);

            equi_name.setText(model.getType());
            equi_rent.setText(model.getPrice());

            if(model.getStatus().equals("available")) {
                equi_status.setTextColor(Color.GREEN);
                equi_status.setText(model.getStatus());
            }
            else {
                equi_status.setTextColor(Color.RED);
                equi_status.setText(model.getStatus());
            }

            DatabaseReference mref = FirebaseDatabase.getInstance().getReference("Provider").child(model.getProvider_uid());
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

            if(model.getEqui_url() != null)
                Picasso.get().load(model.getEqui_url()).resize(120,120).centerCrop().into(equi_pic);
        }
    }

}


