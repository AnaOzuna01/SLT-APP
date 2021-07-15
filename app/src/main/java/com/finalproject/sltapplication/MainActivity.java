package com.finalproject.sltapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity {

    final int CAMERA_PERMISSION_REQUEST_ID = 1001;
    CameraBridgeViewBase cameraBridgeViewBase;

    Scalar colorRGBA;
    Scalar colorHSV;
    Mat matRGBA;

    TextView txtCoordinates;
    TextView txtColor;
    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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