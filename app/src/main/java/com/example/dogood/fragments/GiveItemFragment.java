package com.example.dogood.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogood.R;
import com.example.dogood.activities.NewAskItemActivity;
import com.example.dogood.activities.NewGiveItemActivity;
import com.example.dogood.adapters.RecyclerViewGiveAdapter;
import com.example.dogood.objects.GiveItem;
import com.example.dogood.objects.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;

public class GiveItemFragment extends Fragment {
    private static final String TAG = "Dogood";
    private static final int NEW_GIVE_ITEM_RESULT_CODE = 1011;
    private static final String ITEM_COUNT = "itemCount";

    public static final String CURRENT_USER = "currentUser";


    protected View view;
    private RecyclerView recyclerView;
    private ArrayList<GiveItem> giveItems;
    private FloatingActionButton addItem;
    private User myUser;


    public GiveItemFragment() {
    }

    public GiveItemFragment(ArrayList<GiveItem> giveItems, User user) {
        this.giveItems = giveItems;
        this.myUser = user;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_give_items, container, false);
        }
        initViews();
        populateItemsList();
        return view;
    }

    public void hideFloatingButton() {
        addItem.setVisibility(View.GONE);
    }

    public void showFloatingButton() {
        addItem.setVisibility(View.VISIBLE);
    }

    /**
     * A method to initialize the views
     */
    private void initViews() {
        Log.d(TAG, "initViews: Initing list fragment views");
        addItem = view.findViewById(R.id.giveFragment_BTN_addItemButton);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddItemActivity();
            }
        });
    }

    /**
     * A method to move to add item activity
     */
    private void openAddItemActivity() {
        Log.d(TAG, "openAddItemActivity: ");
        Intent intent = new Intent(getActivity(), NewGiveItemActivity.class);
        Gson gson = new Gson();
        String userJson = gson.toJson(myUser);
        intent.putExtra(CURRENT_USER, userJson);
        intent.putExtra(ITEM_COUNT,giveItems.size());
        startActivityForResult(intent, NEW_GIVE_ITEM_RESULT_CODE);
    }


    /**
     * A method to populate the items list
     */
    private void populateItemsList() {
        Log.d(TAG, "populateEventList: Populating list with:");
        if (giveItems != null) {
            recyclerView = view.findViewById(R.id.giveFragment_LST_mainRecycler);
            RecyclerViewGiveAdapter recyclerViewGiveAdapter = new RecyclerViewGiveAdapter(getContext(), giveItems);
            recyclerView.setAdapter(recyclerViewGiveAdapter);
        } else {
            Log.d(TAG, "populateItemsList: Empty giveItem array");
        }
    }
}