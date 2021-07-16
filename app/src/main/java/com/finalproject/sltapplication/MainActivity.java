package com.finalproject.sltapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener{

    final int CAMERA_PERMISSION_REQUEST_ID = 1001;
    CameraBridgeViewBase cameraBridgeViewBase;

    Scalar colorRGBA;
    Scalar colorHSV;
    Mat matRGBA;
    Color color;

    TextView txtCoordinates;
    TextView txtColor;
    TextToSpeech textToSpeech;

    double x = -1;
    double y = -1;

    private BaseLoaderCallback loaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(final int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                cameraBridgeViewBase.enableView();
                cameraBridgeViewBase.setOnTouchListener(MainActivity.this);
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_ID);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST_ID);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST_ID);
        }

        txtCoordinates = (TextView) findViewById(R.id.txtCoordinates);
        txtColor = (TextView) findViewById(R.id.txtColor);
        color = new Color();
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != textToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });

        cameraBridgeViewBase = (CameraBridgeViewBase) findViewById(R.id.camView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener((CameraBridgeViewBase.CvCameraViewListener) this);
    }

    public void onPause() {
        super.onPause();

        if (cameraBridgeViewBase != null){
            cameraBridgeViewBase.disableView();
        }
        if (textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    public void onResume(){
        super.onResume();

        if (!OpenCVLoader.initDebug()){
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, loaderCallback);
        }
        else{
            loaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();

        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }
    }

    public void onCameraViewStarted(int width, int height){
        matRGBA = new Mat();
        colorRGBA = new Scalar(255);
        colorHSV = new Scalar(255);
    }

    public void onCameraViewStopped(){
        matRGBA.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputCameraFrame){
        matRGBA = inputCameraFrame.rgba();
        return matRGBA;
    }

    public boolean onDisplay(View view, MotionEvent motionEvent){

        int rows = matRGBA.rows();
        int cols = matRGBA.cols();

        double yHigh = (double) cameraBridgeViewBase.getHeight() * 0.7696078;
        double yLow = (double) cameraBridgeViewBase.getHeight() * 0.2401961;
        double yScale = (double) rows / (yHigh - yLow);
        double xScale = (double) cols / (double) cameraBridgeViewBase.getWidth();

        x = motionEvent.getX();
        y = motionEvent.getY();

        y = y - yLow;

        x = x * xScale;
        y = y * yScale;

        if((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;

        txtCoordinates.setText("X: " + Double.valueOf(x) + ", Y: " + Double.valueOf(y));

        Rect rectDisplay = new Rect();

        rectDisplay.x = (int) x;
        rectDisplay.y = (int) y;

        rectDisplay.width = 8;
        rectDisplay.height = 8;

        Mat touchedRegRGBA = matRGBA.submat(rectDisplay);
        Mat touchedRegHSV = new Mat();

        Imgproc.cvtColor(touchedRegRGBA, touchedRegHSV, Imgproc.COLOR_RGB2HSV_FULL);

        colorHSV = Core.sumElems(touchedRegHSV);
        int point = rectDisplay.width * rectDisplay.height;
        for (int i = 0; i < colorHSV.val.length; i++){
            colorHSV.val[i] /= point;
        }

        colorRGBA = fromHSVtoRGBA(colorHSV);

        String colorsName = color.getRGBName( (int) colorRGBA.val[0], (int) colorRGBA.val[1], (int) colorRGBA.val[2]);
        txtColor.setText("Color = #" + String.format("%02X", (int) colorRGBA.val[0]) + String.format("%02X", (int) colorRGBA.val[1]) +
                String.format("%02X", (int) colorRGBA.val[2]));
        txtColor.setTextColor(android.graphics.Color.rgb((int) colorRGBA.val[0], (int) colorRGBA.val[1],
                (int) colorRGBA.val[2]));

        Toast.makeText(getApplicationContext(), colorsName, Toast.LENGTH_SHORT).show();
        textToSpeech.speak(colorsName, TextToSpeech.QUEUE_FLUSH, null);
        return false;
    }

    public Scalar fromHSVtoRGBA(Scalar colorHSV){
        Mat mRGBA = new Mat();
        Mat mHSV = new Mat(1, 1, CvType.CV_8UC3, colorHSV);
        Imgproc.cvtColor(mHSV, mRGBA, Imgproc.COLOR_HSV2BGR_FULL, 4);
        return new Scalar((mRGBA.get(0, 0)));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}