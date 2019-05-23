package com.tiagopereirabr.budgetcontrol;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.GregorianCalendar;
import java.util.List;

public class AddExpensesActicity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, UserAutoCompleteListener.OnTextChangedListener {
    private static final String TAG = "AddExpensesActicity";
    String[] users = new String[]{"...Reading from db..."};
    String[] stores = new String[]{"...Retrive from db..."};
    AppDatabase dbHelper = new AppDatabase(this);
    ArrayAdapter<String> mAdapterUser;
    ArrayAdapter<String> mAdapterStore;
    AutoCompleteTextView userName;
    AutoCompleteTextView storeName;
    Store mStore;
    Category mCategory;
    User mUser;
    Expense mExpense;
    EditText mTotal;
    EditText mExtra;
    TextView mDate;
    private final GregorianCalendar mCalendar = new GregorianCalendar();
    public static final int DIALOG_DATE_SELECT = 1;
    java.text.DateFormat mDateFormat;

    boolean editMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expenses_acticity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDateFormat = DateFormat.getDateFormat(this);
        String currentDate = mDateFormat.format(mCalendar.getTimeInMillis());

        final Intent intent = getIntent();
        mUser = (User) intent.getSerializableExtra(User.class.getSimpleName());
        long expenseID = intent.getLongExtra(ExpensesContract.Columns._ID,-1);
        Log.d(TAG, "onCreate: expense ID equals " + expenseID);
        if( expenseID >= 0) {
            getExpense(expenseID);
            mCalendar.setTimeInMillis(mExpense.getDate());
            currentDate = mDateFormat.format(mCalendar.getTimeInMillis());
            editMode = true;
        }else{
            mCategory = (Category) intent.getSerializableExtra(Category.class.getSimpleName());
        }

        TextView catName = (TextView) findViewById(R.id.ae_tv_catName);
        catName.setText(mCategory.getName());

        ImageView catIcon = (ImageView) findViewById(R.id.ae_iv_catIcon);
        catIcon.setImageResource((int) mCategory.getCatImage());

        userName = (AutoCompleteTextView) findViewById(R.id.ae_act_user_name);
        storeName = (AutoCompleteTextView) findViewById(R.id.ae_act_store_name);

        userName.setText(mUser.getName());
        userName.addTextChangedListener(new UserAutoCompleteListener(this,this));
        storeName.addTextChangedListener(new StoreAutoCompleteListener(this));

