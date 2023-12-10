package com.example.basic;

import static com.example.basic.R.id.toolbar_del;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class datashow_user extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    CircleImageView profileImageView1;
    View headerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_datashow_user);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar_user);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        headerView = navigationView.getHeaderView(0);

        TextView userNameTextView = headerView.findViewById(R.id.id_name);
        TextView balance = headerView.findViewById(R.id.balance);

        profileImageView1 = headerView.findViewById(R.id.profile_image1);

        String userEmail = getEmailFromSharedPreferences();

        DatabaseReference queueReference = FirebaseDatabase.getInstance().getReference().child("User");
        queueReference.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();

                    String username = userSnapshot.child("username").getValue(String.class);
                    String uniquename = userSnapshot.child("uniquename").getValue(String.class);

                    String imageUrl = userSnapshot.child("URL").getValue(String.class);
                    String bal = userSnapshot.child("Balance").getValue(String.class);
                    userNameTextView.setText(uniquename);
                    balance.setText(bal);

                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView user = findViewById(R.id.user);
                    user.setText(uniquename);

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        // Load the image into the ImageView using Picasso
                        Picasso.get().load(imageUrl).into(profileImageView1);
                    } else {
                        // If imageUrl is not available, set a default image
                        profileImageView1.setImageResource(R.drawable.home);
                    }
                    //Picasso.get().load(Uri.parse(imageUrl)).into(userImageView);

                    //Picasso.get().load(imageUrl).into(userImageView);
                } else {
                    Log.d("Firebase", "User does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase Error", "Data retrieval failed: " + databaseError.getMessage());
            }
        });

        navigationView.setCheckedItem(R.id.Transactions);

        ImageView enlistPropertyIcon = findViewById(R.id.enlist_property_icon);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView landGalleryIcon = findViewById(R.id.enlist_property_icon2);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView aboutIcon = findViewById(R.id.aboutUs);


        enlistPropertyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(datashow_user.this, EnlistProperty.class);

                // Get the username from the TextView
                TextView userTextView = findViewById(R.id.user);
                String username = userTextView.getText().toString();

                // Pass the username as an extra to the next activity
                intent.putExtra("username", username);

                // Start the activity
                startActivity(intent);
            }
        });

        aboutIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(datashow_user.this, About.class));
            }
        });

        landGalleryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the new activity with the username as an extra in the Intent
                Intent intent = new Intent(datashow_user.this, PropertyGallery.class);

                // Get the username from the TextView
                TextView userTextView = findViewById(R.id.user);
                String username = userTextView.getText().toString();

                // Pass the username as an extra to the next activity
                intent.putExtra("username", username);

                // Start the activity
                startActivity(intent);
            }
        });

        if (shouldShowPopup()) {
            showPopup();
            setShowPopup(false); // Set it to false after showing it once
        }
    }

    private void showPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.popup, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button visitButton = dialogView.findViewById(R.id.visit_button);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button skipButton = dialogView.findViewById(R.id.skip_button);

        visitButton.setOnClickListener(view -> {
            // Navigate to a specific page when Visit is clicked
            startActivity(new Intent(datashow_user.this, About.class));
            dialog.dismiss();
        });

        skipButton.setOnClickListener(view -> {
            // Dismiss the dialog if Skip is clicked
            dialog.dismiss();
        });

        dialog.show();
    }

    private boolean shouldShowPopup() {
        SharedPreferences someSharedPreferences = getSharedPreferences("YourSharedPreferencesName", Context.MODE_PRIVATE);
        return someSharedPreferences.getBoolean("show_popup", true);
    }

    private void setShowPopup(boolean value) {
        SharedPreferences someSharedPreferences = getSharedPreferences("YourSharedPreferencesName", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = someSharedPreferences.edit();
        editor.putBoolean("show_popup", value);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Your existing code to retrieve user data
        String userEmail = getEmailFromSharedPreferences();

        DatabaseReference queueReference = FirebaseDatabase.getInstance().getReference().child("User");
        queueReference.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();

                    // Update the balance dynamically
                    String bal = userSnapshot.child("Balance").getValue(String.class);
                    TextView balanceTextView = findViewById(R.id.balance);
                    balanceTextView.setText(bal);
                } else {
                    Log.d("Firebase", "User does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase Error", "Data retrieval failed: " + databaseError.getMessage());
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.Add:
                Intent intent = new Intent(datashow_user.this, Blockchain.class);
                startActivity(intent);
                break;
            case R.id.Transactions:
                Intent intent1 = new Intent(datashow_user.this, table_show_user.class);
                startActivity(intent1);
                break;
            case R.id.block:
                TextView userTextView = headerView.findViewById(R.id.id_name);
                String unameu = userTextView.getText().toString();
                //Toast.makeText(this, "name "+ unameu, Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(datashow_user.this, Block_show.class);
                intent3.putExtra("username", unameu);
                startActivity(intent3);
                break;

            case R.id.profile2:
                TextView userTextView1 = headerView.findViewById(R.id.id_name);
                String unameu1 = userTextView1.getText().toString();
                //Toast.makeText(this, "name "+ unameu1, Toast.LENGTH_SHORT).show();
                Intent intent4 = new Intent(datashow_user.this, Show_Profile.class);
                intent4.putExtra("username", unameu1);
                startActivity(intent4);
                break;
            case R.id.logout:

                AlertDialog.Builder builder=new AlertDialog.Builder(datashow_user.this);
                builder.setMessage("Do you want to logout?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        finish();
                        Intent i=new Intent();
                        i.putExtra("finish", true);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
                        //startActivity(i);
                        finish();

                    }
                });

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert=builder.create();
                alert.show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private String getEmailFromSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        return preferences.getString("current_email", ""); // "" is the default value if the key is not found
    }

}