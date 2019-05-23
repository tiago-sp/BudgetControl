package com.tiagopereirabr.budgetcontrol;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class AppProvider extends ContentProvider {
    private static final String TAG = "AppProvider";

    private AppDatabase mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();


    static final String CONTENT_AUTHORITY = "com.tiagopereirabr.budgetcontrol.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final int EXPENSES = 100;
    private static final int EXPENSES_ID = 101;

    private static final int CATEGORY = 200;
    private static final int CATEGORY_ID = 201;

    private static final int STORES = 300;
    private static final int STORES_ID = 301;

    private static final int USERS = 400;
    private static final int USERS_ID = 401;

    private static final int EXTRAS = 500;
    private static final int EXTRAS_ID = 501;

    private static final int STORES_REPORT = 600;
    private static final int STORES_REPORT_ID = 601;
    private static final int STORES_REPORT_YEAR = 602;

    private static final int CATEGORIES_REPORT = 700;
    private static final int CATEGORIES_REPORT_ID = 701;
    private static final int CATEGORIES_REPORT_YEAR = 702;
    private static final int CATEGORIES_REPORT_ACUM = 703;

    private static final int EXPENSES_REPORT = 800;
    private static final int EXPENSES_REPORT_ID = 801;

    private static final int BUDGET = 900;


    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(CONTENT_AUTHORITY, ExpensesContract.TABLE_NAME, EXPENSES);
        matcher.addURI(CONTENT_AUTHORITY, ExpensesContract.TABLE_NAME + "/#", EXPENSES_ID);

        matcher.addURI(CONTENT_AUTHORITY, CategoriesContract.TABLE_NAME, CATEGORY);
        matcher.addURI(CONTENT_AUTHORITY, CategoriesContract.TABLE_NAME + "/#", CATEGORY_ID);

        matcher.addURI(CONTENT_AUTHORITY, StoresContract.TABLE_NAME, STORES);
        matcher.addURI(CONTENT_AUTHORITY, StoresContract.TABLE_NAME + "/#", STORES_ID);

        matcher.addURI(CONTENT_AUTHORITY, UsersContract.TABLE_NAME, USERS);
        matcher.addURI(CONTENT_AUTHORITY, UsersContract.TABLE_NAME + "/#", USERS_ID);

        matcher.addURI(CONTENT_AUTHORITY, ExtrasContract.TABLE_NAME, EXTRAS);
        matcher.addURI(CONTENT_AUTHORITY, ExtrasContract.TABLE_NAME + "/#", EXTRAS_ID);

        matcher.addURI(CONTENT_AUTHORITY,StoresReportContract.TABLE_NAME, STORES_REPORT);
        matcher.addURI(CONTENT_AUTHORITY, StoresReportContract.TABLE_NAME + "/#", STORES_REPORT_ID);
        matcher.addURI(CONTENT_AUTHORITY,StoresReportContract.TABLE_NAME+"_Year", STORES_REPORT_YEAR);

        matcher.addURI(CONTENT_AUTHORITY,CategoriesReportContract.TABLE_NAME, CATEGORIES_REPORT);
        matcher.addURI(CONTENT_AUTHORITY, CategoriesReportContract.TABLE_NAME + "/#", CATEGORIES_REPORT_ID);
        matcher.addURI(CONTENT_AUTHORITY,CategoriesReportContract.TABLE_NAME+"_Year", CATEGORIES_REPORT_YEAR);
        matcher.addURI(CONTENT_AUTHORITY,CategoriesReportContract.TABLE_NAME+"_Year", CATEGORIES_REPORT_ACUM);

        matcher.addURI(CONTENT_AUTHORITY,ExpensesReportContract.TABLE_NAME, EXPENSES_REPORT);
        matcher.addURI(CONTENT_AUTHORITY, ExpensesReportContract.TABLE_NAME + "/#", EXPENSES_REPORT_ID);

        matcher.addURI(CONTENT_AUTHORITY,"Total_Spent", 1000);
        matcher.addURI(CONTENT_AUTHORITY,"BUDGET_INFO", 1001);
        matcher.addURI(CONTENT_AUTHORITY,"TOTAL_EXPENSES_MONTH_YEAR",1002);


        matcher.addURI(CONTENT_AUTHORITY, BudgetContract.TABLE_NAME, BUDGET);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = AppDatabase.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query: called with URI " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "query: match is " + match);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String groupBy = null;

        switch (match) {
            case EXPENSES:
                queryBuilder.setTables(ExpensesContract.TABLE_NAME);
                break;

            case EXPENSES_ID:
                queryBuilder.setTables(ExpensesContract.TABLE_NAME);
                long expenseID = ExpensesContract.getExpenseID(uri);
                queryBuilder.appendWhere(ExpensesContract.Columns._ID + " = " + expenseID);
                break;

            case CATEGORY:
                queryBuilder.setTables(CategoriesContract.TABLE_NAME);
                break;

            case CATEGORY_ID:
                queryBuilder.setTables(CategoriesContract.TABLE_NAME);
                long catID = CategoriesContract.getCategoryID(uri);
                queryBuilder.appendWhere(CategoriesContract.Columns._ID + " = " + catID);
                break;

            case STORES:
                queryBuilder.setTables(StoresContract.TABLE_NAME);
                break;

            case STORES_ID:
                queryBuilder.setTables(StoresContract.TABLE_NAME);
                long storeID = StoresContract.getStoreID(uri);
                queryBuilder.appendWhere(StoresContract.Columns._ID + " = " + storeID);
                break;

            case USERS:
                queryBuilder.setTables(UsersContract.TABLE_NAME);
                break;

            case USERS_ID:
                queryBuilder.setTables(UsersContract.TABLE_NAME);
                long userID = UsersContract.getUserID(uri);
                queryBuilder.appendWhere(UsersContract.Columns._ID + " = " + userID);
                break;

            case EXTRAS:
                queryBuilder.setTables(ExtrasContract.TABLE_NAME);
                break;

            case EXTRAS_ID:
                queryBuilder.setTables(ExtrasContract.TABLE_NAME);
                long extraID = ExtrasContract.getExtraID(uri);
                queryBuilder.appendWhere(ExtrasContract.Columns._ID + " = " + extraID);
                break;

            case STORES_REPORT:
                queryBuilder.setTables(StoresReportContract.TABLE_NAME);
                break;

            case STORES_REPORT_ID:
                queryBuilder.setTables(StoresReportContract.TABLE_NAME);
                long storeReportID = StoresReportContract.getStoreReportID(uri);
                queryBuilder.appendWhere(StoresReportContract.Columns._ID + " = " + storeReportID);
                break;

            case STORES_REPORT_YEAR:
                queryBuilder.setTables(StoresReportContract.TABLE_NAME+"_Year");
                break;

            case CATEGORIES_REPORT:
                queryBuilder.setTables(CategoriesReportContract.TABLE_NAME);
                break;

            case CATEGORIES_REPORT_ID:
                queryBuilder.setTables(CategoriesReportContract.TABLE_NAME);
                long catReportID = CategoriesReportContract.getCategoryReportID(uri);
                queryBuilder.appendWhere(CategoriesReportContract.Columns._ID + " = " + catReportID);
                break;

            case CATEGORIES_REPORT_YEAR:
                queryBuilder.setTables(CategoriesReportContract.TABLE_NAME+"_Year");
                break;

            case CATEGORIES_REPORT_ACUM:
                queryBuilder.setTables(CategoriesReportContract.TABLE_NAME+"_Year");
                groupBy = CategoriesReportContract.Columns.CATEGORY_NAME;
                break;

            case EXPENSES_REPORT:
                queryBuilder.setTables(ExpensesReportContract.TABLE_NAME);
                break;

            case EXPENSES_REPORT_ID:
                queryBuilder.setTables(ExpensesReportContract.TABLE_NAME);
                long expenseReportID = ExpensesReportContract.getExpenseReportID(uri);
                queryBuilder.appendWhere(ExpensesReportContract.Columns._ID + " = " + expenseReportID);
                break;

            case 1000:
                queryBuilder.setTables(CategoriesReportContract.TABLE_NAME);
                break;

            case 1001:
                queryBuilder.setTables(CategoriesReportContract.TABLE_NAME);
                groupBy = CategoriesReportContract.Columns.CATEGORY_NAME;
                break;

            case 1002:
                queryBuilder.setTables(CategoriesReportContract.TABLE_NAME);
                groupBy = CategoriesReportContract.Columns.EXPENSE_YEAR + " , " + CategoriesReportContract.Columns.EXPENSE_MONTH;
                break;

            case BUDGET:
                queryBuilder.setTables(BudgetContract.TABLE_NAME);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Log.d(TAG, "query: " + queryBuilder.buildQuery(projection,selection,selectionArgs,groupBy,null,sortOrder,null));

        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, groupBy, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        groupBy = null;
        return cursor;
    }


    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case EXPENSES:
                return ExpensesContract.CONTENT_TYPE;

            case EXPENSES_ID:
                return ExpensesContract.CONTENT_ITEM_TYPE;

            case CATEGORY:
                return CategoriesContract.CONTENT_TYPE;

            case CATEGORY_ID:
                return CategoriesContract.CONTENT_ITEM_TYPE;

            case STORES:
                return StoresContract.CONTENT_TYPE;

            case STORES_ID:
                return StoresContract.CONTENT_ITEM_TYPE;

            case USERS:
                return UsersContract.CONTENT_TYPE;

            case USERS_ID:
                return UsersContract.CONTENT_ITEM_TYPE;

            case EXTRAS:
                return ExtrasContract.CONTENT_TYPE;

            case EXTRAS_ID:
                return ExtrasContract.CONTENT_ITEM_TYPE;

            case STORES_REPORT:
                return StoresReportContract.CONTENT_TYPE;

            case STORES_REPORT_ID:
                return StoresReportContract.CONTENT_ITEM_TYPE;

            case CATEGORIES_REPORT:
                return CategoriesReportContract.CONTENT_TYPE;

            case CATEGORIES_REPORT_ID:
                return CategoriesReportContract.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);

        final SQLiteDatabase db;

        Uri returnUri = null;
        long recordId;

        switch (match) {
            case EXPENSES:
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(ExpensesContract.TABLE_NAME, null, values);
                if (recordId >= 0) {
                    returnUri = ExpensesContract.buildExpenseUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;

            case CATEGORY:
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(CategoriesContract.TABLE_NAME, null, values);
                if (recordId >= 0) {
                    returnUri = CategoriesContract.buildCategoryUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;

            case STORES:
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(StoresContract.TABLE_NAME, null, values);
                if (recordId >= 0) {
                    returnUri = StoresContract.buildStoreUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;

            case USERS:
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(UsersContract.TABLE_NAME, null, values);
                if (recordId >= 0) {
                    returnUri = UsersContract.buildUserUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;

            case EXTRAS:
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(ExtrasContract.TABLE_NAME, null, values);
                if (recordId >= 0) {
                    returnUri = ExtrasContract.buildExtraUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;

            case BUDGET:
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(BudgetContract.TABLE_NAME, null, values);
                if (recordId >= 0) {
                    returnUri = BudgetContract.buildBudgetUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        if (recordId >= 0) {
            //something was inserted
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch (match) {
            case EXPENSES:
                db = mOpenHelper.getWritableDatabase();
                count = db.delete(ExpensesContract.TABLE_NAME, selection, selectionArgs);
                break;

            case EXPENSES_ID:
                db = mOpenHelper.getWritableDatabase();
                long expenseId = ExpensesContract.getExpenseID(uri);
                selectionCriteria = ExpensesContract.Columns._ID + " = " + expenseId;

                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(ExpensesContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

            case CATEGORY:
                db = mOpenHelper.getWritableDatabase();
                count = db.delete(CategoriesContract.TABLE_NAME, selection, selectionArgs);
                break;

            case CATEGORY_ID:
                db = mOpenHelper.getWritableDatabase();
                long catId = CategoriesContract.getCategoryID(uri);
                selectionCriteria = CategoriesContract.Columns._ID + " = " + catId;

                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(CategoriesContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

            case STORES:
                db = mOpenHelper.getWritableDatabase();
                count = db.delete(StoresContract.TABLE_NAME, selection, selectionArgs);
                break;

            case STORES_ID:
                db = mOpenHelper.getWritableDatabase();
                long storeId = StoresContract.getStoreID(uri);
                selectionCriteria = StoresContract.Columns._ID + " = " + storeId;

                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(StoresContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

            case USERS:
                db = mOpenHelper.getWritableDatabase();
                count = db.delete(UsersContract.TABLE_NAME, selection, selectionArgs);
                break;

            case USERS_ID:
                db = mOpenHelper.getWritableDatabase();
                long userId = UsersContract.getUserID(uri);
                selectionCriteria = UsersContract.Columns._ID + " = " + userId;

                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(UsersContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

            case EXTRAS:
                db = mOpenHelper.getWritableDatabase();
                count = db.delete(ExtrasContract.TABLE_NAME, selection, selectionArgs);
                break;

            case EXTRAS_ID:
                db = mOpenHelper.getWritableDatabase();
                long extraId = ExtrasContract.getExtraID(uri);
                selectionCriteria = ExtrasContract.Columns._ID + " = " + extraId;

                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(ExtrasContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        if (count >= 0) {
            //something was inserted
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch (match) {
            case EXPENSES:
                db = mOpenHelper.getWritableDatabase();
                count = db.update(ExpensesContract.TABLE_NAME, values, selection, selectionArgs);
                Log.d(TAG, "update: count is " + count);
                Log.d(TAG, "update: selection is " + selection);
                Log.d(TAG, "update: selectionsArgs is " + selectionArgs[0]);
                break;

            case EXPENSES_ID:
                db = mOpenHelper.getWritableDatabase();
                long expenseId = ExpensesContract.getExpenseID(uri);
                selectionCriteria = ExpensesContract.Columns._ID + " = " + expenseId;

                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(ExpensesContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;

            case CATEGORY:
                db = mOpenHelper.getWritableDatabase();
                count = db.update(CategoriesContract.TABLE_NAME, values, selection, selectionArgs);
                break;

            case CATEGORY_ID:
                db = mOpenHelper.getWritableDatabase();
                long catId = CategoriesContract.getCategoryID(uri);
                selectionCriteria = CategoriesContract.Columns._ID + " = " + catId;

                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(CategoriesContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;

            case STORES:
                db = mOpenHelper.getWritableDatabase();
                count = db.update(StoresContract.TABLE_NAME, values, selection, selectionArgs);
                break;

            case STORES_ID:
                db = mOpenHelper.getWritableDatabase();
                long storeId = StoresContract.getStoreID(uri);
                selectionCriteria = StoresContract.Columns._ID + " = " + storeId;

                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(StoresContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;

            case USERS:
                db = mOpenHelper.getWritableDatabase();
                count = db.update(UsersContract.TABLE_NAME, values, selection, selectionArgs);
                break;

            case USERS_ID:
                db = mOpenHelper.getWritableDatabase();
                long userId = UsersContract.getUserID(uri);
                selectionCriteria = UsersContract.Columns._ID + " = " + userId;

                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(UsersContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;

            case EXTRAS:
                db = mOpenHelper.getWritableDatabase();
                count = db.update(ExtrasContract.TABLE_NAME, values, selection, selectionArgs);
                break;

            case EXTRAS_ID:
                db = mOpenHelper.getWritableDatabase();
                long extraId = ExtrasContract.getExtraID(uri);
                selectionCriteria = ExtrasContract.Columns._ID + " = " + extraId;

                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(ExtrasContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;

            case BUDGET:
                db = mOpenHelper.getWritableDatabase();
                count = db.update(BudgetContract.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        if (count >= 0) {
            //something was inserted
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

}
