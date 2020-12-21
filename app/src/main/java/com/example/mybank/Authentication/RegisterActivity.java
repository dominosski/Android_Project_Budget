package com.example.mybank.Authentication;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybank.Activities.ProjectActivity;
import com.example.mybank.Database.DatabaseHelper;
import com.example.mybank.Models.User;
import com.example.mybank.R;
import com.example.mybank.Utils.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private TextView txtWarning, txtLogin;
    private Button btnRegister;
    private EditText edtTxtEmail, edtTextName, edtTxtPassword;

    private DatabaseHelper databaseHelper;

    private DoesUserExist doesUserExist;
    private RegisterUser registerUser;
    private String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        databaseHelper = new DatabaseHelper(this);

        initViews();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initRegister();
            }
        });
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initRegister()
    {
        Log.d(TAG, "initRegister: started");

        String email = edtTxtEmail.getText().toString();
        String password = edtTxtPassword.getText().toString();

        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher m = pattern.matcher(password);

        if(email.equals("") || password.equals(""))
        {
            txtWarning.setVisibility(View.VISIBLE);
            txtWarning.setText("Please enter Email and Password");
        }
        else if(m.matches() == false)
        {
            txtWarning.setVisibility(View.VISIBLE);
            txtWarning.setText("Password must contain at least 1 lower case and 1 upper case letter, 1 number, 1 special character, and must be 8-20 characters long.");
        }
        else
        {
            txtWarning.setVisibility(View.GONE);

            doesUserExist = new DoesUserExist();
            doesUserExist.execute(email);
        }
    }

    private class DoesUserExist extends AsyncTask<String, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                Cursor cursor = db.query("users", new String[] {"_id", "email"}, "email=?",
                        new String[] {strings[0]}, null, null, null);
                if(null!= cursor)
                {
                    if(cursor.moveToFirst())
                    {
                        if(cursor.getString(cursor.getColumnIndex("email")).equals(strings[0]))
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
                        cursor.close();
                        db.close();
                        return false;
                    }

                }
                else
                {
                    db.close();
                    return true;
                }
            }
            catch(SQLException x)
            {
                x.printStackTrace();
                return true;
            }

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(aBoolean)
            {
                txtWarning.setVisibility(View.VISIBLE);
                txtWarning.setText("There is a user with this email, please try another email");
            }
            else
            {
                txtWarning.setVisibility(View.GONE);
                registerUser = new RegisterUser();
                registerUser.execute();
            }
        }
    }

    private class RegisterUser extends AsyncTask<Void,Void, User>
    {
        private String email;
        private String password;
        private String first_name;
        private String last_name;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            String email = edtTxtEmail.getText().toString();
            String password = edtTxtPassword.getText().toString();
            String name = edtTextName.getText().toString();

            this.email = email;
            this.password = email;
            this.password = password;

            String[] names = name.split(" ");
            if(names.length >=1)
            {
                this.first_name = names[0];
                for (int i = 1; i < names.length ; i++) {
                    if(i > 1)
                    {
                        last_name += " " + names[i];
                    }else
                    {
                        last_name += names[i];
                    }
                    
                }
            }
            else
            {
                this.first_name = names[0];
            }
        }

        @Override
        protected User doInBackground(Void... voids) {
            try {
                SQLiteDatabase db = databaseHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put("email", this.email);
                values.put("password", this.password);
                values.put("first_name", this.first_name);
                values.put("last_name", this.last_name);
                values.put("remained_amount", 0.0);

                long userId = db.insert("users", null, values);
                Log.d(TAG, "doInBackground: userId");

                Cursor cursor = db.query("users",null, "_id=?", new String[] {String.valueOf(userId)},
                null,null,null);

                if(null!= cursor)
                {
                    if(cursor.moveToFirst())
                    {
                        User user = new User();
                        user.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                        user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                        user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
                        user.setFirst_name(cursor.getString(cursor.getColumnIndex("first_name")));
                        user.setLast_name(cursor.getString(cursor.getColumnIndex("last_name")));
                        user.setRemained_amount(cursor.getDouble(cursor.getColumnIndex("remained_amount")));

                        cursor.close();
                        db.close();
                        return user;
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
        protected void onPostExecute(User user) {
            super.onPostExecute(user);

            if(null!= user)
            {
                Toast.makeText(RegisterActivity.this,"User " + user.getEmail() + " registered succesfully",Toast.LENGTH_SHORT).show();
                Utils utils = new Utils(RegisterActivity.this);
                utils.addUserToSharedPreferences(user);
                Intent intent = new Intent(RegisterActivity.this, ProjectActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
            else
            {
                Toast.makeText(RegisterActivity.this, "Was not able to register, please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void initViews() {
        Log.d(TAG, "initViews: started");
        
        btnRegister = (Button)findViewById(R.id.btnRegister);

        edtTextName = (EditText)findViewById(R.id.edtTxtName);
        edtTxtEmail = (EditText)findViewById(R.id.edtTxtEmail);
        edtTxtPassword = (EditText)findViewById(R.id.edtTxtPassword);

        txtLogin = (TextView)findViewById(R.id.txtLogin);
        txtWarning = (TextView)findViewById(R.id.txtWarning);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(null!= doesUserExist)
        {
            if(!doesUserExist.isCancelled())
            {
                doesUserExist.cancel(true);
            }
            if(!registerUser.isCancelled())
            {
                registerUser.cancel(true);
            }
        }
    }
}