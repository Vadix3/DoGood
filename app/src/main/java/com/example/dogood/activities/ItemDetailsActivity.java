package com.example.dogood.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.example.dogood.Dialogs.AreYouSureDialog;
import com.example.dogood.Dialogs.ContactUserDialog;
import com.example.dogood.Dialogs.ItemPhotoDialog;
import com.example.dogood.R;
import com.example.dogood.interfaces.EditedItemListener;
import com.example.dogood.interfaces.ItemDeleteListener;
import com.example.dogood.objects.AskItem;
import com.example.dogood.objects.GiveItem;
import com.example.dogood.objects.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.IOException;

public class ItemDetailsActivity extends AppCompatActivity implements ItemDeleteListener {
    private static final String TAG = "Dogood";
    private static final String GIVE_ITEM = "giveItem";
    private static final String ASK_ITEM = "askItem";
    private static final String OWNER_USER = "ownerUser";
    private static final String ITEM_TO_DEAL_WITH = "item_to_deal_with";
    private static final String IS_GIVE_ITEM = "is_give_item";
    private static final String TO_DELETE = "to_delete";


    private static final int ITEM_DETAILS_RESULT_CODE = 1014;
    private static final int ITEM_EDIT_RESULT_CODE = 1015;


    private Context context = this;

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
    private boolean itemChanged = false;

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
        if (getIntent().getStringExtra(GIVE_ITEM) != null) {
            myGiveItem = gson.fromJson(getIntent().getStringExtra(GIVE_ITEM), GiveItem.class);
            itemUser = myGiveItem.getGiver();
        } else {
            Log.d(TAG, "checkReceivedItem: Got ask item");
            myAskItem = gson.fromJson(getIntent().getStringExtra(ASK_ITEM), AskItem.class);
            Log.d(TAG, "checkReceivedItem: " + myAskItem.toString());
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
            itemState.setText(getString(R.string.condition) + " " + myGiveItem.getState());
            if (myGiveItem.getPrice().equals("")) {
                itemPrice.setText(getString(R.string.price) + " " + getString(R.string.free));
            } else
                itemPrice.setText(getString(R.string.price) + " " + myGiveItem.getPrice() + " " + getString(R.string.ils));
            itemDescription.setText(getString(R.string.description) + "\n" + myGiveItem.getDescription());
            itemDate.setText(getString(R.string.post_date) + " " + myGiveItem.getDate());
            getPhotoFromStorage();

        } else {
            Log.d(TAG, "updateUI: Updating UI to match ask item");
            itemName.setText(myAskItem.getName());
            itemDescription.setText(getString(R.string.description) + "\n" + myAskItem.getDescription());
            itemDate.setText(getString(R.string.post_date) + myAskItem.getDate());
            itemState.setVisibility(View.GONE);
            itemPrice.setVisibility(View.GONE);
            contactUser.setText(getString(R.string.contact_asker));
            itemPhoto.setPadding(100, 100, 100, 100);
            getItemCategory(itemPhoto, myAskItem);
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
            contactUser.setVisibility(View.INVISIBLE);
        }
        editBtn = findViewById(R.id.itemDetails_BTN_editEntry);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditItemActivity();
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
        String currentItemName = "";
        if (myGiveItem != null) {
            currentItemName = myGiveItem.getName();
        } else {
            currentItemName = myAskItem.getName();
        }
        AreYouSureDialog areYouSureDialog = new AreYouSureDialog(this, currentItemName);
        areYouSureDialog.show();
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        areYouSureDialog.getWindow().setLayout(width, RelativeLayout.LayoutParams.WRAP_CONTENT);
        areYouSureDialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        areYouSureDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        areYouSureDialog.getWindow().setDimAmount(0.5f);
    }

    /**
     * A method to init category image
     */
    private void getItemCategory(ShapeableImageView itemPhoto, AskItem item) {
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

    /**
     * A method to open edit item activity
     */
    private void openEditItemActivity() {
        Log.d(TAG, "openEditItemActivity: ");
        Intent intent = new Intent(this, EditItemActivity.class);
        Gson gson = new Gson();
        if (myAskItem != null) {
            intent.putExtra(GIVE_ITEM, "");
            intent.putExtra(ASK_ITEM, gson.toJson(myAskItem));
        } else {
            intent.putExtra(GIVE_ITEM, gson.toJson(myGiveItem));
            intent.putExtra(ASK_ITEM, "");
        }

        startActivityForResult(intent, ITEM_EDIT_RESULT_CODE);
    }

    /**
     * A method to open contact user dialog to contact giver / asker
     */
    private void openContactUserDialog() {
        Log.d(TAG, "openContactUserDialog: Contacting: " + itemUser.toString());
        ContactUserDialog dialog;
        if (myAskItem != null) {
            if (myAskItem.isDiscreteRequest()) {
                Log.d(TAG, "openContactUserDialog: contacting asker without requester name");
                dialog = new ContactUserDialog(this, myAskItem.getName(), myAskItem.getId(), null);
            } else {
                Log.d(TAG, "openContactUserDialog: contacting asker with requester name");
                dialog = new ContactUserDialog(this, myAskItem.getName(), myAskItem.getId(), itemUser);
            }
        } else {
            Log.d(TAG, "openContactUserDialog: contacting giver");
            dialog = new ContactUserDialog(this, myGiveItem.getName(), myGiveItem.getId(), itemUser);
        }

        dialog.show();
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        dialog.getWindow().setLayout(width, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.7f);
    }

    @Override
    public void deleteSelectedItem() {
        Log.d(TAG, "deleteSelectedItem: ");
        Gson gson = new Gson();
        Intent resultIntent = new Intent();
        String itemJson = "";
        if (myGiveItem != null) {
            itemJson = gson.toJson(myGiveItem);
            resultIntent.putExtra(IS_GIVE_ITEM, true);
        } else {
            itemJson = gson.toJson(myAskItem);
            resultIntent.putExtra(IS_GIVE_ITEM, false);
        }
        resultIntent.putExtra(TO_DELETE, true);
        Log.d(TAG, "deleteSelectedItem: Seding item json: " + itemJson);
        resultIntent.putExtra(ITEM_TO_DEAL_WITH, itemJson);
        setResult(ITEM_DETAILS_RESULT_CODE, resultIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ITEM_EDIT_RESULT_CODE:
                Log.d(TAG, "onActivityResult: I came from edit item activity");
                if (data != null) {
                    itemChanged = true;
                    Gson gson = new Gson();
                    if (data.getStringExtra(GIVE_ITEM) != null) {
                        myGiveItem = gson.fromJson(data.getStringExtra(GIVE_ITEM), GiveItem.class);
                    } else {
                        myAskItem = gson.fromJson(data.getStringExtra(ASK_ITEM), AskItem.class);
                    }
                    updateUI();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (itemChanged) {
            Log.d(TAG, "onBackPressed: Item has been changed, need to update");
            Gson gson = new Gson();
            Intent resultIntent = new Intent();
            String itemJson = "";
            if (myGiveItem != null) {
                itemJson = gson.toJson(myGiveItem);
                resultIntent.putExtra(IS_GIVE_ITEM, true);
            } else {
                itemJson = gson.toJson(myAskItem);
                resultIntent.putExtra(IS_GIVE_ITEM, false);
            }
            Log.d(TAG, "deleteSelectedItem: Seding item json: " + itemJson);
            resultIntent.putExtra(ITEM_TO_DEAL_WITH, itemJson);
            setResult(ITEM_DETAILS_RESULT_CODE, resultIntent);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
