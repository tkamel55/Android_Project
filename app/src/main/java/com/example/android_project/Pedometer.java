package com.example.android_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Pedometer extends AppCompatActivity implements View.OnClickListener, PedometerManager.ScoreFeedbackListener {
    private static final String TAG = "Pedometer";

    private TextView mStepValueView;
    private TextView mPaceValueView;
    private TextView mDistanceValueView;
    private TextView mSpeedValueView;
    private TextView mCaloriesValueView;
    private Button btnStopCounter;
    private int mStepValue;
    private int mPaceValue;
    private float mDistanceValue;
    private float mSpeedValue;
    private int mCaloriesValue;
    private boolean isPedometerService = false; // set to true when PedometerService is running
    private String startTime = "";
    private String endTime = "";
    private PedometerManager pedometerManager;
    private ScoreDbManager scoreDbManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "[ACTIVITY] onCreate");
        super.onCreate(savedInstanceState);

        mStepValue = 0;
        mPaceValue = 0;
        scoreDbManager = new ScoreDbManager(Pedometer.this);
        pedometerManager = new PedometerManager(Pedometer.this, true, this);
        setContentView(R.layout.activity_pedometer);
        initInstances();
        startTime = StaticAccess.getDateTimeNow();
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "[ACTIVITY] onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "[ACTIVITY] onResume");
        super.onResume();

        startPedometerService();
        bindPedometerService();

        // TODO: use a listener or something
        if (!PedometerSetting.getIsMetric()) {
            TextView mDistanceUnitView = (TextView) findViewById(R.id.distance_unit);
            TextView mSpeedUnitView = (TextView) findViewById(R.id.speed_unit);
            mDistanceUnitView.setText(R.string.distance_unit_imperial);
            mSpeedUnitView.setText(R.string.speed_unit_imperial);
        }
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "[ACTIVITY] onPause");

        if (!PedometerSetting.getShouldRunInBackground()) {
            unbindPedometerService();
            stopPedometerService();
        }

        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "[ACTIVITY] onStop");
        stopPedometerService();
        super.onStop();
    }

    protected void onDestroy() {
        Log.i(TAG, "[ACTIVITY] onDestroy");

        // clear things up on destroy if enable background running
        if (PedometerSetting.getShouldRunInBackground()) {
            unbindPedometerService();
            stopPedometerService();
        }

        super.onDestroy();
    }

    protected void onRestart() {
        Log.i(TAG, "[ACTIVITY] onRestart");
        super.onRestart();
    }

    private void initInstances() {
        mStepValueView = (TextView) findViewById(R.id.step_value);
        mPaceValueView = (TextView) findViewById(R.id.pace_value);
        mDistanceValueView = (TextView) findViewById(R.id.distance_value);
        mSpeedValueView = (TextView) findViewById(R.id.speed_value);
        mCaloriesValueView = (TextView) findViewById(R.id.calories_value);
        btnStopCounter = findViewById(R.id.btnStopCounter);
        btnStopCounter.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnStopCounter:
                stopCounter();
                break;
            case R.id.navItem1: // navigate to the map view
                //startTraceActivity();
                break;
            case R.id.navItem2: // share data via various social network
                //shareData();
                break;
            case R.id.navItem3: // TODO: the user should customize the setting here
                //startSettingActivity();
                break;
            case R.id.navItem4: // TODO: the user should give us feedback(rating)
                break;
        }
    }

    private void stopCounter() {
        endTime = StaticAccess.getDateTimeNow();

        Scores score = new Scores();
        score.setUserID(SharedPreferenceValue.getLoggedinUser(Pedometer.this));
        score.setStartTime(startTime);
        score.setEndTime(endTime);
        score.setSteps(String.valueOf(mStepValue));
        score.setCalories(String.valueOf(mCaloriesValue));
        score.setDistance(String.valueOf(mDistanceValue));
        score.setSpeed(String.valueOf(mSpeedValue));
        score.setPace(String.valueOf(mPaceValue));
        score.setCreatedAt(StaticAccess.getDateTimeNow());
        score.setUpdatedAt(StaticAccess.getDateTimeNow());
        long inserted = scoreDbManager.addScore(score);
        if (inserted != -1) {
            // Toast.makeText(mPedometerService, "Score Saved", Toast.LENGTH_SHORT).show();
            startDashboardActivity();
        } else {
            Toast.makeText(mPedometerService, "Error occurred while processing the request", Toast.LENGTH_SHORT).show();
            startDashboardActivity();

        }
        SharedPreferenceValue.clearLastScore(Pedometer.this);
        SharedPreferenceValue.setLastScore(Pedometer.this, score);

    }

    private void startDashboardActivity() {
        startActivity(new Intent(Pedometer.this, DashboardActivity.class));
        finish();
    }

    private PedometerService mPedometerService;

    private ServiceConnection mPedometerConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mPedometerService = ((PedometerService.PedometerBinder) service).getService();
            mPedometerService.registerCallback(mCallback);
        }

        public void onServiceDisconnected(ComponentName className) {
            mPedometerService = null;
        }
    };

    private void startPedometerService() {
        if (!isPedometerService) {
            Log.v(TAG, "[PedometerService] Start");
            isPedometerService = true;
            startService(new Intent(Pedometer.this, PedometerService.class));
        }
    }

    private void bindPedometerService() {
        Log.i(TAG, "[PedometerService] Bind");
        bindService(
                new Intent(Pedometer.this, PedometerService.class),
                mPedometerConnection,
                Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }

    private void unbindPedometerService() {
        Log.i(TAG, "[PedometerService] Unbind");
        unbindService(mPedometerConnection);
    }

    private void stopPedometerService() {
        Log.i(TAG, "[PedometerService] Stop");
        if (mPedometerService != null) {
            Log.i(TAG, "[PedometerService] stopService");
            stopService(new Intent(Pedometer.this, PedometerService.class));
            isPedometerService = false;
        }
    }


    // TODO: unite all into 1 type of message
    private PedometerService.ICallback mCallback = new PedometerService.ICallback() {
        public void stepsChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
        }

        public void paceChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
        }

        public void distanceChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (int) (value * 1000), 0));
        }

        public void speedChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG, (int) (value * 1000), 0));
        }

        public void caloriesChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG, (int) (value), 0));
        }
    };

    private static final int STEPS_MSG = 1;
    private static final int PACE_MSG = 2;
    private static final int DISTANCE_MSG = 3;
    private static final int SPEED_MSG = 4;
    private static final int CALORIES_MSG = 5;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STEPS_MSG:
                    mStepValue = (int) msg.arg1;
                    mStepValueView.setText("" + mStepValue);
                    break;
                case PACE_MSG:
                    mPaceValue = msg.arg1;
                    if (mPaceValue <= 0) {
                        mPaceValueView.setText("0");
                    } else {
                        mPaceValueView.setText("" + (int) mPaceValue);
                    }
                    break;
                case DISTANCE_MSG:
                    mDistanceValue = ((int) msg.arg1) / 1000f;
                    if (mDistanceValue <= 0) {
                        mDistanceValueView.setText("0");
                    } else {
                        mDistanceValueView.setText(
                                ("" + (mDistanceValue + 0.000001f)).substring(0, 5)
                        );
                    }
                    break;
                case SPEED_MSG:
                    mSpeedValue = ((int) msg.arg1) / 1000f;
                    if (mSpeedValue <= 0) {
                        mSpeedValueView.setText("0");
                    } else {
                        mSpeedValueView.setText(
                                ("" + (mSpeedValue + 0.000001f)).substring(0, 4)
                        );
                    }
                    break;
                case CALORIES_MSG:
                    mCaloriesValue = msg.arg1;
                    if (mCaloriesValue <= 0) {
                        mCaloriesValueView.setText("0");
                    } else {
                        mCaloriesValueView.setText("" + (int) mCaloriesValue);
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

    };

    @Override
    public void scoreUpdateUpSuccess(Scores scores) {

    }

    @Override
    public void scoreUpdateFailed(Scores scores) {

    }
}