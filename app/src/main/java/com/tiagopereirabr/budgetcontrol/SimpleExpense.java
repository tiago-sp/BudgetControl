package com.tiagopereirabr.budgetcontrol;

import java.io.Serializable;

class SimpleExpense implements Serializable {

    public static final long serialVersionUID = 201912041450L;

    private long m_Id;
    private String mStoreName;
    private double mTotal;
    private long mDate;

    public SimpleExpense(long id, String store, double total, long date) {
        mStoreName = store;
        mTotal = total;
        mDate = date;
        m_Id = id;
    }

    public long getId() {
        return m_Id;
    }

    public void setId(long id) {
        this.m_Id = id;
    }

    public String getStoreName(){
        return mStoreName;
    }

    public double getTotal() {
        return mTotal;
    }

    public void setTotal(double total) {
        mTotal = total;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long date) {
        mDate = date;
    }

}
