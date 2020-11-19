package com.example.dogood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dogood.Dialogs.AboutUsDialog;
import com.example.dogood.activities.Activity_login;
import com.example.dogood.activities.EditItemActivity;
import com.example.dogood.activities.ItemDetailsActivity;
import com.example.dogood.fragments.AskItemFragment;
import com.example.dogood.fragments.Fragment_profile;
import com.example.dogood.fragments.GiveItemFragment;
import com.example.dogood.fragments.HomeTabFragment;
import com.example.dogood.interfaces.EditProfileListener;
import com.example.dogood.interfaces.ItemDetailsListener;
import com.example.dogood.interfaces.PhotoModeListener;
import com.example.dogood.objects.AskItem;
import com.example.dogood.objects.FirestoreDataContainer;
import com.example.dogood.objects.GiveItem;
import com.example.dogood.objects.User;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import guy4444.smartrate.SmartRate;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class MainActivity extends AppCompatActivity implements ItemDetailsListener, PhotoModeListener, EditProfileListener {
    private static final String TAG = "Dogood";
    private static final String NEW_GIVE_ITEM = "111";
    private static final String NEW_ASK_ITEM = "112";
    private static final String ITEMS_DATA_CONTAINER = "dataContainer";
    private static final String USERS_COLLECTION = "users/";
    private static final String LOGIN_USER_EXTRA = "loginUser";
    private static final String OWNER_USER = "ownerUser";
    private static final String GIVE_ITEM = "giveItem";
    private static final String ASK_ITEM = "askItem";
    private static final String ITEM_TO_DEAL_WITH = "item_to_deal_with";
    private static final String TO_DELETE = "to_delete";
    private static final String IS_GIVE_ITEM = "is_give_item";
    private static final String LOGOUT = "logout";


    private static final int NEW_GIVE_ITEM_RESULT_CODE = 1011;
    private static final int NEW_ASK_ITEM_RESULT_CODE = 1012;
    private static final int UPDATE_PROFILE_RESULT_CODE = 1013;
    private static final int ITEM_DETAILS_RESULT_CODE = 1014;
    private static final int LOGOUT_CODE = 1015;

    private static final int CAMERA_PERMISSION_SETTINGS_REQUSETCODE = 123;
    private static final int STORAGE_PERMISSION_SETTINGS_REQUSETCODE = 133;
    private static final int CAMERA_PICTURE_REQUEST = 124;
    private static final int STORAGE_PICTURE_REQUEST = 125;

    private static final int SEARCH_IN_GIVE_ITEMS = 11;
    private static final int SEARCH_IN_ASK_ITEMS = 12;

    private boolean showingResults = false; // A boolean var to indicate that search results are showing.

    private boolean isProfileFragmentShowing = false;
    private boolean isGiveItemFragmentShowing = false;
    private boolean isAskItemFragmentShowing = false;
    private boolean isHomeFragmentShowing = false;

    //private MaterialToolbar main_TLB_title;
    private Toolbar main_TLB_head;
    private MaterialSearchView main_SRC_search;
    private MenuItem searchItem;
    private MenuItem optionsItem;

    private ConstraintLayout main_LAY_main;

    private FrameLayout mainFragment;
    private FrameLayout giveFragment;
    private FrameLayout askFragment;
    private FrameLayout profileFragment;


    private HomeTabFragment homeTabFragment;
    private GiveItemFragment giveItemFragment;
    private AskItemFragment askItemFragment;
    private Fragment_profile fragment_profile;


    private MaterialProgressBar loadingBar;
    private BottomNavigationView bottomNavigationView;

    private ArrayList<GiveItem> giveItems; // An array to store items to give
    private ArrayList<AskItem> askItems;  // An array to store needed items

    private ArrayList<GiveItem> giveResults = new ArrayList<>(); // An array to store give items search results
    private ArrayList<AskItem> askResults = new ArrayList<>(); // An array to store ask items search results

    private User myUser; // The current user that using the app

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        initViews();
        giveItems = new ArrayList<>();
        askItems = new ArrayList<>();
        initUser();
        initMenu();
    }

    private void initMenu() {
        setSupportActionBar(main_TLB_head);
    }

    /**
     * A method to get the user details from the login activity and fetch the valid info from firebase
     */
    private void initUser() {
        Log.d(TAG, "initUser: ");
        Gson gson = new Gson();
        String userJson = getIntent().getStringExtra(LOGIN_USER_EXTRA);
        myUser = gson.fromJson(userJson, User.class);
        Log.d(TAG, "initUser: Got user from login: " + myUser.toString());
        main_TLB_head.setTitle(getString(R.string.hello) + " " + myUser.getName().split(" ", 2)[0] + "!");

        String containerPath = USERS_COLLECTION + myUser.getEmail();

        DocumentReference containerDocRef = db.document(containerPath);

        containerDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Log.d(TAG, "onSuccess: user exists! ");
                    myUser = documentSnapshot.toObject(User.class);
                    Log.d(TAG, "onSuccess: Got user details from firebase: " + myUser.toString());

                } else {
                    Log.d(TAG, "onSuccess: user does not exist! saving new user");
                    // Save user to firebase
                    db.collection(USERS_COLLECTION).document(myUser.getEmail())
                            .set(myUser)
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
                fetchItemsFromFirestore(); // Get data from firestore and initialize the fragments
                initListChangeListener(); // Init listener for item change detection
                searchAction();
                initBottomNavigationMenu(); // initialize bottom navigation menu
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Exception: " + e.getMessage());
            }
        });
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
        setGiveFragment(giveItems);
        setAskFragment(askItems);
        setHomeFragment();


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
        Log.d(TAG, "updateFragments: give items: " + giveItems.toString());
        setGiveFragment(giveItems);
        setHomeFragment();
        Log.d(TAG, "updateFragments: ask items: " + askItems.toString());

        setAskFragment(askItems);
    }

    /**
     * A method to initialize bottom navigation menu
     */
    private void initBottomNavigationMenu() {
        Log.d(TAG, "initBottomNavigationMenu: Bottom navigation init");
        bottomNavigationView.setVisibility(View.VISIBLE);
        loadingBar.setIndeterminate(false);
        loadingBar.setVisibility(View.GONE);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (main_SRC_search.isSearchOpen()) {
                    main_SRC_search.closeSearch();
                }
                switch (item.getItemId()) {
                    case R.id.bottom_menu_home:
                        searchItem.setVisible(false);
                        if (mainFragment != null) {
                            main_TLB_head.setTitle(getString(R.string.hello) + " " + myUser.getName().split(" ", 2)[0] + "!");
                            showFragment(mainFragment);
                            isProfileFragmentShowing = false;
                            isGiveItemFragmentShowing = false;
                            isAskItemFragmentShowing = false;
                            isHomeFragmentShowing = true;
                        }
                        break;
                    case R.id.bottom_menu_give:
                        searchItem.setVisible(true);
                        if (giveFragment != null) {
                            if (giveFragment.getVisibility() != View.VISIBLE) {
                                main_TLB_head.setTitle(getResources().getString(R.string.object_to_give));
                                main_TLB_head.setNavigationIcon(null);
                                setGiveFragment(giveItems);
                                showFragment(giveFragment);

                                isProfileFragmentShowing = false;
                                isGiveItemFragmentShowing = true;
                                isAskItemFragmentShowing = false;
                                isHomeFragmentShowing = false;
                            }
                        }
                        break;
                    case R.id.bottom_menu_ask:
                        searchItem.setVisible(true);
                        if (askFragment != null) {
                            if (askFragment.getVisibility() != View.VISIBLE) {
                                main_TLB_head.setTitle(getResources().getString(R.string.object_to_ask));
                                main_TLB_head.setNavigationIcon(null);
                                setAskFragment(askItems);
                                showFragment(askFragment);

                                isProfileFragmentShowing = false;
                                isGiveItemFragmentShowing = false;
                                isAskItemFragmentShowing = true;
                                isHomeFragmentShowing = false;
                            }
                        }
                        break;
                    case R.id.bottom_menu_profile:
                        searchItem.setVisible(false);
                        if (profileFragment != null) {
                            main_TLB_head.setTitle(getResources().getString(R.string.profile));
                            showFragment(profileFragment);

                            isProfileFragmentShowing = true;
                            isGiveItemFragmentShowing = false;
                            isAskItemFragmentShowing = false;
                            isHomeFragmentShowing = false;
                        }
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
        Log.d(TAG, "onNavigationItemSelected: profile ");
        fragment_profile = new Fragment_profile(myUser, giveItems.size(), askItems.size(), this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_LAY_profilePageFragment, fragment_profile);
        transaction.commit();
    }

    /**
     * A method to set the Ask fragment
     */
    private void setAskFragment(ArrayList<AskItem> items) {
        Log.d(TAG, "onNavigationItemSelected: ask");
        askItemFragment = new AskItemFragment(items, myUser);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_LAY_askPageFragment, askItemFragment);
        transaction.commit();
    }

    /**
     * A method to set the Give fragment
     */
    private void setGiveFragment(ArrayList<GiveItem> items) {
        Log.d(TAG, "initItemsFragment: Initing give list");
        giveItemFragment = new GiveItemFragment(items, myUser);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_LAY_givePageFragment, giveItemFragment);
        transaction.commit();
    }

    /**
     * A method to set the Home fragment
     */
    private void setHomeFragment() {
        Log.d(TAG, "onNavigationItemSelected: home");
        if (askItems.size() > 0 && giveItems.size() > 0) {
            homeTabFragment = new HomeTabFragment(this, myUser, askItems.get(askItems.size() - 1)
                    , giveItems.get(giveItems.size() - 1));
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_LAY_mainPageFragment, homeTabFragment);
            transaction.commit();
        }
    }

    /**
     * A method to create a list change listener
     */
    private void initListChangeListener() {
        Log.d(TAG, "initListChangeListener: Creating list change listener");
        final DocumentReference docRef = db.collection("data").document(ITEMS_DATA_CONTAINER);
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
                    askItems = container.getAskItems();
                    Log.d(TAG, "onEvent: Got give items: " + giveItems.toString());
                    updateFragments();
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }


    /**
     * A method to initialize the views
     */
    private void initViews() {
        Log.d(TAG, "findViews: ");
        loadingBar = findViewById(R.id.main_BAR_progressBar);
        loadingBar.setIndeterminate(true);

        bottomNavigationView = findViewById(R.id.main_bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);
        main_TLB_head = findViewById(R.id.main_TLB_head);
        main_TLB_head.setTitle("");
        setSupportActionBar(main_TLB_head);


        main_SRC_search = findViewById(R.id.main_SRC_search);
        main_SRC_search.setBackgroundColor(getColor(R.color.white));
        main_SRC_search.showSuggestions();
    }

    /**
     * A method to enable back button on the toolbar
     */
    private void enableToolbarBackOption() {
        Log.d(TAG, "enableToolbarBackOption: enabling back option toolbar");
        main_TLB_head.setNavigationIcon(R.drawable.ic_chevron_left_black_36dp);
        main_TLB_head.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAllItems();
            }
        });

    }

    /**
     * A method to upload items array to firestore
     */
    private void saveItemsToFirestore() {
        Log.d(TAG, "saveItemsToFirestore: Saving items to firestore: ");

        Log.d(TAG, "saveItemsToFirestore: Saving: " + giveItems.toString() + "\n" + askItems.toString());
        FirestoreDataContainer dataContainer = new FirestoreDataContainer(giveItems, askItems);

        // Save arrays
        db.collection("data").document(ITEMS_DATA_CONTAINER)
                .set(dataContainer)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Exception: " + e.toString());
                    }
                });

