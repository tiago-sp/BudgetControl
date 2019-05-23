package com.tiagopereirabr.budgetcontrol;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;

public class StoreAutoCompleteListener implements TextWatcher {
    Context mContext;

    public StoreAutoCompleteListener(Context context){
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

        AddExpensesActicity addExpensesActicity = (AddExpensesActicity) mContext;
        addExpensesActicity.stores = addExpensesActicity.getStoresFromDb(userInput.toString());

        // update the adapter
        addExpensesActicity.mAdapterStore.notifyDataSetChanged();
        addExpensesActicity.mAdapterStore = new ArrayAdapter<String>(addExpensesActicity,android.R.layout.simple_dropdown_item_1line,addExpensesActicity.stores);
        addExpensesActicity.storeName.setAdapter(addExpensesActicity.mAdapterStore);

    }
 }
