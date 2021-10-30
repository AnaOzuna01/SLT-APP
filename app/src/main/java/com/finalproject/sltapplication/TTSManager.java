package com.finalproject.sltapplication;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TTSManager {

    private TextToSpeech TTS = null;
    private boolean isLoaded = false;

    public void init(Context context){
        try {
            TTS = new TextToSpeech(context, onInitListener);
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    private final TextToSpeech.OnInitListener onInitListener = status -> {
        Locale spanish = new Locale("es", "ES");
        if (status == TextToSpeech.SUCCESS){
            int result = TTS.setLanguage(spanish);
            isLoaded = true;

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("error", "Language is not allowed.");
            }
        } else{
            Log.e("error", "Fail to load.");
        }
    };

    public void shutDown(){
        TTS.shutdown();
    }

    public void addQueue(String text){
        if (isLoaded)
            TTS.speak(text, TextToSpeech.QUEUE_ADD, null);
        else
            Log.e("error", "TTS not initialized.");
    }

    public void initQueue(String text){
        if (isLoaded)
            TTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        else
            Log.e("error", "TTS not initialized.");
    }

}
