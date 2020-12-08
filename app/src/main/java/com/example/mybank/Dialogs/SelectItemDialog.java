package com.example.mybank.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybank.Database.DatabaseHelper;
import com.example.mybank.Models.Item;
import com.example.mybank.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SelectItemDialog extends DialogFragment{
    private static final String TAG = "SelectItemDialog";

    private EditText edtTxtItemName;
    private RecyclerView itemRecView;


    private DatabaseHelper databaseHelper;

    private GetAllItems getAllItems;
    private SearchForItems searchForItems;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_select_item, null);

        edtTxtItemName = (EditText)view.findViewById(R.id.edtTxtItemName);
        itemRecView = (RecyclerView) view.findViewById(R.id.itemRecView);

        databaseHelper = new DatabaseHelper(getActivity());

        edtTxtItemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchForItems = new SearchForItems();
                searchForItems.execute(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getAllItems = new GetAllItems();
        getAllItems.execute();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Select an Item");

        return builder.create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=getAllItems)
        {
            if(!getAllItems.isCancelled())
            {
                getAllItems.cancel(true);
            }
        }
    }


    private class GetAllItems extends AsyncTask<Void, Void, ArrayList<Item>>
    {
        @Override
        protected ArrayList<Item> doInBackground(Void... voids) {
            try {
                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                Cursor cursor = db.query("items", null, null, null, null, null, null, null);
                if(cursor != null)
                {
                    if(cursor.moveToNext())
                    {
                        ArrayList<Item> items = new ArrayList<>();
                        for (int i = 0; i < cursor.getCount() ; i++) {
                            Item item = new Item();
                            item.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                            item.setName(cursor.getString(cursor.getColumnIndex("name")));
                            item.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                            items.add(item);
                            cursor.moveToNext();
                        }

                        cursor.close();
                        db.close();
                        return items;
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
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(ArrayList<Item> items) {
            super.onPostExecute(items);
        }
    }

    private class SearchForItems extends AsyncTask<String, Void, ArrayList<Item>>
    {
        @Override
        protected ArrayList<Item> doInBackground(String... strings) {
            try {
                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                Cursor cursor = db.query("items", null, "name LIKE ?",
                        new String[] {strings[0]}, null, null, null);
                if(null!= cursor)
                {
                    if(cursor.moveToFirst())
                    {
                        ArrayList<Item> items = new ArrayList<>();
                        for (int i = 0; i < cursor.getCount() ; i++) {
                            Item item = new Item();
                            item.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                            item.setName(cursor.getString(cursor.getColumnIndex("name")));
                            item.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                            items.add(item);
                            cursor.moveToNext();
                        }

                        cursor.close();
                        db.close();
                        return items;
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
            }catch (SQLException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(ArrayList<Item> items) {
            super.onPostExecute(items);
        }
    }

}
