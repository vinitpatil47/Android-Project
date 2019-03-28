package com.example.vinit.farmzone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String who;

    private FirebaseAuth mAuth;
    private DatabaseReference mref;

    private TextView name,email,mobile;
    private ImageView profileimage;
    private Button edit;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        who = getIntent().getStringExtra("who");
        mAuth = FirebaseAuth.getInstance();
        mref = FirebaseDatabase.getInstance().getReference(who).child(mAuth.getCurrentUser().getUid());

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(who + " Profile");

        name = (TextView)findViewById(R.id.name);
        email = (TextView)findViewById(R.id.email);
        mobile = (TextView)findViewById(R.id.mobile);
        profileimage = (ImageView)findViewById(R.id.profileimage);
        edit = (Button)findViewById(R.id.edit);

        load();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this,EditProfileActivity.class);
                i.putExtra("name",name.getText().toString());
                i.putExtra("email",email.getText().toString());
                i.putExtra("mobile",mobile.getText().toString());
                i.putExtra("url",user.getProfile_url());
                i.putExtra("who",who);
                startActivity(i);
            }
        });
    }

    private void load()
    {

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                user = dataSnapshot.getValue(User.class);
                name.setText(user.getName());
                email.setText(user.getEmail_id());
                mobile.setText(user.getMobile_no());
                if(user.getProfile_url() != null)
                    Picasso.get().load(user.getProfile_url()).placeholder(R.drawable.ic_profileperson_black_24dp).transform(new CircleTransform()).centerCrop().resize(100,100).into(profileimage);
                else
                    profileimage.setImageResource(R.drawable.ic_profileperson_black_24dp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_logout)
        {
            mAuth.signOut();
            Intent i = new Intent(ProfileActivity.this, SelectorActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
