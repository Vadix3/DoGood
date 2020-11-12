package com.example.dogood.activities;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.example.dogood.Dialogs.ItemPhotoDialog;
import com.example.dogood.R;
import com.example.dogood.objects.AskItem;
import com.example.dogood.objects.GiveItem;
import com.example.dogood.objects.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.api.Distribution;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

public class ItemDetailsActivity extends AppCompatActivity {
    private static final String TAG = "Dogood";
    private static final String GIVE_ITEM = "giveItem";
    private static final String ASK_ITEM = "askItem";
    private static final String OWNER_USER = "ownerUser";

    private Context context = getBaseContext();

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private TextView itemName, itemState, itemPrice, itemDescription, itemDate;
    private MaterialButton contactUser;
    private ShapeableImageView itemPhoto;

    private LinearLayout removeEditLayout;
    private MaterialButton editBtn;
    private MaterialButton removeBtn;

    private GiveItem myGiveItem = null;
    private AskItem myAskItem = null;
    private User itemUser;
    private boolean ownerUser = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        ownerUser = getIntent().getBooleanExtra(OWNER_USER, false);
        Log.d(TAG, "onCreate: ");
        initViews();
        checkReceivedItem();
    }


    /**
     * A method to check the received item from the items list.
     * Could be 2 options: giveItem and askItem.
     */
    private void checkReceivedItem() {
        Log.d(TAG, "checkReceivedItem: ");
        Gson gson = new Gson();
        String itemJson;
        if (getIntent().getStringExtra(GIVE_ITEM) != null) {
            Log.d(TAG, "checkReceivedItem: Got give item");
            myGiveItem = gson.fromJson(getIntent().getStringExtra(GIVE_ITEM), GiveItem.class);
            itemUser = myGiveItem.getGiver();
        } else {
            Log.d(TAG, "checkReceivedItem: Got ask item");
            myAskItem = gson.fromJson(getIntent().getStringExtra(ASK_ITEM), AskItem.class);
            itemUser = myAskItem.getRequester();
        }
        updateUI();
    }

    /**
     * A method to update the UI with the given item
     */
    private void updateUI() {
        Log.d(TAG, "updateUI: ");
        if (getIntent().getBooleanExtra(OWNER_USER, false)) {
            Log.d(TAG, "updateUI: Im the owner of the post");
        }
        if (myGiveItem != null) {
            Log.d(TAG, "updateUI: Updating UI to match give item");
            itemName.setText(myGiveItem.getName());
            itemState.setText(getString(R.string.condition) + myGiveItem.getState());
            if (myGiveItem.getPrice().equals("")) {
                itemPrice.setText(getString(R.string.price) + " " + getString(R.string.free));
            } else
                itemPrice.setText(getString(R.string.price) + myGiveItem.getPrice());
            itemDescription.setText(getString(R.string.description) + "\n" + myGiveItem.getDescription());
            itemDate.setText(getString(R.string.post_date) + myGiveItem.getDate());
            getPhotoFromStorage();

        } else {
            Log.d(TAG, "updateUI: Updating UI to match ask item");
            itemName.setText(myAskItem.getName());
            itemDescription.setText(getString(R.string.description) + "\n" + myAskItem.getDescription());
            itemDate.setText(getString(R.string.post_date) + myAskItem.getDate());
            itemState.setVisibility(View.GONE);
            itemPrice.setVisibility(View.GONE);
            contactUser.setText("Contact Asker");
        }
    }

    /**
     * A method to get the item photo from the storage
     */
    private void getPhotoFromStorage() {
        Log.d(TAG, "getPhotoFromStorage: Fetching photo from storage");
        String itemID = myGiveItem.getId();

        String path = "gs://" + getString(R.string.google_storage_bucket) + "/" + itemID + ".jpg";
        Log.d(TAG, "getPhotoFromStorage: Fetching: " + path);
        StorageReference gsReference = storage.getReferenceFromUrl(path);

        gsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "onSuccess: " + uri);
                // create a ProgressDrawable object which we will show as placeholder
                CircularProgressDrawable drawable = new CircularProgressDrawable(ItemDetailsActivity.this);
                drawable.setColorSchemeColors(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.black);
                drawable.setCenterRadius(30f);
                drawable.setStrokeWidth(5f);
                // set all other properties as you would see fit and start it
                drawable.start();
                Glide.with(ItemDetailsActivity.this).load(uri).placeholder(drawable).into(itemPhoto);
                itemPhoto.setClickable(true);
                itemPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openImageDialog(uri);
                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "onFailure: Exception: " + exception.getMessage());
            }
        });
    }

    /**
     * A method to open image on whole screen
     */
    private void openImageDialog(Uri uri) {
        Log.d(TAG, "openImageDialog: ");
        ItemPhotoDialog itemPhotoDialog = new ItemPhotoDialog(this, uri);
        itemPhotoDialog.show();
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.5);
        int width = (int) (getResources().getDisplayMetrics().widthPixels);
        itemPhotoDialog.getWindow().setLayout(width, height);
        itemPhotoDialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        itemPhotoDialog.getWindow().setDimAmount(0.99f);
    }


    /**
     * A method to initialize the views
     */
    private void initViews() {
        Log.d(TAG, "initViews: ");
        itemName = findViewById(R.id.itemDetails_LBL_itemName);
        itemState = findViewById(R.id.itemDetails_LBL_itemState);
        itemPrice = findViewById(R.id.itemDetails_LBL_itemPrice);
        itemDescription = findViewById(R.id.itemDetails_LBL_itemDescription);
        itemDate = findViewById(R.id.itemDetails_LBL_itemDate);
        itemPhoto = findViewById(R.id.itemDetails_IMG_itemPhoto);
        contactUser = findViewById(R.id.itemDetails_BTN_contactGiver);
        contactUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openContactUserDialog();
            }
        });

        removeEditLayout = findViewById(R.id.itemDetails_LAY_removeEditLayout);
        if (ownerUser) {
            removeEditLayout.setVisibility(View.VISIBLE);
            contactUser.setVisibility(View.GONE);
        }
        editBtn = findViewById(R.id.itemDetails_BTN_editEntry);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditItemDialog();
            }
        });
        removeBtn = findViewById(R.id.itemDetails_BTN_removeEntry);
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItem();
            }
        });

    }

    /**
     * A method to remove the current item
     */
    private void removeItem() {
        Log.d(TAG, "removeItem: ");
        Toast.makeText(this, "Opening remove item dialog", Toast.LENGTH_SHORT).show();
    }

    /**
     * A method to open edit item dialog
     */
    private void openEditItemDialog() {
        Log.d(TAG, "openEditItemDialog: ");
        Toast.makeText(this, "Opening edit item dialog", Toast.LENGTH_SHORT).show();
    }

    /**
     * A method to open contact user dialog to contact giver / asker
     */
    private void openContactUserDialog() {
        Log.d(TAG, "openContactUserDialog: Contacting: " + itemUser.toString());
        //TODO: check if request is discrete or not
    }
}