//        for (AskItem maskItem : askItems) {
//            Log.d(TAG, "onActivityResult: " + maskItem.toString());
//        }
    }

    private void updateUserInFirestore() {
        Log.d(TAG, "updateUserInFirestore: saving user: " + myUser.toString());
        // Save arrays
        db.collection(USERS_COLLECTION).document(myUser.getEmail())
                .set(myUser)
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
        String containerPath = "data/" + ITEMS_DATA_CONTAINER;

        DocumentReference containerDocRef = db.document(containerPath);

        containerDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Log.d(TAG, "onSuccess: document exists! " + documentSnapshot.getData().toString());

                    FirestoreDataContainer container = documentSnapshot.toObject(FirestoreDataContainer.class);
                    if (container.getGiveItems() == null) {
                        Log.d(TAG, "onSuccess: null give items");
                    } else {
                        giveItems = container.getGiveItems();
                        Log.d(TAG, "onSuccess: Got give items: " + giveItems.toString());
                    }
                    if (container.getAskItems() == null) {
                        Log.d(TAG, "onSuccess: null ask items");
                    } else {
                        askItems = container.getAskItems();
                    }

                } else {
                    Log.d(TAG, "onSuccess: Document does not exist!");
                }
                initPageFragments();
                initBottomNavigationMenu(); // initialize bottom navigation menu
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Exception: " + e.getMessage());
            }
        });
    }


    // creation of the side menu
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: ");
        getMenuInflater().inflate(R.menu.top_app_bar2, menu);
        searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);
        optionsItem = menu.findItem(R.id.action_more);
        optionsItem.setVisible(true);
        main_SRC_search.setMenuItem(searchItem);


        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_logout) {
            Toast.makeText(this, getString(R.string.logout), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, Activity_login.class);
            intent.putExtra(LOGOUT, LOGOUT_CODE);
            startActivity(intent);
            finish();
            return true;
        }
        if (id == R.id.menu_rate) {
            SmartRate.Rate(MainActivity.this
                    , Color.parseColor("#40C4FF")
                    , -1
                    , new SmartRate.CallBack_UserRating() {
                        @Override
                        public void userRating(int rating) {
                            Toast.makeText(MainActivity.this, getString(R.string.rating) + rating + getString(R.string.stars), Toast.LENGTH_LONG).show();
                            //saveUserRating(rating);
                        }
                    }
            );
            return true;
        }
        if (id == R.id.menu_share) {
            Toast.makeText(this, getString(R.string.talk_to_your_around_tanks_you), Toast.LENGTH_LONG).show();
            return true;
        }
        if (id == R.id.menu_about) {
            AboutUsDialog aboutUsDialog = new AboutUsDialog(this);
            aboutUsDialog.show();
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            aboutUsDialog.getWindow().setLayout(width, RelativeLayout.LayoutParams.WRAP_CONTENT);
            aboutUsDialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            aboutUsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            aboutUsDialog.getWindow().setDimAmount(0.9f);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A method to define what happens when you search for something
     */
    private void searchAction() {
        Log.d(TAG, "searchAction: ");

        main_SRC_search.setVoiceSearch(true);

        main_SRC_search.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: ");
                main_TLB_head.setTitle(getString(R.string.results) + query);
                showingResults = true;
                if (askFragment.getVisibility() == View.VISIBLE) { // search in ask array
                    Log.d(TAG, "onQueryTextChange: Ask fragment is showing");
                    if (!askResults.isEmpty()) {
                        Log.d(TAG, "onQueryTextChange: There are suggestions!");

                        String[] suggestions = getResultNames(SEARCH_IN_ASK_ITEMS);
                        main_SRC_search.setSuggestions(suggestions);
                        main_SRC_search.showSuggestions();
                        setAskFragment(askResults);
                        enableToolbarBackOption();
                    }

                } else { // search in give array
                    Log.d(TAG, "onQueryTextChange: Give fragment is showing");
                    if (!giveResults.isEmpty()) {
                        Log.d(TAG, "onQueryTextChange: There are suggestions!");
                        String[] suggestions = getResultNames(SEARCH_IN_GIVE_ITEMS); // Get suggestion names to string array
                        main_SRC_search.setSuggestions(suggestions);
                        main_SRC_search.showSuggestions();
                        setGiveFragment(giveResults);
                        enableToolbarBackOption();
                    }

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: ");
                if (!newText.equals("")) {
                    if (askFragment.getVisibility() == View.VISIBLE) { // search in ask array
                        Log.d(TAG, "onQueryTextChange: Ask fragment is showing");
                        searchForItem(SEARCH_IN_ASK_ITEMS, newText);
                        if (!askResults.isEmpty()) {
                            Log.d(TAG, "onQueryTextChange: There are suggestions!");
                            String[] suggestions = getResultNames(SEARCH_IN_ASK_ITEMS);
                            main_SRC_search.setSuggestions(suggestions);
                            main_SRC_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Log.d(TAG, "onItemClick: " + askResults.get(position).getName());
                                    showingResults = true;
                                    main_TLB_head.setTitle(getString(R.string.results) + suggestions[position]);
                                    main_SRC_search.closeSearch();
                                }
                            });
                            main_SRC_search.showSuggestions();
                            setAskFragment(askResults);
                            enableToolbarBackOption();
                        }


                    } else { // search in give array
                        Log.d(TAG, "onQueryTextChange: Give fragment is showing");
                        searchForItem(SEARCH_IN_GIVE_ITEMS, newText); // Get suggestions as items array
                        if (!giveResults.isEmpty()) {
                            Log.d(TAG, "onQueryTextChange: There are suggestions!");

                            String[] suggestions = getResultNames(SEARCH_IN_GIVE_ITEMS); // Get suggestion names to string array
                            main_SRC_search.setSuggestions(suggestions);
                            main_SRC_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Log.d(TAG, "onItemClick: " + giveResults.get(position).getName());
                                    showingResults = true;
                                    main_TLB_head.setTitle(getString(R.string.results) + suggestions[position]);
                                    main_SRC_search.closeSearch();
                                }
                            });
                            main_SRC_search.showSuggestions();
                            setGiveFragment(giveResults);
                            enableToolbarBackOption();
                        }

                    }
                }
                return false;
            }
        });

        main_SRC_search.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                Log.d(TAG, "onSearchViewShown: ");
            }

            @Override
            public void onSearchViewClosed() {
                Log.d(TAG, "onSearchViewClosed: ");
                if (!showingResults) {
                    Log.d(TAG, "onSearchViewClosed: Not showing results");
                    showAllItems();
                } else {
                    Log.d(TAG, "onSearchViewClosed: Showing results so I'm not closing");
                    //TODO: Fix not found item problem here
                }
            }
        });
    }

    /**
     * A method to extract search results names and put them in an array to display suggestions
     */
    private String[] getResultNames(int arrayToSearch) {
        ArrayList<String> results = new ArrayList<>();
        if (arrayToSearch == SEARCH_IN_ASK_ITEMS) {
            Log.d(TAG, "getResultNames: extracting from ask items");
            for (AskItem item : askResults) {
                results.add(item.getName());
            }
        } else {
            Log.d(TAG, "getResultNames: extracting from give items");
            for (GiveItem item : giveResults) {
                results.add(item.getName());
            }
        }
        return results.toArray(new String[0]);
    }

    /**
     * A method to search for given query in given array
     */
    private void searchForItem(int searchLocation, String newText) {
        askResults.clear();
        giveResults.clear();
        if (searchLocation == SEARCH_IN_ASK_ITEMS) {
            Log.d(TAG, "searchForItem: Searching for: " + newText + " in ask items");
            for (AskItem item : askItems) {
                if (item.getName().toLowerCase().contains(newText.toLowerCase())) {
                    askResults.add(item);
                }
            }

        } else {
            Log.d(TAG, "searchForItem: Searching for: " + newText + " in give items");
            for (GiveItem item : giveItems) {
                if (item.getName().toLowerCase().contains(newText.toLowerCase())) {
                    giveResults.add(item);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult: request code: " + requestCode + " Result code: " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_PERMISSION_SETTINGS_REQUSETCODE:
                Log.d(TAG, "onActivityResult: I came from app settings: camera");
                break;
            case STORAGE_PERMISSION_SETTINGS_REQUSETCODE:
                Log.d(TAG, "onActivityResult: I came from app settings: storage");
                break;
            case CAMERA_PICTURE_REQUEST:
                Log.d(TAG, "onActivityResult: I came from camera");
                ShapeableImageView userPhoto = fragment_profile.getView().findViewById(R.id.profile_IMG_picture);
                if (data != null) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    userPhoto.setStrokeWidth(20);
                    userPhoto.setStrokeColor(getColorStateList(R.color.colorPrimaryDark));
                    userPhoto.setImageBitmap(bitmap);
                    uploadBitmapToStorage(bitmap);
                }
                break;
            case STORAGE_PICTURE_REQUEST:
                Log.d(TAG, "onActivityResult: I came from storage");
                if (data != null) {
                    Uri selectedImage = data.getData();
                    try {
                        ShapeableImageView profilePhoto = fragment_profile.getView().findViewById(R.id.profile_IMG_picture);

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        profilePhoto.setStrokeWidth(20);
                        profilePhoto.setStrokeColor(getColorStateList(R.color.colorPrimaryDark));
                        profilePhoto.setImageBitmap(bitmap);
                        uploadBitmapToStorage(bitmap);

                    } catch (IOException e) {
                        Log.i("TAG", "Some exception " + e);
                    }
                }
                break;
        }
        switch (resultCode) {

            /** We got from voice listener*/
            //TODO: Add voice listener
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
                    Toast.makeText(this, getString(R.string.item_added_successfully), Toast.LENGTH_SHORT).show();
                    Gson gson = new Gson();
                    Type itemType = new TypeToken<GiveItem>() {
                    }.getType();
                    GiveItem temp = gson.fromJson(gotNewItem, itemType);
                    temp.setDate(getCurrentDate());
                    if (giveItems.size() != 0) {
                        int lastId = Integer.valueOf(this.giveItems.get(this.giveItems.size() - 1).getId().substring(1));
                        temp.setId("G" + (lastId + 1));
                    } else {
                        temp.setId("G" + 0);
                    }
                    this.giveItems.add(temp);
                    myUser.addGiveItem(temp);
                    updateFragments();
                    updateUserInFirestore();
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
                    Toast.makeText(this, getString(R.string.item_added_successfully), Toast.LENGTH_SHORT).show();
                    Gson gson = new Gson();
                    Type itemType = new TypeToken<AskItem>() {
                    }.getType();
                    AskItem temp = gson.fromJson(gotNewItem, itemType);
                    temp.setDate(getCurrentDate());
                    if (askItems.size() != 0) {
                        int lastId = Integer.valueOf(this.askItems.get(this.askItems.size() - 1).getId().substring(1));
                        temp.setId("A" + (lastId + 1));
                    } else {
                        temp.setId("A" + 0);
                    }
                    askItems.add(temp);
                    Log.d(TAG, "onActivityResult: Ask items now: " + askItems.toString());
                    myUser.addAskItem(temp);
                    updateFragments();
                    updateUserInFirestore();
                    saveItemsToFirestore();
                } else {
                    Log.d(TAG, "onActivityResult: User canceled new item input");
                }
                break;
            case ITEM_DETAILS_RESULT_CODE:
                Log.d(TAG, "onActivityResult: Got from item details");
                if (data != null) {
                    String tempJson = data.getStringExtra(ITEM_TO_DEAL_WITH);
                    boolean isGiveItem = data.getBooleanExtra(IS_GIVE_ITEM, false);
                    if (data.getBooleanExtra(TO_DELETE, false)) {
                        Log.d(TAG, "onActivityResult: Deleting: " + tempJson);
                        deleteSelectedItem(tempJson, isGiveItem);
                    } else {
                        Log.d(TAG, "onActivityResult: Editing: " + tempJson);
                        updateSelectedItem(tempJson, isGiveItem);
                    }

                    updateUserInFirestore();
                    saveItemsToFirestore();
                    updateFragments();
                } else {
                    Log.d(TAG, "onActivityResult: User did not do anything special");
                }
                break;
        }
    }


    /**
     * A method to upload picture to Firebase Storage
     */
    private void uploadBitmapToStorage(Bitmap bitmap) {
        Log.d(TAG, "uploadBitmapToStorage: ");
        final String itemID = myUser.getEmail();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl("gs://" + getString(R.string.google_storage_bucket));

        // Create a reference to "mountains.jpg"
        StorageReference tempRef = storageRef.child(itemID + ".jpg");

        // Create a reference to 'images/mountains.jpg'
        StorageReference tempImagesRef = storageRef.child("images/" + itemID + ".jpg");

        // While the file names are the same, the references point to different files
        tempRef.getName().equals(tempImagesRef.getName());    // true
        tempRef.getPath().equals(tempImagesRef.getPath());    // false


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = tempRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "onFailure: Upload failed: " + exception.getMessage());
                Toast.makeText(MainActivity.this, getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: Image upload successful!");
            }
        });
    }

    /**
     * A method to update the received item
     */
    private void updateSelectedItem(String tempJson, boolean isGiveItem) {
        Log.d(TAG, "updateSelectedItem: Updating: " + tempJson);
        Gson gson = new Gson();
        if (isGiveItem) {
            GiveItem tempItem = gson.fromJson(tempJson, GiveItem.class);
            Log.d(TAG, "onActivityResult: Got give item: " + tempItem.toString());

            for (int i = 0; i < giveItems.size(); i++) {
                if (giveItems.get(i).getId().equals(tempItem.getId())) {
                    giveItems.set(i, tempItem);
                }
            }

            for (int i = 0; i < myUser.getGiveItems().size(); i++) {
                if (myUser.getGiveItems().get(i).getId().equals(tempItem.getId())) {
                    myUser.getGiveItems().set(i, tempItem);
                }
            }
            Toast.makeText(this, tempItem.getName() + getString(R.string.updated_successfully), Toast.LENGTH_SHORT).show();
        } else {
            AskItem tempItem = gson.fromJson(tempJson, AskItem.class);
            Log.d(TAG, "onActivityResult: Got ask item: " + tempItem.toString());

            for (int i = 0; i < askItems.size(); i++) {
                if (askItems.get(i).getId().equals(tempItem.getId())) {
                    askItems.set(i, tempItem);
                }
            }
            for (int i = 0; i < myUser.getAskItems().size(); i++) {
                if (myUser.getAskItems().get(i).getId().equals(tempItem.getId())) {
                    myUser.getAskItems().set(i, tempItem);
                }
            }
            Toast.makeText(this, tempItem.getName() + getString(R.string.updated_successfully), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * A method to delete selected item from existence
     */
    private void deleteSelectedItem(String tempJson, boolean isGiveItem) {
        Log.d(TAG, "deleteSelectedItem: Deleting: " + tempJson);
        Gson gson = new Gson();
        if (isGiveItem) {
            GiveItem tempItem = gson.fromJson(tempJson, GiveItem.class);
            Log.d(TAG, "onActivityResult: Got give item: " + tempItem.toString());

            for (int i = 0; i < giveItems.size(); i++) {
                if (giveItems.get(i).getId().equals(tempItem.getId())) {
                    giveItems.remove(i);
                }
            }

            for (int i = 0; i < myUser.getGiveItems().size(); i++) {
                if (myUser.getGiveItems().get(i).getId().equals(tempItem.getId())) {
                    myUser.getGiveItems().remove(i);
                }
            }
            Toast.makeText(this, tempItem.getName() + getString(R.string.removed_successfully), Toast.LENGTH_SHORT).show();
        } else {
            AskItem tempItem = gson.fromJson(tempJson, AskItem.class);
            Log.d(TAG, "onActivityResult: Got ask item: " + tempItem.toString());

            for (int i = 0; i < askItems.size(); i++) {
                if (askItems.get(i).getId().equals(tempItem.getId())) {
                    askItems.remove(i);
                }
            }
            for (int i = 0; i < myUser.getAskItems().size(); i++) {
                if (myUser.getAskItems().get(i).getId().equals(tempItem.getId())) {
                    myUser.getAskItems().remove(i);
                }
            }

            Toast.makeText(this, tempItem.getName() + getString(R.string.removed_successfully), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * A method to get the current time in a string format
     */
    private String getCurrentDate() {
        String returnDate = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDateTime now = LocalDateTime.now();
            returnDate = dtf.format(now);
        }
        return returnDate;
    }

    /**
     * A method to show all the items after exiting search
     */
    private void showAllItems() {
        Log.d(TAG, "showAllItems: Showing all items");
        showingResults = false;
        if (giveFragment.getVisibility() == View.VISIBLE) {
            setGiveFragment(giveItems);
            main_TLB_head.setTitle(getString(R.string.object_to_give));
            main_TLB_head.setNavigationIcon(null);
        } else {
            setAskFragment(askItems);
            main_TLB_head.setTitle(getString(R.string.object_to_ask));
            main_TLB_head.setNavigationIcon(null);
        }
    }

    @Override
    public void onBackPressed() {
        if (main_SRC_search.isSearchOpen()) {
            main_SRC_search.closeSearch();
        } else if (showingResults) {
            showAllItems();
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(fragment_profile.getId());
            if (!((IOnBackPressed) fragment).onBackPressed() && mainFragment.getVisibility() == View.GONE) {
                showFragment(mainFragment);
                return;
            }
            if (!((IOnBackPressed) fragment).onBackPressed()) {
                super.onBackPressed();
            }
        }


    }

    @Override
    public void getSelectedItem(GiveItem gotGiveItem, AskItem gotAskItem, boolean isGiveItem) {
        Intent intent = new Intent(this, ItemDetailsActivity.class);
        Gson gson = new Gson();

        if (isGiveItem) {
            Log.d(TAG, "getSelectedItem: " + gotGiveItem.toString());
            if (gotGiveItem.getGiver().getEmail().equals(myUser.getEmail())) {
                Log.d(TAG, "openItemDetails: Owner user");
                intent.putExtra(OWNER_USER, true);
            }
            String itemJson = gson.toJson(gotGiveItem);
            intent.putExtra(GIVE_ITEM, itemJson);
        } else {
            Log.d(TAG, "getSelectedItem: " + gotAskItem.toString());
            if (gotAskItem.getRequester().getEmail().equals(myUser.getEmail())) {
                Log.d(TAG, "openItemDetails: Owner user");
                intent.putExtra(OWNER_USER, true);
            }
            String itemJson = gson.toJson(gotAskItem);
            intent.putExtra(ASK_ITEM, itemJson);
        }
        startActivityForResult(intent, ITEM_DETAILS_RESULT_CODE);
    }

    @Override
    public void photoMode(Boolean fromCamera) {
        if (fromCamera) {
            Log.d(TAG, "photoMode: Taking picture from camera");
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onClick: User already given permission, moving straight to camera");
                openCamera();
            } else {
                checkingForCameraPermissions();
            }
        } else {
            Log.d(TAG, "photoMode: Fetching picture from storage");
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onClick: User already given permission, moving straight to storage");
                openStorage();
            } else {
                checkingForStoragePermissions();
            }
        }
    }

    /**
     * A method to open the storage to fetch a photo
     */
    private void openStorage() {
        Log.d(TAG, "openStorage: opening storage");
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, STORAGE_PICTURE_REQUEST);
    }

    /**
     * A method to check for camera permissions
     */
    private void checkingForCameraPermissions() {
        Log.d(TAG, "checkingForCameraPermissions: checking for users permissions");

        Dexter.withContext(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Log.d(TAG, "onPermissionGranted: User given permission");
                        openCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            Log.d(TAG, "onPermissionDenied: User denied permission permanently!");
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle(getString(R.string.permission_denied))
                                    .setMessage(getString(R.string.permission_denied_explication_camera))
                                    .setNegativeButton(getString(R.string.cancel), null)
                                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Log.d(TAG, "onClick: Opening settings activity");
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                                            startActivityForResult(intent, CAMERA_PERMISSION_SETTINGS_REQUSETCODE);
                                        }
                                    }).show();
                        } else {
                            Log.d(TAG, "onPermissionDenied: User denied permission!");
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    /**
     * A method to check for storage access permissions
     */
    private void checkingForStoragePermissions() {
        Log.d(TAG, "checkingForStoragePermissions: checking for users permissions");

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Log.d(TAG, "onPermissionGranted: User given permission");
                        openStorage();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            Log.d(TAG, "onPermissionDenied: User denied permission permanently!");
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle(getString(R.string.permission_denied))
                                    .setMessage(getString(R.string.permission_denied_explication_storage))
                                    .setNegativeButton(getString(R.string.cancel), null)
                                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Log.d(TAG, "onClick: Opening settings activity");
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                                            startActivityForResult(intent, STORAGE_PERMISSION_SETTINGS_REQUSETCODE);
                                        }
                                    }).show();
                        } else {
                            Log.d(TAG, "onPermissionDenied: User denied permission!");
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    /**
     * A method to open the camera
     */
    private void openCamera() {
        Log.d(TAG, "openCamera: opening camera");
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_PICTURE_REQUEST);
    }

    @Override
    public void getDetails(String updatedName, String updatedCity, String updatedPhone) {

        if (!updatedName.equals("")) {
            myUser.setName(updatedName);
        }
        if (!updatedPhone.equals("")) {
            myUser.setPhone(updatedPhone);
        }
        if (!updatedCity.equals("")) {
            myUser.setCity(updatedCity);
        }

        updateUserInFirestore();
        setProfileFragment();
    }

    public interface IOnBackPressed {
        /**
         * If you return true the back press will not be taken into account, otherwise the activity will act naturally
         *
         * @return true if your processing has priority if not false
         */
        boolean onBackPressed();
    }

}