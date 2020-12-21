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
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybank.Database.DatabaseHelper;
import com.example.mybank.Models.User;
import com.example.mybank.R;
import com.example.mybank.Utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddProjectActivity extends AppCompatActivity {
    private static final String TAG = "AddProjectActivity";

    private EditText edtTxtProjectName, edtTxtInitDate, edtTxtFinishDate, edtTxtInitBudget;
    private Button btnInitDate, btnFinishDate, btnAddProject;
    private TextView txtWarning;

    private DatabaseHelper databaseHelper;

    private Calendar initCalendar = Calendar.getInstance();
    private Calendar finishCalendar = Calendar.getInstance();

    private DatePickerDialog.OnDateSetListener initDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            initCalendar.set(Calendar.YEAR, year);
            initCalendar.set(Calendar.MONTH, month);
            initCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            edtTxtInitDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(initCalendar.getTime()));
        }
    };
    private DatePickerDialog.OnDateSetListener finishDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            finishCalendar.set(Calendar.YEAR, year);
            finishCalendar.set(Calendar.MONTH, month);
            finishCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            edtTxtFinishDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(finishCalendar.getTime()));
        }
    };

    private Utils utils;

    private AddProject addProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        databaseHelper = new DatabaseHelper(this);

        initViews();

        utils = new Utils(this);

        setOnClickListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(null != addProject)
        {
            if(!addProject.isCancelled())
            {
                addProject.cancel(true);
            }
        }
    }

    private void setOnClickListeners() {
        btnInitDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddProjectActivity.this, initDateSetListener,
                        initCalendar.get(Calendar.YEAR), initCalendar.get(Calendar.MONTH), initCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        btnFinishDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddProjectActivity.this, finishDateSetListener,
                        finishCalendar.get(Calendar.YEAR), finishCalendar.get(Calendar.MONTH), finishCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        btnAddProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateData()) {
                    txtWarning.setVisibility(View.GONE);
                    initAdding();
                } else {
                    txtWarning.setVisibility(View.VISIBLE);
                    txtWarning.setText("Please fill all the blank spaces");
                }
            }
        });
    }

    private void initAdding() {
        Log.d(TAG, "initAdding: started");

        User user = utils.isUserLoggedIn();
        if (null != user) {
            Log.d(TAG, "initAdding: called");
            addProject = new AddProject();
            addProject.execute(user.get_id());
        }

    }

    private class AddProject extends AsyncTask<Integer, Void, Integer> {
        private String name, initDate, finishDate;
        private Double initBudget;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.name = edtTxtProjectName.getText().toString();
            this.initDate = edtTxtInitDate.getText().toString();
            this.finishDate = edtTxtFinishDate.getText().toString();
            this.initBudget = Double.valueOf(edtTxtInitBudget.getText().toString());
        }

        @Override
        protected Integer doInBackground(Integer... integers) {

            try {
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("name", name);
                values.put("init_date", initDate);
                values.put("finish_date", finishDate);
                values.put("init_amount", initBudget);
                values.put("user_id", integers[0]);
                long projectId = db.insert("projects", null, values);
                db.close();
                return (int) projectId;
            } catch (SQLException x) {
                x.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            Toast.makeText(AddProjectActivity.this, edtTxtProjectName.getText().toString() + " added successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddProjectActivity.this, ProjectActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private boolean validateData() {
        if (edtTxtProjectName.getText().toString().equals("")) {
            return false;
        }
        if (edtTxtInitDate.getText().toString().equals("")) {
            return false;
        }
        if (edtTxtFinishDate.getText().toString().equals("")) {
            return false;
        }
        if (edtTxtInitBudget.getText().toString().equals("")) {
            return false;
        }
        return true;
    }

    private void initViews() {
        Log.d(TAG, "initViews: started");

        edtTxtProjectName = (EditText) findViewById(R.id.edtTxtProjectName);
        edtTxtInitDate = (EditText) findViewById(R.id.edtTxtInitDate);
        edtTxtFinishDate = (EditText) findViewById(R.id.edtTxtFinishDate);
        edtTxtInitBudget = (EditText) findViewById(R.id.edtTxtInitBudget);

        btnInitDate = (Button) findViewById(R.id.btnPickDate);
        btnFinishDate = (Button) findViewById(R.id.btnPickFinishDate);
        btnAddProject = (Button) findViewById(R.id.btnAddProject);

        txtWarning = (TextView) findViewById(R.id.txtWarning);
    }
}