package com.example.mybank.Adapters;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybank.Activities.NotesActivity;
import com.example.mybank.Database.DatabaseHelper;
import com.example.mybank.Models.Note;
import com.example.mybank.R;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder>{
    private static final String TAG = "NotesAdapter";

    private ArrayList<Note> notes = new ArrayList<>();

    private DatabaseHelper databaseHelper;

    public NotesAdapter()
    {

    }

    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notes, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: started");

        holder.edtTxtNoteName.setText(notes.get(position).getName());
        holder.edtTxtDate.setText(notes.get(position).getDate());
        holder.edtTxtDescription.setText(notes.get(position).getDescription());
        holder.edtTxtPriority.setText(notes.get(position).getPriority());
        if(holder.edtTxtPriority.getText().toString().equals("Important"))
        {
            holder.edtTxtPriority.setTextColor(Color.RED);
        }
        else
        {
            holder.edtTxtPriority.setTextColor(Color.GREEN);
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(ArrayList<Note> notes)
    {
        this.notes = notes;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView edtTxtNoteName, edtTxtDate, edtTxtDescription, edtTxtPriority;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            edtTxtNoteName = (TextView)itemView.findViewById(R.id.edtTxtNoteName);
            edtTxtDate = (TextView)itemView.findViewById(R.id.edtTxtDate);
            edtTxtDescription = (TextView)itemView.findViewById(R.id.edtTxtDescription);
            edtTxtPriority = (TextView)itemView.findViewById(R.id.txtPriorityValue);

        }
    }
}
