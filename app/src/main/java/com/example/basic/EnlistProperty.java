package com.example.basic;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import java.util.ArrayList;

public class EnlistProperty extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 123;

    private ImageView im1, im2, im3, im4, im5;
    private EditText ownershipTitle, dolil, prevOwner, tax,address,price, fees;
    private Button submit;
    private Uri selectedImageUri;
    private ImageView Clicked; // To track which image view was clicked
    private StorageTask mUploadTask;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_enlist_property);

        Toolbar toolbar = findViewById(R.id.toolbar_enlist);

        // Back icon click listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed(); // This will act as the back button press
            }
        });

        im1 = findViewById(R.id.image1);

        im1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Clicked = im1;
                selectImageFromGallery(v);
            }
        });

        // Initialize edit texts

        Spinner divisionSpinner = findViewById(R.id.divisionSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EnlistProperty.this, R.array.division_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        divisionSpinner.setAdapter(adapter);
        divisionSpinner.setPrompt("Select Division");

        ownershipTitle = findViewById(R.id.CurrentOwner);
        dolil = findViewById(R.id.dolil);
        prevOwner = findViewById(R.id.prevOwner);
        tax = findViewById(R.id.receipt);
        address=findViewById(R.id.address);
        price=findViewById(R.id.price);
        fees = findViewById(R.id.fee);

        String username = getIntent().getStringExtra("username");
        //Toast.makeText(EnlistProperty.this, "username  "+username, Toast.LENGTH_SHORT).show();
        //String title = ownershipTitle.getText().toString().trim();
        ownershipTitle.setText(username);

        price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                calculateFees(); // Call method to calculate fees whenever price changes
            }
        });

        // Initialize submit button
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForApproval();
                submit.setText("Submitted");
                Toast.makeText(EnlistProperty.this, "Submitted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateFees() {
        String priceValue = price.getText().toString().trim();

        if (!TextUtils.isEmpty(priceValue) && isValidNumber(priceValue)) {
            double priceAmount = Double.parseDouble(priceValue);
            double calculatedFees = priceAmount * 0.0001; // Calculate 0.1% of the price

            // Set the calculated fees to the fees EditText
            fees.setText(String.valueOf(calculatedFees));
        } else {
            fees.setText(""); // Clear fees if price is not a valid number or empty
        }
    }

    public void selectImageFromGallery(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            // Determine which image view was clicked and update the corresponding image URI
            im1.setImageURI(selectedImageUri);
        }
    }

    private void submitForApproval() {
        String prop_adress=address.getText().toString().trim();
        String title = ownershipTitle.getText().toString().trim();
        String uniqueDolilNo = dolil.getText().toString().trim();
        String previousOwner = prevOwner.getText().toString().trim();
        String propertyTaxReceipts = tax.getText().toString().trim();
        String prop_price=price.getText().toString().trim();
        String prop_fee = fees.getText().toString().trim();
        String selectedDivision = ((Spinner) findViewById(R.id.divisionSpinner)).getSelectedItem().toString();

        if (!isValidNumber(uniqueDolilNo) || !isValidNumber(propertyTaxReceipts) || !isValidNumber(prop_price)) {
            Toast.makeText(this, "Dolil number, Tax Receipt number, and Price must be numbers.", Toast.LENGTH_SHORT).show();
            return; // Stop further execution if there's an error
        }

        if (TextUtils.isEmpty(selectedDivision)) {
            // Display an error message if the division is not selected
            Toast.makeText(this, "Please select a division.", Toast.LENGTH_SHORT).show();
            return; // Stop further execution if there's an error
        }

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(uniqueDolilNo) ||
                TextUtils.isEmpty(previousOwner) || TextUtils.isEmpty(propertyTaxReceipts) ||
                TextUtils.isEmpty(prop_adress) || TextUtils.isEmpty(prop_price)) {
            // Display an error message to the user
            Toast.makeText(this, "All fields are mandatory. Please fill them all.", Toast.LENGTH_SHORT).show();
            return; // Stop further execution if there's an error
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Properties");
        String propertyId = databaseReference.push().getKey();

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("User");

        databaseReference1.child(title).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String balanceu = snapshot.child("Balance").getValue(String.class);


                    if (balanceu != null) {
                        double currentBalance = Double.parseDouble(balanceu);
                        double fee = Double.parseDouble(prop_fee);
                        if (currentBalance >= fee) {
                            // Decrease the user's balance
                            double newBalance = currentBalance - fee;
                            databaseReference1.child(title).child("Balance").setValue(String.valueOf(newBalance));
                            Toast.makeText(EnlistProperty.this, "balance deducted! ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EnlistProperty.this, "Insufficient balance", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });


        Property property = new Property(prop_adress, title, uniqueDolilNo, previousOwner, propertyTaxReceipts, prop_price, propertyId, selectedDivision,prop_fee);

        databaseReference.child(propertyId).setValue(property);
        //databaseReference.child(propertyId).setValue(prop_fee);


        if (selectedImageUri != null) {
            uploadImageToStorage(propertyId);
        }

        databaseReference1.child(title).child("Properties").child(propertyId).setValue(property);
        Intent intent = new Intent(this, datashow_user.class);
        intent.putExtra("propertyId", propertyId);
        startActivity(intent);

    }

    private void uploadImageToStorage(String propertyId) {

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("Properties");

        StorageReference fileReference = storageRef.child(propertyId).child(System.currentTimeMillis() + "." + getFileExtension(selectedImageUri));

        UploadTask uploadTask = fileReference.putFile(selectedImageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get the download URL
                Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Update the image URL in the property object and save it back to the database
                        DatabaseReference propertyRef = FirebaseDatabase.getInstance().getReference().child("Properties").child(propertyId);
                        propertyRef.child("PropImageUrl").setValue(uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EnlistProperty.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to validate if a given string is a number
    private boolean isValidNumber(String value) {
        try {
            // Try parsing the string to a double
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            // If parsing fails, it's not a valid number
            return false;
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}