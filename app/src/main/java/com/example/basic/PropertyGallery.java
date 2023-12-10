package com.example.basic;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PropertyGallery extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Property> dataList;
    List<Property> searchList;
    SearchView search1;
    RecyclerAdapter adapter;
    DatabaseReference Reference = FirebaseDatabase.getInstance().getReference("Properties");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_property_gallery);

        search1 = findViewById(R.id.searchView);


        String username = getIntent().getStringExtra("username");
        //Toast.makeText(PropertyGallery.this, "username  "+username, Toast.LENGTH_SHORT).show();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        dataList = new ArrayList<>();
        adapter = new RecyclerAdapter(this, dataList,username);
        recyclerView.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.toolbar_gal);

        // Back icon click listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // This will act as the back button press
            }
        });


        Reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Property dataClass = dataSnapshot.getValue(Property.class);
                    if (dataClass != null && "Available".equals(dataClass.getStatus())) {
                        dataList.add(dataClass);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Error", "Data retrieval failed: " + error.getMessage());
            }

        });

        search1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchList =new ArrayList<>();

                if(query.length()>0){
                    for(int i=0;i<dataList.size();i++){
                        if(dataList.get(i).getAddress().toUpperCase().contains(query.toUpperCase())){
                            searchList.add(dataList.get(i));
                        }
                    }
                    adapter.setData(searchList);
                    adapter.notifyDataSetChanged();

                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


    }

}