package com.finalproject.sltapplication;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;

import java.util.ArrayList;
import java.util.Locale;

import static android.Manifest.permission.CAMERA;

public class OCR extends AppCompatActivity {

    private ImageView captureImage;
    private TextView resultText;
    private Button snapBtn, detectBtn;
    private Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    TextToSpeech tts;

    FirebaseTranslator englishTranslator;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        captureImage = findViewById(R.id.captureImageView);
        resultText = findViewById(R.id.detectedImage);
//        snapBtn = findViewById(R.id.snapBtn);
//        detectBtn = findViewById(R.id.detectBtn);

//        detectBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                detectText();
//            }
//        });

//        snapBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(checkPermission()){
//                    captureImage();
//                }else{
//                    requestPermission();
//                }
//            }
//        });
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

    private boolean checkPermission(){
        int cameraPermission = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        return cameraPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(){
        int PERMISSION_CODE = 200;
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, PERMISSION_CODE);
    }

    private void captureImage(){
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePicture.resolveActivity(getPackageManager())!= null){
            startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0){
            boolean cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if(cameraPermission){
                Toast.makeText(this, "Permission Granted...", Toast.LENGTH_SHORT).show();
                captureImage();
            }else{
                Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            captureImage.setImageBitmap(imageBitmap);

        }

        if (requestCode == REQ_CODE_SPEECH_INPUT){
            if (resultCode == RESULT_OK){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String Txt = result.get(0);

                if (result.size() == 0)
                {
                    Toast.makeText(getApplicationContext(), "Lo sentimos! No entendi nada. Vuelva a repetir.",
                            Toast.LENGTH_SHORT).show();
                }

                if(Txt.equals("Capturar") || Txt.equals("capturar"))
                {
                    if(checkPermission()){
                        captureImage();
                    }else{
                        requestPermission();
                    }

                }

                if(Txt.equals("Volver atrás"))
                {
                    Intent intentBack = new Intent(this, Dashboard.class);
                    startActivity(intentBack);

                }

                if(Txt.equals("Detectar") || Txt.equals("detectar"))
                {
                    detectText();
                }

            }

        }
    }

    private void detectText() {
        InputImage image = InputImage.fromBitmap(imageBitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result = recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(@NonNull Text text) {
                StringBuilder result = new StringBuilder();
                for(Text.TextBlock block: text.getTextBlocks()){
                    String blockText = block.getText();
                    Point[] blockCornerPoint = block.getCornerPoints();
                    Rect bockFrame = block.getBoundingBox();
                    for(Text.Line line : block.getLines()){
                        String LineText = line.getText();
                        Point[] lineCornerPoint = line.getCornerPoints();
                        Rect lineRect = line.getBoundingBox();
                        for(Text.Element element : line.getElements()){
                            String elementText = element.getText();
                            result.append(elementText);
                        }

                        resultText.setText(blockText);

                        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                Locale spanish = new Locale("es", "ES");
                                if(status==TextToSpeech.SUCCESS){
                                    translateLanguage();
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OCR.this, "Fail to detect text from image " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void translateLanguage(){
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(FirebaseTranslateLanguage.EN)
                .setTargetLanguage(FirebaseTranslateLanguage.ES)
                .build();
        englishTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

        String text = resultText.getText().toString();
        downloadModal(text);
    }

    private void downloadModal(String input){
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().requireWifi().build();
        englishTranslator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(@NonNull Void unused) {
                Toast.makeText(OCR.this, "Please wait language modal is being downloaded.", Toast.LENGTH_SHORT).show();
                translateLanguageInput(input);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OCR.this, "Fail to download modal.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void translateLanguageInput(String input){
        englishTranslator.translate(input).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(@NonNull String s) {
                resultText.setText(s);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OCR.this, "Fail to translate.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }
}