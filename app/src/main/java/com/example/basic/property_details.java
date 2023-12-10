package com.example.basic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class property_details extends AppCompatActivity
{

    TextView prop_add, prop_dolil, prop_tax, current_owner, prev_owner, prop_price,prop_stat, prop_fees;
    TextView prop_add_id, prop_dolil_id, prop_tax_id, current_owner_id, prev_owner_id, prop_price_id,prop_status, prop_feesid;
    Button butn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_property_details);

        prop_add_id = findViewById(R.id.addressid);
        prop_dolil_id = findViewById(R.id.dolilid);
        prop_tax_id = findViewById(R.id.taxid);
        current_owner_id = findViewById(R.id.ownerid);
        prev_owner_id = findViewById(R.id.previd);
        prop_price_id = findViewById(R.id.priceid);
        prop_stat=findViewById(R.id.statusid);
        prop_feesid = findViewById(R.id.feesid);

        prop_add = findViewById(R.id.address_view);
        prop_dolil = findViewById(R.id.dolil_view);
        prop_tax = findViewById(R.id.receipt_view);
        current_owner = findViewById(R.id.Owner);
        prev_owner = findViewById(R.id.preOwner);
        prop_price = findViewById(R.id.prize);
        prop_status=findViewById(R.id.status);
        prop_fees = findViewById(R.id.fees);

        Toolbar toolbar = findViewById(R.id.toolbar_del);

        // Back icon click listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed(); // This will act as the back button press
            }
        });

        butn = findViewById(R.id.buy);

        String propertyId = getIntent().getStringExtra("ID");
        //String username = getIntent().getStringExtra("username");
        //Toast.makeText(property_details.this, "username  "+username, Toast.LENGTH_SHORT).show();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Properties").child(propertyId);

        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String address = dataSnapshot.child("address").getValue(String.class);
                    String dolil = dataSnapshot.child("dolilNo").getValue(String.class);
                    String taxReceipt = dataSnapshot.child("taxReceipts").getValue(String.class);
                    String  currentOwner = dataSnapshot.child("title").getValue(String.class);
                    String prevOwner = dataSnapshot.child("previousOwner").getValue(String.class);
                    String price = dataSnapshot.child("price").getValue(String.class);
                    String fee = dataSnapshot.child("fee").getValue(String.class);

                    // Set the retrieved data to the TextView widgets
                    prop_add.setText(address);
                    prop_dolil.setText(dolil);
                    prop_tax.setText(taxReceipt);
                    current_owner.setText( currentOwner);
                    prev_owner.setText(prevOwner);
                    prop_price.setText(price);
                    prop_fees.setText(fee);

                    String user = getIntent().getStringExtra("username");
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference("Admin");

                    database.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot adminSnapshot : snapshot.getChildren()) {
                                String adminUsername = adminSnapshot.child("username").getValue(String.class);

                                //Toast.makeText(property_details.this, "name: "+adminUsername, Toast.LENGTH_SHORT).show();

                                if (user.equals(adminUsername)) {
                                    // Disable the button
                                    butn.setEnabled(false);
                                    break; // No need to check further, exit the loop
                                } else {
                                    // Enable the button
                                    butn.setEnabled(true);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    if(currentOwner.equals(user))
                    {
                        //butn.setOnClickListener(null);
                        butn.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        butn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Read property details from your UI components
                                String address = prop_add.getText().toString();
                                String dolilNo = prop_dolil.getText().toString();
                                String taxReceiptNo = prop_tax.getText().toString();
                                String currentOwner = current_owner.getText().toString();
                                String previousOwner = prev_owner.getText().toString();
                                String price = prop_price.getText().toString();
                                String fee = prop_fees.getText().toString();
                                DatabaseReference queueReference = FirebaseDatabase.getInstance().getReference("queue");

                                //String currentOwner = current_owner.getText().toString();
                                String user = getIntent().getStringExtra("username");

                                //Toast.makeText(property_details.this, "current owner" + currentOwner , Toast.LENGTH_SHORT).show();
                                //Toast.makeText(property_details.this, "user"+ user , Toast.LENGTH_SHORT).show();


                                String propertyId = queueReference.push().getKey();
                                String username1 = getIntent().getStringExtra("username");
                                String childName = "queued";
                                prop_status.setText(childName);
                                Property property = new Property(address, currentOwner, dolilNo, previousOwner, taxReceiptNo, price, propertyId, "", fee);
                                queueReference.child(address).setValue(property);
                                queueReference.child(address).child("Status").setValue(childName);
                                queueReference.child(address).child("Buyer").setValue(username1);


                                //butn.setText("Submitted for approval!");
                                //butn.setOnClickListener(null);
                            }
                        });
                    }
                    DatabaseReference queueReference;
                    queueReference = FirebaseDatabase.getInstance().getReference("queue");
                    //DatabaseReference finall = FirebaseDatabase.getInstance().getReference("Transaction");
                    //String user = getIntent().getStringExtra("username");

                    queueReference.child(address).addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.exists())
                            {
                                String status = dataSnapshot.child("Status").getValue(String.class);
                                String name = dataSnapshot.child("Buyer").getValue(String.class);
                                String usern = dataSnapshot.child("title").getValue(String.class);
                                //Toast.makeText(property_details.this, "status "+ status, Toast.LENGTH_SHORT).show();

                                if (status != null)
                                {
                                    prop_status.setText(status);
                                    // Enable or disable the button based on the status
                                    if ("verified!".equals(status) && user.equals(name))
                                    {
                                        butn.setEnabled(true);
                                        butn.setText("Buy");

                                        butn.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {

                                                String username = getIntent().getStringExtra("username");
                                                //Toast.makeText(property_details.this, "username  "+username, Toast.LENGTH_SHORT).show();

                                                DatabaseReference userReference1 = FirebaseDatabase.getInstance().getReference().child("User").child(username);
                                                DatabaseReference userReference2 = FirebaseDatabase.getInstance().getReference().child("User").child(currentOwner);

                                                userReference1.addValueEventListener(new ValueEventListener()
                                                {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot1)
                                                    {
                                                        if (snapshot1.exists())
                                                        {
                                                            String balanceu = snapshot1.child("Balance").getValue(String.class);

                                                            if (balanceu != null)
                                                            {
                                                                double currentBalance = Double.parseDouble(balanceu);

                                                                double propertyPrice = Double.parseDouble(prop_price.getText().toString());

                                                                if (currentBalance >= propertyPrice)
                                                                {
                                                                    // Decrease the user's balance
                                                                    double newBalance = currentBalance - propertyPrice;
                                                                    userReference1.child("Balance").setValue(String.valueOf(newBalance));

                                                                    userReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                                            if (snapshot2.exists()) {
                                                                                String balanceUser2 = snapshot2.child("Balance").getValue(String.class);

                                                                                if (balanceUser2 != null) {
                                                                                    double currentBalanceUser2 = Double.parseDouble(balanceUser2);

                                                                                    // Increase the other user's balance
                                                                                    double newBalanceUser2 = currentBalanceUser2 + propertyPrice;
                                                                                    userReference2.child("Balance").setValue(String.valueOf(newBalanceUser2));

                                                                                    // TODO: Add code to navigate to the next screen if needed
                                                                                    Intent intent = new Intent(property_details.this, PropertyGallery.class);
                                                                                    startActivity(intent);

                                                                                    // Remove the ValueEventListener to avoid memory leaks
                                                                                    userReference1.removeEventListener(this);
                                                                                }
                                                                            }
                                                                        }
                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                                            // Handle database error for userReference2
                                                                        }
                                                                    });


                                                                    // Update property ownership details
                                                                    DatabaseReference propertyReference = FirebaseDatabase.getInstance().getReference("Properties").child(propertyId);
                                                                    DatabaseReference finall = FirebaseDatabase.getInstance().getReference("Transaction_user");
                                                                    DatabaseReference finalll = FirebaseDatabase.getInstance().getReference("Transaction");
                                                                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User");

                                                                    DatabaseReference f = userReference.child(username).child("Properties");

                                                                    String propertyKey = f.push().getKey();

                                                                    DatabaseReference ff = f.child(propertyKey);

                                                                    userReference.addListenerForSingleValueEvent (new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                            //String propertyIdtrans_id=finall.push().getKey();
                                                                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                                                                String Username = userSnapshot.child("uniquename").getValue(String.class);
                                                                                //Toast.makeText(property_details.this, "name: " + username, Toast.LENGTH_SHORT).show();

                                                                                finall.child(Username).child(propertyId).child("propertyID").setValue(propertyId);
                                                                                finall.child(Username).child(propertyId).child("address").setValue(address);
                                                                                finall.child(Username).child(propertyId).child("dolilNo").setValue(dolil);
                                                                                finall.child(Username).child(propertyId).child("fee").setValue(fee);
                                                                                finall.child(Username).child(propertyId).child("taxReceipts").setValue(taxReceipt);
                                                                                finall.child(Username).child(propertyId).child("title").setValue(username);
                                                                                finall.child(Username).child(propertyId).child("previousOwner").setValue(currentOwner);
                                                                                finall.child(Username).child(propertyId).child("price").setValue(price);


                                                                                propertyReference.child("previousOwner").setValue(currentOwner);
                                                                                propertyReference.child("title").setValue(username);


                                                                                ff.child("propertyID").setValue(propertyId);
                                                                                ff.child("address").setValue(address);
                                                                                ff.child("dolilNo").setValue(dolil);
                                                                                ff.child("taxReceipts").setValue(taxReceipt);
                                                                                ff.child("title").setValue(username);
                                                                                ff.child("previousOwner").setValue(currentOwner);
                                                                                ff.child("price").setValue(price);

                                                                                // Now you can use the 'username' value as needed for each child under the "User" node
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                                            // Handle database error for userReference
                                                                        }
                                                                    });
                                                                    String trans_id=finall.push().getKey();
                                                                    finalll.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                            //String propertyIdtrans_id=finall.push().getKey();


                                                                                finalll.child(trans_id).child("propertyID").setValue(propertyId);
                                                                                finalll.child(trans_id).child("address").setValue(address);
                                                                                finalll.child(trans_id).child("dolilNo").setValue(dolil);
                                                                                finalll.child(trans_id).child("taxReceipts").setValue(taxReceipt);
                                                                                finalll.child(trans_id).child("title").setValue(username);
                                                                                finalll.child(trans_id).child("previousOwner").setValue(currentOwner);
                                                                                finalll.child(trans_id).child("price").setValue(price);

                                                                                propertyReference.child("previousOwner").setValue(currentOwner);
                                                                                propertyReference.child("title").setValue(username);

                                                                                // Now you can use the 'username' value as needed for each child under the "User" node
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                                            // Handle database error for userReference
                                                                        }
                                                                    });
                                                                    Toast.makeText(property_details.this,"Property transferred successfully!",Toast.LENGTH_SHORT).show();

                                                                    propertyReference.child("status").removeValue()
                                                                            .addOnCompleteListener(task -> {
                                                                                // Handle the completion, but without a Toast
                                                                                if (task.isSuccessful()) {
                                                                                    // The "status" node has been successfully removed
                                                                                    // Additional logic if needed
                                                                                } else {
                                                                                    // Handle the error if the removal is unsuccessful
                                                                                    // Additional error handling logic if needed
                                                                                }
                                                                            });


                                                                    //propertyReference.child("Status").setValue("N/A");
                                                                    DatabaseReference queue = FirebaseDatabase.getInstance().getReference("queue");
                                                                    queue.child(address).removeValue();

                                                                    // TODO: Add code to navigate to the next screen if needed
                                                                    //Intent intent = new Intent(property_details.this, PropertyGallery.class);
                                                                    //startActivity(intent);

                                                                    // Remove the ValueEventListener to avoid memory leaks
                                                                    userReference1.removeEventListener(this);
                                                                }
                                                                else
                                                                {
                                                                    Toast.makeText(property_details.this, "Insufficient balance", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error)
                                                    {
                                                        // Handle database error
                                                    }
                                                });
                                            }
                                        });
                                    }
                                    else
                                    {
                                        butn.setEnabled(false);
                                        butn.setText("Not Verified!");
                                        butn.setOnClickListener(null);
                                    }
                                }
                                else
                                {
                                    // Set a default value for the status text field when there is no status
                                   // prop_status.setText("No status");


                                        butn.setEnabled(true);
                                        butn.setText("Want to buy");
                                        //butn.setOnClickListener(null);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {
                            // Handle any errors
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                // Handle any errors
            }
        });
    }

    private String getCurrentEmailFromSharedPreferences()
    {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        return preferences.getString("current_email", "");
    }
}