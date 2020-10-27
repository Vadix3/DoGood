package com.example.dogood.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dogood.R;
import com.example.dogood.adapters.RecyclerViewGiveAdapter;
import com.example.dogood.objects.GiveItem;
import com.example.dogood.objects.RequestItem;
import com.example.dogood.objects.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class Fragment_profile extends Fragment {
    private static final String TAG = "Fragment_profile";

    protected View view;

    private ImageView profile_IMG_picture;
    private TextView profile_LBL_name;
    private TextView profile_LBL_city;
    private TextView profile_LBL_phone;
    private TextView profile_LBL_mail;
    private RecyclerView profile_RCV_post;

    private ArrayList<GiveItem> giveItems;
    private ArrayList<RequestItem> requestItems;

    public Fragment_profile(){}

    public Fragment_profile(ArrayList<GiveItem> giveItems, ArrayList<RequestItem> requestItems) {
        this.giveItems = giveItems;
        this.requestItems = requestItems;
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
        populateEventList();
        return view;
    }

    private void addTolistTest() {
        Log.d(TAG, "addTolistTest: ");
        Bitmap userCustomImage = null;
        User nathan = new User("nathan","email","passord","netanya","054","photo");
        giveItems.add(new GiveItem("1000","tv","electronic","new","","no need",userCustomImage,"27/10",nathan));
        giveItems.add(new GiveItem("1000","tv","electronic","new","","no need",userCustomImage,"27/10",nathan));
        giveItems.add(new GiveItem("1000","tv","electronic","new","","no need",userCustomImage,"27/10",nathan));
        giveItems.add(new GiveItem("1000","tv","electronic","new","","no need",userCustomImage,"27/10",nathan));
        giveItems.add(new GiveItem("1000","tv","electronic","new","","no need",userCustomImage,"27/10",nathan));
        giveItems.add(new GiveItem("1000","tv","electronic","new","","no need",userCustomImage,"27/10",nathan));

    }

    private void findViews() {
        profile_IMG_picture = view.findViewById(R.id.profile_IMG_picture);
        profile_LBL_name = view.findViewById(R.id.profile_LBL_name);
        profile_LBL_city = view.findViewById(R.id.profile_LBL_city);
        profile_LBL_phone = view.findViewById(R.id.profile_LBL_phone);
        profile_LBL_mail = view.findViewById(R.id.profile_LBL_mail);
        profile_RCV_post = view.findViewById(R.id.profile_RCV_post);

    }

    private void populateEventList() {
        Log.d(TAG, "populateEventList: Populating list");
        RecyclerViewGiveAdapter recyclerViewGiveAdapter = new RecyclerViewGiveAdapter(getContext(), giveItems);
        profile_RCV_post.setAdapter(recyclerViewGiveAdapter);
    }

    private void initItemsFragment() {
        Log.d(TAG, "initItemsFragment: Initing main list");
        MainListFragment mainListFragment = new MainListFragment(giveItems, requestItems);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_LAY_recyclerFrame, mainListFragment);
        transaction.commit();
    }
}