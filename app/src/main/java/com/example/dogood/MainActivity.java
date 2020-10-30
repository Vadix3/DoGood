package com.example.dogood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.dogood.fragments.AskItemFragment;
import com.example.dogood.fragments.Fragment_profile;
import com.example.dogood.fragments.GiveItemFragment;
import com.example.dogood.fragments.HomeTabFragment;
import com.example.dogood.objects.AskItem;
import com.example.dogood.objects.FirestoreDataContainer;
import com.example.dogood.objects.GiveItem;
import com.example.dogood.objects.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "Dogood";
    private static final String NEW_GIVE_ITEM = "111";
    private static final String NEW_ASK_ITEM = "112";
    private static final String GIVE_ITEMS_ARRAY = "giveItems";
    private static final int NEW_GIVE_ITEM_RESULT_CODE = 1011;
    private static final int NEW_ASK_ITEM_RESULT_CODE = 1012;
    private static final int PROFILE_PAGE_RESULT_CODE = 1012;
    private static final int CAMERA_PERMISSION_REQUSETCODE = 122;
    private static final int CAMERA_PERMISSION_SETTINGS_REQUSETCODE = 123;
    private static final int CAMERA_PICTURE_REQUEST = 124;
    private static final int RETURN_NEW_USER = 125;

    private boolean isDataLoaded = false; // A boolean var to open bottom menu in case of data loaded;

    //private MaterialToolbar main_TLB_title;
    private Toolbar main_TLB_head;
    private MaterialSearchView main_SRC_search;
    private ConstraintLayout main_LAY_main;

    private FrameLayout mainFragment;
    private FrameLayout giveFragment;
    private FrameLayout askFragment;
    private FrameLayout profileFragment;

    private HomeTabFragment homeTabFragment;
    private GiveItemFragment giveItemFragment;
    private AskItemFragment askItemFragment;
    private Fragment_profile fragment_profile;

    private BottomNavigationView bottomNavigationView;

    private ArrayList<GiveItem> giveItems;
    private ArrayList<AskItem> askItems;
    private User testUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        giveItems = new ArrayList<>();
        askItems = new ArrayList<>();

        /**TESTING*/
        testUser = new User("Test User", "Test@user.com", "testPass"
                , "Test City", "0501234567", "testPhoto");

        fetchItemsFromFirestore(); // Get data from firestore and initialize the fragments
        initListChangeListener(); // Init listener for item change detection
        searchAction();
    }

    private void findViews() {
        Log.d(TAG, "findViews: ");

        main_TLB_head = findViewById(R.id.main_TLB_head);
        main_SRC_search = findViewById(R.id.main_SRC_search);
        main_LAY_main = findViewById(R.id.main_LAY_main);
         onCreateOptionsMenu(main_TLB_head.getMenu());

    }

    /**
     * A method to initialize the page fragments
     */
    private void initPageFragments() {
        Log.d(TAG, "initPageFragments: Creating fragments");
        mainFragment = findViewById(R.id.main_LAY_mainPageFragment);
        giveFragment = findViewById(R.id.main_LAY_givePageFragment);
        askFragment = findViewById(R.id.main_LAY_askPageFragment);
        profileFragment = findViewById(R.id.main_LAY_profilePageFragment);

        setProfileFragment();
        setGiveFragment();
        setHomeFragment();
        setAskFragment();


        giveFragment.setVisibility(View.GONE);
        askFragment.setVisibility(View.GONE);
        profileFragment.setVisibility(View.GONE);


    }

    /**
     * A method to update fragments after a change in lists has occured
     */
    private void updateFragments() {
        Log.d(TAG, "updateFragments: Updating fragments");
        setProfileFragment();
        setGiveFragment();
        setHomeFragment();
        setAskFragment();
    }

    /**
     * A method to initialize bottom navigation menu
     */
    private void initBottomNavigationMenu() {
        Log.d(TAG, "initBottomNavigationMenu: Bottom navigation init");
        bottomNavigationView = findViewById(R.id.main_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_menu_home:
                        showFragment(mainFragment);
                        break;
                    case R.id.bottom_menu_give:
                        showFragment(giveFragment);
                        break;
                    case R.id.bottom_menu_ask:
                        showFragment(askFragment);
                        break;
                    case R.id.bottom_menu_profile:
                        showFragment(profileFragment);
                        break;
                }
                return true;
            }
        });
    }

    /**
     * A method to show the selected fragment and hide all the others
     */
    private void showFragment(FrameLayout fragment) {
        Log.d(TAG, "showFragment: Showing fragment: " + fragment.toString());

        mainFragment.setVisibility(View.GONE);
        giveFragment.setVisibility(View.GONE);
        askFragment.setVisibility(View.GONE);
        profileFragment.setVisibility(View.GONE);

        fragment.setVisibility(View.VISIBLE);
    }

    /**
     * A method to set the profile page fragment
     */
    private void setProfileFragment() {
        Log.d(TAG, "onNavigationItemSelected: profile");
        fragment_profile = new Fragment_profile(giveItems, askItems);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_LAY_profilePageFragment, fragment_profile);
        transaction.commit();
    }

    /**
     * A method to set the Ask fragment
     */
    private void setAskFragment() {
        Log.d(TAG, "onNavigationItemSelected: ask");
        askItemFragment = new AskItemFragment(askItems);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_LAY_askPageFragment, askItemFragment);
        transaction.commit();
    }

    /**
     * A method to set the Give fragment
     */
    private void setGiveFragment() {
        Log.d(TAG, "initItemsFragment: Initing give list");
        giveItemFragment = new GiveItemFragment(giveItems);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_LAY_givePageFragment, giveItemFragment);
        transaction.commit();
    }

    /**
     * A method to set the Home fragment
     */
    private void setHomeFragment() {
        Log.d(TAG, "onNavigationItemSelected: home");
        homeTabFragment = new HomeTabFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_LAY_mainPageFragment, homeTabFragment);
        transaction.commit();
    }

    /**
     * A method to create a list change listener
     */
    private void initListChangeListener() {
        Log.d(TAG, "initListChangeListener: Creating list change listener");
        final DocumentReference docRef = db.collection("data").document(GIVE_ITEMS_ARRAY);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "onEvent: Exception: " + e.getMessage());
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: HAS NEW DATA! " + snapshot.getData());
                    FirestoreDataContainer container = snapshot.toObject(FirestoreDataContainer.class);
                    giveItems = container.getGiveItems();
                    updateFragments();
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    /**
     * A method to upload items array to firestore
     */
    private void saveItemsToFirestore() {
        Log.d(TAG, "saveItemsToFirestore: Saving items to firestore: " + giveItems.toString());

        FirestoreDataContainer dataContainer = new FirestoreDataContainer(giveItems, askItems);

        db.collection("data").document(GIVE_ITEMS_ARRAY)
                .set(dataContainer)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }


    /**
     * A method to download items array from firestore
     */
    private void fetchItemsFromFirestore() {
        Log.d(TAG, "fetchItemsFromFirestore: Fetching items from firestore");
        String path = "data/" + GIVE_ITEMS_ARRAY;

        DocumentReference docRef = db.document(path);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Log.d(TAG, "onSuccess: document exists! " + documentSnapshot.getData().toString());

                    FirestoreDataContainer container = documentSnapshot.toObject(FirestoreDataContainer.class);
                    giveItems = container.getGiveItems();
                    askItems = container.getAskItems();
                    Log.d(TAG, "onSuccess: GiveItems: " + giveItems.toString());
                    Log.d(TAG, "onSuccess: askItems: " + askItems.toString());
                    initPageFragments();
                    initBottomNavigationMenu(); // initialize bottom navigation menu
                    bottomNavigationView.setVisibility(View.VISIBLE);

                } else {
                    Log.d(TAG, "onSuccess: Document does not exist!");
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Exception: " + e.getMessage());
            }
        });
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


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("nathan", "onCreateOptionsMenu: ");
        getMenuInflater().inflate(R.menu.top_app_bar, menu);

       MenuItem item = menu.findItem(R.id.top_app_bar_search);
       main_SRC_search.setMenuItem(item);

        return true;
    }

    private void searchAction() {
        Log.d(TAG, "searchAction: ");
        String[] sug = new String[giveItems.size()];
        for (int i = 0; i < giveItems.size(); i++) {
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
        Log.d(TAG, "onActivityResult: request code: " + requestCode + " Result code: " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {

            /** We got from voice listener*/
            case MaterialSearchView.REQUEST_VOICE:
                if (resultCode == RESULT_OK) {
                    ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (matches != null && matches.size() > 0) {
                        String searchWrd = matches.get(0);
                        if (!TextUtils.isEmpty(searchWrd)) {
                            main_SRC_search.setQuery(searchWrd, false);
                        }
                    }
                    return;
                }
                break;
            case NEW_GIVE_ITEM_RESULT_CODE:
                Log.d(TAG, "onActivityResult: Got new item from give activity");

                if (data != null) {
                    String gotNewItem = data.getStringExtra(NEW_GIVE_ITEM);
                    Log.d(TAG, "onActivityResult: Got new give item: " + gotNewItem);
                    Toast.makeText(this, "Item added successfully!", Toast.LENGTH_SHORT).show();
                    Gson gson = new Gson();
                    Type itemType = new TypeToken<GiveItem>() {
                    }.getType();
                    GiveItem temp = gson.fromJson(gotNewItem, itemType);
                    Log.d(TAG, "onActivityResult: Got item as item: " + temp.toString());
                    giveItems.add(temp);
                    saveItemsToFirestore();
                } else {
                    Log.d(TAG, "onActivityResult: User canceled new item input");
                }
                break;
            case NEW_ASK_ITEM_RESULT_CODE:
                Log.d(TAG, "onActivityResult: Got new item from ask activity");
                if (data != null) {
                    String gotNewItem = data.getStringExtra(NEW_ASK_ITEM);
                    Log.d(TAG, "onActivityResult: Got ask item: " + gotNewItem);
                    Toast.makeText(this, "Item added successfully!", Toast.LENGTH_SHORT).show();
                    Gson gson = new Gson();
                    Type itemType = new TypeToken<AskItem>() {
                    }.getType();
                    AskItem temp = gson.fromJson(gotNewItem, itemType);
                    Log.d(TAG, "onActivityResult: Got item as item: " + temp.toString());
                    askItems.add(temp);
                    saveItemsToFirestore();
                } else {
                    Log.d(TAG, "onActivityResult: User canceled new item input");
                }
                //TODO: Add switch for profile page
        }
    }
}