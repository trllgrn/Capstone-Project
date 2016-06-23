package com.example.greent.petals.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by greent on 6/21/16.
 */

public class FlowerAPIResponse {
    List<FetchedProductModel> products;

    //public constructor for collection
    public FlowerAPIResponse() {
        products = new ArrayList<FetchedProductModel>();
    }

    public static FlowerAPIResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        FlowerAPIResponse flowerAPIResponse = gson.fromJson(response, FlowerAPIResponse.class);
        return flowerAPIResponse;
    }
}
