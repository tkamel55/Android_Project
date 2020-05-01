package com.example.android_project;

public class SpeedNotifier implements PaceNotifier.Listener {

    public interface Listener {
        void valueChanged(float value);

        void passValue();
    }

    private Listener mListener;

    float mSpeed = 0;

    boolean mIsMetric = PedometerSetting.getIsMetric();

    float mStepLength = PedometerSetting.getStepLength();

    public SpeedNotifier(Listener listener) {
        mListener = listener;
    }

    public void setSpeed(float speed) {
        mSpeed = speed;
        notifyListener();
    }

    private void notifyListener() {
        mListener.valueChanged(mSpeed);
    }

    public void paceChanged(int value) {
        if (mIsMetric) {
            mSpeed = // kilometers / hour
                    value * mStepLength // centimeters / minute
                            / 100000f * 60f; // centimeters/kilometer
        } else {
            mSpeed = // miles / hour
                    value * mStepLength // inches / minute
                            / 63360f * 60f; // inches/mile
        }
        notifyListener();
    }


    public void passValue() {
        // Not used
    }
}
