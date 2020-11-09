package com.example.dogood.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dogood.Dialogs.NewAccountDialog;
import com.example.dogood.Dialogs.UpdateAccountDialog;
import com.example.dogood.R;
import com.example.dogood.objects.AskItem;
import com.example.dogood.objects.GiveItem;
import com.example.dogood.objects.User;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;


public class Fragment_profile extends Fragment {
    private static final String TAG = "Fragment_profile";

    protected View view;

    private ImageView profile_IMG_picture;
    private TextView profile_LBL_name;
    private TextView profile_LBL_city;
    private TextView profile_LBL_phone;
    private TextView profile_LBL_mail;
    private FrameLayout profile_LAY_post;
    private MaterialButton profile_BTN_update;

    private ArrayList<GiveItem> giveItems;
    private ArrayList<AskItem> askItems;
    private User mUser;

    public Fragment_profile(){}

    public Fragment_profile(ArrayList<GiveItem> giveItems, ArrayList<AskItem> askItems, User user) {
        this.giveItems = giveItems;
        this.askItems = askItems;
        this.mUser = user;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_profile, container, false);
        }
        //todo:check if user have complite profile
        //todo:search the object of the user , not all the object
        findViews();
        updateUser(view.getContext());
        addTolistTest();
        //populateEventList();
        return view;
    }

    private void updateUser(Context context) {
        Log.d(TAG, "updateUser: ");

        profile_LBL_name.setText(mUser.getName());
        profile_LBL_mail.setText(mUser.getEmail());

        profile_BTN_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(context);
            }
        });


    }

    private void addTolistTest() {
        Log.d(TAG, "addTolistTest: ");
        String userCustomImage = "null";
        ArrayList<GiveItem> mgiveItems = new ArrayList<>();
        ArrayList<AskItem>mrequestItems = new ArrayList<>();


        GiveItem gv1 = new GiveItem("1000","tv","electronic","new","","no need",userCustomImage,"27/10",mUser);
        GiveItem gv2 = new GiveItem("1000","tv","electronic","new","","no need",userCustomImage,"27/10",mUser);
        mgiveItems.add(gv1);
        mgiveItems.add(gv2);
        AskItem ask1 = new AskItem();
        AskItem ask2 = new AskItem();
        ask1.setId("2000");
        ask1.setName("phone");
        ask2.setId("2000");
        ask2.setName("phone");
        mrequestItems.add(ask1);
        mrequestItems.add(ask2);

        addGiveItemsFragment(mgiveItems,mrequestItems);
    }

    private void findViews() {
        profile_IMG_picture = view.findViewById(R.id.profile_IMG_picture);
        profile_LBL_name = view.findViewById(R.id.profile_LBL_name);
        profile_LBL_city = view.findViewById(R.id.profile_LBL_city);
        profile_LBL_phone = view.findViewById(R.id.profile_LBL_phone);
        profile_LBL_mail = view.findViewById(R.id.profile_LBL_mail);
        profile_LAY_post = view.findViewById(R.id.profile_LAY_post);
        profile_BTN_update = view.findViewById(R.id.profile_BTN_update);

    }

    private void addGiveItemsFragment(ArrayList<GiveItem> mgiveItems, ArrayList<AskItem> mrequestItems) {
        Log.d(TAG, "initItemsFragment: Initing main list with: " + giveItems.toString());
        GiveItemFragment giveItemFragment = new GiveItemFragment(mgiveItems,mUser);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.profile_LAY_post, giveItemFragment);
        transaction.commit();
    }

    private void openDialog(Context context) {
        UpdateAccountDialog updateAccountDialog = new UpdateAccountDialog(context,mUser);
        updateAccountDialog.show();
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        updateAccountDialog.getWindow().setLayout(width, height);
        updateAccountDialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        updateAccountDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        updateAccountDialog.getWindow().setDimAmount(1f);
    }


}