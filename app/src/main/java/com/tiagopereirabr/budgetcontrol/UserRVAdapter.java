package com.tiagopereirabr.budgetcontrol;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;


class UserRVAdapter extends RecyclerView.Adapter<UserRVAdapter.UserViewHolder> {

    private Cursor mCursor;
    private OnUserClickListener mListener;
    private final java.text.DateFormat mDateFormat;
    private User mUserSelected;
    private Context mContext;


    interface OnUserClickListener {
        void onUserClick(@NotNull User user);

        void onUserLongClick(@NotNull User user);
    }

    public UserRVAdapter(Cursor cursor, OnUserClickListener listener, Context context, User userSelected) {
        mCursor = cursor;
        mListener = listener;
        mDateFormat = DateFormat.getDateFormat(context);
        mContext = context;
        mUserSelected = userSelected;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user, viewGroup, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder holder, int i) {

        if ((mCursor != null)) {

            if (!mCursor.moveToPosition(i)) {
                throw new IllegalStateException("Couldn't move cursor to position " + i);
            }

            final User user = new User(mCursor.getLong(mCursor.getColumnIndex(UsersContract.Columns._ID)),
                    mCursor.getString(mCursor.getColumnIndex(UsersContract.Columns.NAME)),
                    mCursor.getLong(mCursor.getColumnIndex(UsersContract.Columns.INITIAL_DATE)),
                    mCursor.getInt(mCursor.getColumnIndex(UsersContract.Columns.CAN_INPUT_EXPSENSES)),
                    mCursor.getString(mCursor.getColumnIndex(UsersContract.Columns.EMAIL)),
                    mCursor.getLong(mCursor.getColumnIndex(UsersContract.Columns.AVATAR)));

            holder.userPic.setVisibility(View.VISIBLE);
            holder.input.setVisibility(View.VISIBLE);
            holder.since.setVisibility(View.VISIBLE);
            holder.userSelected.setVisibility(View.GONE);

            holder.name.setText(user.getName());

            String userDate = mDateFormat.format(user.getInitialDate() * 1000); //the date base store seconds, we need in milliseconds
            holder.date.setText(userDate);


            holder.userPic.setImageResource((int) user.getAvatar());


            if (user.getCanInputExpenses() == 1) {
                holder.canInput.setText("Yes");
            } else {
                holder.canInput.setText("No");
            }

            View.OnLongClickListener userLongListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
//                    holder.userSelected.setVisibility(View.VISIBLE);
                    mUserSelected = user;
                    if (mListener != null) {
                        mListener.onUserLongClick(user);
                        return true;
                    }
                    return false;
                }
            };

            View.OnClickListener userClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    holder.userSelected.setVisibility(View.VISIBLE);
                    mUserSelected = user;
                    if (mListener != null) {
                        mListener.onUserClick(user);
                    }
                }
            };


            if(mUserSelected != null){
                if(mUserSelected.getName().equalsIgnoreCase(user.getName())) {
                    holder.userSelected.setVisibility(View.VISIBLE);
                  }
            }

            holder.itemView.setOnLongClickListener(userLongListener);
            holder.itemView.setOnClickListener(userClickListener);


        } else {

            holder.name.setText("There is no users created.\n\nAdd a users by clicking in the floating button.");
            holder.userPic.setVisibility(View.GONE);
            holder.input.setVisibility(View.GONE);
            holder.since.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
//        Log.d(TAG, "getItemCount: starts");

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


    static class UserViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView name;
        TextView date;
        TextView canInput;
        ImageView userPic;
        TextView since;
        TextView input;
        ImageView userSelected;

        public UserViewHolder(View item) {
            super(item);

            this.name = item.findViewById(R.id.tv_userName);
            this.date = item.findViewById(R.id.tv_initialDate);
            this.canInput = item.findViewById(R.id.tv_canInputExpesnes);
            this.itemView = item;
            this.userPic = item.findViewById(R.id.tv_usr_pic);
            this.since = item.findViewById(R.id.tv_since);
            this.input = item.findViewById(R.id.tv_input);
            this.userSelected = item.findViewById(R.id.tv_usr_selected);

        }
    }
}
