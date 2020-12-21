package com.example.mybank.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mybank.Adapters.NotesAdapter;
import com.example.mybank.Database.DatabaseHelper;
import com.example.mybank.Models.Note;
import com.example.mybank.Models.User;
import com.example.mybank.R;
import com.example.mybank.Utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class NotesActivity extends AppCompatActivity {
    private static final String TAG = "ReminderActivity";

    private DatabaseHelper databaseHelper;
    private Button btnAddNote;
    private RecyclerView notesRecView;
    private BottomNavigationView bottomNavigationView;
    private TextView txtNotes;

    private NotesAdapter adapter;

    private GetNotes getNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        databaseHelper = new DatabaseHelper(this);

        initViews();

        initNotesRecView();

        initBottomNavView();

        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAdding();
            }
        });
    }

    private void initAdding() {
        Log.d(TAG, "initAdding: started");
        Intent intent = new Intent(NotesActivity.this, AddNoteActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void initBottomNavView() {
        Log.d(TAG, "initBottomNavView: started");
        bottomNavigationView.setSelectedItemId(R.id.menu_item_notes);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.menu_item_stats:
                        Intent statsIntent = new Intent(NotesActivity.this, StatsActivity.class);
                        statsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(statsIntent);
                        break;
                    case R.id.menu_item_notes:

                        break;
                    case R.id.menu_item_leasings:
                        Intent leasingIntent = new Intent(NotesActivity.this, LeasingActivity.class);
                        leasingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(leasingIntent);
                        break;
                    case R.id.menu_item_transaction:
                        Intent transactionIntent = new Intent(NotesActivity.this, TransactionActivity.class);
                        transactionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(transactionIntent);
                        break;
                    case R.id.menu_item_home:
                        Intent homeIntent = new Intent(NotesActivity.this, MainActivity.class);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(homeIntent);
                        break;
                }

                return false;
            }
        });
    }

    private void initNotesRecView() {
        Log.d(TAG, "initNotesRecView: started");

        adapter = new NotesAdapter();
        notesRecView.setAdapter(adapter);
        notesRecView.setLayoutManager(new LinearLayoutManager(this));
        getNotes();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(null!= getNotes)
        {
            if(!getNotes.isCancelled())
            {
                getNotes.cancel(true);
            }
        }
    }

    private void getNotes() {
        Utils utils = new Utils(this);
        User user = utils.isUserLoggedIn();
        if(null != user)
        {
            getNotes = new GetNotes();
            getNotes.execute(user.get_id());
        }
    }

    private void initViews() {
        Log.d(TAG, "initViews: started");

        notesRecView = (RecyclerView)findViewById(R.id.notesRecView);
        btnAddNote = (Button)findViewById(R.id.btnAddNote);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavView);
        txtNotes = (TextView)findViewById(R.id.txtNoNotes);
    }

    private class GetNotes extends AsyncTask<Integer, Void, ArrayList<Note>> {

        @Override
        protected ArrayList<Note> doInBackground(Integer... integers) {

            try {
                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                Cursor cursor = db.query("notes", null, "user_id=?",
                        new String[]{String.valueOf(integers[0])}, null, null, null);
                if(null!=cursor)
                {
                    if(cursor.moveToFirst())
                    {
                        ArrayList<Note> notes = new ArrayList<>();
                        for (int i = 0; i < cursor.getCount() ; i++) {
                            Note note = new Note();
                            note.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                            note.setDate(cursor.getString(cursor.getColumnIndex("date")));
                            note.setName(cursor.getString(cursor.getColumnIndex("name")));
                            note.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                            note.setPriority(cursor.getString(cursor.getColumnIndex("priority")));
                            note.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
                            notes.add(note);
                            cursor.moveToNext();
                        }
                        cursor.close();
                        db.close();
                        return notes;
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
            catch (SQLException x)
            {
                x.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Note> notes) {
            super.onPostExecute(notes);

            if(null!= notes)
            {
                txtNotes.setVisibility(View.GONE);
                adapter.setNotes(notes);
            }
            else
            {
                txtNotes.setVisibility(View.VISIBLE);
                adapter.setNotes(new ArrayList<Note>());
            }
        }
    }
}