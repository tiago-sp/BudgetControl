package com.tiagopereirabr.budgetcontrol;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class UsersContract {

    static final String TABLE_NAME = "Users";

    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String NAME = "Name";
        public static final String EMAIL = "Email";
        public static final String INITIAL_DATE = "Initial_Date";
        public static final String CAN_INPUT_EXPSENSES = "Can_Input_Expenses";
        public static final String AVATAR = "Avatar";

        private Columns() {
            // private constructor to prevent instantiation
        }
    }

    public static final Uri CONTENT_URI = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AppProvider.CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AppProvider.CONTENT_AUTHORITY + "." + TABLE_NAME;

    public static Uri buildUserUri(long expenseID) {
        return ContentUris.withAppendedId(CONTENT_URI, expenseID);
    }

    public static long getUserID(Uri uri) {
        return ContentUris.parseId(uri);
    }
}
