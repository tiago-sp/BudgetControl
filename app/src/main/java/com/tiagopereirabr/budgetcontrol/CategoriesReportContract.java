package com.tiagopereirabr.budgetcontrol;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class CategoriesReportContract {

    static final String TABLE_NAME = "vwCategoriesReport";

    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String CATEGORY_NAME = CategoriesContract.Columns.CATEGORY_NAME;
        public static final String TOTAL_VALUE = ExpensesContract.Columns.TOTAL;
        public static final String CATEGORY_ICON = CategoriesContract.Columns.CATEGORY_IMAGE;
        public static final String EXPENSE_YEAR = "YEAR";
        public static final String EXPENSE_MONTH = "MONTH";
        public static final String DATE = ExpensesContract.Columns.DATE;

        private Columns() {
            // private constructor to prevent instantiation
        }
    }

    public static final Uri CONTENT_URI = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);
    public static final Uri CONTENT_URI_YEAR = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME + "_Year");
    public static final Uri CONTENT_URI_ACUM = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME + "_Year");

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AppProvider.CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AppProvider.CONTENT_AUTHORITY + "." + TABLE_NAME;

    public static Uri buildCategoryReportUri(long expenseID) {
        return ContentUris.withAppendedId(CONTENT_URI, expenseID);
    }

    public static long getCategoryReportID(Uri uri) {
        return ContentUris.parseId(uri);
    }
}
