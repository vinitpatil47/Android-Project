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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText name,email,password,mobile;
    private Button sign_in;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference mref;
    private String who;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        who = getIntent().getStringExtra("who");

        name = (EditText)findViewById(R.id.name);
        email = (EditText)findViewById(R.id.emil_id);
        password = (EditText)findViewById(R.id.password);
        mobile = (EditText)findViewById(R.id.mobile_no);
        sign_in = (Button)findViewById(R.id.sign_in);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing In....");
        progressDialog.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    progressDialog.show();
                    createuser();
                }
            }
        });


    }

    private void createuser() {

        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            savedatabase();
                            if(who.equals("Renter")) {
                                Intent i = new Intent(RegisterActivity.this, RenterHomeActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else {
                                Intent i = new Intent(RegisterActivity.this, ProviderHomeActivity.class);
                                startActivity(i);
                                finish();
                            }

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this,"Sign In Failed...Please Try Again.",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private boolean validate()
    {
        boolean val = true;
        if(TextUtils.isEmpty(name.getText().toString()))
        {
            name.setError("User Name is Required");
            val=false;
        }
        if(TextUtils.isEmpty(email.getText().toString()))
        {
            email.setError("User Email Id is Required");
            val=false;
        }
        if(TextUtils.isEmpty(password.getText().toString()))
        {
            password.setError("Password is Required");
            val=false;
        }
        if(TextUtils.isEmpty(mobile.getText().toString()))
        {
            mobile.setError("Mobile No. is Required");
            val=false;
        }
        return val;
    }

    private void savedatabase() {
        mref = FirebaseDatabase.getInstance().getReference(who).child(mAuth.getCurrentUser().getUid());
        User user = new User(name.getText().toString(),email.getText().toString(),mobile.getText().toString(),null,FirebaseInstanceId.getInstance().getToken(),null,null);
        mref.setValue(user);
    }

}
