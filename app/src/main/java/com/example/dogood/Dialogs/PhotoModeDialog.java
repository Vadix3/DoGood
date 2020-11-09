package com.example.dogood.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.dogood.R;
import com.example.dogood.interfaces.PhotoModeListener;

public class PhotoModeDialog extends Dialog {
    private static final String TAG = "Dogood";
    private Context context;
    private ImageView cameraOption;
    private ImageView storageOption;

    public PhotoModeDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_picture_mode);
        findViews();
    }


    /**
     * A method to initialize the views
     */
    private void findViews() {
        Log.d(TAG, "findViews: ");
        cameraOption = findViewById(R.id.dialogPicMode_IMG_camera);
        cameraOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });
        storageOption = findViewById(R.id.dialogPicMode_IMG_storage);
        storageOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openStorage();
            }
        });
    }

    /**
     * A method to open the camera
     */
    private void openCamera() {
        Log.d(TAG, "openCamera: ");
        PhotoModeListener photoModeListener = (PhotoModeListener) context;
        photoModeListener.photoMode(true);
        dismiss();
    }

    /**
     * A method to open the storage
     */
    private void openStorage() {
        Log.d(TAG, "openStorage:");
        PhotoModeListener photoModeListener = (PhotoModeListener) context;
        photoModeListener.photoMode(false);
        dismiss();
    }
}
