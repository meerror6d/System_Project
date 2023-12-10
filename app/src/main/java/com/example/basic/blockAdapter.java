package com.example.basic;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class blockAdapter extends ArrayAdapter<block_chain> {

    private Activity mcontext;

    List<block_chain> blockList;

    public blockAdapter(Activity mcontext,List<block_chain>blockList){
        super(mcontext,R.layout.list_item_blockchain,blockList);
        this.mcontext=mcontext;
        this.blockList=blockList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_blockchain, parent, false);
        }

        block_chain currentBlock = getItem(position);

        TextView blockNoTextView = listItemView.findViewById(R.id.no_block);
        TextView prevHashTextView = listItemView.findViewById(R.id.hash_prev);
        TextView curHashTextView = listItemView.findViewById(R.id.hash_cur);
        TextView propNoTextView = listItemView.findViewById(R.id.no_prop);

        // Set the data to the UI elements
        if (currentBlock != null) {
           blockNoTextView.setText(currentBlock.getBlockNo());
            prevHashTextView.setText(currentBlock.getPrevHash());
            curHashTextView.setText(currentBlock.getCurHash());
            propNoTextView.setText(currentBlock.getProperties());
        }

        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleItemClick(currentBlock);
            }
        });

        return listItemView;
    }

    private void handleItemClick(block_chain clickedBlock) {
        // Handle the click event for the item
        // You can implement your logic here, such as showing details for the clicked block
        // For example, you can start a new activity with details of the clicked block

        Intent intent = new Intent(mcontext, blockchain_details.class);
        intent.putExtra("blockNo", clickedBlock.getBlockNo());
        intent.putExtra("prevHash", clickedBlock.getPrevHash());
        intent.putExtra("curHash", clickedBlock.getCurHash());


        // If you want to pass properties, you may need to modify the data structure
        // and pass it accordingly
        // For example, you can pass a list of properties as Serializable or Parcelable
        // intent.putExtra("properties", (Serializable) clickedBlock.getProperties());

        mcontext.startActivity(intent);
    }

}
