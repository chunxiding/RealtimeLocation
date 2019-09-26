package com.example.realtimelocation.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.example.realtimelocation.Utils.Common;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.paperdb.Paper;

public class MyLocationReceiver extends BroadcastReceiver {
    public static final String ACTION = "com.example.realtimelocation.UPDATE_LOCATION";

    DatabaseReference publicLocation;
    String uid;

    public MyLocationReceiver() {
        publicLocation = FirebaseDatabase.getInstance().getReference(Common.PUBLIC_LOCATION);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Paper.init(context);

        uid = Paper.book().read(Common.USER_UID_SAVE_KEY);
        if (intent != null) {
            final String action = intent.getAction();
            if (action.equals(ACTION)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    Location location = result.getLastLocation();
                    // if app open
                    if (Common.loggedUser != null) {
                        publicLocation.child(Common.loggedUser.getUid()).setValue(location);
                    } else {
                        // app closed
                        publicLocation.child(uid).setValue(location);
                    }
                }
            }
        }
    }
}
