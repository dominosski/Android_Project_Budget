package com.example.mybank;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.mybank.Adapters.TransactionAdapter;
import com.example.mybank.Authentication.LoginActivity;
import com.example.mybank.Authentication.RegisterActivity;
import com.example.mybank.Database.DatabaseHelper;
import com.example.mybank.Dialogs.AddTransactionDialog;
import com.example.mybank.Models.Shopping;
import com.example.mybank.Models.Transaction;
import com.example.mybank.Models.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Utils utils;

    private BottomNavigationView bottomNavigationView;
    private TextView txtAmount, txtWelcome;
    private RecyclerView transactionRecView;
    private FloatingActionButton fbAddTransaction;
    private Toolbar toolbar;
    private BarChart barChart;
    private LineChart lineChart;

    private GetAccountAmount getAccountAmount;

    private DatabaseHelper databaseHelper;

    private TransactionAdapter adapter;

    private GetTransactions getTransactions;

    private GetProfit getProfit;

    private GetSpending getSpending;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initBottomNavView();
        setSupportActionBar(toolbar);

        utils = new Utils(this);
        User user = utils.isUserLoggedIn();

        if(null!=user)
        {
            Toast.makeText(this, "User: " + user.getFirst_name() + " logged in", Toast.LENGTH_SHORT).show();
        }else
        {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        databaseHelper = new DatabaseHelper(this);

        setupAmount();
        setOnClickListeners();
        initTransactionRecView();
        initLineChart();
        initBarChart();

    }

    private void initBarChart() {
        Log.d(TAG, "initBarChart: started");

        getSpending = new GetSpending();
        User user = utils.isUserLoggedIn();
        if(null!= user)
        {
            getSpending.execute(user.get_id());
        }
    }

    private void initLineChart() {
        Log.d(TAG, "initLineChart: started");

        getProfit = new GetProfit();
        User user = utils.isUserLoggedIn();
        if(null!= user)
        {
            getProfit.execute(user.get_id());
        }
    }


    private void getTransactions() {
        Log.d(TAG, "getTransactions: called");
        getTransactions = new GetTransactions();
        User user = utils.isUserLoggedIn();

        if(null!= user)
        {
            getTransactions.execute(user.get_id());

        }
    }

    private void initTransactionRecView()
    {
        Log.d(TAG, "initTransactionRecView: started");
        adapter = new TransactionAdapter();
        transactionRecView.setAdapter(adapter);
        transactionRecView.setLayoutManager(new LinearLayoutManager(this));
        getTransactions();
    }

    private void setOnClickListeners()
    {
        Log.d(TAG, "setOnClickListeners: started");
        txtWelcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("My Bank")
                        .setMessage("Created and developed by Dominik Kijowski")
                        .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();

            }
        });

        fbAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTransactionDialog addTransactionDialog = new AddTransactionDialog();
                addTransactionDialog.show(getSupportFragmentManager(), "add transaction dialog");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        setupAmount();
        getTransactions();
        initLineChart();
        initBarChart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        if(null!= getTransactions)
        {
            if(!getTransactions.isCancelled())
            {
                getTransactions.cancel(true);
            }
        }
        if(null!= getAccountAmount)
        {
            if(!getAccountAmount.isCancelled())
            {
                getAccountAmount.cancel(true);
            }
        }
        if(null!= getProfit)
        {
            if(!getProfit.isCancelled())
            {
                getProfit.cancel(true);
            }
        }
        if(null!=getSpending)
        {
            if(!getSpending.isCancelled())
            {
                getSpending.cancel(true);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        setupAmount();
        getTransactions();
        initLineChart();
        initBarChart();
}

    private void setupAmount()
    {
        Log.d(TAG, "setupAmount: started");
        User user = utils.isUserLoggedIn();
        if(null != user)
        {
            getAccountAmount = new GetAccountAmount();
            getAccountAmount.execute(user.get_id());
        }
    }
    private class GetAccountAmount extends AsyncTask<Integer, Void, Double>
    {
        @Override
        protected Double doInBackground(Integer... integers) {
            try {
                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                Cursor cursor = db.query("users", new String[] {"remained_amount"}, "_id=?",
                        new String[] {String.valueOf(integers[0])}, null, null, null);

                if (null!=cursor)
                {
                    if (cursor.moveToFirst())
                    {
                        double amount = cursor.getDouble(cursor.getColumnIndex("remained_amount"));
                        cursor.close();
                        db.close();
                        return amount;
                    }
                    else
                    {
                        cursor.close();
                        db.close();
                        return null;
                    }
                }else
                {
                    db.close();
                    return null;
                }

            }catch (SQLException x)
            {
                x.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(Double aDouble) {
            super.onPostExecute(aDouble);

            if(null != aDouble)
            {
                txtAmount.setText(String.valueOf(aDouble) + " $");
            }
            else
            {
                txtAmount.setText("0.0 $");
            }
        }
    }
    private class GetTransactions extends AsyncTask<Integer, Void, ArrayList<Transaction>>
    {
        @Override
        protected ArrayList<Transaction> doInBackground(Integer... integers) {
            try {
                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                Cursor cursor = db.query("transactions", null, "user_id=?",
                        new String[] {String.valueOf(integers[0])}, null, null, "date");
                if(null != cursor)
                {
                    if(cursor.moveToFirst())
                    {
                        ArrayList<Transaction> transactions = new ArrayList<>();
                        for (int i = 0; i < cursor.getCount() ; i++) {
                            Transaction transaction = new Transaction();
                            transaction.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                            transaction.setAmount(cursor.getDouble(cursor.getColumnIndex("amount")));
                            transaction.setDate(cursor.getString(cursor.getColumnIndex("date")));
                            transaction.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                            transaction.setRecipient(cursor.getString(cursor.getColumnIndex("recipient")));
                            transaction.setType(cursor.getString(cursor.getColumnIndex("type")));
                            transaction.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
                            transactions.add(transaction);
                            cursor.moveToNext();
                        }

                        cursor.close();
                        db.close();
                        return transactions;
                    }else
                    {
                        cursor.close();
                        db.close();
                        return null;

                    }
                }else
                {
                    db.close();
                    return null;
                }
            }catch (SQLException x)
            {
                x.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(ArrayList<Transaction> transactions) {
            super.onPostExecute(transactions);

            if(null!= transactions)
            {
                adapter.setTransactions(transactions);
            }
            else
            {
                adapter.setTransactions(new ArrayList<Transaction>());
            }
        }
    }
    private class GetProfit extends AsyncTask<Integer, Void, ArrayList<Transaction>>
    {
        @Override
        protected ArrayList<Transaction> doInBackground(Integer... integers) {
            try {
                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                Cursor cursor = db.query("transactions", null, "user_id=? AND type=?",
                        new String[] {String.valueOf(integers[0]),"profit"}, null, null, null);
                if(null != cursor)
                {
                    if(cursor.moveToFirst())
                    {
                        ArrayList<Transaction> transactions = new ArrayList<>();
                        for (int i = 0; i < cursor.getCount() ; i++)
                        {
                            Transaction transaction = new Transaction();
                            transaction.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                            transaction.setAmount(cursor.getDouble(cursor.getColumnIndex("amount")));
                            transaction.setDate(cursor.getString(cursor.getColumnIndex("date")));
                            transaction.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                            transaction.setRecipient(cursor.getString(cursor.getColumnIndex("recipient")));
                            transaction.setType(cursor.getString(cursor.getColumnIndex("type")));
                            transaction.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
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

            }catch (SQLException x)
            {
                x.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(ArrayList<Transaction> transactions) {
            super.onPostExecute(transactions);
            if (null != transactions)
            {
                ArrayList<Entry> entries = new ArrayList<>();

                for(Transaction t: transactions)
                {
                    try {
                        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(t.getDate());

                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        calendar.setTime(date);
                        int month = calendar.get(Calendar.MONTH);
                        Log.d(TAG, "onPostExecute: month: " + month);

                        if(calendar.get(Calendar.YEAR) == year)
                        {
                            boolean doesMonthExist = false;

                            for(Entry e: entries)
                            {
                                if(e.getX() == month)
                                {
                                    doesMonthExist = true;
                                }
                                else
                                {
                                    doesMonthExist = false;
                                }
                            }

                            if(!doesMonthExist)
                            {
                                entries.add(new Entry(month, (float)t.getAmount()));
                            }
                            else
                            {
                                for (Entry e: entries)
                                {
                                    if(e.getX() == month)
                                    {
                                        e.setY(e.getY() + (float) t.getAmount());
                                    }
                                }
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                for(Entry e: entries)
                {
                    Log.d(TAG, "onPostExecute: x: " + e.getX() + " y: " + e.getY());
                }

                LineDataSet dataSet = new LineDataSet(entries, "Profit chart");
                dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                dataSet.setDrawFilled(true);
                dataSet.setFillColor(Color.GREEN);
                LineData data = new LineData(dataSet);
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setSpaceMin(1);
                xAxis.setSpaceMax(1);
                xAxis.setAxisMaximum(12);
                xAxis.setEnabled(false);
                YAxis yAxis = lineChart.getAxisRight();
                yAxis.setEnabled(false);
                YAxis leftAxis = lineChart.getAxisLeft();
                leftAxis.setAxisMinimum(10);
                leftAxis.setAxisMaximum(100);
                leftAxis.setDrawGridLines(false);
                /*Description description = new Description();
                description.setText("Desc");*/
                lineChart.setDescription(null);
                lineChart.animateY(2000);
                lineChart.setData(data);
                lineChart.invalidate();
            }
            else
            {
                Log.d(TAG, "onPostExecute: transactions array list is null");
            }
        }
    }
    private class GetSpending extends AsyncTask<Integer, Void, ArrayList<Shopping>>
    {

        @Override
        protected ArrayList<Shopping> doInBackground(Integer... integers) {
            try {
                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                Cursor cursor = db.query("shopping", new String[]{"date", "price"}, "user_id=?", 
                        new String[]{String.valueOf(integers[0])}, null, null, null);

                if(null != cursor)
                {
                    if(cursor.moveToNext())
                    {
                        ArrayList<Shopping> shoppings = new ArrayList<>();
                        for (int i = 0; i < cursor.getCount() ; i++) {
                            Shopping shopping = new Shopping();
                            shopping.setDate(cursor.getString(cursor.getColumnIndex("date")));
                            shopping.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
                            shoppings.add(shopping);
                            cursor.moveToNext();
                        }

                        cursor.close();
                        db.close();
                        return shoppings;
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
            }catch (SQLException x)
            {
                x.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Shopping> shoppings) {
            super.onPostExecute(shoppings);

            if(null!=shoppings)
            {
                ArrayList<BarEntry> entries = new ArrayList<>();
                for(Shopping s: shoppings)
                {
                    try {
                        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(s.getDate());
                        Calendar calendar = Calendar.getInstance();
                        int month = calendar.get(Calendar.MONTH)+1;
                        calendar.setTime(date);
                        int day = calendar.get(Calendar.DAY_OF_MONTH)+1;

                        if((calendar.get(Calendar.MONTH)+1) == month)
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
                            if(!doesDayExist)
                            {
                                entries.add(new BarEntry(day, (float)s.getPrice()));
                            }
                            else
                            {
                                for(BarEntry e: entries)
                                {
                                    if(e.getX() == day)
                                    {
                                        e.setY(e.getY() + (float) + s.getPrice());
                                    }
                                }
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                BarDataSet dataSet = new BarDataSet(entries, "Shopping chart");
                BarData data = new BarData(dataSet);
                barChart.setData(data);
                barChart.invalidate();
            }
            else
            {
                Log.d(TAG, "onPostExecute: shoppings list is null");
            }
        }
    }


    private void setSupportActionBar(Toolbar toolbar) {
    }

    private void initBottomNavView()
    {
        Log.d(TAG, "initBottomNavView: started");
        bottomNavigationView.setSelectedItemId(R.id.menu_item_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.menu_item_stats:
                    //TODO
                    break;
                    case R.id.menu_item_investment:

                        break;
                    case R.id.menu_item_loans:

                        break;
                    case R.id.menu_item_transaction:

                        break;
                    case R.id.menu_item_home:

                        break;
                }


                return false;
            }
        });
    }

    private void initViews()
    {
        Log.d(TAG, "initViews: started");

        transactionRecView = (RecyclerView)findViewById(R.id.transactionRecView);
        txtAmount = (TextView)findViewById(R.id.txtAmount);
        txtWelcome = (TextView)findViewById(R.id.txtWelcome);
        barChart = (BarChart)findViewById(R.id.profitChart);
        lineChart = (LineChart)findViewById(R.id.dailySpentChart);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavView);
        fbAddTransaction = (FloatingActionButton)findViewById(R.id.fbAddTransaction);
    }
}