package com.example.dogood.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.dogood.R;
import com.example.dogood.objects.GiveItem;
import com.example.dogood.objects.User;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class NewGiveItemActivity extends AppCompatActivity {
    private static final String TAG = "Dogood";
    private static final String NEW_GIVE_ITEM = "111";
    private static final int CAMERA_PERMISSION_REQUSETCODE = 122;
    private static final int CAMERA_PERMISSION_SETTINGS_REQUSETCODE = 123;
    private static final int CAMERA_PICTURE_REQUEST = 124;
    private static final int NEW_GIVE_ITEM_RESULT_CODE = 1011;


    private ShapeableImageView itemPhoto;
    private EditText itemName;
    private EditText itemPrice;
    private EditText itemDescription;
    private CheckBox freeItem;
    private Spinner condition;
    private Spinner category;
    private Button submitBtn;

    private Bitmap userCustomImage;

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
        itemPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(NewGiveItemActivity.this, Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onClick: User already given permission, moving straight to camera");
                    openCamera();
                } else {
                    checkingForCameraPermissions();
                }
            }
        });
        itemName = findViewById(R.id.addgiveitem_EDT_itemName);
        itemDescription = findViewById(R.id.addgiveitem_EDT_itemDescription);
        itemPrice = findViewById(R.id.addgiveitem_EDT_itemPrice);
        freeItem = findViewById(R.id.addAskItem_CHK_isDescrete);
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
        initCategorySpinner();
        initConditionSpinner();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForValidInput();
            }
        });
    }


    /**
     * A method to open the camera
     */
    private void openCamera() {
        Log.d(TAG, "openCamera: opening camera");
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_PICTURE_REQUEST);
    }

    /**
     * A method to check for camera permissions
     */
    private void checkingForCameraPermissions() {
        Log.d(TAG, "addPhotoToPage: checking for users permissions");

        Dexter.withContext(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Log.d(TAG, "onPermissionGranted: User given permission");
                        openCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            Log.d(TAG, "onPermissionDenied: User denied permission permanently!");
                            AlertDialog.Builder builder = new AlertDialog.Builder(NewGiveItemActivity.this);
                            builder.setTitle("Permission Denied")
                                    .setMessage("Camera permission is required to take a photo of your item.\n" +
                                            "Please allow camera permissions in app settings.")
                                    .setNegativeButton("Cancel", null)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Log.d(TAG, "onClick: Opening settings activity");
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                                            startActivityForResult(intent, CAMERA_PERMISSION_SETTINGS_REQUSETCODE);
                                        }
                                    }).show();
                        } else {
                            Log.d(TAG, "onPermissionDenied: User denied permission!");
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
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
        if (userCustomImage == null) {
            Log.d(TAG, "checkForValidInput: User did not upload item photo");
            return;
        }

        Log.d(TAG, "checkForValidInput: Passed all checks!");
        User testUser = new User("Vadim", "dogoodapp1@gmail.com", "123456"
                , "Netanya", "0541234567", "Photo URL");

        String photosJson = bitMapToString(userCustomImage);

        GiveItem temp = new GiveItem("12", itemName.getText().toString(), category.getSelectedItem().toString()
                , condition.getSelectedItem().toString(), itemPrice.getText().toString(), itemDescription.getText().toString()
                , photosJson, "test-date-27/10", testUser);
        returnGivenItem(temp);
    }

    /**
     * A method to convert bitmap to string
     */
    private String bitMapToString(Bitmap bitmap) {
        Log.d(TAG, "bitMapToString: Converting bitmap to string");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
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
        setResult(NEW_GIVE_ITEM_RESULT_CODE, resultIntent);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: Im im activity result");
        switch (requestCode) {
            case CAMERA_PERMISSION_SETTINGS_REQUSETCODE:
                Log.d(TAG, "onActivityResult: I came from app settings");
                break;

            case CAMERA_PICTURE_REQUEST:
                Log.d(TAG, "onActivityResult: I came from camera");
                userCustomImage = (Bitmap) data.getExtras().get("data");
                itemPhoto.setStrokeWidth(30);
                itemPhoto.setStrokeColor(getColorStateList(R.color.colorPrimary));
                itemPhoto.setImageBitmap(userCustomImage);
                break;
        }
    }
}
