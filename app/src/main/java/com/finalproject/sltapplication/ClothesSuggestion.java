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

public class ClothesSuggestion extends AppCompatActivity {

    private TextView resultText;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothes_menu);

        resultText = findViewById(R.id.clothes_suggestions_option);

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

                if(Txt.equals("estilo casual"))
                {
                    String[] myCasualArray_1 = new String[]{"Camisa", "Poloche"};
                    String[] myCasualArray_2 = new String[]{"Pantalon Casual", "Pantalon Jeans", "Bermuda"};
                    String[] myCasualArray_3 = new String[]{"Tenis", "Zapatos"};

                    String result_1 = getRandomValue(myCasualArray_1);
                    String result_2 = getRandomValue(myCasualArray_2);
                    String result_3 = getRandomValue(myCasualArray_3);

                    String blockText = result_1 + "\n" + result_2 + "\n" + result_3;
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


                if(Txt.equals("estilo deportivo"))
                {
                    String[] myCasualArray_1 = new String[]{"Abrigo", "Camiseta"};
                    String[] myCasualArray_2 = new String[]{"Pantalon Deportivo", "Bermuda"};
                    String[] myCasualArray_3 = new String[]{"Tenis"};

                    String result_1 = getRandomValue(myCasualArray_1);
                    String result_2 = getRandomValue(myCasualArray_2);
                    String result_3 = getRandomValue(myCasualArray_3);

                    String blockText = result_1 + "\n" + result_2 + "\n" + result_3;
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
            }

        }
    }

}
