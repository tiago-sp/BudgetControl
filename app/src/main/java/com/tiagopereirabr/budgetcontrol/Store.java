package com.tiagopereirabr.budgetcontrol;

import java.io.Serializable;

class Store implements Serializable {

    public static final long serialVersionUID = 201912041459L;

    private long m_Id;
    private String Name;
    private Category mCategory;

    public Store(String name, Category category) {
        Name = name;
        mCategory = category;
    }

    public long getId() {
        return m_Id;
    }

    public void setId(long id) {
        this.m_Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Category getCategory() {
        return mCategory;
    }

    public void setCategory(Category category) {
        mCategory = category;
    }
}
