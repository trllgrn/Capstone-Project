package com.example.greent.petals;

import com.example.greent.petals.data.FetchedProductModel;

import org.json.JSONArray;
import org.junit.Test;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import static org.junit.Assert.assertTrue;

/**
 * Created by greent on 6/21/16.
 */

public class ApiUnitTest {
    //Setup the API service

    public interface FlowerShopService {
        @GET("products/{category}")
        Call<List<FetchedProductModel>> getProductsByCategory(@Path("category") String category);
    }

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

    /*@Test
    public void testRetroFitCall() throws Exception {
        final String API_BASE_URL = "https://blumanana.herokuapp.com";

        //Create Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .build();

        FlowerShopService service = retrofit.create(FlowerShopService.class);

        Call<List<FetchedProductModel>> call = service.getProductsByCategory("ty");

        Response<List<FetchedProductModel>> response = call.execute();
    }*/

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
        assertTrue("Error: Invalid Response. " + responseBody, fetchedItems.length() == 0);
    }

}
