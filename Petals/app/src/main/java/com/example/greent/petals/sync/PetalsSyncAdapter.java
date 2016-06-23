package com.example.greent.petals.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.greent.petals.R;
import com.example.greent.petals.data.ProductDBContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by greent on 6/20/16.
 */

public class PetalsSyncAdapter extends AbstractThreadedSyncAdapter {
    //Some globals
    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final String TAG = "PetalsSyncAdapter";

    ContentResolver mContentResolver;




    public PetalsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContentResolver = context.getContentResolver();
    }

    public PetalsSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);

        mContentResolver = context.getContentResolver();
    }

    public static Account getSyncAccount(Context context) {

        Log.d(TAG, "getSyncAccount() called with: context = [" + context + "]");
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount,context);
        }

        Log.d(TAG, "getSyncAccount() returned: " + newAccount);
        return newAccount;
    }


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Log.d(TAG, "syncImmediately() called with: context = [" + context + "]");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
        Log.d(TAG, "syncImmediately() returned: " + "done");
    }

    public static void initializeSyncAdapter(Context context) {
        Log.d(TAG, "initializeSyncAdapter() called with: context = [" + context + "]");
        getSyncAccount(context);
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        Log.d(TAG, "onAccountCreated() called with: newAccount = [" + newAccount + "], context = [" + context + "]");
        /*
         * Since we've created an account
         */
        PetalsSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "onPerformSync() called with: account = [" + account + "], extras = [" + extras + "], authority = [" + authority + "], provider = [" + provider + "], syncResult = [" + syncResult + "]");
        //Build the request
        final String ALL_PRODUCTS_URL = "https://blumanana.herokuapp.com/products/apop";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(ALL_PRODUCTS_URL)
                .build();

        try {
            //make the call
            Response response = client.newCall(request).execute();

            //assertTrue("Error: Request was not successful. ", response.isSuccessful());

            String responseBody = response.body().string();

            Log.d(TAG, "onPerformSync: JSON Response - " + responseBody);

            //create a JSONArray object from the response
            JSONArray fetchedProducts = new JSONArray(responseBody);

            //let's extract the entries into a list of FlowerProduct models
            ContentValues[] productsToAdd = extractProductsFromJson(fetchedProducts);

            //Add the Products to the database.
            Uri uri = ProductDBContract.ProductEntry.CONTENT_URI;

            int count = getContext().getContentResolver().bulkInsert(uri,productsToAdd);

            Log.d(TAG, "onPerformSync: inserted " + count + " rows.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ContentValues[] extractProductsFromJson(JSONArray jsonToProcess) {
        Log.d(TAG, "extractProductsFromJson() called with: jsonToProcess = [" + jsonToProcess + "]");
        final String JSON_KEY_CODE = "code";
        final String JSON_KEY_DESC = "description";
        final String JSON_KEY_LARGE = "large";
        final String JSON_KEY_SMALL = "small";
        final String JSON_KEY_PRICE = "price";
        final String JSON_KEY_NAME = "name";
        final String JSON_KEY_DIMEN = "dimension";
        final String JSON_KEY_TYPE = "type";
        final String JSON_KEY_OCC = "occasions";

        //return data structure
        ArrayList<ContentValues> productsToInsert = new ArrayList<>();


        //Iterate through the array
        for (int i = 0; i < jsonToProcess.length(); i++) {
            //Grab an entry
            try {
                JSONObject entry = jsonToProcess.getJSONObject(i);
                String fetchedCode = entry.optString(JSON_KEY_CODE);
                String fetchedDesc = entry.optString(JSON_KEY_DESC);
                String fetchedLarge = entry.optString(JSON_KEY_LARGE);
                String fetchedSmall = entry.optString(JSON_KEY_SMALL);
                Double fetchedPrice = entry.optDouble(JSON_KEY_PRICE);
                String fetchedName = entry.optString(JSON_KEY_NAME);
                String fetchedDimen = entry.optString(JSON_KEY_DIMEN);
                String fetchedType = entry.optString(JSON_KEY_TYPE);

                String fetchedOccasion = null;
                JSONArray fetchedOccasions = null;

                //occasions could be an array | a string | or null
                //try to fetch a JSON Array first
                if (entry.isNull(JSON_KEY_OCC)) {
                    //manually assign the occasion
                    fetchedOccasion = "ao";
                } else {
                    //entry exists, let's check for an array.
                    fetchedOccasions = entry.optJSONArray(JSON_KEY_OCC);
                    if (fetchedOccasions == null) {
                        //entry is not an array. must be a single string. extract it.
                        fetchedOccasion = entry.getString(JSON_KEY_OCC);
                    }
                }

                //Product table has a unique constraint on code+category,
                //so we need to add a seperate entry for each occasion fetched if necessary

                if (fetchedOccasions != null) {
                    //need to iterate through each entry in the JSONArray
                    Log.d(TAG, "extractProductsFromJson: found an Occasion array...processing.");
                    for (int j = 0; j < fetchedOccasions.length(); j++) {
                        fetchedOccasion = fetchedOccasions.getString(j);
                        ContentValues cv = new ContentValues();
                        cv.put(ProductDBContract.ProductEntry.COLUMN_NAME_CODE,fetchedCode);
                        cv.put(ProductDBContract.ProductEntry.COLUMN_NAME_DESC,fetchedDesc);
                        cv.put(ProductDBContract.ProductEntry.COLUMN_NAME_IMG_LOC_LRG,fetchedLarge);
                        cv.put(ProductDBContract.ProductEntry.COLUMN_NAME_IMG_LOC_SML,fetchedSmall);
                        cv.put(ProductDBContract.ProductEntry.COLUMN_NAME_PRICE,fetchedPrice);
                        cv.put(ProductDBContract.ProductEntry.COLUMN_NAME_NAME,fetchedName);
                        cv.put(ProductDBContract.ProductEntry.COLUMN_NAME_DIMENSION,fetchedDimen);
                        cv.put(ProductDBContract.ProductEntry.COLUMN_NAME_TYPE,fetchedType);
                        cv.put(ProductDBContract.ProductEntry.COLUMN_NAME_OCCASION,fetchedOccasion);

                        //put the products to insert in the return ArrayList
                        productsToInsert.add(cv);
                    }
                } else {
                    ContentValues cv = new ContentValues();
                    cv.put(ProductDBContract.ProductEntry.COLUMN_NAME_CODE,fetchedCode);
                    cv.put(ProductDBContract.ProductEntry.COLUMN_NAME_DESC,fetchedDesc);
                    cv.put(ProductDBContract.ProductEntry.COLUMN_NAME_IMG_LOC_LRG,fetchedLarge);
                    cv.put(ProductDBContract.ProductEntry.COLUMN_NAME_IMG_LOC_SML,fetchedSmall);
                    cv.put(ProductDBContract.ProductEntry.COLUMN_NAME_PRICE,fetchedPrice);
                    cv.put(ProductDBContract.ProductEntry.COLUMN_NAME_NAME,fetchedName);
                    cv.put(ProductDBContract.ProductEntry.COLUMN_NAME_DIMENSION,fetchedDimen);
                    cv.put(ProductDBContract.ProductEntry.COLUMN_NAME_TYPE,fetchedType);
                    cv.put(ProductDBContract.ProductEntry.COLUMN_NAME_OCCASION,fetchedOccasion);

                    //put the products to insert in the return ArrayList
                    productsToInsert.add(cv);
                }

                Log.d(TAG, "extractProductsFromJson: collected " + productsToInsert.size() + " entries.");


            } catch (JSONException je) {
                Log.d(TAG, "extractProductsFromJson: Crap! A JSON issue.");
                je.printStackTrace();
            }
        }

        ContentValues[] fetchedStuff = new ContentValues[productsToInsert.size()];

        productsToInsert.toArray(fetchedStuff);

        return fetchedStuff;
    }
}
