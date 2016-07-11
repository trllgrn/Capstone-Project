package com.example.greent.petals.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.greent.petals.R;
import com.example.greent.petals.data.AnalyticsApplication;
import com.example.greent.petals.data.FlowerProduct;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";

    //Analytics Setup
    Tracker mTracker;

    FlowerProduct mSelectedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setup Analytics
        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();


        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendToCheckout();
//                Snackbar.make(view, "Item Added to Cart!", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        //Get the Bundle passed in as a parameter
        Bundle args = getIntent().getExtras();
        if (args != null) {
            mSelectedProduct = args.getParcelable(getString(R.string.detail_product_key));
        }

        if (mSelectedProduct != null) {
            //Set up the content
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(mSelectedProduct.name);


            ImageView detailProductImage = (ImageView) findViewById(R.id.detail_product_imageview);
            Picasso.with(this).load(mSelectedProduct.img_url_lg).into(detailProductImage);

            TextView detailProductDescription = (TextView) findViewById(R.id.detail_product_descrption);
            detailProductDescription.setText(mSelectedProduct.description);

            TextView detailProductPrice = (TextView) findViewById(R.id.detail_product_price);
            detailProductPrice.setText(String.format(getString(R.string.format_price),mSelectedProduct.price));

            TextView detailProductName = (TextView) findViewById(R.id.detail_product_name);
            detailProductName.setText(mSelectedProduct.name);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Setting screen name: " + TAG);
        mTracker.setScreenName("Image~" + TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public DetailActivity() {
        super();
    }

    private void sendToCheckout() {
        //Add Item to list of products in Shared Prefs

        /*//Get the current cart items, if any
        SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_cart_items),0);
        Set<String> cart_products = prefs.getStringSet(getString(R.string.prefs_cart_items_key),
                new HashSet<String>());
        //add the current product to the set
        cart_products.add(mSelectedProduct.code);*/

        /*//now add the set back to shared prefs
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        editor.putStringSet(getString(R.string.prefs_cart_items_key),cart_products);
        editor.apply();*/

        //startActivity(new Intent(this,CartActivity.class));

        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.checkout_selected_product),mSelectedProduct);
        startActivity(new Intent(this,CheckoutActivity.class).putExtras(args));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
