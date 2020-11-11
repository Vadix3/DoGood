package com.example.dogood.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dogood.MainActivity;
import com.example.dogood.activities.Activity_updateAccount;
import com.example.dogood.R;
import com.example.dogood.objects.AskItem;
import com.example.dogood.objects.GiveItem;
import com.example.dogood.objects.User;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;


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
    private FrameLayout profile_LAY_post;
    private MaterialButton profile_BTN_update;
    private FrameLayout profile_LAY_profile;

    private Fragment_ask_give_profile fragment_ask_give_profile;

    private User mUser;
    private ArrayList<GiveItem> giveItems;
    private ArrayList<AskItem> askItems;

    public Fragment_profile() {
    }

    public Fragment_profile(User user, ArrayList<GiveItem> giveItems, ArrayList<AskItem> askItems) {
        this.mUser = user;
        this.giveItems = giveItems;
        this.askItems = askItems;
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
        addGiveItemsFragment(mUser.getGiveItems(), mUser.getAskItems());

        profile_BTN_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(view);
            }
        });

        return view;
    }

    private void updateUser() {
        Log.d(TAG, "updateUser: ");
        if(mUser.getName() == null || mUser.getName().equals("")){
            profile_LBL_name.setText(R.string.please_update);
        }else {
            profile_LBL_name.setText(mUser.getName());
        }

        if(mUser.getCity() == null || mUser.getCity().equals("")){
            profile_LBL_city.setText(R.string.please_update);
        }else {
            profile_LBL_city.setText(mUser.getCity());
        }

        if(mUser.getPhone() == null || mUser.getPhone().equals("")){
            profile_LBL_phone.setText(R.string.please_update);
        }else {
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
        profile_LAY_post = view.findViewById(R.id.profile_LAY_post);
        profile_BTN_update = view.findViewById(R.id.profile_BTN_update);

        profile_LAY_profile = view.findViewById(R.id.profile_LAY_profile);

    }

    private void addGiveItemsFragment(ArrayList<GiveItem> mgiveItems, ArrayList<AskItem> mrequestItems) {
        Log.d(TAG, "initItemsFragment: Initing main list with: " + mgiveItems.toString());
        fragment_ask_give_profile = new Fragment_ask_give_profile(mUser,giveItems,askItems);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.profile_LAY_post, fragment_ask_give_profile);
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