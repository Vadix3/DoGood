package com.example.dogood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.dogood.activities.NewGiveItemActivity;
import com.example.dogood.fragments.MainListFragment;
import com.example.dogood.objects.GiveItem;
import com.example.dogood.objects.RequestItem;
import com.example.dogood.objects.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "Dogood";

    //private MaterialToolbar main_TLB_title;
    private Toolbar main_TLB_head;
    private MaterialSearchView main_SRC_search;
    private BottomAppBar main_BAB_menu;
    private DrawerLayout main_LAY_main;
    private NavigationView main_NGV_side;
    private FloatingActionButton addItem;

    private ArrayList<GiveItem> giveItems;
    private ArrayList<RequestItem> requestItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        addSide();


        /**TESTING ARRAYS*/
        initTestArrays();


        initItemsFragment();

        searchAction();
    }

    private void findViews() {
        Log.d(TAG, "findViews: ");

        main_TLB_head = findViewById(R.id.main_TLB_head);
        main_SRC_search = findViewById(R.id.main_SRC_search);
        main_BAB_menu = findViewById(R.id.main_BAB_menu);
        main_LAY_main = findViewById(R.id.main_LAY_main);
        main_NGV_side = findViewById(R.id.main_NGV_side);
        addItem = findViewById(R.id.main_BTN_addItemButton);
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
        startActivityForResult(new Intent(MainActivity.this, NewGiveItemActivity.class), 101);
    }

    // open side menu
    private void addSide() {
        Log.d(TAG, "addSide: ");
        main_NGV_side.bringToFront();
        setSupportActionBar(main_TLB_head);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, main_LAY_main, main_TLB_head, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        main_LAY_main.addDrawerListener(toggle);
        toggle.syncState();
        main_NGV_side.setNavigationItemSelectedListener(this);
    }

    // side menu action and move to fragment when bottom press
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_profile) {
            Log.d(TAG, "onNavigationItemSelected: profile press");
        } else if (item.getItemId() == R.id.menu_logout) {
            Log.d(TAG, "onNavigationItemSelected: logout press");
        } else if (item.getItemId() == R.id.menu_rate) {
            Log.d(TAG, "onNavigationItemSelected: rate press");
        } else if (item.getItemId() == R.id.menu_share) {
            Log.d(TAG, "onNavigationItemSelected: share press");
        }
        return true;
    }

    // creation of the side menu
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: ");

        getMenuInflater().inflate(R.menu.top_app_bar, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        main_SRC_search.setMenuItem(item);

        return true;

    }

    // Used we side menu is open you can close and stay in application
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        if (main_LAY_main.isDrawerOpen(GravityCompat.START)) {
            main_LAY_main.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void searchAction(){
        Log.d(TAG, "searchAction: ");
        String[] sug = new String[giveItems.size()];
        for(int i = 0 ; i < giveItems.size() ; i++){
            sug[i] = giveItems.get(i).getName();
        }

        main_SRC_search.setSuggestions(sug);

        main_SRC_search.setVoiceSearch(true);
        
        main_SRC_search.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        main_SRC_search.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult: ");
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    main_SRC_search.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        giveItems.add(new GiveItem("123123", "Coffee Maker", "Electronics", "New"
                , "Free", "Nespresso", "Photo URL", "11/01/20", testUser));
        giveItems.add(new GiveItem("123123", "Testing 1", "Electronics", "New"
                , "Free", "Nespresso", "Photo URL", "11/01/20", testUser));
        giveItems.add(new GiveItem("123123", "Testing 2", "Electronics", "New"
                , "Free", "Nespresso", "Photo URL", "11/01/20", testUser));
        giveItems.add(new GiveItem("123123", "Testing 3", "Electronics", "New"
                , "Free", "Nespresso", "Photo URL", "11/01/20", testUser));
        giveItems.add(new GiveItem("123123", "Testing 4", "Electronics", "New"
                , "Free", "Nespresso", "Photo URL", "11/01/20", testUser));
        giveItems.add(new GiveItem("123123", "Testing 5", "Electronics", "New"
                , "Free", "Nespresso", "Photo URL", "11/01/20", testUser));
        giveItems.add(new GiveItem("123123", "Testing 6", "Electronics", "New"
                , "Free", "Nespresso", "Photo URL", "11/01/20", testUser));
        giveItems.add(new GiveItem("123123", "Testing 7", "Electronics", "New"
                , "Free", "Nespresso", "Photo URL", "11/01/20", testUser));
        giveItems.add(new GiveItem("123123", "Testing 8", "Electronics", "New"
                , "Free", "Nespresso", "Photo URL", "11/01/20", testUser));
        giveItems.add(new GiveItem("123123", "Testing 9", "Electronics", "New"
                , "Free", "Nespresso", "Photo URL", "11/01/20", testUser));
        giveItems.add(new GiveItem("123123", "Testing 10", "Electronics", "New"
                , "Free", "Nespresso", "Photo URL", "11/01/20", testUser));


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