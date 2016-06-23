package com.example.greent.petals;

import android.test.AndroidTestCase;

import org.json.JSONArray;
import org.junit.Test;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by greent on 6/21/16.
 */

public class TestApiCalls extends AndroidTestCase {
    @Test
    public void testAPIConnection() throws Exception {
        final String API_BASE_URL = "https://blumanana.herokuapp.com/products/b";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(API_BASE_URL)
                .build();

        Response response = client.newCall(request).execute();

        assertTrue("Error: Request was not successful. ", response.isSuccessful());

        String responseBody = response.body().string();

        assertTrue("Error: Invalid Response. " + responseBody, responseBody.contains("Balloon"));
    }

    @Test
    public void testProductCount() throws Exception {
        final String ALL_PRODUCTS_URL = "https://blumanana.herokuapp.com/products/apop";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(ALL_PRODUCTS_URL)
                .build();

        Response response = client.newCall(request).execute();

        assertTrue("Error: Request was not successful. ", response.isSuccessful());

        String responseBody = response.body().string();

        //Make it a JSON Object
        JSONArray fetchedItems = new JSONArray(responseBody);
        if (fetchedItems != null) {
            System.out.println("Fetched " + fetchedItems.length() + " items.");
        }
        assertTrue("Error: Invalid Response. " + responseBody, fetchedItems.length() != 0);
    }
}
