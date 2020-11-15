package com.example.dogood.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.dogood.R;

public class AboutUsDialog extends Dialog {

    private Context mContext;
    public AboutUsDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_about_us);
    }
}
