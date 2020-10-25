package com.example.dogood.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dogood.R;

public class NewGiveItemActivity extends AppCompatActivity {
    private static final String TAG = "Dogood";

    private ImageView itemPhoto;
    private EditText itemName;
    private EditText itemPrice;
    private CheckBox freeItem;
    private Spinner condition;
    private Spinner category;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_give_item);

        initViews();

    }

    /**
     * A method to init the views
     */
    private void initViews() {
        Log.d(TAG, "initViews: Creating views");

        itemPhoto = findViewById(R.id.addgiveitem_IMG_itemPhoto);
        itemName = findViewById(R.id.addgiveitem_EDT_itemName);
        itemPrice = findViewById(R.id.addgiveitem_EDT_itemPrice);
        freeItem = findViewById(R.id.addgiveitem_CHK_priceCheckbox);
        condition = findViewById(R.id.addgiveitem_LST_conditionSpinner);
        category = findViewById(R.id.addgiveitem_LST_categorySpinner);
    }
}
