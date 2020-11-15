package com.example.dogood.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.example.dogood.Dialogs.PhotoModeDialog;
import com.example.dogood.R;
import com.example.dogood.interfaces.PhotoModeListener;
import com.example.dogood.objects.GiveItem;
import com.example.dogood.objects.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class NewGiveItemActivity extends AppCompatActivity implements PhotoModeListener {
    private static final String TAG = "Dogood";
    private static final String NEW_GIVE_ITEM = "111";
    public static final String CURRENT_USER = "currentUser";
    private static final int CAMERA_PERMISSION_SETTINGS_REQUSETCODE = 123;
    private static final int STORAGE_PERMISSION_SETTINGS_REQUSETCODE = 133;
    private static final int CAMERA_PICTURE_REQUEST = 124;
    private static final int STORAGE_PICTURE_REQUEST = 125;
    private static final int NEW_GIVE_ITEM_RESULT_CODE = 1011;

    private static final String ITEM_COUNT = "itemCount";


    FirebaseStorage storage = FirebaseStorage.getInstance();

    private ShapeableImageView itemPhoto;
    private TextInputLayout itemName;
    private EditText itemPrice;
    private TextInputLayout itemDescription;
    private CheckBox freeItem;
    private Spinner condition;
    private Spinner category;
    private MaterialButton submitBtn;

    private Bitmap userCustomImage;

    private User currentUser;

    private int itemCount = 0;
    private ArrayList<String> categoriesUS;
    private ArrayList<String> conditionsUS;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_give_item);

        String userJson = getIntent().getStringExtra(CURRENT_USER);
        itemCount = getIntent().getIntExtra(ITEM_COUNT, 0);
        Gson gson = new Gson();
        currentUser = gson.fromJson(userJson, User.class);

        initViews();
    }

    /**
     * A method to open photo choices dialog
     */
    private void openPhotoDialog() {
        Log.d(TAG, "openPhotoDialog: ");
        //TODO: fix quality
        PhotoModeDialog photoModeDialog = new PhotoModeDialog(this);
        photoModeDialog.show();
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        photoModeDialog.getWindow().setLayout(width, RelativeLayout.LayoutParams.WRAP_CONTENT);
        photoModeDialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        photoModeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        photoModeDialog.getWindow().setDimAmount(0.9f);
    }

    /**
     * A method to init the views
     */
    private void initViews() {
        Log.d(TAG, "initViews: Creating views");
        itemPhoto = findViewById(R.id.editItem_IMG_itemPhoto);
        itemPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPhotoDialog();
            }
        });
        itemName = findViewById(R.id.editItem_EDT_itemName);
        itemDescription = findViewById(R.id.editItem_EDT_itemDescription);
        itemPrice = findViewById(R.id.editItem_EDT_itemPrice);
        freeItem = findViewById(R.id.editItem_CHK_askIsDiscrete);
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
        condition = findViewById(R.id.editItem_LST_conditionSpinner);
        category = findViewById(R.id.editItem_LST_categorySpinner);
        submitBtn = findViewById(R.id.editItem_BTN_submitBtn);
        initCategorySpinner();
        initConditionSpinner();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForValidInput();
                submitBtn.setEnabled(false);
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
     * A method to open the storage to fetch a photo
     */
    private void openStorage() {
        Log.d(TAG, "openStorage: opening storage");
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, STORAGE_PICTURE_REQUEST);
    }

    /**
     * A method to check for camera permissions
     */
    private void checkingForCameraPermissions() {
        Log.d(TAG, "checkingForCameraPermissions: checking for users permissions");

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
                            builder.setTitle(getString(R.string.permission_denied))
                                    .setMessage(getString(R.string.permission_denied_explication_camera))
                                    .setNegativeButton(getString(R.string.cancel), null)
                                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
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
     * A method to check for storage access permissions
     */
    private void checkingForStoragePermissions() {
        Log.d(TAG, "checkingForStoragePermissions: checking for users permissions");

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Log.d(TAG, "onPermissionGranted: User given permission");
                        openStorage();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            Log.d(TAG, "onPermissionDenied: User denied permission permanently!");
                            AlertDialog.Builder builder = new AlertDialog.Builder(NewGiveItemActivity.this);
                            builder.setTitle(getString(R.string.permission_denied))
                                    .setMessage(getString(R.string.permission_denied_explication_storage))
                                    .setNegativeButton(getString(R.string.cancel), null)
                                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Log.d(TAG, "onClick: Opening settings activity");
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                                            startActivityForResult(intent, STORAGE_PERMISSION_SETTINGS_REQUSETCODE);
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
        if (itemName.getEditText().getText().toString().equals("")) {
            Log.d(TAG, "checkForValidInput: Empty item name");
            itemName.setError(getString(R.string.please_enter_item_name));
            submitBtn.setEnabled(true);
            return;
        }
        if (itemDescription.getEditText().getText().toString().equals("")) {
            Log.d(TAG, "checkForValidInput: Empty item description");
            itemDescription.setError(getString(R.string.please_enter_item_decription));
            submitBtn.setEnabled(true);
            return;
        }
        if (condition.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select_condition))) {
            Log.d(TAG, "checkForValidInput: Condition not selected");
            ((TextView) condition.getSelectedView()).setError(getString(R.string.please_select_a_condition));
            submitBtn.setEnabled(true);
            return;
        }
        if (category.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select_categories))) {
            Log.d(TAG, "checkForValidInput: Category not selected");
            ((TextView) category.getSelectedView()).setError(getString(R.string.please_select_a_category));
            submitBtn.setEnabled(true);
            return;
        }
        if (itemPrice.getText().toString().equals("") && !freeItem.isChecked()) {
            Log.d(TAG, "checkForValidInput: Item price is not selected but not free");
            itemPrice.setError(getString(R.string.be_reasonable));
            submitBtn.setEnabled(true);
            return;
        }
        if (userCustomImage == null) {
            Log.d(TAG, "checkForValidInput: User did not upload item photo");
            submitBtn.setEnabled(true);
            return;
        }

        Log.d(TAG, "checkForValidInput: Passed all checks!");
        Toast.makeText(this, getText(R.string.uploading_item), Toast.LENGTH_LONG).show();
