package com.example.mybank.Dialogs;

import android.app.AlertDialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mybank.Activities.AddLeasingActivity;
import com.example.mybank.Activities.OutgoingActivity;
import com.example.mybank.R;
import com.example.mybank.Activities.TransferActivity;


public class AddTransactionDialog extends DialogFragment {

    private static final String TAG = "AddTransactionDialog";


    private RelativeLayout shopping, loan, transaction;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_transaction, null);
        shopping = view.findViewById(R.id.outgoingRelLayout);
        loan = view.findViewById(R.id.leasingRelLayout);
        transaction = view.findViewById(R.id.sendRecRelLayout);

        shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OutgoingActivity.class);
                startActivity(intent);
            }
        });

        loan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddLeasingActivity.class);
                startActivity(intent);
            }
        });
        transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TransferActivity.class);
                startActivity(intent);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Add transaction")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setView(view);

        return builder.create();


    }
}
