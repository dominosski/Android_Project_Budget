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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.mybank.Adapters.TransactionAdapter;
import com.example.mybank.Database.DatabaseHelper;
import com.example.mybank.Models.Transaction;
import com.example.mybank.Models.User;
import com.example.mybank.R;
import com.example.mybank.Utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class TransactionActivity extends AppCompatActivity {
    private static final String TAG = "TransactionActivity";

    private RadioGroup rgType;
    private Button btnSearch;
    private EditText edtTxtMin;
    private RecyclerView transactionRecView;
    private TextView txtNoTransaction;
    private BottomNavigationView bottomNavigationView;

    private TransactionAdapter adapter;

    private DatabaseHelper databaseHelper;

    private GetTransactions getTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        initViews();
        initBottomNavView();

        adapter = new TransactionAdapter();

        transactionRecView.setAdapter(adapter);
        transactionRecView.setLayoutManager(new LinearLayoutManager(this));
        databaseHelper = new DatabaseHelper(this);

        initSearch();
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtTxtMin.getText().toString().isEmpty())
                {
                    edtTxtMin.setText("0.0");
                }
                initSearch();
            }
        });
        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                initSearch();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(TransactionActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    private void initSearch() {
        Log.d(TAG, "initSearch: started");

        Utils utils = new Utils(this);
        User user = utils.isUserLoggedIn();
        if(null != user)
        {
            getTransactions = new GetTransactions();
            getTransactions.execute(user.get_id());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != getTransactions)
        {
            if(!getTransactions.isCancelled())
            {
                getTransactions.cancel(true);
            }
        }
    }

    private class GetTransactions extends AsyncTask<Integer, Void, ArrayList<Transaction>> {
        private String type = "all";
        private double min = 0.0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            this.min = Double.valueOf(edtTxtMin.getText().toString());

            switch (rgType.getCheckedRadioButtonId()) {
                case R.id.rbLoan:
                    type = "loan";
                    break;
                case R.id.rbLeasingPayment:
                    type = "leasing_payment";
                    break;
                case R.id.rbProfit:
                    type = "profit";
                    break;
                case R.id.rbShopping:
                    type = "shopping";
                    break;
                case R.id.rbSend:
                    type = "send";
                    break;
                default:
                    type = "all";
                    break;
            }
        }

        @Override
        protected ArrayList<Transaction> doInBackground(Integer... integers) {
            try {
                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                Cursor cursor;
                if(type.equals("all"))
                {
                    cursor = db.query("transactions", null, "user_id=?",
                            new String[] {String.valueOf(integers[0])}, null, null, "date DESC");
                }
                else
                {
                    cursor = db.query("transactions", null, "type=? AND user_id=?",
                            new String[] {type, String.valueOf(integers[0])}, null, null, "date DESC");
                }

                if(null != cursor)
                {
                    if(cursor.moveToFirst())
                    {
                        ArrayList<Transaction> transactions = new ArrayList<>();
                        for (int i = 0; i < cursor.getCount() ; i++) {
                            Transaction transaction = new Transaction();
                            transaction.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                            transaction.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
                            transaction.setType(cursor.getString(cursor.getColumnIndex("type")));
                            transaction.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                            transaction.setRecipient(cursor.getString(cursor.getColumnIndex("recipient")));
                            transaction.setDate(cursor.getString(cursor.getColumnIndex("date")));
                            transaction.setAmount(cursor.getDouble(cursor.getColumnIndex("amount")));

                            double absoluteAmount = transaction.getAmount();
                            if(absoluteAmount < 0)
                            {
                                absoluteAmount = -absoluteAmount;
                            }

                            if(absoluteAmount > min)
                            {
                                transactions.add(transaction);
                            }
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

            if(null != transactions)
            {
                txtNoTransaction.setVisibility(View.GONE);
                adapter.setTransactions(transactions);
            }
            else
            {
                txtNoTransaction.setVisibility(View.VISIBLE);
                adapter.setTransactions(new ArrayList<Transaction>());
            }
        }
    }

    private void initBottomNavView() {
        Log.d(TAG, "initBottomNavView: started");
        bottomNavigationView.setSelectedItemId(R.id.menu_item_transaction);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menu_item_stats:
                        Intent statsIntent = new Intent(TransactionActivity.this, StatsActivity.class);
                        statsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(statsIntent);
                        break;
                    case R.id.menu_item_notes:
                        Intent intent = new Intent(TransactionActivity.this, NotesActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                    case R.id.menu_item_leasings:
                        Intent loanIntent = new Intent(TransactionActivity.this, LeasingActivity.class);
                        loanIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(loanIntent);
                        break;
                    case R.id.menu_item_transaction:

                        break;
                    case R.id.menu_item_home:
                        Intent homeIntent = new Intent(TransactionActivity.this, MainActivity.class);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(homeIntent);
                        break;
                }

                return false;
            }
        });
    }

    private void initViews() {
        Log.d(TAG, "initViews: started");

        rgType = (RadioGroup) findViewById(R.id.rgType);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        edtTxtMin = (EditText) findViewById(R.id.edtTxtMin);
        transactionRecView = (RecyclerView) findViewById(R.id.transactionRecView);
        txtNoTransaction = (TextView) findViewById(R.id.txtNoTransaction);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView);
    }
}