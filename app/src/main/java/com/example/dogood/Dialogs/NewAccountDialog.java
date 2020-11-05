package com.example.dogood.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.dogood.R;
import com.google.android.material.textfield.TextInputLayout;

public class NewAccountDialog extends AppCompatDialogFragment {
    private static final String TAG = "NewAccountDialog";
    //views
    private TextInputLayout dialog_EDT_name;
    private TextInputLayout dialog_EDT_email;
    private TextInputLayout dialog_EDT_phone;
    private TextInputLayout dialog_EDT_country;
    private TextInputLayout dialog_EDT_password;

    private NewAccountDialogListener listener;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog: ");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.activity_new_account_dialog, null);

        findViews(view);

        builder.setView(view)
                .setTitle("Login")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Subsribe", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = "";
                        String email = "";
                        String phone = "";
                        String country = "";
                        String password = "";

                        name = dialog_EDT_name.getEditText().getText().toString();
                        email = dialog_EDT_email.getEditText().getText().toString();
                        password = dialog_EDT_password.getEditText().getText().toString();
                        phone = dialog_EDT_phone.getEditText().getText().toString();
                        country = dialog_EDT_country.getEditText().getText().toString();




                        listener.getInfoUser(name,email,phone,country,password);

                    }
                });


        return builder.create();
    }

    private void findViews(View view) {
        dialog_EDT_name = view.findViewById(R.id.dialog_EDT_name);
        dialog_EDT_email = view.findViewById(R.id.dialog_EDT_email);
        dialog_EDT_phone = view.findViewById(R.id.dialog_EDT_phone);
        dialog_EDT_country = view.findViewById(R.id.dialog_EDT_country);
        dialog_EDT_password = view.findViewById(R.id.dialog_EDT_password);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (NewAccountDialogListener) context;
        } catch (ClassCastException e) {
            Log.d(TAG, "onAttach: " + e.getMessage());
            throw new ClassCastException(context.toString() + "must implement ActivityDialogListener");
        }
    }



}
