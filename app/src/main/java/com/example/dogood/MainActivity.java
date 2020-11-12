package com.example.dogood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.dogood.fragments.AskItemFragment;
import com.example.dogood.fragments.Fragment_ask_give_profile;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Dogood";
    private static final String NEW_GIVE_ITEM = "111";
    private static final String NEW_ASK_ITEM = "112";
    private static final String GIVE_ITEMS_ARRAY = "giveItems";
    private static final String ITEMS_DATA_CONTAINER = "dataContainer";
    private static final String USERS_ARRAY = "usersArray";
    private static final String USERS_COLLECTION = "users/";
    private static final String LOGIN_USER_EXTRA = "loginUser";
    private static final String PENDING_USER_DETAILS = "usersArray";
    private static final String ITEM_COUNT = "itemCount";



    private static final int NEW_GIVE_ITEM_RESULT_CODE = 1011;
    private static final int NEW_ASK_ITEM_RESULT_CODE = 1012;
    private static final int UPDATE_PROFILE_RESULT_CODE = 1013;


    private static final int SEARCH_IN_GIVE_ITEMS = 11;
    private static final int SEARCH_IN_ASK_ITEMS = 12;

    private boolean showingResults = false; // A boolean var to indicate that search results are showing.

    private boolean isProfileFragmentShowing =false;
    private boolean isGiveItemFragmentShowing =false;
    private boolean isAskItemFragmentShowing =false;
    private boolean isHomeFragmentShowing =false;

    //private MaterialToolbar main_TLB_title;
    private Toolbar main_TLB_head;
    private MaterialSearchView main_SRC_search;
    private MenuItem searchItem;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        giveItems = new ArrayList<>();
        askItems = new ArrayList<>();
        initUser();
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
                initPageFragments();
                initBottomNavigationMenu(); // initialize bottom navigation menu
                //TODO: Problem when I'm pressing tabs before data loads, maybe stall bottom menu somehow?
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
        setGiveFragment(giveItems);
        setHomeFragment();
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
                            main_TLB_head.setTitle(getResources().getString(R.string.home));
                            showFragment(mainFragment);

                            isProfileFragmentShowing =false;
                            isGiveItemFragmentShowing =false;
                            isAskItemFragmentShowing =false;
                            isHomeFragmentShowing =true;
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

                                isProfileFragmentShowing =false;
                                isGiveItemFragmentShowing =true;
                                isAskItemFragmentShowing =false;
                                isHomeFragmentShowing =false;
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

                                isProfileFragmentShowing =false;
                                isGiveItemFragmentShowing =false;
                                isAskItemFragmentShowing =true;
                                isHomeFragmentShowing =false;
                            }
                        }
                        break;
                    case R.id.bottom_menu_profile:
                        searchItem.setVisible(false);
                        if (profileFragment != null) {
                            main_TLB_head.setTitle(getResources().getString(R.string.profile));
                            showFragment(profileFragment);

                            isProfileFragmentShowing =true;
                            isGiveItemFragmentShowing =false;
                            isAskItemFragmentShowing =false;
                            isHomeFragmentShowing =false;
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
        fragment_profile = new Fragment_profile(myUser,giveItems.size(),askItems.size());
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

        FirestoreDataContainer dataContainer = new FirestoreDataContainer(giveItems, askItems);

        // Save arrays
        db.collection("data").document(ITEMS_DATA_CONTAINER)
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
                //TODO: Problem when I'm pressing tabs before data loads, maybe stall bottom menu somehow?
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
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);
        main_SRC_search.setMenuItem(searchItem);

        return true;

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
                main_TLB_head.setTitle("Results: " + query);
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
                                    main_TLB_head.setTitle("Results: " + suggestions[position]);
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
                                    main_TLB_head.setTitle("Results: " + suggestions[position]);
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
            Log.d(TAG, "getResultNames: extracting from ask items");
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
                    Toast.makeText(this, "Item added successfully!", Toast.LENGTH_SHORT).show();
                    Gson gson = new Gson();
                    Type itemType = new TypeToken<GiveItem>() {
                    }.getType();
                    GiveItem temp = gson.fromJson(gotNewItem, itemType);
                    temp.setDate(getCurrentDate());
                    temp.setId("G" + giveItems.size());
                    giveItems.add(temp);
                    myUser.addGiveItem(temp);
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
                    Toast.makeText(this, "Item added successfully!", Toast.LENGTH_SHORT).show();
                    Gson gson = new Gson();
                    Type itemType = new TypeToken<AskItem>() {
                    }.getType();
                    AskItem temp = gson.fromJson(gotNewItem, itemType);
                    temp.setDate(getCurrentDate());
                    temp.setId("G" + askItems.size());
                    Log.d(TAG, "onActivityResult: Got item as item: " + temp.toString());
                    askItems.add(temp);
                    myUser.addAskItem(temp);
                    updateUserInFirestore();
                    saveItemsToFirestore();
                } else {
                    Log.d(TAG, "onActivityResult: User canceled new item input");
                }
                //TODO: Add switch for profile page
                break;
            case UPDATE_PROFILE_RESULT_CODE:
                Log.d(TAG, "onActivityResult: UPDATE_PROFILE_RESULT_CODE");
                if (data != null) {
                    String name = (String) data.getExtras().get("name");
                    String phone = (String) data.getExtras().get("phone");
                    String city = (String) data.getExtras().get("city");
                    String photo = (String) data.getExtras().get("photo");

                    myUser.setName(name);
                    myUser.setPhone(phone);
                    myUser.setCity(city);
                    if (photo != null) {
                        myUser.setPhoto(photo);
                    }
                    //end
                    updateUserInFirestore();
                    setProfileFragment();
                } else {
                    Log.d(TAG, "onActivityResult: User canceled update profile");
                }
        }
    }

    /** A method to get the current time in a string format*/
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
            main_TLB_head.setTitle("Given Items");
            main_TLB_head.setNavigationIcon(null);
        } else {
            setAskFragment(askItems);
            main_TLB_head.setTitle("Needed Items");
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
            if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed) fragment).onBackPressed()) {
                super.onBackPressed();
            }
        }



    }

    public interface IOnBackPressed {
        /**
         * If you return true the back press will not be taken into account, otherwise the activity will act naturally
         * @return true if your processing has priority if not false
         */
        boolean onBackPressed();
    }

}