package com.example.basic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.FileObserver;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Show_Profile extends AppCompatActivity {

    ListView transaction_list;
    List<Property> transactionList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_show_profile);

        Toolbar toolbar = findViewById(R.id.toolbar_prof);

        // Back icon click listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed(); // This will act as the back button press
            }
        });

        @SuppressLint({"WrongViewCast", "MissingInflatedId", "LocalSuppress"}) TextView username = findViewById(R.id.user_name);
        @SuppressLint({"WrongViewCast", "MissingInflatedId", "LocalSuppress"}) TextView usernid = findViewById(R.id.user_nid);
        @SuppressLint({"WrongViewCast", "MissingInflatedId", "LocalSuppress"}) TextView usertin = findViewById(R.id.user_tin);
        @SuppressLint({"WrongViewCast", "MissingInflatedId", "LocalSuppress"}) TextView userbal = findViewById(R.id.user_bal);
        @SuppressLint({"WrongViewCast", "MissingInflatedId", "LocalSuppress"}) TextView properties = findViewById(R.id.properties);
        @SuppressLint({"WrongViewCast", "MissingInflatedId", "LocalSuppress"}) TextView real = findViewById(R.id.real);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button edit = findViewById(R.id.edit);


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Show_Profile.this,profile_update.class);
                startActivity(intent);
            }
        });

        @SuppressLint({"WrongViewCast", "MissingInflatedId", "LocalSuppress"}) TextView u_name = findViewById(R.id.u_name);
        @SuppressLint({"WrongViewCast", "MissingInflatedId", "LocalSuppress"}) TextView u_nid = findViewById(R.id.u_nid);
        @SuppressLint({"WrongViewCast", "MissingInflatedId", "LocalSuppress"}) TextView u_tin = findViewById(R.id.u_tin);
        @SuppressLint({"WrongViewCast", "MissingInflatedId", "LocalSuppress"}) TextView u_bal = findViewById(R.id.u_bal);
        @SuppressLint({"WrongViewCast", "MissingInflatedId", "LocalSuppress"}) TextView u_real = findViewById(R.id.actual);


        String user = getIntent().getStringExtra("username");

        transaction_list = findViewById(R.id.user_block);
        transactionList = new ArrayList<>();

        //Toast.makeText(this, "user" + user, Toast.LENGTH_SHORT).show();

        DatabaseReference data = FirebaseDatabase.getInstance().getReference("User").child(user);

        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String nameu = dataSnapshot.child("uniquename").getValue(String.class);
                    String namea = dataSnapshot.child("username").getValue(String.class);
                    String nidu = dataSnapshot.child("nid").getValue(String.class);
                    String tinu = dataSnapshot.child("Tin").getValue(String.class);
                    String balanceu = dataSnapshot.child("Balance").getValue(String.class);
                    String imageUrl = dataSnapshot.child("URL").getValue(String.class);

                    // Set the retrieved data to the TextView widgets
                    u_name.setText(nameu);
                    u_nid.setText(nidu);
                    u_tin.setText(tinu);
                    u_bal.setText( balanceu);
                    u_real.setText(namea);

                    fetchFromDatabase(nameu);

                    CircleImageView profileImageView1 = findViewById(R.id.prof_image);;

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        // Load the image into the ImageView using Picasso
                        Picasso.get().load(imageUrl).into(profileImageView1);
                    } else {
                        // If imageUrl is not available, set a default image
                        profileImageView1.setImageResource(R.drawable.home);
                    }

                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase Error", "Data retrieval failed: " + databaseError.getMessage());
            }
        });

    }

    private void fetchFromDatabase (String username){

        DatabaseReference data = FirebaseDatabase.getInstance().getReference("User");

        data.orderByChild("uniquename").equalTo(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot blockSnapshot : dataSnapshot.getChildren()) {
                    // Access properties node
                    DataSnapshot propertiesSnapshot = blockSnapshot.child("Properties");

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
                Toast.makeText(Show_Profile.this, "Error fetching properties: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void displayProperties() {
        // Create a custom adapter for the ListView
        ListAdapterprop adapter = new ListAdapterprop(Show_Profile.this, transactionList);
        transaction_list.setAdapter(adapter);
    }
}