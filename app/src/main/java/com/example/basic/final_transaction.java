package com.example.basic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class final_transaction extends AppCompatActivity {

    ListView property_list;
    List<Property> propertyList;
    List<Property>searchList;
    SearchView search;

    DatabaseReference db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_final_transaction);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Toolbar toolbar = findViewById(R.id.toolbar_final);
        search = findViewById(R.id.search_final);

        // Back icon click listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed(); // This will act as the back button press
            }
        });

        property_list=findViewById(R.id.property_list_final);
        propertyList =new ArrayList<>();

        db= FirebaseDatabase.getInstance().getReference("Transaction");

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                propertyList.clear();

                for(DataSnapshot property_shot : snapshot.getChildren()){

                    Property property=property_shot.getValue(Property.class);
                    propertyList.add(property);
                }

                ListAdapter adapter=new ListAdapter(final_transaction.this,propertyList);
                property_list.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchList =new ArrayList<>();

                if(query.length()>0){
                    for(int i=0;i<propertyList.size();i++){
                        if(propertyList.get(i).getDolilNo().toUpperCase().contains(query.toUpperCase())){
                            searchList.add(propertyList.get(i));
                        }
                    }

                    ListAdapterAdmin searchAdapter = new ListAdapterAdmin(final_transaction.this, searchList);
                    property_list.setAdapter(searchAdapter);
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