package com.example.dogood.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.dogood.R;
import com.google.android.material.button.MaterialButton;

public class SortFilterFragment extends Fragment {
    private static final String TAG = "Dogood";
    private Context context;
    protected View view;

    private MaterialButton sortButton;
    private MaterialButton filterButton;

    public SortFilterFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_sortfilter, container, false);
        }
        initViews();
        return view;
    }

    private void initViews() {
        Log.d(TAG, "initViews: ");
        sortButton = view.findViewById(R.id.sortfilter_BTN_sort);
        filterButton = view.findViewById(R.id.sortfilter_BTN_filter);

        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortItems();
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterItems();
            }
        });
    }

    /**
     * A method to filter items by criteria
     */
    private void filterItems() {
        Log.d(TAG, "filterItems: ");
    }

    /**
     * A method to sort items by criteria
     */
    private void sortItems() {
        Log.d(TAG, "sortItems: ");
    }

}
