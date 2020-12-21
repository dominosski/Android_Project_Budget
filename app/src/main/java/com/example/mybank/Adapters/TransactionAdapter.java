package com.example.mybank.Adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybank.Models.Transaction;
import com.example.mybank.R;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private static final String TAG = "TransactionAdapter";

    private ArrayList<Transaction> transactions = new ArrayList<>();

    public TransactionAdapter()
    {

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_transaction, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: started");

        holder.date.setText(transactions.get(position).getDate());
        holder.description.setText(transactions.get(position).getDescription());
        holder.sender.setText(transactions.get(position).getRecipient());
        holder.transactionID.setText("Transaction ID: " + String.valueOf(transactions.get(position).get_id()));
        double amount = transactions.get(position).getAmount();
        if(amount > 0)
        {
            holder.txtAmount.setText("+ " + amount);
            holder.txtAmount.setTextColor(Color.GREEN);
        }
        else
        {
            holder.txtAmount.setText(String.valueOf(amount));
            holder.txtAmount.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView txtAmount, sender, description, date, transactionID;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtAmount = (TextView)itemView.findViewById(R.id.txtAmount);
            sender = (TextView)itemView.findViewById(R.id.txtSender);
            description = (TextView)itemView.findViewById(R.id.txtDesc);
            transactionID = (TextView)itemView.findViewById(R.id.txtTransaction);
            date = (TextView)itemView.findViewById(R.id.txtDate);
        }
    }
}
