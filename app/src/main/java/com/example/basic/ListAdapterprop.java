package com.example.basic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ListAdapterprop extends ArrayAdapter {

    private Activity mcontext;

    List<Property> propertyList;

    public ListAdapterprop(Activity mcontext, List<Property> propertyList) {
        super(mcontext, R.layout.list_item, propertyList);
        this.mcontext = mcontext;
        this.propertyList = propertyList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = mcontext.getLayoutInflater();
        View table_show = inflater.inflate(R.layout.list_item_user_prop, null, true);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView address = table_show.findViewById(R.id.laddress);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView dolil = table_show.findViewById(R.id.ldolil);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView tax = table_show.findViewById(R.id.ltax);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView cOwner = table_show.findViewById(R.id.lcOwner);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView pOwner = table_show.findViewById(R.id.lpOwner);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView price = table_show.findViewById(R.id.lprice);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button x = table_show.findViewById(R.id.discard);


        Property property = propertyList.get(position);

        //serial.setText((int) transaction.getSerial());
        address.setText(property.getAddress());
        dolil.setText(property.getDolilNo());
        tax.setText(property.getTaxReceipts());
        cOwner.setText(property.getTitle());
        pOwner.setText(property.getPreviousOwner());
        price.setText(property.getPrice());

        //DatabaseReference queueRef = FirebaseDatabase.getInstance().getReference("Properties");


        DatabaseReference queueRef = FirebaseDatabase.getInstance().getReference("Properties").child(property.getId());

        // Fetch the current status from the database
        queueRef.child("status").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    String status = task.getResult().getValue(String.class);

                    //Toast.makeText(mcontext,"status" + status,Toast.LENGTH_SHORT).show();
                    updateButtonBasedOnStatus(x, status);

                    // Set click listener to toggle status on button click
                    x.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            toggleStatus(queueRef);
                        }
                    });
                }
            }
        });

        return table_show;
    }

    private void updateButtonBasedOnStatus(Button button, String status) {
        // Update the button text and appearance based on the status
        if ("Available".equals(status)) {
            button.setText("❌");
            // Set any other appearance changes as needed
        } else {
            button.setText("✅");
            // Set any other appearance changes as needed
        }
    }

    private void toggleStatus(DatabaseReference queueRef) {
        // Toggle the status between "Available" and "Unavailable"
        queueRef.child("status").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    String currentStatus = task.getResult().getValue(String.class);
                    String newStatus = ("Available".equals(currentStatus)) ? "Unavailable" : "Available";
                    queueRef.child("status").setValue(newStatus);
                }
            }
        });
    }
}
