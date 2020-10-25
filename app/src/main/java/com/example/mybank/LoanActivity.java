package com.example.mybank;

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

import com.example.mybank.Adapters.LoanAdapter;
import com.example.mybank.Database.DatabaseHelper;
import com.example.mybank.Models.Loan;
import com.example.mybank.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class LoanActivity extends AppCompatActivity {
    private static final String TAG = "LoanActivity";

    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;

    private LoanAdapter adapter;

    private DatabaseHelper databaseHelper;

    private GetLoans getLoans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan);

        initViews();
        initBottomNavView();

        adapter = new LoanAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);

        Utils utils = new Utils(this);
        User user = utils.isUserLoggedIn();
        if(null != user)
        {
            getLoans = new GetLoans();
            getLoans.execute(user.get_id());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(null != getLoans)
        {
            if(!getLoans.isCancelled())
            {
                getLoans.cancel(true);
            }
        }
    }

    private class GetLoans extends AsyncTask<Integer, Void, ArrayList<Loan>>
    {
        @Override
        protected ArrayList<Loan> doInBackground(Integer... integers) {

            try {
                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                Cursor cursor = db.query("loans", null, "user_id=?",
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
        bottomNavigationView.setSelectedItemId(R.id.menu_item_loans);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.menu_item_stats:
                        //TODO
                        break;
                    case R.id.menu_item_investment:
                        Intent investmentIntent = new Intent(LoanActivity.this, MainActivity.class);
                        investmentIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(investmentIntent);
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
}