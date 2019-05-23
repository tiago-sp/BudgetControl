package com.tiagopereirabr.budgetcontrol;


import android.database.Cursor;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.security.InvalidParameterException;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class StoreReportFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemSelectedListener {
    private static final String TAG = "StoreReportFragment";

    private static final int LOADER_ID = 1;
    private static final String SELECTION_PARAM = "SELECTION";
    private static final String SELECTION_ARGS_PARAM = "SELECTION_ARGS";
    private static final String SORT_ORDER_PARAM = "SORT_ORDER";
    private static final String YEAR = "YEAR";
    private static final String MONTH = "MONTH";
    private static final String YEARS = "YEARS";
    private static final String MONTHS = "MONTHS";

    private Bundle mArgs = new Bundle();

    private static String[] months;
    private static String[] years;

    private StoreReportRVAdapter mAdapter;
    private SimpleSpinnerAdapter mMonthAdapter;
    private SimpleSpinnerAdapter mYearAdapter;

    private String mMonth = String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1);
    private String mYear = String.format("%4d", Calendar.getInstance().get(Calendar.YEAR));

    public StoreReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_store_report, container, false);

        Spinner monthSpinner = view.findViewById(R.id.sr_sp_month);
        Spinner yearSpinner = view.findViewById(R.id.sr_sp_year);

        if(getArguments() != null){
            mYear = getArguments().getString(YEAR);
            mMonth = getArguments().getString(MONTH);
            months = getArguments().getStringArray(MONTHS);
            years = getArguments().getStringArray(YEARS);
        }


        mMonthAdapter = new SimpleSpinnerAdapter(getContext(), months);
        mYearAdapter = new SimpleSpinnerAdapter(getContext(), years);

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


        RecyclerView recyclerView = view.findViewById(R.id.sr_rv_expenseslist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (mAdapter == null) {
            mAdapter = new StoreReportRVAdapter(null, getContext());
        }
        recyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(LOADER_ID, mArgs, this);

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {
            case R.id.sr_sp_month:
                mMonth = months[position];
                getArguments().putString(MONTH,mMonth);
                break;

            case R.id.sr_sp_year:
                mYear = years[position];
                getArguments().putString(YEAR,mYear);
                break;
        }


        getLoaderManager().restartLoader(LOADER_ID, mArgs, this);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case LOADER_ID:
                if (mMonth.equalsIgnoreCase("Acum.")) {
                    String[] projection = {BaseColumns._ID,
                            StoresReportContract.Columns.STORE_NAME,
                            StoresReportContract.Columns.TOTAL_VALUE, // + ") AS " + StoresReportContract.Columns.TOTAL_VALUE,
                            StoresReportContract.Columns.EXPENSE_YEAR,
                            // StoresReportContract.Columns.EXPENSE_MONTH,
                            StoresReportContract.Columns.CATEGORY_ICON};
                    String selection = "YEAR = ?";
                    String[] selectionArgs = {mYear};
                    String sortOrder = StoresReportContract.Columns.TOTAL_VALUE + " DESC";
                    return new CursorLoader(getContext(),
                            StoresReportContract.CONTENT_URI_YEAR,
                            projection,
                            selection,
                            selectionArgs,
                            sortOrder);

                } else {
                    String[] projection = {BaseColumns._ID,
                            StoresReportContract.Columns.STORE_NAME,
                            StoresReportContract.Columns.TOTAL_VALUE, // + ") AS " + StoresReportContract.Columns.TOTAL_VALUE,
                            StoresReportContract.Columns.EXPENSE_YEAR,
                            StoresReportContract.Columns.EXPENSE_MONTH,
                            StoresReportContract.Columns.CATEGORY_ICON};

                    String selection = "YEAR = ? AND MONTH = ?";
                    String[] selectionArgs = {mYear, mMonth};
                    String sortOrder = StoresReportContract.Columns.TOTAL_VALUE + " DESC";

                    return new CursorLoader(getContext(),
                            StoresReportContract.CONTENT_URI,
                            projection,
                            selection,
                            selectionArgs,
                            sortOrder);
                }


//                if (args != null) {
//                    selection = args.getString(SELECTION_PARAM);
//                    selectionArgs = args.getStringArray(SELECTION_ARGS_PARAM);
//                    sortOrder = args.getString(SORT_ORDER_PARAM);
//                }

//                Log.d(TAG, "onCreateLoader: selection is " + selection);
//                Log.d(TAG, "onCreateLoader: selection args is " + selectionArgs[0] +" , "+ selectionArgs[1]);


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
}
