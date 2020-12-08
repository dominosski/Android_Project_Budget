package com.example.mybank.Models;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mybank.Adapters.ItemsAdapter;
import com.example.mybank.Database.DatabaseHelper;
import com.example.mybank.MainActivity;
import com.example.mybank.R;
import com.example.mybank.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class ShoppingActivity extends AppCompatActivity{
    private static final String TAG = "ShoppingActivity";

    private Calendar calendar = Calendar.getInstance();

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            edtTxtDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        }
    };

    private TextView txtWarning, txtItemName;
    private Button btnPickDate, btnAdd;
    private EditText edtTxtDate, edtTxtDesc, edtTxtPrice, edtTxtStore, edtTxtName;

    private DatabaseHelper databaseHelper;

    private AddShopping addShopping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);

        initViews();

        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ShoppingActivity.this, dateSetListener, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAdd();
            }
        });
    }

    private void initAdd()
    {
        Log.d(TAG, "initAdd: started");
        if(!edtTxtName.getText().toString().equals(""))
        {
            if(!edtTxtPrice.getText().toString().equals(""))
            {
                if(!edtTxtDate.getText().toString().equals(""))
                {
                    addShopping = new AddShopping();
                    addShopping.execute();
                }
                else
                {
                    txtWarning.setVisibility(View.VISIBLE);
                    txtWarning.setText("Please select a date");
                }
            }
            else
            {
                txtWarning.setVisibility(View.VISIBLE);
                txtWarning.setText("Please add a price");
            }
        }
        else
        {
            txtWarning.setVisibility(View.VISIBLE);
            txtWarning.setText("Please add outcome's name");
        }
    }

    private void initViews() {
        Log.d(TAG, "initViews: started");

        txtWarning = (TextView) findViewById(R.id.txtWarning);
        txtItemName = (TextView) findViewById(R.id.txtItemName);
        btnPickDate = (Button)findViewById(R.id.btnPickDate);
        btnAdd = (Button)findViewById(R.id.btnAdd);

        edtTxtDate = (EditText)findViewById(R.id.edtTxtDate);
        edtTxtDesc = (EditText)findViewById(R.id.edtTxtDesc);
        edtTxtPrice = (EditText)findViewById(R.id.edtTxtPrice);
        edtTxtStore = (EditText)findViewById(R.id.edtTxtStore);
        edtTxtName = (EditText)findViewById(R.id.edtTxtOutcomeName);
    }
    private class AddShopping extends AsyncTask<Void, Void, Void>
    {
        private User loggedInUser;
        private String date;
        private double price;
        private String store;
        private String description;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Utils utils = new Utils(ShoppingActivity.this);
            loggedInUser = utils.isUserLoggedIn();
            this.date = edtTxtDate.getText().toString();
            this.price = Double.valueOf(edtTxtPrice.getText().toString());
            this.store = edtTxtStore.getText().toString();
            this.description = edtTxtDesc.getText().toString();

            databaseHelper = new DatabaseHelper(ShoppingActivity.this);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                ContentValues transactionValue = new ContentValues();
                transactionValue.put("amount", -price);
                transactionValue.put("description", description);
                transactionValue.put("user_id", loggedInUser.get_id());
                transactionValue.put("type", "shopping");
                transactionValue.put("date", date);
                transactionValue.put("recipient", store);
                long id = db.insert("transactions", null, transactionValue);

                ContentValues shoppingValues = new ContentValues();
                shoppingValues.put("transaction_id", id);
                shoppingValues.put("user_id", loggedInUser.get_id());
                shoppingValues.put("price", -price);
                shoppingValues.put("description", description);
                shoppingValues.put("date", date);
                long shoppingId = db.insert("shopping", null, shoppingValues);
                Log.d(TAG, "doInBackground: shopping id: " + shoppingId);

                Cursor cursor = db.query("users", new String[] {"remained_amount"}, "_id=?",
                        new String[]{String.valueOf(loggedInUser.get_id())}, null,null,null);

                if(null != cursor)
                {
                    if(cursor.moveToFirst())
                    {
                        double remainedAmount = cursor.getDouble(cursor.getColumnIndex("remained_amount"));
                        ContentValues amountValues = new ContentValues();
                        amountValues.put("remained_amount", remainedAmount-price);
                        int affectedRows = db.update("users", amountValues, "_id=?",
                                new String[]{String.valueOf(loggedInUser.get_id())});
                        Log.d(TAG, "doInBackground: affected rows: " + affectedRows);
                    }
                    cursor.close();
                }
                db.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Toast.makeText(ShoppingActivity.this, edtTxtName.getText().toString() + " added successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ShoppingActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != addShopping)
        {
            if(!addShopping.isCancelled())
            {
                addShopping.cancel(true);
            }
        }
    }
}