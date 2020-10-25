package com.example.dogood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "Dogood";

    private MaterialToolbar main_TLB_title;
    private DrawerLayout main_LAY_main;
    private NavigationView main_NGV_side;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Ya gever");

        findViews();
    }

    private void findViews() {
        Log.d(TAG, "findViews: ");
        main_TLB_title  = findViewById(R.id.main_TLB_title);
        main_LAY_main = findViewById(R.id.main_LAY_main);
        main_NGV_side = findViewById(R.id.main_NGV_side);

        main_TLB_title.setOnClickListener(openMenu);

    }

    View.OnClickListener openMenu = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addSide();
        }
    };



    private void addSide() {
        Log.d(TAG, "addSide: ");
        main_NGV_side.bringToFront();
        setSupportActionBar(main_TLB_title);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, main_LAY_main, main_TLB_title, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        main_LAY_main.addDrawerListener(toggle);
        toggle.syncState();
        main_NGV_side.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onNavigationItemSelected: ");
        switch (item.getItemId()) {
            case R.id.menu_logout:

            case R.id.menu_rate:
        }

        return true;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: ");
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (main_LAY_main.isDrawerOpen(GravityCompat.START)) {
            main_LAY_main.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fm = getFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                Log.i(TAG, "onBackPressed: popping backstack");
                fm.popBackStack();
            } else {
                Log.i(TAG, "onBackPressed: nothing on backstack, calling super");
                super.onBackPressed();
            }
        }
    }
}