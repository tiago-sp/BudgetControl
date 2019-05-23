package com.tiagopereirabr.budgetcontrol;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class GraphReportFragment extends Fragment {
    BarChart categoryChart;
    BarChart totalExpenseChart;
    LineChart expenseLineChart;
    CheckBox ttExpenses;
    Spinner categorySpinner;
    public static final int CATEGORY_EXPENSES = 1;
    public static final int TOTAL_EXPENSE_GRAPH = 2;
    public static final int BUDGET = 3;
    public static final int LINE_EXPENSES = 4;
    public static final int LINE_CATEGORY = 5;
    final ArrayList<String> xLabelBudget = new ArrayList<String>();
    final ArrayList<String> xLabelTotal = new ArrayList<String>();
    final ArrayList<String> xLabelExpenseLine = new ArrayList<String>();
    private String mMonth;
    private String mYear;
    private String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Agu", "Sep", "Oct", "Nov", "Dec"};


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph_report, container, false);

        categoryChart = (BarChart) view.findViewById(R.id.gr_chart_category);
        totalExpenseChart = (BarChart) view.findViewById(R.id.gr_chart_total_expense);
        expenseLineChart = (LineChart) view.findViewById(R.id.gr_chart_expense_line);

        ttExpenses = (CheckBox) view.findViewById(R.id.gr_total_expense_line);
        categorySpinner = (Spinner) view.findViewById(R.id.gr_spinner_categories);

        SpinnerAdapter catAdapter = getCategoriesAdapter(view);
        categorySpinner.setAdapter(catAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LineDataSet lineDataSet = getLineDataSet(LINE_CATEGORY);
                setExpenseLineChart(lineDataSet);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ttExpenses.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (ttExpenses.isChecked()) {
                    categorySpinner.setVisibility(View.GONE);
                    setExpenseLineChart(null);
                } else {
                    categorySpinner.setVisibility(View.VISIBLE);
                }
            }
        });

        setCategoryChart();
        categoryChart.setPinchZoom(false);
        categoryChart.setDrawBarShadow(false);
        categoryChart.setDrawGridBackground(false);


        setTotalExpensesChart();
        totalExpenseChart.setPinchZoom(true);
        totalExpenseChart.setDrawBarShadow(false);
        totalExpenseChart.setDrawGridBackground(false);

        setExpenseLineChart(null);
        expenseLineChart.setTouchEnabled(false);

        return view;
    }

    private SpinnerAdapter getCategoriesAdapter(View view) {
        ArrayList<String> list = new ArrayList<>();
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cursor;

        String[] projection = {CategoriesContract.Columns.CATEGORY_NAME};
        String sortOrder = CategoriesContract.Columns.CATEGORY_NAME;

        cursor = cr.query(CategoriesContract.CONTENT_URI, projection, null, null, sortOrder);

        if (cursor.moveToFirst()) {
            for (float i = 0; i < cursor.getCount(); i++) {
                list.add(cursor.getString(cursor.getColumnIndex(CategoriesContract.Columns.CATEGORY_NAME)));
                cursor.moveToNext();
            }
        }

        ArrayAdapter<String> categoryList = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_dropdown_item_1line, list);

        return categoryList;
    }

    private void setCategoryChart() {
        Legend l = categoryChart.getLegend();


        categoryChart.getDescription().setEnabled(false);
        XAxis xAxis = categoryChart.getXAxis();
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);

        xAxis.setLabelCount(9);


        YAxis leftAxis = categoryChart.getAxisLeft();

        leftAxis.setEnabled(false);

        categoryChart.getAxisRight().setEnabled(false);

        float groupSpace = 0.08f;
        float barSpace = 0.01f; // x2 dataset
        float barWidth = 0.45f; // x2 dataset
// (0.02 + 0.45) * 2 + 0.06 = 1.00 -> interval per "group"

        BarData data = new BarData(getDataSet(BUDGET), getDataSet(CATEGORY_EXPENSES));
        data.setBarWidth(barWidth); // set the width of each bar

        categoryChart.setData(data);
