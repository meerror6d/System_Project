package com.example.basic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ListAdapter extends ArrayAdapter {

    private Activity mcontext;

    List<Property> propertyList;

    public ListAdapter(Activity mcontext,List<Property>propertyList){
        super(mcontext,R.layout.list_item,propertyList);
        this.mcontext=mcontext;
        this.propertyList=propertyList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater=mcontext.getLayoutInflater();
        View table_show=inflater.inflate(R.layout.list_item,null,true);

        TextView address = table_show.findViewById(R.id.maddress);
        TextView dolil = table_show.findViewById(R.id.mdolil);
        TextView tax = table_show.findViewById(R.id.mtax);
        TextView cOwner = table_show.findViewById(R.id.mcOwner);
        TextView pOwner = table_show.findViewById(R.id.mpOwner);
        TextView price = table_show.findViewById(R.id.mprice);


        Property property = propertyList.get(position);

        //serial.setText((int) transaction.getSerial());
        address.setText(property.getAddress());
        dolil.setText(property.getDolilNo());
        tax.setText(property.getTaxReceipts());
        cOwner.setText(property.getTitle());
        pOwner.setText(property.getPreviousOwner());
        price.setText(property.getPrice());

        return table_show;

    }
}
