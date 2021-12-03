package com.finalproject.sltapplication;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class Color extends AppCompatActivity {

    ImageView imgView;
    TextView txtColorValues;
    TextView txtColorName;
    View viewColorViews;
    Bitmap imageBitmap;
    TextToSpeech tts;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);

        imgView = findViewById(R.id.imgViewColorPicker);
        txtColorValues = findViewById(R.id.txtColorPicker);
        txtColorName = findViewById(R.id.txtColorName);
        viewColorViews = findViewById(R.id.vwDisplayColor);

        imgView.setDrawingCacheEnabled(true);
        imgView.buildDrawingCache(true);


        imgView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    imageBitmap = imgView.getDrawingCache();
                    int pixels = imageBitmap.getPixel((int) event.getX(), (int) event.getY());

                    int r = android.graphics.Color.red(pixels);
                    int g = android.graphics.Color.red(pixels);
                    int b = android.graphics.Color.blue(pixels);

                    String hex = "#" + Integer.toHexString(pixels);
//                    viewColorViews.setBackgroundColor(android.graphics.Color.rgb(r, g, b));
                    String name = getRGBName(hex);
                    txtColorValues.setText("RGB: " + r + ", " + g + ", " + b + " \nHEX:" + hex.toUpperCase() + " \n");
                    txtColorName.setText(name);
                    tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            Locale spanish = new Locale("es", "ES");
                            if(status==TextToSpeech.SUCCESS){
                                tts.setLanguage(spanish);
                                tts.setSpeechRate(1.0f);
                                tts.speak(txtColorName.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                                tts.speak(txtColorName.getText().toString(), TextToSpeech.QUEUE_ADD, null);

                            }
                        }
                    });


                }

                return true;
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

                if(Txt.equals("Volver atrás"))
                {
                    Intent intentBack = new Intent(this, Dashboard.class);
                    startActivity(intentBack);

                }
            }

        }
    }

    public String getRGBName(String colorName){

        String name;
        switch (colorName.toUpperCase()){
            case "#FF2B1AFF":
                name = "Azul";
                break;
            case "#FF5D9BFF":
                name = "Morado Claro";
                break;
            case "#FF90033FF":
                name = "Lila";
                break;
            case "#FFFAE1FF":
                name = "Blanco";
                break;
            case "#FFFF2985":
                name = "Amarillo";
                break;
            case "#FFFF2931":
                name = "Amarillo Brillante";
                break;
            case "#FF87FF62":
                name = "Gris";
                break;
            case "#FF6EFF3C":
                name = "Olivo";
                break;
            case "#FF1EFFC3":
                name = "Azul Marino";
                break;
            case "#FFE9967A":
                name = "Dark Salmon";
                break;
            case "#FFFF0000":
                name = "Red";
                break;
            case "#FF8B0000":
                name = "Dark Red";
                break;
            case "#FFFFA500":
                name = "Orange";
                break;
            case "#FFFF8C00":
                name = "Dark Orange";
                break;
            case "#FFFF7F50":
                name = "Coral";
                break;
            case "#FFFF6347":
                name = "Tomato";
                break;
            case "#FFFF4500":
                name = "Orange Red";
                break;
            case "#FFFFD700":
                name = "Gold";
                break;
            case "#FFFFFF01":
                name = "Yellow";
                break;
            case "#FFADFF2F":
                name = "Green Yellow";
                break;
            case "#FF00FF00":
                name = "Lime";
                break;
            case "#FF90EE90":
                name = "Light Green";
                break;
            case "#FF008000":
                name = "Green";
                break;
            case "#FF006400":
                name = "Dark Green";
                break;
            case "#FF6B8E23":
                name = "Olive";
                break;
            case "#FF00FFFF":
                name = "Aqua";
                break;
            case "#FFE0FFFF":
                name = "Cyan";
                break;
            case "#FFADD8E6":
                name = "Light Blue";
                break;
            case "#FF87CEEB":
                name = "Sky Blue";
                break;
            case "#FF001AFF":
                name = "Blue";
                break;
            case "#FF0000CD":
                name = "Medium Blue";
                break;
            case "#FF00008B":
                name = "Dark Blue";
                break;
            case "#FF000080":
                name = "Navy";
                break;
            case "#FF800000":
                name = "Maroon";
                break;
            case "#FFA52A2A":
                name = "Brown";
                break;
            case "#FFFFFFFF":
                name = "White";
                break;
            case "#FFF0FFFF":
                name = "Azure";
                break;
            case "#FF808080":
                name = "Gray";
                break;
            case "#FF000000":
                name = "Black";
                break;
            case "#FFC0C0C0":
                name = "Silver";
                break;
            default:
                name = "Unknown";
                break;
        }
        return name;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }
}