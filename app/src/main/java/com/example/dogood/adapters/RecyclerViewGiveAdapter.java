package com.example.dogood.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.example.dogood.R;
import com.example.dogood.activities.ItemDetailsActivity;
import com.example.dogood.interfaces.ItemDetailsListener;
import com.example.dogood.objects.GiveItem;
import com.example.dogood.objects.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;

public class RecyclerViewGiveAdapter extends RecyclerView.Adapter<RecyclerViewGiveAdapter.ViewHolder> {
    private static final String TAG = "Dogood";
    private static final String GIVE_ITEM = "giveItem";
    private static final String OWNER_USER = "ownerUser";

    FirebaseStorage storage = FirebaseStorage.getInstance();
    private Context context;
    private ArrayList<GiveItem> items;
    private User myUser;

    public RecyclerViewGiveAdapter(Context context, ArrayList<GiveItem> items, User user) {
        Log.d(TAG, "RecyclerViewGiveAdapter: Im in adapter with: " + items.toString());
        this.context = context;
        this.items = items;
        this.myUser = user;
    }

    @NonNull
    @Override
    public RecyclerViewGiveAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.giveitem_recyclerview_row, parent, false);
        return new RecyclerViewGiveAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewGiveAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Got item: " + items.get(position).toString());
        GiveItem temp = items.get(position);
        holder.itemName.setText(temp.getName());
        holder.itemState.setText(temp.getState());
        if (!temp.getPrice().equalsIgnoreCase("")) {//Item not free
            holder.itemPrice.setText(temp.getPrice() + " ILS");
        } else {
            holder.itemPrice.setText(R.string.free);
        }
        holder.postDate.setText(temp.getDate());
        ShapeableImageView itemImage = holder.itemPhoto;
        getPhotoFromStorage(itemImage, position);
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
        ((ItemDetailsListener) context).getSelectedItem(position,true);
    }

    /**
     * A method to get the item photo from the storage
     */
    private void getPhotoFromStorage(final ShapeableImageView itemPhoto, int position) {
        Log.d(TAG, "getPhotoFromStorage: Fetching photo from storage");
        String itemID = items.get(position).getId();

        String path = "gs://" + context.getString(R.string.google_storage_bucket) + "/" + itemID + ".jpg";
        Log.d(TAG, "getPhotoFromStorage: Fetching: " + path);
        StorageReference gsReference = storage.getReferenceFromUrl(path);

        gsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "onSuccess: " + uri);
                // create a ProgressDrawable object which we will show as placeholder
                CircularProgressDrawable drawable = new CircularProgressDrawable(context);
                drawable.setColorSchemeColors(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.black);
                drawable.setCenterRadius(30f);
                drawable.setStrokeWidth(5f);
                // set all other properties as you would see fit and start it
                drawable.start();
                Glide.with(context).load(uri).placeholder(drawable).into(itemPhoto);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "onFailure: Exception: " + exception.getMessage());
            }
        });
    }

    /**
     * This Function converts the String back to Bitmap
     */
    public Bitmap stringToBitMap(String encodedString) {
        Log.d(TAG, "stringToBitMap: Converting string to bitmap");
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            Log.d(TAG, "stringToBitMap: Exception: " + e.getMessage());
            return null;
        }
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
            rowCard = itemView.findViewById(R.id.giveRow_giveitem_row);
            itemName = itemView.findViewById(R.id.giveRow_LBL_itemName);
            itemState = itemView.findViewById(R.id.giveRow_LBL_itemState);
            itemPrice = itemView.findViewById(R.id.giveRow_LBL_itemPrice);
            postDate = itemView.findViewById(R.id.giveRow_LBL_postDate);
            itemPhoto = itemView.findViewById(R.id.giveRow_IMG_itemPicture);
        }


    }
}
