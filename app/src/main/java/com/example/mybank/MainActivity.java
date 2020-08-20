package com.example.mybank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.mybank.Authentication.LoginActivity;
import com.example.mybank.Authentication.RegisterActivity;
import com.example.mybank.Database.DatabaseHelper;
import com.example.mybank.Models.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

    private DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        utils = new Utils(this);
        User user = utils.isUserLoggedIn();

        initViews();
        initBottomNavView();
        setSupportActionBar(toolbar);

        if(null!=user)
        {
            Toast.makeText(this, "User: " + user.getFirst_name() + " logged in", Toast.LENGTH_SHORT).show();
        }else
        {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        setupAmount();

    }
    private void setupAmount()
    {
        Log.d(TAG, "setupAmount: started");
        User user = utils.isUserLoggedIn();
        if(null != user)
        {

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
                

            }catch (SQLException x)
            {
                x.printStackTrace();
            }
            return null;
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