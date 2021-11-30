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

            String[] classes = {"Abrigo", "Bermuda", "Camisa Manga Corta", "Camisa Manga Larga", "Camiseta", "Chaqueta",
                    "Corbata", "Jeans", "Pantalon Casual", "Pantalon Deportivo", "Poloche", "Tenis", "Tenis Adidas",
                    "Tenis Nike", "Zapatos"};

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