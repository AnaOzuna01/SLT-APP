package com.finalproject.sltapplication;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;

import java.util.Locale;

public class TTSManager {

    TextToSpeech tts;

    public void init(Context context){
        tts = new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Locale spanish = new Locale("es", "ES");
                if(status==TextToSpeech.SUCCESS){
                    tts.setLanguage(spanish);
                    tts.setSpeechRate(1.0f);
                }
            }
        });
    }

    public void start(TextView text){
        tts.speak(text.getText().toString(), TextToSpeech.QUEUE_ADD, null);
    }

    public void shutDown(){
        tts.shutdown();
    }
}
