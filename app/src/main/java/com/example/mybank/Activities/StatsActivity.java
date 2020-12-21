package com.example.mybank.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.mybank.Database.DatabaseHelper;
import com.example.mybank.Models.Loan;
import com.example.mybank.Models.Transaction;
import com.example.mybank.Models.User;
import com.example.mybank.R;
import com.example.mybank.Utils.Utils;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class StatsActivity extends AppCompatActivity {
    private static final String TAG = "StatsActivity";

    private BarChart barChart;
    private PieChart pieChart;
    private BottomNavigationView bottomNavigationView;

    private DatabaseHelper databaseHelper;
    private Utils utils;

    private GetLeasings getLeasings;
    private GetTransactions getTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        initViews();
        initBottomNavView();

        databaseHelper = new DatabaseHelper(this);
        utils = new Utils(this);

        User user = utils.isUserLoggedIn();
        if(null != user)
        {
            getTransactions = new GetTransactions();
            getTransactions.execute(user.get_id());
            getLeasings = new GetLeasings();
            getLeasings.execute(user.get_id());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(null != getLeasings)
        {
            if(!getLeasings.isCancelled())
            {
                getLeasings.cancel(true);
            }
        }
        if(getTransactions != null)
        {
            if(!getLeasings.isCancelled())
            {
                getLeasings.cancel(true);
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(StatsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private class GetTransactions extends AsyncTask<Integer, Void, ArrayList<Transaction>>
    {

        @Override
        protected ArrayList<Transaction> doInBackground(Integer... integers) {
            try {
                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                Cursor cursor = db.query("transactions", null, null, null, null, null, null);
                if(null != cursor)
                {
                    if(cursor.moveToFirst())
                    {
                        ArrayList<Transaction> transactions = new ArrayList<>();
                        for (int i = 0; i <cursor.getCount() ; i++) {
                            Transaction transaction = new Transaction();
                            transaction.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                            transaction.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
                            transaction.setType(cursor.getString(cursor.getColumnIndex("type")));
                            transaction.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                            transaction.setRecipient(cursor.getString(cursor.getColumnIndex("recipient")));
                            transaction.setDate(cursor.getString(cursor.getColumnIndex("date")));
                            transaction.setAmount(cursor.getDouble(cursor.getColumnIndex("amount")));

                            transactions.add(transaction);
                            cursor.moveToNext();
                        }
                        cursor.close();
                        db.close();
                        return transactions;
                    }
                    else
                    {
                        cursor.close();
                        db.close();
                        return null;
                    }
                }
                else
                {
                    db.close();
                    return null;
                }
            }
            catch (SQLException x)
            {
                x.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Transaction> transactions) {
            super.onPostExecute(transactions);

            if(null != transactions)
            {
                Calendar calendar = Calendar.getInstance();
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentYear = calendar.get(Calendar.YEAR);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                ArrayList<BarEntry> entries = new ArrayList<>();
                for(Transaction t: transactions)
                {
                    try {
                        Date date = sdf.parse(t.getDate());
                        calendar.setTime(date);
                        int month = calendar.get(Calendar.MONTH);
                        int year = calendar.get(Calendar.YEAR);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);

                        if(month == currentMonth && year == currentYear)
                        {
                            boolean doesDayExist = false;

                            for(BarEntry e: entries)
                            {
                                if(e.getX() == day)
                                {
                                    doesDayExist = true;
                                }
                                else
                                {
                                    doesDayExist = false;
                                }
                            }
                            if(doesDayExist)
                            {
                                for(BarEntry e: entries)
                                {
                                    if(e.getX() == day)
                                    {
                                        e.setY(e.getY() + (float)t.getAmount());
                                    }
                                }
                            }
                            else
                            {
                                entries.add(new BarEntry(day, (float)t.getAmount()));
                            }
                        }
                    }
                    catch (ParseException x)
                    {
                        x.printStackTrace();
                    }
                }
                for(BarEntry e: entries)
                {
                    Log.d(TAG, "onPostExecute: xx: " + e.getX() + " y: " + e.getY());
                }

                BarDataSet dataSet = new BarDataSet(entries, "All of the account transactions");
                dataSet.setColor(Color.GREEN);
                BarData data = new BarData(dataSet);
                barChart.getAxisRight().setEnabled(false);
                XAxis xAxis = barChart.getXAxis();
                xAxis.setAxisMaximum(31);
                xAxis.setEnabled(true);
                YAxis yAxis = barChart.getAxisLeft();
                yAxis.setDrawGridLines(false);
                barChart.setDescription(null);
                barChart.setData(data);
                barChart.animateY(1000);
                barChart.invalidate();
            }
        }
    }

    private class GetLeasings extends AsyncTask<Integer, Void, ArrayList<Loan>>
    {

        @Override
        protected ArrayList<Loan> doInBackground(Integer... integers) {
            try {
                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                Cursor cursor = db.query("leasings", null, null, null, null, null, null);
                if(null != cursor)
                {
                    if(cursor.moveToFirst())
                    {
                        ArrayList<Loan> loans = new ArrayList<>();
                        for (int i = 0; i <cursor.getCount() ; i++) {
                            Loan loan = new Loan();
                            loan.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                            loan.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
                            loan.setTransaction_id(cursor.getInt(cursor.getColumnIndex("transaction_id")));
                            loan.setName(cursor.getString(cursor.getColumnIndex("name")));
                            loan.setInit_date(cursor.getString(cursor.getColumnIndex("init_date")));
                            loan.setFinish_date(cursor.getString(cursor.getColumnIndex("finish_date")));
                            loan.setInit_amount(cursor.getDouble(cursor.getColumnIndex("init_amount")));
                            loan.setMonthly_roi(cursor.getDouble(cursor.getColumnIndex("monthly_roi")));
                            loan.setMonthly_payment(cursor.getDouble(cursor.getColumnIndex("monthly_payment")));
                            loan.setRemained_amount(cursor.getDouble(cursor.getColumnIndex("remained_amount")));
                            loans.add(loan);
                            cursor.moveToNext();
                        }
                        cursor.close();
                        db.close();
                        return loans;
                    }
                    else
                    {
                        cursor.close();
                        db.close();
                        return null;
                    }
                }
            }
            catch (SQLException x)
            {
                x.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Loan> loans) {
            super.onPostExecute(loans);

            if(null != loans)
            {
                ArrayList<PieEntry> entries = new ArrayList<>();
                double totalLeasingsAmount = 0.0;
                double totalRemainedAmount = 0.0;

                for(Loan l: loans)
                {
                    totalLeasingsAmount += l.getInit_amount();
                    totalRemainedAmount += l.getRemained_amount();
                }

                entries.add(new PieEntry((float)totalLeasingsAmount, "Total leasings"));
                entries.add(new PieEntry((float) totalRemainedAmount, "Remained leasings"));
                PieDataSet dataSet = new PieDataSet(entries, "");
                dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                dataSet.setSliceSpace(5f);
                PieData data = new PieData(dataSet);
                pieChart.setDrawHoleEnabled(false);
                pieChart.animateY(1000, Easing.EaseInOutCubic);
                pieChart.setData(data);
                pieChart.setDescription(null);
                pieChart.setCenterTextSize(15);
                pieChart.invalidate();
            }
        }
    }

    private void initBottomNavView()
    {
        Log.d(TAG, "initBottomNavView: started");
        bottomNavigationView.setSelectedItemId(R.id.menu_item_stats);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.menu_item_stats:

                        break;
                    case R.id.menu_item_notes:
                        Intent notesIntent = new Intent(StatsActivity.this, NotesActivity.class);
                        notesIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(notesIntent);
                        break;
                    case R.id.menu_item_leasings:
                        Intent leasingIntent = new Intent(StatsActivity.this, LeasingActivity.class);
                        leasingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(leasingIntent);
                        break;
                    case R.id.menu_item_transaction:
                        Intent transactionIntent = new Intent(StatsActivity.this, TransactionActivity.class);
                        transactionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(transactionIntent);
                        break;
                    case R.id.menu_item_home:
                        Intent homeIntent = new Intent(StatsActivity.this, MainActivity.class);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(homeIntent);
                        break;
                }

                return false;
            }
        });
    }

    private void initViews() {
        Log.d(TAG, "initViews: started");

        barChart = (BarChart)findViewById(R.id.barChartActivities);
        pieChart = (PieChart)findViewById(R.id.pieChartLoans);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavView);
    }
}