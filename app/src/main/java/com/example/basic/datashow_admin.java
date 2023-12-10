package com.example.basic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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

public class datashow_admin extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PICK_IMAGE_REQUEST = 1;
    DrawerLayout drawerLayout1;
    NavigationView navigationView1;
    CircleImageView profileImageView1;
    Toolbar toolbar1;
    View headerView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_datashow_admin);

        drawerLayout1 = findViewById(R.id.drawer_layout_ad);
        navigationView1 = findViewById(R.id.nav_view_ad);
        toolbar1 = findViewById(R.id.toolbar_ad);

        setSupportActionBar(toolbar1);
        headerView = navigationView1.getHeaderView(0);

        navigationView1.bringToFront();

        ActionBarDrawerToggle toggle1 = new ActionBarDrawerToggle(this, drawerLayout1, toolbar1, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout1.addDrawerListener(toggle1);
        toggle1.syncState();
        navigationView1.setNavigationItemSelectedListener(this);

        navigationView1.setCheckedItem(R.id.prop);
        profileImageView1 = headerView.findViewById(R.id.profile_image1_ad);


        String userEmail = getEmailFromSharedPreferences();

        Toast.makeText(datashow_admin.this,"mail"+userEmail,Toast.LENGTH_SHORT).show();
        TextView userNameTextView = headerView.findViewById(R.id.id_name_ad);

        DatabaseReference queueReference = FirebaseDatabase.getInstance().getReference().child("Admin");
        queueReference.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();

                    String username = userSnapshot.child("username").getValue(String.class);
                    //String bal = userSnapshot.child("Balance").getValue(String.class);

                    TextView user = findViewById(R.id.adname);
                    user.setText(username);
                    userNameTextView.setText(username);

                } else {
                    Log.d("Firebase", "User does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase Error", "Data retrieval failed: " + databaseError.getMessage());
            }
        });

        profileImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the gallery to select an image
                openGallery();
            }
        });

       /* if (imageUrl != null && !imageUrl.isEmpty()) {
            // Load the image into the ImageView using Picasso
            Picasso.get().load(imageUrl).into(profileImageView1);
        } else {
            // If imageUrl is not available, set a default image
            profileImageView1.setImageResource(R.drawable.home);
        }*/


        ImageView landGalleryIcon = findViewById(R.id.land_gallery_icon1);


        landGalleryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the new activity with the username as an extra in the Intent
                Intent intent = new Intent(datashow_admin.this, PropertyGallery.class);

                // Get the username from the TextView
                TextView userTextView = findViewById(R.id.adname);
                String username = userTextView.getText().toString();

                // Pass the username as an extra to the next activity
                intent.putExtra("username", username);

                // Start the activity
                startActivity(intent);
            }
        });
    }


    @Override
    public void onBackPressed() {

        if (drawerLayout1.isDrawerOpen(GravityCompat.START)) {
            drawerLayout1.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.prop:
                Intent intent = new Intent(datashow_admin.this,table_show.class);
                startActivity(intent);
                break;
            case R.id.queue:
                Intent intent1 = new Intent(datashow_admin.this,table_show_admin.class);
                startActivity(intent1);
                break;

            case R.id.user:
                Intent intent2 = new Intent(datashow_admin.this,Userlist_show.class);
                startActivity(intent2);
                break;

            case R.id.transaction:
                Intent intent3 = new Intent(datashow_admin.this,final_transaction.class);
                startActivity(intent3);
                break;

            case R.id.logout_ad:

                AlertDialog.Builder builder=new AlertDialog.Builder(datashow_admin.this); //Home is name of the activity
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
        drawerLayout1.closeDrawer(GravityCompat.START);
        return true;
    }

    private String getEmailFromSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        return preferences.getString("current_email", ""); // "" is the default value if the key is not found
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            // Get the selected image URI
            Uri imageUri = data.getData();

            // Set the selected image to the ImageView using Picasso (or any other image loading library)
            Picasso.get().load(imageUri).into(profileImageView1);

            // You may also want to save the image URI to Firebase or elsewhere based on your use case
            // For now, let's just display a Toast message with the image URI
            //Toast.makeText(this, "Image selected: " + imageUri.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}