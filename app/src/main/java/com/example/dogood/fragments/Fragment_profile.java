package com.example.dogood.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dogood.R;
import com.example.dogood.objects.AskItem;
import com.example.dogood.objects.GiveItem;
import com.example.dogood.objects.User;

import java.util.ArrayList;


public class Fragment_profile extends Fragment {
    private static final String TAG = "Fragment_profile";

    protected View view;

    private ImageView profile_IMG_picture;
    private TextView profile_LBL_name;
    private TextView profile_LBL_city;
    private TextView profile_LBL_phone;
    private TextView profile_LBL_mail;
    private FrameLayout profile_LAY_post;

    private ArrayList<GiveItem> giveItems;
    private ArrayList<AskItem> askItems;
    private User mUser;

    public Fragment_profile(){}

    public Fragment_profile(ArrayList<GiveItem> giveItems, ArrayList<AskItem> askItems, User user) {
        this.giveItems = giveItems;
        this.askItems = askItems;
        this.mUser = user;
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
        //todo:check if user have complite profile
        //todo:search the object of the user , not all the object
        findViews();
        updateUser();
        addTolistTest();
        //populateEventList();
        return view;
    }

    private void updateUser() {
        Log.d(TAG, "updateUser: ");

        profile_LBL_name.setText(mUser.getName());
        profile_LBL_mail.setText(mUser.getEmail());


    }

    private void addTolistTest() {
        Log.d(TAG, "addTolistTest: ");
        String userCustomImage = "null";
        ArrayList<GiveItem> mgiveItems = new ArrayList<>();
        ArrayList<AskItem>mrequestItems = new ArrayList<>();

        mgiveItems.add(new GiveItem("1000","tv","electronic","new","","no need",userCustomImage,"27/10",mUser));
        mgiveItems.add(new GiveItem("1000","tv","electronic","new","","no need",userCustomImage,"27/10",mUser));
        mgiveItems.add(new GiveItem("1000","tv","electronic","new","","no need",userCustomImage,"27/10",mUser));
        mgiveItems.add(new GiveItem("1000","tv","electronic","new","","no need",userCustomImage,"27/10",mUser));
        mgiveItems.add(new GiveItem("1000","tv","electronic","new","","no need",userCustomImage,"27/10",mUser));
        mgiveItems.add(new GiveItem("1000","tv","electronic","new","","no need",userCustomImage,"27/10",mUser));

        addGiveItemsFragment(mgiveItems,mrequestItems);
    }

    private void findViews() {
        profile_IMG_picture = view.findViewById(R.id.profile_IMG_picture);
        profile_LBL_name = view.findViewById(R.id.profile_LBL_name);
        profile_LBL_city = view.findViewById(R.id.profile_LBL_city);
        profile_LBL_phone = view.findViewById(R.id.profile_LBL_phone);
        profile_LBL_mail = view.findViewById(R.id.profile_LBL_mail);
        profile_LAY_post = view.findViewById(R.id.profile_LAY_post);

    }

    private void addGiveItemsFragment(ArrayList<GiveItem> mgiveItems, ArrayList<AskItem> mrequestItems) {
        Log.d(TAG, "initItemsFragment: Initing main list with: " + giveItems.toString());
        GiveItemFragment giveItemFragment = new GiveItemFragment(mgiveItems);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.profile_LAY_post, giveItemFragment);
        transaction.commit();
    }


}