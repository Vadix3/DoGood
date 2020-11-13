package com.example.dogood.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.dogood.R;
import com.example.dogood.objects.AskItem;
import com.example.dogood.objects.GiveItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class EditItemDialog extends Dialog {

    private static final String TAG = "Dogood";
    private Context context;

    private GiveItem giveItem = null;
    private AskItem askItem = null;


    public EditItemDialog(@NonNull Context context, GiveItem giveItem, AskItem askItem) {
        super(context);
        this.context = context;
        this.giveItem = giveItem;
        this.askItem = askItem;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: edit item dialog");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_item);
    }

}
