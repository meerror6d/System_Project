package com.example.basic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class table_show extends AppCompatActivity {

    ListView property_list;
    List<Property> propertyList;
    List<Property>searchList1;

    DatabaseReference db;
    SearchView search;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_table_show);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Toolbar toolbar = findViewById(R.id.toolbar_m);

        // Back icon click listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed(); // This will act as the back button press
            }
        });

        ImageView rightIcon = findViewById(R.id.rightIcon1);
        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the new activity (NewActivity)
                Intent intent = new Intent(table_show.this, Userlist_show.class);
                startActivity(intent);
            }
        });

        property_list=findViewById(R.id.property_list);
        propertyList =new ArrayList<>();

        db= FirebaseDatabase.getInstance().getReference("Properties");

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                propertyList.clear();

                for(DataSnapshot property_shot : snapshot.getChildren()){

                    Property property=property_shot.getValue(Property.class);
                    propertyList.add(property);
                }

                ListAdapter adapter=new ListAdapter(table_show.this,propertyList);
                property_list.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}