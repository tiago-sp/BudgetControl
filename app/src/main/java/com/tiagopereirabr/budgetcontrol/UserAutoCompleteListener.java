package com.tiagopereirabr.budgetcontrol;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

public class UserAutoCompleteListener implements TextWatcher {
    Context mContext;
    OnTextChangedListener mListener;

    interface OnTextChangedListener {
        void onTextChanged (CharSequence userInput, int start, int before, int count);
    }

    public UserAutoCompleteListener (Context context, OnTextChangedListener listener){
        mContext = context;
        mListener = listener;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence userInput, int start, int before, int count) {

        mListener.onTextChanged(userInput,start,before,count);

    }
 }
