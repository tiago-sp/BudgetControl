package com.tiagopereirabr.budgetcontrol;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements UserRVAdapter.OnUserClickListener, CategoryRVAdapter.OnCatClickListener {
    private static final String TAG = "MainActivity";

    public static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1;
    public static boolean READ_EXTERNAL_STORAGE = false;
    public static final int IMPORT_EXPENSES = 1;
    public static final int IMPORT_STORES = 2;
    public static final int IMPORT_CATEGORIES = 3;
    public static final int CHANGE_BUDGET_OR_EXPENSE = 4;
    public static final int CHANGE_EXPENSE = 5;

    private String mMonth = null;
    private String mYear = null;
    private Bundle mArgs = new Bundle();


    private BottomNavigationView mBottomNavigationView;
    private Menu mMenu;
    private final String USER_SELECTED = "User_Selected";

    private User mUser = null;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_user:
                    clearFragment();
                    userSelection();
                    return true;
                case R.id.navigation_home:
                    clearFragment();
                    catSelection();
//                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    clearFragment();
                    dashboardSelectin();
//                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
            }
            return false;
        }
    };

    private void importCsv(Intent file) {

    }

    private void checkPermission() {
        Log.d(TAG, "checkPermission: entrou aqui");
        int hasReadExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        Log.d(TAG, "checkPermission: " + hasReadExternalStorage);
        if (hasReadExternalStorage == PackageManager.PERMISSION_DENIED) {
            Log.d(TAG, "checkPermission: entrou aqui");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
            READ_EXTERNAL_STORAGE = true;
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: entrou aqui code is " + requestCode);
        if (data == null) {
            return;
        }

        String filepath;
        Uri uri;

        switch (requestCode) {
            case IMPORT_EXPENSES:

                uri = data.getData();
                filepath = PathUtils.getPath(this, uri);

                try {
                    SQLiteDatabase db = AppDatabase.getInstance(this).getWritableDatabase();
                    FileReader file = new FileReader(filepath);
                    BufferedReader buffer = new BufferedReader(file);
                    ContentValues contentValues = new ContentValues();
                    String line = "";
                    db.beginTransaction();
                    //int i =1;
                    while ((line = buffer.readLine()) != null) {
                        String[] str = line.split(",", 6); //Id, Store_ID, Total, Date_INMILLI, User_ID, Category_ID
                        int id = Integer.parseInt(str[0].toString());
                        String store = str[1].toString();
                        Double total = Double.parseDouble(str[2].toString());
                        Double date = Double.parseDouble(str[3].toString());
                        Long user = Long.parseLong(str[4].toString());
                        Long category = Long.parseLong(str[5].toString());

                        contentValues.put(ExpensesContract.Columns._ID, id);
                        contentValues.put(ExpensesContract.Columns.STORE_ID, store);
                        contentValues.put(ExpensesContract.Columns.TOTAL, total);
                        contentValues.put(ExpensesContract.Columns.DATE, date);
                        contentValues.put(ExpensesContract.Columns.USER_ID, user);
                        contentValues.put(ExpensesContract.Columns.CATEGORY_ID, category);
                        Log.d(TAG, "onActivityResult: " + contentValues);
                        //i++;
                        db.insert(ExpensesContract.TABLE_NAME, null, contentValues);
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();


                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case IMPORT_CATEGORIES:

                uri = data.getData();
                filepath = PathUtils.getPath(this, uri);

                try {
                    SQLiteDatabase db = AppDatabase.getInstance(this).getWritableDatabase();
                    FileReader file = new FileReader(filepath);
                    BufferedReader buffer = new BufferedReader(file);
                    ContentValues contentValues = new ContentValues();
                    String line = "";
                    db.beginTransaction();

                    while ((line = buffer.readLine()) != null) {
                        String[] str = line.split(",", 4); //_ID, Name, Cat_Image, Cat_Color
                        int id = Integer.parseInt(str[0].toString());
                        String name = str[1].toString();
                        Long image = Long.parseLong(str[2].toString());
                        String color = str[3].toString();

                        contentValues.put(CategoriesContract.Columns._ID, id);
                        contentValues.put(CategoriesContract.Columns.CATEGORY_NAME, name);
                        contentValues.put(CategoriesContract.Columns.CATEGORY_IMAGE, image);
                        contentValues.put(CategoriesContract.Columns.CATEGORY_COLOR, color);

                        db.insert(CategoriesContract.TABLE_NAME, null, contentValues);
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();


                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case IMPORT_STORES:

                uri = data.getData();
                filepath = PathUtils.getPath(this, uri);

                try {
                    SQLiteDatabase db = AppDatabase.getInstance(this).getWritableDatabase();
                    FileReader file = new FileReader(filepath);
                    BufferedReader buffer = new BufferedReader(file);
                    ContentValues contentValues = new ContentValues();
                    String line = "";
                    db.beginTransaction();

                    while ((line = buffer.readLine()) != null) {
                        String[] str = line.split(",", 3); //Id, Name, Cat_Id
                        int id = Integer.parseInt(str[0].toString());
                        String name = str[1].toString();
                        Long cat = Long.parseLong(str[2].toString());

                        contentValues.put(StoresContract.Columns._ID, id);
                        contentValues.put(StoresContract.Columns.NAME, name);
                        contentValues.put(StoresContract.Columns.CATEGORY_ID, cat);

                        db.insert(StoresContract.TABLE_NAME, null, contentValues);
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();


                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case CHANGE_BUDGET_OR_EXPENSE:
                CategoryFragment cf = (CategoryFragment) getSupportFragmentManager().findFragmentByTag("Category");
                Category cat = (Category) data.getSerializableExtra(Category.class.getSimpleName());
                cf.refreshList(cat);
                break;

            case CHANGE_EXPENSE:
                ExpensesReportFragment erf = (ExpensesReportFragment) getSupportFragmentManager().findFragmentByTag("expensesreport");
                Expense exp = (Expense) data.getSerializableExtra(Expense.class.getSimpleName());
                erf.refreshList(exp);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.import_files, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.mnu_imp_cat:
                checkPermission();
                if (READ_EXTERNAL_STORAGE = true) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("text/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    Intent fileintent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
                    fileintent.putExtra("CONTENT_TYPE", "text/*");
                    Intent chooserIntent;
                    chooserIntent = Intent.createChooser(fileintent, "Open file");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent});

                    try {
                        startActivityForResult(chooserIntent, IMPORT_CATEGORIES);
                    } catch (ActivityNotFoundException e) {

                    }


                }
                break;

            case R.id.mnu_imp_expenses:
                checkPermission();
                if (READ_EXTERNAL_STORAGE = true) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("text/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    Intent fileintent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
                    fileintent.putExtra("CONTENT_TYPE", "text/*");
                    Intent chooserIntent;
                    chooserIntent = Intent.createChooser(fileintent, "Open file");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent});

                    try {
                        startActivityForResult(chooserIntent, IMPORT_EXPENSES);
                    } catch (ActivityNotFoundException e) {

                    }


                }
                break;

            case R.id.mnu_imp_stores:
                checkPermission();
                if (READ_EXTERNAL_STORAGE = true) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("text/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    Intent fileintent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
                    fileintent.putExtra("CONTENT_TYPE", "text/*");
                    Intent chooserIntent;
                    chooserIntent = Intent.createChooser(fileintent, "Open file");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent});

                    try {
                        startActivityForResult(chooserIntent, IMPORT_STORES);
                    } catch (ActivityNotFoundException e) {

                    }


                }
                break;

            case R.id.mnu_exp_expenses:
                checkPermission();
                exportCSV();

        }

        return true;
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        Menu menu = navigation.getMenu();

        if (mYear == null) {
            mYear = String.format("%4d", Calendar.getInstance().get(Calendar.YEAR));
        }

        if (mMonth == null) {
            mMonth = String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1);
        }

        mArgs.putString("YEAR", mYear);
        mArgs.putString("MONTH", mMonth);

        mBottomNavigationView = navigation;
        mMenu = menu;

        if (savedInstanceState != null) {
            mUser = (User) savedInstanceState.getSerializable(USER_SELECTED);
            Log.d(TAG, "onCreate: savedInstanceState not null user is " + mUser.getName());
        } else {
            Log.d(TAG, "onCreate: savedInstanceState is null");
        }

        if (mUser != null) {
            menu.findItem(R.id.navigation_user).setTitle(mUser.getName());
        } else {
            menu.findItem(R.id.navigation_dashboard).setEnabled(false);
        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        MainFragment fragment = new MainFragment();

        Log.d(TAG, "onCreate: MainFragment created " + fragment);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.user_container, fragment)
                .commit();

        mMenu.findItem(R.id.navigation_user).setChecked(true);
        mBottomNavigationView.setSelectedItemId(R.id.navigation_user);
        getTotalMonth();
//
//        Locale currentLocale = Locale.getDefault();
//        //Currency currentCurrency = Currency.getInstance(currentLocale);
//        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(currentLocale);
//
//        TextView totalMonth = findViewById(R.id.tv_total_month);
//        totalMonth.setText(currencyFormatter.format(total));

    }

    @Override
    public void onResume() {
        super.onResume();

        getTotalMonth();

//        Locale currentLocale = Locale.getDefault();
//        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(currentLocale);
//
//        TextView totalMonth = findViewById(R.id.tv_total_month);
//        totalMonth.setText(currencyFormatter.format(total));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState: " + savedInstanceState);

        if (savedInstanceState != null) {
            mUser = (User) savedInstanceState.getSerializable(USER_SELECTED);
            if (mUser != null) {
                Log.d(TAG, "onRestoreInstanceState: " + mUser.getName());
            }
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mUser != null) {
            Log.d(TAG, "onSaveInstanceState: " + mUser.getName());
            outState.putSerializable(USER_SELECTED, mUser);
        }
    }

    @Override
    public void onUserClick(@NotNull User user) {
        Toast.makeText(this, user.getName() + " was selected.", Toast.LENGTH_SHORT).show();
        FragmentManager fragmentManager = getSupportFragmentManager();
        LoginUserFragment fragment = (LoginUserFragment) fragmentManager.findFragmentById(R.id.user_container);

        fragment.restartLoader(user);

        mUser = user;

        mMenu.findItem(R.id.navigation_user).setTitle(mUser.getName());
        mMenu.findItem(R.id.navigation_dashboard).setEnabled(true);
    }

    @Override
    public void onUserLongClick(@NotNull User user) {
        Toast.makeText(this, user.getName() + " will be edited.", Toast.LENGTH_SHORT).show();

    }

    private void userSelection() {
        LoginUserFragment fragment = new LoginUserFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.user_container, fragment)
                .commit();
    }

    private void catSelection() {
        CategoryFragment fragment = new CategoryFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.user_container, fragment, "Category")
                .commit();
    }

    private void dashboardSelectin() {
        ReportsFragment fragment = new ReportsFragment();
        fragment.setArguments(mArgs);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.user_container, fragment)
                .commit();
    }

    private boolean clearFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.user_container);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
            return true;
        } else {
            return false;
        }
    }

    public User getUserSelected() {
        return mUser;
    }

    @Override
    public void onCatClick(@NotNull Category cat) {
        //Toast.makeText(this, cat.getName() + " was selected.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, AddExpensesActicity.class);
        intent.putExtra(Category.class.getSimpleName(), cat);
        intent.putExtra(User.class.getSimpleName(), mUser);
        startActivityForResult(intent,CHANGE_BUDGET_OR_EXPENSE);


    }

    @Override
    public void onCatLongClick(@NotNull Category cat) {
        //Toast.makeText(this, cat.getName() + " will be edited.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, AddBudgetActivity.class);
        intent.putExtra(Category.class.getSimpleName(), cat);
        intent.putExtra(User.class.getSimpleName(), mUser);
        startActivityForResult(intent, CHANGE_BUDGET_OR_EXPENSE);

    }

    public double getTotalMonth() {

        String month = String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1);
        String year = String.format("%4d", Calendar.getInstance().get(Calendar.YEAR));
        String[] projections = {"Sum( " + CategoriesReportContract.Columns.TOTAL_VALUE + ") AS TOTAL"};
        String[] selectionsArgs = {year, month};

        Uri uri = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, "Total_Spent");

        Cursor cursor = getContentResolver().query(uri, projections, "YEAR = ? AND MONTH = ?", selectionsArgs, null);
        double total;

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndex("TOTAL"));
            cursor.close();
        } else {
            cursor.close();
            total = 0;
        }

        Locale currentLocale = Locale.getDefault();
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(currentLocale);

        TextView totalMonth = findViewById(R.id.tv_total_month);
        totalMonth.setText(currencyFormatter.format(total));

        return total;
    }

