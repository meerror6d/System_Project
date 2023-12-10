package com.example.basic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Block_show extends AppCompatActivity {

    private Activity mcontext2;

    ListView transaction_list;
    List<Property> transactionList;

    DatabaseReference db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_block_show);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Toolbar toolbar = findViewById(R.id.toolbar_block);

        // Back icon click listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed(); // This will act as the back button press
            }
        });

        transaction_list = findViewById(R.id.property_list_block);
        transactionList = new ArrayList<>();

        String username = getIntent().getStringExtra("username");
        //Toast.makeText(mcontext2,"username "+ username,Toast.LENGTH_SHORT).show();
        db = FirebaseDatabase.getInstance().getReference("Transaction_user").child(username);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactionList.clear();

                for (DataSnapshot transaction_shot : snapshot.getChildren()) {

                    DataSnapshot statusSnapshot = transaction_shot.child("propertyID");
                    String status = statusSnapshot.getValue(String.class);
                    //Toast.makeText(mcontext2, "status " + status, Toast.LENGTH_SHORT).show();

                    if (status != null) {
                        Property transaction = transaction_shot.getValue(Property.class);
                        transactionList.add(transaction);
                    }
                }

                if (transaction_list != null) {
                    ListAdapterblock adapter = new ListAdapterblock(Block_show.this, transactionList,username);

                    // Check if the adapter is not null before setting it
                    if (adapter != null) {
                        transaction_list.setAdapter(adapter);
                    } else {
                        Log.e("Block_show", "ListAdapter is null");
                    }
                } else {
                    Log.e("Block_show", "transaction_list is null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }
}