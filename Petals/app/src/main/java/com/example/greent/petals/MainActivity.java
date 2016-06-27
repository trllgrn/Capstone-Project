package com.example.greent.petals;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.greent.petals.data.FlowerProduct;
import com.example.greent.petals.data.ProductDBContract;
import com.example.greent.petals.sync.PetalsSyncAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MainActivity";
    RecyclerView mProductList;
    ArrayList<FlowerProduct> mProductsFromDB;
    ProductsAdapter mProductsAdapter;

    int PRODUCT_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        mProductList = (RecyclerView) findViewById(R.id.main_content_container);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        //init Sync Adapter
        PetalsSyncAdapter.initializeSyncAdapter(this);

        //init Loader
        getSupportLoaderManager().initLoader(PRODUCT_LOADER,null,this);

        Log.d(TAG, "onCreate() returned: ");
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        /* Just testing to see if this works. Manually select a
        category.  Build the query Uri. Return a new CursorLoader.
        *
        * */
        Log.d(TAG, "onCreateLoader() called with: id = [" + id + "], args = [" + args + "]");
        mProductsFromDB = new ArrayList<>();

        Uri romanceRequest = ProductDBContract.ProductEntry.CONTENT_URI.buildUpon()
                .appendPath(ProductDBContract.PATH_CATEGORY).appendPath("lr").build();

        Log.d(TAG, "onCreateLoader() returned: ");

        return new CursorLoader(this,romanceRequest,null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished() called with: loader = [" + loader + "], data = [" + data + "]");
        if (data != null) {
            mProductsAdapter = new ProductsAdapter(cursorToList(data));
            mProductsAdapter.setHasStableIds(true);
            mProductList.setAdapter(mProductsAdapter);

            LinearLayoutManager lm = new LinearLayoutManager(this);
            lm.setOrientation(LinearLayoutManager.VERTICAL);
            mProductList.setLayoutManager(lm);
        }
        Log.d(TAG, "onLoadFinished() returned: ");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductList.setAdapter(null);
        Log.d(TAG, "onLoaderReset: ");
    }

    private List<FlowerProduct> cursorToList(Cursor data) {
        ArrayList<FlowerProduct> products = new ArrayList<>();
        data.moveToFirst();
        do {
            FlowerProduct flowerProduct = new FlowerProduct();
            flowerProduct.name = data.getString(data.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_NAME));
            flowerProduct.code = data.getString(data.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_CODE));
            flowerProduct.description = data.getString(data.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_DESC));
            flowerProduct.dimension = data.getString(data.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_DIMENSION));
            flowerProduct.occasion = data.getString(data.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_OCCASION));
            flowerProduct.price = data.getDouble(data.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_PRICE));
            flowerProduct.img_url_sml = data.getString(data.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_IMG_LOC_SML));
            flowerProduct.img_url_lg = data.getString(data.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_IMG_LOC_LRG));
            flowerProduct.type = data.getString(data.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_TYPE));
            products.add(flowerProduct);

        }while(data.moveToNext());

        return products;
    }

    public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {
        private List<FlowerProduct> mProductList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            final CardView card;
            final ImageView productPicImageView;
            final TextView productNameTextView;
            final TextView productPriceTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                card = (CardView) itemView.findViewById(R.id.item_cardview);
                productPicImageView = (ImageView) itemView.findViewById(R.id.item_product_image);
                productNameTextView = (TextView) itemView.findViewById(R.id.item_product_name);
                productPriceTextView = (TextView) itemView.findViewById(R.id.item_product_price);
            }
        }
        public ProductsAdapter(List<FlowerProduct> products) {
            super();
            mProductList = products;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            FlowerProduct fp = (FlowerProduct)mProductList.get(position);
            holder.productNameTextView.setText(fp.name);
            holder.productPriceTextView.setText(String.format(getString(R.string.format_price),fp.price));
        }

        @Override
        public int getItemCount() {
            return mProductList.size();
        }
    }
}
