package com.example.mybank.Models;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mybank.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class ShoppingActivity extends AppCompatActivity {
    private static final String TAG = "ShoppingActivity";

    private Calendar calendar = Calendar.getInstance();

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            edtTxtDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        }
    };

    private TextView txtWarning, txtItemName;
    private ImageView itemImg;
    private Button btnPickItem, btnPickDate, btnAdd;
    private EditText edtTxtDate, edtTxtDesc, edtTxtItemPrice, edtTxtStore;
    private RelativeLayout itemRelLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);

        initViews();

        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ShoppingActivity.this, dateSetListener, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void initViews() {
        Log.d(TAG, "initViews: started");

        txtWarning = (TextView) findViewById(R.id.txtWarning);
        txtItemName = (TextView) findViewById(R.id.txtItemName);

        itemImg = (ImageView)findViewById(R.id.itemImg);

        btnPickItem = (Button)findViewById(R.id.btnPick);
        btnPickDate = (Button)findViewById(R.id.btnPickDate);

        edtTxtDate = (EditText)findViewById(R.id.edtTxtDate);
        edtTxtDesc = (EditText)findViewById(R.id.edtTxtDesc);
        edtTxtItemPrice = (EditText)findViewById(R.id.edtTxtPrice);
        edtTxtStore = (EditText)findViewById(R.id.edtTxtStore);

        itemRelLayout = (RelativeLayout) findViewById(R.id.invisibleItemRelLayout);
    }
}