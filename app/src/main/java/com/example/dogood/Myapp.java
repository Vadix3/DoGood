package com.example.dogood;

import android.app.Application;
import android.util.Log;

public class Myapp extends Application {
    private static final String TAG = "Myapp";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
    }
}
