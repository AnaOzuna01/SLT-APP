package com.finalproject.sltapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;


public class Fashion extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "Fashion";
    JavaCameraView javaCameraView;
    Mat mRGB, mRGBAT;

    BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(Fashion.this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == BaseLoaderCallback.SUCCESS) {
                javaCameraView.enableView();
            } else {
                super.onManagerConnected(status);
            }
        }
    };

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fashion);

        javaCameraView = findViewById(R.id.javaColorPicker);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(Fashion.this);
    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        mRGB = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mRGB.release();

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRGB = inputFrame.rgba();
        mRGBAT = mRGB.t();
        Core.flip(mRGB.t(), mRGBAT, 1);
        Imgproc.resize(mRGBAT, mRGBAT, mRGB.size());
        return mRGBAT;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(javaCameraView != null){
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(javaCameraView != null){
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV is configured or connected successfully.");
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        }else{
            Log.d(TAG, "OpenCV not working or loaded");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, Fashion.this, baseLoaderCallback);
        }
    }
}