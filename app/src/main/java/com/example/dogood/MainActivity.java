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
import android.widget.Toast;

import com.example.dogood.fragments.AskItemFragment;
import com.example.dogood.fragments.Fragment_profile;
import com.example.dogood.fragments.GiveItemFragment;
import com.example.dogood.fragments.HomeTabFragment;
import com.example.dogood.objects.FirestoreDataContainer;
import com.example.dogood.objects.GiveItem;
import com.example.dogood.objects.RequestItem;
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
    private static final String GIVE_ITEMS_ARRAY = "giveItems";
    private static final int NEW_GIVE_ITEM_RESULT_CODE = 1011;
    private static final int PROFILE_PAGE_RESULT_CODE = 1012;
    private static final int CAMERA_PERMISSION_REQUSETCODE = 122;
    private static final int CAMERA_PERMISSION_SETTINGS_REQUSETCODE = 123;
    private static final int CAMERA_PICTURE_REQUEST = 124;
    private static final int RETURN_NEW_USER = 125;


    //private MaterialToolbar main_TLB_title;
    private Toolbar main_TLB_head;
    private MaterialSearchView main_SRC_search;
    private ConstraintLayout main_LAY_main;

    private ArrayList<GiveItem> giveItems;
    private ArrayList<RequestItem> requestItems;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        /**TESTING ARRAYS*/
        giveItems = new ArrayList<>();
        requestItems = new ArrayList<>();


        fetchItemsFromFirestore();
        initListChangeListener();
        initBottomNavigationMenu();
        searchAction();
        setHomeFragment();
    }

    /**
     * A method to initialize bottom navigation menu
     */
    private void initBottomNavigationMenu() {
        Log.d(TAG, "initBottomNavigationMenu: Bottom navigation init");
        BottomNavigationView bottomNavigationView = findViewById(R.id.main_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_menu_home:
                        setHomeFragment();
                        break;
                    case R.id.bottom_menu_give:
                        setGiveFragment();
                        break;
                    case R.id.bottom_menu_ask:
                        setAskFragment();
                        break;
                    case R.id.bottom_menu_profile:
                        setProfileFragment();
                        break;
                }
                return true;
            }
        });
    }

    /**
     * A method to set the profile page fragment
     */
    private void setProfileFragment() {
        Log.d(TAG, "onNavigationItemSelected: profile");
        Fragment_profile fragment_profile = new Fragment_profile(giveItems, requestItems);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_LAY_mainFragmentsLayout, fragment_profile);
        transaction.commit();
    }

    /**
     * A method to set the Ask fragment
     */
    private void setAskFragment() {
        Log.d(TAG, "onNavigationItemSelected: ask");
        AskItemFragment giveItemFragment = new AskItemFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_LAY_mainFragmentsLayout, giveItemFragment);
        transaction.commit();
    }

    /**
     * A method to set the Give fragment
     */
    private void setGiveFragment() {
        Log.d(TAG, "onNavigationItemSelected: give");
        initGiveItemsFragment();
    }

    /**
     * A method to set the Home fragment
     */
    private void setHomeFragment() {
        Log.d(TAG, "onNavigationItemSelected: home");
        HomeTabFragment giveItemFragment = new HomeTabFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_LAY_mainFragmentsLayout, giveItemFragment);
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
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    private void findViews() {
        Log.d(TAG, "findViews: ");

        main_TLB_head = findViewById(R.id.main_TLB_head);
        main_SRC_search = findViewById(R.id.main_SRC_search);
        main_LAY_main = findViewById(R.id.main_LAY_main);


    }

    /**
     * A method to upload items array to firestore
     */
    private void saveItemsToFirestore() {
        Log.d(TAG, "saveItemsToFirestore: Saving items to firestore: " + giveItems.toString());

        FirestoreDataContainer dataContainer = new FirestoreDataContainer(giveItems);

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
                    Log.d(TAG, "onSuccess: GiveItems: " + giveItems);
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
        Log.d(TAG, "onActivityResult: ");
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

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
                Log.d(TAG, "onActivityResult: Got new item from activity");

                if (data != null) {
                    String gotNewItem = data.getStringExtra(NEW_GIVE_ITEM);
                    Log.d(TAG, "onActivityResult: Got data from activity");
                    Log.d(TAG, "onActivityResult: Got new item after intent: " + gotNewItem);
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
            //TODO: Add switch for profile page
        }
    }

    /**
     * A method to init the main list
     */
    private void initGiveItemsFragment() {
        Log.d(TAG, "initItemsFragment: Initing main list with: " + giveItems.toString());
        GiveItemFragment giveItemFragment = new GiveItemFragment(giveItems, requestItems);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_LAY_mainFragmentsLayout, giveItemFragment);
        transaction.commit();
    }


}