package com.tiagopereirabr.budgetcontrol;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

public class CategoryRVAdapter extends RecyclerView.Adapter<CategoryRVAdapter.CatViewHolder> {
    private static final String TAG = "CategoryRVAdapter";

    private Context mContext;
    private OnCatClickListener mListener;
    private Cursor mCursor;
    private double maxBudget;

    interface OnCatClickListener {
        void onCatClick(@NotNull Category cat);

        void onCatLongClick(@NotNull Category cat);
    }

    public CategoryRVAdapter(Cursor cursor, OnCatClickListener listener, Context context) {
        mCursor = cursor;
        mContext = context;
        mListener = listener;

        maxBudget = getMaxBudget();
    }


    @Override
    public CategoryRVAdapter.CatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.category_item, parent, false);

        return new CatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CatViewHolder holder, int i) {
        if ((mCursor != null)) {

            if (!mCursor.moveToPosition(i)) {
                throw new IllegalStateException("Couldn't move cursor to position " + i);
            }

            final Category cat = new Category(mCursor.getLong(mCursor.getColumnIndex(CategoriesContract.Columns._ID)),
                    mCursor.getString(mCursor.getColumnIndex(CategoriesContract.Columns.CATEGORY_NAME)),
                    mCursor.getLong(mCursor.getColumnIndex(CategoriesContract.Columns.CATEGORY_IMAGE)),
                    mCursor.getString(mCursor.getColumnIndex(CategoriesContract.Columns.CATEGORY_COLOR)));

            Double totalReal = getTotalMonth(cat);
            Double totalBudget = getBudget(cat);

            Locale currentLocale = Locale.getDefault();
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(currentLocale);

            holder.real.setText(currencyFormatter.format(totalReal));
            holder.budget.setText(currencyFormatter.format(totalBudget));

            holder.name.setText(cat.getName());

            holder.catPic.setImageResource((int) cat.getCatImage());
            holder.itemView.setCardBackgroundColor(Color.parseColor(cat.getColor()));
            if (totalBudget != 0) {
                holder.budgetBar.setVisibility(View.VISIBLE);
                holder.budget.setVisibility(View.VISIBLE);
                holder.budgetBar.getLayoutParams().width = (int) (140 + (240 * (totalBudget / maxBudget)));
                holder.budgetBar.requestLayout();
            } else {
                holder.budgetBar.setVisibility(View.INVISIBLE);
                holder.budget.setVisibility(View.INVISIBLE);
            }

            if (totalReal != 0) {
                holder.realBar.setVisibility(View.VISIBLE);
                holder.real.setVisibility(View.VISIBLE);

                if(maxBudget == 0 ){
                    holder.realBar.getLayoutParams().width = 380;
                } else {
                    holder.realBar.getLayoutParams().width = (int) (140 + (240 * (totalReal / maxBudget)));
                }
                if(totalReal > totalBudget){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.realBar.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorAccent)));
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.realBar.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorPrimaryDark)));
                    }
                }
                holder.realBar.requestLayout();
            } else {
                holder.realBar.setVisibility(View.INVISIBLE);
                holder.real.setVisibility(View.INVISIBLE);
            }


            View.OnClickListener catListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onCatClick(cat);
                    }
                }
            };

            View.OnLongClickListener catLongListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mListener != null) {
                        mListener.onCatLongClick(cat);
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            holder.itemView.setOnClickListener(catListener);
            holder.itemView.setOnLongClickListener(catLongListener);
        } else {

        }
    }


    @Override
    public int getItemCount() {
        if ((mCursor == null) || (mCursor.getCount() == 0)) {
            return 0; //fib, because we populate a single ViewHolder with instructions
        } else {
            return mCursor.getCount();
        }
    }


    Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }

        int numItems = getItemCount();

        final Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        } else {
            notifyItemRangeRemoved(0, numItems);
        }

        return oldCursor;

    }

    public int getPosition (Category cat) {
        int position = -1;
        mCursor.moveToFirst();
        for(int i=0; i< mCursor.getCount(); i++){
            if(((int) mCursor.getLong(mCursor.getColumnIndex(CategoriesContract.Columns._ID))) == cat.getId()){
                position = i;
            }
            mCursor.moveToNext();
        }
        return position;
    }

    public double getTotalMonth(Category cat) {

        String month = String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1);
        String year = String.format("%4d", Calendar.getInstance().get(Calendar.YEAR));
        String[] projections = {"Sum( " + CategoriesReportContract.Columns.TOTAL_VALUE + ") AS TOTAL"};
        String[] selectionsArgs = {year, month, cat.getName()};

        Uri uri = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, "Total_Spent");


        Cursor cursor = mContext.getContentResolver().query(uri, projections, "YEAR = ? AND MONTH = ? AND " + CategoriesReportContract.Columns.CATEGORY_NAME + " = ?", selectionsArgs, null);

        double total;

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndex("TOTAL"));
            cursor.close();
        } else {
            cursor.close();
            total = 0;
        }

        return total;
    }

    public double getBudget(Category cat) {

        String[] projections = {BudgetContract.Columns.TOTAL};
        String[] selectionsArgs = {((Long) cat.getId()).toString()};

        Cursor cursor = mContext.getContentResolver().query(BudgetContract.CONTENT_URI, projections, BudgetContract.Columns._ID + " = ?", selectionsArgs, null);

        double total;

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndex(BudgetContract.Columns.TOTAL));
            cursor.close();
        } else {
            cursor.close();
            total = 0;
        }

        return total;
    }

    public double getMaxBudget() {

        String[] projections = {"MAX(" + BudgetContract.Columns.TOTAL + ") AS " + BudgetContract.Columns.TOTAL};
        Cursor cursor = mContext.getContentResolver().query(BudgetContract.CONTENT_URI, projections, null, null, null);

        double total;

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndex(BudgetContract.Columns.TOTAL));
            cursor.close();
        } else {
            cursor.close();
            total = 0;
        }

        return total;
    }

    static class CatViewHolder extends RecyclerView.ViewHolder {

        CardView itemView;
        TextView name;
        ImageView catPic;
        ConstraintLayout card;
        ImageView budgetBar;
        ImageView realBar;
        TextView real;
        TextView budget;

        public CatViewHolder(View item) {
            super(item);

            this.name = item.findViewById(R.id.tv_cat_item);
            this.itemView = item.findViewById(R.id.cv_cat_item);
            this.catPic = item.findViewById(R.id.im_cat_item);
            this.card = item.findViewById(R.id.rl_cat_item);
            this.budgetBar = item.findViewById(R.id.im_cat_item_budget);
            this.realBar = item.findViewById(R.id.im_cat_item_real);
            this.real = item.findViewById(R.id.tv_cat_item_real);
            this.budget = item.findViewById(R.id.tv_cat_item_budget);


        }
    }

}
