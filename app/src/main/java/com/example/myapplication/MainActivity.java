package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    PinEntryEditText pinEntryEditText;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.btn);
        pinEntryEditText = findViewById(R.id.txt_pin_entry);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pinEntryEditText.setText("1234567666");
            }
        });
    }
}