        mAdapterUser = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, users);
        userName.setAdapter(mAdapterUser);

        mAdapterStore = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, stores);
        storeName.setAdapter(mAdapterStore);

        mTotal = (EditText) findViewById(R.id.ae_et_total);
        mExtra = (EditText) findViewById(R.id.ae_et_extra);
        mDate = (TextView) findViewById(R.id.ae_et_date);



        mDate.setText(currentDate);

        mDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDatePickerDialog("Select the date",DIALOG_DATE_SELECT);
            }
        });


        Button saveButton = (Button) findViewById(R.id.ae_bt_save);

        if(editMode){
            storeName.setText(mStore.getName());
            mTotal.setText(((Double)mExpense.getTotal()).toString());
            mDate.setText(mDateFormat.format(mExpense.getDate()));
            saveButton.setText("Save");
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentResolver contentResolver = getContentResolver();
                ContentValues values = new ContentValues();


                if (storeName.length() != 0) {

                    if(stores.length == 0){
                        Log.d(TAG, "onClick: Creating a new store first. Name  " + storeName.getText().toString());
                        ContentValues valuesStore = new ContentValues();
                        valuesStore.put(StoresContract.Columns.NAME, storeName.getText().toString());
                        valuesStore.put(StoresContract.Columns.CATEGORY_ID, mCategory.getId());
                        contentResolver.insert(StoresContract.CONTENT_URI,valuesStore);
                    }

                    values.put(ExpensesContract.Columns.STORE_ID, dbHelper.getStoreId(storeName.getText().toString()));
                    values.put(ExpensesContract.Columns.USER_ID, dbHelper.getUserId(userName.getText().toString()));
                    values.put(ExpensesContract.Columns.TOTAL, Double.parseDouble(mTotal.getText().toString()));
                    values.put(ExpensesContract.Columns.DATE, mCalendar.getTimeInMillis());
                    values.put(ExpensesContract.Columns.CATEGORY_ID, mCategory.getId());

                    if(editMode){

                        Log.d(TAG, "onClick: entrou aqui editmode");
                        String selection = ExpensesContract.Columns._ID + " = ?";
                        String selectionArgs[] = {((Long) mExpense.getId()).toString()};
                        Log.d(TAG, "onClick: expense id is " + ((Long) mExpense.getId()).toString());
                        contentResolver.update(ExpensesContract.CONTENT_URI,values,selection,selectionArgs);

                    }else {
                        contentResolver.insert(ExpensesContract.CONTENT_URI, values);
                    }
                }
                intent.putExtra(Expense.class.getSimpleName(),mExpense);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });

    }

    public boolean getExpense (Long id) {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor;

        String[] projection = {"*"};

        String selection = ExpensesContract.Columns._ID + " = ? ";
        String[] selectionArgs = {id.toString()};
        String sortOrder = null;

        cursor = contentResolver.query(ExpensesContract.CONTENT_URI, projection, selection, selectionArgs, sortOrder);

        if (cursor.moveToFirst()) {
            long storeId = cursor.getLong(cursor.getColumnIndex(ExpensesContract.Columns.STORE_ID));
            long categoryId = cursor.getLong(cursor.getColumnIndex(ExpensesContract.Columns.CATEGORY_ID));
            getCategory(categoryId);
            getStore(storeId);
            mExpense = new Expense(mStore,cursor.getDouble(cursor.getColumnIndex(ExpensesContract.Columns.TOTAL)),cursor.getLong(cursor.getColumnIndex(ExpensesContract.Columns.DATE)),
                    mUser,mCategory);
            mExpense.setId(id);
            cursor.close();
        } else {
            cursor.close();
        }


        return false;
    }

    public boolean getCategory (Long id) {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor;

        String[] projection = {"*"};

        String selection = CategoriesContract.Columns._ID + " = ? ";
        String[] selectionArgs = {id.toString()};
        String sortOrder = null;

        cursor = contentResolver.query(CategoriesContract.CONTENT_URI, projection, selection, selectionArgs, sortOrder);

        if (cursor.moveToFirst()) {
            mCategory = new Category(cursor.getLong(cursor.getColumnIndex(CategoriesContract.Columns._ID)),cursor.getString(cursor.getColumnIndex(CategoriesContract.Columns.CATEGORY_NAME)),
                    cursor.getLong(cursor.getColumnIndex(CategoriesContract.Columns.CATEGORY_IMAGE)),cursor.getString(cursor.getColumnIndex(CategoriesContract.Columns.CATEGORY_COLOR)));
            cursor.close();
        } else {
            cursor.close();
        }


        return false;
    }

    public boolean getStore (Long id) {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor;

        String[] projection = {"*"};

        String selection = StoresContract.Columns._ID + " = ? ";
        String[] selectionArgs = {id.toString()};
        String sortOrder = null;

        cursor = contentResolver.query(StoresContract.CONTENT_URI, projection, selection, selectionArgs, sortOrder);

        if (cursor.moveToFirst()) {
            mStore = new Store(cursor.getString(cursor.getColumnIndex(StoresContract.Columns.NAME)),mCategory);
            cursor.close();
        } else {
            cursor.close();
        }


        return false;
    }

    // this function is used in CustomAutoCompleteTextChangedListener.java
    public String[] getUsersFromDb(String searchTerm) {

        // add items on the array dynamically
        List<User> user = dbHelper.readUser(searchTerm);
        int rowCount = user.size();

        String[] item = new String[rowCount];
        int x = 0;

        for (User record : user) {

            item[x] = record.getName();
            x++;
        }

        return item;
    }

    public String[] getStoresFromDb(String searchTerm) {

        // add items on the array dynamically
        List<Store> store = dbHelper.readStore(searchTerm);
        int rowCount = store.size();

        String[] item = new String[rowCount];
        int x = 0;

        for (Store record : store) {

            item[x] = record.getName();
            x++;
        }

        return item;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        // check the id
        int dialogId = (int) view.getTag();
        mCalendar.set(year, month, dayOfMonth, 0, 0, 0);


        switch (dialogId) {
            case DIALOG_DATE_SELECT:
                    mDate.setText(mDateFormat.format(mCalendar.getTimeInMillis()));
//                applyFilter();
//                getSupportLoaderManager().restartLoader(LOADER_ID, mArgs, this);
                break;

            default:
                throw new IllegalArgumentException("Invalid mode when receiving DatePickerDialog result");

        }
    }

    private void showDatePickerDialog(String title, int dialogId) {
        DialogFragment dialogFragment = new DatePickerFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(DatePickerFragment.DATE_PICKER_ID, dialogId);
        arguments.putString(DatePickerFragment.DATE_PICKER_TITLE, title);
        arguments.putSerializable(DatePickerFragment.DATE_PICKER_DATE, mCalendar.getTime());

        dialogFragment.setArguments(arguments);
        dialogFragment.show(getSupportFragmentManager(), "datePicker");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                setResult(Activity.RESULT_CANCELED);
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onTextChanged(CharSequence userInput, int start, int before, int count) {

        users = getUsersFromDb(userInput.toString());

        // update the adapter
        mAdapterUser.notifyDataSetChanged();
        mAdapterUser = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,users);
        userName.setAdapter(mAdapterUser);
    }
}
