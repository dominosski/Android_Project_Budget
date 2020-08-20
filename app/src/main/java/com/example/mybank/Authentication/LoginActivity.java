package com.example.mybank.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mybank.Database.DatabaseHelper;
import com.example.mybank.MainActivity;
import com.example.mybank.Models.User;
import com.example.mybank.R;
import com.example.mybank.Utils;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private TextView txtWarning, txtRegister;
    private EditText edtTxtEmail, edtTxtPassword;
    private Button btnLogin;

    private DatabaseHelper databaseHelper;

    private LoginUser loginUser;
    private DoesEmailExist doesEmailExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initLogin();
            }
        });

    }

    private void initLogin()
    {
        Log.d(TAG, "initLogin: started");

        if(!edtTxtEmail.getText().toString().equals(""))
        {
            if(!edtTxtPassword.getText().toString().equals(""))
            {
                txtWarning.setVisibility(View.GONE);

                doesEmailExist = new DoesEmailExist();
                doesEmailExist.execute(edtTxtEmail.getText().toString());
            }else
            {
                txtWarning.setVisibility(View.VISIBLE);
                txtWarning.setText("Please enter your password");
            }
        }
        else
        {
            txtWarning.setVisibility(View.VISIBLE);
            txtWarning.setText("Please enter your email");
        }
    }

    private void initViews()
    {
        Log.d(TAG, "initViews: started");
        btnLogin = (Button)findViewById(R.id.btnLogin);

        edtTxtEmail = (EditText)findViewById(R.id.edtTxtEmail);
        edtTxtPassword = (EditText)findViewById(R.id.edtTxtPassword);

        txtWarning = (TextView)findViewById(R.id.txtWarning);
        txtRegister = (TextView)findViewById(R.id.txtRegister);
    }

    private class DoesEmailExist extends AsyncTask<String, Void, Boolean>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            databaseHelper = new DatabaseHelper(LoginActivity.this);

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                Cursor cursor = db.query("users", new String[] {"email"}, "email=?", new String[]
                        {strings[0]}, null, null, null);
                if(null!= cursor)
                {
                    if(cursor.moveToFirst())
                    {
                        cursor.close();
                        db.close();
                        return true;
                    }
                    else
                    {
                        cursor.close();
                        db.close();
                        return false;
                    }
                }
                else
                {
                    db.close();
                    return false;
                }
            }
            catch(SQLException x)
            {
                x.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(aBoolean)
            {
                loginUser = new LoginUser();
                loginUser.execute();
            }
            else
            {
                txtWarning.setVisibility(View.VISIBLE);
                txtWarning.setText("There is no such user with this email");
            }
        }
    }

    private class LoginUser extends AsyncTask<Void, Void, User>
    {
        private String email;
        private String password;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            this.email = edtTxtEmail.getText().toString();
            this.password = edtTxtPassword.getText().toString();
        }

        @Override
        protected User doInBackground(Void... voids) {

            try {
                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                Cursor cursor = db.query("users", null, "email=? AND password=?",
                        new String[] {email, password}, null, null, null);

                if(null!=cursor)
                {
                    if(cursor.moveToFirst())
                    {
                        User user = new User();
                        user.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                        user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                        user.setAddress(cursor.getString(cursor.getColumnIndex("address")));
                        user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
                        user.setFirst_name(cursor.getString(cursor.getColumnIndex("first_name")));
                        user.setLast_name(cursor.getString(cursor.getColumnIndex("last_name")));
                        user.setRemained_amount(cursor.getDouble(cursor.getColumnIndex("remained_amount")));

                        cursor.close();
                        db.close();
                        return user;
                    }
                }else
                {
                    return null;
                }
            }catch (SQLException x)
            {
                x.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);

            if(null!= user)
            {
                Utils utils = new Utils(LoginActivity.this);
                utils.addUserToSharedPreferences(user);

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else
            {
                txtWarning.setVisibility(View.VISIBLE);
                txtWarning.setText("Your password is incorrect");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(null!=doesEmailExist)
        {
            if(!doesEmailExist.isCancelled())
            {
                doesEmailExist.cancel(true);
            }
        }
        if(null != loginUser)
        {
            if(!loginUser.isCancelled())
            {
                loginUser.cancel(true);
            }
        }
    }
}