//        categoryChart.getXAxis().setAxisMaximum(9f);
        categoryChart.getXAxis().setAxisMaximum(categoryChart.getBarData().getGroupWidth(groupSpace, barSpace) * xLabelBudget.size());
        categoryChart.groupBars(0, groupSpace, barSpace); // perform the "explicit" grouping
        categoryChart.setFitBars(false);
        categoryChart.setDrawValueAboveBar(true);

        //chart.setDescription("Expenses by Category");
        categoryChart.animateXY(1000, 3000);
        categoryChart.invalidate();
    }

    private void setTotalExpensesChart() {
        Legend l = totalExpenseChart.getLegend();


        totalExpenseChart.getDescription().setEnabled(false);

        XAxis xAxis = totalExpenseChart.getXAxis();
        xAxis.setCenterAxisLabels(false);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
//        xAxis.setLabelCount(10);

        YAxis leftAxis = totalExpenseChart.getAxisLeft();
        leftAxis.setEnabled(false);

        totalExpenseChart.getAxisRight().setEnabled(false);

        BarData data = new BarData(getDataSet(TOTAL_EXPENSE_GRAPH));

        totalExpenseChart.setData(data);

        totalExpenseChart.setFitBars(true);
        totalExpenseChart.setDrawValueAboveBar(true);

        //chart.setDescription("Expenses by Category");
        totalExpenseChart.animateXY(1000, 2000);
        totalExpenseChart.invalidate();
    }

    private void setExpenseLineChart(LineDataSet dataSet) {
        Legend l = expenseLineChart.getLegend();

        expenseLineChart.getDescription().setEnabled(false);

        XAxis xAxis = expenseLineChart.getXAxis();
        xAxis.setCenterAxisLabels(false);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
//        xAxis.setLabelCount(2,true);
//        xAxis.setAxisMaximum(10);

        YAxis leftAxis = expenseLineChart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setDrawGridLines(true);

        expenseLineChart.getAxisRight().setEnabled(false);

        LineData data;
        if (dataSet == null) {
            data = new LineData(getLineDataSet(LINE_EXPENSES));
        } else {
            data = new LineData(dataSet);
            leftAxis.setAxisMinimum(0);
        }
        expenseLineChart.setData(data);


        //chart.setDescription("Expenses by Category");
        expenseLineChart.animateXY(1500, 500);
        expenseLineChart.invalidate();
    }

    private LineDataSet getLineDataSet(int data) {

        ArrayList valueSet1 = new ArrayList();
        ContentResolver cr = getActivity().getContentResolver();
        final Cursor cursor;
        String serieLabel;

        switch (data) {
            case LINE_EXPENSES:
                String[] projection_total = {"SUM(" + CategoriesReportContract.Columns.TOTAL_VALUE + ") AS " + CategoriesReportContract.Columns.TOTAL_VALUE,
                        CategoriesReportContract.Columns.EXPENSE_YEAR,
                        CategoriesReportContract.Columns.EXPENSE_MONTH};
                String selection_total = null;
                String[] selectionArgs_total = null;
                String sortOrder_total = CategoriesReportContract.Columns.EXPENSE_YEAR + " , " + CategoriesReportContract.Columns.EXPENSE_MONTH;

                Uri uri = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, "TOTAL_EXPENSES_MONTH_YEAR");

                cursor = cr.query(uri, projection_total, selection_total, selectionArgs_total, sortOrder_total);

                if (cursor.moveToFirst()) {
                    String label;
                    for (float i = 0; i < cursor.getCount(); i++) {
                        valueSet1.add(new BarEntry(i, cursor.getFloat(cursor.getColumnIndex(CategoriesReportContract.Columns.TOTAL_VALUE))));
                        label = months[Integer.parseInt(cursor.getString(cursor.getColumnIndex(CategoriesReportContract.Columns.EXPENSE_MONTH))) - 1] + "/" + cursor.getString(cursor.getColumnIndex(CategoriesReportContract.Columns.EXPENSE_YEAR)).substring(2);
                        xLabelExpenseLine.add(label);
                        cursor.moveToNext();
                    }

                    XAxis xAxisTotal = expenseLineChart.getXAxis();
                    xAxisTotal.setValueFormatter(new IAxisValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, AxisBase axis) {
                            if (value >= 0f && value < cursor.getCount()) {
                                return xLabelExpenseLine.get((int) value);
                            } else {
                                return " ";
                            }
                        }
                    });
                }
                serieLabel = "Expenses";
                break;

            case LINE_CATEGORY:
                String[] projection_cat = {"SUM(" + CategoriesReportContract.Columns.TOTAL_VALUE + ") AS " + CategoriesReportContract.Columns.TOTAL_VALUE,
                        CategoriesReportContract.Columns.EXPENSE_YEAR,
                        CategoriesReportContract.Columns.EXPENSE_MONTH};
                String selection_cat = CategoriesReportContract.Columns.CATEGORY_NAME + " = ?";
                String[] selectionArgs_cat = {categorySpinner.getSelectedItem().toString()};
                String sortOrder_cat = CategoriesReportContract.Columns.EXPENSE_YEAR + " , " + CategoriesReportContract.Columns.EXPENSE_MONTH;

                Uri uri_cat = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, "TOTAL_EXPENSES_MONTH_YEAR");

                cursor = cr.query(uri_cat, projection_cat, selection_cat, selectionArgs_cat, sortOrder_cat);

                if (cursor.moveToFirst()) {
                    String label;
                    for (float i = 0; i < cursor.getCount(); i++) {
                        valueSet1.add(new BarEntry(i, cursor.getFloat(cursor.getColumnIndex(CategoriesReportContract.Columns.TOTAL_VALUE))));
                        label = months[Integer.parseInt(cursor.getString(cursor.getColumnIndex(CategoriesReportContract.Columns.EXPENSE_MONTH))) - 1] + "/" + cursor.getString(cursor.getColumnIndex(CategoriesReportContract.Columns.EXPENSE_YEAR)).substring(2);
                        xLabelExpenseLine.add(label);
                        cursor.moveToNext();
                    }

                    XAxis xAxisTotal = expenseLineChart.getXAxis();
                    xAxisTotal.setValueFormatter(new IAxisValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, AxisBase axis) {
                            if (value >= 0f && value < cursor.getCount()) {
                                return xLabelExpenseLine.get((int) value);
                            } else {
                                return " ";
                            }
                        }
                    });
                }
                serieLabel = "Expenses";
                break;

            default:
                serieLabel = "";
        }

        LineDataSet dataSet = new LineDataSet(valueSet1, serieLabel);

        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setColor(Color.rgb(0, 0, 200));
        dataSet.setLineWidth(2f);

        return dataSet;
    }

    private BarDataSet getDataSet(int data) {
        ArrayList valueSet1 = new ArrayList();
        ContentResolver cr = getActivity().getContentResolver();
        final Cursor cursor;
        String serieLabel;

        switch (data) {

            case CATEGORY_EXPENSES:
                String[] projection = {"*"};
                String selection = CategoriesReportContract.Columns.EXPENSE_YEAR + " = ? AND " + CategoriesReportContract.Columns.EXPENSE_MONTH + " = ?";
                String[] selectionArgs = {"2019", "05"};
                String sortOrder = CategoriesReportContract.Columns.CATEGORY_NAME;

                cursor = cr.query(CategoriesReportContract.CONTENT_URI, projection, selection, selectionArgs, sortOrder);

                if (cursor.moveToFirst()) {
                    String catName;
                    String label;
                    Log.d(TAG, "getDataSet: size is " + xLabelBudget.size());
                    for (float i = 0; i < xLabelBudget.size(); i++) {
                        if (!xLabelBudget.isEmpty()) {
                            label = xLabelBudget.get((int) i);
                            catName = cursor.getString(cursor.getColumnIndex(CategoriesReportContract.Columns.CATEGORY_NAME));
                            Log.d(TAG, "getDataSet: category name " + catName);
                            if (catName.length() > 6) {
                                catName = catName.substring(0, 6);
                            }
                            if (label.equalsIgnoreCase(catName)) {
                                valueSet1.add(new BarEntry(i, cursor.getFloat(cursor.getColumnIndex(CategoriesReportContract.Columns.TOTAL_VALUE))));
                                Log.d(TAG, "getDataSet: value is " + cursor.getFloat(cursor.getColumnIndex(CategoriesReportContract.Columns.TOTAL_VALUE)));
                                cursor.moveToNext();
                            } else {
                                valueSet1.add(new BarEntry(i, 0));
                            }
                        }
                    }
                }
                serieLabel = "Expenses";
                break;

            case BUDGET:
                String[] projection_budget = {"*"};
                String sortOrder_budget = BudgetContract.Columns.CATEGORY_NAME;

                cursor = cr.query(BudgetContract.CONTENT_URI, projection_budget, null, null, sortOrder_budget);

                if (cursor.moveToFirst()) {
                    for (float i = 0; i < cursor.getCount(); i++) {
                        valueSet1.add(new BarEntry(i, cursor.getFloat(cursor.getColumnIndex(BudgetContract.Columns.TOTAL))));
                        cursor.moveToNext();
                    }

                    cursor.moveToFirst();
                    final XAxis xAxis = categoryChart.getXAxis();

                    String label;
                    for (float i = 0; i < cursor.getCount(); i++) {
                        label = cursor.getString(cursor.getColumnIndex(BudgetContract.Columns.CATEGORY_NAME));
                        if (label.length() > 6) {
                            label = label.substring(0, 6);
                        }
                        xLabelBudget.add(label);
                        cursor.moveToNext();
                    }
                    xAxis.setValueFormatter(new IAxisValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, AxisBase axis) {

                            Log.d(TAG, "getFormattedValue: max is " + axis.getAxisMaximum());
                            if (value >= 0f && value < cursor.getCount()) {
                                return xLabelBudget.get((int) value);
                            } else {
                                return " ";
                            }
                        }
                    });

                }
                serieLabel = "Budget";
                break;

            case TOTAL_EXPENSE_GRAPH:
                String[] projection_total = {"SUM(" + CategoriesReportContract.Columns.TOTAL_VALUE + ") AS " + CategoriesReportContract.Columns.TOTAL_VALUE,
                        CategoriesReportContract.Columns.EXPENSE_YEAR,
                        CategoriesReportContract.Columns.EXPENSE_MONTH};
                String selection_total = null;
                String[] selectionArgs_total = null;
                String sortOrder_total = CategoriesReportContract.Columns.EXPENSE_YEAR + ", " + CategoriesReportContract.Columns.EXPENSE_MONTH;

                Uri uri = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, "TOTAL_EXPENSES_MONTH_YEAR");
                cursor = cr.query(uri, projection_total, selection_total, selectionArgs_total, sortOrder_total);
                final int iTotal;
                int iInitial;

                if(cursor.getCount() > 8){
                    iTotal = 8;
                    iInitial = cursor.getCount() - 8;
                } else {
                    iTotal = cursor.getCount();
                    iInitial = 0;
                }


                if (cursor.moveToFirst()) {

                    if(iInitial != 0){
                        cursor.moveToPosition(iInitial);
                    }

                    for (float i = 0; i < iTotal ; i++) {
                        String label;
                        valueSet1.add(new BarEntry(i, cursor.getFloat(cursor.getColumnIndex(CategoriesReportContract.Columns.TOTAL_VALUE))));
                        label = months[Integer.parseInt(cursor.getString(cursor.getColumnIndex(CategoriesReportContract.Columns.EXPENSE_MONTH))) - 1] + cursor.getString(cursor.getColumnIndex(CategoriesReportContract.Columns.EXPENSE_YEAR)).substring(2);
                        Log.d(TAG, "getDataSet: " + label);
                        xLabelTotal.add(label);
//                        Log.d(TAG, "getDataSet: " + xLabelTotal.get((int) i));
                        cursor.moveToNext();
                    }

                    XAxis xAxisTotal = totalExpenseChart.getXAxis();
                    xAxisTotal.setValueFormatter(new IAxisValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, AxisBase axis) {
                            if (value >= 0f && value < iTotal) {
                                return xLabelTotal.get((int) value);
                            } else {
                                return " ";
                            }
                        }
                    });

                }

                serieLabel = "Expenses";
                break;

            default:
                serieLabel = "";
        }

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, serieLabel);
        switch (data) {
            case CATEGORY_EXPENSES:
                barDataSet1.setColor(Color.rgb(0, 0, 200));
                barDataSet1.setValueTextSize(8f);
                break;
            case BUDGET:
                barDataSet1.setColor(Color.rgb(180, 180, 180));
                barDataSet1.setValueTextSize(8f);
                break;
            case TOTAL_EXPENSE_GRAPH:
                barDataSet1.setColor(Color.rgb(0, 0, 200));
                barDataSet1.setValueTextSize(10f);
                break;
        }


        return barDataSet1;
    }

}
