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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogood.MainActivity;
import com.example.dogood.R;
import com.example.dogood.activities.NewGiveItemActivity;
import com.example.dogood.adapters.RecyclerViewGiveAdapter;
import com.example.dogood.objects.GiveItem;
import com.example.dogood.objects.RequestItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class GiveItemFragment extends Fragment {
    private static final String TAG = "Dogood";
    private static final int NEW_GIVE_ITEM_RESULT_CODE = 1011;

    protected View view;
    private RecyclerView recyclerView;
    private ArrayList<GiveItem> giveItems;
    private ArrayList<RequestItem> requestItems;
    private FloatingActionButton addItem;


    public GiveItemFragment() {
    }

    public GiveItemFragment(ArrayList<GiveItem> giveItems, ArrayList<RequestItem> requestItems) {
        this.giveItems = giveItems;
        this.requestItems = requestItems;
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
        populateEventList();
        return view;
    }

    /**
     * A method to initialize the views
     */
    private void initViews() {
        Log.d(TAG, "initViews: Initing list fragment views");
        addItem = view.findViewById(R.id.main_BTN_addItemButton);
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
        startActivityForResult(new Intent(getActivity(), NewGiveItemActivity.class), NEW_GIVE_ITEM_RESULT_CODE);
    }


    /**
     * A method to populate the main items recyclerview
     */
    private void populateEventList() {
        Log.d(TAG, "populateEventList: Populating list with: " + giveItems.toString());
        recyclerView = view.findViewById(R.id.mainList_LST_mainRecycler);
        RecyclerViewGiveAdapter recyclerViewGiveAdapter = new RecyclerViewGiveAdapter(getContext(), giveItems);
        recyclerView.setAdapter(recyclerViewGiveAdapter);
    }
}