package com.tiagopereirabr.budgetcontrol;

import java.io.Serializable;

class User implements Serializable {

    public static final long serialVersionUID = 201912041503L;

    private long m_Id;
    private String mName;
    private long mInitialDate;
    private int mCanInputExpenses;
    private long mAvatar;
    private String mEmail;

    public User(long id, String name, long initialDate, int canInputExpenses, String email, long avatar) {
        m_Id = id;
        mName = name;
        mInitialDate = initialDate;
        mCanInputExpenses = canInputExpenses;
        mAvatar = avatar;
        mEmail = email;
    }

    public long getId() {
        return m_Id;
    }

    public void setId(long id) {
        this.m_Id = id;
    }

    public long getAvatar() {
        return mAvatar;
    }

    public void setAvatar(long avatar) {
        mAvatar = avatar;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Long getInitialDate() {
        return mInitialDate;
    }

    public void setInitialDate(long initialDate) {
        mInitialDate = initialDate;
    }

    public int getCanInputExpenses() {
        return mCanInputExpenses;
    }

    public void setCanInputExpenses(int canInputExpenses) {
        mCanInputExpenses = canInputExpenses;
    }
}
