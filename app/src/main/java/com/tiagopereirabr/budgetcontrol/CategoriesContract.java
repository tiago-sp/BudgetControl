package com.tiagopereirabr.budgetcontrol;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class CategoriesContract {

    static final String TABLE_NAME = "Categories";

    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String CATEGORY_NAME = "Name";
        public static final String CATEGORY_IMAGE = "Cat_Image";
        public static final String CATEGORY_COLOR = "Cat_Color";

        private Columns() {
            // private constructor to prevent instantiation
        }
    }

    public static final Uri CONTENT_URI = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AppProvider.CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AppProvider.CONTENT_AUTHORITY + "." + TABLE_NAME;

    public static Uri buildCategoryUri(long categoryID) {
        return ContentUris.withAppendedId(CONTENT_URI, categoryID);
    }

    public static long getCategoryID(Uri uri) {
        return ContentUris.parseId(uri);
    }
}
