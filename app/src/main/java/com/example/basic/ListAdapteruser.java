package com.example.basic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.List;

public class ListAdapteruser extends ArrayAdapter {

    private Activity mcontext2;

    List<Property> propertyList;
    long maxid=0;

    public ListAdapteruser(Activity mcontext2, List<Property> propertyList){
        super(mcontext2,R.layout.list_item_user,propertyList);
        this.mcontext2=mcontext2;
        this.propertyList=propertyList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater=mcontext2.getLayoutInflater();
        View table_show_user=inflater.inflate(R.layout.list_item_user,null,true);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView uaddress=table_show_user.findViewById(R.id.addressu);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView udolil=table_show_user.findViewById(R.id.dolilu);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView utax=table_show_user.findViewById(R.id.taxu);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView unew=table_show_user.findViewById(R.id.unew);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView uold=table_show_user.findViewById(R.id.oldu);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView uprice=table_show_user.findViewById(R.id.priceu);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView ustatus=table_show_user.findViewById(R.id.statusu);

        Property property = propertyList.get(position);


        uaddress.setText(property.getAddress());
        udolil.setText(property.getDolilNo());
        utax.setText(property.getTaxReceipts());
        unew.setText(property.getTitle());
        uold.setText(property.getPreviousOwner());
        uprice.setText(property.getPrice());
        ustatus.setText(property.getStatus());

        // Check if the status is "verified" and set background color accordingly
        if (property != null && "verified!".equalsIgnoreCase(property.getStatus())) {
            table_show_user.setBackgroundColor(ContextCompat.getColor(mcontext2, R.color.pastle));
        } else {
            // Set the default background color for other statuses
            table_show_user.setBackgroundColor(ContextCompat.getColor(mcontext2, android.R.color.transparent));
        }

        return table_show_user;

    }
}
