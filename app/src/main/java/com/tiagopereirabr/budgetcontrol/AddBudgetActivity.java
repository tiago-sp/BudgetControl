package com.tiagopereirabr.budgetcontrol;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class AddBudgetActivity extends AppCompatActivity {
    private static final String TAG = "AddBudgetActivity";

    boolean editingBudget = false;
    Category mCategory = null;
    User mUser = null;
    AppDatabase dbHelper = new AppDatabase(this);
    Double average;
    Double maximum;
    Double minimum;
    Double mBudget = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Intent intent = getIntent();
        mCategory = (Category) intent.getSerializableExtra(Category.class.getSimpleName());
        mUser = (User) intent.getSerializableExtra(User.class.getSimpleName());

        checkIfThereIsABudget();
        getValuesForBudget();

        ImageView catIcon = findViewById(R.id.ab_iv_catIcon);
        TextView catName = findViewById(R.id.ab_tv_catName);
        TextView ave = findViewById(R.id.ab_tv_ave);
        TextView max = findViewById(R.id.ab_tv_max);
        TextView min = findViewById(R.id.ab_tv_min);
        final EditText total = findViewById(R.id.ab_et_total);
        Button addButton = findViewById(R.id.ab_bt_save);

        if(mBudget != null){
            total.setText(mBudget.toString());
            addButton.setText("SAVE");
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentResolver contentResolver = getContentResolver();
                ContentValues values = new ContentValues();

                if ((total.length() != 0) && (!editingBudget) ) {
                    values.put(BudgetContract.Columns._ID, mCategory.getId());
                    values.put(BudgetContract.Columns.USER_ID, mUser.getId());
                    values.put(BudgetContract.Columns.TOTAL, Double.parseDouble(total.getText().toString()));
                    values.put(BudgetContract.Columns.CATEGORY_NAME,mCategory.getName());
                    contentResolver.insert(BudgetContract.CONTENT_URI, values);

                    finish();
                } else if(editingBudget){
                    values.put(BudgetContract.Columns._ID, mCategory.getId());
                    values.put(BudgetContract.Columns.USER_ID, mUser.getId());
                    values.put(BudgetContract.Columns.TOTAL, Double.parseDouble(total.getText().toString()));
                    values.put(BudgetContract.Columns.CATEGORY_NAME,mCategory.getName());
                    String selection = BudgetContract.Columns._ID + " = ?";
                    String[] selectionArgs = {((Long) mCategory.getId()).toString()};
                    contentResolver.update(BudgetContract.CONTENT_URI, values,selection,selectionArgs);

                    setResult(Activity.RESULT_OK,intent);
                    finish();
                } else {
                    Toast.makeText(AddBudgetActivity.this, "It is necessary to set a budget bigger than $ 00.00", Toast.LENGTH_LONG).show();
                }
            }
        });

        catIcon.setImageResource((int) mCategory.getCatImage());
        catName.setText(mCategory.getName());

        ave.setText(String.format("%1$, .2f", average));
        max.setText(String.format("%1$, .2f", maximum));
        min.setText(String.format("%1$, .2f", minimum));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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




    public void getValuesForBudget() {
        Log.d(TAG, "getValuesForBudget: entrou aqui");
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor;

        String[] projection = {
                "AVG( " + CategoriesReportContract.Columns.TOTAL_VALUE + ") AS AVERAGE",
                "MAX( " + CategoriesReportContract.Columns.TOTAL_VALUE + ") AS MAX",
                "MIN( " + CategoriesReportContract.Columns.TOTAL_VALUE + ") AS MIN"
        };

        String selection = CategoriesReportContract.Columns.CATEGORY_NAME + " = ? AND " + CategoriesReportContract.Columns.DATE + " <= ?";
        Long filterMonth = (System.currentTimeMillis() / 1000 - (60 * 60 * 24 * Calendar.getInstance().get(Calendar.DAY_OF_MONTH))) * 1000;
        String[] selectionArgs = {mCategory.getName(), filterMonth.toString()};
        String sortOrder = null;

        Uri uri = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, "BUDGET_INFO");

        cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);

        if (cursor.moveToFirst()) {
            average = cursor.getDouble(cursor.getColumnIndex("AVERAGE"));
            maximum = cursor.getDouble(cursor.getColumnIndex("MAX"));
            minimum = cursor.getDouble(cursor.getColumnIndex("MIN"));
            cursor.close();
        } else {
            cursor.close();
        }

        Log.d(TAG, "getValuesForBudget: ave, max e min = " + average + " , " + maximum + " , " + minimum);

    }

    public void checkIfThereIsABudget () {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor;

        String[] projection = {
                BudgetContract.Columns.TOTAL
        };

        String selection = BudgetContract.Columns._ID + " = ?";
        String[] selectionArgs = {((Long) mCategory.getId()).toString()};
        String sortOrder = null;

        cursor = contentResolver.query(BudgetContract.CONTENT_URI, projection, selection, selectionArgs, sortOrder);

        if (cursor.moveToFirst()) {
            mBudget = cursor.getDouble(cursor.getColumnIndex(BudgetContract.Columns.TOTAL));
            editingBudget = true;
            cursor.close();
        } else {
            cursor.close();
        }

    }
}
