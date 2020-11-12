package com.example.dogood.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.example.dogood.R;
import com.example.dogood.activities.ItemDetailsActivity;

public class ItemPhotoDialog extends Dialog {
    private Context context;
    private Uri photoUri;
    private RelativeLayout mainLayout;
    private ImageView photo;

    public ItemPhotoDialog(@NonNull Context context, Uri photoUri) {
        super(context);
        this.context = context;
        this.photoUri = photoUri;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_item_photo);
        photo = findViewById(R.id.itemphoto_img_photo);
        CircularProgressDrawable drawable = new CircularProgressDrawable(context);
        drawable.setColorSchemeColors(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.black);
        drawable.setCenterRadius(30f);
        drawable.setStrokeWidth(5f);
        // set all other properties as you would see fit and start it
        drawable.start();
        Glide.with(context).load(photoUri).placeholder(drawable).into(photo);
        photo.setOnTouchListener(new ImageMatrixTouchHandler(context));
    }

}
