package com.example.dogood.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.dogood.Dialogs.PhotoModeDialog;
import com.example.dogood.R;
import com.example.dogood.interfaces.EditProfileListener;
import com.example.dogood.objects.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class UpdateAccountDialog extends Dialog {
    private static final String TAG = "UpdateAccountDialog";
    private static final String UPDATE_PROFILE = "user";
    private static final int UPDATE_PROFILE_RESULT_CODE = 1013;
    private Context context;
    private User user;

    private TextInputLayout updateAccount_EDT_name;
    private TextInputLayout updateAccount_EDT_city;
    private TextInputLayout updateAccount_EDT_phone;
    private MaterialButton updateAccount_BTN_submit;

    public UpdateAccountDialog(Context context, User user) {
        super(context);
        this.context = context;
        this.user = user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_update_account);
        findViews();
        initEDTtexts();
    }

    /**
     * A method to put the current data into the edit texts
     */
    private void initEDTtexts() {
        Log.d(TAG, "initEDTtexts: ");
        updateAccount_EDT_name.getEditText().setText(user.getName());
        updateAccount_EDT_city.getEditText().setText(user.getCity());
        updateAccount_EDT_phone.getEditText().setText(user.getPhone());
    }

    /**
     * A method to initialize the activity views
     */
    private void findViews() {
        Log.d(TAG, "findViews: ");
        updateAccount_EDT_name = findViewById(R.id.updateAccount_EDT_name);
        updateAccount_EDT_city = findViewById(R.id.updateAccount_EDT_city);
        updateAccount_EDT_phone = findViewById(R.id.updateAccount_EDT_phone);
        updateAccount_BTN_submit = findViewById(R.id.updateAccount_BTN_submit);
        updateAccount_BTN_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndSendData();
            }
        });
    }

    private void checkAndSendData() {
        if (updateAccount_EDT_city.toString().equals("")) {
            Log.d(TAG, "checkAndSendData: Empty item description");
            updateAccount_EDT_city.setError("Please enter City");
            return;
        }
        if (updateAccount_EDT_phone.toString().equals("")) {
            Log.d(TAG, "checkAndSendData: Empty item description");
            updateAccount_EDT_phone.setError("Please enter phone");
            return;
        }
        if (updateAccount_EDT_name.toString().equals("")) {
            Log.d(TAG, "checkAndSendData: Empty item description");
            updateAccount_EDT_name.setError("Please enter Name");
            return;
        }

        returnUpdate(updateAccount_EDT_city.getEditText().getText().toString()
                , updateAccount_EDT_phone.getEditText().getText().toString()
                , updateAccount_EDT_name.getEditText().getText().toString());
    }

    private void returnUpdate(String city, String phone, String name) {
        Log.d(TAG, "returnGivenItem: Returning item: ");
        ((EditProfileListener) context).getDetails(name, city, phone);
        dismiss();
    }

}

