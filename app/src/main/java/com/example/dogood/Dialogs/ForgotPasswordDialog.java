package com.example.dogood.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.dogood.R;
import com.example.dogood.activities.Activity_login;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordDialog  extends Dialog {

    private static final String TAG = "pttt";
    private Context context;
    private FirebaseAuth mAuth;

    private TextInputLayout email;
    private MaterialButton send;


    public ForgotPasswordDialog(@NonNull Context context, FirebaseAuth mAuth) {
        super(context);
        this.context = context;
        this.mAuth = mAuth;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_forgotpassword);
        email = findViewById(R.id.forgotpassword_EDT_email);
        send = findViewById(R.id.forgotpassword_BTN_sendEmail);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUserInput();
            }
        });
    }

    /**
     * A method to check user email
     */
    private void checkUserInput() {
        Log.d(TAG, "checkUserInput: Checking user email");
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getEditText().getText().toString()).matches()) {
            Log.d(TAG, "checkForValidInputs: Email invalid");
            email.setError(context.getString(R.string.please_enter_a_valid_email_address));
            return;
        }

        mAuth.sendPasswordResetEmail(email.getEditText().getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Sent email to: " + email.getEditText().getText().toString());
                Toast.makeText(context, context.getString(R.string.sent_rest_email), Toast.LENGTH_SHORT).show();
                dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Exception: " + e.getMessage());
                if(e.getMessage().equals("The email address is badly formatted.")){
                    email.setError(context.getString(R.string.please_enter_a_valid_email_address));
                }
                if(e.getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")){
                    email.setError(context.getString(R.string.email_address_does_not_exist));
                }
            }
        });

    }
}