package com.lendeasy.daancorona;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class Transactions extends AppCompatActivity {

    RecyclerView recyclerView;
    TransactionAdapter transactionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        recyclerView=findViewById(R.id.recyclerview);

    }
}
