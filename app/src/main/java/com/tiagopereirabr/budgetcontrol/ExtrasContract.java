package com.tiagopereirabr.budgetcontrol;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class ExtrasContract {

    static final String TABLE_NAME = "Expenses_Extras";

    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String EXPENSE_ID = "Expense_ID";
        public static final String TOTAL = "Total";

        private Columns() {
            // private constructor to prevent instantiation
        }
    }

    public static final Uri CONTENT_URI = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AppProvider.CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AppProvider.CONTENT_AUTHORITY + "." + TABLE_NAME;

    public static Uri buildExtraUri(long expenseID) {
        return ContentUris.withAppendedId(CONTENT_URI, expenseID);
    }

    public static long getExtraID(Uri uri) {
        return ContentUris.parseId(uri);
    }
}
