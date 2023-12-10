package com.example.basic;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class table_show_user extends AppCompatActivity {

    ListView transaction_list;
    List<Property> transactionList;

    DatabaseReference db;
    Button btn;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_table_show_user);

        Toolbar toolbar = findViewById(R.id.toolbar);


        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed(); // This will act as the back button press
            }
        });

        transaction_list = findViewById(R.id.Transac_list1);
        transactionList = new ArrayList<>();

        db = FirebaseDatabase.getInstance().getReference("queue");

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactionList.clear();

                for (DataSnapshot transaction_shot : snapshot.getChildren()) {

                    String status = transaction_shot.child("Status").getValue(String.class);

                    Property transaction = transaction_shot.getValue(Property.class);
                    transaction.setStatus(status);
                    transactionList.add(transaction);

                }

                ListAdapteruser adapter = new ListAdapteruser(table_show_user.this, transactionList);
                transaction_list.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

    }
}
