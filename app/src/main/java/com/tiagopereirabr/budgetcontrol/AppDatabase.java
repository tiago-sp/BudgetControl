package com.tiagopereirabr.budgetcontrol;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class AppDatabase extends SQLiteOpenHelper {
    private static final String TAG = "AppDatabase";

    public static final String DATABASE_NAME = "BudgetControl.db";
    public static final int DATABASE_VERSION = 8;

    //Implement AppDatabase as a Singleton
    private static AppDatabase instance = null;

    public AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Get an instance of the app's singleton database helper object
     *
     * @param context the content providers context.
     * @return a SQLite database helper object.
     */
    static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new AppDatabase(context);
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sSql; //Use a string variable to facilitate logging

        // Create all the initial tables
        // Expenses
        sSql = "CREATE TABLE " + ExpensesContract.TABLE_NAME +" (" + ExpensesContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + ExpensesContract.Columns.STORE_ID + " INTEGER NOT NULL, "
                + ExpensesContract.Columns.TOTAL + " REAL NOT NULL, "
                + ExpensesContract.Columns.DATE + " INTEGER NOT NULL, "
                + ExpensesContract.Columns.USER_ID + " INTERGER NOT NULL, "
                + ExpensesContract.Columns.CATEGORY_ID + " INTEGER NOT NULL);";
        db.execSQL(sSql);

        // Categories
        sSql = "CREATE TABLE " + CategoriesContract.TABLE_NAME +" (" + CategoriesContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + CategoriesContract.Columns.CATEGORY_NAME + " TEXT NOT NULL, "
                + CategoriesContract.Columns.CATEGORY_IMAGE + " INTEGER, "
                + CategoriesContract.Columns.CATEGORY_COLOR + " TEXT);";
        db.execSQL(sSql);

        // Stores
        sSql = "CREATE TABLE " + StoresContract.TABLE_NAME +" (" + StoresContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + StoresContract.Columns.NAME + " TEXT NOT NULL, "
                + StoresContract.Columns.CATEGORY_ID + " INTEGER NOT NULL);";
        db.execSQL(sSql);

        // Users
        sSql = "CREATE TABLE " + UsersContract.TABLE_NAME +" (" + UsersContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + UsersContract.Columns.NAME + " TEXT NOT NULL, "
                + UsersContract.Columns.INITIAL_DATE + " INTEGER NOT NULL, "
                + UsersContract.Columns.CAN_INPUT_EXPSENSES + " INTEGER NOT NULL, "
                + UsersContract.Columns.EMAIL + " TEXT, "
                + UsersContract.Columns.AVATAR + " INTEGER);";
        db.execSQL(sSql);

        // Extras Expenses
        sSql = "CREATE TABLE " + ExtrasContract.TABLE_NAME +" (" + ExtrasContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + ExtrasContract.Columns.EXPENSE_ID + " INTEGER NOT NULL, "
                + ExtrasContract.Columns.TOTAL + " REAL NOT NULL);";
        db.execSQL(sSql);

        addStoreReportView(db);
        addCategoryReportView(db);
        addCorrectExpensesReportView(db);
        addBudget(db);
        addCorrectCategoryReport(db);
        correctBudget(db);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        switch (oldVersion) {
            case 1:
                addStoreReportView(db);
            case 2:
                addCategoryReportView(db);
            case 3:
                addExpensesReportView(db);
            case 4:
                dropViewExpensesReport(db);
                addCorrectExpensesReportView(db);
            case 5:
                addBudget(db);
            case 6:
                addCorrectCategoryReport(db);
            case 7:
                correctBudget(db);
                break;

            default:
                throw new IllegalStateException("onUpgrade() with unknown newVersion: " + newVersion);
        }
    }

    public List<User> readUser(String searchTerm){

        List<User> userList = new ArrayList<User>();
        String sql = "";
        sql += "SELECT * FROM " + UsersContract.TABLE_NAME;
        sql += " WHERE " + UsersContract.Columns.NAME + " LIKE '%" + searchTerm + "%'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql,null);

        if(cursor.moveToFirst()) {
            do{
                String name = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.NAME));
                User user = new User(0l,name,0l,0,null,0l);

                userList.add(user);
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return userList;
    }

    public List<Store> readStore(String searchTerm){

        List<Store> storeList = new ArrayList<Store>();
        String sql = "";
        sql += "SELECT * FROM " + StoresContract.TABLE_NAME;
        sql += " WHERE " + StoresContract.Columns.NAME + " LIKE '%" + searchTerm + "%'";
        //sql += " AND " + StoresContract.Columns.CATEGORY_ID + " = " + catId;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql,null);

        if(cursor.moveToFirst()) {
            do{
                String name = cursor.getString(cursor.getColumnIndex(StoresContract.Columns.NAME));
                Store store = new Store(name,null);

                storeList.add(store);
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return storeList;
    }

    public int getUserId(String userName){

        String sql ="";
        sql += "SELECT " + UsersContract.Columns._ID + " FROM " + UsersContract.TABLE_NAME;
        sql += " WHERE " + UsersContract.Columns.NAME + " LIKE '" + userName + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql,null);

        if(cursor.moveToFirst()) {
            int userID = cursor.getInt((int) cursor.getColumnIndex(UsersContract.Columns._ID));

            cursor.close();
            db.close();

            return userID;
        } else {
            cursor.close();
            db.close();

            return 0;
        }

    }



    public int getStoreId(String storeName){

        String sql ="";
        sql += "SELECT " + StoresContract.Columns._ID + " FROM " + StoresContract.TABLE_NAME;
        sql += " WHERE " + StoresContract.Columns.NAME + " LIKE '" + storeName + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql,null);

        if(cursor.moveToFirst()) {
            int storeID = cursor.getInt((int) cursor.getColumnIndex(StoresContract.Columns._ID));

            cursor.close();
            db.close();

            return storeID;
        } else {
            cursor.close();
            db.close();

            return 0;
        }

    }

    private void addStoreReportView (SQLiteDatabase db){

        String sSQL = "CREATE VIEW " + StoresReportContract.TABLE_NAME + " AS "
                + "SELECT " + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns._ID + ", "
                + StoresContract.TABLE_NAME + "." + StoresContract.Columns.NAME + ", "
                + "sum(" + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.TOTAL + ") AS " + ExpensesContract.Columns.TOTAL + ", "
                + "strftime('%Y',DATE(" +ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.DATE + "/1000, 'unixepoch')) AS " + StoresReportContract.Columns.EXPENSE_YEAR + ", "
                + "strftime('%m',DATE(" +ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.DATE + "/1000, 'unixepoch')) AS " + StoresReportContract.Columns.EXPENSE_MONTH + ", "
                + CategoriesContract.TABLE_NAME + "." + CategoriesContract.Columns.CATEGORY_IMAGE
                + " FROM " + ExpensesContract.TABLE_NAME
                + " JOIN " + StoresContract.TABLE_NAME + " ON " + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.STORE_ID + " = " + StoresContract.TABLE_NAME + "." + StoresContract.Columns._ID
                + " JOIN " + CategoriesContract.TABLE_NAME + " ON " + StoresContract.TABLE_NAME + "." + StoresContract.Columns.CATEGORY_ID + " = " + CategoriesContract.TABLE_NAME + "." + CategoriesContract.Columns._ID
                + " GROUP BY " + StoresContract.TABLE_NAME + "." + StoresContract.Columns.NAME + ", " + StoresReportContract.Columns.EXPENSE_YEAR + ", " +  StoresReportContract.Columns.EXPENSE_MONTH + ";";

        db.execSQL(sSQL);

        sSQL = "CREATE VIEW " + StoresReportContract.TABLE_NAME + "_Year" + " AS "
                + "SELECT " + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns._ID + ", "
                + StoresContract.TABLE_NAME + "." + StoresContract.Columns.NAME + ", "
                + "sum(" + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.TOTAL + ") AS " + ExpensesContract.Columns.TOTAL + ", "
                + "strftime('%Y',DATE(" +ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.DATE + "/1000, 'unixepoch')) AS " + StoresReportContract.Columns.EXPENSE_YEAR + ", "
                + CategoriesContract.TABLE_NAME + "." + CategoriesContract.Columns.CATEGORY_IMAGE
                + " FROM " + ExpensesContract.TABLE_NAME
                + " JOIN " + StoresContract.TABLE_NAME + " ON " + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.STORE_ID + " = " + StoresContract.TABLE_NAME + "." + StoresContract.Columns._ID
                + " JOIN " + CategoriesContract.TABLE_NAME + " ON " + StoresContract.TABLE_NAME + "." + StoresContract.Columns.CATEGORY_ID + " = " + CategoriesContract.TABLE_NAME + "." + CategoriesContract.Columns._ID
                + " GROUP BY " + StoresContract.TABLE_NAME + "." + StoresContract.Columns.NAME + ", " + StoresReportContract.Columns.EXPENSE_YEAR + ";";

        db.execSQL(sSQL);


    }

    private void addCategoryReportView (SQLiteDatabase db){

        String sSQL = "CREATE VIEW " + CategoriesReportContract.TABLE_NAME + " AS "
                + "SELECT " + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns._ID + ", "
                + CategoriesContract.TABLE_NAME + "." + CategoriesContract.Columns.CATEGORY_NAME + ", "
                + "sum(" + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.TOTAL + ") AS " + ExpensesContract.Columns.TOTAL + ", "
                + "strftime('%Y',DATE(" +ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.DATE + "/1000, 'unixepoch')) AS " + CategoriesReportContract.Columns.EXPENSE_YEAR + ", "
                + "strftime('%m',DATE(" +ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.DATE + "/1000, 'unixepoch')) AS " + CategoriesReportContract.Columns.EXPENSE_MONTH + ", "
                + CategoriesContract.TABLE_NAME + "." + CategoriesContract.Columns.CATEGORY_IMAGE
                + " FROM " + ExpensesContract.TABLE_NAME
                + " JOIN " + CategoriesContract.TABLE_NAME + " ON " + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.CATEGORY_ID + " = " + CategoriesContract.TABLE_NAME + "." + CategoriesContract.Columns._ID
                + " GROUP BY " + CategoriesContract.TABLE_NAME + "." + CategoriesContract.Columns.CATEGORY_NAME + ", " + CategoriesReportContract.Columns.EXPENSE_YEAR + ", " +  CategoriesReportContract.Columns.EXPENSE_MONTH + ";";

        db.execSQL(sSQL);

        sSQL = "CREATE VIEW " + CategoriesReportContract.TABLE_NAME + "_Year" + " AS "
                + "SELECT " + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns._ID + ", "
                + CategoriesContract.TABLE_NAME + "." + CategoriesContract.Columns.CATEGORY_NAME + ", "
                + "sum(" + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.TOTAL + ") AS " + ExpensesContract.Columns.TOTAL + ", "
                + "strftime('%Y',DATE(" +ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.DATE + "/1000, 'unixepoch')) AS " + CategoriesReportContract.Columns.EXPENSE_YEAR + ", "
                + CategoriesContract.TABLE_NAME + "." + CategoriesContract.Columns.CATEGORY_IMAGE
                + " FROM " + ExpensesContract.TABLE_NAME
                + " JOIN " + CategoriesContract.TABLE_NAME + " ON " + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.CATEGORY_ID + " = " + CategoriesContract.TABLE_NAME + "." + CategoriesContract.Columns._ID
                + " GROUP BY " + CategoriesContract.TABLE_NAME + "." + CategoriesContract.Columns.CATEGORY_NAME + ", " + CategoriesReportContract.Columns.EXPENSE_YEAR + ";";

        db.execSQL(sSQL);


    }

    private void addExpensesReportView (SQLiteDatabase db){

        String sSQL = "CREATE VIEW " + ExpensesReportContract.TABLE_NAME + " AS "
                + "SELECT " + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns._ID + ", "
                + StoresContract.TABLE_NAME + "." + StoresContract.Columns.NAME + ", "
                + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.TOTAL + ", "
                + "strftime('%Y',DATE(" +ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.DATE + "/1000, 'unixepoch')) AS " + ExpensesReportContract.Columns.EXPENSE_YEAR + ", "
                + "strftime('%m',DATE(" +ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.DATE + "/1000, 'unixepoch')) AS " + ExpensesReportContract.Columns.EXPENSE_MONTH + ", "
                + CategoriesContract.TABLE_NAME + "." + CategoriesContract.Columns.CATEGORY_IMAGE + ", "
                + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.DATE +" AS " + ExpensesReportContract.Columns.DATE_INMILIS + ", "
                + "DATE(" + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.DATE + "/1000, 'unixepoch') AS " + ExpensesReportContract.Columns.DATE
                + " FROM " + ExpensesContract.TABLE_NAME
                + " JOIN " + StoresContract.TABLE_NAME + " ON " + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.STORE_ID + " = " + StoresContract.TABLE_NAME + "." + StoresContract.Columns._ID
                + " JOIN " + CategoriesContract.TABLE_NAME + " ON " + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.CATEGORY_ID + " = " + CategoriesContract.TABLE_NAME + "." + CategoriesContract.Columns._ID
                + " ORDER BY " + ExpensesReportContract.Columns.DATE + " DESC;";

        db.execSQL(sSQL);
    }

    private void dropViewExpensesReport(SQLiteDatabase db){
        String sSQL = "DROP VIEW " + ExpensesReportContract.TABLE_NAME + ";";
        db.execSQL(sSQL);
    }

    private void addCorrectExpensesReportView (SQLiteDatabase db){

        String sSQL = "CREATE VIEW " + ExpensesReportContract.TABLE_NAME + " AS "
                + "SELECT " + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns._ID + ", "
                + StoresContract.TABLE_NAME + "." + StoresContract.Columns.NAME + ", "
                + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.TOTAL + ", "
                + "strftime('%Y',DATE(" +ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.DATE + "/1000, 'unixepoch')) AS " + ExpensesReportContract.Columns.EXPENSE_YEAR + ", "
                + "strftime('%m',DATE(" +ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.DATE + "/1000, 'unixepoch')) AS " + ExpensesReportContract.Columns.EXPENSE_MONTH + ", "
                + CategoriesContract.TABLE_NAME + "." + CategoriesContract.Columns.CATEGORY_IMAGE + ", "
                + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.DATE +" AS " + ExpensesReportContract.Columns.DATE_INMILIS + ", "
                + "DATE(" + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.DATE + "/1000, 'unixepoch') AS " + ExpensesReportContract.Columns.DATE
                + " FROM " + ExpensesContract.TABLE_NAME
                + " JOIN " + StoresContract.TABLE_NAME + " ON " + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.STORE_ID + " = " + StoresContract.TABLE_NAME + "." + StoresContract.Columns._ID
                + " JOIN " + CategoriesContract.TABLE_NAME + " ON " + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.CATEGORY_ID + " = " + CategoriesContract.TABLE_NAME + "." + CategoriesContract.Columns._ID
                + " ORDER BY " + ExpensesContract.Columns.DATE + " DESC;";

        db.execSQL(sSQL);
    }

    private void addBudget (SQLiteDatabase db){
        String sSQL = "CREATE TABLE " + BudgetContract.TABLE_NAME + "( " + BudgetContract.Columns._ID +" INTEGER PRIMARY KEY NOT NULL,"
                + BudgetContract.Columns.USER_ID + " INTEGER NOT NULL,"
                + BudgetContract.Columns.TOTAL + " REAL NOT NULL);";

        db.execSQL(sSQL);
    }

    private void addCorrectCategoryReport (SQLiteDatabase db){
        String sDrop = "DROP VIEW " + CategoriesReportContract.TABLE_NAME + ";";
        db.execSQL(sDrop);


        String sSQL = "CREATE VIEW " + CategoriesReportContract.TABLE_NAME + " AS "
                + "SELECT " + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns._ID + ", "
                + CategoriesContract.TABLE_NAME + "." + CategoriesContract.Columns.CATEGORY_NAME + ", "
                + "sum(" + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.TOTAL + ") AS " + ExpensesContract.Columns.TOTAL + ", "
                + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.DATE + ", "
                + "strftime('%Y',DATE(" +ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.DATE + "/1000, 'unixepoch')) AS " + CategoriesReportContract.Columns.EXPENSE_YEAR + ", "
                + "strftime('%m',DATE(" +ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.DATE + "/1000, 'unixepoch')) AS " + CategoriesReportContract.Columns.EXPENSE_MONTH + ", "
                + CategoriesContract.TABLE_NAME + "." + CategoriesContract.Columns.CATEGORY_IMAGE
                + " FROM " + ExpensesContract.TABLE_NAME
                + " JOIN " + CategoriesContract.TABLE_NAME + " ON " + ExpensesContract.TABLE_NAME + "." + ExpensesContract.Columns.CATEGORY_ID + " = " + CategoriesContract.TABLE_NAME + "." + CategoriesContract.Columns._ID
                + " GROUP BY " + CategoriesContract.TABLE_NAME + "." + CategoriesContract.Columns.CATEGORY_NAME + ", " + CategoriesReportContract.Columns.EXPENSE_YEAR + ", " +  CategoriesReportContract.Columns.EXPENSE_MONTH + ";";

        db.execSQL(sSQL);
    }

    private void correctBudget (SQLiteDatabase db){
        String sDrop = "DROP TABLE " + BudgetContract.TABLE_NAME + ";";
        db.execSQL(sDrop);

        String sSQL = "CREATE TABLE " + BudgetContract.TABLE_NAME + "( " + BudgetContract.Columns._ID +" INTEGER PRIMARY KEY NOT NULL, "
                + BudgetContract.Columns.CATEGORY_NAME + " TEXT NOT NULL, "
                + BudgetContract.Columns.USER_ID + " INTEGER NOT NULL, "
                + BudgetContract.Columns.TOTAL + " REAL NOT NULL);";

        db.execSQL(sSQL);
    }
}
