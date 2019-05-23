package com.tiagopereirabr.budgetcontrol;

import java.io.Serializable;

class Extra implements Serializable {

    public static final long serialVersionUID = 201912041507L;

    private long m_Id;
    private Expense mExpense;
    private double mTotal;

    public Extra(Expense expense, double total) {
        mExpense = expense;
        mTotal = total;
    }

    public long getId() {
        return m_Id;
    }

    public void setId(long id) {
        this.m_Id = id;
    }

    public Expense getExpense() {
        return mExpense;
    }

    public void setExpense(Expense expense) {
        mExpense = expense;
    }

    public double getTotal() {
        return mTotal;
    }

    public void setTotal(double total) {
        mTotal = total;
    }
}
