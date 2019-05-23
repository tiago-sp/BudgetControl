package com.tiagopereirabr.budgetcontrol;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class BudgetContract {

    static final String TABLE_NAME = "Budget";

    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String TOTAL = "Total";
        public static final String USER_ID = "User_ID";
        public static final String CATEGORY_NAME = "Category_Name";

        private Columns() {
            // private constructor to prevent instantiation
        }
    }

    public static final Uri CONTENT_URI = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AppProvider.CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AppProvider.CONTENT_AUTHORITY + "." + TABLE_NAME;

    public static Uri buildBudgetUri(long budgetID) {
        return ContentUris.withAppendedId(CONTENT_URI, budgetID);
    }

    public static long getBudgetID(Uri uri) {
        return ContentUris.parseId(uri);
    }
}

