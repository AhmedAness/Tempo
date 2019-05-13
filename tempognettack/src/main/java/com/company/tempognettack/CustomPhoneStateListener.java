package com.company.tempognettack;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;

public class CustomPhoneStateListener extends PhoneStateListener {
    private static Collect collect;

    // constructor
    public CustomPhoneStateListener(Collect collect) {
        this.collect = collect;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);

        try {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (collect!=null) {
                        collect.OnSignalChanged();
                    }
                }
            }, 300000);

        }catch (Exception e){}
    }
    public interface Collect{
        void OnSignalChanged();
    }
}
