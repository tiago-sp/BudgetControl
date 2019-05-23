package com.tiagopereirabr.budgetcontrol;

import java.io.Serializable;

class Expense implements Serializable {

    public static final long serialVersionUID = 201912041450L;

    private long m_Id;
    private Store mStore;
    private double mTotal;
    private long mDate;
    private User mUser;
    private Category mCategory;

    public Expense(Store store, double total, long date, User user, Category cat) {
        mStore = store;
        mTotal = total;
        mDate = date;
        mUser = user;
        mCategory = cat;
    }

    public long getId() {
        return m_Id;
    }

    public void setId(long id) {
        this.m_Id = id;
    }

    public Store getStore() {
        return mStore;
    }

    public void setStore(Store store) {
        mStore = store;
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

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }
}
