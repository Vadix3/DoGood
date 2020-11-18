package com.example.dogood.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.dogood.Dialogs.PhotoModeDialog;
import com.example.dogood.R;
import com.example.dogood.Dialogs.UpdateAccountDialog;
import com.example.dogood.activities.NewAskItemActivity;
import com.example.dogood.activities.NewGiveItemActivity;
import com.example.dogood.adapters.ViewPagerAdapter;
import com.example.dogood.objects.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;


public class Fragment_profile extends Fragment {

    private static final String TAG = "Fragment_profile";
    private static final int ASK_FRAGMENT = 0;
    private static final int GIVE_FRAGMENT = 1;
    private static final int NEW_GIVE_ITEM_RESULT_CODE = 1011;
    private static final int NEW_ASK_ITEM_RESULT_CODE = 1012;
    private static final int UPDATE_PROFILE_RESULT_CODE = 1013;

    private static final String ITEM_COUNT = "itemCount";
    public static final String CURRENT_USER = "currentUser";

    protected View view;
    private Context context;
    private ShapeableImageView profile_IMG_picture;
    private MaterialTextView profile_LBL_name;
    private MaterialTextView profile_LBL_city;
    private MaterialTextView profile_LBL_phone;
    private MaterialTextView profile_LBL_mail;
    private MaterialButton profile_BTN_update;
    private TabLayout profile_LAY_tab;
    private ViewPager2 viewPager;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FloatingActionButton addBtn;

    private User mUser;
    private int giveItemsArraySize; // The size of the total give items
    private int askItemsArraySIze; // The size of the total ask items


    public Fragment_profile() {
    }

    public Fragment_profile(User user, int giveItemsArraySize, int askItemsArraySize, Context context) {
        this.mUser = user;
        this.giveItemsArraySize = giveItemsArraySize;
        this.askItemsArraySIze = askItemsArraySize;
        this.context = context;
    }

    /**
     * A method to open photo choices dialog
     */
    private void openPhotoDialog() {
        Log.d(TAG, "openPhotoDialog: ");
        //TODO: fix quality
        PhotoModeDialog photoModeDialog = new PhotoModeDialog(getContext());
        photoModeDialog.show();
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        photoModeDialog.getWindow().setLayout(width, RelativeLayout.LayoutParams.WRAP_CONTENT);
        photoModeDialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        photoModeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        photoModeDialog.getWindow().setDimAmount(0.9f);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView profile fragment");
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_profile, container, false);
        }

        findViews();
        updateUser();

        profile_BTN_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditUserDialog();
            }
        });

        profile_IMG_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhotoDialog();
            }
        });

        return view;
    }


    private void updateUser() {
        Log.d(TAG, "updateUser: ");
        if (mUser.getName() == null || mUser.getName().equals("")) {
            profile_LBL_name.setText(R.string.please_update);
        } else {
            profile_LBL_name.setText(mUser.getName());
        }

        if (mUser.getCity() == null || mUser.getCity().equals("")) {
            profile_LBL_city.setText(R.string.please_update);
        } else {
            profile_LBL_city.setText(mUser.getCity());
        }

        if (mUser.getPhone() == null || mUser.getPhone().equals("")) {
            profile_LBL_phone.setText(R.string.please_update);
        } else {
            profile_LBL_phone.setText(mUser.getPhone());
        }

        if (mUser.getPhoto() != null) {
            Bitmap bp = stringToBitMap(mUser.getPhoto());
            profile_IMG_picture.setImageBitmap(bp);
        }

        profile_LBL_mail.setText(mUser.getEmail());

    }

    private void findViews() {
        profile_IMG_picture = view.findViewById(R.id.profile_IMG_picture);
        profile_LBL_name = view.findViewById(R.id.profile_LBL_name);
        profile_LAY_tab = view.findViewById(R.id.profile_LAY_tab);
        profile_LBL_city = view.findViewById(R.id.profile_LBL_city);
        profile_LBL_phone = view.findViewById(R.id.profile_LBL_phone);
        profile_LBL_mail = view.findViewById(R.id.profile_LBL_mail);
        profile_BTN_update = view.findViewById(R.id.profile_BTN_update);
        addBtn = view.findViewById(R.id.profile_BTN_addItemButton);
        viewPager = view.findViewById(R.id.viewPager);
        getUserProfilePhoto();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCurrentFragment();
            }
        });

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this.getFragmentManager(), getLifecycle(), mUser);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == GIVE_FRAGMENT) {
                    profile_LAY_tab.selectTab(profile_LAY_tab.getTabAt(position));
                } else {
                    profile_LAY_tab.selectTab(profile_LAY_tab.getTabAt(position));
                }
            }
        });

        profile_LAY_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "onTabSelected: "+tab.getPosition());
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    /**
     * A method to check what is the current fragment showing
     */
    private void checkCurrentFragment() {
        int currentFragment = viewPager.getCurrentItem();
        if (currentFragment == ASK_FRAGMENT) {
            Log.d(TAG, "checkCurrentFragment: Ask fragment");
            openAddAskItemActivity();
        } else {
            Log.d(TAG, "checkCurrentFragment: Give fragment");
            openAddGiveItemActivity();
        }

    }

    /**
     * A method to move to add item activity
     */
    private void openAddAskItemActivity() {
        Log.d(TAG, "openAddItemActivity: ");
        Intent intent = new Intent(getActivity(), NewAskItemActivity.class);
        Gson gson = new Gson();
        String userJson = gson.toJson(mUser);
        intent.putExtra(CURRENT_USER, userJson);
        intent.putExtra(ITEM_COUNT, askItemsArraySIze);
        startActivityForResult(intent, NEW_ASK_ITEM_RESULT_CODE);
    }

    /**
     * A method to move to add item activity
     */
    private void openAddGiveItemActivity() {
        Log.d(TAG, "openAddItemActivity: ");
        Intent intent = new Intent(getActivity(), NewGiveItemActivity.class);
        Gson gson = new Gson();
        String userJson = gson.toJson(mUser);
        intent.putExtra(CURRENT_USER, userJson);
        intent.putExtra(ITEM_COUNT, giveItemsArraySize);
        startActivityForResult(intent, NEW_GIVE_ITEM_RESULT_CODE);
    }


    /**
     * A method to get the item photo from the storage
     */
    private void getUserProfilePhoto() {
        Log.d(TAG, "getPhotoFromStorage: Fetching photo from storage");

        String itemID = mUser.getEmail();

        String path = "gs://" + getString(R.string.google_storage_bucket) + "/" + itemID + ".jpg";
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
                Glide.with(context).load(uri).placeholder(drawable).into(profile_IMG_picture);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "onFailure: Exception: " + exception.getMessage());
            }
        });
    }


    private void openEditUserDialog() {
        UpdateAccountDialog updateAccountDialog = new UpdateAccountDialog(context, mUser);
        updateAccountDialog.show();
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.5);
        int width = (int) (getResources().getDisplayMetrics().widthPixels);
        updateAccountDialog.getWindow().setLayout(width, height);
        updateAccountDialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        updateAccountDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        updateAccountDialog.getWindow().setDimAmount(0.8f);
    }

    public Bitmap stringToBitMap(String encodedString) {
        Log.d(TAG, "StringToBitMap: ");
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            Log.d(TAG, "StringToBitMap: exception" + e.getMessage());
            return null;
        }
    }
}