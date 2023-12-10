package com.example.basic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
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

public class Blockchain extends AppCompatActivity {

    ListView blockchain_list;
    List<block_chain> blockList; // Change to block_chain

    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_blockchain);

        Toolbar toolbar = findViewById(R.id.toolbar_b);


        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed(); // This will act as the back button press
            }
        });


        blockchain_list = findViewById(R.id.Blockchain);
        blockList = new ArrayList<>();

        db = FirebaseDatabase.getInstance().getReference("Blockchain");

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                blockList.clear();

                for (DataSnapshot blockSnapshot : snapshot.getChildren()) {
                    // Get the values of "previousHash" and "currentHash" from each child
                    String block_no = blockSnapshot.child("blockNo").getValue(String.class);
                    //Toast.makeText(Blockchain.this, "block no"+block_no , Toast.LENGTH_SHORT).show();

                    String previousHash = blockSnapshot.child("previousHash").getValue(String.class);
                    String currentHash = blockSnapshot.child("hash").getValue(String.class);
                    String prop_no = blockSnapshot.child("prop_no").getValue(String.class);

                    block_chain block = new block_chain(block_no,previousHash, currentHash,prop_no);

                    // Add the block_chain object to the list
                    blockList.add(block);


                }

                blockAdapter adapter = new blockAdapter(Blockchain.this, blockList); // Change to blockAdapter
                blockchain_list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });

    }
}