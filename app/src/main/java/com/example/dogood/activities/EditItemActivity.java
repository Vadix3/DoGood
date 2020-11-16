package com.example.dogood.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.dogood.Dialogs.PhotoModeDialog;
import com.example.dogood.R;
import com.example.dogood.interfaces.PhotoModeListener;
import com.example.dogood.objects.AskItem;
import com.example.dogood.objects.GiveItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
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

public class EditItemActivity extends AppCompatActivity implements PhotoModeListener {

    private static final String TAG = "Dogood";
    private static final String GIVE_ITEM = "giveItem";
    private static final String ASK_ITEM = "askItem";

    private static final int CAMERA_PERMISSION_SETTINGS_REQUSETCODE = 123;
    private static final int STORAGE_PERMISSION_SETTINGS_REQUSETCODE = 133;
    private static final int CAMERA_PICTURE_REQUEST = 124;
    private static final int STORAGE_PICTURE_REQUEST = 125;
    private static final int ITEM_EDIT_RESULT_CODE = 1015;

    private Context context = this;
    private FirebaseStorage storage = FirebaseStorage.getInstance();


    private GiveItem giveItem = null;
    private AskItem askItem = null;
    private boolean isGiveItem = false;
    private Bitmap userCustomImage;

    private ArrayList<String> categoriesUS;
    private ArrayList<String> conditionsUS;


    private MaterialButton submitBtn;

    /**
     * Ask items Layout
     */
    private ConstraintLayout askItemLayout;
    private EditText askItemName, askItemDescription;
    private Spinner askCategorySpinner;
    private CheckBox isDescrete;

    /**
     * Give items Layout
     */
    private ConstraintLayout giveItemLayout;
    private ShapeableImageView giveItemPhoto;
    private EditText giveItemName, giveItemDescription, giveItemPrice;
    private Spinner giveCategorySpinner, giveConditionSpinner;
    private CheckBox isFree;

