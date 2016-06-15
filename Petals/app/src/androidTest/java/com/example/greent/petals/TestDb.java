package com.example.greent.petals;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.greent.petals.data.ProductDBHelper;

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
    }

}