//        checkForFirebaseAuthLogin(); TODO: Fix google and fb here
        uploadBitmapToStorage();
    }

    /**
     * A method to check if there is a firebase login instance.
     * Since user may login using facebook or google there is no firebaseAuth instance
     * to upload images to firebase storage.
     */
    private void checkForFirebaseAuthLogin() {
        Log.d(TAG, "checkForFirebaseAuthLogin: ");
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Log.d(TAG, "checkForFirebaseAuthLogin: user is not null!");
            uploadBitmapToStorage();
        } else {
            signInAnonymously();
        }
    }

    /**
     * A method to sign in anonymously in case he hasn't logged in with firebase auth
     */
    private void signInAnonymously() {
        Log.d(TAG, "signInAnonymously: ");
        mAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d(TAG, "onSuccess: Signed in anonymously!");
                try {
                    uploadBitmapToStorage();
                } catch (Exception e) {
                    Log.d(TAG, "onSuccess: Failed to upload image to after anon: " + e.getMessage());
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "signInAnonymously:FAILURE" + exception.getMessage());
            }
        });
    }

    /**
     * A method to upload picture to Firebase Storage
     */
    private void uploadBitmapToStorage() {
        Log.d(TAG, "uploadBitmapToStorage: Uploading bitmap to storage: ID: G" + itemCount);
        CircularProgressDrawable drawable = new CircularProgressDrawable(NewGiveItemActivity.this);
        drawable.setColorSchemeColors(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.black);
        drawable.setCenterRadius(30f);
        drawable.setStrokeWidth(5f);
        drawable.start();
        submitBtn.setBackgroundDrawable(drawable);
        submitBtn.setText(getString(R.string.uploading_item));

        final String itemID = "G" + itemCount;
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl("gs://" + getString(R.string.google_storage_bucket));

        // Create a reference to "mountains.jpg"
        StorageReference tempRef = storageRef.child(itemID + ".jpg");

        // Create a reference to 'images/mountains.jpg'
        StorageReference tempImagesRef = storageRef.child("images/" + itemID + ".jpg");

        // While the file names are the same, the references point to different files
        tempRef.getName().equals(tempImagesRef.getName());    // true
        tempRef.getPath().equals(tempImagesRef.getPath());    // false


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        userCustomImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = tempRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "onFailure: Upload failed: " + exception.getMessage());
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: Image upload successful!");
                // TODO: Fix google and facebook photo issues

                GiveItem temp = new GiveItem(itemID, itemName.getEditText().getText().toString(), categoriesUS.get((int) category.getSelectedItemId()).toString()
                        , conditionsUS.get((int) condition.getSelectedItemId()).toString(), itemPrice.getText().toString(), itemDescription.getEditText().getText().toString()
                        , "Test", "test-date-27/10", currentUser);
                returnGivenItem(temp);
            }
        });
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

        conditionsUS = new ArrayList<>();
        conditionsUS.add("Select condition");
        conditionsUS.add("New");
        conditionsUS.add("Used");
        conditionsUS.add("opened but not used");

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

        categoriesUS = new ArrayList<>();
        categoriesUS.add("Select categories");
        categoriesUS.add("Clothes");
        categoriesUS.add("Office supplies");
        categoriesUS.add("Medical_equipment");
        categoriesUS.add("Gaming");
        categoriesUS.add("Electronics");
        categoriesUS.add("Appliances");
        categoriesUS.add("Gift cards");
        categoriesUS.add("Lighting");
        categoriesUS.add("Games and Toys");
        categoriesUS.add("Cellular");
        categoriesUS.add("Books");
        categoriesUS.add("Baby Supplies");
        categoriesUS.add("Computers");
        categoriesUS.add("Other");




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
                Log.d(TAG, "onActivityResult: I came from app settings: camera");
                break;
            case STORAGE_PERMISSION_SETTINGS_REQUSETCODE:
                Log.d(TAG, "onActivityResult: I came from app settings: storage");
                break;
            case CAMERA_PICTURE_REQUEST:
                Log.d(TAG, "onActivityResult: I came from camera");
                if (data != null) {
                    userCustomImage = (Bitmap) data.getExtras().get("data");
                    itemPhoto.setStrokeWidth(30);
                    itemPhoto.setStrokeColor(getColorStateList(R.color.colorPrimary));
                    itemPhoto.setImageBitmap(userCustomImage);
                }
                break;
            case STORAGE_PICTURE_REQUEST:
                Log.d(TAG, "onActivityResult: I came from storage");
                if (data != null) {
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        userCustomImage = bitmap;
                        itemPhoto.setStrokeWidth(30);
                        itemPhoto.setStrokeColor(getColorStateList(R.color.colorPrimary));
                        itemPhoto.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Log.i("TAG", "Some exception " + e);
                    }
                }
                break;
        }
    }

    @Override
    public void photoMode(Boolean fromCamera) {
        if (fromCamera) {
            Log.d(TAG, "photoMode: Taking picture from camera");
            if (ContextCompat.checkSelfPermission(NewGiveItemActivity.this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onClick: User already given permission, moving straight to camera");
                openCamera();
            } else {
                checkingForCameraPermissions();
            }
        } else {
            Log.d(TAG, "photoMode: Fetching picture from storage");
            if (ContextCompat.checkSelfPermission(NewGiveItemActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onClick: User already given permission, moving straight to storage");
                openStorage();
            } else {
                checkingForStoragePermissions();
            }
        }
    }
}
