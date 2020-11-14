package com.example.dogood.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.dogood.R;
import com.example.dogood.objects.User;

public class ContactUserDialog extends Dialog {
    private static final String TAG = "Dogood";
    private Context context;
    private String itemName;
    private String itemID;
    private User user;

    private TextView itemNameLbl, userNameLbl, userEmailLbl, userCityLbl;


    public ContactUserDialog(@NonNull Context context, String itemName, String itemID, User user) {
        super(context);
        this.context = context;
        this.itemName = itemName;
        this.user = user;
        this.itemID = itemID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_contact_user);

        itemNameLbl = findViewById(R.id.contactDetails_LBL_itemName);
        itemNameLbl.setText(itemName+ "\nID: "+itemID);
        userNameLbl = findViewById(R.id.contactDetails_LBL_contactName);
        userEmailLbl = findViewById(R.id.contactDetails_LBL_contactEmail);
        userCityLbl = findViewById(R.id.contactDetails_LBL_contactCity);

        if (user != null) {
            userNameLbl.setText(user.getName());
            userEmailLbl.setText(user.getEmail());
            userCityLbl.setText(user.getCity());
        } else {
            userNameLbl.setText(context.getString(R.string.contact_us));
            userEmailLbl.setText(context.getString(R.string.our_email));
            userCityLbl.setVisibility(View.INVISIBLE);
        }

    }
}
