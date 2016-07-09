package com.example.greent.petals.ui;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.greent.petals.R;
import com.example.greent.petals.data.FlowerProduct;
import com.example.greent.petals.data.ProductDBContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CartActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "CartActivity";
    private ArrayList<String> mProductsToLoad;
    private ArrayList<FlowerProduct> mProductsFromDB;
    RecyclerView mLineItemList;
    LineItemAdapter mCartAdapter;
    private double mOrderTotal = 0.00;
    private String mImageURL;

    private static int mItemIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_cart);


        //Check shared Preferences to see if there is anything in our cart
        SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_cart_items),0);
        Set<String> productsFromPrefs = prefs.getStringSet(getString(R.string.prefs_cart_items_key),new HashSet<String>());
        if (productsFromPrefs.size() == 0) {
            Log.d(TAG, "onCreate: cart is empty. nothing to see here");
            setContentView(R.layout.activity_cart_empty);
        } else {
            Log.d(TAG, "onCreate: cart set contains " + productsFromPrefs.size() + " items.");
            mProductsToLoad = new ArrayList<>();
            mProductsToLoad.addAll(productsFromPrefs);
            Log.d(TAG, "onCreate: mProductsToLoad has " + mProductsToLoad.size() + " items.");

            setContentView(R.layout.activity_cart);

            //initialize the RecyclerView
            mLineItemList = (RecyclerView) findViewById(R.id.cart_product_container);

            //init RecyclerViewAdapter
            mCartAdapter = new LineItemAdapter();

            //Bind adapter to RecyclerView
            mLineItemList.setAdapter(mCartAdapter);

            LinearLayoutManager lm = new LinearLayoutManager(this);
            lm.setOrientation(LinearLayoutManager.VERTICAL);
            mLineItemList.setLayoutManager(lm);

            getSupportLoaderManager().initLoader(0,null,this);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbar.setTitle(getString(R.string.cart_activity_title));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
    }


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {

        //get the code(s) for the products to load.
        String productCode = mProductsToLoad.remove(0);

        Log.d(TAG, "onCreateLoader: fetching item: " + productCode);

        Uri uri = ProductDBContract.ProductEntry.CONTENT_URI.buildUpon().appendPath(productCode).build();

        return new CursorLoader(this, uri, null,null,null,null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        //add the fetched product to the RecyclerView adapter
        Log.d(TAG, "onLoadFinished: adding item to adapter");
        addProductFromCursor(data);

        if (!mProductsToLoad.isEmpty()) {
            getSupportLoaderManager().restartLoader(0,null,this);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        mLineItemList.setAdapter(null);
    }

    private void addProductFromCursor(Cursor data) {
        Log.d(TAG, "addProductFromCursor() called with: data = [" + data + "]");
        //check the cursor to see if it's null
        if (data != null && data.getCount() != 0) {
            data.moveToFirst();
            FlowerProduct product = new FlowerProduct();
            product.name = data.getString(data.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_NAME));
            product.code = data.getString(data.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_CODE));
            product.description = data.getString(data.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_DESC));
            product.dimension = data.getString(data.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_DIMENSION));
            product.occasion = data.getString(data.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_OCCASION));
            product.price = data.getDouble(data.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_PRICE));
            product.img_url_sml = data.getString(data.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_IMG_LOC_SML));
            product.img_url_lg = data.getString(data.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_IMG_LOC_LRG));
            product.type = data.getString(data.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_TYPE));


            //add the item to the adapter
            mCartAdapter.insert(product);
        } else {
            Log.d(TAG, "addProductFromCursor: There's no data in this Cursor");
        }
    }

    public class LineItemAdapter extends RecyclerView.Adapter<LineItemAdapter.ViewHolder> {

        //local list to hold the data
        ArrayList<FlowerProduct> mLineItems;

        public LineItemAdapter() {
            super();
            mLineItems = new ArrayList<>();
        }

        public void insert(FlowerProduct product) {
            Log.d(TAG, "insert: adding item.");
            mLineItems.add(product);
            notifyDataSetChanged();
        }

        public void remove(int position) {
            mLineItems.remove(position);

            //Delete the item from SharedPrefs
            SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_cart_items),0);
            SharedPreferences.Editor editor = prefs.edit();
            Set<String> cartItems = prefs.getStringSet(getString(R.string.prefs_cart_items_key), new HashSet<String>());
            cartItems.remove(mLineItems.get(position).code);
            editor.putStringSet(getString(R.string.prefs_cart_items_key),cartItems);
            editor.commit();
            notifyDataSetChanged();
        }



        @Override
        public LineItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item,parent,false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(LineItemAdapter.ViewHolder holder, int position) {
            FlowerProduct fp = (FlowerProduct)mLineItems.get(holder.getAdapterPosition());
            holder.productNameTxtView.setText(fp.name);
            holder.productPriceTxtView.setText(String.format(getString(R.string.format_price),fp.price));
            Picasso.with(getBaseContext()).load(fp.img_url_sml).into(holder.productThumbImgView);
        }

        @Override
        public int getItemCount() {
            return mLineItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            final ImageView productThumbImgView;
            final TextView  productNameTxtView;
            final TextView  productPriceTxtView;
            final ImageView productDeleteImgView;

            public ViewHolder(View itemView) {
                super(itemView);
                productThumbImgView = (ImageView) itemView.findViewById(R.id.cart_product_thumb);
                productNameTxtView = (TextView) itemView.findViewById(R.id.cart_product_name);
                productPriceTxtView = (TextView) itemView.findViewById(R.id.cart_product_price);
                productDeleteImgView = (ImageView) itemView.findViewById(R.id.cart_delete_product_btn);
            }
        }
    }

}
