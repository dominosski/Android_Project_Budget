package com.example.mybank.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybank.Models.Loan;
import com.example.mybank.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class LoanAdapter extends RecyclerView.Adapter<LoanAdapter.ViewHolder>{
    private static final String TAG = "LoanAdapter";

    private Context context;

    public LoanAdapter(Context context) {
        this.context = context;
    }

    public LoanAdapter() {
    }

    private ArrayList<Loan> loans = new ArrayList<>();

    private int number = -1;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_loans, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");
        holder.name.setText(loans.get(position).getName());
        holder.initDate.setText(loans.get(position).getInit_date());
        holder.finishDate.setText(loans.get(position).getFinish_date());
        holder.amount.setText(String.valueOf(loans.get(position).getInit_amount()));
        holder.roi.setText(String.valueOf(loans.get(position).getMonthly_roi()));
        holder.name.setText(String.valueOf(loans.get(position).getRemained_amount()));
        holder.name.setText(String.valueOf(getTotalLoss(loans.get(position))));

        if(number == -1)
        {
            holder.parent.setCardBackgroundColor(context.getResources().getColor(R.color.light_green));
            number = 1;
        }
        else
        {
            holder.parent.setCardBackgroundColor(context.getResources().getColor(R.color.light_blue));
            number = -1;
        }

    }

    private double getTotalLoss(Loan loan)
    {
        Log.d(TAG, "getTotalLoss: started for " + loan.toString());

        double loss = 0.0;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date initDate = null;
        try {
            initDate = sdf.parse(loan.getInit_date());
            calendar.setTime(initDate);
            int initMonth = calendar.get(Calendar.YEAR) * 12 + calendar.get(Calendar.MONTH);

            Date finishDate = sdf.parse(loan.getFinish_date());
            calendar.setTime(finishDate);
            int finishMonth = calendar.get(Calendar.YEAR) * 12 + calendar.get(Calendar.MONTH);

            int months = finishMonth - initMonth;

            for (int i = 0; i < months ; i++) {
                loss+= loan.getMonthly_payment() * loan.getMonthly_roi()/100;

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return loss;

    }

    @Override
    public int getItemCount() {
        return loans.size();
    }

    public void setLoans(ArrayList<Loan> loans) {
        this.loans = loans;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView name, initDate, finishDate, roi, loss, amount, remained_amount;
        private CardView parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = (TextView)itemView.findViewById(R.id.txtLoan);
            initDate = (TextView)itemView.findViewById(R.id.txtInitDate);
            finishDate = (TextView)itemView.findViewById(R.id.txtFinishDate);
            roi = (TextView)itemView.findViewById(R.id.txtROI);
            loss = (TextView)itemView.findViewById(R.id.txtMonthlyPayment);
            amount = (TextView) itemView.findViewById(R.id.txtAmount);
            remained_amount = (TextView)itemView.findViewById(R.id.txtRemainedAmount);

            parent = (CardView)itemView.findViewById(R.id.parent);
        }
    }
}
