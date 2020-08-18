package com.example.mybank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mybank.Authentication.RegisterActivity;
import com.example.mybank.Database.DatabaseHelper;
import com.example.mybank.Models.User;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Utils utils;

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button)findViewById(R.id.button);

        utils = new Utils(this);
        User user = utils.isUserLoggedIn();

        if(null!=user)
        {
            Toast.makeText(this, "User: " + user.getFirst_name() + " logged in", Toast.LENGTH_SHORT).show();
        }else
        {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utils.signOutUser();
            }
        });

    }
}