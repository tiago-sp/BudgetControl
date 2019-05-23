package com.tiagopereirabr.budgetcontrol;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

public class AddCategoryActivity extends AppCompatActivity {

    private int cat_icons[] = {R.drawable.cat_delivery_truck,R.drawable.cat_gamepad,R.drawable.cat_giftbox,R.drawable.cat_house,R.drawable.cat_library,R.drawable.cat_networking,R.drawable.cat_shop,R.drawable.cat_shopping_cart,R.drawable.cat_wallet};
    private EditText mCatName;
    private EditText mCatColor;
    private CategoryIcon mCategoryIcon;
    private CategoryIconAdapter mCategoryIconAdapter;

    private Bundle mArgs = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Spinner spinnerCatIcon = findViewById(R.id.ac_cat_icon_spinner);

        mCategoryIconAdapter = new CategoryIconAdapter(this, cat_icons);
        spinnerCatIcon.setAdapter(mCategoryIconAdapter);

        spinnerCatIcon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCategoryIcon = new CategoryIcon(cat_icons[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mCatName = findViewById(R.id.ac_et_cat_name);
        mCatColor = findViewById(R.id.ac_et_cat_color);
        ImageView saveButton = findViewById(R.id.ac_btn_save);

        final Category cat;

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentResolver contentResolver = getContentResolver();
                ContentValues values = new ContentValues();

                if(mCatName.length() != 0) {
                    values.put(CategoriesContract.Columns.CATEGORY_NAME,mCatName.getText().toString());
                    if(mCatColor.length() == 0){
                        values.put(CategoriesContract.Columns.CATEGORY_COLOR,"#FFFFFF");
                    } else {
                        values.put(CategoriesContract.Columns.CATEGORY_COLOR, mCatColor.getText().toString());
                    }
                    values.put(CategoriesContract.Columns.CATEGORY_IMAGE,mCategoryIcon.getCatIcon());
                    contentResolver.insert(CategoriesContract.CONTENT_URI, values);
                }

                finish();
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                onBackPressed();
                break;
        }
        return true;
    }

}
