package com.example.dogood.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

    private TextView itemNameLbl, userNameLbl, userEmailLbl, userCityLbl, userPhoneLbl;


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
        itemNameLbl.setText(itemName + "\nID: " + itemID);
        userNameLbl = findViewById(R.id.contactDetails_LBL_contactName);
        userEmailLbl = findViewById(R.id.contactDetails_LBL_contactEmail);
        userCityLbl = findViewById(R.id.contactDetails_LBL_contactCity);
        userPhoneLbl = findViewById(R.id.contactDetails_LBL_contactPhone);

        if (user != null) {
            userNameLbl.setText(context.getString(R.string.name2)+" "+user.getName());
            userEmailLbl.setText(context.getString(R.string.email2)+" "+user.getEmail());
            userCityLbl.setText(context.getString(R.string.city2)+" "+user.getCity());
            userPhoneLbl.setText(context.getString(R.string.phone2)+" "+user.getPhone());
        } else {
            userNameLbl.setVisibility(View.GONE);
            userEmailLbl.setText(context.getString(R.string.contact_us) + "\n" + context.getString(R.string.our_email));
            userCityLbl.setVisibility(View.GONE);
            userPhoneLbl.setVisibility(View.GONE);
        }
        userPhoneLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("tel:"+user.getPhone());
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                context.startActivity(callIntent);

            }
        });
        userEmailLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] mail ;
                if(userPhoneLbl.getVisibility() == View.GONE){
                    mail = new String[]{context.getString(R.string.our_email)};
                }else {
                    mail = new String[]{user.getEmail()};
                }
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                /* Fill it with Data */
                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, mail);
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, itemName);
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, context.getString(R.string.hey_to_mail)+ " "+itemName);

                /* Send it off to the Activity-Chooser */
                context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            }
        });

    }
}
