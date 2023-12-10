package com.example.basic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class blockchain_details extends AppCompatActivity {

    ListView transaction_list;
    List<Property> transactionList;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_blockchain_details);


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

        transaction_list = findViewById(R.id.prop_block);
        transactionList = new ArrayList<>();

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            String blockNo = intent.getStringExtra("blockNo");
            String prevHash = intent.getStringExtra("prevHash");
            String curHash = intent.getStringExtra("curHash");

            TextView textBlockNo = findViewById(R.id.textBlockNo);
            TextView textPrevHash = findViewById(R.id.textPrevHash);
            TextView textCurHash = findViewById(R.id.textCurHash);

            textBlockNo.setText(blockNo);
            textPrevHash.setText(prevHash);
            textCurHash.setText(curHash);
            fetchPropertiesFromDatabase(blockNo);
        }
    }

        private void fetchPropertiesFromDatabase (String blockNo){
            db = FirebaseDatabase.getInstance().getReference("Blockchain");

            db.orderByChild("blockNo").equalTo(blockNo).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot blockSnapshot : dataSnapshot.getChildren()) {
                        // Access properties node
                        DataSnapshot propertiesSnapshot = blockSnapshot.child("properties");

                        for (DataSnapshot propertySnapshot : propertiesSnapshot.getChildren()) {
                            // Access property details

                            Property property = propertySnapshot.getValue(Property.class);
                            transactionList.add(property);


                            // Display properties in the ListView
                            displayProperties();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle errors or interruptions
                    Toast.makeText(blockchain_details.this, "Error fetching properties: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void displayProperties () {
            // Create a custom adapter for the ListView
            ListAdapter adapter = new ListAdapter(blockchain_details.this, transactionList);
            transaction_list.setAdapter(adapter);
        }

}