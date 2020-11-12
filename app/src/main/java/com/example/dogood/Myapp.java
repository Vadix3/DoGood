package com.example.dogood;

import android.app.Application;
import android.util.Log;

import com.example.dogood.objects.User;

public class Myapp extends Application {
    private static final String TAG = "Myapp";
    @Override
    public void onCreate() {
        super.onCreate();
        //TODO: Make global toast
        Log.d(TAG, "onCreate: ");
    }
}
