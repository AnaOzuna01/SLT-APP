package com.finalproject.sltapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Dashboard extends AppCompatActivity {

    private Button ocrButton;
    private Button fashionButton;
    private Button colorButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ocrButton = (Button) findViewById(R.id.ocr_button);
        ocrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOCR();
            }
        });

        fashionButton = (Button) findViewById(R.id.fashion_button);
        fashionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFashion();
            }
        });

        colorButton = (Button) findViewById(R.id.color_button);
        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });
    }

    public void openOCR(){
        Intent intentOCR = new Intent(this, OCR.class);
        startActivity(intentOCR);
    }

    public void openFashion(){
        Intent intentFashion = new Intent(this, Fashion.class);
        startActivity(intentFashion);
    }

    public void openColorPicker(){
        Intent intentColor = new Intent(this, Color.class);
        startActivity(intentColor);
    }
}