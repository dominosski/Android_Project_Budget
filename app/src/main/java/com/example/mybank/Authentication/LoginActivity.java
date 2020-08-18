package com.example.mybank.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mybank.R;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private TextView txtWarning, txtRegister;
    private EditText edtTxtEmail, edtTxtPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();

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
}