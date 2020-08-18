package com.example.mybank;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.mybank.Models.User;
import com.google.gson.Gson;

public class Utils {
    private static final String TAG = "Utils";

    private Context context;

    public Utils(Context context) {
        this.context = context;
    }
    
    public void addUserToSharedPreferences( User user)
    {
        Log.d(TAG, "addUserToSharedPreferences: adding " + user.toString());
        SharedPreferences sharedPreferences = context.getSharedPreferences("logged_in_user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        Gson gson = new Gson();
        editor.putString("user", gson.toJson(user));
        editor.apply();

    }
}
