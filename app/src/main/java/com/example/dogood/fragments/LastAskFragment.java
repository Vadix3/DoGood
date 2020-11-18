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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

public class LastAskFragment extends Fragment {
    private static final String TAG = "Dogood";
    private Context context;
    private AskItem item;
    protected View view;
    private MaterialCardView mainLayout;
    private TextView itemName;
    private ShapeableImageView itemPhoto;

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    public LastAskFragment(Context context, AskItem item) {
        Log.d(TAG, "LastAskFragment: ");
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
            view = inflater.inflate(R.layout.fragment_last_askitem, container, false);
        }
        initViews();
        return view;
    }

    private void initViews() {
        Log.d(TAG, "initViews: ");
        itemName = view.findViewById(R.id.lastAsk_LBL_itemName);
        itemPhoto = view.findViewById(R.id.lastAsk_IMG_itemPhoto);
        mainLayout=view.findViewById(R.id.lastAsk_LAY_cardview);
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ItemDetailsListener) context).getSelectedItem(null,item,false);
            }
        });
        itemName.setText(item.getName());
        getItemCategory(itemPhoto);
    }

    /**
     * A method to init category image
     */
    private void getItemCategory(ShapeableImageView itemPhoto) {
        Log.d(TAG, "getItemCategory: ");
        String category = item.getCategory();

        if (category.equals("Clothes")) {
            itemPhoto.setImageResource(R.drawable.ic_clothes);
            return;
        }
        if (category.equals("Office supplies")) {
            itemPhoto.setImageResource(R.drawable.ic_office_supplies);
            return;
        }
        if (category.equals("Medical equipment")) {
            itemPhoto.setImageResource(R.drawable.ic_medical_equipment);
            return;
        }
        if (category.equals("Gaming")) {
            itemPhoto.setImageResource(R.drawable.ic_gaming);
            return;
        }
        if (category.equals("Electronics")) {
            itemPhoto.setImageResource(R.drawable.ic_electronics);
            return;
        }
        if (category.equals("Appliances")) {
            itemPhoto.setImageResource(R.drawable.ic_appliances);
            return;
        }
        if (category.equals("Gift cards")) {
            itemPhoto.setImageResource(R.drawable.ic_gift_cards);
            return;
        }
        if (category.equals("Lighting")) {
            itemPhoto.setImageResource(R.drawable.ic_lighting);
            return;
        }
        if (category.equals("Food")) {
            itemPhoto.setImageResource(R.drawable.ic_food);
            return;
        }
        if (category.equals("Cosmetic")) {
            itemPhoto.setImageResource(R.drawable.ic_cosmetic);
            return;
        }
        if (category.equals("Games and Toys")) {
            itemPhoto.setImageResource(R.drawable.ic_games_and_toys);
            return;
        }
        if (category.equals("Cellular")) {
            itemPhoto.setImageResource(R.drawable.ic_cellular);
            return;
        }
        if (category.equals("Books")) {
            itemPhoto.setImageResource(R.drawable.ic_books);
            return;
        }
        if (category.equals("Baby Supplies")) {
            itemPhoto.setImageResource(R.drawable.ic_baby_supplies);
            return;
        }
        if (category.equals("Computers")) {
            itemPhoto.setImageResource(R.drawable.ic_computers);
            return;
        }
        if (category.equals("Other")) {
            itemPhoto.setImageResource(R.drawable.ic_other);
            return;
        }else {
            itemPhoto.setImageResource(R.drawable.ic_other);
            return;
        }
    }


}
