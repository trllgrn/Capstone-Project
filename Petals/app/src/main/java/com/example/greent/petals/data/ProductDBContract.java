package com.example.greent.petals.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by greent on 6/15/16.
 */

public final class ProductDBContract {

    //Provide general static strings for Uris
    public static final String CONTENT_AUTHORITY = "com.example.greent.petals";

    //content authority base
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //content paths
    public static final String PATH_PRODUCT = "product";
    public static final String PATH_CODE = "code";
    public static final String PATH_CATEGORY = "category";

    public ProductDBContract() {

    }

    //Table: product
    //Product field declarations from model code
//    def attributes
//    {'code' => nil,
//            'description' => nil,
//            'large' => nil,
//            'small' => nil,
//            'price' => nil,
//            'name' => nil,
//            'dimension' => nil,
//            'product_type' => nil,
//            'occasions' => nil
//    }
//    end
    public static final class ProductEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

        public static final String TABLE_NAME = "product";
        public static final String COLUMN_NAME_CODE = "code";
        public static final String COLUMN_NAME_DESC = "description";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DIMENSION = "dimension";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_OCCASION = "occasion";
        public static final String COLUMN_NAME_IMG_LOC_LRG = "img_location_lg";
        public static final String COLUMN_NAME_IMG_LOC_SML = "img_location_sml";
        public static final String COLUMN_NAME_PRICE = "price";
    }

    public static final class CategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "category";
        public static final String COLUMN_NAME_CODE = "category_code";
        public static final String COLUMN_NAME_NAME = "category_name";

    }
}
