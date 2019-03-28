package com.example.vinit.farmzone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText email,password;
    private Button login;
    private TextView new_user,forget_password;
    private String who;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference mref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        who = getIntent().getStringExtra("who");

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(who + " Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        email = (EditText)findViewById(R.id.emil_id);
        password = (EditText)findViewById(R.id.password);
        login = (Button)findViewById(R.id.login);
        new_user = (TextView)findViewById(R.id.new_user);
        forget_password = (TextView)findViewById(R.id.forget_password);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging In....");
        progressDialog.setCanceledOnTouchOutside(false);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate())
                {
                    progressDialog.show();
                    loginuser();
                }
            }
        });

        new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
                i.putExtra("who",who);
                startActivity(i);
            }
        });

        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(LoginActivity.this, ForgetpasswordActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
    }

    private boolean validate()
    {
        boolean val = true;
        if(TextUtils.isEmpty(email.getText().toString()))
        {
            email.setError("Enter Email Address");
            val=false;
        }
        if(TextUtils.isEmpty(password.getText().toString()))
        {
            password.setError("Enter Password");
            val=false;
        }
        return val;
    }

    private void loginuser()
    {
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            checkuser();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this,"Login Failed...Please Try Again.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkuser()
    {
        mref = FirebaseDatabase.getInstance().getReference(who);

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(mAuth.getCurrentUser().getUid()))
                {
                    mref.child(mAuth.getCurrentUser().getUid()).child("device_token").setValue(FirebaseInstanceId.getInstance().getToken());
                    if(who.equals("Renter"))
                    {
                        progressDialog.dismiss();
                        Intent i = new Intent(LoginActivity.this, RenterHomeActivity.class);
                        i.putExtra("fragment","renterHomeFragment");
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                    else
                    {
                        progressDialog.dismiss();
                        Intent i = new Intent(LoginActivity.this, ProviderHomeActivity.class);
                        i.putExtra("fragment","renterHomeFragment");
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                }
                else {
                    progressDialog.dismiss();
                    mAuth.signOut();
                    Toast.makeText(LoginActivity.this, "You Have Entered in Wrong Activity", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
