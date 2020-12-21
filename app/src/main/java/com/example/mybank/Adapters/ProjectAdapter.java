package com.example.mybank.Adapters;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybank.Activities.MainActivity;
import com.example.mybank.Models.Project;
import com.example.mybank.R;

import java.util.ArrayList;

public class ProjectAdapter  extends RecyclerView.Adapter<ProjectAdapter.ViewHolder>{
    private static final String TAG = "ProjectAdapter";

    private ArrayList<Project> projects = new ArrayList<>();

    public ProjectAdapter()
    {

    }

    @NonNull
    @Override
    public ProjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_project, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ProjectAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: started");

        holder.txtName.setText(projects.get(position).getName());
        holder.txtInitDate.setText(projects.get(position).getInitDate());
        holder.txtFinishDate.setText(projects.get(position).getFinishDate());
        double budget = projects.get(position).getInitialBudget();
        if(budget > 0)
        {
            holder.txtInitialBudget.setText(String.valueOf(budget));
            holder.txtInitialBudget.setTextColor(Color.GREEN);
        }
        else
        {
            holder.txtInitialBudget.setText("0");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               v.getContext().startActivity(new Intent(v.getContext(), MainActivity.class));
            }
        });

    }


    @Override
    public int getItemCount() {
        return projects.size();
    }

    public void setProjects(ArrayList<Project> projects) {
        this.projects = projects;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private TextView txtName, txtInitDate, txtFinishDate, txtInitialBudget;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = (TextView)itemView.findViewById(R.id.txtProjectName);
            txtInitDate = (TextView)itemView.findViewById(R.id.edtTxtInitDate);
            txtFinishDate = (TextView)itemView.findViewById(R.id.edtTxtFinishDate);
            txtInitialBudget = (TextView)itemView.findViewById(R.id.edtTxtInitialBudget);
        }
    }
}
