package com.transaktsdk.ideal_demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

public class Qrscanner extends AppCompatActivity {

    SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    String intentData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);
        getSupportActionBar().setTitle("Scan QR-code");
        surfaceView=findViewById(R.id.surfaceView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        View mActionBarView = getLayoutInflater().inflate(R.layout.my_action_bar, null);
        getSupportActionBar().setCustomView(mActionBarView);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        initialiseDetectorsAndSources();

    }

private void initialiseDetectorsAndSources()
{
    Toast.makeText(this, "Barcode scanner started", Toast.LENGTH_SHORT).show();

    //setting up barcode detector
barcodeDetector=new BarcodeDetector.Builder(this).
        setBarcodeFormats(Barcode.DATA_MATRIX|Barcode.QR_CODE).build();

//settting up camera detectoor

cameraSource=new CameraSource.Builder(this,barcodeDetector).
        setRequestedPreviewSize(1920,1080)
        .setAutoFocusEnabled(true)
        .build();


surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {

            if (ActivityCompat.checkSelfPermission(Qrscanner.this,
            Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED)
    {
        cameraSource.start(surfaceView.getHolder());
    }       else

        {
           ActivityCompat.requestPermissions(Qrscanner.this,new String[]{Manifest.permission.CAMERA} ,
            REQUEST_CAMERA_PERMISSION);
            }

    } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
});

barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
    @Override
    public void release() {
        Toast.makeText(Qrscanner.this, "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void receiveDetections(Detector.Detections<Barcode> detections) {


final SparseArray<Barcode>barcodeSparseArray=detections.getDetectedItems();

if (barcodeSparseArray.size()!=0)
{
intentData=barcodeSparseArray.valueAt(0).displayValue;
startActivity(new Intent(Qrscanner.this,ScannerResult.class).putExtra("url",intentData));
}

    }
});

}

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
       initialiseDetectorsAndSources();


    }

    @Override
    protected void onStart() {
        super.onStart();
        initialiseDetectorsAndSources();
    }
}
