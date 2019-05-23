package com.tiagopereirabr.budgetcontrol;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import java.security.InvalidParameterException;
import java.text.NumberFormat;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryReportFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemSelectedListener {
    private static final String TAG = "CategoryReportFragment";

    private static final int LOADER_ID = 1;
    private static final String SELECTED_YEAR = "Selected_Year";
    private static final String SELECTED_MONTH = "Selected_Month";
    private static final String YEAR = "YEAR";
    private static final String MONTH = "MONTH";
    private static final String YEARS = "YEARS";
    private static final String MONTHS = "MONTHS";

    private Bundle mArgs = new Bundle();

    private static String[] months;
    private static String[] years;

    private CategoryReportRVAdapter mAdapter;
    private SimpleSpinnerAdapter mMonthAdapter;
    private SimpleSpinnerAdapter mYearAdapter;

    private String mMonth = null;
    private String mYear = null;

    public CategoryReportFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mYear = savedInstanceState.getString(SELECTED_YEAR);
            mMonth = savedInstanceState.getString(SELECTED_MONTH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category_report, container, false);

        if (savedInstanceState != null) {
            mYear = savedInstanceState.getString(SELECTED_YEAR);
            mMonth = savedInstanceState.getString(SELECTED_MONTH);
        }

        if(getArguments() != null){
            mYear = getArguments().getString(YEAR);
            mMonth = getArguments().getString(MONTH);
            months = getArguments().getStringArray(MONTHS);
            years = getArguments().getStringArray(YEARS);
        }

//        if(mYear == null){
//            mYear = String.format("%4d",Calendar.getInstance().get(Calendar.YEAR));
//        }
//
//        if(mMonth == null){
//            mMonth = String.format("%02d", Calendar.getInstance().get(Calendar.MONTH)+1);
//        }

        Spinner monthSpinner = view.findViewById(R.id.cr_sp_month);
        Spinner yearSpinner = view.findViewById(R.id.cr_sp_year);

        mMonthAdapter = new SimpleSpinnerAdapter(getContext(),months);
        mYearAdapter = new SimpleSpinnerAdapter(getContext(),years);

        monthSpinner.setAdapter(mMonthAdapter);
        yearSpinner.setAdapter(mYearAdapter);

        int selectedMonth = 0;
        for(int i=0;i < months.length; i++){
            if(months[i].equalsIgnoreCase(mMonth)){
                selectedMonth = i;
            }
        }
        monthSpinner.setSelection(selectedMonth);

        int selectedYear = 0;
        for(int i=0;i < years.length; i++){
            if(years[i].equalsIgnoreCase(mYear)){
                selectedYear = i;
            }
        }
        yearSpinner.setSelection(selectedYear);

        monthSpinner.setOnItemSelectedListener(this);
        yearSpinner.setOnItemSelectedListener(this);


        RecyclerView recyclerView = view.findViewById(R.id.cr_rv_expenseslist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if(mAdapter == null) {
            mAdapter = new CategoryReportRVAdapter(null,getContext());
        }
        recyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(LOADER_ID, mArgs, this);

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SELECTED_YEAR,mYear);
        outState.putString(SELECTED_MONTH,mMonth);
        Log.d(TAG, "onSaveInstanceState: called");

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            mYear = savedInstanceState.getString(SELECTED_YEAR);
            mMonth = savedInstanceState.getString(SELECTED_MONTH);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()){
            case R.id.cr_sp_month:
                mMonth = months[position];
                getArguments().putString(MONTH,mMonth);
                getTotal(mYear,mMonth);
                break;

            case R.id.cr_sp_year:
                mYear = years[position];
                getArguments().putString(YEAR,mYear);
                getTotal(mYear,mMonth);
                break;
        }

        getLoaderManager().restartLoader(LOADER_ID,mArgs,this);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case LOADER_ID:
                if(mMonth.equalsIgnoreCase("Acum.") && mYear.equalsIgnoreCase("Acum.")) {
                    String[] projection = {BaseColumns._ID,
                            CategoriesReportContract.Columns.CATEGORY_NAME,
                            "SUM (" + CategoriesReportContract.Columns.TOTAL_VALUE + ") AS " + CategoriesReportContract.Columns.TOTAL_VALUE,
                            CategoriesReportContract.Columns.CATEGORY_ICON};

                    String selection = null;
                    String[] selectionArgs = null;
                    String sortOrder = CategoriesReportContract.Columns.TOTAL_VALUE + " DESC";
                    return new CursorLoader(getContext(),
                            CategoriesReportContract.CONTENT_URI_ACUM,
                            projection,
                            selection ,
                            selectionArgs,
                            sortOrder);

                }else if(mMonth.equalsIgnoreCase("Acum.")){
                    String[] projection = {BaseColumns._ID,
                            CategoriesReportContract.Columns.CATEGORY_NAME,
                            CategoriesReportContract.Columns.TOTAL_VALUE, // + ") AS " + StoresReportContract.Columns.TOTAL_VALUE,
                            CategoriesReportContract.Columns.EXPENSE_YEAR,
                            CategoriesReportContract.Columns.CATEGORY_ICON};

                    String selection = "YEAR = ?";
                    String[] selectionArgs = {mYear};
                    String sortOrder = CategoriesReportContract.Columns.TOTAL_VALUE + " DESC";
                    return new CursorLoader(getContext(),
                            CategoriesReportContract.CONTENT_URI_YEAR,
                            projection,
                            selection,
                            selectionArgs,
                            sortOrder);

                }else {
                    String[] projection = {BaseColumns._ID,
                            CategoriesReportContract.Columns.CATEGORY_NAME,
                            CategoriesReportContract.Columns.TOTAL_VALUE, // + ") AS " + StoresReportContract.Columns.TOTAL_VALUE,
                            CategoriesReportContract.Columns.EXPENSE_YEAR,
                            CategoriesReportContract.Columns.EXPENSE_MONTH,
                            CategoriesReportContract.Columns.CATEGORY_ICON};

                    String selection = "YEAR = ? AND MONTH = ?";
                    String[] selectionArgs = {mYear,mMonth};
                    String sortOrder = CategoriesReportContract.Columns.TOTAL_VALUE + " DESC";

                    return new CursorLoader(getContext(),
                            CategoriesReportContract.CONTENT_URI,
                            projection,
                            selection,
                            selectionArgs,
                            sortOrder);
                }


            default:
                throw new InvalidParameterException("onCreateLoader called with invalid loader_id " + id);
        }

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        mAdapter.swapCursor(cursor);


    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        mAdapter.swapCursor(null);

    }

    public double getTotal(String year, String month) {

        String[] projections = {"Sum( " + CategoriesReportContract.Columns.TOTAL_VALUE + ") AS TOTAL"};
        String[] selectionsArgs = null;

        Uri uri = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, "Total_Spent");
        String selection = null;
        if(mMonth.equalsIgnoreCase("Acum.") && (mYear.equalsIgnoreCase("Acum."))){

        }else if(mMonth.equalsIgnoreCase("Acum.")){
            selection = "YEAR = ?";
            selectionsArgs = new String[] {year};
        } else {
            selection = "YEAR = ? AND MONTH = ?";
            selectionsArgs = new String[] {year, month};
        }

        Cursor cursor = getActivity().getContentResolver().query(uri, projections, selection, selectionsArgs, null);
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

        TextView totalMonth = getActivity().findViewById(R.id.cr_tv_total);
        totalMonth.setText(currencyFormatter.format(total));

        return total;
    }
}
