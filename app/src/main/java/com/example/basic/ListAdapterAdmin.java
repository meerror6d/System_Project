package com.example.basic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ListAdapterAdmin extends ArrayAdapter {

    private Activity mcontext;

    List<Property> propertyList;

    public ListAdapterAdmin(Activity mcontext,List<Property> propertyList){
        super(mcontext,R.layout.list_item_admin,propertyList);
        this.mcontext=mcontext;
        this.propertyList=propertyList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater=mcontext.getLayoutInflater();
        View table_show_admin=inflater.inflate(R.layout.list_item_admin,null,true);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView aaddress = table_show_admin.findViewById(R.id.Aaddress);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView dolil = table_show_admin.findViewById(R.id.Adolil);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView tax = table_show_admin.findViewById(R.id.Atax);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView cOwner = table_show_admin.findViewById(R.id.AcOwner);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView pOwner = table_show_admin.findViewById(R.id.ApOwner);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView price = table_show_admin.findViewById(R.id.Aprice);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button vBtn = table_show_admin.findViewById(R.id.Averify);
        Button dBtn = table_show_admin.findViewById(R.id.Adiscard);

        Property property = propertyList.get(position);


        aaddress.setText(property.getAddress());
        dolil.setText(property.getDolilNo());
        tax.setText(property.getTaxReceipts());
        cOwner.setText(property.getTitle());
        pOwner.setText(property.getPreviousOwner());
        price.setText(property.getPrice());

        dBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Property property = propertyList.get(position);
                String add = property.getAddress();

                propertyList.remove(position);
                notifyDataSetChanged();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("queue");;
                databaseReference.child(add).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mcontext, "Property removed from the queue", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mcontext, "Failed to remove item from the database", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        vBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vBtn.setText("Verified!");
                vBtn.setOnClickListener(null);
                String childName = "verified!";

                String add = property.getAddress();

                DatabaseReference queueReference = FirebaseDatabase.getInstance().getReference("queue");

                queueReference.child(add).child("Status").setValue(childName);

            }
        });

        return table_show_admin;

    }
}
