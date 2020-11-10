package com.example.dogood.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dogood.R;
import com.example.dogood.objects.AskItem;
import com.example.dogood.objects.User;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.ArrayList;

public class NewAskItemActivity extends AppCompatActivity {
    private static final String TAG = "Dogood";
    private static final String NEW_GIVE_ITEM = "111";
    private static final String NEW_ASK_ITEM = "112";

    private static final int CAMERA_PERMISSION_REQUSETCODE = 122;
    private static final int CAMERA_PERMISSION_SETTINGS_REQUSETCODE = 123;
    private static final int CAMERA_PICTURE_REQUEST = 124;
    private static final int NEW_GIVE_ITEM_RESULT_CODE = 1011;
    private static final int NEW_ASK_ITEM_RESULT_CODE = 1012;

    private static final String ITEM_COUNT = "itemCount";



    private EditText itemName;
    private EditText itemDescription;
    private Spinner itemCategory;
    private CheckBox isDiscrete;
    private MaterialButton submitBtn;

    private int itemCount=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ask_item);
        itemCount=getIntent().getIntExtra(ITEM_COUNT,0);
        //TODO: Need user here?

        initViews();
        initCategorySpinner();

    }

    /**
     * A method to initialize the item category spinner
     */
    private void initCategorySpinner() {
        Log.d(TAG, "initCategotySpinner: Initing category spinner");
        ArrayList<String> categories = new ArrayList<>();
        categories.add(getString(R.string.select_categories));
        categories.add(getString(R.string.clothes));
        categories.add(getString(R.string.office_supplies));
        categories.add(getString(R.string.medical_equipment));
        categories.add(getString(R.string.gaming));
        categories.add(getString(R.string.electronics));
        categories.add(getString(R.string.appliances));
        categories.add(getString(R.string.gift_cards));
        categories.add(getString(R.string.lighting));
        categories.add(getString(R.string.games_and_toys));
        categories.add(getString(R.string.cellular));
        categories.add(getString(R.string.books));
        categories.add(getString(R.string.baby_supplies));
        categories.add(getString(R.string.computers));
        categories.add(getString(R.string.other));


        //create an ArrayAdapter from the String Array
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, categories);
        //set the view for the Drop down list
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set the ArrayAdapter to the spinner
        itemCategory.setAdapter(dataAdapter);
        //attach the listener to the spinner
        itemCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemSelected: Selected: " + categories.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * A method to initialize the activity views
     */
    private void initViews() {
        Log.d(TAG, "initViews: initing views");
        itemName = findViewById(R.id.addAskItem_EDT_itemName);
        itemDescription = findViewById(R.id.addAskItem_EDT_itemDescription);
        itemCategory = findViewById(R.id.addAskItem_LST_categorySpinner);
        isDiscrete = findViewById(R.id.addAskItem_CHK_isDescrete);
        submitBtn = findViewById(R.id.addAskItem_BTN_submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndSendData();
            }
        });
    }

    /**
     * A method to check and send the data to main activity
     */
    private void checkAndSendData() {
        Log.d(TAG, "checkAndSendData: Checking form data");

        boolean tempDiscrete = false;

        if (itemName.getText().toString().equals("")) {
            Log.d(TAG, "checkForValidInput: Empty item name");
            itemName.setError(getString(R.string.please_enter_item_name));
            return;
        }
        if (itemDescription.getText().toString().equals("")) {
            Log.d(TAG, "checkForValidInput: Empty item description");
            itemDescription.setError(getString(R.string.please_enter_item_decription));
            return;
        }
        if (itemCategory.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select_categories))) {
            Log.d(TAG, "checkForValidInput: Category not selected");
            ((TextView) itemCategory.getSelectedView()).setError("Please select a category");
            return;
        }
        if (isDiscrete.isChecked()) {
            Log.d(TAG, "checkAndSendData: discrete data is checked");
            tempDiscrete = true;
        }

        User testUser = new User("Vadim", "dogoodapp1@gmail.com", "123456"
                , "Netanya", "0541234567");

        AskItem temp = new AskItem("123", itemName.getText().toString(), itemCategory.getSelectedItem().toString()
                , testUser.getCity(), itemDescription.getText().toString(), "20-12-20", testUser, tempDiscrete);

        Intent resultIntent = new Intent();
        Gson gson = new Gson();
        String jsonItems = gson.toJson(temp);
        resultIntent.putExtra(NEW_ASK_ITEM, jsonItems);
        setResult(NEW_ASK_ITEM_RESULT_CODE, resultIntent);
        finish();
    }
}
