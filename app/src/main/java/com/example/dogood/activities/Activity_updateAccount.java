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
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.dogood.Dialogs.PhotoModeDialog;
import com.example.dogood.R;
import com.example.dogood.interfaces.PhotoModeListener;
import com.example.dogood.objects.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Activity_updateAccount extends AppCompatActivity implements PhotoModeListener {
    private static final String TAG = "UpdateAccountDialog";
    private static final String UPDATE_PROFILE = "user";
    private static final int UPDATE_PROFILE_RESULT_CODE = 1013;

    private static final int CAMERA_PERMISSION_SETTINGS_REQUSETCODE = 123;
    private static final int STORAGE_PERMISSION_SETTINGS_REQUSETCODE = 133;
    private static final int CAMERA_PICTURE_REQUEST = 124;
    private static final int STORAGE_PICTURE_REQUEST = 125;

    private ShapeableImageView updateAccount_IMG_photo;
    private TextInputLayout updateAccount_EDT_name;
    private TextInputLayout updateAccount_EDT_city;
    private TextInputLayout updateAccount_EDT_phone;
    private MaterialButton updateAccount_BTN_submit;

    private Bitmap userCustomImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_update_account);

        findViews();
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
                    updateAccount_IMG_photo.setStrokeWidth(30);
                    updateAccount_IMG_photo.setStrokeColor(getColorStateList(R.color.colorPrimary));
                    updateAccount_IMG_photo.setImageBitmap(userCustomImage);
                }
                break;
            case STORAGE_PICTURE_REQUEST:
                Log.d(TAG, "onActivityResult: I came from storage");
                if (data != null) {
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        userCustomImage = bitmap;
                        updateAccount_IMG_photo.setStrokeWidth(30);
                        updateAccount_IMG_photo.setStrokeColor(getColorStateList(R.color.colorPrimary));
                        updateAccount_IMG_photo.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Log.i("TAG", "Some exception " + e);
                    }
                }
        }
    }

    /**
     * A method to initialize the activity views
     */
    private void findViews() {
        Log.d(TAG, "findViews: ");
        updateAccount_IMG_photo = findViewById(R.id.updateAccount_IMG_photo);
        updateAccount_EDT_name = findViewById(R.id.updateAccount_EDT_name);
        updateAccount_EDT_city = findViewById(R.id.updateAccount_EDT_city);
        updateAccount_EDT_phone = findViewById(R.id.updateAccount_EDT_phone);
        updateAccount_BTN_submit = findViewById(R.id.updateAccount_BTN_submit);
        updateAccount_BTN_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndSendData();
            }
        });
        updateAccount_IMG_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhotoDialog();
            }
        });
    }

    private void openPhotoDialog() {
        Log.d(TAG, "openPhotoDialog: ");
        //TODO: fix quality
        PhotoModeDialog photoModeDialog = new PhotoModeDialog(Activity_updateAccount.this);
        photoModeDialog.show();
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        photoModeDialog.getWindow().setLayout(width, RelativeLayout.LayoutParams.WRAP_CONTENT);
        photoModeDialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        photoModeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        photoModeDialog.getWindow().setDimAmount(0.9f);
    }

    private void checkAndSendData() {
        if (updateAccount_EDT_city.toString().equals("")){
            Log.d(TAG, "checkAndSendData: Empty item description");
            updateAccount_EDT_city.setError("Please enter City");
            return;
        }
        if (updateAccount_EDT_phone.toString().equals("")){
            Log.d(TAG, "checkAndSendData: Empty item description");
            updateAccount_EDT_phone.setError("Please enter phone");
            return;
        }
        if (updateAccount_EDT_name.toString().equals("")){
            Log.d(TAG, "checkAndSendData: Empty item description");
            updateAccount_EDT_name.setError("Please enter Name");
            return;
        }
        String photosJson = null;
        if (userCustomImage != null){
            photosJson = bitMapToString(userCustomImage);
        }

        returnUpdate(updateAccount_EDT_city.getEditText().getText().toString(),updateAccount_EDT_phone.getEditText().getText().toString(),updateAccount_EDT_name.getEditText().getText().toString(),photosJson);
    }

    private void returnUpdate(String city, String phone, String name, String photosJson) {
        Log.d(TAG, "returnGivenItem: Returning item: " );
        Intent resultIntent = new Intent();
        resultIntent.putExtra("city",city );
        resultIntent.putExtra("phone",phone );
        resultIntent.putExtra("name",name );
        if(photosJson != null){
            resultIntent.putExtra("photo",photosJson );
        }
        setResult(UPDATE_PROFILE_RESULT_CODE, resultIntent);
        finish();
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


    @Override
    public void photoMode(Boolean fromCamera) {
        if (fromCamera) {
            Log.d(TAG, "photoMode: Taking picture from camera");
            if (ContextCompat.checkSelfPermission(Activity_updateAccount.this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onClick: User already given permission, moving straight to camera");
                openCamera();
            } else {
                checkingForCameraPermissions();
            }
        } else {
            Log.d(TAG, "photoMode: Fetching picture from storage");
            if (ContextCompat.checkSelfPermission(Activity_updateAccount.this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onClick: User already given permission, moving straight to storage");
                openStorage();
            } else {
                checkingForStoragePermissions();
            }
        }
    }

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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_updateAccount.this);
                            builder.setTitle("Permission Denied")
                                    .setMessage("Storage permission is required to add a photo of your item.\n" +
                                            "Please allow storage permissions in app settings.")
                                    .setNegativeButton("Cancel", null)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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

    private void openStorage() {
        Log.d(TAG, "openStorage: opening storage");
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, STORAGE_PICTURE_REQUEST);
    }

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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_updateAccount.this);
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

    private void openCamera() {
        Log.d(TAG, "openCamera: opening camera");
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_PICTURE_REQUEST);
    }
}

