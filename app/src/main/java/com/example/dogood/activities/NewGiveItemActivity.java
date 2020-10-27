package com.example.dogood.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dogood.R;
import com.example.dogood.objects.GiveItem;
import com.example.dogood.objects.User;
import com.google.gson.Gson;

import java.util.ArrayList;

public class NewGiveItemActivity extends AppCompatActivity {
    private static final String TAG = "Dogood";
    private static final String NEW_GIVE_ITEM="111";

    private ImageView itemPhoto;
    private EditText itemName;
    private EditText itemPrice;
    private EditText itemDescription;
    private CheckBox freeItem;
    private Spinner condition;
    private Spinner category;
    private Button submitBtn;

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
        itemDescription = findViewById(R.id.addgiveitem_EDT_itemDescription);
        itemPrice = findViewById(R.id.addgiveitem_EDT_itemPrice);
        freeItem = findViewById(R.id.addgiveitem_CHK_priceCheckbox);
        freeItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    itemPrice.setEnabled(true);
                    itemPrice.setEnabled(true);
                    itemPrice.setText("");
                } else {
                    itemPrice.setError(null);
                    itemPrice.setEnabled(false);
                    itemPrice.setText(getString(R.string.free_item));
                }
            }
        });
        condition = findViewById(R.id.addgiveitem_LST_conditionSpinner);
        category = findViewById(R.id.addgiveitem_LST_categorySpinner);
        submitBtn = findViewById(R.id.addgiveitem_BTN_submitBtn);
        initCategotySpinner();
        initConditionSpinner();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForValidInput();
            }
        });
    }

    /**
     * A method to check for valid user input
     */
    private void checkForValidInput() {
        Log.d(TAG, "checkForValidInput: Checking user input");
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
        if (condition.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select_condition))) {
            Log.d(TAG, "checkForValidInput: Condition not selected");
            ((TextView) condition.getSelectedView()).setError("Please select a condition");
            return;
        }
        if (category.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select_categories))) {
            Log.d(TAG, "checkForValidInput: Category not selected");
            ((TextView) category.getSelectedView()).setError("Please select a category");
            return;
        }
        if (itemPrice.getText().toString().equals("") && !freeItem.isChecked()) {
            Log.d(TAG, "checkForValidInput: Item price is not selected but not free");
            itemPrice.setError("Please enter price (Be reasonable!)");
            return;
        }

        Log.d(TAG, "checkForValidInput: Passed all checks!");
        User testUser = new User("Vadim", "dogoodapp1@gmail.com", "123456"
                , "Netanya", "0541234567", "Photo URL");

//    public GiveItem(String id, String name, String category, String state, String price
//                , String description, String pictures, String date, User giver) {

        GiveItem temp = new GiveItem("12", itemName.getText().toString(), category.getSelectedItem().toString()
                , condition.getSelectedItem().toString(), itemPrice.getText().toString(), itemDescription.getText().toString()
                , "PicURL", "test-date-27/10", testUser);
        returnGivenItem(temp);
    }

    /**
     * A method to return the give item back to the calling activity
     */
    private void returnGivenItem(GiveItem temp) {
        Log.d(TAG, "returnGivenItem: Returning item: " + temp.toString());
        Intent resultIntent = new Intent();
        Gson gson = new Gson();
        String jsonEvents = gson.toJson(temp);
        resultIntent.putExtra(NEW_GIVE_ITEM, jsonEvents);
        setResult(52, resultIntent);
        finish();

    }

    /**
     * A method to initialize the item condition spinner
     */
    private void initConditionSpinner() {
        Log.d(TAG, "initConditionSpinner: initing condition spinner");
        ArrayList<String> conditions = new ArrayList<>();
        conditions.add(getString(R.string.select_condition));
        conditions.add(getString(R.string.new_item));
        conditions.add(getString(R.string.used_item));
        conditions.add(getString(R.string.opened_item));

        //create an ArrayAdapter from the String Array
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, conditions);
        //set the view for the Drop down list
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set the ArrayAdapter to the spinner
        condition.setAdapter(dataAdapter);
        //attach the listener to the spinner
        condition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemSelected: Selected: " + conditions.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    /**
     * A method to initialize the item category spinner
     */
    private void initCategotySpinner() {
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

        //create an ArrayAdapter from the String Array
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, categories);
        //set the view for the Drop down list
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set the ArrayAdapter to the spinner
        category.setAdapter(dataAdapter);
        //attach the listener to the spinner
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemSelected: Selected: " + categories.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
