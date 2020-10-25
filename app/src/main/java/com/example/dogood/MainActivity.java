package com.example.dogood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.example.dogood.fragments.MainListFragment;
import com.example.dogood.objects.GiveItem;
import com.example.dogood.objects.RequestItem;
import com.example.dogood.objects.User;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Dogood";

    private ArrayList<GiveItem> giveItems;
    private ArrayList<RequestItem> requestItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /** TEST ARRAYS*/
        initTestArrays();


        initItemsFragment();

    }

    /**
     * A method to create test arrays
     */
    private void initTestArrays() {
        Log.d(TAG, "initTestArrays: Creating test arrays");
        User testUser = new User("Vadim", "dogoodapp1@gmail.com", "123456"
                , "Netanya", "0541234567", "Photo URL");

        giveItems = new ArrayList<>();
        requestItems = new ArrayList<>();

        requestItems.add(new RequestItem("12345", "Refrigirator", "Appliances", "Tel Aviv"
                , "A simple white fridge", "21/09/2020", testUser, false));
        requestItems.add(new RequestItem("12345", "Microwave", "Appliances", "Petah Tikva"
                , "A simple white microwave", "21/09/2020", testUser, false));


        giveItems.add(new GiveItem("123123", "Computer", "Computers", "New"
                , "Free", "A 2 year old working computer", "Photo URL", "23/04/20", testUser));
        giveItems.add(new GiveItem("123123", "Oven", "Appliances", "New"
                , "Free", "De Longhi oven", "Photo URL", "13/05/20", testUser));
        giveItems.add(new GiveItem("123123", "Television", "Electronics", "New"
                , "Free", "LG 50' tv", "Photo URL", "11/01/20", testUser));

    }

    /**
     * A method to init the main list
     */
    private void initItemsFragment() {
        Log.d(TAG, "initItemsFragment: Initing main list");
        MainListFragment mainListFragment = new MainListFragment(giveItems, requestItems);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_LAY_recyclerFrame, mainListFragment);
        transaction.commit();
    }
}