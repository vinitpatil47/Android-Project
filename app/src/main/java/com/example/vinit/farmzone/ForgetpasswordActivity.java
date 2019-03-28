package com.example.vinit.farmzone;

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
import com.google.firebase.auth.FirebaseAuth;

public class ForgetpasswordActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText reset_email;
    private Button send;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reset Password");

        reset_email = (EditText)findViewById(R.id.reset_email);
        send =  (Button)findViewById(R.id.sent_email);
        mAuth = FirebaseAuth.getInstance();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(validate())
                    sendemail();
            }
        });
    }

    private boolean validate()
    {
        boolean val=true;
        if(TextUtils.isEmpty(reset_email.getText().toString())) {
            reset_email.setError("Enter Valid Email_id");
            val = false;
        }
        return val;
    }

    private void sendemail()
    {
        String emailAddress = reset_email.getText().toString().trim();

        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(ForgetpasswordActivity.this,"Email Sent",Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(ForgetpasswordActivity.this,SelectorActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                        else {
                            Toast.makeText(ForgetpasswordActivity.this,"Something wents Wrong...Please try again.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
