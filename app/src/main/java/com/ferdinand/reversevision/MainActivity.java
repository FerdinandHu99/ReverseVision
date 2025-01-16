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
}