package com.tiagopereirabr.budgetcontrol;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import co.dift.ui.SwipeToAction;

public class ExpensesReportRVAdapter extends RecyclerView.Adapter<ExpensesReportRVAdapter.ExpenseViewHolder> {

    private Context mContext;
//    private OnExpenseSwipeListener mListener;
    private Cursor mCursor;
    private final java.text.DateFormat mDateFormat;


//    interface OnExpenseSwipeListener {
//        void onExpenseSwipeLeft(@NotNull Expense expense);
//
//        void onExpenseSwipeRight(@NotNull Expense expense);
//    }

    public ExpensesReportRVAdapter(Cursor cursor, Context context) {
        mCursor = cursor;
        mContext = context;
        mDateFormat = DateFormat.getDateFormat(context);
//        mListener = listener;
    }


    @Override
    public ExpensesReportRVAdapter.ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.expense_report_item, parent, false);

        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExpenseViewHolder holder, int i) {
        if ((mCursor != null)) {

            if (!mCursor.moveToPosition(i)) {
                throw new IllegalStateException("Couldn't move cursor to position " + i);
            }

            String name = mCursor.getString(mCursor.getColumnIndex(ExpensesReportContract.Columns.STORE_NAME));
            Double total = mCursor.getDouble(mCursor.getColumnIndex(ExpensesReportContract.Columns.TOTAL_VALUE));
            int cat_image = mCursor.getInt(mCursor.getColumnIndex(ExpensesReportContract.Columns.CATEGORY_ICON));
            long dateInMilis = mCursor.getLong(mCursor.getColumnIndex(ExpensesReportContract.Columns.DATE_INMILIS));

            SimpleExpense expense = new SimpleExpense(mCursor.getLong(mCursor.getColumnIndex(ExpensesReportContract.Columns._ID)),name,total,dateInMilis);


            String expenseDate = mDateFormat.format(dateInMilis);


            holder.storeName.setText(name);
            holder.catIcon.setImageResource(cat_image);
            holder.totalValue.setText(String.format("%1$, .2f",total));
            holder.date.setText(expenseDate);
            holder.data = expense;

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

    public int getPosition (Expense exp) {
        int position = -1;
        mCursor.moveToFirst();
        for(int i=0; i< mCursor.getCount(); i++){
            if(((int) mCursor.getLong(mCursor.getColumnIndex(CategoriesContract.Columns._ID))) == exp.getId()){
                position = i;
            }
            mCursor.moveToNext();
        }
        return position;
    }

    static class ExpenseViewHolder extends SwipeToAction.ViewHolder<SimpleExpense> {

        ImageView catIcon;
        TextView storeName;
        TextView totalValue;
        TextView date;
        View item;

        public ExpenseViewHolder(View item) {
            super(item);

            this.storeName = item.findViewById(R.id.er_tv_storeName);
            this.catIcon = item.findViewById(R.id.er_iv_catIcon);
            this.totalValue = item.findViewById(R.id.er_tv_total);
            this.date = item.findViewById(R.id.er_tv_date);
            this.item = item;


        }
    }

}
