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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Userlist_show extends AppCompatActivity {

    ListView user_list;
    List<User> userList;
    List<User>searchList1;
    SearchView search;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_userlist_show);


        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Toolbar toolbar = findViewById(R.id.toolbar_u);
        search = findViewById(R.id.search_u);

        // Back icon click listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed(); // This will act as the back button press
            }
        });

        user_list=findViewById(R.id.userlist);
        userList =new ArrayList<>();

        DatabaseReference db= FirebaseDatabase.getInstance().getReference("User");


        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();

                for(DataSnapshot property_shot : snapshot.getChildren()){
                    User user = property_shot.getValue(User.class);
                    userList.add(user);
                }

                userlistadapter adapter=new userlistadapter(Userlist_show.this,userList);
                user_list.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchList1 =new ArrayList<>();

                if(query.length()>0){
                    for(int i=0;i<userList.size();i++){
                        if(userList.get(i).getUniquename().toUpperCase().contains(query.toUpperCase())){
                            searchList1.add(userList.get(i));
                            //Toast.makeText(Userlist_show.this, "user" + userList.get(i), Toast.LENGTH_SHORT).show();
                        }
                    }

                    userlistadapter searchAdapter = new userlistadapter(Userlist_show.this, searchList1);
                    user_list.setAdapter(searchAdapter);
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