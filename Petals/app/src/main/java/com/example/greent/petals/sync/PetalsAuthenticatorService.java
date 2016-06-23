package com.example.greent.petals.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PetalsAuthenticatorService extends Service {

    //Instance field that stores the authenticator object
    private PetalsAuthenticator mAuthenticator;


    @Override
    public void onCreate() {
        //Create a new authenticator object
        mAuthenticator = new PetalsAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
