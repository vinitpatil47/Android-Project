package com.example.vinit.farmzone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class EditProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText user_name,user_email,user_mobile;
    private Button save;
    private ImageButton profilebutton;
    private ImageView profileimage;
    private BottomSheetDialog bottomSheetDialog;
    private LinearLayout gallery,camera,remove;
    private final static int GALLERY = 2;

    private String name,email,mobile,profileurl,who;

    private FirebaseAuth mAuth;
    DatabaseReference mdatabase;
    
    private ProgressDialog progressDialog;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Update Profile");

        user_name = (EditText)findViewById(R.id.name);
        user_email = (EditText)findViewById(R.id.email);
        user_mobile = (EditText)findViewById(R.id.mobile);
        profilebutton = (ImageButton) findViewById(R.id.profilebutton);
        profileimage = (ImageView) findViewById(R.id.profileimage);
        save = (Button)findViewById(R.id.save);




        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        mobile = getIntent().getStringExtra("mobile");
        profileurl = getIntent().getStringExtra("url");
        who = getIntent().getStringExtra("who");

        mAuth = FirebaseAuth.getInstance();
        mdatabase = FirebaseDatabase.getInstance().getReference(who).child(mAuth.getCurrentUser().getUid());
        progressDialog = new ProgressDialog(this);

        user_name.setText(name);
        user_email.setText(email);
        user_mobile.setText(mobile);
        if(profileurl != null)
            Picasso.get().load(profileurl).placeholder(R.drawable.ic_profileperson_black_24dp).transform(new CircleTransform()).centerCrop().resize(100,100).into(profileimage);
        else
            profileimage.setImageResource(R.drawable.ic_profileperson_black_24dp);
        setbottomdialog();

        profilebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Updating Profile");
                progressDialog.setCanceledOnTouchOutside(false);
                updatedatabase();
            }
        });

    }

    private void updatedatabase()
    {
        if(validate())
        {
            progressDialog.show();

            if (uri != null) {
                final StorageReference upload = FirebaseStorage.getInstance().getReference().child(mAuth.getCurrentUser().getUid()).child("Profile Picture").child("profile_picture.jpeg");
                final UploadTask uploadTask = upload.putFile(uri);

                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return upload.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            mdatabase.child("profile_url").setValue(downloadUri.toString());


                            if (!(user_email.getText().toString().equals(email))) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                user.updateEmail(user_email.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    mdatabase.child("email_id").setValue(user_email.getText().toString());
                                                    if (!(user_name.getText().toString().equals(name)))
                                                        mdatabase.child("name").setValue(user_name.getText().toString());

                                                    if (!(user_mobile.getText().toString().equals(mobile)))
                                                        mdatabase.child("mobile_no").setValue(user_mobile.getText().toString());

                                                    Intent i = new Intent(EditProfileActivity.this, ProfileActivity.class);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    progressDialog.dismiss();
                                                    i.putExtra("who",who);
                                                    startActivity(i);
                                                    Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                                }
                                                else { }
                                            }
                                        });
                            }
                            else {

                                if (!(user_name.getText().toString().equals(name)))
                                    mdatabase.child("name").setValue(user_name.getText().toString());

                                if (!(user_mobile.getText().toString().equals(mobile)))
                                    mdatabase.child("mobile_no").setValue(user_mobile.getText().toString());

                                Intent i = new Intent(EditProfileActivity.this, ProfileActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                progressDialog.dismiss();
                                i.putExtra("who",who);
                                startActivity(i);
                                Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                        }
                    }
                });
            }
            else if (!(user_email.getText().toString().equals(email))) {
                FirebaseUser user = mAuth.getCurrentUser();
                user.updateEmail(user_email.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mdatabase.child("email_id").setValue(user_email.getText().toString());
                                    if (!(user_name.getText().toString().equals(name)))
                                        mdatabase.child("name").setValue(user_name.getText().toString());

                                    if (!(user_mobile.getText().toString().equals(mobile)))
                                        mdatabase.child("mobile_no").setValue(user_mobile.getText().toString());

                                    Intent i = new Intent(EditProfileActivity.this, ProfileActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    progressDialog.dismiss();
                                    i.putExtra("who",who);
                                    startActivity(i);
                                    Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    progressDialog.dismiss();
                                    Toast.makeText(EditProfileActivity.this,"Unable",Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
            else {
                if (!(user_name.getText().toString().equals(name)))
                    mdatabase.child("name").setValue(user_name.getText().toString());

                if (!(user_mobile.getText().toString().equals(mobile)))
                    mdatabase.child("mobile_no").setValue(user_mobile.getText().toString());

                Intent in = new Intent(EditProfileActivity.this,ProfileActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                progressDialog.dismiss();
                in.putExtra("who",who);
                startActivity(in);
                Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setbottomdialog()
    {
        if(bottomSheetDialog == null)
        {
            View view = LayoutInflater.from(this).inflate(R.layout.bottomdialogsheet,null);
            gallery = view.findViewById(R.id.layout_gallery);
            camera = view.findViewById(R.id.layout_camera);
            remove = view.findViewById(R.id.layout_remove);

            bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(view);
        }

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/");
                startActivityForResult(i,GALLERY);
                bottomSheetDialog.dismiss();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent in = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(in,GALLERY);
                bottomSheetDialog.dismiss();
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(who).child(mAuth.getCurrentUser().getUid());
                databaseReference.child("profile_url").setValue(null);
                profileimage.setImageResource(R.drawable.ic_profileperson_black_24dp);
                Toast.makeText(EditProfileActivity.this,"Profile Picture Deleted",Toast.LENGTH_SHORT).show();

                StorageReference storageReference = FirebaseStorage.getInstance().getReference(mAuth.getCurrentUser().getUid()).child("Profile Picture");
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditProfileActivity.this,"Deleted",Toast.LENGTH_SHORT).show();
                    }
                });
                bottomSheetDialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY && resultCode == RESULT_OK && data!=null)
        {
            uri = data.getData();
            Picasso.get().load(uri).transform(new CircleTransform()).centerCrop().resize(100,100).into(profileimage);
        }

    }

    private boolean validate()
    {
        boolean val = true;
        if(TextUtils.isEmpty(user_name.getText().toString()))
        {
            user_name.setError("Name is Required");
            val = false;
        }
        if(TextUtils.isEmpty(user_email.getText().toString()))
        {
            user_email.setError("Email Id is Required");
            val = false;
        }
        if(TextUtils.isEmpty(user_mobile.getText().toString()))
        {
            user_mobile.setError("Mobile no. is Required");
            val = false;
        }
        return val;
    }
}
