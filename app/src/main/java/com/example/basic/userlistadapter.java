package com.example.basic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class userlistadapter extends ArrayAdapter {

    private Activity mcontext;

    List<User> userList;

    public userlistadapter(Activity mcontext,List<User>userList){
        super(mcontext,R.layout.userlist,userList);
        this.mcontext=mcontext;
        this.userList=userList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = mcontext.getLayoutInflater();
        View table_show = inflater.inflate(R.layout.userlist, null, true);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView uniqueNameTextView = table_show.findViewById(R.id.uname);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView unameTextView= table_show.findViewById(R.id.full);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView nidTextView = table_show.findViewById(R.id.unid);

        User user = userList.get(position);

        unameTextView.setText(user.getUsername());
        uniqueNameTextView.setText(user.getUniquename());
        nidTextView.setText(user.getNid());

        return table_show;
    }
}
