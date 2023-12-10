package com.example.basic;

import static android.telecom.Call.Details.propertiesToString;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.snapshot.StringNode;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class ListAdapterblock extends ArrayAdapter {


    private Activity mcontext;
    long selectedCount = 0;

    List<Property> propertyList;
    private List<Property> selectedProperties;
    long maxid=0;
    String username;
    String a;

    private static final String SHARED_PREFERENCES_FILE = "MySharedPreferences";

    public ListAdapterblock(Activity mcontext, List<Property> propertyList,String username){
        super(mcontext,R.layout.list_item_block,propertyList);
        this.mcontext=mcontext;
        this.propertyList=propertyList;
        this.selectedProperties = new ArrayList<>();
        this.username = username;  // Initialize the username field
    }

    public List<Property> getSelectedProperties() {
        return selectedProperties;
    }

    @NonNull
    //@Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater=mcontext.getLayoutInflater();
        View table_show_block=inflater.inflate(R.layout.list_item_block,null,true);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView uaddress=table_show_block.findViewById(R.id.baddress);
        //@SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView udolil=table_show_block.findViewById(R.id.bdolil);
        //@SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView utax=table_show_block.findViewById(R.id.btax);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView unew=table_show_block.findViewById(R.id.bcOwner);
       // @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView uold=table_show_block.findViewById(R.id.bpOwner);
       // @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView uprice=table_show_block.findViewById(R.id.bprice);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView ufees=table_show_block.findViewById(R.id.bfees);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button addtoblock=table_show_block.findViewById(R.id.bblock);

        Property property = propertyList.get(position);

        uaddress.setText(property.getAddress());
        //udolil.setText(property.getDolilNo());
        //utax.setText(property.getTaxReceipts());
        unew.setText(property.getTitle());
        ufees.setText(property.getFee());
        //uold.setText(property.getPreviousOwner());
        //uprice.setText(property.getPrice());

        /*DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Properties");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot blockSnapshot : snapshot.getChildren()) {
                    // Get the values of "previousHash" and "currentHash" from each child
                    a = blockSnapshot.child("fee").getValue(String.class);
                    ufees.setText(a);
                    //Toast.makeText(mcontext,"fee "+ a,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });*/
        //ustatus.setText(property.getStatus());

        if (selectedProperties.contains(property)) {
            table_show_block.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.pastle));
        } else {
            table_show_block.setBackgroundColor(Color.TRANSPARENT);
        }

        addtoblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleAddToBlockClick(property);
            }
        });
        return table_show_block;
    }

    private void handleAddToBlockClick(Property property) {
        if (selectedProperties.contains(property)) {
            selectedProperties.remove(property);
            selectedCount--;
        } else {
            selectedProperties.add(property);
            selectedCount++;
        }
        saveSelectedCountToSharedPreferences();
        notifyDataSetChanged(); // Refresh the list to update the UI

        // Display selected items in a pop-up box
        showSelectedPropertiesDialog();
    }

    private void showSelectedPropertiesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
        builder.setTitle("Selected Properties");

        if (!selectedProperties.isEmpty()) {
            StringBuilder selectedItems = new StringBuilder();
            for (Property property : selectedProperties) {
                selectedItems.append(property.getAddress()).append("\n");
            }
            builder.setMessage(selectedItems.toString());
        } else {
            builder.setMessage("No properties selected");
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Dismiss the dialog if needed
            }
        });

        builder.setNegativeButton("Mine", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Handle the "Mine" button click
                handleMineButtonClick();
                addfee();
            }
        });

        builder.create().show();
    }

    private void handleMineButtonClick() {
        if (!selectedProperties.isEmpty()) {
            DatabaseReference blockchainRef = FirebaseDatabase.getInstance().getReference().child("Blockchain");
            DatabaseReference queueRef = FirebaseDatabase.getInstance().getReference("Transaction_user").child(username);

            String currentHash = generateSHA256HashWithDifficulty("your_random_salt" + System.currentTimeMillis(), 3);

            // Retrieve the previous hash from the latest transaction
            blockchainRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String previousHash = null;

                    // Get the previous hash from the last existing node
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        previousHash = snapshot.child("hash").getValue(String.class);
                    }

                    DatabaseReference newTransactionRef = blockchainRef.push();
                    newTransactionRef.child("hash").setValue(currentHash);
                    newTransactionRef.child("previousHash").setValue(previousHash);
                    String selected = String.valueOf(selectedCount);
                    newTransactionRef.child("prop_no").setValue(selected);
                    long nodeCount = dataSnapshot.getChildrenCount();
                    long c = nodeCount + 1;
                    String nodeC = String.valueOf(c);
                    newTransactionRef.child("blockNo").setValue(nodeC);

                    for (Property property : selectedProperties) {
                        DatabaseReference propertyRef = newTransactionRef.child("properties").push();
                        propertyRef.child("address").setValue(property.getAddress());
                        propertyRef.child("dolilNo").setValue(property.getDolilNo());
                        propertyRef.child("previousOwner").setValue(property.getPreviousOwner());
                        propertyRef.child("price").setValue(property.getPrice());
                        propertyRef.child("title").setValue(property.getTitle());
                        propertyRef.child("taxReceipts").setValue(property.getTaxReceipts());
                    }

                    removeSelectedPropertiesFromTransactionNode(queueRef);


                    // Clear the selected properties list
                    selectedProperties.clear();

                    // Notify the adapter that the data set has changed
                    notifyDataSetChanged();

                    // Inform the user that the properties have been added to the blockchain
                    Toast.makeText(mcontext, "Properties added to the blockchain", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(mcontext, "Error retrieving previous hash", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(mcontext, "No properties selected", Toast.LENGTH_SHORT).show();
        }
    }

    // Add this method to remove selected properties from the Transaction node
    private void removeSelectedPropertiesFromTransactionNode(DatabaseReference queueRef) {
        for (Property property : selectedProperties) {
            String propertyId = property.getAddress();
            //Toast.makeText(mcontext, "Removed transaction for PropertyId: " + propertyId, Toast.LENGTH_SHORT).show();
            queueRef.orderByChild("address").equalTo(propertyId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DataSnapshot snapshot : task.getResult().getChildren()) {
                            snapshot.getRef().removeValue();
                            //Toast.makeText(mcontext, "Removed transaction for PropertyId: " + propertyId, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mcontext, "Error removing transaction for PropertyId: ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void addfee() {
        DatabaseReference b = FirebaseDatabase.getInstance().getReference("User").child(username);
        b.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String newbalance = snapshot.child("Balance").getValue(String.class);

                if (newbalance != null) {
                    double currentBalance2 = Double.parseDouble(newbalance);
                    double feee=Double.parseDouble(a);

                    // Increase the other user's balance
                    double newbalance2 = currentBalance2 + feee;
                    b.child("Balance").setValue(String.valueOf(newbalance2));

                    // Remove the ValueEventListener to avoid memory leaks
                    b.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private String generateSHA256HashWithDifficulty(String input, int difficulty) {
        String targetPrefix = new String(new char[difficulty]).replace('\0', '0');

        while (true) {
            String hash = generateSHA256Hash(input);
            if (hash != null && hash.startsWith(targetPrefix)) {
                return hash;
            }
            // If the generated hash doesn't meet the difficulty criteria or an exception occurs, try again
            input = "your_random_salt" + System.currentTimeMillis();
        }
    }


    private String generateSHA256Hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());

            // Convert byte array to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Add this method in your ListAdapterblock class
    private String propertiesToString(List<Property> properties) {
        StringBuilder propertiesString = new StringBuilder();
        for (Property property : properties) {
            propertiesString.append(property.getAddress()).append("\n");
            // Add other property details as needed
        }
        return propertiesString.toString();
    }

    private void saveSelectedCountToSharedPreferences() {
        SharedPreferences sharedPreferences = mcontext.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedCount", (int) selectedCount);
        editor.apply();
    }
}
