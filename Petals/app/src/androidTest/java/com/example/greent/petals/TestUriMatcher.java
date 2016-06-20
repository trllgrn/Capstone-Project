/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.greent.petals;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.example.greent.petals.data.ProductDBContract;



public class TestUriMatcher extends AndroidTestCase {
    private static final String TEST_CODE = "F1-116";
    private static final String TEST_CATEGORY = "ty";

    // content://com.example.android.sunshine.app/weather"
    private static final Uri TEST_PRODUCT_DIR = ProductDBContract.ProductEntry.CONTENT_URI;
    private static final Uri TEST_PRODUCT_WITH_CODE = ProductDBContract.ProductEntry.CONTENT_URI.buildUpon().appendPath(TEST_CODE).build();
    private static final Uri TEST_PRODUCT_WITH_CATEGORY = ProductDBContract.ProductEntry.CONTENT_URI.buildUpon().appendPath(ProductDBContract.PATH_CATEGORY).appendPath(TEST_CATEGORY).build();


    public void testUriMatcher() {
        UriMatcher testMatcher = ProductContentProvider.buildUriMatcher();

        assertEquals("Error: The PRODUCT URI was matched incorrectly.",
                ProductContentProvider.PRODUCT, testMatcher.match(TEST_PRODUCT_DIR));
        assertEquals("Error: The PRODUCT WITH CODE URI was matched incorrectly.",
                ProductContentProvider.PRODUCT_WITH_CODE, testMatcher.match(TEST_PRODUCT_WITH_CODE));
        assertEquals("Error: The PRODUCT WITH CATEGORY was matched incorrectly. ",
                ProductContentProvider.PRODUCT_BY_CATEGORY, testMatcher.match(TEST_PRODUCT_WITH_CATEGORY));

    }

    public void testGetCategoryFromUri() {
        assertEquals("Error: Uri was parsed incorrectly.", TEST_CATEGORY,
                ProductContentProvider.getCategoryFromUri(TEST_PRODUCT_WITH_CATEGORY));

    }

    public void testGetCodeFromUri() {
        assertEquals("Error: Uri was parsed incorrectly.", TEST_CODE,
                ProductContentProvider.getCodeFromUri(TEST_PRODUCT_WITH_CODE));
    }
}
