package com.example.vinit.farmzone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AddEquipmentActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton imageButton,equipment_pic;
    private EditText description,price;
    private Button save;
    private Spinner equi_type;
    private final static int GALLERY = 2;

    private Uri uri;
    private String selected_equi,equi_key;

    private FirebaseAuth mAuth;
    private DatabaseReference mref;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_equipment);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Equipment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageButton = (ImageButton)findViewById(R.id.profilebutton);
        equipment_pic = (ImageButton)findViewById(R.id.equ_pic);
        description = (EditText)findViewById(R.id.equ_description);
        price = (EditText)findViewById(R.id.equ_price);
        save = (Button)findViewById(R.id.save);
        equi_type = (Spinner)findViewById(R.id.equi_type);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Equipment...");
        progressDialog.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();
        equi_key = FirebaseDatabase.getInstance().getReference("Equipment").push().getKey();
        mref = FirebaseDatabase.getInstance().getReference("Equipment").child(equi_key);


        String[] equi_name = new String[]{"Select Equiment Type","Tractor","Harvestor","Suckles"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,equi_name);
        equi_type.setAdapter(arrayAdapter);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/");
                startActivityForResult(i,GALLERY);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate())
                    save_equipment();
            }
        });

    }

    private void save_equipment()
    {
        progressDialog.show();
        if(uri != null)
        {
            final StorageReference upload = FirebaseStorage.getInstance().getReference().child(mAuth.getCurrentUser().getUid()).child("Equipments").child(uri.getLastPathSegment() + ".jpeg");
            UploadTask uploadTask = upload.putFile(uri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return upload.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        Equipment equipment = new Equipment(mAuth.getCurrentUser().getUid(),selected_equi,description.getText().toString(),price.getText().toString(),"available",downloadUri.toString());
                        mref.setValue(equipment);

                        mref = FirebaseDatabase.getInstance().getReference("Provider").child(mAuth.getCurrentUser().getUid()).child("uploaded_equipment").push().child("equi_key");
                        mref.setValue(equi_key);

                        progressDialog.dismiss();
                        Toast.makeText(AddEquipmentActivity.this,"Equipment Uploaded.",Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(AddEquipmentActivity.this,ProviderHomeActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);


                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(AddEquipmentActivity.this,"Something wents Wrong...Please try Again.",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            Equipment equipment = new Equipment(mAuth.getCurrentUser().getUid(),selected_equi,description.getText().toString(),price.getText().toString(),"available",null);
            mref.setValue(equipment);

            mref = FirebaseDatabase.getInstance().getReference("Provider").child(mAuth.getCurrentUser().getUid()).child("uploaded_equipment").push().child("equi_key");
            mref.setValue(equi_key);

            progressDialog.dismiss();
            Toast.makeText(AddEquipmentActivity.this,"Equipment Uploaded.",Toast.LENGTH_SHORT).show();

            Intent i = new Intent(AddEquipmentActivity.this,ProviderHomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY && resultCode == RESULT_OK && data != null)
        {
            uri = data.getData();
            Picasso.get().load(uri).resize(280,280).centerCrop().into(equipment_pic);
        }
    }

    private boolean validate()
    {
        boolean val=true;
        selected_equi = equi_type.getSelectedItem().toString();
        if(selected_equi == "Select Equiment Type")
        {
            Toast.makeText(AddEquipmentActivity.this,"Select Equipment Type",Toast.LENGTH_SHORT).show();
            val=false;
        }
        if(TextUtils.isEmpty(description.getText().toString()))
        {
            description.setError("Enter Description");
            val = false;
        }
        if(TextUtils.isEmpty(price.getText().toString()))
        {
            price.setError("Enter Valid Price Detail");
            val = false;
        }
        return val;
    }
}
