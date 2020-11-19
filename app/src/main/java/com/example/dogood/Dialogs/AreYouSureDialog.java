package com.example.dogood.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.example.dogood.R;
import com.example.dogood.interfaces.ItemDeleteListener;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class AreYouSureDialog extends Dialog {
    private static final String TAG = "Dogood";
    private Context context;
    private String itemName;
    private TextView message;
    private MaterialButton yes;
    private MaterialButton no;


    public AreYouSureDialog(@NonNull Context context, String itemName) {
        super(context);
        this.context = context;
        this.itemName = itemName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_are_you_sure);
        message = findViewById(R.id.areyousure_LBL_title);
        String toPrint = context.getString(R.string.are_you_sure) + " " + itemName + "?";
        message.setText(toPrint);

        yes = findViewById(R.id.areyousure_BTN_yes);
        no = findViewById(R.id.areyousure_BTN_no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: User decided to delete the entry");
                ItemDeleteListener itemDeleteListener = (ItemDeleteListener) context;
                itemDeleteListener.deleteSelectedItem();
                dismiss();

            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: User decided not to delete the entry");
                dismiss();
            }
        });
    }

}
