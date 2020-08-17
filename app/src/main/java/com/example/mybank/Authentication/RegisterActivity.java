package com.example.mybank.Authentication;

import androidx.appcompat.app.AppCompatActivity;

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
import com.example.mybank.R;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private TextView txtWarning, txtLogin;
    private Button btnRegister;
    private EditText edtTxtEmail, edtTextName, edtTxtAddress, edtTxtPassword;

    private DatabaseHelper databaseHelper;

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

    }

    private void initRegister()
    {
        Log.d(TAG, "initRegister: started");

        String email = edtTxtEmail.getText().toString();
        String password = edtTxtPassword.getText().toString();

        if(email.equals("") || password.equals(""))
        {
            txtWarning.setVisibility(View.VISIBLE);
            txtWarning.setText("Please enter Email and Password");
        }
        else
        {
            txtWarning.setVisibility(View.GONE);
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
                
            }
        }
    }


    private void initViews() {
        Log.d(TAG, "initViews: started");
        
        btnRegister = (Button)findViewById(R.id.btnRegister);

        edtTextName = (EditText)findViewById(R.id.edtTxtName);
        edtTxtAddress = (EditText)findViewById(R.id.edtTxtAddress);
        edtTxtEmail = (EditText)findViewById(R.id.edtTxtEmail);
        edtTxtPassword = (EditText)findViewById(R.id.edtTxtPassword);

        txtLogin = (TextView)findViewById(R.id.txtLogin);
        txtWarning = (TextView)findViewById(R.id.txtWarning);
    }


}