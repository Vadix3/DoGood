package com.example.dogood.fragments;


/*

        <FrameLayout
            android:id="@+id/profile_LAY_post"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.dogood.MainActivity;
import com.example.dogood.R;
import com.example.dogood.activities.Activity_updateAccount;
import com.example.dogood.adapters.ViewPagerAdapter;
import com.example.dogood.objects.User;
import com.google.android.material.button.MaterialButton;


public class Fragment_profile extends Fragment implements MainActivity.IOnBackPressed {
    private static final String TAG = "Fragment_profile";

    protected View view;

    private static final int UPDATE_PROFILE_RESULT_CODE = 1013;
    private static final String ITEM_COUNT = "itemCount";


    private ImageView profile_IMG_picture;
    private TextView profile_LBL_name;
    private TextView profile_LBL_city;
    private TextView profile_LBL_phone;
    private TextView profile_LBL_mail;
    //private FrameLayout profile_LAY_post;
    private MaterialButton profile_BTN_update;
    private ViewPager2 viewPager;


    private Fragment_ask_give_profile fragment_ask_give_profile;

    private User mUser;
    private int giveItemsArraySize; // The size of the total give items
    private int askItemsArraySIze; // The size of the total ask items

    public Fragment_profile() {
    }

    public Fragment_profile(User user, int giveItemsArraySize, int askItemsArraySize) {
        this.mUser = user;
        this.giveItemsArraySize = giveItemsArraySize;
        this.askItemsArraySIze = askItemsArraySize;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_profile, container, false);
        }

        findViews();
        updateUser();
        //addGiveItemsFragment();

        profile_BTN_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(view);
            }
        });

        profile_IMG_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPicture();
            }
        });

        return view;
    }

    private void addPicture() {

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
        profile_LBL_city = view.findViewById(R.id.profile_LBL_city);
        profile_LBL_phone = view.findViewById(R.id.profile_LBL_phone);
        profile_LBL_mail = view.findViewById(R.id.profile_LBL_mail);
        //profile_LAY_post = view.findViewById(R.id.profile_LAY_post);
        profile_BTN_update = view.findViewById(R.id.profile_BTN_update);

        viewPager = view.findViewById(R.id.viewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this.getFragmentManager(),getLifecycle(), mUser);
        viewPager.setAdapter(viewPagerAdapter);
    }


    private void addGiveItemsFragment() {
        Log.d(TAG, "initItemsFragment: Initing main list with: ");
        fragment_ask_give_profile = new Fragment_ask_give_profile(mUser, giveItemsArraySize, askItemsArraySIze);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        //  transaction.replace(R.id.profile_LAY_post, fragment_ask_give_profile);
        transaction.commit();
    }

    private void openDialog(View view) {
        Intent intent = new Intent(getActivity(), Activity_updateAccount.class);
        startActivityForResult(intent, UPDATE_PROFILE_RESULT_CODE);
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


    @Override
    public boolean onBackPressed() {
        Log.d(TAG, "onBackPressed: from profile");
        return fragment_ask_give_profile.onBackPressed();
    }
}