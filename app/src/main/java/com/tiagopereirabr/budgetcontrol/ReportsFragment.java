package com.tiagopereirabr.budgetcontrol;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class ReportsFragment extends Fragment {
    private static final String TAG = "ReportsFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String YEAR = "YEAR";
    private static final String MONTH = "MONTH";
    public static final String YEARS = "YEARS";
    public static final String MONTHS = "MONTHS";

    // TODO: Rename and change types of parameters
    private String mYear;
    private String mMonth;

    private static final String[] months = {"Acum.", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    private static final String[] years = {"Acum.", "2018", "2019"};

    private Bundle mArgs = new Bundle();


//    private BottomNavigationView.OnNavigationItemSelectedListener mListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.navigation_report_user:
//
//                    return true;
//
//                case R.id.navigation_report_category:
//                    Log.d(TAG, "onNavigationItemSelected: Category report selected");
//                    CategoryReportFragment fragment_cat = new CategoryReportFragment();
//
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.report_container, fragment_cat)
//                            .commit();
//                    return true;
//
//
//                case R.id.navigation_report_store:
//
//                    StoreReportFragment fragment_store = new StoreReportFragment();
//
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.report_container, fragment_store)
//                            .commit();
//                    return true;
//
//            }
//            return false;
//        }
//    };



    public ReportsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportsFragment newInstance(String param1, String param2) {
        ReportsFragment fragment = new ReportsFragment();
        Bundle args = new Bundle();
        args.putString(YEAR, param1);
        args.putString(MONTH, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mYear = getArguments().getString(YEAR);
            mMonth = getArguments().getString(MONTH);
            mArgs.putString(YEAR,mYear);
            mArgs.putString(MONTH,mMonth);
            mArgs.putStringArray(MONTHS,months);
            mArgs.putStringArray(YEARS,years);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        return inflater.inflate(R.layout.fragment_reports, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView navigation = (BottomNavigationView) view.findViewById(R.id.navigationbar_report);
        //  Menu menu = navigation.getMenu();

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_report_user:
                        clearFragment();
                        return true;

                    case R.id.navigation_report_category:
                        clearFragment();
                        CategoryReportFragment fragment_cat = new CategoryReportFragment();
                        fragment_cat.setArguments(mArgs);

                            getFragmentManager().beginTransaction()
                                    .replace(R.id.report_container, fragment_cat, "fragment_cat")
                                    .commit();

                        return true;

                    case R.id.navigation_report_store:
                        clearFragment();
                        StoreReportFragment fragment = new StoreReportFragment();
                        fragment.setArguments(mArgs);

                        getFragmentManager().beginTransaction()
                                .replace(R.id.report_container, fragment,"storereport")
                                .commit();
                        return true;

                    case R.id.navigation_report_expenses:
                        clearFragment();
                        ExpensesReportFragment fragment_expense = new ExpensesReportFragment();
                        fragment_expense.setArguments(mArgs);

                        getFragmentManager().beginTransaction()
                                .replace(R.id.report_container, fragment_expense,"expensesreport")
                                .commit();
                        return true;

                    case R.id.navigation_report_graphs:
                        clearFragment();
                        GraphReportFragment graph_fragment = new GraphReportFragment();
                        graph_fragment.setArguments(mArgs);

                        getFragmentManager().beginTransaction()
                                .replace(R.id.report_container, graph_fragment,"graph_fragment")
                                .commit();
                        return true;

                }
                return false;
            }
        });


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);



    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private boolean clearFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.report_container);
        if (fragment != null) {
            if(fragment.getArguments() != null){
                Bundle args = fragment.getArguments();
                mMonth = args.getString(MONTH);
                mYear = args.getString(YEAR);
            }
            getFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
            return true;
        } else {
            return false;
        }
    }

}
