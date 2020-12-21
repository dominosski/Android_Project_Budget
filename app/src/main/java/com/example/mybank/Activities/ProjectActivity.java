package com.example.mybank.Activities;

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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybank.Adapters.ProjectAdapter;
import com.example.mybank.Authentication.LoginActivity;
import com.example.mybank.Database.DatabaseHelper;
import com.example.mybank.Models.Project;
import com.example.mybank.Models.User;
import com.example.mybank.R;
import com.example.mybank.Utils.Utils;

import java.util.ArrayList;

public class ProjectActivity extends AppCompatActivity {

    private static final String TAG = "ProjectActivity";

    private Button btnAddProject;
    private RecyclerView projectRecView;
    private TextView txtNoProjects;

    private DatabaseHelper databaseHelper;

    private ProjectAdapter adapter;

    private Utils utils;

    private GetProjects getProjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        initViews();

        utils = new Utils(this);
        User user = utils.isUserLoggedIn();

        if(null!=user)
        {
            Toast.makeText(this, "User: " + user.getFirst_name() + " logged in", Toast.LENGTH_SHORT).show();
        }else
        {
            Intent intent = new Intent(ProjectActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        databaseHelper = new DatabaseHelper(this);

        initProjectsRecView();

        setOnClickListeners();

        if(null!=user)
        {
            Toast.makeText(this, "User: " + user.getFirst_name() + " logged in", Toast.LENGTH_SHORT).show();
        }else
        {
            Intent intent = new Intent(ProjectActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }



    @Override
    protected void onResume() {
        super.onResume();
        setOnClickListeners();
        initProjectsRecView();
        getProjects();

    }

    private void setOnClickListeners() {
        Log.d(TAG, "setOnClickListeners: started");

        btnAddProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectActivity.this, AddProjectActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void initProjectsRecView() {
        Log.d(TAG, "initProjectsRecView: started");

        adapter = new ProjectAdapter();
        projectRecView.setAdapter(adapter);
        projectRecView.setLayoutManager(new LinearLayoutManager(this));
        getProjects();
    }

    private void getProjects() {
        getProjects = new GetProjects();
        User user = utils.isUserLoggedIn();
        if(user != null)
        {
            getProjects.execute(user.get_id());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(null!= getProjects)
        {
            if(!getProjects.isCancelled())
            {
                getProjects.cancel(true);
            }
        }
    }

    private class GetProjects extends AsyncTask<Integer, Void, ArrayList<Project>>
    {
        @Override
        protected ArrayList<Project> doInBackground(Integer... integers) {

            try {
                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                Cursor cursor = db.query("projects", null, "user_id=?",
                        new String[] {String.valueOf(integers[0])}, null, null, null);
                if(null!=cursor)
                {
                    if (cursor.moveToFirst())
                    {
                        ArrayList<Project>projects = new ArrayList<>();
                        for (int i = 0; i < cursor.getCount() ; i++) {
                            Project project = new Project();
                            project.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                            project.setInitDate(cursor.getString(cursor.getColumnIndex("init_date")));
                            project.setFinishDate(cursor.getString(cursor.getColumnIndex("finish_date")));
                            project.setName(cursor.getString(cursor.getColumnIndex("name")));
                            project.setInitialBudget(cursor.getDouble(cursor.getColumnIndex("init_amount")));
                            projects.add(project);
                            cursor.moveToNext();
                        }
                        cursor.close();
                        db.close();
                        return projects;
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
            }catch (SQLException x)
            {
                x.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Project> projects) {
            super.onPostExecute(projects);

            if(null!=projects)
            {
                txtNoProjects.setVisibility(View.GONE);
                adapter.setProjects(projects);
            }
            else
            {
                txtNoProjects.setVisibility(View.VISIBLE);
                adapter.setProjects(new ArrayList<Project>());
            }
        }
    }

    private void initViews() {
        Log.d(TAG, "initViews: started");

        btnAddProject = (Button)findViewById(R.id.btnAddProject);
        projectRecView = (RecyclerView)findViewById(R.id.projectRecView);
        txtNoProjects = (TextView)findViewById(R.id.txtNoProjects);
    }
}