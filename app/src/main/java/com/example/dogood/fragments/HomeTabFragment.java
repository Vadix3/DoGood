package com.example.dogood.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.dogood.MainActivity;
import com.example.dogood.R;
import com.example.dogood.interfaces.ShortcutSelectionListener;
import com.example.dogood.objects.AskItem;
import com.example.dogood.objects.GiveItem;
import com.example.dogood.objects.User;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

public class HomeTabFragment extends Fragment implements MainActivity.IOnBackPressed {
    private static final String TAG = "Dogood";
    protected View view;
    private Context context;
    private User user;
    private AdView adView;
    private ShapeableImageView mainPicture;
    private TextView helloLabel;

    private AskItem lastAskItem;
    private GiveItem lastGiveItem;

    private FrameLayout lastGiveFrame;
    private FrameLayout lastAskFrame;

    private MaterialButton askItemsShortcut;
    private MaterialButton giveItemsShortcut;
    private MaterialButton askItemsListShortcut;
    private MaterialButton giveItemsListShortcut;


    public HomeTabFragment() {
    }

    public HomeTabFragment(Context context, User user, AskItem lastAskItem, GiveItem lastGiveItem) {
        this.context = context;
        this.user = user;
        this.lastAskItem = lastAskItem;
        this.lastGiveItem = lastGiveItem;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView profile fragment");
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home, container, false);
        }
        findViews();
        initAd();
        initFragments();
        initShortcuts();
        return view;
    }

    /**
     * A method to initialize the shortcuts on the fragment
     */
    private void initShortcuts() {
        Log.d(TAG, "initShortcuts: ");
        askItemsShortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ShortcutSelectionListener) context).shortcutSelection(true, false, false, false);
            }
        });

        giveItemsShortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ShortcutSelectionListener) context).shortcutSelection(false, true, false, false);
            }
        });

        askItemsListShortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ShortcutSelectionListener) context).shortcutSelection(false, false, true, false);

            }
        });

        giveItemsListShortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ShortcutSelectionListener) context).shortcutSelection(false, false, false, true);
            }
        });


    }


    /**
     * A method to init the page fragments
     */
    private void initFragments() {
        Log.d(TAG, "initFragmets: ");
        LastAskFragment lastAskFragment = new LastAskFragment(context, lastAskItem);
        LastGiveFragment lastGiveFragment = new LastGiveFragment(context, lastGiveItem);
        FragmentTransaction transaction1 = getActivity().getSupportFragmentManager().beginTransaction();
        FragmentTransaction transaction2 = getActivity().getSupportFragmentManager().beginTransaction();
        transaction1.replace(R.id.home_LAY_lastGift, lastGiveFragment);
        transaction1.commit();
        transaction2.replace(R.id.home_LAY_lastAsk, lastAskFragment);
        transaction2.commit();
    }

    /**
     * A method to init bottom ad
     */
    @SuppressLint("MissingPermission")
    private void initAd() {
        Log.d(TAG, "initAd: ");
        adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded: Ad loaded");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                Log.d(TAG, "onAdFailedToLoad: Error: " + adError.toString());
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "onAdOpened: ");
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked: ");
            }

            @Override
            public void onAdLeftApplication() {
                Log.d(TAG, "onAdLeftApplication: ");
            }

            @Override
            public void onAdClosed() {
                Log.d(TAG, "onAdClosed: ");
            }
        });
    }

    /**
     * A method to initialize the home fragment
     */
    private void findViews() {
        Log.d(TAG, "findViews: ");

        mainPicture = view.findViewById(R.id.home_IMG_mainPicture);
        helloLabel = view.findViewById(R.id.home_LAY_helloLabel);
        lastGiveFrame = view.findViewById(R.id.home_LAY_lastGift);
        lastAskFrame = view.findViewById(R.id.home_LAY_lastAsk);

        askItemsShortcut = view.findViewById(R.id.home_BTN_askItemShortcut);
        giveItemsShortcut = view.findViewById(R.id.home_BTN_giveItemShortcut);
        askItemsListShortcut = view.findViewById(R.id.home_BTN_askItemListShortcut);
        giveItemsListShortcut = view.findViewById(R.id.home_BTN_giveItemListShortcut);


    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
