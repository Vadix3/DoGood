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

    public Fragment_profile(){}

    public Fragment_profile(ArrayList<GiveItem> giveItems, ArrayList<AskItem> askItems) {
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
        addTolistTest();
        //populateEventList();
        return view;
    }

    private void addTolistTest() {
        Log.d(TAG, "addTolistTest: ");
        String userCustomImage = "null";
        ArrayList<GiveItem> mgiveItems = new ArrayList<>();
        ArrayList<AskItem>mrequestItems = new ArrayList<>();

        User nathan = new User("nathan","email","passord","netanya","054","photo");
        mgiveItems.add(new GiveItem("1000","tv","electronic","new","","no need",userCustomImage,"27/10",nathan));
        mgiveItems.add(new GiveItem("1000","tv","electronic","new","","no need",userCustomImage,"27/10",nathan));
        mgiveItems.add(new GiveItem("1000","tv","electronic","new","","no need",userCustomImage,"27/10",nathan));
        mgiveItems.add(new GiveItem("1000","tv","electronic","new","","no need",userCustomImage,"27/10",nathan));
        mgiveItems.add(new GiveItem("1000","tv","electronic","new","","no need",userCustomImage,"27/10",nathan));
        mgiveItems.add(new GiveItem("1000","tv","electronic","new","","no need",userCustomImage,"27/10",nathan));

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