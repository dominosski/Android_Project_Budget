package com.example.mybank.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String DB_NAME = "db_my_bank";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: started database helper");

        String createUserTable = "CREATE TABLE users  (_id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT NOT NULL," +
                " password TEXT NOT NULL," + "first_name TEXT, " + "last_name TEXT, " +
                "address TEXT, " + "image_url TEXT, " + "remained_amount DOUBLE)";

        String createShoppingTable = "CREATE TABLE shopping (_id INTEGER PRIMARY KEY AUTOINCREMENT, item_id INTEGER, " +
                                    "user_id INTEGER, transaction_id INTEGER, price DOUBLE, date DATE, description TEXT)";

        String createInvestmentTable = "CREATE TABLE investments (_id INTEGER PRIMARY KEY AUTOINCREMENT, amount DOUBLE, " +
                "monthly_roi DOUBLE, name TEXT, init_date DATE, finish_date DATE, user_id INTEGER, transaction_id INTEGER)";

        String createLoansTable = "CREATE TABLE loans (_id INTEGER PRIMARY KEY AUTOINCREMENT, init_date DATE, finish_date DATE," +
                "init_amount DOUBLE, remained_amount DOUBLE, monthly_payment DOUBLE, monthly_roi DOUBLE, name TEXT, user_id INTEGER)";

        String createTransactionTable = "CREATE TABLE transactions (_id INTEGER PRIMARY KEY AUTOINCREMENT, amount DOUBLE, date DATE," +
                "type TEXT, user_id INTEGER, recipient TEXT, description TEXT)";

        String createItemsTable = "CREATE TABLE items (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, image_url TEXT," +
                "description TEXT)";

        db.execSQL(createUserTable);
        db.execSQL(createShoppingTable);
        db.execSQL(createInvestmentTable);
        db.execSQL(createLoansTable);
        db.execSQL(createTransactionTable);
        db.execSQL(createItemsTable);

        addInitialItems(db);
        addTestTransaction(db);
        addTestShopping(db);
        addTestProfit(db);

    }

    private void addTestShopping(SQLiteDatabase dataBase) {
        Log.d(TAG, "addTestShopping: started");
        ContentValues firstValues = new ContentValues();
        firstValues.put("item_id", 1);
        firstValues.put("transaction_id", 1);
        firstValues.put("user_id", 0);
        firstValues.put("price", 10.0);
        firstValues.put("description", "First of all");
        firstValues.put("date", "2020-05-02");
        dataBase.insert("shopping", null, firstValues);

        ContentValues secondValues = new ContentValues();
        secondValues.put("item_id", 2);
        secondValues.put("transaction_id",2);
        secondValues.put("user_id", 0);
        secondValues.put("price", 15.0);
        secondValues.put("description", "Second of all");
        secondValues.put("date", "2020-05-02");
        dataBase.insert("shopping", null, secondValues);

        ContentValues thirdValues = new ContentValues();
        thirdValues.put("item_id",2);
        thirdValues.put("transaction_id", 3);
        thirdValues.put("user_id", 0);
        thirdValues.put("price", 16.0);
        thirdValues.put("description", "Third one");
        thirdValues.put("date", "2020-05-03");
        dataBase.insert("shopping", null, thirdValues);
    }

    private void addTestProfit(SQLiteDatabase db) {
        Log.d(TAG, "addTestProfit: started");
        ContentValues firstValues = new ContentValues();
        firstValues.put("amount", 25.0);
        firstValues.put("type", "profit");
        firstValues.put("date", "2020-09-28");
        firstValues.put("description", "monthly profit from ordering gears");
        firstValues.put("user_id", 1);
        firstValues.put("recipient", "AMD company");
        db.insert("transactions", null, firstValues);

        ContentValues secondValues = new ContentValues();
        secondValues.put("amount", 40.0);
        secondValues.put("type", "profit");
        secondValues.put("date", "2020-07-23");
        secondValues.put("description", "ordering food");
        secondValues.put("user_id", 1);
        secondValues.put("recipient", "Healthy food");
        db.insert("transactions", null, secondValues);
    }

    private void addTestTransaction(SQLiteDatabase db) {
        Log.d(TAG, "addTestTransaction: started");
        ContentValues values = new ContentValues();
        values.put("_id", 0);
        values.put("amount", 10.5);
        values.put("date", "2020-07-20");
        values.put("type", "shopping");
        values.put("user_id", 1);
        values.put("description", "Grocery shopping");
        values.put("recipient", "Walmart");

        long newTransactionId = db.insert("transactions", null, values);
        Log.d(TAG, "addTestTransaction: transaction id: " + newTransactionId);

    }

    private void addInitialItems(SQLiteDatabase db)
    {
        Log.d(TAG, "addInitialItems: started");
        ContentValues values = new ContentValues();
        values.put("name", "bike");
        values.put("description", "The perfect bike for you!");
        values.put("image_url", "https://images.internetstores.de/products//1089728/02/294799/NS_Bikes_Movement_1_26__black_splash[1920x1920].jpg?forceSize=false&forceAspectRatio=true&useTrim=true#xd_co_f=ZTY5ZjQwYTgtYzk2ZS00MGRmLWI4NzEtZDM0MGQxMzU3NDIz~");
        db.insert("items", null, values);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
