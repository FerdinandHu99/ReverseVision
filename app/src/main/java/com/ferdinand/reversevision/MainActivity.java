package com.ferdinand.reversevision;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.car.Car;
import android.car.Car.CarServiceLifecycleListener;
import android.car.evs.CarEvsManager;
import static android.car.evs.CarEvsManager.ERROR_NONE;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AuxiliaryLineView auxiliaryLineView;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int CAR_WAIT_TIMEOUT_MS = 3_000;
    private Car mCar;

    /** CarService status listener  */
    private final CarServiceLifecycleListener mCarServiceLifecycleListener = (car, ready) -> {
        if (!ready) {
            return;
        }

        try {
            CarEvsManager evsManager = (CarEvsManager) car.getCarManager(
                    Car.CAR_EVS_SERVICE);
            if (evsManager.startActivity(CarEvsManager.SERVICE_TYPE_REARVIEW) != ERROR_NONE) {
                Log.e(TAG, "Failed to start a camera preview activity");
            }
        } finally {
            mCar = car;
            finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Car.createCar(getApplicationContext(), /* handler = */ null, CAR_WAIT_TIMEOUT_MS,
                mCarServiceLifecycleListener);

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

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (mCar != null) {
            // Explicitly stops monitoring the car service's status
            mCar.disconnect();
        }

        super.onDestroy();
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