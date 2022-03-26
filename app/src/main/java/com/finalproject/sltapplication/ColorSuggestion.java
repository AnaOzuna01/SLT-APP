package com.finalproject.sltapplication;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class ColorSuggestion extends AppCompatActivity {

    private TextView resultText;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_menu);

        resultText = findViewById(R.id.color_suggestions_option);

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

    public static String getRandomValue(String[] array){
        int result = new Random().nextInt(array.length);
        return array[result];
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

                if(Txt.equals("color frío"))
                {
                    String[] myArrayFrio_1 = new String[]{"Verde", "Turquesa"};
                    String[] myArrayFrio_2 = new String[]{"Violeta", "Azul"};

                    String result_1 = getRandomValue(myArrayFrio_1);
                    String result_2 = getRandomValue(myArrayFrio_2);

                    String blockText = result_1 + "\n" + result_2;
                    resultText.setText(blockText);

                    tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            Locale spanish = new Locale("es", "ES");
                            if(status==TextToSpeech.SUCCESS){
                                tts.setLanguage(spanish);
                                tts.setSpeechRate(1.0f);
                                tts.speak(resultText.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                                tts.speak(resultText.getText().toString(), TextToSpeech.QUEUE_ADD, null);

                            }
                        }
                    });

                }

                if(Txt.equals("color cálido"))
                {
                    String[] myArrayCalido_1 = new String[]{"Amarillo", "Naranja"};
                    String[] myArrayCalido_2 = new String[]{"Vino", "Rojo"};

                    String result_1 = getRandomValue(myArrayCalido_1);
                    String result_2 = getRandomValue(myArrayCalido_2);

                    String blockText = result_1 + "\n" + result_2;
                    resultText.setText(blockText);

                    tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            Locale spanish = new Locale("es", "ES");
                            if(status==TextToSpeech.SUCCESS){
                                tts.setLanguage(spanish);
                                tts.setSpeechRate(1.0f);
                                tts.speak(resultText.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                                tts.speak(resultText.getText().toString(), TextToSpeech.QUEUE_ADD, null);

                            }
                        }
                    });

                }

                if(Txt.equals("color complementario"))
                {
                    String[] myArrayComplementarios_1 = new String[]{"Verde", "Turquesa", "Azul"};
                    String[] myArrayComplementarios_2 = new String[]{"Rojo", "Naranja", "Vino"};

                    String result_1 = getRandomValue(myArrayComplementarios_1);
                    String result_2 = getRandomValue(myArrayComplementarios_2);

                    String blockText = result_1 + "\n" + result_2;
                    resultText.setText(blockText);

                    tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            Locale spanish = new Locale("es", "ES");
                            if(status==TextToSpeech.SUCCESS){
                                tts.setLanguage(spanish);
                                tts.setSpeechRate(1.0f);
                                tts.speak(resultText.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                                tts.speak(resultText.getText().toString(), TextToSpeech.QUEUE_ADD, null);

                            }
                        }
                    });

                }

                if (Txt.equals("Volver atrás")) {
                    Intent intentBack = new Intent(this, Dashboard.class);
                    startActivity(intentBack);

                }

            }

        }
    }

}
