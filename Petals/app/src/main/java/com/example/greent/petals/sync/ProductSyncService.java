package com.example.greent.petals.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ProductSyncService extends Service {
    private static final String TAG = "ProductSyncService";
    //Storage for an instance of the sync adapter
    private static PetalsSyncAdapter petalsSyncAdapter = null;
    //Object to use as a thread-safe lock
    private static final Object petalSyncAdapterLock = new Object();

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate() called");
        synchronized (petalSyncAdapterLock) {
            if (petalsSyncAdapter == null) {
                petalsSyncAdapter = new PetalsSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return petalsSyncAdapter.getSyncAdapterBinder();
    }
}
