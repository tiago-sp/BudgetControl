package com.tiagopereirabr.budgetcontrol;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;

public class LoginUserFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, UserRVAdapter.OnUserClickListener {

    public static final int LOADER_ID = 0;

    private Bundle mArgs = new Bundle();
    private UserRVAdapter mAdapter; // add adapter reference
    private MainActivity mMainActivity;
    private RecyclerView mRecyclerView;

    public static LoginUserFragment newInstance() {
        return new LoginUserFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Activities containing this fragment must implement its callbacks.
        Activity activity = getActivity();
        if (!(activity instanceof UserRVAdapter.OnUserClickListener)) {
            assert activity != null;
            throw new ClassCastException(activity.getClass().getSimpleName() + " must implements CursorRecyclerViewAdapter.OnTaskClickListener interface");
        }

        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.login_user,container,false);

        FloatingActionButton fab = view.findViewById(R.id.fabAddUser);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),AddUserActivity.class));
            }
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list_user);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView = recyclerView;

        MainActivity main = (MainActivity) getActivity();
        User mUser = main.getUserSelected();

        if (mAdapter == null) {
            mAdapter = new UserRVAdapter(null, (UserRVAdapter.OnUserClickListener) getActivity(),getContext(),mUser);
        }

        recyclerView.setAdapter(mAdapter);

        return view;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = {UsersContract.Columns._ID, UsersContract.Columns.NAME, UsersContract.Columns.INITIAL_DATE, UsersContract.Columns.CAN_INPUT_EXPSENSES, UsersContract.Columns.EMAIL,UsersContract.Columns.AVATAR};
        String sortOrder = UsersContract.Columns.NAME + "," + UsersContract.Columns.CAN_INPUT_EXPSENSES + " COLLATE NOCASE";

        switch (i) {
            case LOADER_ID:
                return new CursorLoader(getActivity(),
                        UsersContract.CONTENT_URI,
                        projection,
                        null,
                        null,
                        sortOrder);
            default:
                throw new InvalidParameterException(".onCreateLoader called with invalid loader id" + i);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        int count = mAdapter.getItemCount();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);

    }

    @Override
    public void onUserClick(@NotNull User user) {
        UserRVAdapter.OnUserClickListener listener = (UserRVAdapter.OnUserClickListener) getActivity();
        if (listener != null) {
            listener.onUserClick(user);
        }


    }

    @Override
    public void onUserLongClick(@NotNull User user) {
        UserRVAdapter.OnUserClickListener listener = (UserRVAdapter.OnUserClickListener) getActivity();

        if (listener != null) {
            listener.onUserLongClick(user);
        }
    }

    public void restartLoader(User user){

        mAdapter.notifyDataSetChanged();


    }
}
