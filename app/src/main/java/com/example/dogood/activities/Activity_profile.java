package com.example.dogood.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dogood.R;

public class Activity_profile extends AppCompatActivity {
    private static final String TAG = "Activity_profile";
    private ImageView profile_IMG_picture;
    private TextView profile_LBL_name;
    private TextView profile_LBL_city;
    private TextView profile_LBL_phone;
    private TextView profile_LBL_mail;
    private FrameLayout profile_LAY_post;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: ");
        findViews();
    }

    private void findViews() {
        profile_IMG_picture = findViewById(R.id.profile_IMG_picture);
        profile_LBL_name = findViewById(R.id.profile_LBL_name);
        profile_LBL_city = findViewById(R.id.profile_LBL_city);
        profile_LBL_phone = findViewById(R.id.profile_LBL_phone);
        profile_LBL_mail = findViewById(R.id.profile_LBL_mail);

        profile_LAY_post = findViewById(R.id.profile_LAY_post);
    }
}