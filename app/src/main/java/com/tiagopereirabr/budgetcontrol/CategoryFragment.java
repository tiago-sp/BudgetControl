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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class CategoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, CategoryRVAdapter.OnCatClickListener{
    public static final int LOADER_ID = 0;

    private Bundle mArgs = new Bundle();

    private RecyclerView mRecyclerView;
    private ArrayList<Category> mCategories;
    private CategoryRVAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public static CategoryFragment newInstance() {
        return new CategoryFragment();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        LoaderManager.getInstance(this).restartLoader(LOADER_ID,null,null);
//    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Activities containing this fragment must implement its callbacks.
        Activity activity = getActivity();
        if (!(activity instanceof UserRVAdapter.OnUserClickListener)) {
            assert activity != null;
            throw new ClassCastException(activity.getClass().getSimpleName() + " must implements CursorRecyclerViewAdapter.OnTaskClickListener interface");
        }

        LoaderManager.getInstance(this).initLoader(LOADER_ID, null,  this);


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.category_fragment,container,false);

        FloatingActionButton fab = view.findViewById(R.id.fab_category_fragment);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),AddCategoryActivity.class));
            }
        });

        mRecyclerView = view.findViewById(R.id.rv_category);
        GridLayoutManager manager = new GridLayoutManager(getActivity(),2,GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(manager);


        if (mAdapter == null) {
            mAdapter = new CategoryRVAdapter(null,(CategoryRVAdapter.OnCatClickListener) getActivity(),getContext());
        }

        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = {CategoriesContract.Columns._ID, CategoriesContract.Columns.CATEGORY_NAME, CategoriesContract.Columns.CATEGORY_IMAGE, CategoriesContract.Columns.CATEGORY_COLOR};
        String sortOrder = CategoriesContract.Columns.CATEGORY_NAME  + " COLLATE NOCASE";

        switch (i) {
            case LOADER_ID:
                return new CursorLoader(getActivity(),
                        CategoriesContract.CONTENT_URI,
                        projection,
                        null,
                        null,
                        sortOrder);
            default:
                throw new InvalidParameterException(".onCreateLoader called with invalid loader id" + i);
        }
    }

//    @Override
//    public void onItemClick(Category item) {
//        Toast.makeText(getContext(), item.getName() + " is clicked", Toast.LENGTH_SHORT).show();
//    }

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
    public void onCatClick(@NotNull Category cat) {
        CategoryRVAdapter.OnCatClickListener listener = (CategoryRVAdapter.OnCatClickListener) getActivity();
        if (listener != null) {
            listener.onCatClick(cat);
        }

    }

    @Override
    public void onCatLongClick(@NotNull Category cat) {
        CategoryRVAdapter.OnCatClickListener listener = (CategoryRVAdapter.OnCatClickListener) getActivity();
        if (listener != null) {
            listener.onCatLongClick(cat);
        }
    }

    public void refreshList (Category cat){
        int position = mAdapter.getPosition(cat);
        Log.d(TAG, "refreshList: " + position);
        if(position >= 0) {
            mAdapter.notifyItemChanged(position);
        }
    }
}
