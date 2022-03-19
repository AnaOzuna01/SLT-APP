package com.finalproject.sltapplication;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.finalproject.sltapplication.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Locale;

import static android.Manifest.permission.CAMERA;
public class Fashion extends AppCompatActivity {

    private ImageView captureImage;
    private TextView resultText;
    private TextView confidenceText;
    private TextView colorText;
    private Button snapBtn;

    private Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private final int REQ_CODE_SPEECH_INPUT = 100;
    int imageSize = 224;

    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fashion);

        captureImage = findViewById(R.id.captureImageView);
        resultText = findViewById(R.id.detectedImage);
        snapBtn = findViewById(R.id.snapBtn);
        confidenceText = findViewById(R.id.confidenceTxt);
        colorText = findViewById(R.id.colorTxt);
//        captureImage.setImageBitmap(imageBitmap);

        snapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    captureImage();
                } else {
                    requestPermission();
                }
            }

        });

    }

    private boolean checkPermission() {
        int cameraPermission = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        return cameraPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        int PERMISSION_CODE = 200;
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, PERMISSION_CODE);
    }

    private void captureImage() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicture.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            boolean cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (cameraPermission) {
                Toast.makeText(this, "Permission Granted...", Toast.LENGTH_SHORT).show();
                captureImage();
            } else {
                Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public boolean onTouchEvent(MotionEvent e) {
        Log.e("Touching", "Touching the screen");
        startVoiceInput();
        return true;
    }

    public void startVoiceInput() {
        Intent intentVoice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentVoice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intentVoice.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ES");
        intentVoice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ES");
        intentVoice.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "ES");
        intentVoice.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hola, Como te puedo ayudar");
        try {
            startActivityForResult(intentVoice, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Lo sentimos! Tu dispositivo no suporta comando por voz.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void classifyImage(Bitmap imageBitmap){

        try {
            Model model = Model.newInstance(getApplicationContext());

            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            imageBitmap.getPixels(intValues,
                    0,
                    imageBitmap.getWidth(),
                    0,
                    0,
                    imageBitmap.getWidth(),
                    imageBitmap.getHeight());
            int pixel = 0;
            for (int i = 0; i < imageSize; i++){
                for(int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val >> 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++){
                if(confidences[i] > maxConfidence){
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }

            String[] classes = {"Abrigo", "Bermuda", "Camisa", "Camiseta", "Chancletas", "Chaqueta",
                    "Corbata", "Correa", "Gorras", "Pantalon Jeans", "Medias", "Pantalon Casual", "Pantalon Deportivo", "Poloche", "Tenis", "Zapatos"};

            Log.d("CLASSIFY", classes[maxPos]);
            Log.d("CLASSIFY", String.valueOf(confidences[maxPos]));

            resultText.setText(classes[maxPos]);

            String result = "";
            result += String.format("Prenda: %s\n Confianza: %.1f%%", classes[maxPos], confidences[maxPos] * 100);
            confidenceText.setText(result);

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

            Palette.from(imageBitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    int color = palette.getDominantColor(1);
                    String hex = "#" + Integer.toHexString(color);
                    Log.d("CLASSIFY", hex);
                    String colorTxt = "";
                    String name = getRGBName(hex);
                    colorTxt += String.format("Color: %s", name);
                    colorText.setText(colorTxt);
                    Log.d("CLASSIFY", String.valueOf(name));
                    tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            Locale spanish = new Locale("es", "ES");
                            if(status==TextToSpeech.SUCCESS){
                                tts.setLanguage(spanish);
                                tts.setSpeechRate(1.0f);
                                tts.speak(colorText.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                                tts.speak(colorText.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                            }
                        }
                    });
                }
            });

            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            int dimension = Math.min(imageBitmap.getWidth(), imageBitmap.getHeight());
            imageBitmap = ThumbnailUtils.extractThumbnail(imageBitmap, dimension, dimension);
            captureImage.setImageBitmap(imageBitmap);

            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, imageSize, imageSize, false);
            classifyImage(imageBitmap);


            if (requestCode == REQ_CODE_SPEECH_INPUT) {
                if (resultCode == RESULT_OK) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String Txt = result.get(0);

                    if (result.size() == 0) {
                        Toast.makeText(getApplicationContext(), "Lo sentimos! No entendi nada. Vuelva a repetir.",
                                Toast.LENGTH_SHORT).show();
                    }

                    if (Txt.equals("Volver atrás")) {
                        Intent intentBack = new Intent(this, Dashboard.class);
                        startActivity(intentBack);

                    }
                }

            }
        }

    }

    public String getRGBName(String colorName){

        String name;
        switch (colorName.toUpperCase()){
            case "#FF2B1AFF":
            case "ff304050":
            case "#FF00FFFF":
            case "#FFE0FFFF":
            case "#FFADD8E6":
            case "#FF87CEEB":
            case "#FF001AFF":
            case "#FF0000CD":
            case "#FF00008B":
            case "#FF000080":
            case "#FFF0FFFF":
            case "ff304060":
            case "ff2078c0":
            case "ff607090":
            case "ff283048":
            case "ffa8c0c8":
            case "ff5070a0":
            case "ff303850":
            case "ff202840":
            case "ff202038":
            case "ff282838":
            case "ff383848":
            case "ff485068":
            case "ff202838":
            case "ffb0b8c0":
            case "ff606888":
            case "ff383850":
            case "ff284068":
            case "ff687098":
            case "ff607098":
            case "ff7080a0":
            case "ff385878":
            case "ff404060":
            case "ffa8b0c0":
            case "ff384880":
            case "ff687090":
                name = "Azul";
                break;
            case "#FF5D9BFF":
                name = "Morado";
                break;
            case "#FF90033FF":
                name = "Lila";
                break;
            case "#FFFAE1FF":
            case "#FFFFFFFF":
            case "ffd0d0c8":
            case "ffd0d0d0":
            case "ff9098a8":
            case "ffa8a0a8":
                name = "Blanco";
                break;
            case "#FFFF2985":
            case "#FFFFFF01":
            case "#FFADFF2F":
            case "ffd8a808":
                name = "Amarillo";
                break;
            case "#FFFF2931":
                name = "Amarillo Brillante";
                break;
            case "#FF87FF62":
            case "#FF808080":
            case "ffb0b8c8":
            case "ffc0c0c8":
            case "ff8088a0":
            case "ff909090":
            case "ff787890":
            case "ffa8a8b0":
            case "ff484858":
            case "ff989090":
            case "ffb8b8c8":
            case "ff384050":
            case "ff506070":
            case "ffa8a8b8":
            case "ffb8b0c0":
            case "ff303848":
            case "ff90a080":
                name = "Gris";
                break;
            case "#FF1EFFC3":
                name = "Azul Marino";
                break;
            case "#FFE9967A":
                name = "Salmon";
                break;
            case "#FFFF0000":
            case "#FF8B0000":
            case "#FFFF6347":
            case "#FFFF4500":
                name = "Rojo";
                break;
            case "#FFFFA500":
            case "#FFFF8C00":
                name = "Naranja";
                break;
            case "#FFFF7F50":
                name = "Coral";
                break;
            case "#FFFFD700":
                name = "Oro";
                break;
            case "#FF00FF00":
            case "#FF6EFF3C":
            case "#FF90EE90":
            case "#FF008000":
            case "#FF006400":
            case "#FF6B8E23":
                name = "Verde";
                break;
            case "#FF800000":
            case "#FFA52A2A":
                name = "Marron";
                break;
            case "#FF000000":
            case "ff303040":
            case "ff404050":
            case "ff505060":
            case "ff283038":
                name = "Negro";
                break;
            case "#FFC0C0C0":
            case "ffb0b0b0":
            case "ffc8c0c0":
            case "ffc0c0c0":
            case "ffc8c0c8":
            case "ffb8b8b8":
                name = "Plateado";
                break;
            case "ffa85078":
            case "ff906098":
                name = "Rosa";
                break;
            case "ff283040":
            case "ff303048":
            case "ff404048":
                name = "Negro";
                break;
            case "ff807078":
                name = "gris";
                break;
            case "ff889870":
                name = "Verde";
                break;
            case "ffa04860":
                name = "Rojo";
                break;
            case "ffa0a0a0":
                name = "Plateado";
                break;
            case "ff383838":
                name = "Negro";
                break;
            case "ff884058":
                name = "Rojizo";
                break;
            case "ff98b0d0":
                name = "Celeste";
                break;
            case "ffc88088":
                name = "Rosado";
                break;
            case "ff902830":
                name = "Rojo";
                break;
            case "ff381818":
                name = "Marron";
                break;
            case "ff282830":
                name = "Negro";
                break;
            case "ff9898a8":
                name = "Gris";
                break;
            case "ff181828":
                name = "Negro";
                break;
            case "ffb0b8b8":
                name = "Gris";
                break;
            case "ff606880":
                name = "Gris";
                break;
            case "ff181820":
                name = "Negro";
                break;
            case "ff202030":
                name = "Negro";
                break;
            case "ff302838":
                name = "Negro";
                break;
            case "ffa02828":
                name = "Rojo";
                break;
            case "ff182838":
                name = "Negro";
                break;
            case "ffb03858":
                name = "Rojo";
                break;
            case "ff888888":
                name = "";
                break;
            case "ff301818":
                name = "Escarlata";
                break;
            default:
                name = "Desconocido";
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