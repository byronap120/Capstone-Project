package com.example.byron.vrviewer;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Byron on 11/27/2016.
 */

public class VRViewerApp extends android.app.Application  {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
