package com.tiagopereirabr.budgetcontrol;


import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;

public class StoreExpensesAutoCompleteListener implements TextWatcher {
    Fragment mContext;

    public StoreExpensesAutoCompleteListener(Fragment context){
        mContext = context;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence userInput, int start, int before, int count) {

        ExpensesReportFragment caller = (ExpensesReportFragment) mContext;
        caller.stores = caller.getStoresFromDb(userInput.toString());

        // update the adapter
        caller.mAdapterStore.notifyDataSetChanged();
        caller.mAdapterStore = new ArrayAdapter<String>(caller.getContext(),android.R.layout.simple_dropdown_item_1line,caller.stores);
        caller.storeName.setAdapter(caller.mAdapterStore);

    }
 }
