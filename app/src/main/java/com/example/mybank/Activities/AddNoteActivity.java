package com.example.mybank.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybank.Database.DatabaseHelper;
import com.example.mybank.Models.User;
import com.example.mybank.R;
import com.example.mybank.Utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddNoteActivity extends AppCompatActivity {
    private static final String TAG = "AddNoteActivity";

    private EditText edtTxtNoteName, edtTxtDate, edtTxtDescription;
    private RadioGroup rbGroup;
    private Button btnAddNote, btnPickDate;
    private TextView txtWarning;

    private DatabaseHelper databaseHelper;
    private User user;
    private Utils utils;

    private Calendar dateCalendar = Calendar.getInstance();

    private DatePickerDialog.OnDateSetListener initDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            dateCalendar.set(Calendar.YEAR, year);
            dateCalendar.set(Calendar.MONTH, month);
            dateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            edtTxtDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(dateCalendar.getTime()));
        }
    };

    private AddNote addNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        initViews();

        utils = new Utils(this);

        user = utils.isUserLoggedIn();

        databaseHelper = new DatabaseHelper(this);

        setOnClickListeners();

    }

    private void setOnClickListeners() {
        Log.d(TAG, "setOnClickListeners: started");

        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddNoteActivity.this, initDateSetListener,
                        dateCalendar.get(Calendar.YEAR), dateCalendar.get(Calendar.MONTH), dateCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData())
                {
                    txtWarning.setVisibility(View.GONE);
                    initAdding();
                }
                else
                {
                    txtWarning.setVisibility(View.VISIBLE);
                    txtWarning.setText("Fill all the blank spaces");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(null != addNote)
        {
            if(!addNote.isCancelled())
            {
                addNote.cancel(true);
            }
        }
    }

    private void initAdding() {
        Log.d(TAG, "initAdding: started");

        if(null != user)
        {
            addNote = new AddNote();
            addNote.execute(user.get_id());
        }
    }

    private class AddNote extends AsyncTask<Integer, Void, Integer>
    {
        private String noteName, noteDate, description, priority;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            this.noteName = edtTxtNoteName.getText().toString();
            this.noteDate = edtTxtDate.getText().toString();
            this.description = edtTxtDescription.getText().toString();

            switch (rbGroup.getCheckedRadioButtonId())
            {
                case R.id.rbImportant:
                    priority = "Important";
                    break;
                case R.id.rbNotImportant:
                    priority = "Not Important";
                    break;
                default:
                    break;
            }
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            try {
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("name", noteName);
                values.put("date", noteDate);
                values.put("description", description);
                values.put("user_id", integers[0]);
                values.put("priority", priority);
                long id = db.insert("notes", null, values);
                db.close();
                return (int) id;
            }catch(SQLException x)
            {
                x.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            Toast.makeText(AddNoteActivity.this, "Note added successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddNoteActivity.this, NotesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private boolean validateData() {
        if(edtTxtNoteName.getText().toString().equals(""))
        {
            return false;
        }
        if (edtTxtDate.getText().toString().equals(""))
        {
            return false;
        }
        if(edtTxtDescription.getText().toString().equals(""))
        {
            return false;
        }
        return true;
    }

    private void initViews() {
        Log.d(TAG, "initViews: started");

        edtTxtNoteName = (EditText)findViewById(R.id.edtTxtNoteName);
        edtTxtDate = (EditText)findViewById(R.id.edtTxtDate);
        edtTxtDescription = (EditText)findViewById(R.id.edtTxtDescription);

        rbGroup = (RadioGroup)findViewById(R.id.rbGroup);

        btnAddNote = (Button)findViewById(R.id.btnAddNote);
        btnPickDate = (Button)findViewById(R.id.btnPickDate);

        txtWarning = (TextView)findViewById(R.id.txtWarning);
    }
}