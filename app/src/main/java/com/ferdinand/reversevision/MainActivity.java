package com.ferdinand.reversevision;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListenableFuture<ProcessCameraProvider> processCameraProviderListenableFuture;
    private PreviewView previewView;
    private CameraSelector cameraSelector;
    private String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    private static final int REQUEST_CODE_PERMISSIONS = 10;

    private AuxiliaryLineView auxiliaryLineView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraList = cameraManager.getCameraIdList();
            if (cameraList.length == 0) {
                Log.e("Camera", "No camera detected on this device.");
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

//        previewView = findViewById(R.id.previewView);

        if (allPermissionsGranted()) {
//            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        auxiliaryLineView = findViewById(R.id.auxiliaryLineView);

        // 定义你的按钮
        View btn_wideAngle = findViewById(R.id.btn_wideAngle);
        View btn_normalAngle = findViewById(R.id.btn_normalAngle);
        View btn_overlookAngle = findViewById(R.id.btn_overlookAngle);

        btn_wideAngle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuxiliaryLineView.GuideLine guideLine = createDynamicGuideLine();
                auxiliaryLineView.setEditMode(true);
                auxiliaryLineView.setGuideLine(guideLine); // 设置动态引导线
            }
        });

        btn_normalAngle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuxiliaryLineView.GuideLine guideLine = createStaticGuideLine();
                auxiliaryLineView.setEditMode(true);
                auxiliaryLineView.setGuideLine(guideLine); // 设置静态引导线
            }
        });

        btn_overlookAngle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuxiliaryLineView.GuideLine guideLine = createTopviewGuideLine();
                auxiliaryLineView.setEditMode(true);
                auxiliaryLineView.setGuideLine(guideLine); // 设置顶部视图引导线
            }
        });


    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            }
        }
    }
    private void startCamera() {
        processCameraProviderListenableFuture = ProcessCameraProvider.getInstance(this);
        processCameraProviderListenableFuture.addListener(() -> {
            try {
                ProcessCameraProvider processCameraProvider = processCameraProviderListenableFuture.get();
                bindCamera(processCameraProvider);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCamera(ProcessCameraProvider processCameraProvider) {
        processCameraProvider.unbindAll();
        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        processCameraProvider.bindToLifecycle(this, cameraSelector, preview);
    }

    private AuxiliaryLineView.GuideLine createTopviewGuideLine() {
        List<AuxiliaryLineView.Point> testPoints = new java.util.ArrayList<>();
        testPoints.add(new AuxiliaryLineView.Point(200, 400));
        testPoints.add(new AuxiliaryLineView.Point(200, 100));
        testPoints.add(new AuxiliaryLineView.Point(300, 100));
        testPoints.add(new AuxiliaryLineView.Point(800, 100));
        testPoints.add(new AuxiliaryLineView.Point(300, 300));
        testPoints.add(new AuxiliaryLineView.Point(800, 300));
        testPoints.add(new AuxiliaryLineView.Point(900, 100));
        testPoints.add(new AuxiliaryLineView.Point(900, 400));
        return new AuxiliaryLineView.GuideLine(testPoints, AuxiliaryLineView.GuideLineType.TOP);
    }

    private AuxiliaryLineView.GuideLine createStaticGuideLine() {
        List<AuxiliaryLineView.Point> testPoints = new java.util.ArrayList<>();
        testPoints.add(new AuxiliaryLineView.Point(200, 400));
        testPoints.add(new AuxiliaryLineView.Point(240, 320));
        testPoints.add(new AuxiliaryLineView.Point(280, 240));
        testPoints.add(new AuxiliaryLineView.Point(320, 160));
        testPoints.add(new AuxiliaryLineView.Point(360, 80));

        testPoints.add(new AuxiliaryLineView.Point(800, 400));
        testPoints.add(new AuxiliaryLineView.Point(760, 320));
        testPoints.add(new AuxiliaryLineView.Point(720, 240));
        testPoints.add(new AuxiliaryLineView.Point(680, 160));
        testPoints.add(new AuxiliaryLineView.Point(640, 80));

        testPoints.add(new AuxiliaryLineView.Point(280, 320));
        testPoints.add(new AuxiliaryLineView.Point(720, 320));

        testPoints.add(new AuxiliaryLineView.Point(320, 200));
        testPoints.add(new AuxiliaryLineView.Point(680, 200));

        testPoints.add(new AuxiliaryLineView.Point(380, 80));
        testPoints.add(new AuxiliaryLineView.Point(620, 80));

        return new AuxiliaryLineView.GuideLine(testPoints, AuxiliaryLineView.GuideLineType.STATIC);
    }

    private AuxiliaryLineView.GuideLine createDynamicGuideLine() {
        List<AuxiliaryLineView.Point> testPoints = new java.util.ArrayList<>();
        testPoints.add(new AuxiliaryLineView.Point(200, 400));
        testPoints.add(new AuxiliaryLineView.Point(240, 320));
        testPoints.add(new AuxiliaryLineView.Point(280, 240));
        testPoints.add(new AuxiliaryLineView.Point(320, 160));
        testPoints.add(new AuxiliaryLineView.Point(360, 80));

        testPoints.add(new AuxiliaryLineView.Point(800, 400));
        testPoints.add(new AuxiliaryLineView.Point(760, 320));
        testPoints.add(new AuxiliaryLineView.Point(720, 240));
        testPoints.add(new AuxiliaryLineView.Point(680, 160));
        testPoints.add(new AuxiliaryLineView.Point(640, 80));

        testPoints.add(new AuxiliaryLineView.Point(280, 320));
        testPoints.add(new AuxiliaryLineView.Point(700, 320));

        testPoints.add(new AuxiliaryLineView.Point(300, 200));
        testPoints.add(new AuxiliaryLineView.Point(340, 200));

        testPoints.add(new AuxiliaryLineView.Point(660, 200));
        testPoints.add(new AuxiliaryLineView.Point(700, 200));

        testPoints.add(new AuxiliaryLineView.Point(330, 140));
        testPoints.add(new AuxiliaryLineView.Point(370, 140));

        testPoints.add(new AuxiliaryLineView.Point(630, 140));
        testPoints.add(new AuxiliaryLineView.Point(670, 140));

        testPoints.add(new AuxiliaryLineView.Point(380, 80));
        testPoints.add(new AuxiliaryLineView.Point(620, 80));

        return new AuxiliaryLineView.GuideLine(testPoints, AuxiliaryLineView.GuideLineType.DYNAMIC);
    }
}