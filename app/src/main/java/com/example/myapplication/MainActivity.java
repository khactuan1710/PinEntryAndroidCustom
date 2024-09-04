package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
                pinEntryEditText.clearFocus();



                Integer i = 100;
                int b = 100;
                Toast.makeText(pinEntryEditText.getContext(), "Độ dài hiện tại: " + i.equals(b), Toast.LENGTH_SHORT).show();
//                Toast.makeText(pinEntryEditText.getContext(), "Độ dài hiện tại: " + pinEntryEditText.getText().length(), Toast.LENGTH_SHORT).show();
            }
        });
        pinEntryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int length = editable.length();

                // Hiển thị Toast với độ dài hiện tại
                Toast.makeText(pinEntryEditText.getContext(), "Độ dài hiện tại: " + length, Toast.LENGTH_SHORT).show();

            }
        });
    }
}