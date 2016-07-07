package com.example.greent.petals.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class ProductContentProvider extends ContentProvider {

    private static final String TAG = "ProductContentProvider";

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ProductDBHelper mOpenHelper;

    public static final int PRODUCT = 100;
    public static final int PRODUCT_WITH_CODE = 101;
    public static final int PRODUCT_BY_CATEGORY = 102;

    public ProductContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ProductDBContract.CONTENT_AUTHORITY;

        //For each type of URI, match the code
        //All products
        matcher.addURI(authority,ProductDBContract.PATH_PRODUCT,PRODUCT);
        //All products in this category
        matcher.addURI(authority,ProductDBContract.PATH_PRODUCT + "/" + ProductDBContract.PATH_CATEGORY + "/*",PRODUCT_BY_CATEGORY);
        //Just the one product identified by the code
        matcher.addURI(authority,ProductDBContract.PATH_PRODUCT + "/*",PRODUCT_WITH_CODE);



        return matcher;
    }

    @Override
    public String getType(Uri uri) {
        //User the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCT_WITH_CODE:
                return ProductDBContract.ProductEntry.CONTENT_ITEM_TYPE;
            case PRODUCT:
            case PRODUCT_BY_CATEGORY:
                return ProductDBContract.ProductEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //get a match with the Uri Matcher
        int match = sUriMatcher.match(uri);

        Uri returnUri = null;

        switch (match) {
            case PRODUCT:
            {
                long entryId = mOpenHelper.getWritableDatabase().insertWithOnConflict(
                        ProductDBContract.ProductEntry.TABLE_NAME,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE
                );

                if (entryId > 0) {
                    //insert was successful
                    returnUri = uri.buildUpon().appendPath(Long.toString(entryId)).build();
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("insert: Sorry,don't recognize this URi ");

        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        Log.d(TAG, "bulkInsert() called with: uri = [" + uri + "], values = [" + values + "]");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        Uri returnedUri = insert(uri, value);
                        //extract inserted row id from returned URI
                        long _id = Long.parseLong(returnedUri.getLastPathSegment());
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new ProductDBHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor retCursor = null;
        switch (sUriMatcher.match(uri)) {
            //weather
            case PRODUCT:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ProductDBContract.ProductEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case PRODUCT_BY_CATEGORY:
            {

                //get the category from the URI
                String category = getCategoryFromUri(uri);
                //set the selection column
                String catSelection = ProductDBContract.ProductEntry.COLUMN_NAME_OCCASION + " = ? ";
                //set the selection arguments
                String[] catSelectionArgs = {category};

                retCursor = mOpenHelper.getReadableDatabase().query(
                        ProductDBContract.ProductEntry.TABLE_NAME,
                        projection,
                        catSelection,
                        catSelectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case PRODUCT_WITH_CODE:
            {
                //get the code from the URI
                String code = getCodeFromUri(uri);
                //set the selection column
                String codeSelection = ProductDBContract.ProductEntry.COLUMN_NAME_CODE + " = ? ";
                //set the selection arguments
                String[] codeSelectionArgs = {code};

                retCursor = mOpenHelper.getReadableDatabase().query(
                        ProductDBContract.ProductEntry.TABLE_NAME,
                        projection,
                        codeSelection,
                        codeSelectionArgs,
                        null,
                        null,
                        sortOrder
                );

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);


        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;

    }

    public static String getCategoryFromUri(Uri uri) {
        //Assuming the string is the following:
        //content://AUTHORITY/product/category/CAT

        String extractedCategory = null;
        if (uri != null) {
            extractedCategory = uri.getLastPathSegment();
        }
        return extractedCategory;
    }

    public static String getCodeFromUri(Uri uri) {
        //Assuming the string is the following:
        //content://AUTHORITY/product/CODE

        String extractedCode = null;
        if (uri != null){
            extractedCode = uri.getLastPathSegment();
        }
        return extractedCode;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
