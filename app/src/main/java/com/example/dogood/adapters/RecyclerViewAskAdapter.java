package com.example.dogood.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogood.R;
import com.example.dogood.activities.ItemDetailsActivity;
import com.example.dogood.interfaces.ItemDetailsListener;
import com.example.dogood.objects.AskItem;
import com.example.dogood.objects.GiveItem;
import com.example.dogood.objects.User;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;

import java.util.ArrayList;

public class RecyclerViewAskAdapter extends RecyclerView.Adapter<RecyclerViewAskAdapter.ViewHolder> {
    private static final String TAG = "Dogood";
    private static final String GIVE_ITEM = "giveItem";
    private static final String ASK_ITEM = "askItem";

    private Context context;
    private ArrayList<AskItem> items;
    private User myUser;

    public RecyclerViewAskAdapter(Context context, ArrayList<AskItem> items, User user) {
        Log.d(TAG, "RecyclerViewAskAdapter: Im in adapter with: " + items.toString());
        this.context = context;
        this.items = items;
        this.myUser = user;
    }


    @NonNull
    @Override
    public RecyclerViewAskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.askitem_recyclerview_row, parent, false);
        return new RecyclerViewAskAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAskAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: got item: " + items.get(position));
        AskItem temp = items.get(position);
        holder.itemName.setText(temp.getName());
        holder.itemCity.setText(temp.getCity());
        holder.postDate.setText(temp.getDate());
        holder.itemPhoto.setImageResource(R.color.colorPrimary);
        holder.rowCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openItemDetails(position);
            }
        });
    }

    /**
     * A method to open the item details activity
     */
    private void openItemDetails(int position) {
        Log.d(TAG, "openItemDetails: ");
        ((ItemDetailsListener) context).getSelectedItem(position,false);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    /**
     * An inner class to specify each row contents
     */
    public class ViewHolder extends RecyclerView.ViewHolder { // To hold each row

        TextView itemName, itemCategory, itemCity, itemDescription, postDate;
        ShapeableImageView itemPhoto;
        MaterialCardView rowCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews();
        }

        /**
         * A method to initialize the views
         */
        private void initViews() {
            rowCard = itemView.findViewById(R.id.askRow_LAY_row);
            itemName = itemView.findViewById(R.id.askRow_LBL_itemName);
            itemCity = itemView.findViewById(R.id.askRow_LBL_itemCity);
            postDate = itemView.findViewById(R.id.askRow_LBL_postDate);
            itemPhoto = itemView.findViewById(R.id.askRow_IMG_itemCategoryPic);
        }


    }
}
