package com.finalproject.sltapplication;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    private Button ocrButton;
    private Button fashionButton;
    private Button colorButton;

    private final int REQ_CODE_SPEECH_INPUT = 100;

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

    public boolean onTouchEvent(MotionEvent e){
        Log.e("Touching", "Touching the screen");
        startVoiceInput();
        return true;
    }

    public void startVoiceInput(){
        Intent intentVoice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentVoice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intentVoice.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ES");
        intentVoice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ES");
        intentVoice.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE,"ES");
        intentVoice.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hola, Como te puedo ayudar");
        try {
            startActivityForResult(intentVoice, REQ_CODE_SPEECH_INPUT);
        }catch (ActivityNotFoundException a){
            Toast.makeText(getApplicationContext(), "Lo sentimos! Tu dispositivo no suporta comando por voz.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SPEECH_INPUT){
            if (resultCode == RESULT_OK){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String Txt = result.get(0);

                if (result.size() == 0)
                {
                    Toast.makeText(getApplicationContext(), "Lo sentimos! No entendi nada. Vuelva a repetir.",
                            Toast.LENGTH_SHORT).show();
                }

                if(Txt.equals("abrir moda"))
                {
                    Intent intentOpenFashion = new Intent(this, Fashion.class);
                    startActivity(intentOpenFashion);

                }
                if(Txt.equals("abrir color"))
                {
                    Intent intentOpenColor = new Intent(getApplicationContext(), Color.class);
                    startActivity(intentOpenColor);
                }

                if(Txt.equals("abrir texto"))
                {
                    Intent intentOpenOCR = new Intent(getApplicationContext(), OCR.class);
                    startActivity(intentOpenOCR);
                }

                if(Txt.equals("sugerir ropa"))
                {
                    Intent intentOpenClothesSuggestionMenu = new Intent(this, ClothesSuggestion.class);
                    startActivity(intentOpenClothesSuggestionMenu);

                }
                if(Txt.equals("sugerir color"))
                {
                    Intent intentOpenColorSuggestionMenu = new Intent(this, ColorSuggestion.class);
                    startActivity(intentOpenColorSuggestionMenu);

                }
            }

        }
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