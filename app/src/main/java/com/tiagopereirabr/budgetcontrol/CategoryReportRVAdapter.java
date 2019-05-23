package com.tiagopereirabr.budgetcontrol;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryReportRVAdapter extends RecyclerView.Adapter<CategoryReportRVAdapter.ExpenseViewHolder> {

    private Context mContext;
//    private OnCatClickListener mListener;
    private Cursor mCursor;


//    interface OnCatClickListener {
//        void onCatClick(@NotNull Category cat);
//
//        void onCatLongClick(@NotNull Category cat);
//    }

    public CategoryReportRVAdapter(Cursor cursor, Context context) {
        mCursor = cursor;
        mContext = context;
//        mListener = listener;
    }


    @Override
    public CategoryReportRVAdapter.ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.expense_categoryreport_item, parent, false);

        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExpenseViewHolder holder, int i) {
        if ((mCursor != null)) {

            if (!mCursor.moveToPosition(i)) {
                throw new IllegalStateException("Couldn't move cursor to position " + i);
            }

            String name = mCursor.getString(mCursor.getColumnIndex(StoresReportContract.Columns.STORE_NAME));
            Double total = mCursor.getDouble(mCursor.getColumnIndex(StoresReportContract.Columns.TOTAL_VALUE));
            int cat_image = mCursor.getInt(mCursor.getColumnIndex(StoresReportContract.Columns.CATEGORY_ICON));



            holder.storeName.setText(name);
            holder.catIcon.setImageResource(cat_image);
            holder.totalValue.setText(String.format("%1$, .2f",total));


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
    static class ExpenseViewHolder extends RecyclerView.ViewHolder {

        ImageView catIcon;
        TextView storeName;
        TextView totalValue;

        public ExpenseViewHolder(View item) {
            super(item);

            this.storeName = item.findViewById(R.id.ecr_tv_storeName);
            this.catIcon = item.findViewById(R.id.ecr_iv_catIcon);
            this.totalValue = item.findViewById(R.id.ecr_tv_total);


        }
    }

}
