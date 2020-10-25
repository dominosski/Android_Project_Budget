package com.example.mybank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.mybank.Adapters.LoanAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LoanActivity extends AppCompatActivity {
    private static final String TAG = "LoanActivity";

    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;

    private LoanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan);

        initViews();
        initBottomNavView();

        adapter = new LoanAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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