package com.example.greent.petals.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by greent on 6/15/16.
 */

public class ProductDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "petals.db";

    //CREATE PRODUCT COMMAND
    final String CREATE_PRODUCT_TABLE =
            "CREATE TABLE " + ProductDBContract.ProductEntry.TABLE_NAME + " (" +
                    ProductDBContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ProductDBContract.ProductEntry.COLUMN_NAME_CODE + " TEXT NOT NULL, " +
                    ProductDBContract.ProductEntry.COLUMN_NAME_DESC + " TEXT, " +
                    ProductDBContract.ProductEntry.COLUMN_NAME_DIMENSION + " TEXT, " +
                    ProductDBContract.ProductEntry.COLUMN_NAME_NAME + " TEXT, " +
                    ProductDBContract.ProductEntry.COLUMN_NAME_TYPE + " TEXT, " +
                    ProductDBContract.ProductEntry.COLUMN_NAME_OCCASION + " TEXT, " +
                    ProductDBContract.ProductEntry.COLUMN_NAME_IMG_LOC_SML + " TEXT, " +
                    ProductDBContract.ProductEntry.COLUMN_NAME_IMG_LOC_LRG + " TEXT, " +
                    ProductDBContract.ProductEntry.COLUMN_NAME_PRICE + " FLOAT, " +

                    "UNIQUE (" + ProductDBContract.ProductEntry.COLUMN_NAME_CODE + ", " +
                                 ProductDBContract.ProductEntry.COLUMN_NAME_OCCASION + ")" +
                    ");";

    final String CREATE_CATEGORY_TABLE =
            "CREATE TABLE " + ProductDBContract.CategoryEntry.TABLE_NAME + " (" +
                    ProductDBContract.CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ProductDBContract.CategoryEntry.COLUMN_NAME_CODE + " TEXT NOT NULL, " +
                    ProductDBContract.CategoryEntry.COLUMN_NAME_NAME + " TEXT NOT NULL" +
                    ");";


    @Override
    public void onCreate(SQLiteDatabase db) {
        //create the product table
        db.execSQL(CREATE_PRODUCT_TABLE);
        db.execSQL(CREATE_CATEGORY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ProductDBContract.ProductEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ProductDBContract.CategoryEntry.TABLE_NAME);
        onCreate(db);
    }

    public ProductDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
