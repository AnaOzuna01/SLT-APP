package com.finalproject.sltapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    final int CAMERA_PERMISSION_REQUEST_ID = 1001;
    CameraBridgeViewBase cameraBridgeViewBase;

    Scalar colorRGBA;
    Scalar colorHSV;
    Mat matRGBA;
    Color color;

    TextView txtCoordinates;
    TextView txtColor;
    TextToSpeech textToSpeech;

    private BaseLoaderCallback loaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(final int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                cameraBridgeViewBase.enableView();
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

    public Scalar fromHSVtoRGBA(Scalar colorHSV){
        Mat mRGBA = new Mat();
        Mat mHSV = new Mat(1, 1, CvType.CV_8UC3, colorHSV);
        Imgproc.cvtColor(mHSV, mRGBA, Imgproc.COLOR_HSV2BGR_FULL, 4);
        return new Scalar((mRGBA.get(0, 0)));
    }
}