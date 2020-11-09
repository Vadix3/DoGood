package com.example.dogood.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.example.dogood.R;
import com.example.dogood.objects.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;

import static com.google.firebase.crashlytics.internal.Logger.TAG;

public class UpdateAccountDialog extends Dialog {
    private static final String TAG = "UpdateAccountDialog";
    private Context context;
    private ShapeableImageView updateAccount_IMG_photo;
    private TextInputLayout updateAccount_EDT_name;
    private TextInputLayout updateAccount_EDT_city;
    private TextInputLayout updateAccount_EDT_phone;
    private MaterialButton updateAccount_BTN_submit;
    private User mUser;

    public UpdateAccountDialog(@NonNull Context context, User user) {
        super(context);
        this.context = context;
        this.mUser = user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_update_account);

        findViews();


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
        PhotoModeDialog photoModeDialog = new PhotoModeDialog(context);
        photoModeDialog.show();
        int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.8);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.9);
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

        mUser.setCity(updateAccount_EDT_city.getEditText().getText().toString());
        mUser.setPhone(updateAccount_EDT_phone.getEditText().getText().toString());
        mUser.setName(updateAccount_EDT_name.getEditText().getText().toString());

        //String photosJson = bitMapToString(updateAccount_IMG_photo);
        dismiss();
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



}
