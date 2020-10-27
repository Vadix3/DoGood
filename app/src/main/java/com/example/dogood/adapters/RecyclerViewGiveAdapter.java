package com.example.dogood.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogood.R;
import com.example.dogood.objects.GiveItem;
import com.google.android.material.card.MaterialCardView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RecyclerViewGiveAdapter extends RecyclerView.Adapter<RecyclerViewGiveAdapter.ViewHolder> {
    private static final String TAG = "Dogood";
    private Context context;
    private ArrayList<GiveItem> items;

    public RecyclerViewGiveAdapter(Context context, ArrayList<GiveItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerViewGiveAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_row, parent, false);
        return new RecyclerViewGiveAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewGiveAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Got item: " + items.get(position).toString());
        GiveItem temp = items.get(position);
        holder.itemName.setText(temp.getName());
        holder.itemState.setText(temp.getState());
        holder.itemPrice.setText(temp.getPrice());
        holder.itemDescription.setText(temp.getDescription());
        holder.postDate.setText(temp.getDate());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    /**
     * An inner class to specify each row contents
     */
    public class ViewHolder extends RecyclerView.ViewHolder { // To hold each row

        TextView itemName, itemState, itemPrice, itemDescription, postDate;
        ImageView itemPhoto;
        MaterialCardView rowCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews();
        }

        /**
         * A method to initialize the views
         */
        private void initViews() {
            rowCard = itemView.findViewById(R.id.row_giveitem_row);
            itemName = itemView.findViewById(R.id.row_LBL_itemName);
            itemState = itemView.findViewById(R.id.row_LBL_itemState);
            itemPrice = itemView.findViewById(R.id.row_LBL_itemPrice);
            itemDescription = itemView.findViewById(R.id.row_LBL_itemDescription);
            postDate = itemView.findViewById(R.id.row_LBL_postDate);
            itemPhoto = itemView.findViewById(R.id.row_IMG_itemPicture);
        }


    }
}
