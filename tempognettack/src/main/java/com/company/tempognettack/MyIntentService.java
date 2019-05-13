package com.company.tempognettack;

import android.app.IntentService;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class MyIntentService extends IntentService implements CustomPhoneStateListener.Collect {

    private static CollectData collectData;



    CustomPhoneStateListener phoneListener = null;
    public MyIntentService() {
        super("MyIntentService");
    }
    public MyIntentService(String name)
    {
        super(name);
    }

    @Override
    public void onDestroy() {
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_NONE);
        }
        super.onDestroy();
    }

    // constructor
    public MyIntentService(CollectData collect) {
        super("SignalChanged");
        this.collectData = collect;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
        }
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        if (phoneListener == null) {

            TelephonyManager tm = (TelephonyManager)getApplicationContext().getSystemService(TELEPHONY_SERVICE);
            phoneListener = new CustomPhoneStateListener(this);

            tm.listen(phoneListener,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        }
        // do what you need to do here

        return START_STICKY; // you can set it as you want
    }

    @Override
    public void OnSignalChanged() {
        if (collectData!=null){
            collectData.OnDataSignalChanged();
        }
    }


    public interface CollectData{
        void OnDataSignalChanged();
    }

}
