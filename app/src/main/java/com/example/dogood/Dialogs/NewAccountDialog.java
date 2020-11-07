package com.example.dogood.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.dogood.R;
import com.example.dogood.objects.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class NewAccountDialog extends Dialog {
    private static final String TAG = "Dogood";
    //views
    private ConstraintLayout newAccount_LAY_mainLayout;
    private TextInputLayout newAccount_EDT_name;
    private TextInputLayout newAccount_EDT_email;
    private TextInputLayout newAccount_EDT_phone;
    private TextInputLayout newAccount_EDT_city;
    private TextInputLayout newAccount_EDT_password;
    private TextInputLayout newAccount_EDT_confirmPassword;
    private MaterialButton newAccount_EDT_submit;

    private NewAccountDialogListener listener;

    public NewAccountDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: New account dialog");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_new_account);
        findViews();
    }


    private void findViews() {
        Log.d(TAG, "findViews new account dialog: ");
        newAccount_LAY_mainLayout = findViewById(R.id.newAccount_LAY_mainLayout);
        glideToBackground(newAccount_LAY_mainLayout, R.drawable.dialog_background);
        newAccount_EDT_name = findViewById(R.id.newAccount_EDT_name);
        newAccount_EDT_email = findViewById(R.id.newAccount_EDT_email);
        newAccount_EDT_phone = findViewById(R.id.newAccount_EDT_phone);
        newAccount_EDT_city = findViewById(R.id.newAccount_EDT_city);
        newAccount_EDT_password = findViewById(R.id.newAccount_EDT_password);
        newAccount_EDT_confirmPassword = findViewById(R.id.newAccount_EDT_confirmPassword);
        newAccount_EDT_submit = findViewById(R.id.newAccount_EDT_submit);
        newAccount_EDT_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidInfo();
            }
        });
        setViewListeners();
    }

    /**
     * A method to set the new account page listeners
     */
    private void setViewListeners() {
        Log.d(TAG, "setViewListeners: setting listeners");
        newAccount_EDT_name.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                newAccount_EDT_name.setErrorEnabled(false); // disable error
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        newAccount_EDT_email.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                newAccount_EDT_email.setErrorEnabled(false); // disable error
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        newAccount_EDT_city.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                newAccount_EDT_city.setErrorEnabled(false); // disable error
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        newAccount_EDT_phone.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                newAccount_EDT_phone.setErrorEnabled(false); // disable error
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        newAccount_EDT_password.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                newAccount_EDT_password.setErrorEnabled(false); // disable error
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        newAccount_EDT_confirmPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                newAccount_EDT_confirmPassword.setErrorEnabled(false); // disable error
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * A method to check the valid information
     */
    private void checkValidInfo() {
        Log.d(TAG, "checkValidInfo: Checking valid input");
        if (newAccount_EDT_name.getEditText().getText().toString().equals("")) {
            Log.d(TAG, "checkForValidInputs: first name invalid");
            newAccount_EDT_name.setError(getContext().getString(R.string.enter_name_error));
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newAccount_EDT_email.getEditText().getText().toString()).matches()) {
            Log.d(TAG, "checkForValidInputs: Email invalid");
            newAccount_EDT_email.setError(getContext().getString(R.string.enter_email_error));
            return;
        }
        if (newAccount_EDT_city.getEditText().getText().toString().equals("")) {
            Log.d(TAG, "checkForValidInputs: invalid city");
            //TODO: Search for real cities
            newAccount_EDT_city.setError(getContext().getString(R.string.enter_city_error));
            return;
        }
        if (newAccount_EDT_phone.getEditText().getText().toString().equals("")) {
            Log.d(TAG, "checkForValidInputs: invalid phone");
            //TODO: Add prefix for number & check if number valid
            newAccount_EDT_phone.setError(getContext().getString(R.string.enter_phone_error));
            return;
        }
        if (newAccount_EDT_password.getEditText().getText().toString().equals("")
                || newAccount_EDT_password.getEditText().getText().toString().length() < 6) {
            if (newAccount_EDT_password.getEditText().getText().toString().length() < 6) {
                Log.d(TAG, "checkForValidInputs: short password");
                newAccount_EDT_password.setError(getContext().getString(R.string.six_chars_password_error));
                return;
            } else {
                Log.d(TAG, "checkForValidInputs: invalid password");
                newAccount_EDT_password.setError(getContext().getString(R.string.null_password));
                return;
            }
        }
        if (newAccount_EDT_confirmPassword.getEditText().getText().toString().equals("")) {
            Log.d(TAG, "checkForValidInputs: confirm invalid");
            newAccount_EDT_confirmPassword.setError(getContext().getString(R.string.confirm_password));
            return;
        }
        if (!newAccount_EDT_password.getEditText().getText().toString()
                .equals(newAccount_EDT_confirmPassword.getEditText().getText().toString())) {
            Log.d(TAG, "checkForValidInputs: Passwords doesnt match");
            newAccount_EDT_confirmPassword.getEditText().setText("");
            newAccount_EDT_confirmPassword.setError(getContext().getString(R.string.password_no_match));
            return;
        }
        sendInfoToMainActivity();


    }

    /**
     * A method to send the confirmed input info to the main activity
     */
    private void sendInfoToMainActivity() {
        Log.d(TAG, "sendInfoToMainActivity: sending info back");

        //Extracting information after check to a user object

        String name = newAccount_EDT_name.getEditText().getText().toString();
        String email = newAccount_EDT_email.getEditText().getText().toString();
        String city = newAccount_EDT_city.getEditText().getText().toString();
        String phone = newAccount_EDT_phone.getEditText().getText().toString();
        String password = newAccount_EDT_password.getEditText().getText().toString();

        //Creating a new user with the extracted information
        User temp = new User(name, email, password, city, phone);
        listener.getInfoUser(temp);
    }

    /**
     * A method to insert image to background with glide
     */
    private void glideToBackground(final View target, int pictureID) {
        Glide.with(target).load(pictureID).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                target.setBackground(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });
    }

}
