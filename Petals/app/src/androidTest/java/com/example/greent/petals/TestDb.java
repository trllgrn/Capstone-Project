package com.example.greent.petals;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.greent.petals.data.ProductDBContract;
import com.example.greent.petals.data.ProductDBHelper;

import java.util.HashSet;

/**
 * Created by greent on 6/15/16.
 */

public class TestDb extends AndroidTestCase {
    void deleteTheDatabase() {
        mContext.deleteDatabase(ProductDBHelper.DATABASE_NAME);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(ProductDBHelper.DATABASE_NAME);
        SQLiteDatabase db = new ProductDBHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        //put the table names in a collection
        HashSet<String> tableNames = new HashSet<>();
        do {
            tableNames.add(c.getString(0));
        } while(c.moveToNext());

        assertTrue("Error: The product table wasn't created.",
                tableNames.contains(ProductDBContract.ProductEntry.TABLE_NAME));
        assertTrue("Error: The category table wasn't created.",
                tableNames.contains(ProductDBContract.CategoryEntry.TABLE_NAME));

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + ProductDBContract.ProductEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        //Create a set of the column entries
        int columnNameIndex = c.getColumnIndex("name");
        HashSet<String> colummsSet = new HashSet<>();

        //put the column names in the collection
        do {
            colummsSet.add(c.getString(columnNameIndex));
        } while(c.moveToNext());

        //Verify Product code
        assertTrue("Error: ", colummsSet.contains(ProductDBContract.ProductEntry.COLUMN_NAME_CODE));
        //Verify Product description
        assertTrue("Error: ", colummsSet.contains(ProductDBContract.ProductEntry.COLUMN_NAME_DESC));
        //Verify Product dimension
        assertTrue("Error: ", colummsSet.contains(ProductDBContract.ProductEntry.COLUMN_NAME_DIMENSION));
        //Verify Product name
        assertTrue("Error: ", colummsSet.contains(ProductDBContract.ProductEntry.COLUMN_NAME_NAME));
        //Verify Product occasion
        assertTrue("Error: ", colummsSet.contains(ProductDBContract.ProductEntry.COLUMN_NAME_OCCASION));
        //Verify Product image location large
        assertTrue("Error: ", colummsSet.contains(ProductDBContract.ProductEntry.COLUMN_NAME_IMG_LOC_LRG));
        //Verify Product image location small
        assertTrue("Error: ", colummsSet.contains(ProductDBContract.ProductEntry.COLUMN_NAME_IMG_LOC_SML));
        //Verify Product image location short
        assertTrue("Error: ", colummsSet.contains(ProductDBContract.ProductEntry.COLUMN_NAME_CODE));
        //Verify Product price
        assertTrue("Error: ", colummsSet.contains(ProductDBContract.ProductEntry.COLUMN_NAME_PRICE));


        //Verify correct creation of category table
        c.close();
        db.close();
    }

    public void testProductTable() {
        //Approach:
        //Step 1: Create test values to insert
        SQLiteDatabase db = new ProductDBHelper(mContext).getWritableDatabase();

        ContentValues productContent = new ContentValues();
        productContent.put(ProductDBContract.ProductEntry.COLUMN_NAME_CODE,"T34-1A");
        productContent.put(ProductDBContract.ProductEntry.COLUMN_NAME_DESC,"This bright and sunny bouquet is as sweet as a fairy tale ending. Perfect for baby showers, birthdays and beyond. Beautiful light blue hydrangea, cheerful yellow gerberas and white daisy spray chrysanthemums are delivered in a light blue Satin Cylinder that comes complete with a pretty light blue ribbon. Blue skies ahead!");
        productContent.put(ProductDBContract.ProductEntry.COLUMN_NAME_IMG_LOC_LRG,"https://www.floristone.com/flowers/products/T34-1A_d1.jpg");
        productContent.put(ProductDBContract.ProductEntry.COLUMN_NAME_IMG_LOC_SML,"https://www.floristone.com/flowers/thumbnails/T34-1A_t1.jpg");
        productContent.put(ProductDBContract.ProductEntry.COLUMN_NAME_PRICE,69.95);
        productContent.put(ProductDBContract.ProductEntry.COLUMN_NAME_NAME,"Teleflora's Once Upon a Daisy");
        productContent.put(ProductDBContract.ProductEntry.COLUMN_NAME_DIMENSION,"14\" h x 13 1/2\" w");
        productContent.put(ProductDBContract.ProductEntry.COLUMN_NAME_TYPE,"C");
        productContent.put(ProductDBContract.ProductEntry.COLUMN_NAME_OCCASION,"ty");

        //Step 2: Insert the the test values into the table
        long insertedRow = db.insert(ProductDBContract.ProductEntry.TABLE_NAME,null,productContent);
        assertTrue("Error: Could not insert values into the DB.",insertedRow != -1);


        //Step 3: Try to read the values back from the table.
        Cursor fetchCursor = db.query(ProductDBContract.ProductEntry.TABLE_NAME,null,null,null,null,null,null);

        if (fetchCursor.moveToFirst()) {
            //check the column name
            String fetchedCode = fetchCursor.getString(fetchCursor.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_CODE));
            assertTrue("Error: product Code was not verified.", fetchedCode.equals(productContent.get(ProductDBContract.ProductEntry.COLUMN_NAME_CODE)));

            String fetchedDesc = fetchCursor.getString(fetchCursor.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_DESC));
            assertTrue("Error: product Description was not verified.", fetchedDesc.equals(productContent.get(ProductDBContract.ProductEntry.COLUMN_NAME_DESC)));

            String fetchedImgLg = fetchCursor.getString(fetchCursor.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_IMG_LOC_LRG));
            assertTrue("Error: product Image Large was not verified.", fetchedImgLg.equals(productContent.get(ProductDBContract.ProductEntry.COLUMN_NAME_IMG_LOC_LRG)));

            String fetchedImgSm = fetchCursor.getString(fetchCursor.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_IMG_LOC_SML));
            assertTrue("Error: product Image Small was not verified.", fetchedImgSm.equals(productContent.get(ProductDBContract.ProductEntry.COLUMN_NAME_IMG_LOC_SML)));

            String fetchedName = fetchCursor.getString(fetchCursor.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_NAME));
            assertTrue("Error: product Name was not verified.", fetchedName.equals(productContent.get(ProductDBContract.ProductEntry.COLUMN_NAME_NAME)));

            Double fetchedPrice = fetchCursor.getDouble(fetchCursor.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_PRICE));
            assertTrue("Error: product Price was not verified.", fetchedPrice.equals(productContent.get(ProductDBContract.ProductEntry.COLUMN_NAME_PRICE)));

            String fetchedCategory = fetchCursor.getString(fetchCursor.getColumnIndex(ProductDBContract.ProductEntry.COLUMN_NAME_OCCASION));
            assertTrue("Error: product Name was not verified.", fetchedCategory.equals(productContent.get(ProductDBContract.ProductEntry.COLUMN_NAME_OCCASION)));

            //check the values
            fetchCursor.close();
        }

        //Step 4: Delete the record from the table

        db.close();
    }

}
