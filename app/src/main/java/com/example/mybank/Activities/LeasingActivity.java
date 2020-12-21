package com.example.mybank.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.mybank.Adapters.LeasingAdapter;
import com.example.mybank.Database.DatabaseHelper;
import com.example.mybank.Models.Loan;
import com.example.mybank.Models.User;
import com.example.mybank.R;
import com.example.mybank.Utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class LeasingActivity extends AppCompatActivity {
    private static final String TAG = "LeasingActivity";

    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;

    private LeasingAdapter adapter;

    private DatabaseHelper databaseHelper;

    private GetLeasings getLeasings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan);

        initViews();
        initBottomNavView();

        adapter = new LeasingAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);

        Utils utils = new Utils(this);
        User user = utils.isUserLoggedIn();
        if(null != user)
        {
            getLeasings = new GetLeasings();
            getLeasings.execute(user.get_id());
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(LeasingActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
    }

    private class GetLeasings extends AsyncTask<Integer, Void, ArrayList<Loan>>
    {
        @Override
        protected ArrayList<Loan> doInBackground(Integer... integers) {

            try {
                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                Cursor cursor = db.query("leasings", null, "user_id=?",
                        new String[] {String.valueOf(integers[0])}, null, null, "init_date DESC");
                if(cursor!=null)
                {
                    if(cursor.moveToFirst())
                    {
                        ArrayList<Loan> loans = new ArrayList<>();
                        for (int i = 0; i <cursor.getCount(); i++) {
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
        protected void onPostExecute(ArrayList<Loan> loans) {
            super.onPostExecute(loans);

            if(null != loans)
            {
                adapter.setLoans(loans);
            }
            else
            {
                adapter.setLoans(new ArrayList<Loan>());
            }
        }
    }
    private void initViews() {
        Log.d(TAG, "initViews: started");

        recyclerView = (RecyclerView)findViewById(R.id.loanRecView);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavView);
    }
    private void initBottomNavView()
    {
        Log.d(TAG, "initBottomNavView: started");
        bottomNavigationView.setSelectedItemId(R.id.menu_item_leasings);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.menu_item_stats:
                        Intent statsIntent = new Intent(LeasingActivity.this, StatsActivity.class);
                        statsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(statsIntent);
                        break;
                    case R.id.menu_item_notes:
                        Intent investmentIntent = new Intent(LeasingActivity.this, NotesActivity.class);
                        investmentIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(investmentIntent);
                        break;
                    case R.id.menu_item_leasings:

                        break;
                    case R.id.menu_item_transaction:
                        Intent transactionIntent = new Intent(LeasingActivity.this, TransactionActivity.class);
                        transactionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(transactionIntent);
                        break;
                    case R.id.menu_item_home:
                        Intent homeIntent = new Intent(LeasingActivity.this, MainActivity.class);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(homeIntent);
                        break;
                }


                return false;
            }
        });
    }
}