    private boolean isPictureChanged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: edit item activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        Gson gson = new Gson();
        if (!getIntent().getStringExtra(GIVE_ITEM).equals("")) {
            giveItem = gson.fromJson(getIntent().getStringExtra(GIVE_ITEM), GiveItem.class);
            Log.d(TAG, "onCreate: Got give item: " + giveItem.toString());
        } else {
            askItem = gson.fromJson(getIntent().getStringExtra(ASK_ITEM), AskItem.class);
            Log.d(TAG, "onCreate: Got ask item: " + askItem.toString());
        }
        initViews();
    }

    private void initViews() {
        Log.d(TAG, "initViews: ");
        submitBtn = findViewById(R.id.editItem_BTN_submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForValidInput();
            }
        });
        askItemName = findViewById(R.id.editItem_EDT_AskitemName);
        askItemDescription = findViewById(R.id.editItem_EDT_askItemDescription);
        askCategorySpinner = findViewById(R.id.editItem_LST_AskCategorySpinner);
        isDescrete = findViewById(R.id.editItem_CHK_askIsDiscrete);

        giveItemPhoto = findViewById(R.id.editItem_IMG_itemPhoto);
        giveItemPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPhotoDialog();
            }
        });
        giveItemName = findViewById(R.id.editItem_EDT_itemName);
        giveItemDescription = findViewById(R.id.editItem_EDT_itemDescription);
        giveItemPrice = findViewById(R.id.editItem_EDT_itemPrice);
        giveCategorySpinner = findViewById(R.id.editItem_LST_categorySpinner);
        giveConditionSpinner = findViewById(R.id.editItem_LST_conditionSpinner);
        isFree = findViewById(R.id.editItem_CHK_isFree);

        if (askItem == null) {
            Log.d(TAG, "initViews: Got give item");
            isGiveItem = true;
            askItemName.setVisibility(View.INVISIBLE);
            askItemDescription.setVisibility(View.INVISIBLE);
            askCategorySpinner.setVisibility(View.INVISIBLE);
            isDescrete.setVisibility(View.INVISIBLE);


            getPhotoFromStorage();
            giveItemName.setText(giveItem.getName());
            giveItemDescription.setText(giveItem.getDescription());
            initConditionSpinner(giveConditionSpinner, giveItem.getState());

            if (giveItem.getPrice().equals("")) {
                isFree.setChecked(true);
                giveItemPrice.setText("");
            }
            initCategorySpinner(giveItem.getCategory(), giveCategorySpinner);

        } else {
            Log.d(TAG, "initViews: got ask item");
            giveItemPhoto.setVisibility(View.INVISIBLE);
            giveItemName.setVisibility(View.INVISIBLE);
            giveItemDescription.setVisibility(View.INVISIBLE);
            giveItemPrice.setVisibility(View.INVISIBLE);
            giveCategorySpinner.setVisibility(View.INVISIBLE);
            giveConditionSpinner.setVisibility(View.INVISIBLE);
            isFree.setVisibility(View.INVISIBLE);

            askItemName.setText(askItem.getName());
            askItemDescription.setText(askItem.getDescription());
            initCategorySpinner(askItem.getCategory(), askCategorySpinner);
            isDescrete.setChecked(askItem.isDiscreteRequest());
            giveItemPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openPhotoDialog();
                }
            });
        }

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
     * A method to check for valid user input
     */
    private void checkForValidInput() {
        Log.d(TAG, "checkForValidInput: Checking user input");
        if (askItem == null) {
            if (giveItemName.getText().toString().equals("")) {
                Log.d(TAG, "checkForValidInput: Empty item name");
                giveItemName.setError(getString(R.string.please_enter_item_name));
                submitBtn.setEnabled(true);
                return;
            }
            if (giveItemDescription.getText().toString().equals("")) {
                Log.d(TAG, "checkForValidInput: Empty item description");
                giveItemDescription.setError(getString(R.string.please_enter_item_decription));
                submitBtn.setEnabled(true);
                return;
            }
            if (giveConditionSpinner.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select_condition))) {
                Log.d(TAG, "checkForValidInput: Condition not selected");
                ((TextView) giveConditionSpinner.getSelectedView()).setError(getString(R.string.please_select_a_condition));
                submitBtn.setEnabled(true);
                return;
            }
            if (giveCategorySpinner.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select_categories))) {
                Log.d(TAG, "checkForValidInput: Category not selected");
                ((TextView) giveCategorySpinner.getSelectedView()).setError(getString(R.string.please_select_a_category));
                submitBtn.setEnabled(true);
                return;
            }
            if (giveItemPrice.getText().toString().equals("") && !isFree.isChecked()) {
                Log.d(TAG, "checkForValidInput: Item price is not selected but not free");
                giveItemPrice.setError(getString(R.string.be_reasonable));
                submitBtn.setEnabled(true);
                return;
            }
            if (giveItemPhoto == null) {
                Log.d(TAG, "checkForValidInput: User did not upload item photo");
                submitBtn.setEnabled(true);
                return;
            }

            Log.d(TAG, "checkForValidInput: Passed all checks!");
            Toast.makeText(EditItemActivity.this, getString(R.string.uploading_item), Toast.LENGTH_LONG).show();
            submitBtn.setEnabled(false);
            CircularProgressDrawable drawable = new CircularProgressDrawable(EditItemActivity.this);
            drawable.setColorSchemeColors(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.black);
            drawable.setCenterRadius(30f);
            drawable.setStrokeWidth(5f);
            drawable.start();
            submitBtn.setBackgroundDrawable(drawable);
            submitBtn.setText("Uploading item");
            if (isPictureChanged) {
                Log.d(TAG, "checkForValidInput: Picture changed!");
                uploadBitmapToStorage();
            } else {
                Log.d(TAG, "checkForValidInput: Picture didn't change!");
                updateGiveItem();
            }
        } else {

            boolean tempDiscrete = false;

            if (askItemName.getText().toString().equals("")) {
                Log.d(TAG, "checkForValidInput: Empty item name");
                askItemName.setError(getString(R.string.please_enter_item_name));
                return;
            }
            if (askItemDescription.getText().toString().equals("")) {
                Log.d(TAG, "checkForValidInput: Empty item description");
                askItemDescription.setError(getString(R.string.please_enter_item_decription));
                return;
            }
            if (askCategorySpinner.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select_categories))) {
                Log.d(TAG, "checkForValidInput: Category not selected");
                ((TextView) askCategorySpinner.getSelectedView()).setError(getString(R.string.please_select_a_category));
                return;
            }
            if (isDescrete.isChecked()) {
                Log.d(TAG, "checkAndSendData: discrete data is checked");
                tempDiscrete = true;
            }

            askItem.setName(askItemName.getText().toString());
            askItem.setCategory(askCategorySpinner.getSelectedItem().toString());
            askItem.setDescription(askItemDescription.getText().toString());
            askItem.setDiscreteRequest(tempDiscrete);

            Intent resultIntent = new Intent();
            Gson gson = new Gson();
            String jsonItems = gson.toJson(askItem);
            resultIntent.putExtra(ASK_ITEM, jsonItems);
            setResult(ITEM_EDIT_RESULT_CODE, resultIntent);
            finish();
        }

    }


    /**
     * A method to get the item photo from the storage
     */
    private void getPhotoFromStorage() {
        Log.d(TAG, "getPhotoFromStorage: Fetching photo from storage");
        String itemID = giveItem.getId();

        String path = "gs://" + getString(R.string.google_storage_bucket) + "/" + itemID + ".jpg";
        Log.d(TAG, "getPhotoFromStorage: Fetching: " + path);
        StorageReference gsReference = storage.getReferenceFromUrl(path);

        gsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "onSuccess: " + uri);
                // create a ProgressDrawable object which we will show as placeholder
                CircularProgressDrawable drawable = new CircularProgressDrawable(EditItemActivity.this);
                drawable.setColorSchemeColors(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.black);
                drawable.setCenterRadius(30f);
                drawable.setStrokeWidth(5f);
                drawable.start();
                Glide.with(EditItemActivity.this).load(uri).placeholder(drawable).into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        BitmapDrawable drawable = (BitmapDrawable) resource;
                        Bitmap bitmap = drawable.getBitmap();
                        userCustomImage = bitmap;
                        giveItemPhoto.setStrokeWidth(30);
                        giveItemPhoto.setStrokeColor(getColorStateList(R.color.colorPrimary));
                        giveItemPhoto.setImageBitmap(userCustomImage);

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "onFailure: Exception: " + exception.getMessage());
            }
        });
    }


    /**
     * A method to set the category spinner
     */
    private void initCategorySpinner(String category, Spinner spinner) {
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
        categoriesUS.add("Medical equipment");
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
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, categories);
        //set the view for the Drop down list
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set the ArrayAdapter to the spinner
        spinner.setAdapter(dataAdapter);
        //attach the listener to the spinner
        spinner.setSelection(dataAdapter.getPosition(category));
    }

    /**
     * A method to initialize the item condition spinner
     */
    private void initConditionSpinner(Spinner spinner, String category) {
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
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(dataAdapter.getPosition(category));
    }

    /**
     * A method to upload picture to Firebase Storage
     */
    private void uploadBitmapToStorage() {
        Log.d(TAG, "uploadBitmapToStorage: Uploading bitmap to storage: " + giveItem.getId());
        final String itemID = giveItem.getId();
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
                updateGiveItem();
            }
        });
    }

    private void updateGiveItem() {
        Log.d(TAG, "updateGiveItem: Updating give item");

        giveItem.setName(giveItemName.getText().toString());
        giveItem.setCategory(categoriesUS.get((int) giveCategorySpinner.getSelectedItemId()).toString());
        giveItem.setState(conditionsUS.get((int) giveConditionSpinner.getSelectedItemId()).toString());
        giveItem.setPrice(giveItemPrice.getText().toString());
        giveItem.setDescription(giveItemDescription.getText().toString());

        Intent resultIntent = new Intent();
        Gson gson = new Gson();
        String jsonEvents = gson.toJson(giveItem);
        resultIntent.putExtra(GIVE_ITEM, jsonEvents);
        setResult(ITEM_EDIT_RESULT_CODE, resultIntent);
        finish();
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
                    isPictureChanged = true;
                    userCustomImage = (Bitmap) data.getExtras().get("data");
                    giveItemPhoto.setStrokeWidth(30);
                    giveItemPhoto.setStrokeColor(getColorStateList(R.color.colorPrimary));
                    giveItemPhoto.setImageBitmap(userCustomImage);
                }
                break;
            case STORAGE_PICTURE_REQUEST:
                Log.d(TAG, "onActivityResult: I came from storage");
                if (data != null) {
                    isPictureChanged = true;
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        userCustomImage = bitmap;
                        giveItemPhoto.setStrokeWidth(30);
                        giveItemPhoto.setStrokeColor(getColorStateList(R.color.colorPrimary));
                        giveItemPhoto.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Log.i("TAG", "Some exception " + e);
                    }
                }
        }
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(EditItemActivity.this);
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
                        openCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            Log.d(TAG, "onPermissionDenied: User denied permission permanently!");
                            AlertDialog.Builder builder = new AlertDialog.Builder(EditItemActivity.this);
                            builder.setTitle(getString(R.string.permission_denied))
                                    .setMessage(getString(R.string.permission_denied_explication_storage))
                                    .setNegativeButton(R.string.cancel, null)
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


    @Override
    public void photoMode(Boolean fromCamera) {
        if (fromCamera) {
            Log.d(TAG, "photoMode: Taking picture from camera");
            if (ContextCompat.checkSelfPermission(EditItemActivity.this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onClick: User already given permission, moving straight to camera");
                openCamera();
            } else {
                checkingForCameraPermissions();
            }
        } else {
            Log.d(TAG, "photoMode: Fetching picture from storage");
            if (ContextCompat.checkSelfPermission(EditItemActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onClick: User already given permission, moving straight to storage");
                openStorage();
            } else {
                checkingForStoragePermissions();
            }
        }
    }

}
