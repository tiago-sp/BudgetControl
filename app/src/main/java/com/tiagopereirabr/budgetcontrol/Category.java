package com.tiagopereirabr.budgetcontrol;

import java.io.Serializable;

class Category implements Serializable {

    public static final long serialVersionUID = 201912041455L;

    private long m_Id;
    private String mName;
    private long mCatImage;
    private String mColor;

    public Category(long id,String name, long catImage, String color) {
        mName = name;
        m_Id = id;
        mCatImage = catImage;
        mColor = color;
    }

    public long getId() {
        return m_Id;
    }

    public void setId(long id) {
        this.m_Id = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public long getCatImage() {
        return mCatImage;
    }

    public void setCatImage(long catImage) {
        mCatImage = catImage;
    }

    public String getColor() {
        return mColor;
    }

    public void setColor(String color) {
        mColor = color;
    }
}
