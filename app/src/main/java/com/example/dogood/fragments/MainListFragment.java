package com.example.dogood.fragments;

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

import com.example.dogood.R;
import com.example.dogood.adapters.RecyclerViewGiveAdapter;
import com.example.dogood.objects.GiveItem;
import com.example.dogood.objects.RequestItem;

import java.util.ArrayList;

public class MainListFragment extends Fragment {
    private static final String TAG = "Dogood";
    protected View view;
    private RecyclerView recyclerView;
    private ArrayList<GiveItem> giveItems;
    private ArrayList<RequestItem> requestItems;

    public MainListFragment() {
    }

    public MainListFragment(ArrayList<GiveItem> giveItems, ArrayList<RequestItem> requestItems) {
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
            view = inflater.inflate(R.layout.fragment_main_recycler, container, false);
        }
        populateEventList();
        return view;
    }


    /**
     * A method to populate the main items recyclerview
     */
    private void populateEventList() {
        Log.d(TAG, "populateEventList: Populating list");
        recyclerView = view.findViewById(R.id.mainList_LST_mainRecycler);
        RecyclerViewGiveAdapter recyclerViewGiveAdapter = new RecyclerViewGiveAdapter(getContext(), giveItems);
        recyclerView.setAdapter(recyclerViewGiveAdapter);
    }
}