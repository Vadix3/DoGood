package com.example.dogood.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.example.dogood.R;
import com.example.dogood.interfaces.ItemDetailsListener;
import com.example.dogood.objects.AskItem;
import com.example.dogood.objects.GiveItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class LastGiveFragment extends Fragment {
    private static final String TAG = "Dogood";
    private Context context;
    private GiveItem item;
    protected View view;
    private MaterialCardView mainLayout;
    private TextView itemName;
    private ShapeableImageView itemPhoto;

    private FirebaseStorage storage = FirebaseStorage.getInstance();


    public LastGiveFragment(Context context, GiveItem item) {
        Log.d(TAG, "LastGiveFragment: ");
        this.context = context;
        this.item = item;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_last_giveitem, container, false);
        }
        initViews();
        return view;
    }

    private void initViews() {
        Log.d(TAG, "initViews: ");
        itemName = view.findViewById(R.id.lastGive_LBL_itemName);
        itemPhoto = view.findViewById(R.id.lastGive_IMG_itemPhoto);
        mainLayout=view.findViewById(R.id.lastGive_LAY_cardview);
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ItemDetailsListener) context).getSelectedItem(item,null,true);
            }
        });
        itemName.setText(item.getName());
        getPhotoFromStorage(itemPhoto);

    }

    /**
     * A method to get the item photo from the storage
     */
    private void getPhotoFromStorage(final ShapeableImageView itemPhoto) {
        Log.d(TAG, "getPhotoFromStorage: Fetching photo from storage");
        String itemID = item.getId();

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

}
