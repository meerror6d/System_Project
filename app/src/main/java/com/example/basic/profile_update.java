package com.example.basic;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.basic.databinding.ProfileUpdateBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile_update extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView PImageView;

    ProfileUpdateBinding Pbinding;
    String nid;
    String tin;
    String balance;
    String user;
    long Pmaxid = 0;
    FirebaseDatabase Pdb;
    DatabaseReference Preference;
    StorageReference PstorageRef;
    String userImg;
    CircleImageView profileImageView;


    private Uri PImageUri;
    private StorageTask PUploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Pbinding = ProfileUpdateBinding.inflate(getLayoutInflater());
        setContentView(Pbinding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar_up);

        // Back icon click listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed(); // This will act as the back button press
            }
        });

        //Pchose = findViewById(R.id.choose);
        profileImageView = findViewById(R.id.profile_image);
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the gallery to select an image
                openGallery();
            }
        });


        PstorageRef = FirebaseStorage.getInstance().getReference("Profiles");


        Pbinding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nid = Pbinding.receiver.getText().toString();
                tin = Pbinding.landno.getText().toString();
                balance = Pbinding.signature.getText().toString();
                //user = Pbinding.signature2.getText().toString();



                DatabaseReference queueReference = FirebaseDatabase.getInstance().getReference("User");

                queueReference.orderByChild("nid").equalTo(nid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                String username = userSnapshot.child("uniquename").getValue(String.class);

                                //Toast.makeText(profile_update.this, "username"+ username, Toast.LENGTH_SHORT).show();
                                queueReference.child(username).child("Tin").setValue(tin);
                                queueReference.child(username).child("Balance").setValue(balance);
                                //queueReference.child(username).child("Name").setValue(user);

                                uploadFile();
                            }
                        } else {
                            Toast.makeText(profile_update.this, "Nid is not matched!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Firebase Error", "Data retrieval failed: " + databaseError.getMessage());
                    }
                });

            }
        });

    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    // Handle the result when an image is selected from the gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            PImageUri = data.getData();

            // Now, you can set the selected image to the CircleImageView
            Picasso.get().load(PImageUri).into(profileImageView);
        }
    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (PImageUri != null) {

            DatabaseReference queueReference = FirebaseDatabase.getInstance().getReference("User");

            queueReference.orderByChild("nid").equalTo(nid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String username = userSnapshot.child("uniquename").getValue(String.class);

                            StorageReference fileReference = PstorageRef.child(username).child(System.currentTimeMillis()
                                    + "." + getFileExtension(PImageUri));

                            PUploadTask = fileReference.putFile(PImageUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            if (!nid.isEmpty() && !tin.isEmpty() && !balance.isEmpty()) {

                                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        userImg = uri.toString();

                                                        queueReference.child(username).child("URL").setValue(userImg);
                                                        Toast.makeText(profile_update.this,"URL"+ userImg, Toast.LENGTH_SHORT).show();

                                                        // Inform the user after the database update
                                                        Toast.makeText(profile_update.this, "PROFILE INFORMATION ENTERED!", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(profile_update.this, LoginActivity.class));
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(profile_update.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(profile_update.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(profile_update.this, "No file selected", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            Toast.makeText(profile_update.this, "Nid is not matched!", Toast.LENGTH_SHORT).show();
        }

    }

}