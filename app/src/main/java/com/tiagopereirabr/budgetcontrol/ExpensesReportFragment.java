package com.tiagopereirabr.budgetcontrol;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.dift.ui.SwipeToAction;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExpensesReportFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemSelectedListener {
    private static final String TAG = "StoreReportFragment";

    private static final int LOADER_ID = 1;
    private static final String SELECTION_PARAM = "SELECTION";
    private static final String SELECTION_ARGS_PARAM = "SELECTION_ARGS";
    private static final String SORT_ORDER_PARAM = "SORT_ORDER";
    public static final String YEAR = "YEAR";
    public static final String MONTH = "MONTH";
    public static final String YEARS = "YEARS";
    public static final String MONTHS = "MONTHS";

    private Bundle mArgs = new Bundle();

    AppDatabase dbHelper = AppDatabase.getInstance(getContext());

    private static String[] months;
    private static String[] years;

    private ExpensesReportRVAdapter mAdapter;
    private SimpleSpinnerAdapter mMonthAdapter;
    private SimpleSpinnerAdapter mYearAdapter;

    private View mcurrentView;
    SwipeToAction swipeToAction;
    String searchTerm;

    AutoCompleteTextView storeName;
    String[] stores = new String[]{"...Retrive from db..."};
    ArrayAdapter<String> mAdapterStore;
    Button btnClear;

    private String mMonth;
    private String mYear;
    private String mStoreFiltered = null;

    public ExpensesReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expenses_report, container, false);
        mcurrentView = view;

        Spinner monthSpinner = view.findViewById(R.id.er_sp_month);
        Spinner yearSpinner = view.findViewById(R.id.er_sp_year);

        if(getArguments() != null){
            mYear = getArguments().getString(YEAR);
            mMonth = getArguments().getString(MONTH);
            months = getArguments().getStringArray(MONTHS);
            years = getArguments().getStringArray(YEARS);

            List<String> listmonth = new ArrayList<String>(Arrays.asList(months));
            listmonth.remove("Acum.");
            months = listmonth.toArray(new String[0]);

            List<String> listyear = new ArrayList<String>(Arrays.asList(years));
            listyear.remove("Acum.");
            years = listyear.toArray(new String[0]);
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

        btnClear = view.findViewById(R.id.er_bt_clear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStoreFiltered = null;
                storeName.setText("");
                reloadLoader();
            }
        });

        storeName = (AutoCompleteTextView) view.findViewById(R.id.er_ac_store_name);
        storeName.addTextChangedListener(new StoreExpensesAutoCompleteListener(this));
        mAdapterStore = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, stores);
        storeName.setAdapter(mAdapterStore);

        storeName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mStoreFiltered = storeName.getText().toString();
                storeName.clearFocus();
                btnClear.requestFocus();
                hideKeyboard(btnClear);
                reloadLoader();
            }
        });

        storeName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    mStoreFiltered = storeName.getText().toString();
                    reloadLoader();
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    storeName.clearFocus();
                    btnClear.requestFocus();
                    return true;
                }
                return false;
            }
        });


        final RecyclerView recyclerView = view.findViewById(R.id.er_rv_expenseslist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (mAdapter == null) {
            mAdapter = new ExpensesReportRVAdapter(null, getContext());
        }
        recyclerView.setAdapter(mAdapter);

        swipeToAction = new SwipeToAction(recyclerView, new SwipeToAction.SwipeListener<SimpleExpense>() {

            @Override
            public boolean swipeLeft(final SimpleExpense itemData) {
                displaySnackbar("Delete " +itemData.getStoreName() + "$ " + itemData.getTotal(), "YES", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().getContentResolver().delete(ExpensesContract.buildExpenseUri((long) itemData.getId()),null,null);
                        //mAdapter.notifyDataSetChanged();
                        ExpensesReportFragment frag = (ExpensesReportFragment) getFragmentManager().findFragmentByTag("expensesreport");
                        frag.getLoaderManager().restartLoader(LOADER_ID, mArgs, frag);
                        MainActivity main = (MainActivity) getActivity();
                        main.getTotalMonth();
                    }
                });
                return true;
            }

            @Override
            public boolean swipeRight(SimpleExpense itemData) {

                MainActivity ma = (MainActivity) getActivity();
                Intent intent = new Intent(getActivity(), AddExpensesActicity.class);
                intent.putExtra(ExpensesContract.Columns._ID, itemData.getId());
                Log.d(TAG, "swipeRight: expense id equals to " + itemData.getId());
                intent.putExtra(User.class.getSimpleName(),ma.getUserSelected());
                ma.startActivityForResult(intent, MainActivity.CHANGE_EXPENSE);

                return true;
            }

            @Override
            public void onClick(SimpleExpense itemData) {


            }

            @Override
            public void onLongClick(SimpleExpense itemData) {

            }
        });

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                swipeToAction.swipeRight(2);
//            }
//        }, 3000);

        getLoaderManager().initLoader(LOADER_ID, mArgs, this);

        return view;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


        switch (parent.getId()) {
            case R.id.er_sp_month:
                mMonth = months[position];
                getArguments().putString(MONTH,mMonth);
                break;

            case R.id.er_sp_year:
                mYear = years[position];
                getArguments().putString(YEAR,mYear);
                break;
        }


        getLoaderManager().restartLoader(LOADER_ID, mArgs, this);

    }

    private void reloadLoader(){
        Log.d(TAG, "reloadLoader: entrou aqui");
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
                String[] projection = {BaseColumns._ID,
                        ExpensesReportContract.Columns.STORE_NAME,
                        ExpensesReportContract.Columns.TOTAL_VALUE,
                        ExpensesReportContract.Columns.EXPENSE_YEAR,
                        ExpensesReportContract.Columns.EXPENSE_MONTH,
                        ExpensesReportContract.Columns.DATE_INMILIS,
                        ExpensesReportContract.Columns.DATE,
                        ExpensesReportContract.Columns.CATEGORY_ICON};
                if(mStoreFiltered != null) {
                    String selection = "YEAR = ? AND MONTH = ? AND " + ExpensesReportContract.Columns.STORE_NAME + " = ?";
                    String[] selectionArgs = {mYear, mMonth, mStoreFiltered};
                    mStoreFiltered = null;
                    return new CursorLoader(getContext(),
                            ExpensesReportContract.CONTENT_URI,
                            projection,
                            selection,
                            selectionArgs,
                            null);
                } else {
                    String selection = "YEAR = ? AND MONTH = ?";
                    String[] selectionArgs = {mYear, mMonth};

                    return new CursorLoader(getContext(),
                            ExpensesReportContract.CONTENT_URI,
                            projection,
                            selection,
                            selectionArgs,
                            null);
                }




//            case 2:
//                String[] projection_Stores = {StoresContract.Columns.NAME};
//                String selection_Store = "Name LIKE ?";
//                String[] selectionArgs_Store = {searchTerm};
//
//                return new CursorLoader(getContext(),
//                        StoresContract.CONTENT_URI,
//                        projection_Stores,
//                        selection_Store,
//                        selectionArgs_Store,
//                        null);


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

    private void displaySnackbar(String text, String actionName, View.OnClickListener action) {
        Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG)
                .setAction(actionName, action);

        View v = snack.getView();
        v.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.BLACK);

        snack.show();
    }

    public void refreshList (Expense exp){
        int position = mAdapter.getPosition(exp);
        Log.d(TAG, "refreshList: " + position);
        if(position >= 0) {
            ExpensesReportFragment frag = (ExpensesReportFragment) getFragmentManager().findFragmentByTag("expensesreport");
            frag.getLoaderManager().restartLoader(LOADER_ID, mArgs, frag);
//            mAdapter.notifyDataSetChanged();
            mAdapter.notifyItemChanged(position);
        }
    }
}
