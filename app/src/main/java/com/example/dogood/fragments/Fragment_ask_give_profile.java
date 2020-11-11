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

import com.example.dogood.MainActivity;
import com.example.dogood.R;
import com.example.dogood.activities.NewAskItemActivity;
import com.example.dogood.activities.NewGiveItemActivity;
import com.example.dogood.adapters.RecyclerViewAskAdapter;
import com.example.dogood.adapters.RecyclerViewGiveAdapter;
import com.example.dogood.objects.AskItem;
import com.example.dogood.objects.GiveItem;
import com.example.dogood.objects.User;
import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;


public class Fragment_ask_give_profile extends Fragment implements MainActivity.IOnBackPressed {

    private static final String TAG = "Fragment_ask_give_profile";
    private static final int NEW_GIVE_ITEM_RESULT_CODE = 1011;
    private static final int NEW_ASK_ITEM_RESULT_CODE = 1012;
    private static final String ITEM_COUNT = "itemCount";
    public static final String CURRENT_USER = "currentUser";

    protected View view;
    private RecyclerView recyclerView;
    private User myUser;
    private ArrayList<GiveItem> giveItems;
    private ArrayList<AskItem> askItems;

    private FloatingActionMenu giveAskFragment_BTM_menu;
    private FloatingActionButton giveAskFragment_BTM_menu_item1 ;
    private FloatingActionButton giveAskFragment_BTM_menu_item2 ;



    public Fragment_ask_give_profile() { }

    public Fragment_ask_give_profile(User user,ArrayList<GiveItem> giveItems, ArrayList<AskItem> askItems) {
        this.myUser = user;
        this.giveItems = giveItems;
        this.askItems = askItems;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_ask_give_profile, container, false);
        }
        initViews();
        populateItemsListGive();

        giveAskFragment_BTM_menu_item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGiveItemActivity();
                populateItemsListGive();
            }
        });

        giveAskFragment_BTM_menu_item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAskItemActivity();
                populateItemsListAsk();
            }
        });

        return view;
    }

    private void openGiveItemActivity() {
        Log.d(TAG, "openAddItemActivity: ");
        Intent intent = new Intent(getActivity(), NewGiveItemActivity.class);
        Gson gson = new Gson();
        String userJson = gson.toJson(myUser);
        intent.putExtra(CURRENT_USER, userJson);
        intent.putExtra(ITEM_COUNT,giveItems.size());
        startActivityForResult(intent, NEW_GIVE_ITEM_RESULT_CODE);
    }

    private void openAskItemActivity() {
        Log.d(TAG, "openAddItemActivity: ");
        Intent intent = new Intent(getActivity(), NewAskItemActivity.class);
        Gson gson = new Gson();
        String userJson = gson.toJson(myUser);
        intent.putExtra(CURRENT_USER,userJson);
        intent.putExtra(ITEM_COUNT,askItems.size());
        startActivityForResult(intent, NEW_ASK_ITEM_RESULT_CODE);
    }

    private void initViews() {
        giveAskFragment_BTM_menu = view.findViewById(R.id.giveAskFragment_BTM_menu);
        giveAskFragment_BTM_menu_item1 = view.findViewById(R.id.giveAskFragment_BTM_menu_item1);
        giveAskFragment_BTM_menu_item2 = view.findViewById(R.id.giveAskFragment_BTM_menu_item2);
    }

    private void populateItemsListAsk() {
        Log.d(TAG, "populateEventList: Populating list ");
        if (myUser.getAskItems() == null) {
            Log.d(TAG, "populateItemsList: no askItems");
        } else {
            recyclerView = view.findViewById(R.id.giveAskFragment_LST_mainRecycler);
            RecyclerViewAskAdapter recyclerViewAskAdapter = new RecyclerViewAskAdapter(getContext(), myUser.getAskItems());
            recyclerView.setAdapter(recyclerViewAskAdapter);
        }
    }

    private void populateItemsListGive() {
        Log.d(TAG, "populateEventList: Populating list with:");
        if (myUser.getGiveItems() != null) {
            recyclerView = view.findViewById(R.id.giveAskFragment_LST_mainRecycler);
            RecyclerViewGiveAdapter recyclerViewGiveAdapter = new RecyclerViewGiveAdapter(getContext(), myUser.getGiveItems());
            recyclerView.setAdapter(recyclerViewGiveAdapter);
        } else {
            Log.d(TAG, "populateItemsList: Empty giveItem array");
        }
    }

    @Override
    public boolean onBackPressed() {
        if (giveAskFragment_BTM_menu.isOpened()){
            giveAskFragment_BTM_menu.close(true);
            return true;
        }else{
            return false;
        }

    }
}