//    private void exportDB() {
//
//        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
//        if (!exportDir.exists())
//        {
//            exportDir.mkdirs();
//        }
//
//        File file = new File(exportDir, "despesas.csv");
//        try
//        {
//            file.createNewFile();
//            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
//            SQLiteDatabase db = dbhelper.getReadableDatabase();
//            Cursor curCSV = getContentResolver().query("SELECT * FROM contacts", null);
//            csvWrite.writeNext(curCSV.getColumnNames());
//            while(curCSV.moveToNext())
//            {
//                //Which column you want to exprort
//                String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2)};
//                csvWrite.writeNext(arrStr);
//            }
//            csvWrite.close();
//            curCSV.close();
//        }
//        catch(Exception sqlEx)
//        {
//            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
//        }
//    }

    private void exportCSV() {
        ContentResolver cr = getContentResolver();
//        SQLiteDatabase sqldb = controller.getReadableDatabase(); //My Database class
        Cursor c = null;
        int rowcount = 0;
        int colcount = 0;
        File sdCardDir = Environment.getExternalStorageDirectory();

        // Export Expenses
        try {
            String[] projection = {"*"};

            String selection = null;
            String[] selectionArgs = null;
            String sortOrder = null;

            c = cr.query(ExpensesContract.CONTENT_URI, projection, selection, selectionArgs, sortOrder);

            String filename = "MyBackUp-Expenses.csv";
            // the name of the file to export with
            File saveFile = new File(sdCardDir, filename);
            FileWriter fw = new FileWriter(saveFile);

            BufferedWriter bw = new BufferedWriter(fw);
            rowcount = c.getCount();
            colcount = c.getColumnCount();

            if (rowcount > 0) {
                c.moveToFirst();

                for (int i = 0; i < colcount; i++) {
                    if (i != colcount - 1) {

                        bw.write(c.getColumnName(i) + ",");

                    } else {

                        bw.write(c.getColumnName(i));

                    }
                }
                bw.newLine();

                for (int i = 0; i < rowcount; i++) {
                    c.moveToPosition(i);

                    for (int j = 0; j < colcount; j++) {
                        if (j != colcount - 1)
                            bw.write(c.getString(j) + ",");
                        else
                            bw.write(c.getString(j));
                    }
                    bw.newLine();
                }
                bw.close();
            }
        } catch (Exception ex) {
            Log.d(TAG, "exportCSV: error!!");
        }

        // Export Stores
        c = null;
        rowcount = 0;
        colcount = 0;

        try {
            String[] projection = {"*"};

            String selection = null;
            String[] selectionArgs = null;
            String sortOrder = null;

            c = cr.query(StoresContract.CONTENT_URI, projection, selection, selectionArgs, sortOrder);

            String filename = "MyBackUp-Stores.csv";
            // the name of the file to export with
            File saveFile = new File(sdCardDir, filename);
            FileWriter fw = new FileWriter(saveFile);

            BufferedWriter bw = new BufferedWriter(fw);
            rowcount = c.getCount();
            colcount = c.getColumnCount();

            if (rowcount > 0) {
                c.moveToFirst();

                for (int i = 0; i < colcount; i++) {
                    if (i != colcount - 1) {

                        bw.write(c.getColumnName(i) + ",");

                    } else {

                        bw.write(c.getColumnName(i));

                    }
                }
                bw.newLine();

                for (int i = 0; i < rowcount; i++) {
                    c.moveToPosition(i);

                    for (int j = 0; j < colcount; j++) {
                        if (j != colcount - 1)
                            bw.write(c.getString(j) + ",");
                        else
                            bw.write(c.getString(j));
                    }
                    bw.newLine();
                }
                bw.close();
            }
        } catch (Exception ex) {
            Log.d(TAG, "exportCSV: error!!");
        }

        // Export Categories
        c = null;
        rowcount = 0;
        colcount = 0;

        try {
            String[] projection = {"*"};

            String selection = null;
            String[] selectionArgs = null;
            String sortOrder = null;

            c = cr.query(CategoriesContract.CONTENT_URI, projection, selection, selectionArgs, sortOrder);

            String filename = "MyBackUp-Categories.csv";
            // the name of the file to export with
            File saveFile = new File(sdCardDir, filename);
            FileWriter fw = new FileWriter(saveFile);

            BufferedWriter bw = new BufferedWriter(fw);
            rowcount = c.getCount();
            colcount = c.getColumnCount();

            if (rowcount > 0) {
                c.moveToFirst();

                for (int i = 0; i < colcount; i++) {
                    if (i != colcount - 1) {

                        bw.write(c.getColumnName(i) + ",");

                    } else {

                        bw.write(c.getColumnName(i));

                    }
                }
                bw.newLine();

                for (int i = 0; i < rowcount; i++) {
                    c.moveToPosition(i);

                    for (int j = 0; j < colcount; j++) {
                        if (j != colcount - 1)
                            bw.write(c.getString(j) + ",");
                        else
                            bw.write(c.getString(j));
                    }
                    bw.newLine();
                }
                bw.close();
            }
        } catch (Exception ex) {
            Log.d(TAG, "exportCSV: error!!");
        }
    }
}


