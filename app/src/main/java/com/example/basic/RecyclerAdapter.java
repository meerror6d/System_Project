package com.example.basic;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<Property> dataList;
    Context context;
    String address,price,propertyId;
    String userName;

    public RecyclerAdapter(Context context, ArrayList<Property> dataList, String username) {
        this.context = context;
        this.dataList = dataList;
        this.userName = username;
    }

    public void setData(List<Property> newDataList) {
        dataList.clear();
        dataList.addAll(newDataList);
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {

        Property userPhoto=dataList.get(position);
        address=userPhoto.getAddress();
        price=userPhoto.getPrice();
        propertyId = userPhoto.getId(); // Extract propertyId from userPhoto
        //Toast.makeText(context, "propertyid  " + propertyId, Toast.LENGTH_SHORT).show();


        // Set a dynamic height for the recyclerImage
        int height = (position % 2 == 0) ? 250 : 300;
        holder.recyclerImage.getLayoutParams().height = height;


        holder.recycler_loc.setText(address);
        holder.recycler_price.setText(price);
        holder.loadFirebaseImage(propertyId);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos=holder.getAdapterPosition();
                Property hm=dataList.get(pos);
                String photoID1=hm.getId();

                Intent intent=new Intent(context,property_details.class);
                intent.putExtra("ID",photoID1);
                intent.putExtra("username", userName);
                Toast.makeText(context, "username  " + userName, Toast.LENGTH_SHORT).show();

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView recyclerImage;
        TextView recycler_loc, recycler_price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerImage = itemView.findViewById(R.id.pic);
            recycler_loc = itemView.findViewById(R.id.property_loc);
            recycler_price = itemView.findViewById(R.id.property_prize);
        }

        private void loadFirebaseImage(String propertyId) {
            DatabaseReference propertyRef = FirebaseDatabase.getInstance().getReference("Properties").child(propertyId);

            propertyRef.child("PropImageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String imageUrl = dataSnapshot.getValue(String.class);
                        //Toast.makeText(context, "URL:  " + imageUrl, Toast.LENGTH_SHORT).show();

                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            // Load the image using Picasso or your preferred image loading library
                            Picasso.get()
                                    .load(imageUrl)
                                    .placeholder(R.drawable.home)
                                    .fit()
                                    .centerInside()
                                    .into(recyclerImage);
                        } else {
                            // Handle the case where the image URL is empty or null
                            Picasso.get().load(R.drawable.home).into(recyclerImage);
                        }
                    } else {
                        // Handle the case where the "PropImageUrl" field does not exist
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors or database access issues here
                }
            });
        }
    }
}
