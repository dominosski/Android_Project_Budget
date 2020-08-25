package com.example.mybank.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mybank.R;

public class AddTransactionDialog extends DialogFragment {

    private static final String TAG = "AddTransactionDialog";

    private RelativeLayout shopping, investment, loan, transaction;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_transaction, null);
        shopping = view.findViewById(R.id.shoppingRellayout);
        investment = view.findViewById(R.id.investmentRelLayout);
        loan = view.findViewById(R.id.loanRelLayout);
        transaction = view.findViewById(R.id.sendRecRelLayout);

        shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        investment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        loan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Add transaction")
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setView(view);

        return builder.create();


    }
}
