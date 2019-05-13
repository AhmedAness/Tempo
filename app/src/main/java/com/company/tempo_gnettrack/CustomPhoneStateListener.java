package com.company.tempo_gnettrack;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.util.Log;

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

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (collect!=null) {
                        collect.OnSignalChanged();
                    }
                }
            }, 300000);

    }
    public interface Collect{
        void OnSignalChanged();
    }
}
